package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import constant.Constant;
import excption.BusinessException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {

    private static String token;

    //token刷新时间间隔秒 1小时55分钟
    private static int AccessTokenTime = 6900;


//    public static  void writeLogFileName(String fileName,String...contents){
//        String file=getLogFilePath()+File.separator +fileName;
//        writeLogFile(file,contents);
//    }
    public static  String getLogFilePath(String fileName){
        return getLogFilePath()+File.separator+fileName;
    }
    public static String getLogFilePath(){
        String os = System.getProperty("os.name");
        String logFilePath=null;
        if (os.contains("Windows")) {
            logFilePath = (LogUtils.class.getResource("/").getPath() + "log");
        } else if (os.contains("Linux")) {
            logFilePath = Constant.LOG_ROOT_PATH;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String data = format.format(new Date());
        logFilePath=(logFilePath + File.separator + data);
        File file = new File(logFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return logFilePath;
    }

//    public static void writeLogFile(String path,String...contents){
//
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            File file = new File(path);
//            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8"));
//            writer.write(formatter.format(new Date())+"\t");
//            for (String content : contents){
//                writer.write(content+"\t");
//            }
//            writer.write("\n");
//            writer.flush();
//            writer.close();
//        } catch (Exception e1) {
//            throw new BusinessException(333,e1.toString());
//        }
//    }

    public static void refreshAccessToken(String accessTokenPath,String appId,String appSecret){

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" +
                "&appid=" + appId +
                "&secret="+ appSecret;

        File file = new File(accessTokenPath);
        String access_token;
        long time = 1;

        try {
            if(!file.exists()){
                file.createNewFile();
            }

            //读取token
            BufferedReader br = new BufferedReader(new FileReader(file));
            access_token = br.readLine();
            token = access_token;

            if(access_token != null){
                time = Long.parseLong(br.readLine());
            }
            br.close();



            //token过期，重新获取
            if(System.currentTimeMillis() - time > AccessTokenTime * 1000){

                String result = HttpRequestUtils.sendGet(url);
                if(!result.contains("access_token")){
                    System.out.println("请求AccessToken失败");
                    return;
                }

                JSONObject jsonObject = JSON.parseObject(result);
                access_token = jsonObject.getString("access_token");
                token = access_token;
                time = System.currentTimeMillis();
//            String expires_in = jsonObject.getString("expires_in");

                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false),"UTF-8"));
                writer.write(access_token+"\n");
                writer.write(time+"\n");
                writer.flush();
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getToken() {
        return token;
    }

}
