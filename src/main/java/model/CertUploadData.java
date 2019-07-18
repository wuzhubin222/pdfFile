package model;

import java.util.Date;

public class CertUploadData {
    private String id;

    private Date createTime;

    private Date modifyTime;

    private String etpTaxId;

    private String applyid;

    private String yyzzUrl;

    private String sfzUrl;

    private String sqbUrl;

    private String sqxyUrl;

    private String errCode;

    private String errMsg;

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

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getEtpTaxId() {
        return etpTaxId;
    }

    public void setEtpTaxId(String etpTaxId) {
        this.etpTaxId = etpTaxId == null ? null : etpTaxId.trim();
    }

    public String getApplyid() {
        return applyid;
    }

    public void setApplyid(String applyid) {
        this.applyid = applyid == null ? null : applyid.trim();
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

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode == null ? null : errCode.trim();
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg == null ? null : errMsg.trim();
    }

private String invoiceSealUrl;


    private String financeSealUrl;
    private String parm1;
    private String parm2;


    public String getInvoiceSealUrl() {
        return invoiceSealUrl;
    }

    public void setInvoiceSealUrl(String invoiceSealUrl) {
        this.invoiceSealUrl = invoiceSealUrl;
    }

    public String getFinanceSealUrl() {
        return financeSealUrl;
    }

    public void setFinanceSealUrl(String financeSealUrl) {
        this.financeSealUrl = financeSealUrl;
    }

    public String getParm1() {
        return parm1;
    }

    public void setParm1(String parm1) {
        this.parm1 = parm1;
    }

    public String getParm2() {
        return parm2;
    }

    public void setParm2(String parm2) {
        this.parm2 = parm2;
    }
}