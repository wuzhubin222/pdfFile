package model;

import java.util.Date;

public class EfpZpInvoiceCopy {
    private String id;

    private String fpdm;

    private String fphm;

    private Integer bz;

    private String nsrsbh;

    private String qqlsh;

    private String kpresult;

    private String parm1;

    private String parm2;

    private String parm3;

    private String parm4;

    private String parm5;

    private Date createTime;

    private String kpJson;

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

    public String getFphm() {
        return fphm;
    }

    public void setFphm(String fphm) {
        this.fphm = fphm == null ? null : fphm.trim();
    }

    public Integer getBz() {
        return bz;
    }

    public void setBz(Integer bz) {
        this.bz = bz;
    }

    public String getNsrsbh() {
        return nsrsbh;
    }

    public void setNsrsbh(String nsrsbh) {
        this.nsrsbh = nsrsbh == null ? null : nsrsbh.trim();
    }

    public String getQqlsh() {
        return qqlsh;
    }

    public void setQqlsh(String qqlsh) {
        this.qqlsh = qqlsh == null ? null : qqlsh.trim();
    }

    public String getKpresult() {
        return kpresult;
    }

    public void setKpresult(String kpresult) {
        this.kpresult = kpresult == null ? null : kpresult.trim();
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getKpJson() {
        return kpJson;
    }

    public void setKpJson(String kpJson) {
        this.kpJson = kpJson == null ? null : kpJson.trim();
    }
}