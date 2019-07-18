package test;

import java.util.ArrayList;
import java.util.List;

import Dao.EfpCaMaterialMapper;
import cn.com.szca.bss.toolkit.client.base.AllInfo;
import cn.com.szca.bss.toolkit.client.base.ApplyAuditInfo;
import cn.com.szca.bss.toolkit.client.base.AuthInfo;
import cn.com.szca.bss.toolkit.client.base.BusinessInfo;
import cn.com.szca.bss.toolkit.client.base.CertInfo;
import cn.com.szca.bss.toolkit.client.manage.ServiceWspoolDone;
import cn.com.szca.bss.toolkit.client.request.CertApplyQueryByIdRequest;
import cn.com.szca.bss.toolkit.client.response.CertApplyQueryByIdResponse;
import excption.BusinessException;
import model.EfpCaMaterial;
import utils.LogUtils;

public class ttd_testApplySelect {
    public static void main(String[] args) throws Exception {
        //设置授权信息
        AuthInfo authInfo = new AuthInfo();
        authInfo.setProjectId("705");            //项目编号
        authInfo.setLocationId("269");          //受理点ID
        authInfo.setIp("");                        //IP，置空 
        authInfo.setCertsn("5F64183039B45A8D");//证书序列号，测试环境适用
        //authInfo.setCertsn("");                 //证书序列号，生产环境适用
        authInfo.setAuthCode("EFKEAKFafjkadf123");//授权码

        CertApplyQueryByIdRequest certApplyQueryByIdRequest = new CertApplyQueryByIdRequest();
        certApplyQueryByIdRequest.setAuthInfo(authInfo);

        //构建业务单信息查询请求类
        List<String> applyIdLi = new ArrayList<String>();
        applyIdLi.add("20171000001959");               //设置业务单号///////////////////////////////
        certApplyQueryByIdRequest.setList(applyIdLi);

        //业务单信息查询
        ServiceWspoolDone sdn = new ServiceWspoolDone();
        //测试环境内网访问地址
//        String wsUrl = "http://192.168.100.208:8080/wspool3/service/certProcessManage?wsdl";
        //测试环境外网访问地址
		String wsUrl = "http://218.17.161.11:7083/wspool3/service/certProcessManage?wsdl";

        CertApplyQueryByIdResponse resp = sdn.certApplyQueryById(wsUrl, "certProcessManage",
                "RSA", "QAPPLYBYID", certApplyQueryByIdRequest, "beian.pfx", "1234");

        //业务信息响应
        if ("0".equals(resp.getResultCode())) {
            //业务信息查询成功
            System.out.println("**************** resultCode ： " + resp.getResultCode() + "; resultDesc : " + resp.getResultDesc());
            List<AllInfo> li = resp.getList();
            if (null != li && li.size() > 0) {
                for (int i = 0; i < li.size(); i++) {
                    AllInfo allInfo = li.get(i);
                    CertInfo certInfo = allInfo.getCertInfo();
                    BusinessInfo businessInfo = allInfo.getBusinessInfo();

                    System.out.println("****************applyId : " + certInfo.getApplyId()); //业务单号
                    System.out.println("****************busiStatus : " + certInfo.getBusiStatus());//业务单状态，“SUCCESS”和“ARCHIVE”表示已下载证书；“DISABLE”表示业务单已作废；“AUDITFAIL”表示业务单审核驳回；
                    // 审核不通过理由（关注后重新申请）
                    ApplyAuditInfo applyAuditInfo = allInfo.getAuditInfo();
                    System.out.println("****************auditRemark : " + applyAuditInfo.getAuditRemark());
                }
            }
        } else {   //业务信息查询失败
            System.out.println("业务信息查询失败，失败原因:" + resp.getResultDesc());
        }
    }

    public static void applySelect(String applyId, String logPath, EfpCaMaterialMapper efpCaMaterialMapper,String NsrDabId){
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
            throw new BusinessException(3011, "业务查询失败");
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
            throw new BusinessException(3012, "业务查询失败");
        }

    }


}
