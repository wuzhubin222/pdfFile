package controller;

import Dao.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import constant.Constant;
import entity.ConResult;
import excption.BusinessException;
import Dao.CertUserMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import model.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import szca.SzcaService;
import utils.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Scope("prototype")
@Controller
public class SzcaController extends BaseController {

    @Autowired
    private EfpCaMaterialMapper efpCaMaterialMapper;


    @Autowired
    private EfpNsrDabMapper efpNsrDabMapper;

    @Autowired
    private CityCodeMapper cityCodeMapper;

    @Autowired
    private EfpCzyDabMapper efpCzyDabMapper;

    @Autowired
    private EfpCaMapper efpCaMapper;

    @Autowired
    private CertUserMapper certUserMapper;
    @Autowired
    private CertApplyDataMapper certApplyDataMapper;

    @Autowired
    private CertUploadDataMapper certUploadDataMapper;

    /*
       正式环境
        */
    private static final String URL = "http://wxgz1.bjzsxx.com/cert/";

    /*
    测试环境
     */
//  private static final String URL = "http://localhost:8070/";
//   private static final String CAIMGPATH = "caTest/1/1";
//   private static final String CERT_UPLOAD = "cert_upload_test";
//#CA证书上传

    @ApiOperation(value = "上传ca图片", notes = "企业识别号和图片")
    @ResponseBody
    @RequestMapping(value = "pictureCaCertificate", produces =
            "application/json;charset=utf-8", method = RequestMethod.POST)
    /**
     * @Description: pictureCaCertificate
     * @Param: [response, fileRes, nsrsbh, fileType, applyId]
     * @return: entity.ConResult
     * @Author: 帅帅
     * @Date: 2018/3/2
     */
    public ConResult pictureCaCertificate(HttpServletResponse response,
                                          MultipartFile fileRes,
                                          @ApiParam("nsrsbh,必填,纳税人识别号") @RequestParam(value = "nsrsbh") String nsrsbh,
                                          @ApiParam("fileType,必填,证件照的类型") @RequestParam(value = "fileType") Integer fileType
    ) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if (fileRes.getSize() < 5) {
            throw new BusinessException(1002, "图片太小，请重新上传");
        }
        if (fileRes.getSize() / 1024 > 300) {
            throw new BusinessException(1022, "图片太大，请压缩后再上传");
        }


        CertApplyData certApplyData = new CertApplyData();
        certApplyData.setEtpTaxId(nsrsbh);
        List<CertApplyData> certApplyDatas = certApplyDataMapper.selectSelective(certApplyData);
        if (certApplyDatas.size() != 1) {
            throw new BusinessException(3020, "该企业未申请ca");

        }
        if (certApplyDatas.get(0).getStatus().equalsIgnoreCase("SUCCESS") || certApplyDatas.get(0).getStatus().equalsIgnoreCase("FINISH")) {
            throw new BusinessException(3020, "当前状态不允许修改图片");
        }
        String applyId = certApplyDatas.get(0).getApplyId();
        CertUser certUser = new CertUser();
        certUser.setNsrsbh(nsrsbh);
        List<CertUser> certUsers = certUserMapper.selectSelective(certUser);
        String appId = certUsers.get(0).getAppId();
        String timeStamp = System.currentTimeMillis() + "";
        String sign = EDcodeUtils.getMD5(appId + certUsers.get(0).getAppSecret()
                + timeStamp);

        String fileName = fileRes.getOriginalFilename();
        File fileBody = null;
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        try {
            // 获取文件后缀
            fileBody = File.createTempFile(UUID.randomUUID().toString().replaceAll("-", ""), prefix);
            fileRes.transferTo(fileBody);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(3022, e.toString());
        }
//appId, sign, timeStamp, fileRes, fileType, applyId
        String url1 = URL + "ca/imgUpload";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url1);
        CloseableHttpResponse response1 = null;
        try {
            HttpEntity httpEntity = MultipartEntityBuilder.create()
                    .addPart("fileRes", new FileBody(fileBody))
                    .addPart("fileType", new StringBody(fileType.toString(), ContentType
                            .create("application/x-www-form-urlencoded", "utf-8")))
                    .addPart("appId", new StringBody(appId, ContentType.create
                            ("application/x-www-form-urlencoded", "utf-8")))
                    .addPart("sign", new StringBody(sign, ContentType.create("application/x-www-form-urlencoded", "utf-8")))
                    .addPart("timeStamp", new StringBody(timeStamp, ContentType.create("application/x-www-form-urlencoded", "utf-8")))
                    .addPart("applyId", new StringBody(applyId, ContentType.create
                            ("application/x-www-form-urlencoded", "utf-8")))
                    .build();
            httpPost.setEntity(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(3023, e.toString());
        }
        try {
            response1 = httpclient.execute(httpPost);
            HttpEntity resEntity = response1.getEntity();
            if (resEntity != null) {
                System.out.println("Response content length: " + resEntity.getContentLength());
                String postRequest = EntityUtils.toString(resEntity);
                EntityUtils.consume(resEntity);
                if (postRequest == null || postRequest.trim().length() <= 2) {
                    throw new BusinessException(3021, "请求服务器错误");
                }
                JSONObject jsonObject = JSON.parseObject(postRequest);
                if (jsonObject.getInteger("errcode") == 0) {

                    if (fileType == 1) {
                        fileName = "sqb" + prefix;
                    } else if (fileType == 2) {
                        fileName = "sqxy" + prefix;
                    } else if (fileType == 3) {
                        fileName = "sfz" + prefix;
                    } else {
                        fileName = "yyzz" + prefix;
                    }
                    String url2 = Constant.PDFFILEURL + Constant.CERT_UPLOAD + "/" + nsrsbh + "/" + fileName;
                    ConResult conResult = new ConResult();
                    conResult.getMap().put("url", url2);

                    return conResult;
                } else {
                    throw new BusinessException(jsonObject.getInteger("errcode"), jsonObject.getString("errmsg"));
                }
            } else {
                throw new BusinessException(3021, "请求服务器错误");
            }

        } finally {
            response1.close();
            httpclient.close();
            if (fileBody.exists()) {
                fileBody.delete();
            }
        }
    }


    @ApiOperation(value = "ca账号申请", notes = "appId")
    @ResponseBody
    @RequestMapping(value = "logon", produces =
            "application/json;charset=utf-8", method = RequestMethod.GET)
    public ConResult logon(HttpServletResponse response,
                           @ApiParam("nsrsbh,纳税人识别号(必填)") @RequestParam(value = "nsrsbh") String nsrsbh

    ) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String log = logFilePath + "log/ca.txt";
        EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(nsrsbh);
        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if (efpNsrDabs.size() == 0) {
            throw new BusinessException(3020, "纳税人表数据不存在");
        }
        CertUser certUser = new CertUser();
        certUser.setNsrsbh(nsrsbh);
        List<CertUser> certUsers = certUserMapper.selectSelective(certUser);
        if (certUsers.size() == 0) {
            String appId = "cert" + System.currentTimeMillis();
            String appSecret = EDcodeUtils.getMD5(System.currentTimeMillis() + "");
            certUser.setAppId(appId);
            certUser.setAppSecret(appSecret);
            certUser.setType(0);
            int num = certUserMapper.insertSelective(certUser);
            if (num != 1) {
                throw new BusinessException(3011, "账号申请错误");
            }
            return new ConResult();
        } else {
            return new ConResult();
        }
    }


    @ApiOperation(value = "ca证书申请", notes = "获得applyId")
    @ResponseBody
    @RequestMapping(value = "caMaterialUploads", produces =
            "application/json;charset=utf-8", method = RequestMethod.POST)
    /**
     * @Description: caMaterialUpload
     * @Param: [response, nsrsbh, parm5, parm6]
     * @return: entity.ConResult
     * @Author: 帅帅
     * @Date: 2018/3/2
     */
    public ConResult caMaterialUploads(HttpServletResponse response,
                                       @ApiParam("nsrsbh,纳税人识别号(必填)") @RequestParam(value = "nsrsbh") String nsrsbh,
                                       @ApiParam("parm5,身份证(必填)") @RequestParam
                                               (value = "parm5") String parm5,
                                       @ApiParam("parm6,身份证号") @RequestParam
                                               (value = "parm6") String parm6

    ) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String log = logFilePath + "log/ca.txt";
        CertUser certUser = new CertUser();
        certUser.setNsrsbh(nsrsbh);
        List<CertUser> certUsers = certUserMapper.selectSelective(certUser);
        String appId = certUsers.get(0).getAppId();
        String timeStamp = System.currentTimeMillis() + "";
        String sign = EDcodeUtils.getMD5(appId + certUsers.get(0).getAppSecret()
                + timeStamp);
        EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(nsrsbh);
        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if (efpNsrDabs.size() == 0) {
            throw new BusinessException(3020, "纳税人表数据不存在");
        }
        String etpName = efpNsrDabs.get(0).getNsrmc();
        String efpLegalName = efpNsrDabs.get(0).getFrmc();
        String etpAddr = efpNsrDabs.get(0).getLxdz();
        String etpPhone = efpNsrDabs.get(0).getLxdh();
        CityCode cityCode = new CityCode();
        cityCode.setCode(efpNsrDabs.get(0).getProvinceCode());
        String etpProvince = cityCodeMapper.selectSelective(cityCode).get(0).getCity();
        cityCode.setCode(efpNsrDabs.get(0).getCityCode());
        String etpCity = cityCodeMapper.selectSelective(cityCode).get(0).getCity();
        String etpTaxId = nsrsbh;
        EfpCzyDab efpCzyDab = new EfpCzyDab();
        efpCzyDab.setNsrsbh(nsrsbh);
        efpCzyDab.setType(0);
        efpCzyDab.setStatus(1);
        efpCzyDab = efpCzyDabMapper.selectSelective(efpCzyDab).get(0);
        String agentPhone = efpCzyDab.getCzyId();
        String agentName = parm5;
        String agentIdCard = parm6;
        //    appId, sign, timeStamp, etpName, efpLegalName, etpAddr, etpPhone,etpProvince, etpCity, etpTaxId, agentName, agentPhone, agentIdCard
        String url1 = URL + "ca/apply";
        String data = "appId=" + appId + "&sign=" + sign + "&timeStamp=" + timeStamp + "&etpName=" + etpName
                + "&efpLegalName=" + efpLegalName + "&etpAddr=" + etpAddr + "&etpPhone=" + etpPhone
                + "&etpProvince=" + etpProvince + "&etpCity=" + etpCity + "&etpTaxId=" + etpTaxId
                + "&agentName=" + agentName + "&agentPhone=" + agentPhone + "&agentIdCard=" + agentIdCard;
        String postRequest = HttpRequestUtils.postRequest(url1, data);
        if (postRequest == null || postRequest.trim().length() == 0) {
            throw new BusinessException(3021, "请求服务器错误");
        }
        JSONObject jsonObject = JSON.parseObject(postRequest);
        ConResult conResult = new ConResult();

        if (jsonObject.getInteger("errcode") == 0) {
            conResult.getMap().put("applyId", jsonObject.getString("applyId"));
            //将身份证信息加入nsr表
            EfpCzyDab efpCzyDab1 = new EfpCzyDab();
            efpCzyDab1.setId(efpCzyDab.getId());
            efpCzyDab1.setParm5(parm5);
            efpCzyDab1.setParm6(parm6);
            int num = efpCzyDabMapper.updateByPrimaryKeySelective(efpCzyDab1);
            if (num != 1) {
                throw new BusinessException(3022, "添加身份证信息失败");
            }
            return conResult;
        } else {
            throw new BusinessException(jsonObject.getInteger("errcode"), jsonObject.getString("errmsg"));
        }
    }


    @ApiOperation(value = "从深圳ca下载ca证书到文件服务器", notes = "下载ca文件")
    @ResponseBody
    @RequestMapping(value = "caDowns", produces = "application/json;" +
            "charset=utf-8", method = RequestMethod.POST)
    public ConResult caDowns(
            HttpServletResponse response,
            @RequestParam(value = "nsrsbh") String nsrsbh) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        CertApplyData certApplyData = new CertApplyData();
        certApplyData.setEtpTaxId(nsrsbh);
        List<CertApplyData> certApplyDatas = certApplyDataMapper.selectSelective(certApplyData);
        if (certApplyDatas.size() != 1) {
            throw new BusinessException(3020, "该企业未申请ca");
        }
        String applyId = certApplyDatas.get(0).getApplyId();
        CertUploadData certUploadData = new CertUploadData();
        certUploadData.setEtpTaxId(nsrsbh);
        List<CertUploadData> certUploadDatas = certUploadDataMapper
                .selectSelective(certUploadData);
        if (certUploadDatas.size() != 1) {
            throw new BusinessException(3020, "该企业未申请ca");
        }
        CertUser certUser = new CertUser();
        certUser.setNsrsbh(nsrsbh);
        List<CertUser> certUsers = certUserMapper.selectSelective(certUser);
        String appId = certUsers.get(0).getAppId();
        String timeStamp = System.currentTimeMillis() + "";
        String sign = EDcodeUtils.getMD5(appId + certUsers.get(0).getAppSecret()
                + timeStamp);
//向深圳ca查询证书审核情况
        String url1 = URL + "ca/query";
        String data1 = "appId=" + appId + "&sign=" + sign + "&timeStamp=" +
                timeStamp + "&applyId=" + applyId;
        String postRequest1 = HttpRequestUtils.postRequest(url1, data1);
        if (postRequest1 == null || postRequest1.trim().length() == 0) {
            throw new BusinessException(3021, "申请错误");
        }
        JSONObject jsonObject1 = JSON.parseObject(postRequest1);
        if (jsonObject1.getInteger("errcode") == 0) {

            if (jsonObject1.getString("status").equalsIgnoreCase("SUCCESS")) {
                //下载图片
                //从深圳ca下载证书至项目相应目录，并返回证书密码
                String etpTaxId = nsrsbh;
                String url2 = URL + "ca/down";
                String data2 = "appId=" + appId + "&sign=" + sign + "&timeStamp=" + timeStamp + "&etpTaxId=" + etpTaxId;
                String postRequest2 = HttpRequestUtils.postRequest(url2, data2);
                if (postRequest2 == null || postRequest2.trim().length() == 0) {
                    throw new BusinessException(3021, "请求服务器错误");
                }
                JSONObject jsonObject2 = JSON.parseObject(postRequest2);
                if (jsonObject2.getInteger("errcode") == 0) {
                    String password = jsonObject2.getString("password");
                    String certBase64 = jsonObject2.getString("certBase64");
                    try {
                        password = CaUtils.downCaBynsrsbh(null, nsrsbh, this.getClass().getResource("/").getPath() +
                                "downFile/" + nsrsbh + ".pfx");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
/**
 * 将证书上传至文件服务器相应的目录，并且将密码存储efpca至表相应字段
 */
                    try {
//
                        InputStream inputStream = new FileInputStream(this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx");
                        FileStorageUtils.uploadFile(Constant.CAIMGPATH, nsrsbh + ".pfx", inputStream);
                        EfpCa efpCa = new EfpCa();
                        efpCa.setNsrsbh(nsrsbh);
                        List<EfpCa> efpCas = efpCaMapper.selectSelective(efpCa);
                        if (efpCas.size() == 1) {
                            String id = efpCas.get(0).getId();
                            if (efpCaMapper.deleteByPrimaryKey(id) != 1) {
                                throw new BusinessException(3504, "下载失败");
                            }
                        }
                        efpCa.setUpdateTime(new Date());
                        efpCa.setCaPath(Constant.CAIMGPATH);
                        efpCa.setCaImgPath(Constant.CAIMGPATH);
                        efpCa.setCaPassword(password);
                        efpCa.setTemplateName("深圳");
                        if (efpCaMapper.insertSelective(efpCa) != 1) {
                            throw new BusinessException(3505, "ca表添加失败");
                        }
                        certApplyData = new CertApplyData();
                        certApplyData.setId(certApplyDatas.get(0).getId());
                        certApplyData.setStatus("FINISH");
                        int num = certApplyDataMapper
                                .updateByPrimaryKeySelective(certApplyData);
                        if (num != 1) {
                            throw new BusinessException(3506, "apply表添加失败");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new BusinessException(3503, "下载失败");
                    }
                    return new ConResult();
                } else {
                    throw new BusinessException(jsonObject2.getInteger("errcode"), jsonObject2.getString("errmsg"));
                }
            } else {
                throw new BusinessException(3504, "未通过审核");
            }

        } else {
            throw new BusinessException(jsonObject1.getInteger("errcode"), jsonObject1.getString("errmsg"));
        }
    }


    //查询深圳ca的证书审核状态，并将结果写入日志
    @ApiOperation(value = "查询深圳ca的证书审核状态", notes = "查询ca证书审核状态")
    @ResponseBody
    @RequestMapping(value = "caQuery", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    public ConResult caQuery(
            HttpServletResponse response,
            @RequestParam(value = "nsrsbh") String nsrsbh
    ) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        CertApplyData certApplyData = new CertApplyData();
        certApplyData.setEtpTaxId(nsrsbh);
        List<CertApplyData> certApplyDatas = certApplyDataMapper.selectSelective(certApplyData);
        if (certApplyDatas.size() != 1) {
            throw new BusinessException(3020, "该企业未申请ca");
        }
        String sealCode = certApplyDatas.get(0).getSealCode();
        String financeCode = certApplyDatas.get(0).getFinanceCode();
        Integer statusZs = certApplyDatas.get(0).getStatusZs();
        if (statusZs == 1) {
            throw new BusinessException(3019, "该企业未审核");
        } else if (statusZs == 2) {
            throw new BusinessException(3019, certApplyDatas.get(0).getErrMsg());
        }
        String applyId = certApplyDatas.get(0).getApplyId();
        CertUploadData certUploadData = new CertUploadData();
        certUploadData.setEtpTaxId(nsrsbh);
        ConResult conResult = new ConResult();
        List<CertUploadData> certUploadDatas = certUploadDataMapper
                .selectSelective(certUploadData);
        if (certUploadDatas.size() != 1) {
            throw new BusinessException(3020, "该企业未申请ca");
        }

        conResult.getMap().put("financeSealUrl", certUploadDatas.get(0).getFinanceSealUrl());

        CertUser certUser = new CertUser();
        certUser.setNsrsbh(nsrsbh);
        List<CertUser> certUsers = certUserMapper.selectSelective(certUser);
        String appId = certUsers.get(0).getAppId();
        String timeStamp = System.currentTimeMillis() + "";
        String sign = EDcodeUtils.getMD5(appId + certUsers.get(0).getAppSecret()
                + timeStamp);
        String url1 = URL + "ca/query";
        String data = "appId=" + appId + "&sign=" + sign + "&timeStamp=" + timeStamp + "&applyId=" + applyId;
        String postRequest = HttpRequestUtils.postRequest(url1, data);
        if (postRequest == null || postRequest.trim().length() == 0) {
            throw new BusinessException(3021, "请求服务器错误");
        }
        JSONObject jsonObject = JSON.parseObject(postRequest);
        System.out.println(jsonObject);
        if (jsonObject.getInteger("errcode") == 0) {

            conResult.getMap().put("jsonObject", jsonObject);
            EfpNsrDab efpNsrDab = new EfpNsrDab();
            efpNsrDab.setNsrsbh(nsrsbh);
            List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
            if (efpNsrDabs.size() == 0) {
                throw new BusinessException(3020, "纳税人表数据不存在");
            }
            EfpCzyDab efpCzyDab = new EfpCzyDab();
            efpCzyDab.setNsrsbh(nsrsbh);
            efpCzyDab.setType(0);
            efpCzyDab.setStatus(1);
            List<EfpCzyDab> efpCzyDabs = efpCzyDabMapper.selectSelective(efpCzyDab);
            if (efpCzyDabs.size() == 0) {
                throw new BusinessException(3020, "操作员表数据不存在");
            }
            EfpCa efpCa = new EfpCa();
            efpCa.setNsrsbh(nsrsbh);
            List<EfpCa> efpCas = efpCaMapper.selectSelective(efpCa);
            if (efpCas.size() != 0) {
                String caImgPath = efpCas.get(0).getCaImgPath();
                if (caImgPath != null && !caImgPath.equals("")) {
                    caImgPath = Constant.PDFFILEURL + caImgPath + "/" + nsrsbh + ".png";
                    conResult.getMap().put("caImgPath", caImgPath);
                }
            }
            conResult.getMap().put("nsrUsers", efpNsrDabs.get(0));
            conResult.getMap().put("czyUsers", efpCzyDabs.get(0));
            conResult.getMap().put("sealCode", sealCode);
            conResult.getMap().put("financeCode", financeCode);
            return conResult;
        } else {
            throw new BusinessException(jsonObject.getInteger("errcode"), jsonObject.getString("errmsg"));
        }
    }

    @ApiOperation(value = "按条件分页查询查询ca列表", notes = "查询申请ca的")
    @ResponseBody
    @RequestMapping(value = "selectMaterialLimit", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    public ConResult selectMaterialLimit(
            HttpServletResponse response,
            @ApiParam("nsrsbh,纳税人识别号(可为空)") @RequestParam(value = "nsrsbh") String nsrsbh,
            @ApiParam("nsrmc,公司名称(可为空)") @RequestParam(value = "nsrmc") String nsrmc,
            @ApiParam("status,状态(必填)") @RequestParam(value = "status") String status,
            @ApiParam("pageNo,当前页(必填)") @RequestParam(value = "pageNo") Integer pageNo,
            @ApiParam("pageSize,页面大小(必填)") @RequestParam(value = "pageSize") Integer pageSize
    ) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        CertApplyData certApplyData = new CertApplyData();
        if (nsrsbh != null && nsrsbh.trim().length() != 0) {
            certApplyData.setEtpTaxId(nsrsbh);
        }
        if (nsrmc != null && nsrmc.trim().length() != 0) {
            certApplyData.setEtpName(nsrmc);
        }

        //  “AUDIT”表示待审核“HANDLE”表示审核中“SUCCESS”表示审核通过
        // “FINISH”申请成功“DISABLE”表示申请作废“AUDITFAIL
        // ”表示审核失败"ALL"是全部

        if (!status.equalsIgnoreCase("ALL")) {
            certApplyData.setStatus(status);
        }
        int count = certApplyDataMapper.selectSelectivesCount(certApplyData);
        if (count == 0) {
            throw new BusinessException(3031, "查询无记录");
        }
        Integer pageOff = (pageNo - 1) * pageSize;
        certApplyData.setPageOff(pageOff);
        certApplyData.setPageSize(pageSize);
        List<HashMap<String, Object>> certApplyDatas = certApplyDataMapper.selectSelectives(certApplyData);
        for (int i = 0; i < certApplyDatas.size(); i++) {
            EfpCa efpCa = new EfpCa();
            efpCa.setNsrsbh((String) certApplyDatas.get(i).get("etp_tax_id"));
            List<EfpCa> cas = efpCaMapper.selectSelective(efpCa);
            String templateName = null;
            if (cas.size() > 0) {
                templateName = cas.get(0).getTemplateName();
            }
            certApplyDatas.get(i).put("templateName", templateName);

            CertUploadData certUploadData = new CertUploadData();
            certUploadData.setEtpTaxId((String) certApplyDatas.get(i).get("etp_tax_id"));
            List<CertUploadData> certUploadDatas = certUploadDataMapper
                    .selectSelective(certUploadData);
            if (certUploadDatas.size() > 0) {
                certApplyDatas.get(i).put("certUploadData", certUploadDatas.get(0));
            }
        }
        ConResult conResult = new ConResult();
        conResult.getMap().put("certApplyDatas", certApplyDatas);
        //  int size = (int) Math.ceil(count*1.0/pageSize);
        conResult.getMap().put("count", count);

        return conResult;
    }


    @ApiOperation(value = "查询ca详情", notes = "")
    @ResponseBody
    @RequestMapping(value = "selectMaterialExamines", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    public ConResult selectMaterialExamines(
            HttpServletResponse response,
            @ApiParam("nsrsbh,纳税人识别号(不为空)") @RequestParam(value = "nsrsbh") String nsrsbh
    ) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(nsrsbh);
        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if (efpNsrDabs.size() == 0) {
            throw new BusinessException(3020, "纳税人表数据不存在");
        }

        EfpCzyDab efpCzyDab = new EfpCzyDab();
        efpCzyDab.setNsrsbh(nsrsbh);
        efpCzyDab.setType(0);
        efpCzyDab.setStatus(1);
        List<EfpCzyDab> efpCzyDabs = efpCzyDabMapper.selectSelective(efpCzyDab);
        if (efpCzyDabs.size() == 0) {
            throw new BusinessException(3020, "操作员表数据不存在");
        }
        ConResult conResult = new ConResult();
        conResult.getMap().put("nsrUsers", efpNsrDabs.get(0));
        conResult.getMap().put("czyUsers", efpCzyDabs.get(0));

        return conResult;
    }


    @ApiOperation(value = "修改ca模板名template", notes = "ca表的template")
    @ResponseBody
    @RequestMapping(value = "updateCaCertificateTemplate", produces = "application/json;charset=utf-8", method =
            RequestMethod.POST)
    public ConResult updateCaCertificateTemplate(
            HttpServletResponse response,
            @ApiParam("nsrsbh,纳税人识别号(不为空)") @RequestParam(value = "nsrsbh") String nsrsbh,
            @ApiParam("templateName,模板名(可为空)") @RequestParam(value = "templateName") String templateName) throws UnsupportedEncodingException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        EfpCa efpCa = new EfpCa();
        efpCa.setNsrsbh(nsrsbh);
        List<EfpCa> efpCas = efpCaMapper.selectSelective(efpCa);
        if (efpCas.size() == 0) {
            throw new BusinessException(3041, "ca证书未下载");
        }
        if (efpCas.get(0).getCaPassword() == "" || efpCas.get(0).getCaPassword().trim().length() == 0) {
            throw new BusinessException(3041, "ca证书未下载");
        }
        //  templateName = URLEncoder.encode(templateName, "utf-8");
        if (!templateName.equals("黑龙江") && !templateName.equals("内蒙古")) {
            templateName = templateName.substring(0, 2);
        }
        //已经注册,修改模板名
        efpCa.setTemplateName(templateName);
        efpCa.setId(efpCas.get(0).getId());
        int res = efpCaMapper.updateByPrimaryKeySelective(efpCa);
        if (res != 1) {
            throw new BusinessException(3042, "修改模板失败");
        }
        return new ConResult();
    }


    @ApiOperation(value = "上传印章图片", notes = "ca表的ca_img_path")
    @ResponseBody
    @RequestMapping(value = "pictureCaSeal", produces = "application/json;" +
            "charset=utf-8", method = RequestMethod.POST)
    public ConResult pictureCaSeal(
            HttpServletResponse response,
            MultipartFile fileRes,
            @ApiParam("nsrsbh,必填,纳税人识别号") @RequestParam(value = "nsrsbh") String nsrsbh,
            @ApiParam("fileType,必填,证件照的类型,5是发票专用章，6是财务专用章") @RequestParam(value = "fileType") Integer fileType) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if (nsrsbh == null || nsrsbh.length() == 0) {
            throw new BusinessException(3504, "税号不能为空");
        }
        // 获取文件后缀
        String fileName = fileRes.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        if (!prefix.equalsIgnoreCase(".png")) {
            throw new BusinessException(3504, "请选择PNG格式图片");
        } else {
            prefix = ".png";
        }

        //ca证书已经审核
        CertApplyData certApplyData = new CertApplyData();
        certApplyData.setEtpTaxId(nsrsbh);
        List<CertApplyData> certApplyDataList = certApplyDataMapper.selectSelective(certApplyData);
        if (certApplyDataList == null || certApplyDataList.size() != 1 ||
                !(certApplyDataList.get(0).getStatus().equalsIgnoreCase("FINISH"))) {
            throw new BusinessException(3505, "CA证书未下载");
        }

        ConResult conResult = new ConResult();
        if (fileType == 5) {
            try {
                InputStream inputStream = fileRes.getInputStream();
                FileStorageUtils.uploadFile(Constant.CAIMGPATH, nsrsbh + prefix, inputStream);
                EfpCa efpCa = new EfpCa();
                efpCa.setNsrsbh(nsrsbh);
                efpCa.setUpdateTime(new Date());
                efpCa.setCaImgPath(Constant.CAIMGPATH);
                String url1 = Constant.PDFFILEURL + Constant.CAIMGPATH + "/" + nsrsbh + prefix;
                if (efpCaMapper.updateByNsrsbh(efpCa) != 1) {
                    throw new BusinessException(3505, "印章添加失败");
                }
                conResult.getMap().put("url", url1);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(3503, "上传印章失败");
            }
        } else if (fileType == 6) {
            try {
                InputStream inputStream = fileRes.getInputStream();
                String fileNameNew = nsrsbh + "finan" + prefix;
                FileStorageUtils.uploadFile(Constant.CERT_UPLOAD + "/" + nsrsbh, fileNameNew, inputStream);
                String url1 = Constant.PDFFILEURL + Constant.CERT_UPLOAD + "/" + nsrsbh + "/" + fileNameNew;
                CertUploadData certUploadData = new CertUploadData();
                certUploadData.setEtpTaxId(nsrsbh);
                certUploadData.setFinanceSealUrl(url1);
                certUploadData.setCreateTime(new Date());
                if (certUploadDataMapper.updateByNsrsbh(certUploadData) != 1) {
                    throw new BusinessException(3505, "印章添加失败");
                }
                conResult.getMap().put("url", url1);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(3503, "上传印章失败");
            }
        } else {
            throw new BusinessException(3506, "印章类型错误");
        }
        return conResult;

    }


    /**
     * @Author: 帅帅
     * @Date: 2018/7/11
     * @Description: examineSuccess
     * @Param response
     * @Param nsrsbh
     * @Return: entity.ConResult
     */
    //查询深圳ca的证书审核状态，并将结果写入日志
    @ApiOperation(value = "审核通过", notes = "查询ca证书审核状态")
    @ResponseBody
    @RequestMapping(value = "examineSuccess", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @Transactional
    public ConResult examineSuccess(
            HttpServletResponse response,
            @RequestParam(value = "nsrsbh") String nsrsbh
    ) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String log = logFilePath + "log/examineSuccess.txt";
        CertUser certUser = new CertUser();
        certUser.setNsrsbh(nsrsbh);
        List<CertUser> certUsers = certUserMapper.selectSelective(certUser);


        String sign = null;
        if (certUsers == null || certUsers.size() == 0) {
            //新增数据

            String appId = "cert" + System.currentTimeMillis();
            String appSecret = EDcodeUtils.getMD5(System.currentTimeMillis() + "");
            String timeStamp = System.currentTimeMillis() + "";
            certUser.setAppId(appId);
            certUser.setAppSecret(appSecret);
            certUser.setType(2);
            int num = certUserMapper.insertSelective(certUser);
            if (num != 1) {
                throw new BusinessException(3011, "账号申请错误");
            }
            sign = EDcodeUtils.getMD5(appId + appSecret + timeStamp);
        } else {
            //查询数据

            String appId = certUsers.get(0).getAppId();
            String appSecret = certUsers.get(0).getAppSecret();
            String timeStamp = System.currentTimeMillis() + "";
            sign = EDcodeUtils.getMD5(appId + appSecret + timeStamp);
        }


        CertApplyData certApplyData = new CertApplyData();
        certApplyData.setEtpTaxId(nsrsbh);
        List<CertApplyData> certApplyDatas = certApplyDataMapper.selectSelective(certApplyData);

        if (certApplyDatas == null || certApplyDatas.size() == 0) {
            throw new BusinessException(3012, "Apply表无数据");
        }

        CertUploadData certUploadData = new CertUploadData();
        certUploadData.setEtpTaxId(nsrsbh);
        List<CertUploadData> certUploadDatas = certUploadDataMapper.selectSelective(certUploadData);
        if (certUploadDatas == null || certUploadDatas.size() == 0) {
            throw new BusinessException(3012, "Upload表无数据");
        }

        //下载图片

        //获取四种图片的目录
        String sfz = certUploadDatas.get(0).getSfzUrl().split("invoicefileservice.bjzsxx.com/")[1];
        String sqb = certUploadDatas.get(0).getSqbUrl().split("invoicefileservice.bjzsxx.com/")[1];
        String sqxy = certUploadDatas.get(0).getSqxyUrl().split("invoicefileservice.bjzsxx.com/")[1];
        String yyzz = certUploadDatas.get(0).getYyzzUrl().split("invoicefileservice.bjzsxx.com/")[1];


        //文件名称
        String sfzName = sfz.substring(sfz.lastIndexOf("/") + 1);
        String sqbName = sqb.substring(sqb.lastIndexOf("/") + 1);
        String sqxyName = sqxy.substring(sqxy.lastIndexOf("/") + 1);
        String yyzzName = yyzz.substring(yyzz.lastIndexOf("/") + 1);
        //下载到服务器位置
        String sfzOutFile = this.getClass().getResource("/").getPath() + "certFile/" + nsrsbh + sfzName;
        String sqbOutFile = this.getClass().getResource("/").getPath() + "certFile/" + nsrsbh + sqbName;
        String sqxyOutFile = this.getClass().getResource("/").getPath() + "certFile/" + nsrsbh + sqxyName;
        String yyzzOutFile = this.getClass().getResource("/").getPath() + "certFile/" + nsrsbh + yyzzName;

        //从文件服务器下载四种图片至项目相应目录
        try {
            FileStorageUtils.downFile(sfzOutFile,sfz);
            FileStorageUtils.downFile(sqbOutFile,sqb);
            FileStorageUtils.downFile(sqxyOutFile,sqxy);
            FileStorageUtils.downFile(yyzzOutFile,yyzz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(3012, "企业文件下载失败");
        }
        //申请apply
        //申请深圳ca，后续补充提交四种图片
        String applyId = null;
        applyId = SzcaService.certApply(certApplyDatas.get(0).getEtpName(), certApplyDatas.get(0).getEtpAddr(), nsrsbh,
                certApplyDatas.get(0).getEtpProvince(), certApplyDatas.get(0).getEtpCity(),
                certApplyDatas.get(0).getAgentName(),
                certApplyDatas.get(0).getAgentPhone(), certApplyDatas.get(0).getAgentIdCard(), certApplyDatas.get(0)
                        .getEtpLegalName(), certApplyDatas.get(0).getEtpPhone(), log);
        if (applyId == null || applyId.length() == 0) {
            throw new BusinessException(3013, "申请applyId失败");
        }
        //上传图片
        //提交四种图片至深圳ca
        SzcaService.imageAppUoload(log, sfzName, sfzOutFile, applyId);
        SzcaService.imageAppUoload(log, sqbName, sqbOutFile, applyId);
        SzcaService.imageAppUoload(log, sqxyName, sqxyOutFile, applyId);
        SzcaService.imageAppUoload(log, yyzzName, yyzzOutFile, applyId);
        //修改Upload表
        String idUpload = certUploadDatas.get(0).getId();
        certUploadData.setId(idUpload);
        certUploadData.setCreateTime(new Date());
        certUploadData.setApplyid(applyId);
        certUploadData.setErrCode("0");
        certUploadData.setErrMsg("success");

        int num1 = certUploadDataMapper.updateByPrimaryKeySelective(certUploadData);
        if (num1 != 1) {
            throw new BusinessException(3014, "Upload表修改失败");
        }
        //修改Apply表

        String idApply = certApplyDatas.get(0).getId();

        certApplyData.setId(idApply);
        certApplyData.setApplyId(applyId);
        certApplyData.setCreateTime(new Date());
        certApplyData.setErrMsg("success");
        certApplyData.setErrCode("0");
        certApplyData.setStatusZs(0);
        certApplyData.setStatus("AUDIT");

        int num2 = certApplyDataMapper.updateByPrimaryKeySelective(certApplyData);
        if (num2 != 1) {
            throw new BusinessException(3014, "Apply表修改失败");
        }
        return new ConResult();
    }

    /**
     * @Author: 帅帅
     * @Date: 2018/7/11
     * @Description: examineFail
     * @Param response
     * @Param nsrsbh
     * @Param reason
     * @Return: entity.ConResult
     */
    //查询深圳ca的证书审核状态，并将结果写入日志
    @ApiOperation(value = "审核不通过", notes = "查询ca证书审核状态")
    @ResponseBody
    @RequestMapping(value = "examineFail", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    public ConResult examineFail(
            HttpServletResponse response,
            @RequestParam(value = "nsrsbh") String nsrsbh,
            @RequestParam(value = "reason") String reason
    ) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        CertApplyData certApplyData = new CertApplyData();
        certApplyData.setEtpTaxId(nsrsbh);
        List<CertApplyData> certApplyDatas = certApplyDataMapper.selectSelective(certApplyData);

        if (certApplyDatas == null || certApplyDatas.size() == 0) {
            throw new BusinessException(3012, "Apply表无数据");
        }

        certApplyData.setId(certApplyDatas.get(0).getId());
        certApplyData.setCreateTime(new Date());
        certApplyData.setStatusZs(2);
        certApplyData.setStatus("AUDITFAIL");
        certApplyData.setErrMsg(reason);
        int num = certApplyDataMapper.updateByPrimaryKeySelective(certApplyData);
        if (num != 1) {
            throw new BusinessException(3014, "Apply表修改失败");
        }
        return new ConResult();
    }

    @ApiOperation(value = "审核不通过", notes = "查询ca证书审核状态")
    @ResponseBody
    @RequestMapping(value = "selectCertPdf", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    public ConResult selectCertPdf(
            HttpServletResponse response,
            @RequestParam(value = "nsrsbh") String nsrsbh,
            @RequestParam(value = "startTime") String startTime,
            @RequestParam(value = "endTime") String endTime
    ) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if (startTime == null || startTime.length() == 0) {
            throw new BusinessException(3020, "startTime不能为空");
        }
        if (endTime == null || endTime.length() == 0) {
            throw new BusinessException(3020, "endTime不能为空");
        }
        String url1 = URL + "ca/selectCertPdf";
        String data = "nsrsbh=" + nsrsbh + "&startTime=" + startTime + "&endTime=" + endTime;
        String postRequest = HttpRequestUtils.postRequest(url1, data);
        if (postRequest == null || postRequest.trim().length() == 0) {
            throw new BusinessException(3021, "请求服务器错误");
        }
        JSONObject jsonObject = JSON.parseObject(postRequest);
        ConResult conResult = new ConResult();
        if (jsonObject.getInteger("errcode") == 0) {
            conResult.getMap().put("jsonObject", jsonObject);
        }
        return conResult;
    }
}
