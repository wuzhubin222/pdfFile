package test;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;

public class ttd_testQueryDown {

    public static void main(String args[]) throws Exception {
        try
        {
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();

            //测试环境内网地址
            //String url = "http://192.168.2.6:8084/signsup/service/supCertDownManage?wsdl";
            //测试环境外网地址
            String url = "http://218.17.161.11:8067/signsup/service/supCertDownManage?wsdl";

            org.apache.cxf.endpoint.Client client = dcf.createClient(url);

            //测试环境数据
            String applyid = "20171000001947";

            String xmlData = getSupP12CertDownReqXmlData(applyid);
            System.out.println("xmlData : " + xmlData);
            Object[] objects = client.invoke("supP12CertDown", xmlData);

            String resultData = objects[0].toString();

            //输出调用结果
            System.out.println("************************** resultData : " + resultData);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //生产申请证书的XML数据
    public static String getSupP12CertDownReqXmlData(String applyid) {
        String strXml = null;
        try
        {
            DocumentFactory df = DocumentFactory.getInstance();
            Document doc = df.createDocument("UTF-8");

            // 创建根节点
            Element rootElt = doc.addElement("inputdata");

            Element authinfoElt = rootElt.addElement("authinfo");

            Element projectidElt = authinfoElt.addElement("projectid");
            projectidElt.setText("510");

            Element locationidElt = authinfoElt.addElement("locationid");
            locationidElt.setText("604");

            Element ipElt = authinfoElt.addElement("ip");
            ipElt.setText("");

            Element certsnElt = authinfoElt.addElement("certsn");
            certsnElt.setText("5F64183039B45A8D");

            Element authcodeElt = authinfoElt.addElement("authcode");
            authcodeElt.setText("EFKEAKFafjkadf123");

            //业务单相关信息
            Element applyIdElt = rootElt.addElement("applyId");
            applyIdElt.setText(applyid);

            OutputFormat opf = OutputFormat.createPrettyPrint();
            opf.setTrimText(true);
            // 获取XML字符串形式
            StringWriter writerStr = new StringWriter();
            XMLWriter xmlw = new XMLWriter(writerStr, opf);
            xmlw.write(doc);
            strXml = writerStr.getBuffer().toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return strXml;
    }
}

