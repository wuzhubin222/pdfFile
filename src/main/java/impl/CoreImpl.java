package impl;

import Dao.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.Rectangle;
import constant.Constant;
import entity.ConResult;
import excption.BusinessException;
import Dao.CertPdfMapper;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import schedule.BsnAsync;
import service.ICore;
import utils.*;

import java.io.*;
import java.math.BigDecimal;
import java.security.*;
import java.security.cert.Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Scope("prototype")
@Service
public class CoreImpl extends BaseUtils implements ICore {

    @Autowired
    private EfpInvoiceCopyMapper efpInvoiceCopyMapper;

    @Autowired
    private EfpNsrDabMapper efpNsrDabMapper;

    @Autowired
    private EfpInvoiceMapper efpInvoiceMapper;

    @Autowired
    private EfpInvoiceQdMapper efpInvoiceQdMapper;

    @Autowired
    private EfpFileRepositoryMapper efpFileRepositoryMapper;

    @Autowired
    private EfpZpInvoiceMapper efpZpInvoiceMapper;

    @Autowired
    private EfpZyfpInvoiceMapper efpZyfpInvoiceMapper;

    @Autowired
    private EfpZpInvoiceQdMapper efpZpInvoiceQdMapper;

    @Autowired
    private EfpZyfpInvoiceQdMapper efpZyfpInvoiceQdMapper;

    @Autowired
    private EfpZyfpInvoiceCopyMapper efpZyfpInvoiceCopyMapper;

    @Autowired
    private EfpZpInvoiceCopyMapper efpZpInvoiceCopyMapper;

    @Autowired
    private EfpInvoiceRollMapper efpInvoiceRollMapper;

    @Autowired
    private EfpJpInvoiceMapper efpJpInvoiceMapper;

    @Autowired
    private EfpJpInvoiceQdMapper efpJpInvoiceQdMapper;

    @Autowired
    private EfpJpInvoiceCopyMapper efpJpInvoiceCopyMapper;

    @Autowired
    private EfpCaMapper efpCaMapper;
    @Autowired
    private EfpZfMapper efpZfMapper;


    @Autowired
    private CdEfpMailmanageMapper cdEfpMailmanageMapper;


    @Autowired
    private BsnAsync bsnAsync;


    @Autowired
    private CertUploadDataMapper certUploadDataMapper;

    @Autowired
    private CertPdfMapper certPdfMapper;



    //tcis推送电子发票
    @Transactional
    public ConResult cenKp(String fpdm, String fphm, String qqlsh, String jqbm, String kprq, String jym, String bz, String code, String zjm, String fpmw) {

        EfpInvoiceCopy efpInvoiceCopy = new EfpInvoiceCopy();
        efpInvoiceCopy.setParm2(qqlsh);
        List<EfpInvoiceCopy> efpInvoiceCopys = efpInvoiceCopyMapper.selectSelective(efpInvoiceCopy);
        if (efpInvoiceCopys.size() == 0) {
            throw new BusinessException(3046, "临时表未查找到数据");
        }
        efpInvoiceCopy = efpInvoiceCopys.get(0);

        //版式文件表单数据
        HashMap<String, String> dataMap = new HashMap<String, String>();


        int div = fpmw.length() / 4;
        if (fpmw.length() == 108) {
            dataMap.put("mmq1", fpmw.substring(0, div));
            dataMap.put("mmq2", fpmw.substring(div, div * 2));
            dataMap.put("mmq3", fpmw.substring(div * 2, div * 3));
            dataMap.put("mmq4", fpmw.substring(div * 3, div * 4));
        } else {
            dataMap.put("mmq1", fpmw);
        }

        //组装invoice
        EfpInvoice efpInvoice = new EfpInvoice();
        efpInvoice.setFpdm(fpdm);
        dataMap.put("fpdm", fpdm);
        efpInvoice.setFphm(fphm);
        dataMap.put("fphm", fphm);
        efpInvoice.setJqbh(jqbm);
        dataMap.put("jqbh", jqbm);
        efpInvoice.setFpJym(jym);
        if (jym.length() == 20) {
            dataMap.put("jym", jym.substring(0, 5) + " " + jym.substring(5, 10) + " " +
                    jym.substring(10, 15) + " " + jym.substring(15, 20));
        } else {
            dataMap.put("jym", jym);
        }
        efpInvoice.setPhoneNo(efpInvoiceCopy.getParm3());
        efpInvoice.setBz(bz);
        dataMap.put("bz1", bz);
        efpInvoice.setKprq(DateUtils.getFormatDate("yyyyMMddHHmmss", kprq));
        dataMap.put("kprq_nian", kprq.substring(0, 4));
        dataMap.put("kprq_yue", kprq.substring(4, 6));
        dataMap.put("kprq_ri", kprq.substring(6, 8));

        String kpJson = efpInvoiceCopy.getKpJson();
        updateInvoiceFromKpJson(efpInvoice, kpJson, dataMap);


        //组装invoiceQd
        List<EfpInvoiceQd> efpInvoiceQds = getInvoiceQdsFromKpJson(kpJson, dataMap);
        BigDecimal jehj = new BigDecimal(0);
        BigDecimal sehj = new BigDecimal(0);
        for (EfpInvoiceQd efpInvoiceQd : efpInvoiceQds) {
            efpInvoiceQd.setFpdm(fpdm);
            efpInvoiceQd.setFphm(fphm);
            efpInvoiceQd.setXfNsrsbh(efpInvoice.getXfsh());
            efpInvoiceQd.setGfNsrsbh(efpInvoice.getGfsh());
            jehj = jehj.add(efpInvoiceQd.getJe());
            sehj = sehj.add(efpInvoiceQd.getSe());
            if (efpInvoiceQdMapper.insertSelective(efpInvoiceQd) != 1) {
                throw new BusinessException(1);
            }
        }

        efpInvoice.setCsmUnionid(efpInvoiceCopy.getParm6());
        efpInvoice.setHjje(jehj);
        efpInvoice.setHjse(sehj);
        dataMap.put("hjje", "¥" + jehj.toString());
        dataMap.put("hjse", "¥" + sehj.toString());
        BigDecimal jshjx = jehj.add(sehj);
        String jshjdx = PdfUtils.moneyNumberTocCh(jshjx);
//        jshjdx = PdfUtils.moneyNumberTocCh(jshjx);
        String jshjxx = "¥" + jshjx.toString();
        dataMap.put("jshjdx", jshjdx);
        dataMap.put("jshjxx", jshjxx);
        if (efpInvoiceMapper.insertSelective(efpInvoice) != 1) {
            throw new BusinessException(1);
        }

        EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(efpInvoice.getXfsh());
        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if (efpNsrDabs.size() == 0) {
            throw new BusinessException(3046, "纳税人表信息错误");
        }

        JSONObject jsonObject = JSON.parseObject(kpJson);
        EfpInvoiceCopy efpInvoiceCopyTmp = new EfpInvoiceCopy();
        efpInvoiceCopyTmp.setId(efpInvoiceCopy.getId());
        efpInvoiceCopyTmp.setBz(3);
        efpInvoiceCopyTmp.setParm4("开票成功");
        efpInvoiceCopyTmp.setFpdm(fpdm);
        efpInvoiceCopyTmp.setFphm(fphm);
        if (efpInvoiceCopyMapper.updateByPrimaryKeySelective(efpInvoiceCopyTmp) != 1) {
            throw new BusinessException(3046, "临时表修改失败");
        }

        //上传文件并存库
        String pdfUrl = uploadPdf(fpdm, fphm, zjm, efpNsrDabs.get(0));
//        String pdfUrl = uploadPdf(fpdm, fphm, inputStream, efpNsrDabs.get(0));

        //发送邮件
        String emailAccount = jsonObject.getString("emailAccount");
        if (!StringUtils.isEmpty(emailAccount)) {
            BigDecimal jshj = efpInvoice.getHjje().add(efpInvoice.getHjse());
            Date dt = DateUtils.getFormatDate("yyyyMMddHHmmss", kprq);
         /*   bsnAsync.mailPush(pdfUrl, DateUtils.getFormatString("yyyy-MM-dd_HH:mm:ss", dt), fpdm, fphm,
                    efpInvoice.getXfmc(), efpInvoice.getGfmc(), jshj.toString(), emailAccount);*/
        }

        //公众号发送模板消息
        String pubOpenId = jsonObject.getString("pubOpenId");
        if (!StringUtils.isEmpty(pubOpenId)) {
            BigDecimal jshj = efpInvoice.getHjje().add(efpInvoice.getHjse());
            Date dt = DateUtils.getFormatDate("yyyyMMddHHmmss", kprq);
            //   bsnAsync.tmpMsgPush(pubOpenId, fpdm, fphm, efpInvoice.getXfmc(), efpInvoice.getGfmc(), jshj.toString(), DateUtils.getFormatString("yyyy-MM-dd_HH:mm:ss", dt));
        }

        //公众号发送短信通知
        String pubPhone = jsonObject.getString("pubPhone");
        if (!StringUtils.isEmpty(pubPhone)) {
            //     bsnAsync.sendPhoneMsg(pubPhone, efpInvoice.getId(),efpInvoiceCopy.getParm5());
        }

        //小程序发送模板消息
        String microOpenId = jsonObject.getString("microOpenId");
        String formId = jsonObject.getString("formId");
        if (!StringUtils.isEmpty(microOpenId)) {
            BigDecimal jshj = efpInvoice.getHjje().add(efpInvoice.getHjse());
            Date dt = DateUtils.getFormatDate("yyyyMMddHHmmss", kprq);
          /* bsnAsync.sendTemMsg(microOpenId, efpInvoice.getXfmc(), efpInvoice.getGfmc(), jshj.toString(),
                    DateUtils.getFormatString("yyyy-MM-dd_HH:mm:ss", dt), formId);*/
        }

        //推送
        String kprId = jsonObject.getString("kprId");
        String sendUrl = jsonObject.getString("sendUrl");
        if (!StringUtils.isEmpty(kprId)) {
            String data = "invguid=" + efpInvoiceCopy.getId() +
                    "&kprId=" + kprId + "&status=2&reson=&fpdm=" + fpdm + "&fphm=" + fphm +
                    "&url=" + pdfUrl;
            String result = HttpRequestUtils.postRequest(sendUrl, data);
        }

        return new ConResult();
    }

    /**
     * 开具红冲发票
     *
     * @param logPath
     * @param dzfpResult tcis传入的json数据
     * @param qqlsh      请求流水号
     * @return
     */
    @Transactional
    public ConResult sendHzfp(String logPath, String dzfpResult, String qqlsh) throws Exception {

        EfpZf efpZf = new EfpZf();
        efpZf.setParm3(qqlsh);
        //efpZf.setStatus(4);
        List<EfpZf> efpZfs = efpZfMapper.selectSelective(efpZf);
        //efpZf = efpZfs.get(0);
        //解析json数据
        JSONObject jsonObject = JSON.parseObject(dzfpResult).getJSONObject("dzfpresult");
        HashMap<String, String> dataMap = new HashMap<String, String>();
        PdfUtils.composeMapToPdf(jsonObject, dataMap);

        List<EfpInvoiceQd> efpInvoiceQds = new ArrayList<EfpInvoiceQd>();
        PdfUtils.composezfInvoiceQdToDb(jsonObject, efpInvoiceQds);
        for (EfpInvoiceQd efpInvoiceQd : efpInvoiceQds) {
          /*if (efpInvoiceQdMapper.insertSelective(efpInvoiceQd) != 1) {
                throw new BusinessException(1);
            }
        */}

        EfpInvoice efpInvoice = new EfpInvoice();
        PdfUtils.composezfInvoiceToDb(jsonObject, efpInvoice);
        efpInvoice.setCzyId(efpZf.getParm6());
        efpInvoice.setSkm(String.valueOf(efpZf.getKjh()));
        efpInvoice.setZfBz(2);
      /*if (efpInvoiceMapper.insertSelective(efpInvoice) != 1) {
            throw new BusinessException(1);
        }
      */
      EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(efpInvoice.getXfsh());
        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if (efpNsrDabs.size() == 0) {
            throw new BusinessException(3046, "纳税人表信息错误");
        }

//        二维码内容
        String qrCodeContent = jsonObject.getString("qrm");
//        生成后的二维码图片路径
        String QrCodeImgPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + "QrCode.png";
        try {
            QrCodeUtils.proQrCodeImg(qrCodeContent, 150, 150, QrCodeImgPath);
        } catch (Exception e) {
            throw new BusinessException(3045, "二维码图片操作失败");
        }

//        字体文件路径
        String fontPath = this.getClass().getResource("/").getPath() + "pdfSign/C9.ttf";
//        下载ca与图片至本地downFile目录
        EfpCa efpCa = downCa(efpInvoice.getXfsh(), logPath + "hcca.txt");
//        pdf发票模板
        String tmplatePdfPath = this.getClass().getResource("/").getPath() + "pdfTemplate/" + efpCa.getTemplateName() + ".pdf";

//        ketstore文件路径
        String keyStorePath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".pfx";
//        ca图片路径
        String signImgPath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".png";
//        ketstore密码
        String password = efpCa.getCaPassword();

        //文件数量下标
        int num = 1;
        //合并前的pdf文件路径
        String tmpOutPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + num + ".pdf";

//         签完章的pdf文件路径
        String outPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + ".pdf";

        // TODO: 2017/11/2 上传后删除相关文件
        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        InputStream inputStream;
        if (jsonArray.size() <= 5) {
            try {
                //签名私钥
                KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
                String aliases = ks.aliases().nextElement();
                PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
                //证书链

                Certificate[] chain = ks.getCertificateChain(aliases);
                PdfUtils.createhc(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, chain, pk, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3073, "版式文件失败" + e.toString());
            }
        } else {
            //不进行签证操作
            try {
                PdfUtils.createhc(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, null, null, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3073, "版式文件失败" + e.toString());
            }
        }
        //用于发送邮件
        //JSONArray mailAttachUrls = new JSONArray();
        if (jsonArray.size() > 5) {
            StringBuffer sb = new StringBuffer();
            String path = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm();
            PDFDetailedUtil.create(jsonObject, this.getClass().getResource("/").getPath() + "pdfTemplate/商品清单模板.pdf", fontPath,
                    () -> {
                        sb.append("1");
                        return path + (sb.length() + 1) + ".pdf";
                    });
            num += sb.length();
        }
        if (num > 1) {
            //合并文件
            JSONArray filePaths = new JSONArray();
            List<Rectangle> reck = new ArrayList<Rectangle>();
            for (int i = 1; i <= num; i++) {
                if (i == 1) {
                    reck.add(new Rectangle(480, 0, 587, 104));
                } else {
                    // //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
                    reck.add(new Rectangle(20, 0, 250, 104));
                }
                filePaths.add(this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + i + ".pdf");
            }
            PdfUtils.mergePdfFiles(logPath, filePaths, outPdfPath);
            //单独进行签证
            KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
            String aliases = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
            //证书链
            Certificate[] chain = ks.getCertificateChain(aliases);
            for (int i = 0; i < reck.size(); i++) {
                PdfUtils.visa(outPdfPath, outPdfPath + ".pdf", chain, pk, signImgPath, "sign", "Ghent", reck.get(i),    i + 1);
                outPdfPath += ".pdf";
            }
        } else {
            outPdfPath = tmpOutPdfPath;
        }
        String pdfUrl = uploadPdf(efpInvoice.getFpdm(), efpInvoice.getFphm(), efpInvoice.getFpdm() + efpInvoice.getFphm() + ".pdf", outPdfPath, efpNsrDabs.get(0));
        EfpZf efpZftmp = new EfpZf();
        efpZftmp.setId(efpZf.getId());
        efpZftmp.setStatus(1);
        efpZftmp.setMessage("红冲成功");
        efpZftmp.setParm4(efpInvoice.getFpdm());
        efpZftmp.setParm5(efpInvoice.getFphm());
     /*if(this.efpZfMapper.updateByPrimaryKeySelective(efpZftmp) != 1) {
            throw new BusinessException(3046, "临时表修改失败");
        }
     */
     // return new ConResult();

        //*本地生成红冲使用*/
        Map<String, Object> map = new HashMap<String, Object>();
        ConResult conResult = new ConResult();
        System.out.println();
        map.put("Url", pdfUrl);
        conResult.setMap(map);
        return conResult;
    }


    /**
     * 手动红冲发票
     *
     * @param logPath
     * @param dzfpResult 传入的json数据
     * @return
     */
    @Transactional
    public ConResult sendHz(String logPath, String dzfpResult) throws Exception {


        //解析json数据
        JSONObject jsonObject = JSON.parseObject(dzfpResult).getJSONObject("dzfpresult");
        HashMap<String, String> dataMap = new HashMap<String, String>();
        PdfUtils.composeMapToPdf(jsonObject, dataMap);

        List<EfpInvoiceQd> efpInvoiceQds = new ArrayList<EfpInvoiceQd>();

        EfpInvoice efpInvoice = new EfpInvoice();
        PdfUtils.composezfInvoiceToDb(jsonObject, efpInvoice);

        EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(efpInvoice.getXfsh());
        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if (efpNsrDabs.size() == 0) {
            throw new BusinessException(3046, "纳税人表信息错误");
        }

//        二维码内容
        String qrCodeContent = jsonObject.getString("qrm");
//        生成后的二维码图片路径
        String QrCodeImgPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + "QrCode.png";
        try {
            QrCodeUtils.proQrCodeImg(qrCodeContent, 150, 150, QrCodeImgPath);
        } catch (Exception e) {
            throw new BusinessException(3045, "二维码图片操作失败");
        }

//        字体文件路径
        String fontPath = this.getClass().getResource("/").getPath() + "pdfSign/C9.ttf";

//        下载ca与图片至本地downFile目录
        EfpCa efpCa = downCa(efpInvoice.getXfsh(), logPath + "hcca.txt");
//        pdf发票模板
        String tmplatePdfPath = this.getClass().getResource("/").getPath() + "pdfTemplate/" + efpCa.getTemplateName() + ".pdf";

//        ketstore文件路径
        String keyStorePath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".pfx";
//        ca图片路径
        String signImgPath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".png";
//        ketstore密码
        String password = efpCa.getCaPassword();

        //文件数量下标
        int num = 1;
        //合并前的pdf文件路径
        String tmpOutPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + num + ".pdf";

//         签完章的pdf文件路径
        String outPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + ".pdf";

        // TODO: 2017/11/2 上传后删除相关文件
        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        InputStream inputStream;
        if (jsonArray.size() <= 5) {
            try {
                //签名私钥
                KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
                String aliases = ks.aliases().nextElement();
                PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
                //证书链

                Certificate[] chain = ks.getCertificateChain(aliases);
                PdfUtils.createhc(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, chain, pk, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3073, "版式文件失败" + e.toString());
            }
        } else {
            //不进行签证操作
            try {
                PdfUtils.createhc(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, null, null, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3073, "版式文件失败" + e.toString());
            }
        }
        //用于发送邮件
        //JSONArray mailAttachUrls = new JSONArray();
        if (jsonArray.size() > 5) {
            StringBuffer sb = new StringBuffer();
            String path = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm();
            PDFDetailedUtil.create(jsonObject, this.getClass().getResource("/").getPath() + "pdfTemplate/商品清单模板.pdf", fontPath,
                    () -> {
                        sb.append("1");
                        return path + (sb.length() + 1) + ".pdf";
                    });
            num += sb.length();
        }
        if (num > 1) {
            //合并文件
            JSONArray filePaths = new JSONArray();
            List<Rectangle> reck = new ArrayList<Rectangle>();
            for (int i = 1; i <= num; i++) {
                if (i == 1) {
                    reck.add(new Rectangle(480, 0, 587, 104));
                } else {
                    // //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
                    reck.add(new Rectangle(20, 0, 250, 104));
                }
                filePaths.add(this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + i + ".pdf");
            }
            PdfUtils.mergePdfFiles(logPath, filePaths, outPdfPath);
            //单独进行签证
            KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
            String aliases = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
            //证书链
            Certificate[] chain = ks.getCertificateChain(aliases);
            for (int i = 0; i < reck.size(); i++) {
                PdfUtils.visa(outPdfPath, outPdfPath + ".pdf", chain, pk, signImgPath, "sign", "Ghent", reck.get(i),
                        i + 1);
                outPdfPath += ".pdf";
            }
        } else {
            outPdfPath = tmpOutPdfPath;
        }
        String pdfUrl = uploadhcPdf(efpInvoice.getFpdm(), efpInvoice.getFphm(), efpInvoice.getFpdm() + efpInvoice.getFphm() + ".pdf", outPdfPath, efpNsrDabs.get(0));

        //*本地生成红冲使用*/
        Map<String, Object> map = new HashMap<String, Object>();
        ConResult conResult = new ConResult();
        System.out.println();
        map.put("Url", pdfUrl);
        conResult.setMap(map);
        return conResult;
    }


    public ConResult createDZp2(String logPath, String dzfpResult, String qqlsh, ICore iCore) throws Exception {
       /* ICore thisCode=this;
        ICore par=iCore;
        System.out.println(thisCode.getClass().getName());
        System.out.println(par.getClass().getName());*/
        HashMap<String, String> efpInvoiceCopy = iCore.transactionalCreateDZp2(logPath, dzfpResult, qqlsh);
        ConResult conResult = new ConResult(1, efpInvoiceCopy.get("gfmc") + "^" + efpInvoiceCopy.get("hjje"));
        /*webSock推送*/
        WebSocketUtils1 ss = new WebSocketUtils1();
        Map map = ss.getSocketMaps();
        ss.qunfa(map, JSON.toJSONString(conResult), conResult);
        String kprId = efpInvoiceCopy.get("kprId");
        String sendUrl = efpInvoiceCopy.get("sendUrl");
        if (!StringUtils.isEmpty(sendUrl)) {
            bsnAsync.sendbackMsg(sendUrl, efpInvoiceCopy.get("id"), kprId, efpInvoiceCopy.get("fpdm"), efpInvoiceCopy.get("fphm"), efpInvoiceCopy.get("kptime"), efpInvoiceCopy.get("pdfUrl"), efpInvoiceCopy.get("email"));
        }
        String spzhm = efpInvoiceCopy.get("parm5");
        if (!StringUtils.isEmpty(spzhm)) {
            bsnAsync.sendWx(logPath, spzhm);
        }
        /*支付宝*/

      /*  String alipaystatus = efpInvoiceCopy.get("Alipaystatus");
       *//* !StringUtils.isEmpty(alipaystatus)*//*
        if (alipaystatus.equals("1")) {
            bsnAsync.sendZfb(efpInvoiceCopy.get("id"));
        }*/
        return new ConResult();
    }


    /*发送邮箱测试*/
    public HashMap sendTest(String ss) {
        List<HashMap<String, String>> list = efpInvoiceMapper.selectSeleTest();
        int num = 0;
        for (Map<String, String> item : list) {

            JSONObject jsonObject = JSON.parseObject(item.get("kp_json"));
            String emailAccount = "1257831509@qq.com";
            System.out.println(jsonObject.get("emailAccount"));
            System.out.println("youxiagn" + emailAccount);
            String fpdm = item.get("fpdm");
            String fphm = item.get("fphm");
            num++;
            bsnAsync.mailPushTest(
                    fpdm,
                    fphm, emailAccount);
        }
        System.out.println("shuliango" + num);
        return null;
    }


    @Transactional
    public HashMap<String, String> transactionalCreateDZp2(String logPath, String dzfpResult, String qqlsh) throws Exception {
        Set<String> files=new HashSet<String>();
        //查询订单是否存在
        EfpInvoiceCopy efpInvoiceCopy = new EfpInvoiceCopy();
        efpInvoiceCopy.setParm2(qqlsh);
        efpInvoiceCopy.setBz(4);
        List<EfpInvoiceCopy> efpInvoiceCopys = efpInvoiceCopyMapper.selectSelective(efpInvoiceCopy);

        if (efpInvoiceCopys.size() == 0) {
            this.logOutput(new BusinessException(3046, "临时表未查找到数据"));
            throw new BusinessException(3046, "临时表未查找到数据");
        }
        efpInvoiceCopy = efpInvoiceCopys.get(0);

        //解析json数据
        JSONObject jsonObject = JSON.parseObject(dzfpResult).getJSONObject("dzfpresult");

        HashMap<String, String> dataMap = new HashMap<String, String>();
        HashMap<String, String> dataMaps = new HashMap<String, String>();

        PdfUtils.composeMapToPdf(jsonObject, dataMap);

        List<EfpInvoiceQd> efpInvoiceQds = new ArrayList<EfpInvoiceQd>();

        PdfUtils.composeInvoiceQdToDb(jsonObject, efpInvoiceQds);

        for (EfpInvoiceQd efpInvoiceQd : efpInvoiceQds) {
            if (efpInvoiceQdMapper.insertSelective(efpInvoiceQd) != 1) {
                this.logOutput(new BusinessException(1));
                throw new BusinessException(1);
            }
        }
        EfpInvoice efpInvoice = new EfpInvoice();

        PdfUtils.composeInvoiceToDb(jsonObject, efpInvoice);
        PdfUtils.composeInvoiceToDb(efpInvoiceCopy, efpInvoice);

        String kpJson = efpInvoiceCopy.getKpJson();
        JSONObject tableJsonObject = JSON.parseObject(kpJson);
        //获取邮件
        String emailAccount = tableJsonObject.getString("emailAccount");

        efpInvoice.setSkm(efpInvoiceCopy.getKjh());
        efpInvoice.setCzyId(efpInvoiceCopy.getParm7());
        efpInvoice.setPhoneNo(efpInvoiceCopy.getParm3());
        efpInvoice.setSpUser(tableJsonObject.getString("kprId"));
        efpInvoice.setParm2(emailAccount);
        if (efpInvoiceMapper.insertSelective(efpInvoice) != 1) {
            this.logOutput(new BusinessException(1));
            throw new BusinessException(1);
        }

        //查询是否存在数据
        List<Cdefpresemaintain> reseSize = efpInvoiceMapper.selectRiseInfo(efpInvoice.getGfsh());
        if (reseSize.size() == 0) {
            Cdefpresemaintain cdefpresemaintain = new Cdefpresemaintain();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            cdefpresemaintain.setId(uuid);
            cdefpresemaintain.setXfsh(efpInvoice.getXfsh());
            cdefpresemaintain.setGfsh(efpInvoice.getGfsh());
            cdefpresemaintain.setGfdzdh(efpInvoice.getGfdzDh());
            cdefpresemaintain.setGfyhmcyhzh(efpInvoice.getGfyhmcYhzh());
            cdefpresemaintain.setEmail(emailAccount);
            cdefpresemaintain.setGfmc(efpInvoice.getGfmc());
            cdefpresemaintain.setUpdatetime(efpInvoice.getCreateTime());
            cdefpresemaintain.setCreatetime(new Date());
            cdefpresemaintain.setStatus("1");
            efpInvoiceMapper.insertRise(cdefpresemaintain);
        }

        EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(efpInvoice.getXfsh());

        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if (efpNsrDabs.size() == 0) {
            this.logOutput(new BusinessException(3048, "纳税人表信息错误"));
            throw new BusinessException(3048, "纳税人表信息错误");
        }
        efpNsrDab = efpNsrDabs.get(0);
//        二维码内容
        String qrCodeContent = jsonObject.getString("qrm");
//        生成后的二维码图片路径
        String QrCodeImgPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + "QrCode.png";
        try {
            QrCodeUtils.proQrCodeImg(qrCodeContent, 150, 150, QrCodeImgPath);
        } catch (Exception e) {
           // LogUtils.writeLogFileName("createDZpError.txt", "二维码图片操作失败" + e.toString());
            this.logOutput(new BusinessException(3045, "二维码图片操作失败"+ e.toString()));
            /* LogUtils.writeLogFile(this.getClass().getResource("/").getPath() + "log/createDZp.txt", "二维码图片操作失败" + e.toString());*/
            throw new BusinessException(3045, "二维码图片操作失败");
        }
//        字体文件路径
        String fontPath = this.getClass().getResource("/").getPath() + "pdfSign/C9.ttf";
//        下载ca与图片至本地downFile目录
        EfpCa efpCa = downCa(efpInvoice.getXfsh(), this.getClass().getResource("/").getPath() + "log/ca.txt");
        //        ca图片路径
        String signImgPath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".png";


//        pdf发票模板
        String tmplatePdfPath = this.getClass().getResource("/").getPath() + "pdfTemplate/" + efpCa.getTemplateName() + ".pdf";
//        ketstore文件路径
        String keyStorePath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".pfx";
//        ketstore密码
        String password = efpCa.getCaPassword();

        if (StringUtils.isEmpty(signImgPath)) {
            throw new BusinessException(3049, "ca图片不存在");
        }
        //文件数量下标
        int num = 1;
        //合并前的pdf文件路径
        String tmpOutPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + num + ".pdf";
//         签完章的pdf文件路径
        String outPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + ".pdf";
        EfpInvoiceCopy efpInvoiceCopyTmp = new EfpInvoiceCopy();
        efpInvoiceCopyTmp.setId(efpInvoiceCopy.getId());
        efpInvoiceCopyTmp.setBz(3);
        efpInvoiceCopyTmp.setParm4("开票成功");
        efpInvoiceCopyTmp.setFpdm(efpInvoice.getFpdm());
        efpInvoiceCopyTmp.setFphm(efpInvoice.getFphm());
        if (efpInvoiceCopyMapper.updateByPrimaryKeySelective(efpInvoiceCopyTmp) != 1) {
            throw new BusinessException(3049, "临时表修改失败");
        }
        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        InputStream inputStream;
        if (jsonArray.size() <= 5) {
            try {
                //签名私钥
                KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
                String aliases = ks.aliases().nextElement();
                PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
                //证书链
                Certificate[] chain = ks.getCertificateChain(aliases);
                //LogUtils.writeLogFileName("hbDZp.txt", "合并前的pdf文件路径tmpOutPdfPath" + tmpOutPdfPath);
                PdfUtils.create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, chain, pk, "sign", "Ghent");
                //            inputStream = new FileInputStream(outPdfPath);
            } catch (Exception e) {
                //e.printStackTrace();
                this.logOutput(new BusinessException(3073, "获取版式文件失败"+ e.toString()));
                throw new BusinessException(3073, "获取版式文件失败" + e.toString());
            }
        } else {
            //不进行签证操作
            try {
              //  LogUtils.writeLogFileName("hbDZp.txt", "不进行签证操作 合并前的pdf文件路径tmpOutPdfPath" + tmpOutPdfPath);
                PdfUtils.create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, null, null, "sign", "Ghent");
            } catch (Exception e) {
                //e.printStackTrace();
                this.logOutput(new BusinessException(3073, "获取版式文件失败"+ e.toString()));
                throw new BusinessException(3073, "获取版式文件失败" + e.toString());
            }
        }
        // TODO: 2017/11/2 上传后删除相关文件
        if (jsonArray.size() > 5) {
            StringBuffer sb = new StringBuffer();
            String path = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm();

            PDFDetailedUtil.create(jsonObject, this.getClass().getResource("/").getPath() + "pdfTemplate/商品清单模板.pdf", fontPath, () -> { sb.append("1");
            return path + (sb.length() + 1) + ".pdf";
                    });
            num += sb.length();
        }

        if (num > 1) {
            //合并文件
            JSONArray filePaths = new JSONArray();
            List<Rectangle> reck = new ArrayList<Rectangle>();
            for (int i = 1; i <= num; i++) {
                if (i == 1) {
                    reck.add(new Rectangle(480, 0, 587, 104));
                } else {
                    // //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
                    reck.add(new Rectangle(20, 0, 250, 104));
                }
                files.add(this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + i + ".pdf");
                filePaths.add(this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + i + ".pdf");
            }
            PdfUtils.mergePdfFiles(logPath, filePaths, outPdfPath);
            //单独进行签证
            KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
            String aliases = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
            //证书链
            Certificate[] chain = ks.getCertificateChain(aliases);
            for (int i = 0; i < reck.size(); i++) {
                PdfUtils.visa(outPdfPath, outPdfPath + ".pdf", chain, pk, signImgPath, "sign", "Ghent", reck.get(i),
                        i + 1);
                outPdfPath += ".pdf";
                files.add(outPdfPath);
            }
        } else {
            files.add(tmpOutPdfPath);
            outPdfPath = tmpOutPdfPath;
        }
        //记录邮箱
        CdEfpMailmanage cEfpMailmanage = new CdEfpMailmanage();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        cEfpMailmanage.setId(uuid);
        cEfpMailmanage.setCopyid(efpInvoiceCopy.getId());
        cEfpMailmanage.setFphm(efpInvoice.getFphm());
        cEfpMailmanage.setFpdm(efpInvoice.getFpdm());
        cEfpMailmanage.setCreateTime(new Date());
       // LogUtils.writeLogFileName("hbDZp.txt", "签章后的outPdfPath" + outPdfPath);
        String pdfUrl = uploadPdf(efpInvoice.getFpdm(), efpInvoice.getFphm(), efpInvoice.getFpdm() + efpInvoice.getFphm(),
                outPdfPath, efpNsrDabs.get(0),efpInvoice.getKprq());
        //emailAccount="1257831509@qq.com";
        String email = emailAccount;
        if (!StringUtils.isEmpty(emailAccount)) {
            BigDecimal jshj = efpInvoice.getHjje().add(efpInvoice.getHjse());
            Date dt = new Date();
            bsnAsync.mailPush(logPath, pdfUrl, DateUtils.getFormatString("yyyy-MM-dd_HH:mm:ss", efpInvoice.getCreateTime()), efpInvoice.getFpdm(), efpInvoice.getFphm(), efpInvoice.getXfmc(), efpInvoice.getGfmc(), jshj.toString(), emailAccount
                    , (resultMes, url, code) -> {
                        // 发送成功  返回
                        if (code == 200) {
                            try {
                                JSONObject o = JSON.parseObject(resultMes);
                                cEfpMailmanage.setParm1(email);
                                cEfpMailmanage.setMessage(resultMes);
                                cEfpMailmanage.setState(o.getInteger("code") == 0 ? 200 : -1);
                            } catch (Exception e) {
                                cEfpMailmanage.setParm1(email);
                                cEfpMailmanage.setMessage(resultMes);
                                cEfpMailmanage.setState(-1);
                            }
                        } else {
                            // 发送失败 返回
                            cEfpMailmanage.setParm1(email);
                            cEfpMailmanage.setMessage(resultMes);
                            cEfpMailmanage.setState(-1);
                        }
                        cEfpMailmanage.setParm2(url);
                        cdEfpMailmanageMapper.insert(cEfpMailmanage);
                    }
            );
        } else {
            cEfpMailmanage.setParm1(emailAccount);
            cEfpMailmanage.setMessage("邮箱为空");
            cEfpMailmanage.setState(0);
            cdEfpMailmanageMapper.insert(cEfpMailmanage);
        }
        //公众号发送模板消息
        String pubOpenId = tableJsonObject.getString("pubOpenId");
        if (!StringUtils.isEmpty(pubOpenId)) {
            BigDecimal jshj = efpInvoice.getHjje().add(efpInvoice.getHjse());
            DecimalFormat df = new DecimalFormat("##0.00");
            Date dt = new Date();
            bsnAsync.tmpMsgPush(logPath, pubOpenId, efpInvoice.getFpdm(), efpInvoice.getFphm(), efpInvoice.getXfmc(), efpInvoice.getGfmc(), df.format(jshj), DateUtils.getFormatString("yyyy-MM-dd_HH:mm:ss", efpInvoice.getCreateTime()));
        }


        //公众号发送短信通知
        String pubPhone = tableJsonObject.getString("pubPhone");
        String smstype = efpNsrDab.getSmstype();
        HashMap<String, Object> surpluss = efpInvoiceCopyMapper.selectSmsOrderbyNsrsbh(efpInvoice.getXfsh());
        // pubPhone="13612820346";
        //*BsnAsync.postRequest("13612820346","ppp","110746a286e211e7b7977cd30asbbca00a","mtle45pkTu6M2ui8");
        if (!StringUtils.isEmpty(pubPhone)) {
            if (surpluss != null && Integer.parseInt(surpluss.get("sms_num").toString()) + Integer.parseInt(surpluss.get("sms_givenum").toString()) > 0) {
                if (smstype.equals("0")) {
                    String[] parm5 = {efpInvoiceCopy.getParm5()};
                    /*发送开票短信*/
                    bsnAsync.sendMsg(logPath, pubPhone, "193958", efpInvoice.getId(), parm5, efpInvoiceCopy.getParm1());
                    Integer smsNumber = Integer.parseInt(surpluss.get("sms_num").toString()) + Integer.parseInt(surpluss.get("sms_givenum").toString());
                    if (smsNumber == 30) {
                        String[] parms = {String.valueOf("30")};
                        //提示发送短信数量剩余多少
                        bsnAsync.sendMsg(logPath, pubPhone, "319288", efpInvoice.getId(), parms, efpInvoiceCopy.getParm1());
                    }
                }
            } else {
                efpNsrDabMapper.updateByPrimary(efpNsrDab.getNsrsbh());
            }
        }

        //固定推送
        String kprId = tableJsonObject.getString("kprId");
        String Alipaystatus = tableJsonObject.getString("Alipaystatus");
        String sendUrl = tableJsonObject.getString("sendUrl");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataTime = df.format(efpInvoice.getCreateTime());
        dataMaps.put("id", efpInvoiceCopy.getId());
        dataMaps.put("kprId", kprId);
        dataMaps.put("fpdm", efpInvoice.getFpdm());
        dataMaps.put("fphm", efpInvoice.getFphm());
        dataMaps.put("kptime", dataTime);
        dataMaps.put("pdfUrl", pdfUrl);
        dataMaps.put("sendUrl", sendUrl);
        dataMaps.put("email", email);
        dataMaps.put("gfmc", efpInvoice.getGfmc());
        dataMaps.put("parm5", efpInvoiceCopy.getParm5());
        String hjses = String.valueOf(efpInvoice.getHjje().add(efpInvoice.getHjse()));
        dataMaps.put("hjje", hjses);
        deleteFiles(files);
      //  dataMaps.put("Alipaystatus", Alipaystatus);
        return dataMaps;
    }
    private  void deleteFiles(Set<String> files){
        for(String s:files){
            File f=new File(s);
            if(f.exists()){
                f.delete();
            }
            //System.out.println(s);
        }
    }

    public ConResult createZyfp(String qqlsh, String zp, ICore iCore) throws Exception {

        HashMap<String, String> efpInvoicezp = iCore.transactionalCreateZyfp(qqlsh, zp);


        ConResult conResult = new ConResult(1, efpInvoicezp.get("fphm") + "&" + efpInvoicezp.get("fpdm"));

        String czyid = efpInvoicezp.get("czyid");
        /*编写websocket*/
        WebSocketzpUtils ss = new WebSocketzpUtils();
        ss.sendMessage(czyid, JSON.toJSONString(conResult));
        return new ConResult();
    }


    @Transactional
    public HashMap<String, String> transactionalCreateZyfp(String qqlsh, String Json) {

        HashMap<String, String> dataMaps = new HashMap<String, String>();
        EfpZpInvoice efpZpInvoice = new EfpZpInvoice();
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(Json).getJSONObject("fpxx");
        } catch (Exception e) {
            throw new BusinessException(3060, "json格式错误:" + e.toString());
        }
        String fpdm = jsonObject.getString("lbdm");
        String fphm = jsonObject.getString("fphm");
        efpZpInvoice.setFpdm(fpdm);
        efpZpInvoice.setFphm(fphm);

        String kprqString = jsonObject.getString("kprq");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        Date kprq;
        try {
            kprq = simpleDateFormat.parse(kprqString);
        } catch (Exception e) {
            throw new BusinessException(3060, "json开票日期错误:" + e.toString());
        }
        efpZpInvoice.setKprq(kprq);
        String mw = jsonObject.getString("mw");
        efpZpInvoice.setMw(mw);
        String bz = jsonObject.getString("bz");
        efpZpInvoice.setBz(bz);
        BigDecimal hjje = jsonObject.getBigDecimal("hjje");
        efpZpInvoice.setHjje(hjje);
        String hjseString = jsonObject.getString("hjse").replace("￥", "");
        BigDecimal hjse = new BigDecimal(hjseString);
        efpZpInvoice.setHjse(hjse);
        String kpr = jsonObject.getString("kpr");
        efpZpInvoice.setKpr(kpr);
        String fhr = jsonObject.getString("fhr");
        efpZpInvoice.setFhr(fhr);
        String skr = jsonObject.getString("skr");
        efpZpInvoice.setSkr(skr);
        String gfmc = jsonObject.getString("gfmc");
        efpZpInvoice.setGfmc(gfmc);
        String gfsh = jsonObject.getString("gfsh");
        efpZpInvoice.setGfsh(gfsh);
        String gfdzdh = jsonObject.getString("gfdzdh");
        efpZpInvoice.setGfdzDh(gfdzdh);
        String gfyhzh = jsonObject.getString("gfyhzh");
        efpZpInvoice.setGfyhmcYhzh(gfyhzh);
        String xfmc = jsonObject.getString("xfmc");
        efpZpInvoice.setXfmc(xfmc);
        String xfsh = jsonObject.getString("xfsh");
        efpZpInvoice.setXfsh(xfsh);
        String xfdzdh = jsonObject.getString("xfdzdh");
        efpZpInvoice.setXfdzDh(xfdzdh);
        String xfyhzh = jsonObject.getString("xfyhzh");
        efpZpInvoice.setXfyhmcYhzh(xfyhzh);
        String jym = jsonObject.getString("jym");
        efpZpInvoice.setFpJym(jym);
        String jqbm = jsonObject.getString("qrm");
        efpZpInvoice.setJqbh(jqbm);

        JSONArray jsonArray = jsonObject.getJSONArray("list");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject temp = jsonArray.getJSONObject(0);
            EfpZpInvoiceQd efpZpInvoiceQd = new EfpZpInvoiceQd();
            String wpmc = temp.getString("hwmc");
            String wpxh = temp.getString("ggxh");
            String wpdw = temp.getString("dw");

            String slString = temp.getString("sl").trim();
            if (!StringUtils.isEmpty(slString)) {
                BigDecimal sl = new BigDecimal(slString);
                efpZpInvoiceQd.setSl(sl);
            }

            String djString = temp.getString("dj").trim();
            if (!StringUtils.isEmpty(djString)) {
                BigDecimal dj = new BigDecimal(djString);
                efpZpInvoiceQd.setDj(dj);
            }

            String jeString = temp.getString("je").trim();
            BigDecimal je = new BigDecimal(jeString);
            String slvString = temp.getString("slv").trim();
            BigDecimal slv = new BigDecimal(slvString);
            String seString = temp.getString("se").trim();
            BigDecimal se = new BigDecimal(seString);

            efpZpInvoiceQd.setFpdm(fpdm);
            efpZpInvoiceQd.setFphm(fphm);
            efpZpInvoiceQd.setWpMc(wpmc);
            efpZpInvoiceQd.setWpXh(wpxh);
            efpZpInvoiceQd.setWpDw(wpdw);
            efpZpInvoiceQd.setJe(je);
            efpZpInvoiceQd.setSlv(slv);
            efpZpInvoiceQd.setSe(se);

            int qdres = efpZpInvoiceQdMapper.insertSelective(efpZpInvoiceQd);
            if (qdres != 1) {
                throw new BusinessException(1);
            }
        }

        EfpZpInvoiceCopy efpZpInvoiceCopy = new EfpZpInvoiceCopy();
        efpZpInvoiceCopy.setQqlsh(qqlsh);
        List<EfpZpInvoiceCopy> efpZpInvoiceCopies = efpZpInvoiceCopyMapper.selectSelective(efpZpInvoiceCopy);
        if (efpZpInvoiceCopies.size() == 0) {
            throw new BusinessException(3048, "流水号错误");
        }

        efpZpInvoice.setCzyId(efpZpInvoiceCopies.get(0).getParm2());
        efpZpInvoice.setPhoneNo(efpZpInvoiceCopies.get(0).getParm1());
        int vores = efpZpInvoiceMapper.insertSelective(efpZpInvoice);
        if (vores != 1) {
            throw new BusinessException(1);
        }


        efpZpInvoiceCopy.setId(efpZpInvoiceCopies.get(0).getId());
        efpZpInvoiceCopy.setFpdm(fpdm);
        efpZpInvoiceCopy.setFphm(fphm);
        efpZpInvoiceCopy.setBz(3);
        efpZpInvoiceCopy.setKpresult("开票成功");
        int cores = efpZpInvoiceCopyMapper.updateByPrimaryKeySelective(efpZpInvoiceCopy);
        if (cores != 1) {
            throw new BusinessException(1);
        }


        /*  *//**//**
         * 处理票卷数据
         *//**//**/
        List<EfpInvoiceRoll> efpInvoiceRolls = efpInvoiceRollMapper.select1(xfsh);
        if (efpInvoiceRolls.size() != 1) {
            throw new BusinessException(3060, "卷票不唯一");
        }
        EfpInvoiceRoll efpInvoiceRoll = efpInvoiceRolls.get(0);
        EfpInvoiceRoll invoiceRollTemp = new EfpInvoiceRoll();
        if (efpInvoiceRoll.getFpIn() == Integer.parseInt(fphm)) {
            invoiceRollTemp.setFpIn(efpInvoiceRoll.getFpIn() + 1);
            invoiceRollTemp.setRemainNum(efpInvoiceRoll.getRemainNum() - 1);
            invoiceRollTemp.setId(efpInvoiceRoll.getId());
            int res = efpInvoiceRollMapper.updateByPrimaryKeySelective(invoiceRollTemp);
            if (res != 1) {
                throw new BusinessException(1);
            }
        } else {
            invoiceRollTemp.setFpIn(Integer.parseInt(fphm) + 1);
            invoiceRollTemp.setId(efpInvoiceRoll.getId());
            invoiceRollTemp.setRemainNum(efpInvoiceRoll.getFpEd() - Integer.parseInt(fphm));
            int res = efpInvoiceRollMapper.updateByPrimaryKeySelective(invoiceRollTemp);
            if (res != 1) {
                throw new BusinessException(1);
            }
        }
        long fpin = efpInvoiceRoll.getFpIn().longValue();
        long fped = efpInvoiceRoll.getFpEd().longValue();
        if (fpin == fped) {
            int res = efpInvoiceRollMapper.deleteByPrimaryKey(efpInvoiceRoll.getId());
            if (res != 1) {
                throw new BusinessException(1);
            }
        }
        /*返回数据*/
        dataMaps.put("fphm", fphm);
        dataMaps.put("xfsh", xfsh);
        dataMaps.put("id", efpInvoiceRolls.get(0).getId());
        dataMaps.put("fpdm", fpdm);
        dataMaps.put("czyid", efpZpInvoiceCopies.get(0).getParm2());
        return dataMaps;
    }

    public ConResult financeSeal(String logFilePath, MultipartFile fileResPdf, String etpTaxId) throws IOException {
        CertUploadData certUploadData = new CertUploadData();
        certUploadData.setEtpTaxId(etpTaxId);
        List<CertUploadData> certUploadDataList = certUploadDataMapper.selectSelective(certUploadData);
        if (certUploadDataList == null || certUploadDataList.size() == 0 || certUploadDataList.get(0)
                .getFinanceSealUrl() == null || certUploadDataList.get(0).getFinanceSealUrl().length() == 0) {
            throw new BusinessException(3061, "财务专用章未上传");
        }

        long timeStamp = System.currentTimeMillis();
//
        //获取文件名称
        String fileName = fileResPdf.getOriginalFilename();
        //获取pdf旧文件存放路径
        String pdfPathOld = this.getClass().getResource("/").getPath() + "outFile/" + timeStamp + "old" + fileName;
//新pdf路径
        String pdfPath = this.getClass().getResource("/").getPath() + "outFile/" + timeStamp + "new" + fileName;
        //        ketstore文件路径
        String caFilePath = this.getClass().getResource("/").getPath() + "downFile/" + etpTaxId + ".pfx";
//        ca图片路径
        String pngPath = this.getClass().getResource("/").getPath() + "downFile/" + etpTaxId + "finan.png";
        //上传文件
        File targetFile = new File(pdfPathOld);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        //保存
        try {
            fileResPdf.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(3062, "pdf模板下载失败");
        }
        //        下载ca与图片至本地downFile目录
        EfpCa efpCa = downCaFinance(etpTaxId, logFilePath);
        String password = efpCa.getCaPassword();
        ConResult conResult = new ConResult();

        //新生成的pdf
        //签章

        try {
            KeyStore ks = PdfUtils.signCheck(caFilePath, password);
            String aliases = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
            //证书链
            Certificate[] chain = ks.getCertificateChain(aliases);
            SignPdf.createFinanceSeal(pdfPathOld, pngPath, pdfPath, chain, pk, "sign", "Ghent");
        } catch (Exception e) {
            throw new BusinessException(3063, "CA签章失败");
        }
        InputStream inputStream = new FileInputStream(pdfPath);
        Calendar cale = Calendar.getInstance();
        Integer year = cale.get(Calendar.YEAR);
        Integer month = cale.get(Calendar.MONTH) + 1;
        Integer day = cale.get(Calendar.DATE);
        String pdfPathNew = Constant.CERTPDF  + "/" +  year.toString() + "/" + month.toString() + "/" + day.toString() + "/" + etpTaxId;
        String pdfNameNew = timeStamp + "new" + fileName;
        //将pdf文件上传到文件服务器
        try {
            FileStorageUtils.uploadFile( pdfPathNew, pdfNameNew, inputStream);
        } catch (Exception e) {
            throw new BusinessException(3064, "pdf文件上传失败");
        } finally {
            if (inputStream != null) inputStream.close();
        }
        String pdfUrl = Constant.PDFFILEURL + pdfPathNew + "/" + pdfNameNew;
        conResult.getMap().put("pdfUrl", pdfUrl);
        return conResult;
    }

    private List<EfpInvoiceQd> getInvoiceQdsFromKpJson(String kpJson, HashMap<String, String> dataMap) {
        List<EfpInvoiceQd> efpInvoiceQds = new ArrayList<EfpInvoiceQd>();
        try {
            JSONObject jsonObject = JSON.parseObject(kpJson);
            JSONArray jsonArray = jsonObject.getJSONArray("sps");
            for (int i = 0; i < jsonArray.size(); i++) {
                int j = i + 1;
                JSONObject obj = jsonArray.getJSONObject(i);
                EfpInvoiceQd invoiceQd = new EfpInvoiceQd();
                invoiceQd.setWpMc(obj.getString("spmc"));
                dataMap.put("wpmc" + j, invoiceQd.getWpMc());
                invoiceQd.setSpbm(obj.getString("spbm"));
                if (obj.getString("ggxh") != null) {
                    invoiceQd.setWpXh(obj.getString("ggxh"));
                    dataMap.put("ggxh" + j, invoiceQd.getWpXh());
                }
                if (obj.getString("dw") != null) {
                    invoiceQd.setWpDw(obj.getString("dw"));
                    dataMap.put("dw" + j, invoiceQd.getWpDw());
                }
                if (!StringUtils.isEmpty(obj.getString("dj"))) {
                    invoiceQd.setDj(new BigDecimal(obj.getString("dj")));
                    dataMap.put("dj" + j, invoiceQd.getDj().toString());
                }
                if (!StringUtils.isEmpty(obj.getString("sl"))) {
                    invoiceQd.setSl(new BigDecimal(obj.getString("sl")));
                    dataMap.put("sl" + j, invoiceQd.getSl().toString());
                }
                if (!StringUtils.isEmpty(obj.getString("je"))) {
                    invoiceQd.setJe(new BigDecimal(obj.getString("je")));
                    double je = GeralBsiness.getTwoDecimal(invoiceQd.getJe());
                    DecimalFormat df = new DecimalFormat("##0.00");
                    dataMap.put("je" + j, df.format(je));
                }
                if (!StringUtils.isEmpty(obj.getString("se"))) {
                    invoiceQd.setSe(new BigDecimal(obj.getString("se")));
                    double se = GeralBsiness.getTwoDecimal(invoiceQd.getSe());
                    DecimalFormat df = new DecimalFormat("##0.00");
                    dataMap.put("se" + j, df.format(se));
                }
                if (!StringUtils.isEmpty(obj.getString("slv"))) {
                    invoiceQd.setSlv(new BigDecimal(obj.getString("slv")));
                    float slv = invoiceQd.getSlv().floatValue();
                    int slv2 = (int) (slv * 100);
                    dataMap.put("slv" + j, slv2 + "%");
                }
                efpInvoiceQds.add(invoiceQd);
            }
        } catch (Exception e) {
            throw new BusinessException(3036, "json组装qd表失败:" + e.toString());
        }
        return efpInvoiceQds;
    }

    private EfpInvoice updateInvoiceFromKpJson(EfpInvoice invoice, String kpJson, HashMap<String, String> dataMap) {
        try {
            JSONObject jsonObject = JSON.parseObject(kpJson);
            invoice.setXfmc(jsonObject.getString("nsrmc"));
            dataMap.put("xfmc", invoice.getXfmc());
            invoice.setXfsh(jsonObject.getString("nsrsbh"));
            dataMap.put("xfsh", invoice.getXfsh());
            if (!StringUtils.isEmpty(jsonObject.getString("dzdh"))) {
                invoice.setXfdzDh(jsonObject.getString("dzdh"));
                dataMap.put("xfdzdh", invoice.getXfdzDh());
            }
            if (!StringUtils.isEmpty(jsonObject.getString("yhzh"))) {
                invoice.setXfyhmcYhzh(jsonObject.getString("yhzh"));
                dataMap.put("xfyhzh", invoice.getXfyhmcYhzh());
            }

            invoice.setGfmc(jsonObject.getString("gfmc"));
            dataMap.put("gfmc", invoice.getGfmc());
            if (!StringUtils.isEmpty(jsonObject.getString("gfnsrsbh"))) {
                invoice.setGfsh(jsonObject.getString("gfnsrsbh"));
                dataMap.put("gfsh", invoice.getGfsh());
            }
            if (!StringUtils.isEmpty(jsonObject.getString("gfdzdh"))) {
                invoice.setGfdzDh(jsonObject.getString("gfdzdh"));
                dataMap.put("gfdzdh", invoice.getGfdzDh());
            }
            if (!StringUtils.isEmpty(jsonObject.getString("gfyhzh"))) {
                invoice.setGfyhmcYhzh(jsonObject.getString("gfyhzh"));
                dataMap.put("gfyhzh", invoice.getGfyhmcYhzh());
            }
            invoice.setKpr(jsonObject.getString("kpr"));
            dataMap.put("kpr", invoice.getKpr());
            invoice.setSkr(jsonObject.getString("skr"));
            dataMap.put("skr", invoice.getSkr());
            invoice.setFhr(jsonObject.getString("fhr"));
            dataMap.put("fhr", invoice.getFhr());
            invoice.setCzyId(jsonObject.getString("czyId"));
        } catch (Exception e) {
            throw new BusinessException(3036, "json组装invoice表失败:" + e.toString());
        }
        return invoice;
    }

    private String uploadPdf(String fpdm, String fphm, String zjm, EfpNsrDab nsrdab) {
        String result = "";
        String fileName = fpdm + fphm + ".pdf";

        byte[] bytes = FileBase64.getByteFromBinary(zjm);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        InputStream inputStream = new BufferedInputStream(bais);
        try {

            int folderName = 1;
            Date  currentDate = new Date();
            String path = FileStorageUtils.genUploadPath(nsrdab.getSqRq(),currentDate,nsrdab.getNsrsbh(),nsrdab.getCityCode());
            String pdfUrl = FileStorageUtils.getHttpUrl(path,fileName);
            FileStorageUtils.uploadFile(path, fileName, inputStream);
            //存储fileRepository
            EfpFileRepository efpFileRepository = new EfpFileRepository();
            efpFileRepository.setFpdm(fpdm);
            efpFileRepository.setFphm(fphm);
            efpFileRepository.setUrl(pdfUrl);
            efpFileRepository.setNsrsbh(nsrdab.getNsrsbh());
            efpFileRepository.setFileNumber(folderName);
            if (efpFileRepositoryMapper.insertSelective(efpFileRepository) != 1) {
                throw new BusinessException(1);
            }
            result = pdfUrl;
        } catch (IOException e) {
            throw new BusinessException(3046, "上传pdf文件错误:" + e.toString());
        } finally {
            try {
                bais.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    private String uploadPdf(String fpdm, String fphm, String fileName, String outPdfPath, EfpNsrDab nsrda) {
        return uploadPdf(fpdm,fphm,fileName,outPdfPath,nsrda,new Date());
    }

    private String uploadPdf(String fpdm, String fphm, String fileName, String outPdfPath, EfpNsrDab nsrdab,Date date) {
        try {
            if(date==null){
                date=new Date();
            }
            //// 处理图片
            String image = TestImage.pdf2Image(outPdfPath, this.getClass().getResource("/").getPath() + "outFile/", 95);
            InputStream fileImage = new FileInputStream(image);
            InputStream inputStream = new FileInputStream(outPdfPath);
            String result = "";

            try {
                String sq_rq = DateUtils.getFormatString("yyyy-MM-dd", nsrdab.getSqRq());
                String regYearAndMonth = sq_rq.split("-")[0] + sq_rq.split("-")[1];
                String regDay = sq_rq.split("-")[2];

                String kp_rq = DateUtils.getFormatString("yyyy-MM-dd",date);
                String kpYearAndMonth = kp_rq.split("-")[0] + kp_rq.split("-")[1];
                String kpDay = kp_rq.split("-")[2];

                //默认文件夹从1开始
                int folderName = 1;
                String path = nsrdab.getCityCode() + "/"
                        + regYearAndMonth + "/" + regDay + "/"
                        + nsrdab.getNsrsbh() + "/" + kpYearAndMonth + "/" + kpDay + "/"
                        + folderName + "/";

                //ToDo
                FileStorageUtils.uploadFile(path, fileName + ".jpg", fileImage);
                FileStorageUtils.uploadFile(path, fileName + ".pdf", inputStream);
                String pdfUrl = Constant.PDFFILEURL + nsrdab.getCityCode() + "/"
                        + regYearAndMonth + "/" + regDay + "/"
                        + nsrdab.getNsrsbh() + "/" + kpYearAndMonth + "/" + kpDay + "/"
                        + folderName + "/" + fileName + ".pdf";

                //LogUtils.writeLogFileName("hbDZp.txt", "ftp生成路径" + pdfUrl);
                EfpFileRepository efpFileRepository = new EfpFileRepository();
                efpFileRepository.setFpdm(fpdm);
                efpFileRepository.setFphm(fphm);
                efpFileRepository.setUrl(pdfUrl);
                efpFileRepository.setNsrsbh(nsrdab.getNsrsbh());
                efpFileRepository.setFileNumber(folderName);

               if (efpFileRepositoryMapper.insertSelective(efpFileRepository) != 1) {
                    throw new BusinessException(1);
                }
                result = pdfUrl;
            } catch (IOException e) {
                this.logOutput(new BusinessException(3050, "上传pdf文件错误:" + e.toString()));
                throw new BusinessException(3050, "上传pdf文件错误:" + e.toString());
            } finally {
                try {
                    inputStream.close();
                    fileImage.close();
                    // 创建文件对象   image = 文件路径
                    File file=new File(image);
                    //如果文件存在
                    if(file.exists()){
                        // 删除文件
                        file.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String uploadhcPdf(String fpdm, String fphm, String fileName, String outPdfPath, EfpNsrDab nsrdab) {
        try {
            String image = TestImage.pdf2Image(outPdfPath, this.getClass().getResource("/").getPath() + "outFile/", 95);
            InputStream fileImage = new FileInputStream(image);
            InputStream inputStream = new FileInputStream(outPdfPath);
            String result = "";
            try {
                Date currentDate = new Date();
                String path = FileStorageUtils.genUploadPath(nsrdab.getSqRq(), currentDate, nsrdab.getNsrsbh(),nsrdab.getCityCode());
                String pdfUrl = FileStorageUtils.getHttpUrl(path,fileName);
                FileStorageUtils.uploadFile(path, fileName + ".jpg", fileImage);
                FileStorageUtils.uploadFile(path, fileName + ".pdf", inputStream);
                result = pdfUrl;
            } catch (IOException e) {
                throw new BusinessException(3050, "上传pdf文件错误:" + e.toString());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public EfpCa downCa(String nsrsbh, String logPath, int leve) throws IOException {
        if (leve == 0) {
            //3 2 1
            this.logOutput(new BusinessException("jpg 文件下载失败"));
            throw new BusinessException("jpg 文件下载失败");
        }
        EfpCa efpCa = new EfpCa();
        efpCa.setNsrsbh(nsrsbh);
        List<EfpCa> efpCas = efpCaMapper.selectSelective(efpCa);
        if (efpCas.size() == 0) {
           // LogUtils.writeLogFile(logPath, "ca表查询失败", nsrsbh);
            this.logOutput(new BusinessException(3048, "ca表查询失败"+"税号"+nsrsbh));
            throw new BusinessException(3048, "ca表查询失败");
        }
        efpCa = efpCas.get(0);
        //判断时间差是否在一天之内
        Date date = efpCa.getUpdateTime();
       if (System.currentTimeMillis() - date.getTime() > 86400000) {
            //深圳ca下载ca文件
            String path = this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx";

            String password = CaUtils.downCaBynsrsbh(logPath, nsrsbh, path);
            //返回efpCa时，密码为最新
            efpCa.setCaPassword(password);
            //修改密码和更新时间
            EfpCa tmpEfpCa = new EfpCa();
            tmpEfpCa.setId(efpCa.getId());
            tmpEfpCa.setCaPassword(password);
            tmpEfpCa.setUpdateTime(new Date());
            int res = efpCaMapper.updateByPrimaryKeySelective(tmpEfpCa);
            if (res != 1) {
                throw new BusinessException(1);
            }
            //上传ca文件至服务器
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx");
                FileStorageUtils.uploadFile(logPath, nsrsbh + ".pfx", inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
               // LogUtils.writeLogFile(logPath, "3010 ca文件" + this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx" + "未找到:" + e.toString());
                this.logOutput(new BusinessException(3048, "3010 ca文件" + this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx" + "未找到:" + e.toString()));
                throw new BusinessException(3010, "ca文件上传失败");
            }
            if (inputStream != null) inputStream.close();
        } else {
            //检查或下载ca文件
            try {
                FileStorageUtils.downFile(this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx", efpCa.getCaPath() + "/" + nsrsbh + ".pfx");
              //  LogUtils.writeLogFile(logPath, "下载ca错误", System.currentTimeMillis() + "", date.getTime() + "", (System.currentTimeMillis() - date.getTime()) + "--》" + leve);
            } catch (Exception e) {
                e.printStackTrace();
               // LogUtils.writeLogFile(logPath, "3011 ca文件下载失败");
                this.logOutput(new BusinessException(3011, "ca文件下载失败"));
                throw new BusinessException(3011, "ca文件下载失败");
            }
        }
       try {
            //todo lzw
            FileStorageUtils.downFile(this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".png", efpCa.getCaPath() + "/" + nsrsbh + ".png");
            //this.LOGGER.error("下载png 错误", System.currentTimeMillis() + "", date.getTime() + "", (System.currentTimeMillis() - date.getTime()) + "--》" + leve);
        } catch (Exception e) {
            //*e.printStackTrace();*//
            //LogUtils.writeLogFile(logPath, "3012 png文件下载失败");
            this.logOutput(new BusinessException(3012, "png文件下载失败"));
            throw new BusinessException(3012, "png文件下载失败");
        }
        return efpCa;
    }

    //tcis推送平推票
    //下载ca与印章图片
    EfpCa downCa(String nsrsbh, String logPath) throws IOException {
        return downCa(nsrsbh, logPath, 3);
    }

    //下载ca与财务章图片
    EfpCa downCaFinance(String nsrsbh, String logPath) throws IOException {
        EfpCa efpCa = new EfpCa();
        efpCa.setNsrsbh(nsrsbh);
        List<EfpCa> efpCas = efpCaMapper.selectSelective(efpCa);
        if (efpCas.size() == 0) {
            throw new BusinessException(3048, "ca表查询失败");
        }
        efpCa = efpCas.get(0);
        //判断时间差是否在一天之内
        Date date = efpCa.getUpdateTime();
        if (System.currentTimeMillis() - date.getTime() > 86400000) {
            //深圳ca下载ca文件
            String path = this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx";
            String password = CaUtils.downCaBynsrsbh(logPath, nsrsbh, path);
            //返回efpCa时，密码为最新
            efpCa.setCaPassword(password);
            //修改密码和更新时间
            EfpCa tmpEfpCa = new EfpCa();
            tmpEfpCa.setId(efpCa.getId());
            tmpEfpCa.setCaPassword(password);
            tmpEfpCa.setUpdateTime(new Date());
            int res = efpCaMapper.updateByPrimaryKeySelective(tmpEfpCa);
            if (res != 1) {
                throw new BusinessException(1);
            }
            //上传ca文件至服务器
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx");
                FileStorageUtils.uploadFile(efpCa.getCaPath(), nsrsbh + ".pfx", inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                   throw new BusinessException(3010, "ca文件上传失败");
            } finally {
                if (inputStream != null) inputStream.close();
            }
        } else {
            try {
                String pa = efpCa.getCaPath() + "/" + nsrsbh + ".pfx";
                FileStorageUtils.downFile(this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + ".pfx",  pa);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(3011, "ca文件下载失败");
            }
        }
        try {
            CertUploadData certUploadData = new CertUploadData();
            certUploadData.setEtpTaxId(nsrsbh);
            List<CertUploadData> certUploadDataList = certUploadDataMapper.selectSelective(certUploadData);
            if(certUploadDataList == null || certUploadDataList.size() == 0){
                throw new BusinessException(3012, "png文件不存在");
            }
            String finan = certUploadDataList.get(0).getFinanceSealUrl().split("invoicefileservice.bjzsxx.com/")[1];
            FileStorageUtils.downFile(this.getClass().getResource("/").getPath() + "downFile/" + nsrsbh + "finan.png",finan);
        } catch (Exception e) {
            throw new BusinessException(3012, "png文件下载失败");
        }
        return efpCa;
    }

    public String toPDF(String json) throws Exception {
        JSONObject jsonObject = JSON.parseObject(json);
        HashMap<String, String> dataMap = new HashMap<String, String>();
        PdfUtils.composeMapToPdf(jsonObject, dataMap);
        System.out.println(jsonObject.toJSONString());
        EfpInvoice efpInvoice = new EfpInvoice();
        PdfUtils.composeInvoiceToDb(jsonObject, efpInvoice);
        String qrCodeContent = jsonObject.getString("qrm");
        String QrCodeImgPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + "QrCode.png";
        QrCodeUtils.proQrCodeImg(qrCodeContent, 150, 150, QrCodeImgPath);
        String fontPath = this.getClass().getResource("/").getPath() + "pdfSign/C9.ttf";
        EfpCa efpCa = downCa(efpInvoice.getXfsh(), this.getClass().getResource("/").getPath() + "log/ca.txt");
        String tmplatePdfPath = this.getClass().getResource("/").getPath() + "pdfTemplate/" + efpCa.getTemplateName() + ".pdf";
        String keyStorePath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".pfx";
        String signImgPath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".png";
        String password = efpCa.getCaPassword();
        String logPath = "e://log//";
        int num = 1;
        String tmpOutPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + num + ".pdf";
        String outPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + ".pdf";

        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        if (jsonArray.size() <= 5) {
            try {
                KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
                String aliases = ks.aliases().nextElement();
                PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
                Certificate[] chain = ks.getCertificateChain(aliases);
                PdfUtils.create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, chain, pk, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3073, "版式文件失败" + e.toString());
            }
        } else {
            try {
                PdfUtils.create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, null, null, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3073, "版式文件失败" + e.toString());
            }
        }
        if (jsonArray.size() > 5) {
            tmplatePdfPath = this.getClass().getResource("/").getPath() + "pdfTemplate/商品清单模板.pdf";
            List<String> file = new ArrayList<String>();
            String filename = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm();
            PDFDetailedUtil.create(jsonObject, tmplatePdfPath, fontPath, () -> {
                file.add("1");
                return filename + (file.size() + 1) + ".pdf";
            });
            num = file.size() + 1;
        }

        if (num > 1) {//合并文件
            JSONArray filePaths = new JSONArray();
            List<Rectangle> reck = new ArrayList<Rectangle>();
            for (int i = 1; i <= num; i++) {
                if (i == 1) {
                    reck.add(new Rectangle(480, 0, 587, 104));
                } else {
                    // //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
                    reck.add(new Rectangle(20, 0, 250, 104));
                }
                filePaths.add(this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + i + ".pdf");
            }
            PdfUtils.mergePdfFiles(logPath, filePaths, outPdfPath);
            //单独进行签证
            KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
            String aliases = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
            //证书链
            Certificate[] chain = ks.getCertificateChain(aliases);
            for (int i = 0; i < reck.size(); i++) {
                PdfUtils.visa(outPdfPath, outPdfPath + ".pdf", chain, pk, signImgPath, "sign", "Ghent", reck.get(i),
                        i + 1);
                outPdfPath += ".pdf";
            }
        } else {
            outPdfPath = tmpOutPdfPath;
        }
        return outPdfPath;
    }


    @Transactional
    public HashMap<String, String> Createpdf(String logPath, String dzfpResult, String qqlsh) throws Exception {

        //查询订单是否存在
        EfpInvoiceCopy efpInvoiceCopy = new EfpInvoiceCopy();
        efpInvoiceCopy.setParm2(qqlsh);
        List<EfpInvoiceCopy> efpInvoiceCopys = efpInvoiceCopyMapper.selectSelective(efpInvoiceCopy);
        if (efpInvoiceCopys.size() == 0) {
            throw new BusinessException(3046, "临时表未查找到数据");
        }
        efpInvoiceCopy = efpInvoiceCopys.get(0);
        //解析json数据
        JSONObject jsonObject = JSON.parseObject(dzfpResult).getJSONObject("dzfpresult");

        HashMap<String, String> dataMap = new HashMap<String, String>();
        HashMap<String, String> dataMaps = new HashMap<String, String>();

        PdfUtils.composeMapToPdf(jsonObject, dataMap);

        List<EfpInvoiceQd> efpInvoiceQds = new ArrayList<EfpInvoiceQd>();

        PdfUtils.composeInvoiceQdToDb(jsonObject, efpInvoiceQds);


        EfpInvoice efpInvoice = new EfpInvoice();

        PdfUtils.composeInvoiceToDb(jsonObject, efpInvoice);
        PdfUtils.composeInvoiceToDb(efpInvoiceCopy, efpInvoice);

        String kpJson = efpInvoiceCopy.getKpJson();
        JSONObject tableJsonObject = JSON.parseObject(kpJson);
        //获取邮件
        String emailAccount = tableJsonObject.getString("emailAccount");


        EfpNsrDab efpNsrDab = new EfpNsrDab();
        efpNsrDab.setNsrsbh(efpInvoice.getXfsh());

        List<EfpNsrDab> efpNsrDabs = efpNsrDabMapper.selectSelective(efpNsrDab);
        if (efpNsrDabs.size() == 0) {
            throw new BusinessException(3048, "纳税人表信息错误");
        }
        efpNsrDab = efpNsrDabs.get(0);
//        二维码内容
        String qrCodeContent = jsonObject.getString("qrm");
//        生成后的二维码图片路径
        String QrCodeImgPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + "QrCode.png";
        try {
            QrCodeUtils.proQrCodeImg(qrCodeContent, 150, 150, QrCodeImgPath);
        } catch (Exception e) {
            /* LogUtils.writeLogFile(this.getClass().getResource("/").getPath() + "log/createDZp.txt", "二维码图片操作失败" + e.toString());*/
            throw new BusinessException(3045, "二维码图片操作失败");
        }
//        字体文件路径
        String fontPath = this.getClass().getResource("/").getPath() + "pdfSign/C9.ttf";
//        下载ca与图片至本地downFile目录
        EfpCa efpCa = downCa(efpInvoice.getXfsh(), this.getClass().getResource("/").getPath() + "log/ca.txt");
//        pdf发票模板
        String tmplatePdfPath = this.getClass().getResource("/").getPath() + "pdfTemplate/" + efpCa.getTemplateName() + ".pdf";

//        ketstore文件路径
        String keyStorePath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".pfx";
//        ca图片路径
        String signImgPath = this.getClass().getResource("/").getPath() + "downFile/" + efpInvoice.getXfsh() + ".png";

        //        ketstore密码
        String password = efpCa.getCaPassword();

        //文件数量下标
        int num = 1;
        //合并前的pdf文件路径
        String tmpOutPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + num + ".pdf";
//         签完章的pdf文件路径
        String outPdfPath = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + ".pdf";

        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        InputStream inputStream;
        if (jsonArray.size() <= 5) {
            try {
                //签名私钥
                KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
                String aliases = ks.aliases().nextElement();
                PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
                //证书链
                Certificate[] chain = ks.getCertificateChain(aliases);
                PdfUtils.create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, chain, pk, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3073, "版式文件失败" + e.toString());
            }
        } else {
            //不进行签证操作
            try {
                PdfUtils.create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, tmpOutPdfPath, fontPath, null, null, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3073, "版式文件失败" + e.toString());
            }
        }

        // TODO: 2017/11/2 上传后删除相关文件

        if (jsonArray.size() > 5) {
            StringBuffer sb = new StringBuffer();
            String path = this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm();
            PDFDetailedUtil.create(jsonObject, this.getClass().getResource("/").getPath() + "pdfTemplate/商品清单模板.pdf", fontPath, () -> { sb.append("1");
                        return path + (sb.length() + 1) + ".pdf";
                    });
            num += sb.length();
        }

        if (num > 1) {
            //合并文件
            JSONArray filePaths = new JSONArray();
            List<Rectangle> reck = new ArrayList<Rectangle>();
            for (int i = 1; i <= num; i++) {
                if (i == 1) {
                    reck.add(new Rectangle(480, 0, 587, 104));
                } else {
                    // //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
                    reck.add(new Rectangle(20, 0, 250, 104));
                }
                filePaths.add(this.getClass().getResource("/").getPath() + "outFile/" + efpInvoice.getFpdm() + efpInvoice.getFphm() + i + ".pdf");
            }
            PdfUtils.mergePdfFiles(logPath, filePaths, outPdfPath);
            //单独进行签证
            KeyStore ks = PdfUtils.signCheck(keyStorePath, password);
            String aliases = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
            //证书链
            Certificate[] chain = ks.getCertificateChain(aliases);
            for (int i = 0; i < reck.size(); i++) {
                PdfUtils.visa(outPdfPath, outPdfPath + ".pdf", chain, pk, signImgPath, "sign", "Ghent", reck.get(i),
                        i + 1);
                outPdfPath += ".pdf";
            }
        } else {
            outPdfPath = tmpOutPdfPath;
        }
        //记录邮箱
        CdEfpMailmanage cEfpMailmanage = new CdEfpMailmanage();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        cEfpMailmanage.setId(uuid);
        cEfpMailmanage.setCopyid(efpInvoiceCopy.getId());
        cEfpMailmanage.setFphm(efpInvoice.getFphm());
        cEfpMailmanage.setFpdm(efpInvoice.getFpdm());
        cEfpMailmanage.setCreateTime(new Date());


        String pdfUrl = uploadPdf(efpInvoice.getFpdm(), efpInvoice.getFphm(), efpInvoice.getFpdm() + efpInvoice.getFphm(),
                outPdfPath, efpNsrDabs.get(0),efpInvoice.getKprq());
        String email = emailAccount;
        if (!StringUtils.isEmpty(emailAccount)) {
            BigDecimal jshj = efpInvoice.getHjje().add(efpInvoice.getHjse());
            Date dt = new Date();
            bsnAsync.mailPush(logPath, pdfUrl, DateUtils.getFormatString("yyyy-MM-dd_HH:mm:ss", efpInvoice.getCreateTime()), efpInvoice.getFpdm(), efpInvoice.getFphm(), efpInvoice.getXfmc(), efpInvoice.getGfmc(), jshj.toString(), emailAccount
                    , (resultMes, url, code) -> {
                        // 发送成功  返回
                        if (code == 200) {
                            try {
                                JSONObject o = JSON.parseObject(resultMes);
                                cEfpMailmanage.setParm1(email);
                                cEfpMailmanage.setMessage(resultMes);
                                cEfpMailmanage.setState(o.getInteger("code") == 0 ? 200 : -1);
                            } catch (Exception e) {
                                cEfpMailmanage.setParm1(email);
                                cEfpMailmanage.setMessage(resultMes);
                                cEfpMailmanage.setState(-1);
                            }
                        } else {
                            // 发送失败 返回
                            cEfpMailmanage.setParm1(email);
                            cEfpMailmanage.setMessage(resultMes);
                            cEfpMailmanage.setState(-1);
                        }
                        cEfpMailmanage.setParm2(url);
                        cdEfpMailmanageMapper.insert(cEfpMailmanage);
                    }
            );
        } else {
            cEfpMailmanage.setParm1(emailAccount);
            cEfpMailmanage.setMessage("邮箱为空");
            cEfpMailmanage.setState(0);
            cdEfpMailmanageMapper.insert(cEfpMailmanage);
        }
        //公众号发送模板消息
        String pubOpenId = tableJsonObject.getString("pubOpenId");
        if (!StringUtils.isEmpty(pubOpenId)) {
            BigDecimal jshj = efpInvoice.getHjje().add(efpInvoice.getHjse());
            DecimalFormat df = new DecimalFormat("##0.00");
            Date dt = new Date();
            bsnAsync.tmpMsgPush(logPath, pubOpenId, efpInvoice.getFpdm(), efpInvoice.getFphm(), efpInvoice.getXfmc(), efpInvoice.getGfmc(), df.format(jshj), DateUtils.getFormatString("yyyy-MM-dd_HH:mm:ss", efpInvoice.getCreateTime()));
        }


        //公众号发送短信通知
        String pubPhone = tableJsonObject.getString("pubPhone");
        String smstype = efpNsrDab.getSmstype();
        HashMap<String, Object> surpluss = efpInvoiceCopyMapper.selectSmsOrderbyNsrsbh(efpInvoice.getXfsh());
        if (!StringUtils.isEmpty(pubPhone)) {
            if (surpluss != null && Integer.parseInt(surpluss.get("sms_num").toString()) + Integer.parseInt(surpluss.get("sms_givenum").toString()) > 0) {
                if (smstype.equals("0")) {
                    String[] parm5 = {efpInvoiceCopy.getParm5()};
                    /*发送开票短信*/
                    bsnAsync.sendMsg(logPath, pubPhone, "193958", efpInvoice.getId(), parm5, efpInvoiceCopy.getParm1());
                    Integer smsNumber = Integer.parseInt(surpluss.get("sms_num").toString()) + Integer.parseInt(surpluss.get("sms_givenum").toString());
                    if (smsNumber == 30) {
                        String[] parms = {String.valueOf("30")};
                        //提示发送短信数量剩余多少
                        bsnAsync.sendMsg(logPath, pubPhone, "319288", efpInvoice.getId(), parms, efpInvoiceCopy.getParm1());
                    }
                }
            } else {
                efpNsrDabMapper.updateByPrimary(efpNsrDab.getNsrsbh());
            }
        }

        //固定推送
        String kprId = tableJsonObject.getString("kprId");
        String sendUrl = tableJsonObject.getString("sendUrl");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataTime = df.format(efpInvoice.getCreateTime());
        dataMaps.put("id", efpInvoiceCopy.getId());
        dataMaps.put("kprId", kprId);
        dataMaps.put("fpdm", efpInvoice.getFpdm());
        dataMaps.put("fphm", efpInvoice.getFphm());
        dataMaps.put("kptime", dataTime);
        dataMaps.put("pdfUrl", pdfUrl);
        dataMaps.put("sendUrl", sendUrl);
        dataMaps.put("email", email);
        dataMaps.put("gfmc", efpInvoice.getGfmc());
        dataMaps.put("parm5", efpInvoiceCopy.getParm5());
        String hjses = String.valueOf(efpInvoice.getHjje().add(efpInvoice.getHjse()));
        dataMaps.put("hjje", hjses);
        return dataMaps;
    }

    @Override
    public ConResult applyFinanceSeal(String logFilePath, String parm2) throws Exception{
        File filepdfPathOld = null;
        File filepdfPath = null;
        ConResult conResult;
        try {
            if(parm2 == null || parm2.length()==0){
                throw new BusinessException(3059, "参数parm2不能为空");
            }
            CertPdf certPdf = certPdfMapper.selectByParm2(parm2);
            if(certPdf == null){
                throw new BusinessException(3060, "该订单号查询失败");
            }
            String parm4 = certPdf.getParm4();
            String etpTaxId = certPdf.getNsrsbh();
            String id = certPdf.getId();
            if(parm4 == null || parm4.length()==0){
                throw new BusinessException(3061, "参数parm4不能为空");
            }
            if (certPdf.getParm3().equalsIgnoreCase("0")){
                throw new BusinessException(3062, "该订单已推送成功");
            }
            CertUploadData certUploadData = new CertUploadData();
            certUploadData.setEtpTaxId(etpTaxId);
            List<CertUploadData> certUploadDataList = certUploadDataMapper.selectSelective(certUploadData);
            if (certUploadDataList == null || certUploadDataList.size() == 0 || certUploadDataList.get(0)
                    .getFinanceSealUrl() == null || certUploadDataList.get(0).getFinanceSealUrl().length() == 0) {
                throw new BusinessException(3063, "财务专用章未上传");
            }
            long timeStamp = System.currentTimeMillis();
            String fileName = parm2 + ".pdf";
            //获取pdf旧文件存放路径
            String pdfPathOld = this.getClass().getResource("/").getPath() + "outFile/" + timeStamp + "old" + fileName;
//新pdf路径
            String pdfPath = this.getClass().getResource("/").getPath() + "outFile/" + timeStamp + "new" + fileName;
            //        ketstore文件路径
            String caFilePath = this.getClass().getResource("/").getPath() + "downFile/" + etpTaxId + ".pfx";
//        ca图片路径
            String pngPath = this.getClass().getResource("/").getPath() + "downFile/" + etpTaxId + "finan.png";

            try {
                HttpRequestUtils.downLoad(parm4,pdfPathOld);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(3064, "pdf模板下载失败");
            }

             filepdfPathOld = new File(pdfPathOld);
            if(!filepdfPathOld.exists()){
                throw new BusinessException(3065, "pdf模板下载失败");
            }
            //        下载ca与图片至本地downFile目录
            String password = null;
            try {
                EfpCa efpCa = downCaFinance(etpTaxId, logFilePath);
                password = efpCa.getCaPassword();
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException(3066, "财务章下载失败");
            }

            File filecaFilePath = new File(caFilePath);
            if(!filecaFilePath.exists()){
                throw new BusinessException(3067, "CA证书下载失败");
            }
            File filepngPath = new File(pngPath);
            if(!filepngPath.exists()){
                throw new BusinessException(3068, "财务章下载失败");
            }
            //签章
            try {
                KeyStore ks = PdfUtils.signCheck(caFilePath, password);
                String aliases = ks.aliases().nextElement();
                PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
                //证书链
                Certificate[] chain = ks.getCertificateChain(aliases);
                SignPdf.createFinanceSeal(pdfPathOld, pngPath, pdfPath, chain, pk, "sign", "Ghent");
            } catch (Exception e) {
                throw new BusinessException(3069, "CA签章失败");
            }
             filepdfPath = new File(pdfPath);
            if(!filepdfPath.exists()){
                throw new BusinessException(3070, "电子收据生成失败");
            }
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(pdfPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Calendar cale = Calendar.getInstance();
            Integer year = cale.get(Calendar.YEAR);
            Integer month = cale.get(Calendar.MONTH) + 1;
            Integer day = cale.get(Calendar.DATE);
            String pdfPathNew = Constant.CERTPDF  + "/" +  year.toString() + "/" + month.toString() + "/" + day.toString() + "/" + etpTaxId;
            String pdfNameNew = timeStamp + "new" + fileName;
            //将pdf文件上传到文件服务器 FileStorageUtils.uploadFile( pdfPathNew, pdfNameNew, inputStream);
            try {
                FileStorageUtils.uploadFile( pdfPathNew, pdfNameNew, inputStream);
            } catch (Exception e) {
                throw new BusinessException(3071, "电子收据上传失败");
            } finally {
                if (inputStream != null) try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String pdfUrl = Constant.PDFFILEURL + pdfPathNew + "/" + pdfNameNew;
            //推送
            String ts = String.valueOf(System.currentTimeMillis()/1000);
            String sign = MD5Security.EncodeMD5Hex(Constant.APPID + ts + Constant.TOKEN + "false");
            HashMap hashMap = new HashMap();
            hashMap.put("appID",Constant.APPID);
            hashMap.put("sign",sign);
            hashMap.put("ts",ts);
            hashMap.put("orderid",parm2);
            hashMap.put("url",pdfUrl);
            hashMap.put("status","1");
            String result = HttpRequestUtils.sendPost(Constant.RESULT,hashMap);
            conResult = new ConResult();
            if(result.length()>0){
                JSONObject jsonObject = JSON.parseObject(result);
                if(jsonObject.getInteger("code") == 0){
                    Integer num = null;
                    try {
                         num = certPdfMapper.updateByParm2(id,"0",pdfUrl,"");
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new BusinessException(3072, "电子收据添加失败");
                    }
                    if(num != 1){
                        throw new BusinessException(3073, "电子收据添加失败");
                    }
                    conResult.setCode(1);
                } else {
                    String parm5 = jsonObject.getString("message");
                    certPdfMapper.updateByParm2(id,"1",pdfUrl,parm5);
                    throw new BusinessException(3074, parm5);
                }
            }else {
                throw new BusinessException(3075, "电子收据推送失败");
            }
        } finally {

            if (filepdfPathOld != null &&filepdfPathOld.exists() && filepdfPathOld.isFile()) {
                filepdfPathOld.delete();
            }
            if (filepdfPath != null && filepdfPath.exists() && filepdfPath.isFile()) {
                filepdfPath.delete();
            }

        }
        return conResult;
    }

    @Override
    public ConResult financeSealExamine(String [] ids, String parm3) {
        for (String id : ids){
            File filepdfPathOld = null;
            File filepdfPath = null;
            try {
                CertPdf certPdf = certPdfMapper.selectByPrimaryKey(id);
                if(certPdf == null){
                    throw new BusinessException(3080, id + "收据查询失败");
                }
                String parm3Old = certPdf.getParm3();
                if(parm3Old.equalsIgnoreCase("0")){
                    throw new BusinessException(3081, id + "收据已经推送成功");
                }
                String parm2 = certPdf.getParm2();
//        String pdfUrl = Constant.PDFFILEURL + pdfPathNew + "/" + pdfNameNew;
                //推送
                String ts = String.valueOf(System.currentTimeMillis()/1000);
                String sign = MD5Security.EncodeMD5Hex(Constant.APPID + ts + Constant.TOKEN + "false");
                HashMap hashMap = new HashMap();
                hashMap.put("appID",Constant.APPID);
                hashMap.put("sign",sign);
                hashMap.put("ts",ts);
                hashMap.put("orderid",parm2);
                String parm5 = null;
                if(parm3.equalsIgnoreCase("0")){
    //            重新申请
                    String parm4 = certPdf.getParm4();
                    String etpTaxId = certPdf.getNsrsbh();
                    if(parm4 == null || parm4.length()==0){
                        throw new BusinessException(3061, id + "参数parm4不能为空");
                    }

                    String fileName = parm2 + ".pdf";
                    //获取pdf旧文件存放路径
                    String pdfPathOld = this.getClass().getResource("/").getPath() + "outFile/" + ts + "old" + fileName;
                    //新pdf路径
                    String pdfPath = this.getClass().getResource("/").getPath() + "outFile/" + ts + "new" + fileName;
                    //        ketstore文件路径
                    String caFilePath = this.getClass().getResource("/").getPath() + "downFile/" + etpTaxId + ".pfx";
    //        ca图片路径
                    String pngPath = this.getClass().getResource("/").getPath() + "downFile/" + etpTaxId + "finan.png";
                    parm5 = "pdf模板下载失败";
                    boolean down = false;
                    try {
                        HttpRequestUtils.downLoad1(parm4,pdfPathOld);
                        HttpRequestUtils.downLoad(parm4,pdfPathOld);
                    } catch (Exception e) {
                        down = true;
                        parm5 = e.getMessage();
                    }
                     filepdfPathOld = new File(pdfPathOld);
                    if(!filepdfPathOld.exists() || down){
                        hashMap.put("errmsg",parm5);
                        hashMap.put("status","0");
                        String result = HttpRequestUtils.sendPost(Constant.RESULT,hashMap);
                        if(result.length()>0){
                            JSONObject jsonObject = JSON.parseObject(result);
                            if(jsonObject.getInteger("code") != 0){
                                parm5 = jsonObject.getString("message");
                            }
                            certPdfMapper.updateByParm2(id,"2",null,parm5);
                        }else {
                            throw new BusinessException(3066, id + parm5);
                        }
                        ConResult conResult = new ConResult();
                        conResult.setCode(1);
                        conResult.setMessage(parm5);
                        return conResult;
                    }
                    //        下载ca与图片至本地downFile目录
                    String password = null;
                    try {
                        EfpCa efpCa = downCaFinance(etpTaxId, null);
                        password = efpCa.getCaPassword();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new BusinessException(3066, id + "财务章下载失败");
                    }

                    File filecaFilePath = new File(caFilePath);
                    if(!filecaFilePath.exists()){
                        throw new BusinessException(3067, id + "CA证书下载失败");
                    }
                    File filepngPath = new File(pngPath);
                    if(!filepngPath.exists()){
                        throw new BusinessException(3068, id + "财务章下载失败");
                    }
                    //签章
                    try {
                        KeyStore ks = PdfUtils.signCheck(caFilePath, password);
                        String aliases = ks.aliases().nextElement();
                        PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
                        //证书链
                        Certificate[] chain = ks.getCertificateChain(aliases);
                        SignPdf.createFinanceSeal(pdfPathOld, pngPath, pdfPath, chain, pk, "sign", "Ghent");
                    } catch (Exception e) {
                        throw new BusinessException(3069, id + "CA签章失败");
                    }
                     filepdfPath = new File(pdfPath);
                    if(!filepdfPath.exists()){
                        throw new BusinessException(3070, id + "电子收据生成失败");
                    }
                    InputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(pdfPath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Calendar cale = Calendar.getInstance();
                    Integer year = cale.get(Calendar.YEAR);
                    Integer month = cale.get(Calendar.MONTH) + 1;
                    Integer day = cale.get(Calendar.DATE);
                    String pdfPathNew = Constant.CERTPDF  + "/" +  year.toString() + "/" + month.toString() + "/" + day.toString() + "/" + etpTaxId;
                    String pdfNameNew = ts + "new" + fileName;
                    //将pdf文件上传到文件服务器 FileStorageUtils.uploadFile( pdfPathNew, pdfNameNew, inputStream);
                    try {
                        FileStorageUtils.uploadFile( pdfPathNew, pdfNameNew, inputStream);
                    } catch (Exception e) {
                        throw new BusinessException(3071, id + "电子收据上传失败");
                    } finally {
                        if (inputStream != null) try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String pdfUrl = Constant.PDFFILEURL + pdfPathNew + "/" + pdfNameNew;
                    hashMap.put("url",pdfUrl);
                    hashMap.put("status","1");
                    String result = HttpRequestUtils.sendPost(Constant.RESULT,hashMap);
                    ConResult conResult = new ConResult();
                    conResult.setCode(1);
                    conResult.setMessage(id + "电子收据推送失败");
                    if(result.length()>0){
                        JSONObject jsonObject = JSON.parseObject(result);
                        if(jsonObject.getInteger("code") == 0){
                            certPdfMapper.updateByParm2(id,parm3,pdfUrl,"");
                        }else {
                            return conResult;
                        }
                    }else {
                        return conResult;
                    }
                }else if(parm3.equalsIgnoreCase("2")){
                    if(parm3Old.equalsIgnoreCase("2")){
                        throw new BusinessException(3082, id + "收据已经作废");
                    }
                    parm5 = "该订单申请作废";
                    hashMap.put("errmsg",parm5);
                    hashMap.put("status","0");
                    String result = HttpRequestUtils.sendPost(Constant.RESULT,hashMap);
                    if(result.length()>0){
                        JSONObject jsonObject = JSON.parseObject(result);
                        if(jsonObject.getInteger("code") == 0){
                            try {
                                certPdfMapper.updateByParm2(id,parm3,null,parm5);
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new BusinessException(3085, id + "收据作废失败");
                            }
                        }else {
                            throw new BusinessException(3083, id + jsonObject.getString("message"));
                        }
                    }else {
                        throw new BusinessException(3084, id + "收据推送失败");
                    }
                }
            } finally {
                if (filepdfPathOld != null &&filepdfPathOld.exists() && filepdfPathOld.isFile()) {
                    filepdfPathOld.delete();
                }
                if (filepdfPath != null && filepdfPath.exists() && filepdfPath.isFile()) {
                    filepdfPath.delete();
                }
            }
        }
        return new ConResult();
    }


    public ConResult Createpdf1(String logPath, String dzfpResult, String qqlsh, ICore iCore) throws Exception {

        HashMap<String, String> efpInvoiceCopy = iCore.Createpdf(logPath, dzfpResult, qqlsh);
        ConResult conResult = new ConResult(1, efpInvoiceCopy.get("gfmc") + "^" + efpInvoiceCopy.get("hjje"));
        WebSocketUtils1 ss = new WebSocketUtils1();
        Map map = ss.getSocketMaps();
        ss.qunfa(map, JSON.toJSONString(conResult), conResult);
        String kprId = efpInvoiceCopy.get("kprId");
        String sendUrl = efpInvoiceCopy.get("sendUrl");
        if (!StringUtils.isEmpty(sendUrl)) {
            bsnAsync.sendbackMsg(sendUrl, efpInvoiceCopy.get("id"), kprId, efpInvoiceCopy.get("fpdm"), efpInvoiceCopy.get("fphm"), efpInvoiceCopy.get("kptime"), efpInvoiceCopy.get("pdfUrl"), efpInvoiceCopy.get("email"));
        }
        String spzhm = efpInvoiceCopy.get("parm5");
        if (!StringUtils.isEmpty(spzhm)) {
            bsnAsync.sendWx(logPath, spzhm);
        }
        return new ConResult();
    }

    public static void main(String[] args) {
        String b = String.valueOf(System.currentTimeMillis());
        System.out.println(b);

        System.out.println(System.currentTimeMillis() - Long.parseLong("1559700512618"));
        Long s = (System.currentTimeMillis() - Long.parseLong("1559700512618")) / (1000 * 60);

        System.out.println(s);

    }
}
