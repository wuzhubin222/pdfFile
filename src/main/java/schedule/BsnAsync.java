package schedule;

import Dao.CddfpsmsMapper;
import Dao.EfpInvoiceMapper;
import com.alibaba.fastjson.JSONObject;
import constant.Constant;
import excption.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import utils.BaseUtils;
import utils.HttpRequestUtils;
import utils.LogUtils;
import utils.MD5Security;
import utils.SendMsg.SendPMYMsg;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class BsnAsync extends BaseUtils {

    @Autowired
    private CddfpsmsMapper cddfpsmsMapper;
    @Autowired
    public EfpInvoiceMapper efpInvoiceMapper;


    public interface  MainPushCallBack{
        public void callback(String result,String url,int code);
    }
    @Async
    public synchronized  void mailPush(String logPath,String pdfUrl, String kprq, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String emailAccount,MainPushCallBack callback) {
        // String path = this.getClass().getResource("/").getPath() + "log/mailPush.txt";
        String url=null;
        try{
           // String path ="mailPush.txt";
            String gfmcs=gfmc.replace(" ", "");
           /* System.out.println(URLEncoder.encode("深圳市万华汽车服务投资控股有限公司") );
            System.out.println(URLEncoder.encode(xfmc) );*/
             url = Constant.XMURL+"mailPush?" +
                    "pdfUrl="+URLEncoder.encode(pdfUrl,"UTF-8")+
                    "&kprq="+URLEncoder.encode(kprq,"UTF-8")+
                    "&fpdm="+URLEncoder.encode(fpdm,"UTF-8")+
                    "&fphm="+URLEncoder.encode(fphm,"UTF-8")+
                    "&xfmc="+URLEncoder.encode(xfmc,"UTF-8")+
                    "&gfmc="+URLEncoder.encode(gfmcs,"UTF-8")+
                    "&jshj="+URLEncoder.encode(jshj,"UTF-8")+
                    "&emailAccount="+URLEncoder.encode(emailAccount,"UTF-8");
            //System.out.println(url);
            String result = HttpRequestUtils.get(url);
         //   LogUtils.writeLogFileName(path,result,"发票代码"+fpdm+"发票号码"+fphm+"Url地址"+pdfUrl+"邮箱"+emailAccount+"开票日期"+kprq);
            this.logOutput(result,"发票代码"+fpdm+"发票号码"+fphm+"Url地址"+pdfUrl+"邮箱"+emailAccount+"开票日期"+kprq);
            if(callback!=null) {
                callback.callback(result,url,200);
            }
        }catch (BusinessException e){
            if(callback!=null){
                e.printStackTrace();
                callback.callback(e.getMessage(),url,e.getErrorCode());
            }
        }catch (Exception e){
            if(callback!=null){
                e.printStackTrace();
                callback.callback(e.getMessage(),url,-1);
            }
        }catch(Error e){
            if(callback!=null) {
                e.printStackTrace();
                callback.callback(e.getMessage(),url,-1);
            }
        }

    }
        //邮件推送

    public String mailPush(String logPath,String pdfUrl, String kprq, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String emailAccount){
        mailPush(logPath,pdfUrl,kprq,fpdm,fphm,xfmc,gfmc,jshj,emailAccount,null);
        return null;
    }

    //邮件推送
    @Async
    public void mailPushs(String pdfUrls, String kprq, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String emailAccount){
        String path = this.getClass().getResource("/").getPath() + "log/mailPush.txt";
//        LogUtils.writeLogFile(path,pdfUrls,kprq,fpdm,fphm,xfmc,gfmc,jshj,emailAccount);
        String url = Constant.XMURL+"mailPushs?" +
                "pdfUrls="+pdfUrls+
                "&kprq="+kprq+
                "&fpdm="+fpdm+
                "&fphm="+fphm+
                "&xfmc="+xfmc+
                "&gfmc="+gfmc+
                "&jshj="+jshj+
                "&emailAccount="+emailAccount;
        String result = HttpRequestUtils.sendGet(url);
//        LogUtils.writeLogFile(path,result);
    }



    @Async
    public void mailPushTest(String fpdm, String fphm, String emailAccount){
        String path = this.getClass().getResource("/").getPath() + "log/mailPush.txt";
//        LogUtils.writeLogFile(fpdm,fphm,emailAccount);
        String url = Constant.XMURL+"sendEmail?" +
                "fpdm="+fpdm+
                "&fphm="+fphm+
                "&emailAccount="+emailAccount;
        String result = HttpRequestUtils.sendGet(url);
//        LogUtils.writeLogFile(path,result);
    }


    //本地邮件推送
    @Async
    public void mailPushlocal(String pdfUrl, String kprq, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String emailAccount){
        String path = this.getClass().getResource("/").getPath() + "log/mailPush.txt";
//        LogUtils.writeLogFile(path,pdfUrl,kprq,fpdm,fphm,xfmc,gfmc,jshj,emailAccount);
        String url = Constant.XMURL+"localmailPush?" +
                "pdfUrl="+pdfUrl+
                "&kprq="+kprq+
                "&fpdm="+fpdm+
                "&fphm="+fphm+
                "&xfmc="+xfmc+
                "&gfmc="+gfmc+
                "&jshj="+jshj+
                "&emailAccount="+emailAccount;
        String result = HttpRequestUtils.sendGet(url);
    /*    localEmailUtils.sendEmail(pdfUrl,Constant.XMURL+"localmailPush?" +
                "pdfUrl="+pdfUrl+
                "&kprq="+kprq+
                "&fpdm="+fpdm+
                "&fphm="+fphm+
                "&xfmc="+xfmc+
                "&gfmc="+gfmc+
                "&jshj="+jshj+
                "&emailAccount="+emailAccount);
*/
//        LogUtils.writeLogFile(path,result);
    }

    //本地邮件推送
    @Async
    public void mailPushslocal(String pdfUrls, String kprq, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String emailAccount){
        String path = this.getClass().getResource("/").getPath() + "log/mailPush.txt";
//        LogUtils.writeLogFile(path,pdfUrls,kprq,fpdm,fphm,xfmc,gfmc,jshj,emailAccount);
        String url = Constant.XMURL+"localmailPush?" +
                "pdfUrls="+pdfUrls+
                "&kprq="+kprq+
                "&fpdm="+fpdm+
                "&fphm="+fphm+
                "&xfmc="+xfmc+
                "&gfmc="+gfmc+
                "&jshj="+jshj+
                "&emailAccount="+emailAccount;
        String result = HttpRequestUtils.sendGet(url);
//        LogUtils.writeLogFile(path,result);
    }






    //公众号模板消息
    @Async
    public void tmpMsgPush(String logPath, String openId, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String createTime){
       /* String path = this.getClass().getResource("/").getPath() + "log/tmpMsgPush.txt";*/
        String path ="tmpMsgPush.txt";
//        LogUtils.writeLogFileName(path,openId,fpdm,fphm,xfmc,gfmc,jshj,createTime);
        String url = Constant.XMURL+"tmpMsgPush?" +
                "openId="+openId+
                "&fpdm="+fpdm+
                "&fphm="+fphm+
                "&xfmc="+xfmc+
                "&gfmc="+gfmc+
                "&jshj="+jshj+
                "&createTime="+createTime;
        String result = HttpRequestUtils.sendGet(url);
       // this.logOutput(result);
       // LogUtils.writeLogFileName(path,result);
        this.logOutput("公众号模板消息"+result);
    }



    //小程序模板消息
    @Async
    public void sendTemMsg(String logPath,String openId,String xfmc, String gfmc, String jshj, String kprq,String formId){
        // String path = this.getClass().getResource("/").getPath() + "log/tmpMsgPush.txt";
        String path =logPath+"tmpMsgPush.txt";

//        LogUtils.writeLogFile(path,openId,xfmc,gfmc,jshj,kprq,formId);
        String url = Constant.XMURL+"sendTemMsg?" +
                "openId="+openId+
                "&xfmc="+xfmc+
                "&gfmc="+gfmc+
                "&jshj="+jshj+
                "&kprq="+kprq+
                "&formId="+formId;
        String result = HttpRequestUtils.sendGet(url);
        this.logOutput("小程序模板消息"+result);
        //LogUtils.writeLogFile(path,result);
    }

    //短信
    @Async
    public void sendPhoneMsg(String pubPhone,String invoiceId,String qpm){
        String path = this.getClass().getResource("/").getPath() + "log/createTicketCode.txt";
        String url = Constant.XMURL+"createTicketCode?phone=" + pubPhone
                + "&invoiceId=" + invoiceId+"&qpm="+qpm;

        String result = HttpRequestUtils.sendGet(url);

//        LogUtils.writeLogFile(path,pubPhone,invoiceId,result);

    }





    //发布短信并添加到短信记录
    @Async
    public void sendMsg(String logPath,String to, String datas,String invoiceId, String[] tempId, String nsrsbh) throws Exception {
       /* String path = this.getClass().getResource("/").getPath() + "log/createTicketCode.txt";*/
        String path ="createTicketCode.txt";
        Map<String,Object> insetData=new HashMap<String,Object>();
        insetData.put("id", UUID.randomUUID().toString().replace("-",""));
        insetData.put("appid", SendPMYMsg.appId);
        insetData.put("templateid", datas);
        insetData.put("fromtype", "3");
        insetData.put("mobile", to);
        insetData.put("create_time",new Date());
        insetData.put("nsrsbh",nsrsbh);

        HashMap<String, Object> result = SendPMYMsg.sendMsgBak(to,datas,tempId);
        if("000000".equals(result.get("statusCode"))){
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            try{
                insetData.put("state","0");
                insetData.put("smsid",((HashMap<String, Object>) data.get("templateSMS")).get("smsMessageSid"));
                String ss= ((HashMap<String,Object>)data.get("templateSMS")).get("dateCreated").toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                insetData.put("receiveTime",sdf.parse(ss));
            }catch(Exception e){
                e.printStackTrace();

                throw new BusinessException("参数有误");
            }}else{
            insetData.put("state","1");
            insetData.put("parm1",result.get("statusCode"));
            insetData.put("parm2",result.get("statusMsg"));
        }
        int nums=cddfpsmsMapper.insertSmsMap(insetData);
        if (nums>=1){
         //   LogUtils.writeLogFileName(path,to,invoiceId, String.valueOf(result));
            this.logOutput("发布短信并添加到短信记录"+to,invoiceId, String.valueOf(result));
        }else{
            throw new BusinessException("短信存库失败");
        }
    }



    @Async
    public void sendWx(String logPath,String succId){
        //String path = this.getClass().getResource("/").getPath() + "log/createWx.txt";
       // String path ="createWx.txt";
        HashMap<String,String> para=new HashMap<String,String>();
        para.put("succId",succId);
        try {
           /* http://wxgz1.bjzsxx.com/wxDev/wxAddToBag*/
           /* http://wangjie.tunnel.qydev.com/wxDevTest*/
            String result=HttpRequestUtils.sendPost("http://wxgz1.bjzsxx.com/wxDev/wxAddToBag",para);
           this.logOutput(result,"succId:"+succId);
          //  LogUtils.writeLogFileName(path,result,"succId:"+succId);
        }catch (Exception e){
            throw new BusinessException(3046, "请求失败");
        }

    }


    //新版短信
    public static String postRequest(String mobile,String param,String userData,String templateId){

        String url="http://www.zypaas.com:9988/V1/Account/ACC3afb74f6202f4cbebcf3ddf86e33e950/sms/sureTempalteSend";
        String apiAccount="ACC3afb74f6202f4cbebcf3ddf86e33e950";
        String apiKey="API1e473b14e8484f6bb05d2800783928dc";
        Long timeStamp=System.currentTimeMillis();

        JSONObject map = new JSONObject();
        map.put("apiAccount", apiAccount);
        map.put("appId", "APPa74b7e4d14814bd9afdceb35998bc8e5");
        map.put("sign", MD5Security.EncodeMD5Hex(apiAccount+apiKey+timeStamp));
        map.put("timeStamp",timeStamp.toString());
        map.put("templateId",templateId);
        map.put("singerId", "msn7RWa3O9xLq702");
        map.put("mobile", mobile);
        map.put("userData",userData);
        map.put("statusPushAddr", "");
        map.put("param",param);

        String array_test = map.toJSONString();
        URL urlPath;
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        OutputStreamWriter outputStreamWriter = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            urlPath = new URL(url);
            httpURLConnection = (HttpURLConnection)urlPath.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);

            //添加请求体
            outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(),"UTF-8");
            outputStreamWriter.write(array_test);
            outputStreamWriter.flush();

            //请求返回的数据
            inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }



    /*回调*/
   // @Async
    public  void sendbackMsg(String sendUrl, String id, String kprId, String fpdm, String fphm, String kptime, String pdfUrl, String email) throws UnsupportedEncodingException {
        String data = "invguid=" + id + "&kprId=" + kprId + "&status=2&reson=&fpdm=" + fpdm + "&fphm=" + fphm +
                "&url=" + pdfUrl + "&email=" + email + "&kptime=" + kptime;
        String path = this.getClass().getResource("/").getPath() + "log/PushMessage.txt";

        /*URLEncoder.encode(data, "UTF-8")*/
        try {
            HashMap hashMap = new HashMap();
            hashMap.put("type", "1");
            hashMap.put("sendUrl",sendUrl);
            hashMap.put("data", data);
            HttpRequestUtils.sendPost(Constant.XMURL1, hashMap);
            this.logOutput("====开票成功回调"+sendUrl);
           //LogUtils.writeLogFile(path, "====开票成功回调"+sendUrl);
        } catch (Exception e) {
            this.logOutput("====开票回调错误信息"+e.getMessage());
            //LogUtils.writeLogFile(path, "错误信息"+e.getMessage());
        }

    }

    public static void main(String[] args) throws Exception {
        String sendUrl="http://qmg32m.natappfree.cc/tcis/getToken9";
        String kprId="454545454";
        String fpdm="111452454622";
        String fphm="45621545";
        String kptime="2019-03-21";
        String pdfUrl="http://invoicefileservice.bjzsxx.com/310100/201806/06/913101135574488816/201901/16/1/03100180021178581982.pdf";
        String email="1257831556@qq.com";
        String id="122354";
        //sendbackMsg(sendUrl,id,kprId,fpdm,fphm,kptime,pdfUrl,email);



        /*
        String gfmc="36-17-503         繆科萍";
        String gfmcs=gfmc.replace(" ", "");
        System.out.println(gfmcs);
        String url = Constant.XMURL+"mailPush?" +
                "pdfUrl="+"http://invoicefileservice.bjzsxx.com/310100/201806/06/913101135574488816/201901/16/1/03100180021178581982.pdf"+
                "&kprq="+"2019-01-16_10:53:28"+
                "&fpdm="+"031001800211"+
                "&fphm="+"78581982"+
                "&xfmc="+"深圳市彩生活物业管理有限公司上海分公司"+
                "&gfmc="+gfmcs+
                "&jshj="+"1027.00"+
                "&emailAccount="+"1257831509@qq.com";*/
        //http://wxgz1.bjzsxx.com/wxDev/mailPush?pdfUrl=http://invoicefileservice.bjzsxx.com/310100/201806/06/913101135574488816/201901/16/1/03100180021178581982.pdf&kprq=2019-01-16_10:53:28&fpdm=031001800211&fphm=78581982&xfmc=%E6%B7%B1%E5%9C%B3%E5%B8%82%E5%BD%A9%E7%94%9F%E6%B4%BB%E7%89%A9%E4%B8%9A%E7%AE%A1%E7%90%86%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%E4%B8%8A%E6%B5%B7%E5%88%86%E5%85%AC%E5%8F%B8&gfmc=36-17-503%E7%B9%86%E7%A7%91%E8%90%8D&jshj=1027.00&emailAccount=1257831509@qq.com

        //String result = HttpRequestUtils.sendGet(url);
        // System.out.println(result);

    }


}
