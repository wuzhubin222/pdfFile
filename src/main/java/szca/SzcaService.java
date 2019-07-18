package szca;

import Dao.EfpCaMaterialMapper;
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
import excption.BusinessException;
import model.EfpCaMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.cn;

public class SzcaService {


    public static void main(String[] args) {
        //public static void applySelect(String applyId, String logPath, EfpCaMaterialMapper efpCaMaterialMapper, String NsrDabId){

String applyTd = "20180400004002";
String logPath = "a/b.txt";
        String NsrDabId = "123";
        applySelect(applyTd,logPath,null,NsrDabId);
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

    public static String certApply(String gsmc, String lxdz, String nsrsbh, String province, String city, String czyMc, String czyPhone,
                                   String czySfz, String frmc, String lxdh,String logPath){

//        LogUtils.writeLogFile(logPath, "certApply","申请资料信息",gsmc,lxdz,nsrsbh,province,city,czyMc,czyPhone,czySfz,
//                frmc,lxdh);

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
        certApplyInfo.setCertdn("CN=" + gsmc + ",OU=" + nsrsbh + ",O=" + gsmc + ",L=" + city + ",ST=" + province + ",C=CN");
        certApplyInfo.setApplyValidate(365);      //证书申请服务期限
        certApplyInfo.setCertValidate(365);       //证书申请有效期
        certApplyInfo.setEmail("");                 //电邮地址

        //设置自定义扩展域信息
        List<SelfExtRes> selfList = new ArrayList<SelfExtRes>();

        SelfExtRes selfExtRes1 = new SelfExtRes();
        selfExtRes1.setExtId("1.2.86.11.7.7550205");// 固定值
        selfExtRes1.setExtName("深圳CA扩展标识");      // 固定值
        selfExtRes1.setNullAble("N");                  // 固定值
        selfExtRes1.setExtValue(nsrsbh);                   // 由合作商定义/////////////////////////////
        selfList.add(selfExtRes1);

        SelfExtRes selfExtRes2 = new SelfExtRes();
        selfExtRes2.setExtId("1.2.86.11.7.7550222");// 固定值
        selfExtRes2.setExtName("身份证号");            // 固定值
        selfExtRes2.setNullAble("N");                  // 固定值
        selfExtRes2.setExtValue(czySfz); // 客户身份证号///////////////////////////jbr_dm
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
        selfExtRes4.setExtValue(nsrsbh);                     //由合作商定义///////////////////////
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
        company.setCustName(gsmc); //单位名称，必填//////////
        company.setIdType("JJ");         //单位证件类型，组织机构代码证，必填
        company.setIdNo(nsrsbh);     //组织机构代码，必填
        company.setRegisterAddr(lxdz);//注册地址，必填///////////////
        company.setContactName(frmc);                   //联系人，必填//////////////////////
        company.setContactPhoneNo(lxdh);      //联系电话号码，必填///////////////////////////
        company.setContactAddr(lxdz);//联系地址，必填///////////////////////////
        company.setZipCode("518053");         //邮政编码，必填/////////////////////
        company.setCity(city);              //城市，必填/////////////////
        company.setProvince(province);         //省份，必填///////////////////////
        company.setAgentName(czyMc);          //经办人姓名，必填////////////////
        company.setAgentIdType("SF");         //经办人证件类型，身份证，必填
        company.setAgentIdNo(czySfz); //经办人身份证号码，必填////////////////
        company.setAgentMobileNo(czyPhone);//经办人手机号码，必填
        company.setAgentPhoneNo("");//经办人电话号码
        company.setAgentFaxNo("");  //经办人传真号码
        company.setDepartment("");          //经办人所属部门
        company.setPosition("");           //经办人职务
        company.setAgentAddress(lxdz);//经办人联系地址，必填////////////////////
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

        CertApplyResponse resp;
        try {
            resp = sdn.certApply(wsUrl, "certProcessManage",
                    "RSA", "APPLY", certApplyRequest, "beian.pfx", "1234");
        } catch (Exception e) {e.printStackTrace();
//            LogUtils.writeLogFile(logPath, "certApply","certApply方法异常", e.toString());
            throw new BusinessException(3011, e.getMessage());
        }

        String applyId = "";
        //证书申请响应
        if ("0".equals(resp.getResultCode())) {
            //证书申请成功
            System.out.println("证书申请成功!");
            List<ApplyResult> applyResult = resp.getList();
            if (applyResult != null && applyResult.size() > 0) {
                for (ApplyResult apply : applyResult) {
                    applyId = apply.getApplyId();
//                    System.out.println("applyId:" + apply.getApplyId() + ";applyFlag:" + apply.getApplyFlag() + ";certdn:" + apply.getSubject() + ";refId:" + apply.getRefId());
                }
            }
        } else {
            //证书申请失败
//            LogUtils.writeLogFile(logPath,"certApply","返回状态码:"+resp.getResultCode(),resp.getResultDesc());
            throw new BusinessException(3012, resp.getResultDesc());
        }

        return applyId;
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
            throw new BusinessException(3022, e.getMessage());
        }

        //上传图片数据响应
        if (!"0".equals(resp.getResultCode()))
        {
            //上传图片数据失败
//            LogUtils.writeLogFile(logPath,"imageAppUoload","返回状态码:"+resp.getResultCode(),resp.getResultDesc());
            throw new BusinessException(3023,resp.getResultDesc());
        }
    }


    public static void applySelect(String applyId, String logPath, EfpCaMaterialMapper efpCaMaterialMapper, String NsrDabId){
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

        CertApplyQueryByIdResponse resp = null;
        try {
            resp = sdn.certApplyQueryById(wsUrl, "certProcessManage",
                    "RSA", "QAPPLYBYID", certApplyQueryByIdRequest, "beian.pfx", "1234");
        } catch (Exception e) {e.printStackTrace();
//            LogUtils.writeLogFile(logPath, "applySelect","certApplyQueryById", e.toString());
            throw new BusinessException(3011, e.getMessage());
        }

        //业务信息响应
        if ("0".equals(resp.getResultCode())) {
            //业务信息查询成功
            System.out.println("**************** resultCode ： " + resp.getResultCode() + "; resultDesc : " + resp.getResultDesc());
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
                    EfpCaMaterial efpCaMaterial = new EfpCaMaterial();
                    efpCaMaterial.setReason(applyAuditInfo.getAuditRemark());
                    efpCaMaterial.setId(NsrDabId);
                    efpCaMaterialMapper.updateByPrimaryKeySelective(efpCaMaterial);
//                    System.out.println("****************auditRemark : " + applyAuditInfo.getAuditRemark());
                    //getBusiStatus 业务单状态，“SUCCESS”和“ARCHIVE”表示已下载证书；“DISABLE”表示业务单已作废；“AUDITFAIL”表示业务单审核驳回
//                    LogUtils.writeLogFile(logPath,"applyId :"+certInfo.getApplyId(),"busiStatus :"+certInfo.getBusiStatus(),
//                            "auditRemark :"+applyAuditInfo.getAuditRemark());
                }
            }
        } else {   //业务信息查询失败
//            LogUtils.writeLogFile(logPath,"certApply","返回状态码:"+resp.getResultCode(),resp.getResultDesc());
            throw new BusinessException(3012, resp.getResultDesc());
        }

    }

}
