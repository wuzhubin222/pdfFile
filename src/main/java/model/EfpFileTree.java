package model;

import java.util.Date;

public class EfpFileTree {
    private String id;

    private String nsrsbh;

    private String cityNo;

    private String regMonth;

    private String regDay;

    private String kpMonth;

    private String kpDay;

    private String fileName;

    private Integer fileNumber;

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

    public String getCityNo() {
        return cityNo;
    }

    public void setCityNo(String cityNo) {
        this.cityNo = cityNo == null ? null : cityNo.trim();
    }

    public String getRegMonth() {
        return regMonth;
    }

    public void setRegMonth(String regMonth) {
        this.regMonth = regMonth == null ? null : regMonth.trim();
    }

    public String getRegDay() {
        return regDay;
    }

    public void setRegDay(String regDay) {
        this.regDay = regDay == null ? null : regDay.trim();
    }

    public String getKpMonth() {
        return kpMonth;
    }

    public void setKpMonth(String kpMonth) {
        this.kpMonth = kpMonth == null ? null : kpMonth.trim();
    }

    public String getKpDay() {
        return kpDay;
    }

    public void setKpDay(String kpDay) {
        this.kpDay = kpDay == null ? null : kpDay.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(Integer fileNumber) {
        this.fileNumber = fileNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}