package model;

import java.util.Date;

public class EfpCzyDab {
    private String id;

    private String nsrsbh;

    private String czyId;

    private String czyName;

    private String password;

    private Integer type;

    private Integer status;

    private Date logintime;

    private String email;

    private String parm1;

    private String parm2;

    private String parm3;

    private String parm4;

    private String parm5;

    private String parm6;

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

    public String getCzyId() {
        return czyId;
    }

    public void setCzyId(String czyId) {
        this.czyId = czyId == null ? null : czyId.trim();
    }

    public String getCzyName() {
        return czyName;
    }

    public void setCzyName(String czyName) {
        this.czyName = czyName == null ? null : czyName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLogintime() {
        return logintime;
    }

    public void setLogintime(Date logintime) {
        this.logintime = logintime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
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
}