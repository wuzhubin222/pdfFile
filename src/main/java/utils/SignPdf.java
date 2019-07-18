package utils;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.UUID;

import com.itextpdf.text.DocumentException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import sun.font.FontDesignMetrics;
import sun.misc.BASE64Encoder;

/**
 * @author 帅帅
 * @date 2018/5/15 16:10
 */
public class SignPdf {



    /**
     * @param password
     *            秘钥密码
     * @param keyStorePath
     *            秘钥文件路径
     * @param signPdfSrc
     *            签名的PDF文件
     * @param signImage
     *            签名图片文件
     * @param x
     *            x坐标
     * @param y
     *            y坐标
     * @return
     */
    public static byte[] sign(String password, String keyStorePath, String signPdfSrc, String signImage,
                              float x, float y) {
        File signPdfSrcFile = new File(signPdfSrc);
        PdfReader reader = null;
        ByteArrayOutputStream signPDFData = null;
        PdfStamper stp = null;
        FileInputStream fos = null;
        try {
            BouncyCastleProvider provider = new BouncyCastleProvider();
            Security.addProvider(provider);
            KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
            fos = new FileInputStream(keyStorePath);
            // 私钥密码 为Pkcs生成证书是的私钥密码 123456
            ks.load(fos, password.toCharArray());
            String alias = (String) ks.aliases().nextElement();
            PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);
            reader = new PdfReader(signPdfSrc);
            signPDFData = new ByteArrayOutputStream();
            // 临时pdf文件
            File temp = new File(signPdfSrcFile.getParent(), System.currentTimeMillis() + ".pdf");
            stp = PdfStamper.createSignature(reader, signPDFData, '\0', temp, true);
            stp.setFullCompression();
            PdfSignatureAppearance sap = stp.getSignatureAppearance();
            sap.setReason("sign");
            sap.setLocation("Ghent");
            // 使用png格式透明图片
            Image image = Image.getInstance(signImage);


         sap.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);


            sap.setImageScale(0);
            sap.setSignatureGraphic(image);
            sap.setRenderingMode(RenderingMode.GRAPHIC);
            // 是对应x轴和y轴坐标
            sap.setVisibleSignature(new Rectangle(x, y, x + 107, y + 104), 1,
                    UUID.randomUUID().toString().replaceAll("-", ""));
            stp.getWriter().setCompressionLevel(5);
            ExternalDigest digest = new BouncyCastleDigest();
            ExternalSignature signature = new PrivateKeySignature(key, DigestAlgorithms.SHA512, provider.getName());
            MakeSignature.signDetached(sap, digest, signature, chain, null, null, null, 0, CryptoStandard.CADES);
            stp.close();
            reader.close();
            return signPDFData.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (signPDFData != null) {
                try {
                    signPDFData.close();
                } catch (IOException e) {
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

/*
    * @param password
     *            秘钥密码
     * @param keyStorePath
     *            秘钥文件路径
     * @param signPdfSrc
     *            签名的PDF文件
     * @param signImage
     *            签名图片文件
 */
    public static void main(String[] args) throws Exception {


        String pfx="D:\\1\\91610132710152658R.pfx";
        String pdf="D:\\1\\91610132710152658R999.pdf";
        String png="D:\\1\\91610132710152658R.png";
        String password="841248";
//        byte[] fileData = sign("841248", "D:\\1\\91610132710152658R.pfx", //
//                "D:\\1\\91610132710152658R.pdf",//
//                "D:\\1\\91610132710152658R.png", 480, 0);
//        FileOutputStream f = new FileOutputStream(new File("D:\\1\\91610132710152658RNew1.pdf"));
//        f.write(fileData);
//        f.close();


        KeyStore ks = PdfUtils.signCheck(pfx, password);
        String aliases = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
        //证书链
        Certificate[] chain = ks.getCertificateChain(aliases);
        //原pdf文件，png图片，pdf输出，
       create(pdf,png,"D:\\1\\91610132710152658RNew2.pdf",
               chain,pk, "sign", "Ghent");




       // createSignTextImg("华佗", "在线医院", "2018.01.01",   "D:\\1\\签章.jpg");
        System.out.println("成功");
    }





    public static void create(String tmplatePdfPath,String pngImgPath,
                              String outPdfPath,Certificate[] chain, PrivateKey pk, String reason, String location)
            throws GeneralSecurityException, IOException, DocumentException {

        PdfReader reader = new PdfReader(tmplatePdfPath);
        FileOutputStream os = new FileOutputStream(outPdfPath);
        //创建签章工具PdfStamper ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, false);

        // 获取数字签章属性对象，设定数字签章的属性
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样
        //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
        //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
        appearance.setVisibleSignature(new Rectangle(480, 0, 587, 104), 1, "sig1");

        //读取图章图片，这个image是itext包的image
       Image image = Image.getInstance(pngImgPath);
       appearance.setSignatureGraphic(image);
       appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);

        //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
    appearance.setRenderingMode(RenderingMode.GRAPHIC);

        // 摘要算法
       ExternalDigest digest = new BouncyCastleDigest();
        // 签名算法
       ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA1, null);
       MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

        os.flush();
        os.close();
        stamper.close();
        reader.close();
    }


    public static void createFinanceSeal(String tmplatePdfPath,String pngImgPath,
                              String outPdfPath,Certificate[] chain, PrivateKey pk, String reason, String location)
            throws GeneralSecurityException, IOException, DocumentException {

        PdfReader reader = new PdfReader(tmplatePdfPath);
        FileOutputStream os = new FileOutputStream(outPdfPath);
        //创建签章工具PdfStamper ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, false);

        // 获取数字签章属性对象，设定数字签章的属性
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样
        //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
        //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
        //或者下面的也可以
//           PdfReader reader = new PdfReader(new FileInputStream(filename));
        Rectangle pageSize = reader.getPageSize(1);
        float height = pageSize.getHeight();
        float width = pageSize.getWidth();
        float x=width - 120;
        float y=30;
        System.out.println("width = "+width+", height = "+height);
        appearance.setVisibleSignature(new Rectangle(x, y, x + 100, y + 100), 1, "sig1");

        //读取图章图片，这个image是itext包的image
        Image image = Image.getInstance(pngImgPath);

        appearance.setSignatureGraphic(image);
        appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);

        //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
        appearance.setRenderingMode(RenderingMode.GRAPHIC);

        // 摘要算法
        ExternalDigest digest = new BouncyCastleDigest();
        // 签名算法
        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA1, null);
        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

        os.flush();
        os.close();
        stamper.close();
        reader.close();
    }


    /**
     * @param doctorName
     *            String 医生名字
     * @param hospitalName
     *            String 医生名称
     * @param date
     *            String 签名日期
     *            图片高度
     * @param jpgname
     *            String jpg图片名
     * @return
     */
    public static boolean createSignTextImg(
            String doctorName, //
            String hospitalName, //
            String date,
            String jpgname) {
        int width = 255;
        int height = 100;
        FileOutputStream out = null;
        //背景色
        Color bgcolor = Color.WHITE;
        //字色
        Color fontcolor = Color.RED;
        Font doctorNameFont = new Font(null, Font.BOLD, 20);
        Font othorTextFont = new Font(null, Font.BOLD, 18);
        try { // 宽度 高度
            BufferedImage bimage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bimage.createGraphics();
            g.setColor(bgcolor); // 背景色
            g.fillRect(0, 0, width, height); // 画一个矩形
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON); // 去除锯齿(当设置的字体过大的时候,会出现锯齿)

            g.setColor(Color.RED);
            g.fillRect(0, 0, 8, height);
            g.fillRect(0, 0, width, 8);
            g.fillRect(0, height - 8, width, height);
            g.fillRect(width - 8, 0, width, height);

            g.setColor(fontcolor); // 字的颜色
            g.setFont(doctorNameFont); // 字体字形字号
            FontMetrics fm = FontDesignMetrics.getMetrics(doctorNameFont);
            int font1_Hight = fm.getHeight();
            int strWidth = fm.stringWidth(doctorName);
            int y = 35;
            int x = (width - strWidth) / 2;
            g.drawString(doctorName, x, y); // 在指定坐标除添加文字

            g.setFont(othorTextFont); // 字体字形字号

            fm = FontDesignMetrics.getMetrics(othorTextFont);
            int font2_Hight = fm.getHeight();
            strWidth = fm.stringWidth(hospitalName);
            x = (width - strWidth) / 2;
            g.drawString(hospitalName, x, y + font1_Hight); // 在指定坐标除添加文字

            strWidth = fm.stringWidth(date);
            x = (width - strWidth) / 2;
            g.drawString(date, x, y + font1_Hight + font2_Hight); // 在指定坐标除添加文字

            g.dispose();
            out = new FileOutputStream(jpgname); // 指定输出文件
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
            param.setQuality(50f, true);
            encoder.encode(bimage, param); // 存盘
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }finally{
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }




    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);;
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);

    }




}
