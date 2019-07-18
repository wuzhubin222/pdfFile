package entity;

import java.util.HashMap;
import java.util.Map;

public class ConResult {

    private int code = 0;

    private String message = "success";

    private Map<String,Object> map = new HashMap<String, Object>();

    public ConResult(){
    }

    public ConResult(int code,String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
