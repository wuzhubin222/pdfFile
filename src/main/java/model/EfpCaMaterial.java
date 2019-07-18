package model;

import java.util.Date;

public class EfpCaMaterial {
    private String id;

    private String nsrsbh;

    private String nsrmc;

    private String yyzzUrl;

    private String sfzUrl;

    private String sqbUrl;

    private String sqxyUrl;

    private String status;

    private String reason;

    private String sealCode;

    private String caBusinessCode;

    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getNsrsbh() {
        return nsrsbh;
    }

    public void setNsrsbh(String nsrsbh) {
        this.nsrsbh = nsrsbh == null ? null : nsrsbh.trim();
    }

    public String getNsrmc() {
        return nsrmc;
    }

    public void setNsrmc(String nsrmc) {
        this.nsrmc = nsrmc == null ? null : nsrmc.trim();
    }

    public String getYyzzUrl() {
        return yyzzUrl;
    }

    public void setYyzzUrl(String yyzzUrl) {
        this.yyzzUrl = yyzzUrl == null ? null : yyzzUrl.trim();
    }

    public String getSfzUrl() {
        return sfzUrl;
    }

    public void setSfzUrl(String sfzUrl) {
        this.sfzUrl = sfzUrl == null ? null : sfzUrl.trim();
    }

    public String getSqbUrl() {
        return sqbUrl;
    }

    public void setSqbUrl(String sqbUrl) {
        this.sqbUrl = sqbUrl == null ? null : sqbUrl.trim();
    }

    public String getSqxyUrl() {
        return sqxyUrl;
    }

    public void setSqxyUrl(String sqxyUrl) {
        this.sqxyUrl = sqxyUrl == null ? null : sqxyUrl.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode == null ? null : sealCode.trim();
    }

    public String getCaBusinessCode() {
        return caBusinessCode;
    }

    public void setCaBusinessCode(String caBusinessCode) {
        this.caBusinessCode = caBusinessCode == null ? null : caBusinessCode.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}