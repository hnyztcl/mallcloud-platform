package com.mallplus.common.entity.pms;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mallplus.common.entity.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 产品咨询表
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@TableName("pms_product_consult")
public class PmsProductConsult extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 咨询编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品编号
     */
    @TableField("goods_id")
    private Long goodsId;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 咨询发布者会员编号(0：游客)
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 会员名称
     */
    @TableField("member_name")
    private String memberName;


    /**
     * 咨询发布者邮箱
     */
    private String email;

    /**
     * 咨询内容
     */
    @TableField("consult_content")
    private String consultContent;

    /**
     * 咨询添加时间
     */
    @TableField("consult_addtime")
    private Date consultAddtime;

    /**
     * 咨询回复内容
     */
    @TableField("consult_reply")
    private String consultReply;

    /**
     * 咨询回复时间
     */
    @TableField("consult_reply_time")
    private Date consultReplyTime;

    /**
     * 0表示不匿名 1表示匿名
     */
    private Boolean isanonymous;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConsultContent() {
        return consultContent;
    }

    public void setConsultContent(String consultContent) {
        this.consultContent = consultContent;
    }

    public Date getConsultAddtime() {
        return consultAddtime;
    }

    public void setConsultAddtime(Date consultAddtime) {
        this.consultAddtime = consultAddtime;
    }

    public String getConsultReply() {
        return consultReply;
    }

    public void setConsultReply(String consultReply) {
        this.consultReply = consultReply;
    }

    public Date getConsultReplyTime() {
        return consultReplyTime;
    }

    public void setConsultReplyTime(Date consultReplyTime) {
        this.consultReplyTime = consultReplyTime;
    }

    public Boolean getIsanonymous() {
        return isanonymous;
    }

    public void setIsanonymous(Boolean isanonymous) {
        this.isanonymous = isanonymous;
    }


    @Override
    public String toString() {
        return "PmsProductConsult{" +
        ", id=" + id +
        ", goodsId=" + goodsId +
        ", goodsName=" + goodsName +
        ", memberId=" + memberId +
        ", memberName=" + memberName +

        ", email=" + email +
        ", consultContent=" + consultContent +
        ", consultAddtime=" + consultAddtime +
        ", consultReply=" + consultReply +
        ", consultReplyTime=" + consultReplyTime +
        ", isanonymous=" + isanonymous +
       
        "}";
    }
}