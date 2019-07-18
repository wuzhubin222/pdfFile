package model;

import java.util.Date;

public class EfpCa {
    private String id;

    private Date createTime;

    private String nsrsbh;

    private Date updateTime;

    private String caPath;

    private String caImgPath;

    private String caPassword;

    private String templateName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNsrsbh() {
        return nsrsbh;
    }

    public void setNsrsbh(String nsrsbh) {
        this.nsrsbh = nsrsbh == null ? null : nsrsbh.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCaPath() {
        return caPath;
    }

    public void setCaPath(String caPath) {
        this.caPath = caPath == null ? null : caPath.trim();
    }

    public String getCaImgPath() {
        return caImgPath;
    }

    public void setCaImgPath(String caImgPath) {
        this.caImgPath = caImgPath == null ? null : caImgPath.trim();
    }

    public String getCaPassword() {
        return caPassword;
    }

    public void setCaPassword(String caPassword) {
        this.caPassword = caPassword == null ? null : caPassword.trim();
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName == null ? null : templateName.trim();
    }
}