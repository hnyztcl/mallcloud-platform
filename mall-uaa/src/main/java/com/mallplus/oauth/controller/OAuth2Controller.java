package com.mallplus.oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallplus.common.constant.SecurityConstants;
import com.mallplus.common.feign.UserService;
import com.mallplus.common.model.Result;
import com.mallplus.common.model.SysUser;
import com.mallplus.common.utils.CommonResult;
import com.mallplus.common.utils.SpringUtil;
import com.mallplus.oauth.mobile.MobileAuthenticationToken;
import com.mallplus.oauth.openid.OpenIdAuthenticationToken;
import com.mallplus.oauth.service.impl.RedisClientDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * OAuth2相关操作
 *
 * @author mall
 */
@Api(tags = "OAuth2相关操作")
@Slf4j
@RestController
public class OAuth2Controller {

    @Resource
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Resource
    private UserService userService;

    @ApiOperation(value = "用户名密码获取token")
    @PostMapping(SecurityConstants.PASSWORD_LOGIN_PRO_URL)
    public void getUserTokenInfo( @ApiParam(required = true, name = "username", value = "账号") String username,
                                  @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (username == null || "".equals(username)) {
            throw new UnapprovedClientAuthenticationException("用户名为空");
        }
        if ( password == null || "".equals( password)) {
            throw new UnapprovedClientAuthenticationException("密码为空");
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,  password);
       SysUser user = userService.selectByUsername(username);
       if (user!=null){
           writerToken(request, response, token, "用户名或密码错误",user.getId());
       }else {
           exceptionHandler(response, "用户名或密码错误");
       }

    }

    @ApiOperation(value = "openId获取token")
    @PostMapping(SecurityConstants.OPENID_TOKEN_URL)
    public void getTokenByOpenId(
            @ApiParam(required = true, name = "openId", value = "openId") String openId,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        OpenIdAuthenticationToken token = new OpenIdAuthenticationToken(openId);
        SysUser member = userService.findByOpenId(openId);
        if (member!=null){
            writerToken(request, response, token, "openId错误",member.getId());
        }else {
            exceptionHandler(response, "openId错误");
        }

    }




    @ApiOperation(value = "mobile获取token")
    @PostMapping(SecurityConstants.MOBILE_TOKEN_URL)
    public void getTokenByMobile(
            @ApiParam(required = true, name = "mobile", value = "mobile") String mobile,
            @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        MobileAuthenticationToken token = new MobileAuthenticationToken(mobile, password);
        writerToken(request, response, token, "手机号或密码错误",1L);
    }


    /**
     * 当前登陆用户信息
     * security获取当前登录用户的方法是SecurityContextHolder.getContext().getAuthentication()
     * 这里的实现类是org.springframework.security.oauth2.provider.OAuth2Authentication
     *
     * @return
     */
    @ApiOperation(value = "当前登陆用户信息")
    @RequestMapping(value = { "/oauth/userinfo" }, produces = "application/json") // 获取用户信息。/auth/user
    public Object getCurrentUserDetail() {
        return new CommonResult().success(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    private void writerToken(HttpServletRequest request, HttpServletResponse response, AbstractAuthenticationToken token
            , String badCredenbtialsMsg,Long userId) throws IOException {
        try {
            String clientId = request.getHeader("client_id");
            String clientSecret = request.getHeader("client_secret");
            if (clientId == null || "".equals(clientId)) {
                throw new UnapprovedClientAuthenticationException("请求头中无client_id信息");
            }

            if (clientSecret == null || "".equals(clientSecret)) {
                throw new UnapprovedClientAuthenticationException("请求头中无client_secret信息");
            }

            ClientDetails clientDetails = getClient(clientId, clientSecret, null);
            TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), "customer");
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
            OAuth2AccessToken oAuth2AccessToken =  authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
            oAuth2Authentication.setAuthenticated(true);

            writerObj(response, oAuth2AccessToken);
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            exceptionHandler(response, badCredenbtialsMsg);
            e.printStackTrace();
        } catch (Exception e) {
            exceptionHandler(response, e);
        }
    }

    private void exceptionHandler(HttpServletResponse response, Exception e) throws IOException {
        log.error("exceptionHandler-error:", e);
        exceptionHandler(response, e.getMessage());
    }

    private void exceptionHandler(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        writerObj(response, Result.failed(msg));
    }

    private void writerObj(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try (
                Writer writer = response.getWriter()
        ) {
            writer.write(objectMapper.writeValueAsString(obj));
            writer.flush();
        }
    }

    private ClientDetails getClient(String clientId, String clientSecret, RedisClientDetailsService clientDetailsService) {
        if (clientDetailsService == null) {
            clientDetailsService = SpringUtil.getBean(RedisClientDetailsService.class);
        }
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
        } else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
        }
        return clientDetails;
    }
}