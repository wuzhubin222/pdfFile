package controller;

import Dao.*;
import entity.ConResult;
import excption.BusinessException;
import model.CityCode;
import model.EfpCaMaterial;
import model.EfpCzyDab;
import model.EfpNsrDab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.LogUtils;
import utils.PdfUtils;

import javax.servlet.http.HttpServletResponse;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

@Scope("prototype")
@Controller
public class SzcaControllerTest extends BaseController {

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


    /**
     * ca证书申请与上传ca图片
     * @param response
     * @param nsrsbh
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "caMaterialUploadTest", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
    public ConResult caMaterialUploadTest(HttpServletResponse response,
                        @RequestParam(value = "nsrsbh") String nsrsbh) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        ConResult conResult = new ConResult();

        EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(nsrsbh);
        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if(efpNsrDabs.size() == 0){
            throw new BusinessException(3020,nsrsbh+"纳税人表数据不存在");
        }
        efpNsrDab = efpNsrDabs.get(0);

        CityCode cityCode = new CityCode();
        cityCode.setCode(efpNsrDab.getProvinceCode());
        List<CityCode> cityCodes = cityCodeMapper.selectSelective(cityCode);
        if(cityCodes.size()==0){
            throw new BusinessException(3021,"省份编码未找到"+cityCode);
        }
        String province = cityCodes.get(0).getCity();
        cityCode.setCode(efpNsrDab.getCityCode());
        cityCodes = cityCodeMapper.selectSelective(cityCode);
        if(cityCodes.size()==0){
            throw new BusinessException(3022,"城市编码未找到"+cityCode);
        }
        String city = cityCodes.get(0).getCity();

        EfpCzyDab efpCzyDab = new EfpCzyDab();
        efpCzyDab.setNsrsbh(nsrsbh);
        efpCzyDab.setType(0);
        List<EfpCzyDab> efpCzyDabs = efpCzyDabMapper.selectSelective(efpCzyDab);
        if(efpCzyDabs.size()!=1){
            throw new BusinessException(3023,"操作员管理员查询错误"+nsrsbh);
        }
        efpCzyDab = efpCzyDabs.get(0);

        EfpCaMaterial efpCaMaterial = new EfpCaMaterial();
        efpCaMaterial.setNsrsbh(nsrsbh);
        List<EfpCaMaterial> efpCaMaterials = efpCaMaterialMapper.selectSelective(efpCaMaterial);
        if(efpCaMaterials.size()!=1){
            throw new BusinessException(3024,"ca_material表数据错误"+nsrsbh);
        }
        efpCaMaterial = efpCaMaterials.get(0);

//        String applyId = ttd_testCertApply.certApply(efpNsrDab.getNsrmc(), efpNsrDab.getLxdz(), nsrsbh,province,city,efpCzyDab.getCzyName(),
//                efpCzyDab.getCzyId(),efpCzyDab.getParm1(),efpNsrDab.getFrmc(), efpNsrDab.getLxdz(),log);

//        String sfz = efpCaMaterial.getSfzUrl().split("invoicefileservice.bjzsxx.com/")[1];
//        String sqb = efpCaMaterial.getSqbUrl().split("invoicefileservice.bjzsxx.com/")[1];
//        String sqxy = efpCaMaterial.getSqxyUrl().split("invoicefileservice.bjzsxx.com/")[1];
//        String yyzz = efpCaMaterial.getYyzzUrl().split("invoicefileservice.bjzsxx.com/")[1];

//        FTPClient ftpClient = FileStorageUtils.openFtpConnect(log);
//        FileStorageUtils.downFileIfNotExist(log,this.getClass().getResource("/").getPath() + "downFile/身份证"+nsrsbh+".jpg",sfz,ftpClient);
//        ftpClient = FileStorageUtils.openFtpConnect(log);
//        FileStorageUtils.downFileIfNotExist(log,this.getClass().getResource("/").getPath() + "downFile/申请表"+nsrsbh+".jpg",sqb,ftpClient);
//        ftpClient = FileStorageUtils.openFtpConnect(log);
//        FileStorageUtils.downFileIfNotExist(log,this.getClass().getResource("/").getPath() + "downFile/申请协议"+nsrsbh+".jpg",sqxy,ftpClient);
//        ftpClient = FileStorageUtils.openFtpConnect(log);
//        FileStorageUtils.downFileIfNotExist(log,this.getClass().getResource("/").getPath() + "downFile/营业执照"+nsrsbh+".jpg",yyzz,ftpClient);

//        ttd_imageAppUpload.imageAppUoload(log,"营业执照.jpg", this.getClass().getResource("/").getPath() + "downFile/营业执照"+nsrsbh+".jpg", applyId);
//        ttd_imageAppUpload.imageAppUoload(log,"身份证.jpg",   this.getClass().getResource("/").getPath() + "downFile/身份证"+nsrsbh+".jpg", applyId);
//        ttd_imageAppUpload.imageAppUoload(log,"申请表.jpg",   this.getClass().getResource("/").getPath() + "downFile/申请表"+nsrsbh+".jpg", applyId);
//        ttd_imageAppUpload.imageAppUoload(log,"申请协议.jpg", this.getClass().getResource("/").getPath() + "downFile/申请协议"+nsrsbh+".jpg", applyId);

//        EfpCaMaterial teaml = new EfpCaMaterial();
//        teaml.setId(efpCaMaterial.getId());
//        teaml.setCaBusinessCode(applyId);
//        if(efpCaMaterialMapper.updateByPrimaryKeySelective(teaml)!=1){
//            throw new BusinessException(1);
//        }

        return new ConResult();
    }


    //从深圳ca下载ca证书到文件服务器
    @ResponseBody
    @RequestMapping(value = "caDownTest", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
    public ConResult caDownTest(
            HttpServletResponse response,
            @RequestParam(value = "nsrsbh") String nsrsbh) {
        response.setHeader("Access-Control-Allow-Origin", "*");

//        if(nsrsbh.equals("1")){
//            throw new BusinessException(3501,"下载失败");
//        }

//        String password = CaUtils.downCaBynsrsbh(log, nsrsbh, this.getClass().getResource("/").getPath() + "downFile/"+nsrsbh+".pfx");
//
//        try {
//            FTPClient ftpClient = FileStorageUtils.openFtpConnect(log);
//            ftpClient.changeWorkingDirectory("ca");
//            //一级目录文件数
//            int lastFolderName = ftpClient.listNames().length;
//            ftpClient.changeWorkingDirectory(lastFolderName+"");
//            if(ftpClient.listNames().length> Constant.CA_MAX_NUMBER){
//                ftpClient.changeToParentDirectory();
//                lastFolderName++;
//                ftpClient.makeDirectory(lastFolderName+"");
//                ftpClient.changeWorkingDirectory(lastFolderName+"");
//                ftpClient.makeDirectory("1");
//            }
//            //进入二位文件目录
//            int secondFolderName = ftpClient.listNames().length;
//            ftpClient.changeWorkingDirectory(secondFolderName+"");
//            if(ftpClient.listNames().length> Constant.CA_MAX_NUMBER){
//                ftpClient.changeToParentDirectory();
//                secondFolderName++;
//                ftpClient.makeDirectory(secondFolderName+"");
//                ftpClient.changeWorkingDirectory(secondFolderName+"");
//            }
//
//            InputStream inputStream = new FileInputStream(this.getClass().getResource("/").getPath() + "downFile/"+nsrsbh+".pfx");
//            if(ftpClient.storeFile(nsrsbh+".pfx",inputStream)){
//                EfpCa efpCa = new EfpCa();
//                efpCa.setNsrsbh(nsrsbh);
//                List<EfpCa> efpCas = efpCaMapper.selectSelective(efpCa);
//                if(efpCas.size()==1){
//                    String id = efpCas.get(0).getId();
//                    if(efpCaMapper.deleteByPrimaryKey(id)!=1){
//                        throw new BusinessException(1);
//                    }
//                }
//                efpCa.setUpdateTime(new Date());
//                efpCa.setCaPath("ca/"+lastFolderName+"/"+secondFolderName);
//                efpCa.setCaImgPath("ca/"+lastFolderName+"/"+secondFolderName);
//                efpCa.setCaPassword(password);
//                efpCa.setTemplateName("深圳");
//                if(efpCaMapper.insertSelective(efpCa)!=1){
//                    throw new BusinessException(1);
//                }
//            }else {
//                LogUtils.writeLogFile(log,"caDown","storeFile方法异常",nsrsbh);
//                throw new BusinessException(3502,"下载失败");
//            }
//        } catch (Exception e) {
//            LogUtils.writeLogFile(log,"caDown","下载异常",nsrsbh,e.toString());
//            throw new BusinessException(3503,"下载失败");
//        }

        return new ConResult();
    }


    public static void main(String[] args) {

  try {
      //  String caFilePath=this.getClass().getResource("/").getPath() + "downFile/"+etpTaxId+".pfx";
      String caFilePath="D:/91440300319797090X.pfx";
      String password ="729539";
      //签章
      KeyStore ks = PdfUtils.signCheck(caFilePath, password);
      String aliases = ks.aliases().nextElement();
      PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
      //证书链
      Certificate[] chain = ks.getCertificateChain(aliases);
      System.out.println(11);
  }catch (Exception e){
      e.printStackTrace();
  }




    }



}
