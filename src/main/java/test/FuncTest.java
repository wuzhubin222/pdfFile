package test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloopen.rest.sdk.utils.encoder.BASE64Encoder;
import schedule.BsnAsync;
import utils.HttpRequestUtils;
import utils.PdfQdUtils;
import utils.PdfUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class FuncTest {

    public static void main(String[] args) throws UnsupportedEncodingException {

//        String fpdm = "11111111";
//        String fphm = "22222222";
//        String xfmc = "去玩儿童与";
//        String gfmc = "李凯建华高佛";
//        String jshj = "3.24";
//        String kprq = "2017-11-15_17:03:39";
//        String emailAccount = "631064249@qq.com";
//        String pdfUrls = "[\"http://invoicefileservice.bjzsxx.com/440300/201705/02/91440300MA5DK61A1K/201711/15/1/04403170011113255067.pdf\",\"http://invoicefileservice.bjzsxx.com/440300/201705/02/91440300MA5DK61A1K/201711/15/1/04403170011113255066发票清单1.pdf\"]";
//        pdfUrls = URLEncoder.encode(pdfUrls,"utf-8");
//        BsnAsync bsnAsync = new BsnAsync();
//        bsnAsync.mailPushs(pdfUrls,kprq,fpdm,fphm,xfmc,gfmc,jshj,emailAccount);

//        try {
//            URL urlPdf = new URL("http://invoicefileservice.bjzsxx.com/440300/201705/02/91440300MA5DK61A1K/201711/15/1/04403170011113255066发票清单1.pdf");
//            InputStream ins = urlPdf.openStream();
//            ins.close();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        String dw = HttpRequestUtils.postRequest("http://wxdev.natapp4.cc/invoice/createDZp","qqlsh=2017110400260191067&dzfpResult={\"gfsh\":\"91110102633082712Q\",\"gfmc\":\"北京中税信息网络有限公司\",\"gfyhzh\":\"\",\"se\":\"￥17.48\",\"kprq\":\"2017年11月04日\",\"bz\":\"\",\"mxxx\":[{\"ggxh\":\"\",\"se\":\"17.48\",\"dw\":\"\",\"hwmc\":\"餐饮\",\"dj\":\"600.00\",\"sl\":\"0.9708738\",\"je\":\"582.52\",\"slv\":\"3%\"}],\"xfdzdh\":\"深圳市南山区南头街道月亮湾大道与桂湾三路交汇处龙海商业广场B区105号铺 18927463555\",\"skr\":\"\",\"fpdm\":\"044031600111\",\"mw\":\"6>>>/-133-*1+4685702<704-*09<+<<+5*/+415091-5472724>-1757-83485/<021*>+43/4512+534+84**8<663>/+49/791>/61-54\",\"jshjxx\":\"600.00\",\"kpr\":\"刘景峰\",\"qrm\":\"01,10,044031600111,02407312,582.52,20171104,80883429292637169951,D8EA,\",\"xfyhzh\":\"招商银行-深圳泰然支行 6226097550117213\",\"fhr\":\"\",\"gfdzdh\":\"\",\"jshjdx\":\"陆佰圆整\",\"je\":\"￥582.52\",\"xfsh\":\"92440300L91421355X\",\"jqbh\":\"661721593369\",\"xfmc\":\"深圳市南山区宝鼎烧烤吧\",\"fphm\":\"02407312\",\"jym\":\"80883429292637169951\"}:");
//        String we = "{\"gfsh\":\"91110102633082712Q\",\"gfmc\":\"北京中税信息网络有限公司\",\"gfyhzh\":\"\",\"se\":\"￥17.48\",\"kprq\":\"2017年11月04日\",\"bz\":\"\",\"mxxx\":[{\"ggxh\":\"\",\"se\":\"17.48\",\"dw\":\"\",\"hwmc\":\"餐饮\",\"dj\":\"600.00\",\"sl\":\"0.9708738\",\"je\":\"582.52\",\"slv\":\"3%\"}],\"xfdzdh\":\"深圳市南山区南头街道月亮湾大道与桂湾三路交汇处龙海商业广场B区105号铺 18927463555\",\"skr\":\"\",\"fpdm\":\"044031600111\",\"mw\":\"6>>>/-133-*1+4685702<704-*09<+<<+5*/+415091-5472724>-1757-83485/<021*>+43/4512+534+84**8<663>/+49/791>/61-54\",\"jshjxx\":\"600.00\",\"kpr\":\"刘景峰\",\"qrm\":\"01,10,044031600111,02407312,582.52,20171104,80883429292637169951,D8EA,\",\"xfyhzh\":\"招商银行-深圳泰然支行 6226097550117213\",\"fhr\":\"\",\"gfdzdh\":\"\",\"jshjdx\":\"陆佰圆整\",\"je\":\"￥582.52\",\"xfsh\":\"92440300L91421355X\",\"jqbh\":\"661721593369\",\"xfmc\":\"深圳市南山区宝鼎烧烤吧\",\"fphm\":\"02407312\",\"jym\":\"80883429292637169951\"}";
//        System.out.println(we);
//        String dw = HttpRequestUtils.postRequest("http://wxdev.natapp4.cc/invoice/createDZp","dzfpResult="+we+"&qqlsh=2017110400260191067");
//        System.out.println(dw);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("totalNum",5);
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(1);
//        jsonArray.add(3);
//        jsonArray.add(4);
//        jsonArray.add(3);
//        jsonArray.add(5);
//        System.out.println(jsonArray.getIntValue(4));
//        jsonObject.put("length",jsonArray);
//        System.out.println(jsonObject.toString());

//        ttd_testCertApply.certApply("深圳市桂小厨餐饮有限公司壹方城分公司","深圳市宝安区新安街道新湖路壹方城99号L4层030铺","91440300MA5DPBUX5A",
//                "广东省","深圳市","陈惜梅","15920086700","445281199206032806","李恒","深圳市宝安区新安街道新湖路壹方城99号L4层030铺");

    }


}
