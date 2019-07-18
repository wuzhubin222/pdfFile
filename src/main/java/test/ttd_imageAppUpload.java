package test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.szca.bss.toolkit.client.base.AppImage;
import cn.com.szca.bss.toolkit.client.base.AuthInfo;
import cn.com.szca.bss.toolkit.client.manage.Base64;
import cn.com.szca.bss.toolkit.client.manage.ServiceDone;
import cn.com.szca.bss.toolkit.client.request.CertAppImageRequest;
import cn.com.szca.bss.toolkit.client.response.Response;
import entity.ConResult;
import excption.BusinessException;
import utils.LogUtils;

public class ttd_imageAppUpload {

    public static void main(String[] args) throws Exception {
        //设置授权信息
//        AuthInfo authInfo = new AuthInfo();
//        authInfo.setProjectId("705");            //项目编号
//        authInfo.setLocationId("269");           //受理点ID
//        authInfo.setIp("");                        //IP
//        authInfo.setCertsn("5F64183039B45A8D");//证书序列号，测试环境适用
//        //authInfo.setCertsn("");                 //证书序列号，生产环境适用
//        authInfo.setAuthCode("EFKEAKFafjkadf123");//授权码
//
//        //构建图片数据请求类
//        CertAppImageRequest certAppImageRequest = new CertAppImageRequest();
//        certAppImageRequest.setAuthInfo(authInfo);
//        List appImageList = new ArrayList();
//
//        AppImage ai = new AppImage();
//        ai.setApplyid("20171000001959");     //图片对应的业务单ID
//        ai.setFilename("希吉营业执照.jpg");       //图片名称
//        ai.setFiletype("image/jpeg");   //图片类型
//
//        String image = getImage("E:\\希吉营业执照压缩.jpg");
//        ai.setImgbase64(image);                //图片BASE64数据, 客户端请限制图片小于300KB
//        ai.setFilesize(image.length());                  //图片大小
//        ai.setStatus("E");    //固定值
//        ai.setImgcate("3");   //图片分类  1：身份证正面；2：身份证反面；3：摄像头采集图片；4：申请表扫描件；5：身份证摄像头合成图片；
//
//        appImageList.add(ai);
//        certAppImageRequest.setList(appImageList);
//
//        //上传图片数据
//        ServiceDone sd = new ServiceDone();
//        //测试环境内网访问地址
////        String wsUrl = "http://192.168.100.208:8080/wspool3/service/imageAppManage?wsdl";
//        //测试环境外网访问地址
//        String wsUrl = "http://218.17.161.11:7083/wspool3/service/imageAppManage?wsdl";
//
//        Response resp = sd.sendAppImages(wsUrl, "imageAppUpload", certAppImageRequest, "beian.pfx", "1234");
//
//        //上传图片数据响应
//        if ("0".equals(resp.getResultCode())) {
//            //上传图片数据成功
//            System.out.println("图片上传成功,结果描述:" + resp.getResultDesc());
//        } else {
//            //上传图片数据失败
//            System.out.println("图片上传失败，失败原因:" + resp.getResultDesc());
//        }
    }


    public static void imageAppUoload(String logPath,String fileName, String filePath, String applyId){

        //设置授权信息
        AuthInfo authInfo = new AuthInfo();
        authInfo.setProjectId("708");            //项目编号
        authInfo.setLocationId("12");           //受理点ID
        authInfo.setIp("");                        //IP
        authInfo.setCertsn("39D8D0A401D2D818");//证书序列号，测试环境适用
        //authInfo.setCertsn("");                 //证书序列号，生产环境适用
        authInfo.setAuthCode("EFKEAKFafjkadf123");//授权码

        //构建图片数据请求类
        CertAppImageRequest certAppImageRequest = new CertAppImageRequest();
        certAppImageRequest.setAuthInfo(authInfo);
        List appImageList = new ArrayList();

        AppImage ai = new AppImage();
        ai.setApplyid(applyId);     //图片对应的业务单ID
        ai.setFilename(fileName);       //图片名称
//        ai.setFiletype("image/jpeg");   //图片类型

        String image = getImage(filePath,logPath);
        ai.setImgbase64(image);                //图片BASE64数据, 客户端请限制图片小于300KB
        ai.setFilesize(image.length());                  //图片大小
        ai.setStatus("E");    //固定值
        ai.setImgcate("3");   //图片分类  1：身份证正面；2：身份证反面；3：摄像头采集图片；4：申请表扫描件；5：身份证摄像头合成图片；

        appImageList.add(ai);
        certAppImageRequest.setList(appImageList);

        //上传图片数据
        ServiceDone sd = new ServiceDone();
        String wsUrl = "http://202.103.144.100:689/wspool/service/imageAppManage?wsdl";
        System.out.println(wsUrl);
        Response resp = null;
        try {
            resp = sd.sendAppImages(wsUrl, "imageAppUpload", certAppImageRequest, "beian.pfx", "1234");
        } catch (Exception e) {e.printStackTrace();
//            LogUtils.writeLogFile(logPath, "imageAppUoload","sendAppImages方法异常", e.toString());
            throw new BusinessException(3022, "资料上传失败");
        }

        //上传图片数据响应
        if (!"0".equals(resp.getResultCode()))
        {
            //上传图片数据失败
//            LogUtils.writeLogFile(logPath,"imageAppUoload","返回状态码:"+resp.getResultCode(),resp.getResultDesc());
            throw new BusinessException(3023,"资料上传失败");
        }
    }

    private static String getImage(String filepath,String logPath) {
        String image = "";

        File file = new File(filepath);
        if (filepath == null || filepath.equals("")) {
//            LogUtils.writeLogFile(logPath,"getImage",filepath,"无效的文件路径");
            throw new BusinessException(3024,"资料上传失败");
        }
        long len = file.length();
        byte[] bytes = new byte[(int) len];

        BufferedInputStream bufferedInputStream;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            int r = bufferedInputStream.read(bytes);
            if (r != len){
//                LogUtils.writeLogFile(logPath,"getImage",filepath,"读取文件不正确");
                throw new BusinessException(3025,"资料上传失败");
            }
            bufferedInputStream.close();
        } catch (IOException e) {e.printStackTrace();
//            LogUtils.writeLogFile(logPath,"getImage",filepath,e.toString());
            throw new BusinessException(3026,"资料上传失败");
        }

        image = Base64.encode(bytes);
        return image;
    }

}
