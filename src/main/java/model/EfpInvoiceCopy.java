package model;

import java.util.Date;

public class EfpInvoiceCopy {
    private String id;

    private String fpdm;

    private String fphm;

    private Integer bz;

    private String parm1;

    private String parm2;

    private String parm3;

    private String parm4;

    private String parm5;

    private String parm6;

    private String parm7;

    private String parm8;

    private Date createTime;


    private String kpJson;

    private Date updatetime;

    private String kjh;


    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getKjh() {
        return kjh;
    }

    public void setKjh(String kjh) {
        this.kjh = kjh;
    }


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

    public String getParm6() {
        return parm6;
    }

    public void setParm6(String parm6) {
        this.parm6 = parm6 == null ? null : parm6.trim();
    }

    public String getParm7() {
        return parm7;
    }

    public void setParm7(String parm7) {
        this.parm7 = parm7 == null ? null : parm7.trim();
    }

    public String getParm8() {
        return parm8;
    }

    public void setParm8(String parm8) {
        this.parm8 = parm8 == null ? null : parm8.trim();
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