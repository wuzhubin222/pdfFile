package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import excption.BusinessException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpRequestUtils {

    public static void main(String ...args){
        //%C9%EE%DB%DA%CA%D0%CD%F2%BB%AA%C6%FB%B3%B5%B7%FE%CE%F1%CD%B6%D7%CA%BF%D8%B9%C9%D3%D0%CF%DE%B9%AB%CB%BE
      /*  try{
            System.out.println(
                    URLEncoder.encode("深圳市万华汽车服务投资控股有限公司","GBK")
            );
        }catch (Exception e){

        }*/

     //   System.out.println(get("http://wxgz1.bjzsxx.com/wxDev/mailPush?pdfUrl=http://invoicefileservice.bjzsxx.com/440300/201807/20/91440300MA5DLA9G8H/201902/28/1/04403180021123752895.pdf&kprq=2019-02-28_16:49:39&fpdm=044031800211&fphm=23752895&xfmc=%E6%B7%B1%E5%9C%B3%E5%B8%82%E4%B8%87%E5%8D%8E%E6%B1%BD%E8%BD%A6%E6%9C%8D%E5%8A%A1%E6%8A%95%E8%B5%84%E6%8E%A7%E8%82%A1%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&gfmc=%E6%B7%B1%E5%9C%B3%E5%B8%82%E4%B8%87%E5%8D%8E%E6%B1%BD%E8%BD%A6%E6%9C%8D%E5%8A%A1%E6%8A%95%E8%B5%84%E6%8E%A7%E8%82%A1%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%E4%B8%9C%E8%8E%9E%E5%88%86%E5%85%AC%E5%8F%B8&jshj=560.00&emailAccount=1257831509@qq.com"));
        //http://wxgz1.bjzsxx.com/wxDev/mailPush?pdfUrl=http://invoicefileservice.bjzsxx.com/440300/201807/20/91440300MA5DLA9G8H/201902/28/1/04403180021123752895.pdf&kprq=2019-02-28_16:51:28&fpdm=044031800211&fphm=23752895&xfmc=深圳市万华汽车服务投资控股有限公司&gfmc=深圳市万华汽车服务投资控股有限公司东莞分公司&jshj=560.00&emailAccount=1257831509@qq.com
        //http://wxgz1.bjzsxx.com/wxDev/mailPush?pdfUrl=http://invoicefileservice.bjzsxx.com/440300/201807/20/91440300MA5DLA9G8H/201902/28/1/04403180021123752895.pdf&kprq=2019-02-28_16:49:39&fpdm=044031800211&fphm=23752895&xfmc=%E6%B7%B1%E5%9C%B3%E5%B8%82%E4%B8%87%E5%8D%8E%E6%B1%BD%E8%BD%A6%E6%9C%8D%E5%8A%A1%E6%8A%95%E8%B5%84%E6%8E%A7%E8%82%A1%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&gfmc=%E6%B7%B1%E5%9C%B3%E5%B8%82%E4%B8%87%E5%8D%8E%E6%B1%BD%E8%BD%A6%E6%9C%8D%E5%8A%A1%E6%8A%95%E8%B5%84%E6%8E%A7%E8%82%A1%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%E4%B8%9C%E8%8E%9E%E5%88%86%E5%85%AC%E5%8F%B8&jshj=560.00&emailAccount=1257831509@qq.com
    //    System.out.println(get("http://wxgz1.bjzsxx.com/wxDev/mailPush?pdfUrl=http://invoicefileservice.bjzsxx.com/440300/201807/20/91440300MA5DLA9G8H/201902/28/1/04403180021123752895.pdf&kprq=2019-02-28_16:49:39&fpdm=044031800211&fphm=23752895&xfmc=%E6%B7%B1%E5%9C%B3%E5%B8%82%E4%B8%87%E5%8D%8E%E6%B1%BD%E8%BD%A6%E6%9C%8D%E5%8A%A1%E6%8A%95%E8%B5%84%E6%8E%A7%E8%82%A1%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&gfmc=%E6%B7%B1%E5%9C%B3%E5%B8%82%E4%B8%87%E5%8D%8E%E6%B1%BD%E8%BD%A6%E6%9C%8D%E5%8A%A1%E6%8A%95%E8%B5%84%E6%8E%A7%E8%82%A1%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%E4%B8%9C%E8%8E%9E%E5%88%86%E5%85%AC%E5%8F%B8&jshj=560.00&emailAccount=1257831509@qq.com"));

            String id="";
            String kprId="";
        String fpdm="";
        String fphm="";
        String kptime="";
        String pdfUrl="";
        String email="";

        String data = "invguid=" + id +
                "&kprId=" + kprId + "&status=2&reson=&fpdm=" + fpdm + "&fphm=" + fphm + "&kptime=" + kptime+
                "&url=" + pdfUrl +"&email=" + email;
        String result = HttpRequestUtils.postRequest("",data);



    }
    public static String get(String url) {

        StringBuffer result = new StringBuffer();
        BufferedReader in = null;
        int code=-1;
        try {
            String urlNameString = url  ;
            URL realUrl = new URL(urlNameString);
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            if(connection.getResponseCode()==200){
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
            }else{
                if(connection.getErrorStream()!=null){
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }else {
                    in=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }


            }
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line+"\n");
            }
            code=connection.getResponseCode();

        } catch (Exception e) {
            e.printStackTrace();
            throw  new BusinessException(-1,e.getMessage());
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if(code!=200){
            throw new BusinessException(code,result.toString());
        }
        return result.toString();
    }

    public static String sendGet(String url) {

        HttpGet get = new HttpGet(url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse resp;
        String result = null;

        try {
            resp = client.execute(get);
            if (resp.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(resp.getEntity(), "UTF-8");
            }else{
                result = EntityUtils.toString(resp.getEntity(), "UTF-8");
            }
        } catch (IOException e) {e.printStackTrace();
//            System.out.println(e.getMessage() + "===" + e.getCause().getMessage());
            throw new BusinessException(3046, "请求失败");
        }
        return result;
    }


    public static String sendPost(String url, HashMap<String, String> dataMap) {
        HttpPost post = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response;
        String result = null;
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        //构建超时等配置信息
        RequestConfig config = RequestConfig.custom().setConnectTimeout(3000) //连接超时时间
                .setConnectionRequestTimeout(3000) //从连接池中取的连接的最长时间
                .setSocketTimeout(10 *1000) //数据传输的超时时间
                .setStaleConnectionCheckEnabled(true) //提交请求前测试连接是否可用
                .build();
        //设置请求配置时间
        post.setConfig(config);
        if (dataMap != null) {
            for (String key : dataMap.keySet()) {
                nvps.add(new BasicNameValuePair(key, dataMap.get(key)));
            }
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps));
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("postWx返回值:" + result);
            }else{
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException e) {e.printStackTrace();
//            System.out.println(e.getMessage() + "===" + e.getCause().getMessage());
            throw new BusinessException(3046, "请求失败");
        }
        return result;
    }


    public static String postRequest(String url, String data) {
        URL urlPath;
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        OutputStreamWriter outputStreamWriter = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            urlPath = new URL(url);
            httpURLConnection = (HttpURLConnection) urlPath.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setReadTimeout(30000);
            //添加请求头
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");

            //添加请求体
            outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
            outputStreamWriter.write(data);
            outputStreamWriter.flush();

            //请求返回的数据
            inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {e.printStackTrace();
            e.printStackTrace();
        } finally {
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


    public static void downLoad(String url,String localFileName) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        OutputStream out = null;
        InputStream in = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();
            long length = entity.getContentLength();
            if (length <= 0) {
                System.out.println("下载文件不存在！");
                throw new BusinessException(3061, "下载文件不存在！");
            }
            File file = new File(localFileName);
            if(!file.exists()){
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength=in.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }
            out.flush();

//            String result = EntityUtils.toString(entity, "UTF-8");
//            System.out.println("postWx返回值:" + result);
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject = JSON.parseObject(result);
//            } catch (Exception e) {
//                jsonObject = null;
//            }
//            if(jsonObject != null){
//                Integer statusCode = jsonObject.getInteger("statusCode");
//                if(statusCode != 200){
//                    throw new BusinessException(3062, jsonObject.getString("message"));
//                }
//            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void downLoad1(String url,String localFileName) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
//        OutputStream out = null;
//        InputStream in = null;
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            HttpEntity entity = httpResponse.getEntity();
//            in = entity.getContent();
            long length = entity.getContentLength();
            if (length <= 0) {
                System.out.println("下载文件不存在！");
                throw new BusinessException(3061, "下载文件不存在！");
            }
            String result = EntityUtils.toString(entity, "UTF-8");
            System.out.println("postWx返回值:" + result);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = JSON.parseObject(result);
            } catch (Exception e) {
                jsonObject = null;
            }
            if(jsonObject != null){
                Integer statusCode = jsonObject.getInteger("statusCode");
                if(statusCode != 200){
                    throw new BusinessException(3062, jsonObject.getString("message"));
                }
            }
    }

}
