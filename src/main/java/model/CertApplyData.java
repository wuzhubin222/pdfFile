package model;

import java.util.Date;

public class CertApplyData {
    private String id;

    private Date createTime;

    private Date modifyTime;

    private String errCode;

    private String errMsg;

    private String status;

    private String applyId;

    private String etpName;

    private String etpLegalName;

    private String etpAddr;

    private String etpPhone;

    private String etpProvince;

    private String etpCity;

    private String etpTaxId;

    private String agentName;

    private String agentPhone;

    private String agentIdCard;

    private String certPassword;


    private Integer statusZs;

    public Integer getStatusZs() {
        return statusZs;
    }

    public void setStatusZs(Integer statusZs) {
        this.statusZs = statusZs;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getEtpName() {
        return etpName;
    }

    public void setEtpName(String etpName) {
        this.etpName = etpName == null ? null : etpName.trim();
    }

    public String getEtpLegalName() {
        return etpLegalName;
    }

    public void setEtpLegalName(String etpLegalName) {
        this.etpLegalName = etpLegalName == null ? null : etpLegalName.trim();
    }

    public String getEtpAddr() {
        return etpAddr;
    }

    public void setEtpAddr(String etpAddr) {
        this.etpAddr = etpAddr == null ? null : etpAddr.trim();
    }

    public String getEtpPhone() {
        return etpPhone;
    }

    public void setEtpPhone(String etpPhone) {
        this.etpPhone = etpPhone == null ? null : etpPhone.trim();
    }

    public String getEtpProvince() {
        return etpProvince;
    }

    public void setEtpProvince(String etpProvince) {
        this.etpProvince = etpProvince == null ? null : etpProvince.trim();
    }

    public String getEtpCity() {
        return etpCity;
    }

    public void setEtpCity(String etpCity) {
        this.etpCity = etpCity == null ? null : etpCity.trim();
    }

    public String getEtpTaxId() {
        return etpTaxId;
    }

    public void setEtpTaxId(String etpTaxId) {
        this.etpTaxId = etpTaxId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName == null ? null : agentName.trim();
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone == null ? null : agentPhone.trim();
    }

    public String getAgentIdCard() {
        return agentIdCard;
    }

    public void setAgentIdCard(String agentIdCard) {
        this.agentIdCard = agentIdCard == null ? null : agentIdCard.trim();
    }


    private Integer pageOff;
    private Integer pageSize;

    public Integer getPageOff() {
        return pageOff;
    }

    public void setPageOff(Integer pageOff) {
        this.pageOff = pageOff;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    private String sealCode;

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }
    private String financeCode;

    public String getFinanceCode() {
        return financeCode;
    }

    public void setFinanceCode(String financeCode) {
        this.financeCode = financeCode;
    }
}