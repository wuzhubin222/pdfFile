package model;

import java.util.Date;

public class EfpXfzdab {
    private String id;

    private String xfzmc;

    private Integer sex;

    private String xfzIdcard;

    private String xfzPhone;

    private String xfzEmail;

    private String xfzSfz;

    private Integer type;

    private Integer active;

    private String memo;

    private String address;

    private String comment;

    private Date createTime;

    private String bmbh;

    private String imageUrl;

    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getXfzmc() {
        return xfzmc;
    }

    public void setXfzmc(String xfzmc) {
        this.xfzmc = xfzmc == null ? null : xfzmc.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getXfzIdcard() {
        return xfzIdcard;
    }

    public void setXfzIdcard(String xfzIdcard) {
        this.xfzIdcard = xfzIdcard == null ? null : xfzIdcard.trim();
    }

    public String getXfzPhone() {
        return xfzPhone;
    }

    public void setXfzPhone(String xfzPhone) {
        this.xfzPhone = xfzPhone == null ? null : xfzPhone.trim();
    }

    public String getXfzEmail() {
        return xfzEmail;
    }

    public void setXfzEmail(String xfzEmail) {
        this.xfzEmail = xfzEmail == null ? null : xfzEmail.trim();
    }

    public String getXfzSfz() {
        return xfzSfz;
    }

    public void setXfzSfz(String xfzSfz) {
        this.xfzSfz = xfzSfz == null ? null : xfzSfz.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBmbh() {
        return bmbh;
    }

    public void setBmbh(String bmbh) {
        this.bmbh = bmbh == null ? null : bmbh.trim();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }
}