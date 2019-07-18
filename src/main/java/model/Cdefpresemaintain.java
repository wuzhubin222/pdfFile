package model;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/31.
 */
public class Cdefpresemaintain {

    private String id;
    private String xfsh;
    private String gfsh;
    private String gfmc;
    private String gfdzdh;
    private String gfyhmcyhzh;
    private String email;
    private String status;
    private Date createtime;
    private Date updatetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getXfsh() {
        return xfsh;
    }

    public void setXfsh(String xfsh) {
        this.xfsh = xfsh;
    }

    public String getGfsh() {
        return gfsh;
    }

    public void setGfsh(String gfsh) {
        this.gfsh = gfsh;
    }

    public String getGfmc() {
        return gfmc;
    }

    public void setGfmc(String gfmc) {
        this.gfmc = gfmc;
    }

    public String getGfdzdh() {
        return gfdzdh;
    }

    public void setGfdzdh(String gfdzdh) {
        this.gfdzdh = gfdzdh;
    }

    public String getGfyhmcyhzh() {
        return gfyhmcyhzh;
    }

    public void setGfyhmcyhzh(String gfyhmcyhzh) {
        this.gfyhmcyhzh = gfyhmcyhzh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}
