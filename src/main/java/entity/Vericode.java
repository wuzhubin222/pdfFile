package entity;

public class Vericode {

    private long creatTime;

    private String vericode;

    private Boolean isChecked = false;

    public Vericode(String vericode,long creatTime){
        this.creatTime = creatTime;
        this.vericode = vericode;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public String getVericode() {
        return vericode;
    }

    public void setVericode(String vericode) {
        this.vericode = vericode;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

}
