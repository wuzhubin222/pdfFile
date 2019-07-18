package utils;

import cn.com.szca.bss.toolkit.client.manage.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import excption.BusinessException;
import model.EfpInvoice;
import model.EfpInvoiceCopy;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import test.AESCrypt;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;

public class CaUtils {

   /* public static void main(String[] args) throws IOException {

        //深圳市鱼上餐饮管理有限公司欢乐海岸分公司 701557

        String password = CaUtils.downCaBynsrsbh("D:/1.pfx",
                "92440300L91042806L", "D:/pdfFileV2.0/out/artifacts/pdfFile_war_exploded/WEB-INF/classes/log/ca.txt");
        System.out.println(password);
    }*/

    /**
     * 下载深圳ca文件到指定目录
     *
     * @param logPath      日志文件路径
     * @param nsrsbh       纳税人识别号
     * @param downFilePath ca文件输出路径
     * @returnx
     */
    public static String downCaBynsrsbh(String logPath, String nsrsbh, String downFilePath) {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        String url = "http://202.103.144.100:686/signsup/service/supCertDownManage?wsdl";
        org.apache.cxf.endpoint.Client client = dcf.createClient(url);
        Object[] objects;
        String xmlData = getSupP12CertDownReqXmlData(logPath, nsrsbh);
        System.out.println("========"+xmlData);
        try {
            objects = client.invoke("supP12CertDown", xmlData);
        } catch (Exception e) {
            throw new BusinessException(3000, "下载证书失败");
        }
        String resultData = objects[0].toString();
        return readXml(resultData, logPath, downFilePath);
    }

    //生产申请证书的XML数据
    public static String getSupP12CertDownReqXmlData(String logPath, String applyid) {
        DocumentFactory df = DocumentFactory.getInstance();
        Document doc = df.createDocument("UTF-8");
        // 创建根节点
        Element rootElt = doc.addElement("inputdata");
        Element authinfoElt = rootElt.addElement("authinfo");
        //项目编号
        Element projectidElt = authinfoElt.addElement("projectid");
        projectidElt.setText("708");
        //受理网点,不验证
        Element locationidElt = authinfoElt.addElement("locationid");
        locationidElt.setText("12");
        Element ipElt = authinfoElt.addElement("ip");
        ipElt.setText("");
        Element certsnElt = authinfoElt.addElement("certsn");
        //证书序列号
        certsnElt.setText("39D8D0A401D2D818");
        //客户授权码
        Element authcodeElt = authinfoElt.addElement("authcode");
        authcodeElt.setText("EFKEAKFafjkadf123");
        //szca扩展项想在证书
        Element applyIdElt = rootElt.addElement("szcaExtid");
        applyIdElt.setText(applyid);
//            //业务单相关信息
//            Element applyIdElt = rootElt.addElement("applyId");
//            applyIdElt.setText(applyid);
        OutputFormat opf = OutputFormat.createPrettyPrint();
        opf.setTrimText(true);
        // 获取XML字符串形式
        StringWriter writerStr = new StringWriter();
        XMLWriter xmlw = new XMLWriter(writerStr, opf);
        try {
            xmlw.write(doc);
        } catch (IOException e) {
            throw new BusinessException(3001, "下载证书失败");
        }
        return writerStr.getBuffer().toString();
    }

    //处理返回结果
    public static String readXml(String xmlData, String logPath, String outFilePath) {
        String decryptResult;
        Document document;
        String resultdescStr = "";
        try {
            //读取xml字符串
            document = DocumentHelper.parseText(xmlData);
            // 取得根节点
            Element rootNode = document.getRootElement();
            Element baseCodeNode = rootNode.element("resultCode");
            String resultCode = baseCodeNode.getText();
            Element resultdesc = rootNode.element("resultdesc");
            if(resultdesc!=null){
                resultdescStr = resultdesc.getText();
            }
            if (resultCode.equals("0")) {
                Element signP12Node = rootNode.element("signP12");
                String p12Str = signP12Node.getText();
                Element p12pwdNode = rootNode.element("p12pwd");
                String p12Pwd = p12pwdNode.getText();
                decryptResult = AESCrypt.AES_Decrypt(AESCrypt.keyStr_RSA2048, p12Pwd);
                byte[] b = Base64.decode(p12Str);
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {// 调整异常数据v
                        b[i] += 256;
                    }
                }
                createFile(new File(outFilePath));
                FileOutputStream out = new FileOutputStream(outFilePath);
                out.write(b);
                out.flush();
                out.close();
            } else {
                throw new BusinessException(3005, "下载证书失败:"+resultdescStr);
            }
        } catch (DocumentException e) {
            throw new BusinessException(3001, "下载证书失败:"+resultdescStr);
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException(3002, "下载证书失败:"+resultdescStr);
        } catch (FileNotFoundException e) {
            throw new BusinessException(3003, "下载证书失败:"+resultdescStr);
        } catch (IOException e) {
            throw new BusinessException(3004, "下载证书失败:"+resultdescStr);
        }
        return decryptResult;
    }
    private static void createFile(File file) throws IOException {
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
    }
}
