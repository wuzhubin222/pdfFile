package utils;

import entity.Vericode;
import utils.SendMsg.SendPMYMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PhoneMsgUtils {

    public static void main(String[] args) throws Exception {
        String[] sLst = {"10rr33"};
        SendPMYMsg.sendMsg("13612820346", "193958", sLst);
    }

    private static final int LEN = 6;

    private static Map<String, Vericode> verifyCodeMap = new HashMap<String,Vericode>();

    public static Map<String, Vericode> getVerifyCodeMap() {
        return verifyCodeMap;
    }

    public static String createVerifyCode() {
        char[] randoms = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        Random random = new Random(System.currentTimeMillis());
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < LEN; i++) {
            ret.append(randoms[random.nextInt(randoms.length)]);
        }
        return ret.toString();
    }


    public static int getVericode(String phoneNumber) {

        //获取验证码间隔时间60秒
        int intervalTime = 60;

        String keyword = phoneNumber;

        if (verifyCodeMap.get(keyword) != null) {
            long time = verifyCodeMap.get(keyword).getCreatTime();
            if(System.currentTimeMillis() - time < intervalTime * 1000){
                return 1000;
            }
        }

        long currentTime = System.currentTimeMillis();
        String vericode = PhoneMsgUtils.createVerifyCode();
        verifyCodeMap.put(keyword,new Vericode(vericode,currentTime));

        return 0;
    }


    public static int checkVericode(String phoneNumber, String vericode){

        //验证码有效时间600秒
        int validTime = 6000;

        String keyword = phoneNumber;

        Vericode code = verifyCodeMap.get(keyword);

        if(code == null){
            return 1001;//请输入正确的4位数字验证码
        }

        if(!code.getVericode().equals(vericode)){
            return 1001;
        }

        long time = System.currentTimeMillis();

        if(!code.getChecked()){
            if(time - code.getCreatTime() > validTime * 1000){
                return 1002;//验证码已过期
            }
        }

        return 0;
    }


    public static int sendVericode(String phone,int modelId){

        String verityCode = verifyCodeMap.get(phone).getVericode();
        try {
            if(modelId == 170664){
                String[]  params = {verityCode,"10"};
                SendPMYMsg.sendMsg(phone, "170664", params);
            }
            if(modelId == 169183){
                String[]  params = {verityCode};
                SendPMYMsg.sendMsg(phone, "169183", params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1003;
        }

        return 0;
    }




}
