package utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import impl.GeralBsiness;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;

import static com.lowagie.text.pdf.PdfName.DEST;
import static java.awt.AlphaComposite.SRC;

/**
 * Created by Administrator on 2018/5/9.
 */
public class pdfCaTest {

    public void sign(String src  //需要签章的pdf文件路径
            , String dest  // 签完章的pdf文件路径
            , Certificate[] chain //证书链
            , PrivateKey pk //签名私钥
            , String provider  // 密钥算法提供者，可以为null
            , String reason  //签名的原因，显示在pdf签名属性中，随便填
            , String location ) //签名的地点，显示在pdf签名属性中，随便填
            throws GeneralSecurityException, IOException, DocumentException {

        //开始pdfreader
        PdfReader reader = new PdfReader(src);
        //目标文件输出流
        FileOutputStream os = new FileOutputStream(dest);
        //创建签章工具PdfStamper ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);

        // 获取数字签章属性对象，设定数字签章的属性
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样
        //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
        //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角
        appearance.setVisibleSignature(new Rectangle(480, 0, 587, 104), 1, "sig1");
        //读取图章图片，这个image是itext包的image
         String signImgPath="D:\\pdfFileV2.0\\src\\main\\resources\\pdfSign\\义乌市申通快递有限公司.png";
        Image image = Image.getInstance(signImgPath);
        appearance.setSignatureGraphic(image);
        appearance.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_FORM_FILLING);
        //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

        // 摘要算法
        ExternalDigest digest = new BouncyCastleDigest();
        // 签名算法
        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA1, null);
        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
    }


    public static void main(String[] args) {

        //需要签章的pdf文件路径
        String tmplatePdfPath = "D:\\pdfFileV2.0\\src\\main\\resources\\pdfTemplate\\北京.pdf";
        // 签完章的pdf文件路径
        String outPdfPath = "d:\\1.pdf";
        //ketstore文件路径
        String keyStorePath = "D:\\pdfFileV2.0\\src\\main\\resources\\pdfSign\\Test.pfx";


        String password = "123456";
        try {
            //签名私钥
            KeyStore ks = signCheck(keyStorePath, password);
            String aliases = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
            //证书链
            Certificate[] chain = ks.getCertificateChain(aliases);

            //签名的理由，随便填
            String reason = "sign";
            //签名的位置，随便填
            String location = "Ghent";

            pdfCaTest app = new pdfCaTest();

            //app.sign(tmplatePdfPath, outPdfPath, chain, pk, null, reason, location);

            app.sign(tmplatePdfPath, outPdfPath, chain, pk, null, reason, location);

            System.out.println("测试");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }


    public static KeyStore signCheck(String keyStorePath, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(keyStorePath), password.toCharArray());
        return ks;
    }







}
