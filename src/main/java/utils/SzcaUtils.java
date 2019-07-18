package utils;

import cn.com.szca.bss.toolkit.client.base.*;
import cn.com.szca.bss.toolkit.client.manage.Base64;
import cn.com.szca.bss.toolkit.client.manage.ServiceDone;
import cn.com.szca.bss.toolkit.client.manage.ServiceWspoolDone;
import cn.com.szca.bss.toolkit.client.request.CertAppImageRequest;
import cn.com.szca.bss.toolkit.client.request.CertApplyQueryByIdRequest;
import cn.com.szca.bss.toolkit.client.request.CertApplyRequest;
import cn.com.szca.bss.toolkit.client.response.CertApplyQueryByIdResponse;
import cn.com.szca.bss.toolkit.client.response.CertApplyResponse;
import cn.com.szca.bss.toolkit.client.response.Response;
import com.alibaba.fastjson.JSONObject;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 雷琨 创建于2018/1/4 18:19
 */
public class SzcaUtils {


    public static void main(String[] args) throws Exception {

        String d = "bsw.png";
        String f = "dwd.jpegjiji";
        String r = ".jpg";
        String s= ".*?\\.(jpg|png|jpeg)$";
        System.out.println(s);
        System.out.println(d.matches(s));
        System.out.println(f.matches(s));
        System.out.println(r.matches(s));

        /**
         * 申请
         */
//        JSONObject jsonObject = certApply("测试","测试","测试","测试","测试","测试","测试","测试","测试","测试","测试");
//        System.out.println(jsonObject.toJSONString());


        //状态查询

        JSONObject applySelect=applySelect("20180300020755","");
        System.out.println(applySelect);

    }

    /**
     * CA资料提交申请
     */
    public static JSONObject
    certApply(String etpName, String etpAddr, String etpTaxId, String etpProvince, String etpCity, String agentName, String agentPhone,
                                       String agentIdCard, String efpLegalName, String etpPhone, String logPath) throws Exception {

//        LogUtils.writeLogFile(logPath, "SczaUtils.certApply", etpName, etpAddr, etpTaxId, etpProvince, etpCity, agentName, agentPhone, agentIdCard,
//                efpLegalName, etpPhone);

        //设置授权信息
        AuthInfo authInfo = new AuthInfo();
        authInfo.setProjectId("708");              //项目编号，链融科技:705
        authInfo.setLocationId("12");             //受理点ID
        authInfo.setIp("");                           //IP置空
        authInfo.setCertsn("39D8D0A401D2D818");    //证书序列号,测试环境
        authInfo.setAuthCode("EFKEAKFafjkadf123");//授权码

        //设置证书申请信息
        CertApplyInfo certApplyInfo = new CertApplyInfo();
        certApplyInfo.setCertType("ORGAN");       //证书申请类型(机构证书)
        //主题信息
        certApplyInfo.setCertdn("CN=" + etpName + ",OU=" + etpTaxId + ",O=" + etpName + ",L=" + etpCity + ",ST=" + etpProvince + ",C=CN");
        certApplyInfo.setApplyValidate(365);      //证书申请服务期限
        certApplyInfo.setCertValidate(365);       //证书申请有效期
        certApplyInfo.setEmail("");                 //电邮地址

        //设置自定义扩展域信息
        List<SelfExtRes> selfList = new ArrayList<SelfExtRes>();

        SelfExtRes selfExtRes1 = new SelfExtRes();
        selfExtRes1.setExtId("1.2.86.11.7.7550205");// 固定值
        selfExtRes1.setExtName("深圳CA扩展标识");      // 固定值
        selfExtRes1.setNullAble("N");                  // 固定值
        selfExtRes1.setExtValue(etpTaxId);                   // 由合作商定义/////////////////////////////
        selfList.add(selfExtRes1);

        SelfExtRes selfExtRes2 = new SelfExtRes();
        selfExtRes2.setExtId("1.2.86.11.7.7550222");// 固定值
        selfExtRes2.setExtName("身份证号");            // 固定值
        selfExtRes2.setNullAble("N");                  // 固定值
        selfExtRes2.setExtValue(agentIdCard); // 客户身份证号///////////////////////////jbr_dm
        selfList.add(selfExtRes2);

        SelfExtRes selfExtRes3 = new SelfExtRes();
        selfExtRes3.setExtId("1.2.86.11.7.7550224");// 固定值
        selfExtRes3.setExtName("项目类型");            // 固定值
        selfExtRes3.setNullAble("N");                  // 固定值
        selfExtRes3.setExtValue("BJZS");               //由合作商定义
        selfList.add(selfExtRes3);

        SelfExtRes selfExtRes4 = new SelfExtRes();
        selfExtRes4.setExtId("1.2.86.11.7.7550243.0001");// 固定值
        selfExtRes4.setExtName("统一社会信用代码");       // 固定值
        selfExtRes4.setNullAble("N");                    // 固定值
        selfExtRes4.setExtValue(etpTaxId);                     //由合作商定义///////////////////////
        selfList.add(selfExtRes4);

        SelfExtRes selfExtRes5 = new SelfExtRes();
        selfExtRes5.setExtId("1.2.86.11.7.7550307");// 固定值
        selfExtRes5.setExtName("OA号");                // 固定值
        selfExtRes5.setNullAble("N");                  // 固定值
        selfExtRes5.setExtValue("");                   //由合作商定义
        selfList.add(selfExtRes5);

        SelfExtRes selfExtRes6 = new SelfExtRes();
        selfExtRes6.setExtId("2.16.156.112548");     // 固定值
        selfExtRes6.setExtName("市电子政务实体唯一标识");// 固定值
        selfExtRes6.setNullAble("N");                  // 固定值
        selfExtRes6.setExtValue("");                   //由合作商定义
        selfList.add(selfExtRes6);
        certApplyInfo.setSelfExtResList(selfList);

        //设置业务信息
        BusinessInfo businessInfo = new BusinessInfo();
        businessInfo.setChargeMethod("MONTH");           //收费方式，月结
        businessInfo.setPlanKeyFee(new BigDecimal(0));  //应收介质费用
        businessInfo.setFactKeyFee(new BigDecimal(0));  //实收介质费用
        businessInfo.setPlanCertFee(new BigDecimal(0)); //应收证书费用
        businessInfo.setFactCertFee(new BigDecimal(0)); //实收证书费用
        businessInfo.setPlanOpenFee(new BigDecimal(0)); //应收签章费
        businessInfo.setFactOpenFee(new BigDecimal(0)); //实收签章费
        businessInfo.setDeliverMethod("SELF");            //领取方式
        businessInfo.setPostAddr("");                       //邮寄地址
        businessInfo.setExpressId("");                  //快递单号
        businessInfo.setPostCode("");                   //邮政编码
        businessInfo.setContacter("");                  //收件人姓名
        businessInfo.setCntPhoneNo("");                 //收件人联系电话
        businessInfo.setIsPrecord("N");                 //是否预登记
        businessInfo.setRefId("");                       //参考标识
        businessInfo.setRemitId("");                    //汇款单号
        businessInfo.setCheckRemarks("");              //审核备注

        //设置机构信息
        Company company = new Company();
        company.setCustName(etpName); //单位名称，必填//////////
        company.setIdType("JJ");         //单位证件类型，组织机构代码证，必填
        company.setIdNo(etpTaxId);     //组织机构代码，必填
        company.setRegisterAddr(etpAddr);//注册地址，必填///////////////
        company.setContactName(efpLegalName);                   //联系人，必填//////////////////////
        company.setContactPhoneNo(etpPhone);      //联系电话号码，必填///////////////////////////
        company.setContactAddr(etpAddr);//联系地址，必填///////////////////////////
        company.setZipCode("518053");         //邮政编码，必填/////////////////////
        company.setCity(etpCity);              //城市，必填/////////////////
        company.setProvince(etpProvince);         //省份，必填///////////////////////
        company.setAgentName(agentName);          //经办人姓名，必填////////////////
        company.setAgentIdType("SF");         //经办人证件类型，身份证，必填
        company.setAgentIdNo(agentIdCard); //经办人身份证号码，必填////////////////
        company.setAgentMobileNo(agentPhone);//经办人手机号码，必填
        company.setAgentPhoneNo("");//经办人电话号码
        company.setAgentFaxNo("");  //经办人传真号码
        company.setDepartment("");          //经办人所属部门
        company.setPosition("");           //经办人职务
        company.setAgentAddress(etpAddr);//经办人联系地址，必填////////////////////
        company.setAgentZipCode("");       //经办人邮政编码

        //构建证书申请信息类数组
        List<ApplyInfo> applyInfoList = new ArrayList<ApplyInfo>();
        ApplyInfo applyInfo = new ApplyInfo();
        applyInfo.setCertApplyInfo(certApplyInfo);
        applyInfo.setBusinessInfo(businessInfo);

        //申请机构证书(只选其一)
        applyInfo.setCompany(company);
        applyInfoList.add(applyInfo);

        //构建证书申请请求类
        CertApplyRequest certApplyRequest = new CertApplyRequest();
        certApplyRequest.setAuthInfo(authInfo);
        certApplyRequest.setList(applyInfoList);

        //证书申请
        ServiceWspoolDone sdn = new ServiceWspoolDone();
        String wsUrl = "http://202.103.144.100:689/wspool/service/certProcessManage?wsdl";
        CertApplyResponse resp = sdn.certApply(wsUrl, "certProcessManage",
                "RSA", "APPLY", certApplyRequest, "szca/beian.pfx", "1234");

        String applyId;
        JSONObject jsonObject = new JSONObject();
        //证书申请响应
        if ("0".equals(resp.getResultCode())) {
            //证书申请成功
            List<ApplyResult> applyResult = resp.getList();
            if (applyResult != null && applyResult.size() > 0) {
                for (ApplyResult apply : applyResult) {
                    applyId = apply.getApplyId();
                    jsonObject.put("errcode", "0");
                    jsonObject.put("errmsg", "success");
                    jsonObject.put("applyId", applyId);
                }
            }
        } else {
            //证书申请失败
            jsonObject.put("errcode", resp.getResultCode());
            jsonObject.put("errmsg", resp.getResultDesc());
        }
//        LogUtils.writeLogFile(logPath, "SczaUtils.certApply=SUCCESS", etpName, etpAddr, etpTaxId, etpProvince, etpCity, agentName, agentPhone, agentIdCard,
//                efpLegalName, etpPhone,jsonObject.toString());
        return jsonObject;
    }


    /**
     * CA资料图片上传
     */
    public static JSONObject imageAppUoload(String logPath, String fileName, String applyId, int fileSize, InputStream inputStream) throws Exception {

//        LogUtils.writeLogFile(logPath, "SczaUtils.imageAppUoload", fileName, applyId);

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


        byte[] bytes = new byte[fileSize];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.read(bytes);
        bufferedInputStream.close();
        String image = Base64.encode(bytes);

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
        resp = sd.sendAppImages(wsUrl, "imageAppUpload", certAppImageRequest, "szca/beian.pfx", "1234");

        JSONObject jsonObject = new JSONObject();
        //上传图片数据响应
        if ("0".equals(resp.getResultCode())) {
            jsonObject.put("errcode", "0");
            jsonObject.put("errmsg", "success");
        } else {
            jsonObject.put("errcode", resp.getResultCode());
            jsonObject.put("errmsg", resp.getResultDesc());
        }

//        LogUtils.writeLogFile(logPath, "SczaUtils.imageAppUoload", fileName, applyId,jsonObject.toJSONString());
        return jsonObject;
    }


    /**
     * CA审核状态查询
     */
    public static JSONObject applySelect(String applyId, String logPath) throws Exception {
//       LogUtils.writeLogFile(logPath, "SczaUtils.applySelect", applyId,logPath);
        //设置授权信息
        AuthInfo authInfo = new AuthInfo();
        authInfo.setProjectId("708");            //项目编号
        authInfo.setLocationId("12");          //受理点ID
        authInfo.setIp("");                        //IP，置空
        authInfo.setCertsn("39D8D0A401D2D818");//证书序列号，测试环境适用
        authInfo.setAuthCode("EFKEAKFafjkadf123");//授权码

        CertApplyQueryByIdRequest certApplyQueryByIdRequest = new CertApplyQueryByIdRequest();
        certApplyQueryByIdRequest.setAuthInfo(authInfo);

        //构建业务单信息查询请求类
        List<String> applyIdLi = new ArrayList<String>();
        applyIdLi.add(applyId);               //设置业务单号///////////////////////////////
        certApplyQueryByIdRequest.setList(applyIdLi);

        //业务单信息查询
        ServiceWspoolDone sdn = new ServiceWspoolDone();
        String wsUrl = "http://202.103.144.100:689/wspool/service/certProcessManage?wsdl";
        CertApplyQueryByIdResponse resp = sdn.certApplyQueryById(wsUrl, "certProcessManage", "RSA", "QAPPLYBYID", certApplyQueryByIdRequest, "szca/beian.pfx", "1234");

        JSONObject jsonObject = new JSONObject();
        //业务信息响应
        if ("0".equals(resp.getResultCode())) {
            //业务信息查询成功
//            System.out.println("**************** resultCode ： " + resp.getResultCode() + "; resultDesc : " + resp.getResultDesc());
            List<AllInfo> li = resp.getList();
            if (null != li && li.size() > 0) {
                for (int i = 0; i < li.size(); i++) {
                    AllInfo allInfo = li.get(i);
                    CertInfo certInfo = allInfo.getCertInfo();
//                    BusinessInfo businessInfo = allInfo.getBusinessInfo();
//                    System.out.println("****************applyId : " + certInfo.getApplyId()); //业务单号
//                    System.out.println("****************busiStatus : " + certInfo.getBusiStatus());//业务单状态，“SUCCESS”和“ARCHIVE”表示已下载证书；“DISABLE”表示业务单已作废；“AUDITFAIL”表示业务单审核驳回；
                    // 审核不通过理由（关注后重新申请）
                    ApplyAuditInfo applyAuditInfo = allInfo.getAuditInfo();
                    jsonObject.put("errcode", "0");
                    jsonObject.put("errmsg", "success");
                    if(certInfo.getBusiStatus().equalsIgnoreCase("ARCHIVE")){
                        jsonObject.put("status", "SUCCESS");
                    }else if(certInfo.getBusiStatus().equalsIgnoreCase("AUDITFAIL")){
                        jsonObject.put("reason", applyAuditInfo.getAuditRemark());
                    }
                    jsonObject.put("status", certInfo.getBusiStatus());
                }
            }
        } else {   //业务信息查询失败
            jsonObject.put("errcode", resp.getResultCode());
            jsonObject.put("errmsg", resp.getResultDesc());
        }
        return jsonObject;
    }


    /**
     * CA证书下载
     */
    public static JSONObject certDown(String etpTaxId, String logPath) throws Exception {

//        LogUtils.writeLogFile(logPath, "SczaUtils.certDown", etpTaxId);

        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        String url = "http://202.103.144.100:686/signsup/service/supCertDownManage?wsdl";
        org.apache.cxf.endpoint.Client client = dcf.createClient(url);
        Object[] objects;

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
        applyIdElt.setText(etpTaxId);
        OutputFormat opf = OutputFormat.createPrettyPrint();
        opf.setTrimText(true);
        // 获取XML字符串形式
        StringWriter writerStr = new StringWriter();
        XMLWriter xmlw = new XMLWriter(writerStr, opf);
        xmlw.write(doc);
        String xmlData = writerStr.getBuffer().toString();

        objects = client.invoke("supP12CertDown", xmlData);
        String resultData = objects[0].toString();


        Document document = DocumentHelper.parseText(resultData);
        // 取得根节点
        Element rootNode = document.getRootElement();
        Element baseCodeNode = rootNode.element("resultCode");
        String resultCode = baseCodeNode.getText();
        Element resultdesc = rootNode.element("resultdesc");
        String resultdescStr = "";
        if (resultdesc != null) {
            //错误描述
            resultdescStr = resultdesc.getText();
        }

        JSONObject jsonObject = new JSONObject();
        if (resultCode.equals("0")) {
            Element signP12Node = rootNode.element("signP12");
            String p12Str = signP12Node.getText();
            Element p12pwdNode = rootNode.element("p12pwd");
            String p12Pwd = p12pwdNode.getText();
            //ca证书密码
            String decryptResult = SzcaAESCrypt.AES_Decrypt(SzcaAESCrypt.keyStr_RSA2048, p12Pwd);

            byte[] b = Base64.decode(p12Str);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据v
                    b[i] += 256;
                }
            }
            jsonObject.put("errcode", "0");
            jsonObject.put("password", decryptResult);
            jsonObject.put("cert_bytes", b);
//            FileOutputStream out = new FileOutputStream(downFilePath);
//            out.write(b);
//            out.flush();
//            out.close();
        } else {
            jsonObject.put("errcode", resultCode);
            jsonObject.put("errmsg", resultdescStr);
        }

//        LogUtils.writeLogFile(logPath, "SczaUtils.certDown", etpTaxId,jsonObject.toJSONString());
        return jsonObject;
    }


}
