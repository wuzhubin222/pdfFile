package test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.com.szca.bss.toolkit.client.base.ApplyInfo;
import cn.com.szca.bss.toolkit.client.base.ApplyResult;
import cn.com.szca.bss.toolkit.client.base.AuthInfo;
import cn.com.szca.bss.toolkit.client.base.BusinessInfo;
import cn.com.szca.bss.toolkit.client.base.CertApplyInfo;
import cn.com.szca.bss.toolkit.client.base.Company;
import cn.com.szca.bss.toolkit.client.base.SelfExtRes;
import cn.com.szca.bss.toolkit.client.manage.ServiceWspoolDone;
import cn.com.szca.bss.toolkit.client.request.CertApplyRequest;
import cn.com.szca.bss.toolkit.client.response.CertApplyResponse;
import entity.ConResult;
import excption.BusinessException;
import utils.LogUtils;

public class ttd_testCertApply {

    public static void main(String[] args) throws Exception {
        //设置授权信息
        AuthInfo authInfo = new AuthInfo();
        authInfo.setProjectId("705");              //项目编号，链融科技:705
        authInfo.setLocationId("269");             //受理点ID
        authInfo.setIp("");                           //IP置空
        authInfo.setCertsn("5F64183039B45A8D");    //证书序列号,测试环境
        //authInfo.setCertsn("");                     //证书序列号，生产环境
        authInfo.setAuthCode("EFKEAKFafjkadf123");//授权码

        //设置证书申请信息
        CertApplyInfo certApplyInfo = new CertApplyInfo();
        certApplyInfo.setCertType("ORGAN");       //证书申请类型(机构证书)
        //主题信息 ///////////////////////////////
        certApplyInfo.setCertdn("CN=深圳市希吉信息技术有限公司,OU=91440300349714137M,O=深圳市希吉信息技术有限公司,L=深圳市,ST=广东省,C=CN");
        certApplyInfo.setApplyValidate(365);      //证书申请服务期限
        certApplyInfo.setCertValidate(365);       //证书申请有效期
        certApplyInfo.setEmail("");                 //电邮地址

        //设置自定义扩展域信息
        List<SelfExtRes> selfList = new ArrayList<SelfExtRes>();

        SelfExtRes selfExtRes1 = new SelfExtRes();
        selfExtRes1.setExtId("1.2.86.11.7.7550205");// 固定值
        selfExtRes1.setExtName("深圳CA扩展标识");      // 固定值
        selfExtRes1.setNullAble("N");                  // 固定值
        selfExtRes1.setExtValue("91440300349714137M");                   // 由合作商定义/////////////////////////////
        selfList.add(selfExtRes1);

        SelfExtRes selfExtRes2 = new SelfExtRes();
        selfExtRes2.setExtId("1.2.86.11.7.7550222");// 固定值
        selfExtRes2.setExtName("身份证号");            // 固定值
        selfExtRes2.setNullAble("N");                  // 固定值
        selfExtRes2.setExtValue("441581199108102025"); // 客户身份证号///////////////////////////jbr_dm
        selfList.add(selfExtRes2);

        SelfExtRes selfExtRes3 = new SelfExtRes();
        selfExtRes3.setExtId("1.2.86.11.7.7550224");// 固定值
        selfExtRes3.setExtName("项目类型");            // 固定值
        selfExtRes3.setNullAble("N");                  // 固定值
        selfExtRes3.setExtValue("BJZS");               //由合作商定义
        selfList.add(selfExtRes3);

        SelfExtRes selfExtRes4 = new SelfExtRes();
        selfExtRes4.setExtId("1.2.86.11.7.755024301");// 固定值
        selfExtRes4.setExtName("统一社会信用代码");       // 固定值
        selfExtRes4.setNullAble("N");                    // 固定值
        selfExtRes4.setExtValue("91440300349714137M");                     //由合作商定义///////////////////////
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
        company.setCustName("深圳市希吉信息技术有限公司"); //单位名称，必填//////////
        company.setIdType("JJ");         //单位证件类型，组织机构代码证，必填
        company.setIdNo("1700012");     //组织机构代码，必填
        company.setRegisterAddr("深圳市福田区香蜜湖街道侨香路香蜜时代A栋17L");//注册地址，必填///////////////
        company.setContactName("卢若冰");                   //联系人，必填//////////////////////
        company.setContactPhoneNo("18506655676");      //联系电话号码，必填///////////////////////////
        company.setContactAddr("深圳市福田区香蜜湖街道侨香路香蜜时代A栋17L");//联系地址，必填///////////////////////////
        company.setZipCode("518053");         //邮政编码，必填/////////////////////
        company.setCity("深圳市");              //城市，必填/////////////////
        company.setProvince("广东省");         //省份，必填///////////////////////
        company.setAgentName("卢若冰");          //经办人姓名，必填////////////////
        company.setAgentIdType("SF");         //经办人证件类型，身份证，必填
        company.setAgentIdNo("441581199108102025"); //经办人身份证号码，必填////////////////
        company.setAgentMobileNo("18506655676");//经办人手机号码，必填
        company.setAgentPhoneNo("");//经办人电话号码
        company.setAgentFaxNo("");  //经办人传真号码
        company.setDepartment("");          //经办人所属部门
        company.setPosition("");           //经办人职务
        company.setAgentAddress("深圳市福田区香蜜湖街道侨香路香蜜时代A栋17L");//经办人联系地址，必填////////////////////
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
        //测试环境内网访问地址
//		String wsUrl = "http://192.168.100.208:8080/wspool3/service/certProcessManage?wsdl";
        //测试环境外网访问地址
        String wsUrl = "http://218.17.161.11:7083/wspool3/service/certProcessManage?wsdl";

        CertApplyResponse resp = sdn.certApply(wsUrl, "certProcessManage",
                "RSA", "APPLY", certApplyRequest, "beian.pfx", "1234");

        //证书申请响应
        if ("0".equals(resp.getResultCode())) {
            //证书申请成功
            System.out.println("证书申请成功!");
            List<ApplyResult> applyResult = resp.getList();
            if (applyResult != null && applyResult.size() > 0) {
                for (ApplyResult apply : applyResult) {
                    System.out.println("applyId:" + apply.getApplyId() + ";applyFlag:" + apply.getApplyFlag() + ";certdn:" + apply.getSubject() + ";refId:" + apply.getRefId());
                }
            }
        } else {
            //证书申请失败
            System.out.println("证书申请失败，失败原因:" + resp.getResultDesc());
        }
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
            throw new BusinessException(3011, "资料上传失败");
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
            throw new BusinessException(3012, "资料上传失败");
        }

        return applyId;
    }

}
