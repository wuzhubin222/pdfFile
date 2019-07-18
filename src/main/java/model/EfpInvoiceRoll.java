package model;

import java.util.Date;

public class EfpInvoiceRoll {
    private String id;

    private String fpdm;

    private Integer fpSt;

    private Integer fpEd;

    private Integer fpIn;

    private Integer ph;

    private String type;

    private Integer status;

    private String nsrsbh;

    private Integer remainNum;

    private Date addtime;

    private Date updatetime;

    private String parm1;

    private String parm2;

    private String parm3;

    private String parm4;

    private String parm5;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getFpdm() {
        return fpdm;
    }

    public void setFpdm(String fpdm) {
        this.fpdm = fpdm == null ? null : fpdm.trim();
    }

    public Integer getFpSt() {
        return fpSt;
    }

    public void setFpSt(Integer fpSt) {
        this.fpSt = fpSt;
    }

    public Integer getFpEd() {
        return fpEd;
    }

    public void setFpEd(Integer fpEd) {
        this.fpEd = fpEd;
    }

    public Integer getFpIn() {
        return fpIn;
    }

    public void setFpIn(Integer fpIn) {
        this.fpIn = fpIn;
    }

    public Integer getPh() {
        return ph;
    }

    public void setPh(Integer ph) {
        this.ph = ph;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNsrsbh() {
        return nsrsbh;
    }

    public void setNsrsbh(String nsrsbh) {
        this.nsrsbh = nsrsbh == null ? null : nsrsbh.trim();
    }

    public Integer getRemainNum() {
        return remainNum;
    }

    public void setRemainNum(Integer remainNum) {
        this.remainNum = remainNum;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getParm1() {
        return parm1;
    }

    public void setParm1(String parm1) {
        this.parm1 = parm1 == null ? null : parm1.trim();
    }

    public String getParm2() {
        return parm2;
    }

    public void setParm2(String parm2) {
        this.parm2 = parm2 == null ? null : parm2.trim();
    }

    public String getParm3() {
        return parm3;
    }

    public void setParm3(String parm3) {
        this.parm3 = parm3 == null ? null : parm3.trim();
    }

    public String getParm4() {
        return parm4;
    }

    public void setParm4(String parm4) {
        this.parm4 = parm4 == null ? null : parm4.trim();
    }

    public String getParm5() {
        return parm5;
    }

    public void setParm5(String parm5) {
        this.parm5 = parm5 == null ? null : parm5.trim();
    }
}