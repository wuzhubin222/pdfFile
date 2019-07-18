package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import controller.InvoiceController;
import entity.TextSealPos;
import excption.BusinessException;
import impl.GeralBsiness;
import model.EfpInvoice;
import model.EfpInvoiceCopy;
import model.EfpInvoiceQd;
import model.EfpZf;

import java.io.*;
import java.math.BigDecimal;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PdfUtils{

    //签证
    public static void visa(String src, String outSrc
            , Certificate[] chain // 证书链
            , PrivateKey pk // 签名私钥
            , String orImage, String locat, String reason, Rectangle rect,
                            int leve
    ) {
        try {
            PdfReader reader = new PdfReader(src);
            //目标文件输出流
            FileOutputStream os = new FileOutputStream(outSrc);
            //最加签证
            PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);

            // 获取数字签章属性对象，设定数字签章的属性
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            appearance.setReason(reason);
            appearance.setLocation(locat);
            //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样
            //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
            //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
            appearance.setVisibleSignature(rect, leve, "sig" + leve);
            //读取图章图片，这个image是itext包的image
            Image image = Image.getInstance(orImage);
            appearance.setSignatureGraphic(image);
            appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);

            //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
            // 摘要算法
            ExternalDigest digest = new BouncyCastleDigest();

            // 签名算法
            ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA1, null);
            MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
            stamper.close();
            os.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 多个PDF合并功能
     *
     * @param files    多个PDF的路径
     * @param savepath 生成的新PDF路径
     * @return boolean boolean
     */
    public static void mergePdfFiles(String logPath, JSONArray files, String savepath) {
        Document document = null;
        InputStream isx = null;
        try {
            List<InputStream> closeList = new ArrayList<InputStream>();
            isx = new FileInputStream(files.getString(0));
            document = new Document(new PdfReader(isx).getPageSize(1));
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(savepath));
            document.open();
            for (int i = 0; i < files.size(); i++) {
                InputStream is = new FileInputStream(files.getString(i));
                PdfReader reader = new PdfReader(is);
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    //PdfStamper
                    copy.addPage(page);
                }
                if (reader != null) {
                    reader.close();
                }
                if (is != null) {
                    is.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
          //  LogUtils.writeLogFile(logPath, "PdfUtils.mergePdfFiles IOException", e.toString());
          //  this.LOGGER.error();
            throw new BusinessException(5010, "pdf文件合并失败");
        } catch (DocumentException e) {
          //  LogUtils.writeLogFile(logPath, "PdfUtils.mergePdfFiles DocumentException", e.toString());
            e.printStackTrace();
            throw new BusinessException(5011, "pdf文件合并失败");
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                }
            }
            if (isx != null) {
                try {
                    isx.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 小写金额转大写
     *
     * @param numberOfMoney 小写金额
     * @return 大写金额字符串
     */
    public static String moneyNumberTocCh(BigDecimal numberOfMoney) {
        String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆",
                "伍", "陆", "柒", "捌", "玖"};
        String[] CN_UPPER_MONETRAY_UNIT = {"分", "角", "元",
                "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾",
                "佰", "仟"};
        String CN_FULL = "整";
        String CN_NEGATIVE = "负";
        int MONEY_PRECISION = 2;
        String CN_ZEOR_FULL = "零元" + CN_FULL;
        StringBuilder sb = new StringBuilder();
        // -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
        // positive.
        int signum = numberOfMoney.signum();
        // 零元整的情况
        if (signum == 0) {
            return CN_ZEOR_FULL;
        }
        //这里会进行金额的四舍五入
        long number = numberOfMoney.movePointRight(MONEY_PRECISION)
                .setScale(0, 4).abs().longValue();
        // 得到小数点后两位值
        long scale = number % 100;
        int numUnit = 0;
        int numIndex = 0;
        boolean getZero = false;
        // 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
        if (!(scale > 0)) {
            numIndex = 2;
            number = number / 100;
            getZero = true;
        }
        if ((scale > 0) && (!(scale % 10 > 0))) {
            numIndex = 1;
            number = number / 10;
            getZero = true;
        }
        int zeroSize = 0;
        while (true) {
            if (number <= 0) {
                break;
            }
            // 每次获取到最后一个数
            numUnit = (int) (number % 10);
            if (numUnit > 0) {
                if ((numIndex == 9) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
                }
                if ((numIndex == 13) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
                }
                sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                if (!(getZero)) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                }
                if (numIndex == 2) {
                    if (number > 0) {
                        sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                    }
                } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                }
                getZero = true;
            }
            // 让number每次都去掉最后一个数
            number = number / 10;
            ++numIndex;
        }
        // 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);
        }
        // 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
        if (!(scale > 0)) {
            sb.append(CN_FULL);
        }
        return sb.toString();
    }

    /**
     * 计算字符串实际长度，用于生成pdf文件文本内容宽度控制
     *
     * @param str
     * @return 字符串长度
     */
    public static int countLeng(String str) {
        int count = 0;
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            String len = Integer.toBinaryString(c[i]);
            if (len.length() > 8) {
                count += 2;
            } else {
                count++;
            }
        }
        return count;
    }

    //    public static void create(HashMap<String, String> dataMap, String QrCodeImgPath, String tmplatePdfPath, String signImgPath,
//                              String outPdfPath, String fontPath,String path,String password, String reason, String location){
//        try{
//            System.out.println("String map=\""+JSONObject.toJSON(dataMap).toString().replace("\"","\\\"")+"\";");
//
//            System.out.println("String QrCodeImgPath=\""+QrCodeImgPath.replace("\\","\\\\")+"\";");
//
//            System.out.println("String tmplatePdfPath=\""+tmplatePdfPath.replace("\\","\\\\")+"\";");
//            System.out.println("String outPdfPath=\""+outPdfPath.replace("\\","\\\\")+"\";");
//            System.out.println("String signImgPath=\""+signImgPath.replace("\\","\\\\")+"\";");
//            System.out.println("String fontPath=\""+fontPath.replace("\\","\\\\")+"\";");
//
//            System.out.println("String path=\""+path.replace("\\","\\\\")+"\";");
//            System.out.println("String password=\""+password+"\";");
//
//            System.out.println("String reason=\""+reason+"\";");
//            System.out.println("String location=\""+location+"\";");
//            System.out.println("HashMap<String, String> dataMap=JSON.parseObject(map,HashMap.class);");
//
//            System.out.println("PdfUtils.create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, outPdfPath,fontPath,path, password, reason, location);");
//
//
//            KeyStore ks = signCheck(path, password);
//            String aliases = ks.aliases().nextElement();
//            PrivateKey pk = (PrivateKey) ks.getKey(aliases, password.toCharArray());
//            //证书链
//            Certificate[] chain = ks.getCertificateChain(aliases);
//            create(dataMap,QrCodeImgPath,tmplatePdfPath,signImgPath,outPdfPath,fontPath,chain,pk,reason,location);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
    public  static void create(HashMap<String, String> dataMap, String QrCodeImgPath, String tmplatePdfPath, String signImgPath,
                              String outPdfPath, String fontPath, Certificate[] chain, PrivateKey pk, String reason, String location
    ) throws GeneralSecurityException, IOException, DocumentException {
      /*  File file =new File(outPdfPath);
        if(file.exists()){
            file.delete();
            file.createNewFile();
        }*/
        create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, outPdfPath, fontPath, chain, pk, reason, location, false);
    }

    /**
     * @param dataMap        填充的文本内容
     * @param QrCodeImgPath  二维码图片路径
     * @param tmplatePdfPath 初始的pdf文件模板路径
     * @param signImgPath    电子签章图片路径
     * @param outPdfPath     完成后的pdf文件输出路径
     * @param fontPath       字体路径
     * @param chain          ca证书链
     * @param pk             ca证书匙
     * @param reason         签章理由 默认"sign"
     * @param location       签章地址 默认"Ghent"
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws DocumentException
     */
    public synchronized static void create(HashMap<String, String> dataMap, String QrCodeImgPath, String tmplatePdfPath, String signImgPath,
                              String outPdfPath, String fontPath, Certificate[] chain, PrivateKey pk, String reason, String location,
                              boolean auto)
            throws GeneralSecurityException, IOException, DocumentException {

        PdfReader reader = null;
        PdfStamper stamper = null;

        FileOutputStream os = null;
        ByteArrayOutputStream bos = null;
        OutputStream fos = null;
        //创建签章工具PdfStamper ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        try {
            reader = new PdfReader(tmplatePdfPath);
            //添加二维码
            if (pk != null) {
                os = new FileOutputStream(outPdfPath);
                stamper = PdfStamper.createSignature(reader, os, '\0', null, false);

                //修改字体格式
                addTextSealx(stamper, dataMap, fontPath);
                //addTextSeahc(stamper, dataMap, fontPath);
                addQrCode(stamper, QrCodeImgPath);
                // 获取数字签章属性对象，设定数字签章的属性
                // 获取数字签章属性对象，设定数字签章的属性
                PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
                appearance.setReason(reason);
                appearance.setLocation(location);
                //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样
                //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
                //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
                appearance.setVisibleSignature(new Rectangle(480, 0, 587, 104), 1, "sig1");
                //appearance.setVisibleSignature(new Rectangle(480, -10, 590, 110), 1, "sig1");
                //读取图章图片，这个image是itext包的image
                Image image = Image.getInstance(signImgPath);
                appearance.setSignatureGraphic(image);
                appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
                //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
                appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
                // 摘要算法
                ExternalDigest digest = new BouncyCastleDigest();
                // 签名算法
                ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA1, null);
                MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
                //    stamper.close();
            } else {

                bos = new ByteArrayOutputStream();
                stamper = new PdfStamper(reader, bos);
                stamper.setFormFlattening(true);
                addTextSealx(stamper, dataMap, fontPath);
                addQrCode(stamper, QrCodeImgPath);
                // 需要先关闭    如果不关闭  则无法生成二进制数据
                stamper.close();
                //再进行创建一个输出流
                fos = new FileOutputStream(outPdfPath);
                //写入二进制数据
                fos.write(bos.toByteArray());
                fos.flush();
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * @param dataMap        红字发票填充的文本内容
     * @param QrCodeImgPath  红字发票二维码图片路径
     * @param tmplatePdfPath 红字发票初始的pdf文件模板路径
     * @param signImgPath    红字发票电子签章图片路径
     * @param outPdfPath     完成后的pdf文件输出路径
     * @param fontPath       字体路径
     * @param chain          ca证书链
     * @param pk             ca证书匙
     * @param reason         签章理由 默认"sign"
     * @param location       签章地址 默认"Ghent"
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws DocumentException
     */
    public static void createhc(HashMap<String, String> dataMap, String QrCodeImgPath, String tmplatePdfPath, String signImgPath,
                                String outPdfPath, String fontPath, Certificate[] chain, PrivateKey pk, String reason, String location)
            throws GeneralSecurityException, IOException, DocumentException {
        PdfReader reader = null;
        PdfStamper stamper = null;

        FileOutputStream os = null;
        ByteArrayOutputStream bos = null;
        OutputStream fos = null;
        //创建签章工具PdfStamper ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        try {
            reader = new PdfReader(tmplatePdfPath);
            //添加二维码
            if (pk != null) {
                os = new FileOutputStream(outPdfPath);
                stamper = PdfStamper.createSignature(reader, os, '\0', null, false);

                //修改字体格式
                //addTextSealx(stamper, dataMap, fontPath);
                addTextSeahc(stamper, dataMap, fontPath);
                addQrCode(stamper, QrCodeImgPath);
                // 获取数字签章属性对象，设定数字签章的属性
                // 获取数字签章属性对象，设定数字签章的属性
                PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
                appearance.setReason(reason);
                appearance.setLocation(location);
                //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样
                //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
                //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
                appearance.setVisibleSignature(new Rectangle(480, 0, 587, 104), 1, "sig1");
                //appearance.setVisibleSignature(new Rectangle(480, -10, 590, 110), 1, "sig1");
                //读取图章图片，这个image是itext包的image
                Image image = Image.getInstance(signImgPath);
                appearance.setSignatureGraphic(image);
                appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
                //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
                appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
                // 摘要算法
                ExternalDigest digest = new BouncyCastleDigest();
                // 签名算法
                ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA1, null);
                MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
                //    stamper.close();
            } else {

                bos = new ByteArrayOutputStream();
                stamper = new PdfStamper(reader, bos);
                stamper.setFormFlattening(true);
                addTextSealx(stamper, dataMap, fontPath);
                addQrCode(stamper, QrCodeImgPath);
                // 需要先关闭    如果不关闭  则无法生成二进制数据
                stamper.close();
                //再进行创建一个输出流
                fos = new FileOutputStream(outPdfPath);
                //写入二进制数据
                fos.write(bos.toByteArray());
                fos.flush();
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    static void addTextSealxhc(PdfStamper stamper, HashMap<String, String> dataMap, String fontPath) throws IOException, DocumentException {
        HashMap<String, TextSealPos> posMap = initTextSealPos2();
        adJust(dataMap, posMap);
        PdfContentByte over = stamper.getOverContent(1);
        BaseFont bf;
        List<String> list = new ArrayList<String>();
        list.add("wpmc");
        list.add("ggxh");
        list.add("dw");
        list.add("sl");
        list.add("dj");
        list.add("je");
        list.add("slv");
        list.add("se");

       /* List<String> fiter=new ArrayList<String>();
        fiter.add("hjse");
        fiter.add("hjje");
        */
        //单独绘画字体
        String name2 = "销项负数";
        TextSealPos t = new TextSealPos(110, 340, 12);
        bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        over.beginText();
        over.setFontAndSize(bf, t.getSize());
        over.showTextAligned(Element.ALIGN_LEFT, name2, t.getX(), t.getY(), 0);
        over.endText();

        for (String key : dataMap.keySet()) {
            boolean isContinue = false;
            for (String s : list) {
                if (key.contains(s)) {
                    isContinue = true;
                    break;
                }
            }
            if (!posMap.containsKey(key) || isContinue) {
                continue;
            }
            //修改字体
            if (key.contains("fsh") || key.contains("mmq") || key.contains("kprq")
                    || key.equals("hjje") || key.equals("hjse") || key.equals("jshjxx")) {
                bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else {
                bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
            }
            TextSealPos textSealPos = posMap.get(key);
            over.beginText();
            over.setFontAndSize(bf, textSealPos.getSize());
            if (key.contains("sl") || key.contains("dj") || key.contains("je") || key.contains("slv")
                    || key.contains("se")) {
                over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else if (key.contains("mmq") || key.contains("dw")) {
                over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else {
                over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            }
            over.endText();
        }
        int ling = 1;
        bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        int addHeight = 0;
        for (int i = 1; i <= 5; i++) {
            boolean isLine = false;
            int addLine = 0;
            int j = ling * 12 + addHeight;
            //addHeight=0;
            for (String str : list) {
                String name = str + i;
                if (dataMap.containsKey(name)) {
                    if (!isLine) {
                        ling++;
                        isLine = true;
                        posMap.put("dw" + i, new TextSealPos(236, 237 - j, 9));
                        posMap.put("sl" + i, new TextSealPos(300, 237 - j, 9));
                        posMap.put("dj" + i, new TextSealPos(390, 237 - j, 9));
                        posMap.put("je" + i, new TextSealPos(478, 237 - j, 9));
                        posMap.put("slv" + i, new TextSealPos(502, 237 - j, 9));
                        posMap.put("se" + i, new TextSealPos(590, 237 - j, 9));
                    }
                    posMap.put("wpmc" + i, new TextSealPos(50, 237 - j, 9));    //25, 237 - j, 9
                    posMap.put("ggxh" + i, new TextSealPos(200, 239 - j, 9)); //原始195, 237 - j, 9
                    TextSealPos textSealPos = posMap.get(name);
                    String value = dataMap.get(name);
                    over.beginText();
                    over.setFontAndSize(bf, textSealPos.getSize());
                    if (name.contains("sl") || name.contains("dj") || name.contains("je") || name.contains("slv")
                            || name.contains("se")) {
                        over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    } else if (name.contains("dw")) {
                        over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    } else if (str.equals("ggxh")) {
                        //System.out.println(value);
                        //字符串  最大字体  最小字体  宽度  最大行数
                        StringData data = StringData.stringlist(value, 8, 4, 37, 5);
                        for (int index = 0; index < data.getStrList().size(); index++) {
                            String s = data.getStrList().get(index);
                            over.setFontAndSize(bf, data.getFontSize());//8 -3
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX() - 20, textSealPos.getY() - (index * data.getFontSize() - 3), 0);
                            if (index * data.getFontSize() > 10) {
                                addHeight += data.getFontSize() + 1;
                            }
                            over.endText();
                            over.beginText();
                        }
                    } else if (str.equals("wpmc")) {
                        //字符串  最大字体  最小字体  宽度  最大行数
                        StringData data = StringData.stringlist(value, 8, 4, 120, 5);
                        int addSplst = 2;
                        for (int index = 0; index < data.getStrList().size(); index++) {
                            String s = data.getStrList().get(index);
                            over.setFontAndSize(bf, data.getFontSize());//8 -3
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX() - 20, textSealPos.getY() - (index * (data.getFontSize() + addSplst) - 3), 0);
                            if (index * (data.getFontSize() + addSplst) > 10) {
                                addHeight += data.getFontSize() + addSplst;
                            }
                            over.endText();
                            over.beginText();
                        }
                    } else {
                        over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    }
                    over.endText();
                }
            }
            ling += addLine;
        }
    }


    //添加二维码图片至pdf
    static void addQrCode(PdfStamper stamper, String QrCodeImgPath) throws IOException, DocumentException {
        PdfContentByte content = stamper.getOverContent(1);
        Image image = Image.getInstance(QrCodeImgPath);
        //设置显示位置,x增加图片往右移动,y增加图片往上移
        image.setAbsolutePosition(36, 333);
        //设置大小
        image.scaleToFit(57, 57);
        content.addImage(image);
    }

    //调整某些字段的字号大小，显示位置和显示内容
    static void adJust(HashMap<String, String> dataMap, HashMap<String, TextSealPos> posMap) {
        String[] keys = new String[]{"gfmc", "gfyhzh", "gfdzdh", "xfmc", "xfyhzh", "xfdzdh"};
        for (String key : keys) {
            //9号字体 最大宽度54  8号 60  7号70 根据长度切换字号，7号时长度还超过就变成2行  6号 80
            String value = dataMap.get(key);
            if (value != null) {
                int length = countLeng(value);
                if (length <= 54) {
                    continue;
                }
                if (length <= 60) {//改为8号字体
                    posMap.get(key).setSize(8);
                } else if (length <= 70) {//改为7号字体
                    posMap.get(key).setSize(7);
                } else {//改为7号字体并变成2
                    JSONArray jsonArray = PdfQdUtils.textWidthSeg(value, 54);
                    TextSealPos textSealPos = posMap.get(key);
                    textSealPos.setSize(textSealPos.getSize() - 1);
                    if (jsonArray.size() > 1) {//增加一行
                        dataMap.put(key, jsonArray.getString(0));
                        textSealPos.setY(textSealPos.getY() + 4);
                        posMap.put(key + "Add", new TextSealPos(textSealPos.getX(), textSealPos.getY() - 8, textSealPos.getSize()));
                        dataMap.put(key + "Add", jsonArray.getString(1));
                    }
                }
//                if(countLeng(value)>80){//缩小字体,修改内容
//                    JSONArray jsonArray = PdfQdUtils.textWidthSeg(value,54);
//                    TextSealPos textSealPos = posMap.get(key);
//                    textSealPos.setSize(textSealPos.getSize()-2);
//                    if(jsonArray.size()>1){//增加一行
//                        dataMap.put(key,jsonArray.getString(0));
//                        textSealPos.setY(textSealPos.getY()+4);
//                        posMap.put(key+"Add",new TextSealPos(textSealPos.getX(),textSealPos.getY()-6,textSealPos.getSize()));
//                        dataMap.put(key+"Add",jsonArray.getString(1));
//                    }
//                }
            }
        }

    }

    //调整详情字段的字号大小，显示位置和显示内容
    static void adJustdetails(HashMap<String, String> dataMap, HashMap<String, TextSealPos> posMap) {
        String[] keyss = new String[]{"ggxh", "wpmc"};
        for (String key : keyss) {
            String value = dataMap.get(key);
            if (value != null) {
                int length = countLeng(value);
                if (length <= 30) {
                    continue;
                }
                if (length <= 40) {//改为8号字体
                    posMap.get(key).setSize(8);
                } else if (length <= 50) {//改为7号字体
                    posMap.get(key).setSize(7);
                } else {//改为7号字体并变成2行
                    JSONArray jsonArray = PdfQdUtils.textWidthSeg(value, 30);
                    TextSealPos textSealPos = posMap.get(key);
                    textSealPos.setSize(textSealPos.getSize() - 2);
                    if (jsonArray.size() > 1) {//增加一行
                        dataMap.put(key, jsonArray.getString(0));
                        textSealPos.setY(textSealPos.getY() + 4);
                        posMap.put(key + "Add", new TextSealPos(textSealPos.getX(), textSealPos.getY() - 5, textSealPos.getSize()));
                        dataMap.put(key + "Add", jsonArray.getString(1));
                    }
                }
            }
        }

    }

    public static List<String> splitStr(String str, int length) {
        List<String> strList = new ArrayList<String>();
        int begin = 0;
        while (str.length() > begin) {
            int strLength = length;
            if (begin + strLength > str.length()) {
                strLength = str.length() - begin;
            }
            strList.add(str.substring(begin, begin + strLength));
            begin += length;
        }
        return strList;
    }

    //调整字体格式大小
    static void addTextSealx(PdfStamper stamper, HashMap<String, String> dataMap, String fontPath) throws IOException, DocumentException {
        HashMap<String, TextSealPos> posMap = initTextSealPos();
        adJust(dataMap, posMap);
        //adJustdetails(dataMap,posMap);
        PdfContentByte over = stamper.getOverContent(1);
        BaseFont bf;
        List<String> list = new ArrayList<String>();
        list.add("wpmc");
        list.add("ggxh");
        list.add("dw");
        list.add("sl");
        list.add("dj");
        list.add("je");
        list.add("slv");
        list.add("se");
        List<String> fiter = new ArrayList<String>();
        fiter.add("hjse");
        fiter.add("hjje");

        for (String key : dataMap.keySet()) {
            boolean isContinue = false;
            for (String s : list) {
                if (!fiter.contains(key)) {
                    if (key.contains(s)) {
                        isContinue = true;
                        break;
                    }
                }
            }
            if (!posMap.containsKey(key) || isContinue) {
                continue;
            }
            if(key.equals("hjje") ||key.equals("hjse")) {
                double hjje1=Double.parseDouble(dataMap.get(key).replace("¥",""));
                dataMap.put(key, ("¥"+  new java.text.DecimalFormat("#0.00").format(hjje1)).toString());
            }
            //修改字体
            if (key.contains("fsh") || key.contains("mmq") || key.contains("kprq")
                    || key.equals("hjje") || key.equals("hjse") || key.equals("jshjxx")) {
                bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else {
                bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
            }
            TextSealPos textSealPos = posMap.get(key);
            over.beginText();
            over.setFontAndSize(bf, textSealPos.getSize());
            if (key.contains("sl") || key.contains("dj") || key.contains("je") || key.contains("slv")
                    || key.contains("se")) {
                over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else if (key.contains("mmq") || key.contains("dw")) {
                over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else {
                over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            }
            over.endText();
        }
        //调整 备注
        showText(over,dataMap);

        int ling = 1;
        bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        int addHeight = 0;
        for (int i = 1; i <= 5; i++) {
            boolean isLine = false;
            int addLine = 0;
            int j = ling * 12 + addHeight;
            //addHeight=0;
            for (String str : list) {
                String name = str + i;
                if (dataMap.containsKey(name)) {
                    if (!isLine) {
                        ling++;
                        isLine = true;
                        posMap.put("dw" + i, new TextSealPos(236, 237 - j, 9));
                        posMap.put("sl" + i, new TextSealPos(300, 237 - j, 9));
                        posMap.put("dj" + i, new TextSealPos(390, 237 - j, 9));
                        posMap.put("je" + i, new TextSealPos(478, 237 - j, 9));
                        posMap.put("slv" + i, new TextSealPos(502, 237 - j, 9));
                        posMap.put("se" + i, new TextSealPos(590, 237 - j, 9));
                    }
                    posMap.put("wpmc" + i, new TextSealPos(50, 237 - j, 9));    //25, 237 - j, 9
                    posMap.put("ggxh" + i, new TextSealPos(200, 239 - j, 9)); //原始195, 237 - j, 9
                    TextSealPos textSealPos = posMap.get(name);
                    String value = dataMap.get(name);
                    over.beginText();
                    over.setFontAndSize(bf, textSealPos.getSize());
                    if (name.contains("sl") || name.contains("dj") || name.contains("je") || name.contains("slv")
                            || name.contains("se")) {
                        if(name.contains("slv")){
                            String slv=dataMap.get(name).toString();
                            if(slv!=null && slv.indexOf("%")==-1){
                                int slvValue=(int)( Double.parseDouble(slv)*100.0);
                                slv=slvValue+"%";
                            }
                            over.showTextAligned(Element.ALIGN_RIGHT,slv, textSealPos.getX(), textSealPos.getY(), 0);
                        }else{
                            over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                        }
                    } else if (name.contains("dw")) {
                        over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    } else if (str.equals("ggxh")) {
                        //System.out.println(value);
                        //字符串  最大字体  最小字体  宽度  最大行数
                        StringData data = StringData.stringlist(value, 8, 4, 37, 5);
                        for (int index = 0; index < data.getStrList().size(); index++) {
                            String s = data.getStrList().get(index);
                            over.setFontAndSize(bf, data.getFontSize());//8 -3
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX() - 20, textSealPos.getY() - (index * data.getFontSize() - 3), 0);
                            if (index * data.getFontSize() > 10) {
                                addHeight += data.getFontSize() + 1;
                            }
                            over.endText();
                            over.beginText();
                        }
                    } else if (str.equals("wpmc")) {
                        //字符串  最大字体  最小字体  宽度  最大行数
                        StringData data = StringData.stringlist(value, 8, 4, 120, 5);
                        int addSplst = 2;
                        for (int index = 0; index < data.getStrList().size(); index++) {
                            String s = data.getStrList().get(index);
                            over.setFontAndSize(bf, data.getFontSize());//8 -3
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX() - 20, textSealPos.getY() - (index * (data.getFontSize() + addSplst) - 3), 0);
                            if (index * (data.getFontSize() + addSplst) > 10) {
                                addHeight += data.getFontSize() + addSplst;
                            }
                            over.endText();
                            over.beginText();
                        }
                    } else {
                        over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    }
                    over.endText();
                }
            }
            ling += addLine;
        }
    }
    public static void showText(PdfContentByte over,BaseFont font,float x,float y,String data,int Element,int fontSize){
        over.beginText();
        over.setFontAndSize(font, fontSize);
        over.showTextAligned(Element,data,x, y, 0);
        over.endText();
    }
    public static void showText(PdfContentByte over,Map<String,String> data){
        StringData stringData=
                StringData.stringlist(data.get("bz1"), 11,11, 225, 100);
        for(int i=0;i<stringData.getStrList().size();i++){
            showText(over,
                    FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont(),
                    370,90-(i*11),stringData.getStrList().get(i),Element.ALIGN_LEFT,11);
        }

    }

    public static void showHcText(PdfContentByte over,BaseFont font,float x,float y,String data,int Element,int fontSize){
        over.beginText();
        over.setFontAndSize(font, fontSize);
        over.showTextAligned(Element,data,x, y, 0);
        over.endText();
    }

    public static void showHcText(PdfContentByte over,Map<String,String> data){
        StringData stringData=
                StringData.stringlist(data.get("bz1"), 11,10, 242, 100);  //字符串  最大字体  最小字体  宽度  最大行数
        for(int i=0;i<stringData.getStrList().size();i++){
            showText(over,
                    FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont(),
                    370,90-(i*11),stringData.getStrList().get(i),Element.ALIGN_LEFT,11);
        }

    }


    //修改中2
    static void addTextSealx2(PdfStamper stamper, HashMap<String, String> dataMap, String fontPath) throws IOException, DocumentException {
        HashMap<String, TextSealPos> posMap = initTextSealPos2();
        adJust(dataMap, posMap);
        adJustdetails(dataMap, posMap);
        PdfContentByte over = stamper.getOverContent(1);
        BaseFont bf;
        List<String> list = new ArrayList<String>();
        list.add("wpmc");
        list.add("ggxh");
        list.add("dw");
        list.add("sl");
        list.add("dj");
        list.add("je");
        list.add("slv");
        list.add("se");


        //单独绘画字体
        String name2 = "销项负数";
        TextSealPos t = new TextSealPos(110, 340, 12);
        bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        over.beginText();
        over.setFontAndSize(bf, t.getSize());
        over.showTextAligned(Element.ALIGN_LEFT, name2, t.getX(), t.getY(), 0);
        over.endText();


        for (String key : dataMap.keySet()) {

           /* if(key.equals("ceshi")){
                System.out.println("123");
            }*/
            boolean isContinue = false;
            for (String s : list) {
                if (key.contains(s)) {
                    isContinue = true;
                    break;
                }
            }
            if (!posMap.containsKey(key) || isContinue) {
                continue;
            }
            //修改字体
            if (key.contains("fsh") || key.contains("mmq") || key.contains("kprq")
                    || key.equals("hjje") || key.equals("hjse") || key.equals("jshjxx")) {
                bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else {
                bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
            }
            TextSealPos textSealPos = posMap.get(key);
            over.beginText();
            over.setFontAndSize(bf, textSealPos.getSize());
            if (key.contains("sl") || key.contains("dj") || key.contains("je") || key.contains("slv")
                    || key.contains("se")) {
                over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else if (key.contains("mmq") || key.contains("dw")) {
                over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else {
                over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            }

            over.endText();
        }
        int ling = 1;
        bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        for (int i = 1; i <= 5; i++) {
            boolean isLine = false;
            int addLine = 0;
            int j = ling * 12;
            for (String str : list) {
                String name = str + i;
                if (dataMap.containsKey(name)) {
                    if (!isLine) {
                        ling++;
                        isLine = true;
                        posMap.put("dw" + i, new TextSealPos(236, 237 - j, 9));
                        posMap.put("sl" + i, new TextSealPos(300, 237 - j, 9));
                        posMap.put("dj" + i, new TextSealPos(390, 237 - j, 9));
                        posMap.put("je" + i, new TextSealPos(478, 237 - j, 9));
                        posMap.put("slv" + i, new TextSealPos(502, 237 - j, 9));
                        posMap.put("se" + i, new TextSealPos(590, 237 - j, 9));
                    }
                    posMap.put("wpmc" + i, new TextSealPos(50, 237 - j, 9));    //25, 237 - j, 9
                    posMap.put("ggxh" + i, new TextSealPos(200, 239 - j, 9)); //原始195, 237 - j, 9
                    TextSealPos textSealPos = posMap.get(name);
                    String value = dataMap.get(name);
                    over.beginText();
                    over.setFontAndSize(bf, textSealPos.getSize());
                    if (name.contains("sl") || name.contains("dj") || name.contains("je") || name.contains("slv")
                            || name.contains("se")) {
                        over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    } else if (name.contains("dw")) {
                        over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    } else if (str.equals("ggxh")) {
                        List<String> listStr = splitStr(value, 5);
                        for (int index = 0; index < listStr.size(); index++) {
                            String s = listStr.get(index);
                            over.setFontAndSize(bf, 8);
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX() - 20,
                                    textSealPos.getY() - (index * 12) - 3, 0);
                            over.endText();
                            over.beginText();
                        }
                        int num = listStr.size();
                        if (num > 0 && num - 1 > addLine) {
                            addLine = num - 1;
                        }
                    } else if (str.equals("wpmc")) {
                        List<String> listStr = splitStr(value, 21);
                        for (int index = 0; index < listStr.size(); index++) {
                            String s = listStr.get(index);
                            over.setFontAndSize(bf, 7);//456465456
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX(),
                                    textSealPos.getY() - (index * 12), 0);
                            over.endText();
                            over.beginText();
                        }
                        int num = listStr.size();
                        if (num > 0 && num - 1 > addLine) {
                            addLine = num - 1;
                        }
                    } else {
                        over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    }
                    over.endText();
                }
            }
            ling += addLine;
        }
    }

    static void addTextSeahc(PdfStamper stamper, HashMap<String, String> dataMap, String fontPath) throws IOException, DocumentException {
        HashMap<String, TextSealPos> posMap = initTextSealPos();
        adJust(dataMap, posMap);
        //adJustdetails(dataMap,posMap);
        PdfContentByte over = stamper.getOverContent(1);
        BaseFont bf;
        List<String> list = new ArrayList<String>();
        list.add("wpmc");
        list.add("ggxh");
        list.add("dw");
        list.add("sl");
        list.add("dj");
        list.add("je");
        list.add("slv");
        list.add("se");


        //单独绘画字体
        String name2 = "销项负数";
        TextSealPos t = new TextSealPos(110, 340, 12);
        bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        over.beginText();
        over.setFontAndSize(bf, t.getSize());
        over.showTextAligned(Element.ALIGN_LEFT, name2, t.getX(), t.getY(), 0);
        over.endText();


        List<String> fiter = new ArrayList<String>();
        fiter.add("hjse");
        fiter.add("hjje");

        for (String key : dataMap.keySet()) {
            boolean isContinue = false;
            for (String s : list) {
                if (!fiter.contains(key)) {
                    if (key.contains(s)) {
                        isContinue = true;
                        break;
                    }
                }
            }
            if (!posMap.containsKey(key) || isContinue) {
                continue;
            }

            if(key.equals("hjje") ||key.equals("hjse")) {
                double hjje1=Double.parseDouble(dataMap.get(key).replace("¥",""));
                dataMap.put(key, ("¥"+  new java.text.DecimalFormat("#0.00").format(hjje1)).toString());
            }

            //修改字体
            if (key.contains("fsh") || key.contains("mmq") || key.contains("kprq")
                    || key.equals("hjje") || key.equals("hjse") || key.equals("jshjxx")) {
                bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

            } else {
                bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
            }

            TextSealPos textSealPos = posMap.get(key);
            over.beginText();
            over.setFontAndSize(bf, textSealPos.getSize());
            if (key.contains("sl") || key.contains("dj") || key.contains("je") || key.contains("slv")
                    || key.contains("se")) {
                over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else if (key.contains("mmq") || key.contains("dw")) {
                over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else {
                over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            }
            over.endText();
        }
        //调整 备注
        showHcText(over,dataMap);

        int ling = 1;
        bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        int addHeight = 0;
        for (int i = 1; i <= 5; i++) {
            boolean isLine = false;
            int addLine = 0;
            int j = ling * 12 + addHeight;
            //addHeight=0;
            for (String str : list) {
                String name = str + i;
                if (dataMap.containsKey(name)) {
                    if (!isLine) {
                        ling++;
                        isLine = true;
                        posMap.put("dw" + i, new TextSealPos(236, 237 - j, 9));
                        posMap.put("sl" + i, new TextSealPos(300, 237 - j, 9));
                        posMap.put("dj" + i, new TextSealPos(390, 237 - j, 9));
                        posMap.put("je" + i, new TextSealPos(478, 237 - j, 9));
                        posMap.put("slv" + i, new TextSealPos(502, 237 - j, 9));
                        posMap.put("se" + i, new TextSealPos(590, 237 - j, 9));
                    }
                    posMap.put("wpmc" + i, new TextSealPos(50, 237 - j, 9));    //25, 237 - j, 9
                    posMap.put("ggxh" + i, new TextSealPos(200, 239 - j, 9)); //原始195, 237 - j, 9
                    TextSealPos textSealPos = posMap.get(name);
                    String value = dataMap.get(name);
                    over.beginText();
                    over.setFontAndSize(bf, textSealPos.getSize());
                    if (name.contains("sl") || name.contains("dj") || name.contains("je") || name.contains("slv")
                            || name.contains("se")) {
                        if(name.contains("slv")){
                            String slv=dataMap.get(name).toString();
                            if(slv!=null && slv.indexOf("%")==-1){
                                int slvValue=(int)( Double.parseDouble(slv)*100.0);
                                slv=slvValue+"%";
                            }
                            over.showTextAligned(Element.ALIGN_RIGHT,slv, textSealPos.getX(), textSealPos.getY(), 0);
                        }else{
                            over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                        }
                    } else if (name.contains("dw")) {
                        over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    } else if (str.equals("ggxh")) {
                        //System.out.println(value);
                        //字符串  最大字体  最小字体  宽度  最大行数
                        StringData data = StringData.stringlist(value, 8, 4, 37, 5);
                        for (int index = 0; index < data.getStrList().size(); index++) {
                            String s = data.getStrList().get(index);
                            over.setFontAndSize(bf, data.getFontSize());//8 -3
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX() - 20, textSealPos.getY() - (index * data.getFontSize() - 3), 0);
                            if (index * data.getFontSize() > 10) {
                                addHeight += data.getFontSize() + 1;
                            }
                            over.endText();
                            over.beginText();
                        }
                    } else if (str.equals("wpmc")) {
                        //字符串  最大字体  最小字体  宽度  最大行数
                        StringData data = StringData.stringlist(value, 8, 4, 120, 5);
                        int addSplst = 2;
                        for (int index = 0; index < data.getStrList().size(); index++) {
                            String s = data.getStrList().get(index);
                            over.setFontAndSize(bf, data.getFontSize());//8 -3
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX() - 20, textSealPos.getY() - (index * (data.getFontSize() + addSplst) - 3), 0);
                            if (index * (data.getFontSize() + addSplst) > 10) {
                                addHeight += data.getFontSize() + addSplst;
                            }
                            over.endText();
                            over.beginText();
                        }
                    } else {
                        over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    }
                    over.endText();
                }
            }
            ling += addLine;
        }
    }

    //添加文字水印至pdf
    static void addTextSeal(PdfStamper stamper, HashMap<String, String> dataMap, String fontPath) throws IOException, DocumentException {
        HashMap<String, TextSealPos> posMap = initTextSealPos();
        adJust(dataMap, posMap);
        //adJustdetails(dataMap,posMap);
        PdfContentByte over = stamper.getOverContent(1);
        BaseFont bf;
        for (String key : dataMap.keySet()) {
            if (!posMap.containsKey(key)) {
                continue;
            }

            //修改字体
            if (key.contains("fsh") || key.contains("mmq") || key.contains("kprq")
                    || key.equals("hjje") || key.equals("hjse") || key.equals("jshjxx")) {
                bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else {
                bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
            }
            TextSealPos textSealPos = posMap.get(key);
            over.beginText();

            //动态调整字号大小
//            if (key.contains("dzdh")||key.contains("yhzh")||key.contains("xfmc")||key.contains("gfmc")) {
//                //9号字体 最大宽度54 6号 80
//                String value = dataMap.get(key);
//                if(value!=null){
//                    if(countLeng(value)>54){//缩小字体,修改内容
//                        JSONArray jsonArray = PdfQdUtils.textWidthSeg(value,80);
//                        System.out.println(jsonArray.size());
//                        textSealPos.setSize(textSealPos.getSize()-3);
//                        if(jsonArray.size()>1){//增加一行
//                            dataMap.put(key,jsonArray.getString(0));
//                            textSealPos.setY(textSealPos.getY()+3);
//                            posMap.put(key+"Add",new TextSealPos(textSealPos.getX(),textSealPos.getY()-3,textSealPos.getSize()));
//                            dataMap.put(key+"Add",jsonArray.getString(1));
//                        }else {//单行
//                        }
//                    }
//                }
////                JSONArray jsonArray = PdfQdUtils.textWidthSeg(value,80);
//                System.out.println(key);
////                dataMap.put(key,jsonArray.getString(0));
//            }
            over.setFontAndSize(bf, textSealPos.getSize());
            //对齐方式以(x,y)点为中心、显示的文字、x坐标增加则右移动、y坐标增加则上移、旋转角度
            if (key.contains("sl") || key.contains("dj") || key.contains("je") || key.contains("slv")
                    || key.contains("se")) {
                over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else if (key.contains("mmq") || key.contains("dw") || key.contains("ggxh")) {
                over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else {
                over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            }
            over.endText();
        }
    }

    //初始化文字位置
    static HashMap<String, TextSealPos> initTextSealPos() {
        HashMap<String, TextSealPos> posMap = new HashMap<String, TextSealPos>();
        posMap.put("jqbh", new TextSealPos(75, 322, 9));
        posMap.put("gfmc", new TextSealPos(108, 300, 9));
        posMap.put("gfsh", new TextSealPos(108, 284, 12));
        posMap.put("gfdzdh", new TextSealPos(108, 270, 9));
        posMap.put("gfyhzh", new TextSealPos(108, 255, 9));

        posMap.put("xfmc", new TextSealPos(108, 91, 9));
        posMap.put("xfsh", new TextSealPos(108, 76, 12));
        posMap.put("xfdzdh", new TextSealPos(108, 63, 9));
        posMap.put("xfyhzh", new TextSealPos(108, 49, 9));

        posMap.put("mmq1", new TextSealPos(480, 298, 12));
        posMap.put("mmq2", new TextSealPos(480, 283, 12));
        posMap.put("mmq3", new TextSealPos(480, 268, 12));
        posMap.put("mmq4", new TextSealPos(480, 253, 12));

        posMap.put("fpdm", new TextSealPos(475, 374, 9));
        posMap.put("fphm", new TextSealPos(475, 356, 9));
        posMap.put("kprq_nian", new TextSealPos(475, 339, 9));
        posMap.put("kprq_yue", new TextSealPos(507, 339, 9));
        posMap.put("kprq_ri", new TextSealPos(530, 339, 9));
        posMap.put("jym", new TextSealPos(475, 321, 9));

        posMap.put("skr", new TextSealPos(66, 30, 9));
        posMap.put("fhr", new TextSealPos(216, 30, 9));
        posMap.put("kpr", new TextSealPos(346, 30, 9));

        posMap.put("jshjdx", new TextSealPos(188, 110, 9));
        posMap.put("jshjxx", new TextSealPos(474, 110, 11));
        posMap.put("hjje", new TextSealPos(478, 126, 11));
        posMap.put("hjse", new TextSealPos(590, 126, 11));

      //  posMap.put("bz1", new TextSealPos(370, 90, 11));

        for (int i = 1; i <= 26; i++) {
            int j = i * 12;
            posMap.put("wpmc" + i, new TextSealPos(28, 237 - j, 9));
            posMap.put("ggxh" + i, new TextSealPos(195, 237 - j, 9)); //原始195, 237 - j, 9
            posMap.put("dw" + i, new TextSealPos(236, 237 - j, 9));
            posMap.put("sl" + i, new TextSealPos(318, 237 - j, 9));
            posMap.put("dj" + i, new TextSealPos(390, 237 - j, 9));
            posMap.put("je" + i, new TextSealPos(478, 237 - j, 9));
            posMap.put("slv" + i, new TextSealPos(502, 237 - j, 9));
            posMap.put("se" + i, new TextSealPos(590, 237 - j, 9));
        }
        return posMap;
    }

    //初始化文字位置
    static HashMap<String, TextSealPos> initTextSealPos2() {
        HashMap<String, TextSealPos> posMap = new HashMap<String, TextSealPos>();
        posMap.put("jqbh", new TextSealPos(75, 322, 9));
        posMap.put("chishi", new TextSealPos(110, 340, 12));
        posMap.put("gfmc", new TextSealPos(108, 300, 9));
        posMap.put("gfsh", new TextSealPos(108, 284, 12));
        posMap.put("gfdzdh", new TextSealPos(108, 270, 9));
        posMap.put("gfyhzh", new TextSealPos(108, 255, 9));

        posMap.put("xfmc", new TextSealPos(108, 91, 9));
        posMap.put("xfsh", new TextSealPos(108, 76, 12));
        posMap.put("xfdzdh", new TextSealPos(108, 63, 9));
        posMap.put("xfyhzh", new TextSealPos(108, 49, 9));

        posMap.put("mmq1", new TextSealPos(480, 298, 12));
        posMap.put("mmq2", new TextSealPos(480, 283, 12));
        posMap.put("mmq3", new TextSealPos(480, 268, 12));
        posMap.put("mmq4", new TextSealPos(480, 253, 12));

        posMap.put("fpdm", new TextSealPos(475, 374, 9));
        posMap.put("fphm", new TextSealPos(475, 356, 9));
        posMap.put("kprq_nian", new TextSealPos(475, 339, 9));
        posMap.put("kprq_yue", new TextSealPos(507, 339, 9));
        posMap.put("kprq_ri", new TextSealPos(530, 339, 9));
        posMap.put("jym", new TextSealPos(475, 321, 9));

        posMap.put("skr", new TextSealPos(66, 30, 9));
        posMap.put("fhr", new TextSealPos(216, 30, 9));
        posMap.put("kpr", new TextSealPos(346, 30, 9));

        posMap.put("jshjdx", new TextSealPos(188, 110, 9));
        posMap.put("jshjxx", new TextSealPos(474, 110, 11));
        posMap.put("hjje", new TextSealPos(478, 126, 11));
        posMap.put("hjse", new TextSealPos(590, 126, 11));

        posMap.put("bz1", new TextSealPos(370, 90, 11));

        for (int i = 1; i <= 26; i++) {
            int j = i * 12;
            posMap.put("wpmc" + i, new TextSealPos(28, 237 - j, 9));
            posMap.put("ggxh" + i, new TextSealPos(195, 237 - j, 9)); //原始195, 237 - j, 9
            posMap.put("dw" + i, new TextSealPos(236, 237 - j, 9));
            posMap.put("sl" + i, new TextSealPos(318, 237 - j, 9));
            posMap.put("dj" + i, new TextSealPos(390, 237 - j, 9));
            posMap.put("je" + i, new TextSealPos(478, 237 - j, 9));
            posMap.put("slv" + i, new TextSealPos(502, 237 - j, 9));
            posMap.put("se" + i, new TextSealPos(590, 237 - j, 9));
        }
        return posMap;
    }

    public static KeyStore signCheck(String keyStorePath, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        FileInputStream fs=null;
        try{
            fs=new FileInputStream(keyStorePath);
            ks.load(fs, password.toCharArray());
        }catch(Exception e){
            throw  e;
        }catch(Error e){
            throw e;
        }finally {
            if(fs!=null){
                fs.close();
            }
        }
        return ks;
    }


    //组装json数据至map,将填入pdf文件模板
    public static void composeMapToPdf(JSONObject jsonObject, HashMap<String, String> dataMap) {
        String fpdm = jsonObject.getString("fpdm");
        String fphm = jsonObject.getString("fphm");
        String fpmw = jsonObject.getString("mw");
        String jqbh = jsonObject.getString("jqbh");
        String jym = jsonObject.getString("jym");
        String bz = jsonObject.getString("bz");
        String kprq = jsonObject.getString("kprq"); //格式:yyyy年MM月dd日
        String xfmc = jsonObject.getString("xfmc");
        String xfsh = jsonObject.getString("xfsh");
        String xfdzdh = jsonObject.getString("xfdzdh");
        String xfyhzh = jsonObject.getString("xfyhzh");
        String gfmc = jsonObject.getString("gfmc");
        String gfsh = jsonObject.getString("gfsh");
        String gfdzdh = jsonObject.getString("gfdzdh");
        String gfyhzh = jsonObject.getString("gfyhzh");
        String fhr = jsonObject.getString("fhr");
        String skr = jsonObject.getString("skr");
        String kpr = jsonObject.getString("kpr");
        String hjje = jsonObject.getString("je").replace('￥', '¥');
        String hjse = jsonObject.getString("se").replace('￥', '¥');
        String jshjxx = jsonObject.getString("jshjxx");
        String jshjdx = jsonObject.getString("jshjdx");

        dataMap.put("jshjdx", jshjdx);
        dataMap.put("jshjxx", "¥" + jshjxx);
        DecimalFormat dfs = new DecimalFormat("##0.00");
        /*dataMap.put("hjje", dfs.format(hjje));
        dataMap.put("hjse", dfs.format(hjse));*/
        dataMap.put("hjse", hjse);
        dataMap.put("hjje", hjje);

        dataMap.put("xfmc", xfmc);
        dataMap.put("xfsh", xfsh);
        dataMap.put("xfdzdh", xfdzdh);
        dataMap.put("xfyhzh", xfyhzh);
        dataMap.put("gfmc", gfmc);
        dataMap.put("gfdzdh", gfdzdh);
        dataMap.put("gfyhzh", gfyhzh);
        dataMap.put("fhr", fhr);
        dataMap.put("skr", skr);
        dataMap.put("kpr", kpr);

        if (gfsh.contains("0000000000")) {
            dataMap.put("gfsh", "");
        } else {
            dataMap.put("gfsh", gfsh);
        }

        //发票密文，用于填入发票的密码区
        int div = fpmw.length() / 4;
        if (fpmw.length() == 108 || fpmw.length() == 112) {
            dataMap.put("mmq1", fpmw.substring(0, div));
            dataMap.put("mmq2", fpmw.substring(div, div * 2));
            dataMap.put("mmq3", fpmw.substring(div * 2, div * 3));
            dataMap.put("mmq4", fpmw.substring(div * 3, div * 4));
        } else {
            dataMap.put("mmq1", fpmw);
        }
        dataMap.put("fpdm", fpdm);
        dataMap.put("fphm", fphm);
        dataMap.put("jqbh", jqbh);
        if (jym.length() == 20) {
            dataMap.put("jym", jym.substring(0, 5) + " " + jym.substring(5, 10) + " " +
                    jym.substring(10, 15) + " " + jym.substring(15, 20));
        } else {
            dataMap.put("jym", jym);
        }
        //// TODO: 2017/11/1 备注根据长度动态的分为最多四行
        dataMap.put("bz1", bz);
        dataMap.put("kprq_nian", kprq.substring(0, 4));
        dataMap.put("kprq_yue", kprq.substring(5, 7));
        dataMap.put("kprq_ri", kprq.substring(8, 10));

        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        //需要生成明细清单pdf文件
        if (jsonArray.size() > 5) {
            dataMap.put("wpmc1", "(详见销货清单)");
            String slv = jsonArray.getJSONObject(0).getString("slv");
            dataMap.put("slv1", slv);
            dataMap.put("je1", hjje.replace("¥", ""));
            dataMap.put("se1", hjse.replace("¥", ""));

        } else {//不需要生成明细清单pdf文件
            for (int i = 0; i < jsonArray.size(); i++) {
                int j = i + 1;
                JSONObject obj = jsonArray.getJSONObject(i);
                dataMap.put("wpmc" + j, obj.getString("hwmc"));
                dataMap.put("ggxh" + j, obj.getString("ggxh"));
                dataMap.put("dw" + j, obj.getString("dw"));

                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("dj"))) {
                    /*System.out.println("生成6位数的单价"+GeralBsiness.getTwoDecimal2(new BigDecimal(obj.getString("dj"))));*/
                    dataMap.put("dj" + j, GeralBsiness.getTwoDecimal2(new BigDecimal(obj.getString("dj"))));
                }
                boolean jeIsDj = false;
                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("sl"))) {
                    BigDecimal bsl = new BigDecimal(obj.getString("sl"));
                    if (bsl.doubleValue() == 0) {
                        dataMap.put("sl" + j, 1 + "");
                        jeIsDj = true;
                    } else {
                        dataMap.put("sl" + j, obj.getString("sl"));
                    }
                }

                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("je"))) {
                    double je = GeralBsiness.getTwoDecimal(new BigDecimal(obj.getString("je")));
                    DecimalFormat df = new DecimalFormat("##0.00");
                    dataMap.put("je" + j, df.format(je));
                    if (jeIsDj) {
                        //System.out.println("生成6位数的单价"+GeralBsiness.getTwoDecimal2(new BigDecimal(obj.getString("dj"))));
                        dataMap.put("dj" + j, GeralBsiness.getTwoDecimal2(new BigDecimal(df.format(je))));
                    }
                }

                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("se"))) {
                    double se = GeralBsiness.getTwoDecimal(new BigDecimal(obj.getString("se")));
                    DecimalFormat df = new DecimalFormat("##0.00");
                    dataMap.put("se" + j, df.format(se));
                }

                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("slv"))) {
                    String slv = obj.getString("slv");
                    dataMap.put("slv" + j, slv);
                }
            }//for
        }

    }

    //组装json数据至EfpInvoice,将存储至数据库
    public static void composeInvoiceToDb(JSONObject jsonObject, EfpInvoice efpInvoice) {
        String jym = jsonObject.getString("jym");
        String qrm = jsonObject.getString("qrm");
        String mw = jsonObject.getString("mw");
        String fpdm = jsonObject.getString("fpdm");
        String fphm = jsonObject.getString("fphm");
        String jqbh = jsonObject.getString("jqbh");
        String bz = jsonObject.getString("bz");
        String kprq = jsonObject.getString("kprq");
        String xfmc = jsonObject.getString("xfmc");
        String xfsh = jsonObject.getString("xfsh");
        String xfdzdh = jsonObject.getString("xfdzdh");
        String xfyhzh = jsonObject.getString("xfyhzh");
        String gfmc = jsonObject.getString("gfmc");
        String gfsh = jsonObject.getString("gfsh");
        String gfdzdh = jsonObject.getString("gfdzdh");
        String gfyhzh = jsonObject.getString("gfyhzh");
        String fhr = jsonObject.getString("fhr");
        String skr = jsonObject.getString("skr");
        String kpr = jsonObject.getString("kpr");
        String hjje = jsonObject.getString("je").replace('￥', '¥');
        String hjse = jsonObject.getString("se").replace('￥', '¥');

        if (gfsh.contains("0000000000")) {
            gfsh = "";
        }
        efpInvoice.setMw(mw);

        //LogUtils.writeLogFile("/usr/pdfFileLog/PdfUtils.txt/", "qian hjje:"+hjje, "qian hjse:"+hjse);
        efpInvoice.setHjje(new BigDecimal(hjje.substring(1, hjje.length())));
        efpInvoice.setHjse(new BigDecimal(hjse.substring(1, hjse.length())));
        efpInvoice.setParm1(qrm);
        efpInvoice.setFpJym(jym);
        efpInvoice.setXfmc(xfmc);
        efpInvoice.setXfsh(xfsh);
        efpInvoice.setXfdzDh(xfdzdh);
        efpInvoice.setXfyhmcYhzh(xfyhzh);
        efpInvoice.setGfmc(gfmc);
        efpInvoice.setGfsh(gfsh);
        efpInvoice.setGfdzDh(gfdzdh);
        efpInvoice.setGfyhmcYhzh(gfyhzh);
        efpInvoice.setFhr(fhr);
        efpInvoice.setSkr(skr);
        efpInvoice.setKpr(kpr);
        efpInvoice.setFpdm(fpdm);
        efpInvoice.setFphm(fphm);
        efpInvoice.setJqbh(jqbh);
        efpInvoice.setBz(bz);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dates = new Date();
        String createtime = formatDate.format(dates);
        efpInvoice.setCreateTime(new Date());
        efpInvoice.setKprq(DateUtils.getFormatDate("yyyy年MM月dd日", kprq));
    }

    //将所需efpInvoiceCopy数据填入EfpInvoice,将存储至数据库
    public static void composeInvoiceToDb(EfpInvoiceCopy efpInvoiceCopy, EfpInvoice efpInvoice) {

        String kpJson = efpInvoiceCopy.getKpJson();
        JSONObject tableJsonObject = JSON.parseObject(kpJson);
        efpInvoice.setSpUser(tableJsonObject.getString("kprId"));
        efpInvoice.setCzyId(tableJsonObject.getString("czyId"));
        efpInvoice.setPhoneNo(efpInvoiceCopy.getParm3());
        efpInvoice.setCsmUnionid(efpInvoiceCopy.getParm6());
        efpInvoice.setWspzhm(efpInvoiceCopy.getParm5());
    }

    //组装json数据至多个EfpInvoiceQd,将存储至数据库
    public static void composeInvoiceQdToDb(JSONObject jsonObject, List<EfpInvoiceQd> efpInvoiceQds) {
        String fpdm = jsonObject.getString("fpdm");
        String fphm = jsonObject.getString("fphm");
        String xfsh = jsonObject.getString("xfsh");
        String gfsh = jsonObject.getString("gfsh");
        if (gfsh.contains("0000000000")) {
            gfsh = "";
        }

        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            EfpInvoiceQd invoiceQd = new EfpInvoiceQd();
            invoiceQd.setFpdm(fpdm);
            invoiceQd.setFphm(fphm);
            invoiceQd.setXfNsrsbh(xfsh);
            invoiceQd.setGfNsrsbh(gfsh);

            invoiceQd.setWpMc(obj.getString("hwmc"));
            invoiceQd.setWpXh(obj.getString("ggxh"));
            if (obj.getString("dw") != null) {
                invoiceQd.setWpDw(obj.getString("dw"));
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("dj"))) {
                invoiceQd.setDj(new BigDecimal(obj.getString("dj")));
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("sl"))) {
                BigDecimal bsl = new BigDecimal(obj.getString("sl"));
                invoiceQd.setSl(bsl);
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("je"))) {
                invoiceQd.setJe(new BigDecimal(obj.getString("je")));
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("se"))) {
                invoiceQd.setSe(new BigDecimal(obj.getString("se")));
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("slv"))) {
                String slv = obj.getString("slv");
                if(slv.indexOf("%")!=-1){
                    BigDecimal slvbig = new BigDecimal(slv.replace("%", ""));
                    invoiceQd.setSlv(slvbig.divide(new BigDecimal(100)));
                }else{
                    BigDecimal slvbig = new BigDecimal(slv.replace("%", ""));
                    invoiceQd.setSlv(slvbig);
                }
            }
            efpInvoiceQds.add(invoiceQd);
        }
    }


    //组装红冲json数据至map,将填入pdf文件模板
    public static void composezfMapToPdf(JSONObject jsonObject, HashMap<String, String> dataMap) {
        String fpdm = jsonObject.getString("fpdm");
        String fphm = jsonObject.getString("fphm");
        String fpmw = jsonObject.getString("mw");
        String jqbh = jsonObject.getString("jqbh");
        String jym = jsonObject.getString("jym");
        String bz = jsonObject.getString("bz");
        String kprq = jsonObject.getString("kprq"); //格式:yyyy年MM月dd日
        String xfmc = jsonObject.getString("xfmc");
        String xfsh = jsonObject.getString("xfsh");
        String xfdzdh = jsonObject.getString("xfdzdh");
        String xfyhzh = jsonObject.getString("xfyhzh");
        String gfmc = jsonObject.getString("gfmc");
        String gfsh = jsonObject.getString("gfsh");
        String gfdzdh = jsonObject.getString("gfdzdh");
        String gfyhzh = jsonObject.getString("gfyhzh");
        String fhr = jsonObject.getString("fhr");
        String skr = jsonObject.getString("skr");
        String kpr = jsonObject.getString("kpr");
        String hjje = jsonObject.getString("je").replace('￥', '¥');
        String hjse = jsonObject.getString("se").replace('￥', '¥');
        String jshjxx = jsonObject.getString("jshjxx");
        String jshjdx = jsonObject.getString("jshjdx");

        dataMap.put("jshjdx", jshjdx);
        dataMap.put("jshjxx", "¥" + jshjxx);

        dataMap.put("hjje", hjje);
        dataMap.put("hjse", hjse);
        dataMap.put("ceshi", "销项负数");

        dataMap.put("xfmc", xfmc);
        dataMap.put("xfsh", xfsh);
        dataMap.put("xfdzdh", xfdzdh);
        dataMap.put("xfyhzh", xfyhzh);
        dataMap.put("gfmc", gfmc);
        dataMap.put("gfdzdh", gfdzdh);
        dataMap.put("gfyhzh", gfyhzh);
        dataMap.put("fhr", fhr);
        dataMap.put("skr", skr);
        dataMap.put("kpr", kpr);

        if (gfsh.contains("0000000000")) {
            dataMap.put("gfsh", "");
        } else {
            dataMap.put("gfsh", gfsh);
        }

        //发票密文，用于填入发票的密码区
        int div = fpmw.length() / 4;
        if (fpmw.length() == 108 || fpmw.length() == 112) {
            dataMap.put("mmq1", fpmw.substring(0, div));
            dataMap.put("mmq2", fpmw.substring(div, div * 2));
            dataMap.put("mmq3", fpmw.substring(div * 2, div * 3));
            dataMap.put("mmq4", fpmw.substring(div * 3, div * 4));
        } else {
            dataMap.put("mmq1", fpmw);
        }
        dataMap.put("fpdm", fpdm);
        dataMap.put("fphm", fphm);
        dataMap.put("jqbh", jqbh);
        if (jym.length() == 20) {
            dataMap.put("jym", jym.substring(0, 5) + " " + jym.substring(5, 10) + " " +
                    jym.substring(10, 15) + " " + jym.substring(15, 20));
        } else {
            dataMap.put("jym", jym);
        }
        //// TODO: 2017/11/1 备注根据长度动态的分为最多四行
        dataMap.put("bz1", bz);
        dataMap.put("kprq_nian", kprq.substring(0, 4));
        dataMap.put("kprq_yue", kprq.substring(5, 7));
        dataMap.put("kprq_ri", kprq.substring(8, 10));

        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        //需要生成明细清单pdf文件
        if (jsonArray.size() > 5) {
            dataMap.put("wpmc1", "(详见销货清单)");
            String slv = jsonArray.getJSONObject(0).getString("slv");
            dataMap.put("slv1", slv);
            dataMap.put("je1", hjje.replace("¥", ""));
            dataMap.put("se1", hjse.replace("¥", ""));

        } else {//不需要生成明细清单pdf文件
            for (int i = 0; i < jsonArray.size(); i++) {
                int j = i + 1;
                JSONObject obj = jsonArray.getJSONObject(i);
                dataMap.put("wpmc" + j, obj.getString("hwmc"));
                dataMap.put("ggxh" + j, obj.getString("ggxh"));
                dataMap.put("dw" + j, obj.getString("dw"));

                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("dj"))) {
                    /*System.out.println("生成6位数的单价"+GeralBsiness.getTwoDecimal2(new BigDecimal(obj.getString("dj"))));*/
                    dataMap.put("dj" + j, GeralBsiness.getTwoDecimal2(new BigDecimal(obj.getString("dj"))));
                }
                boolean jeIsDj = false;
                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("sl"))) {
                    BigDecimal bsl = new BigDecimal(obj.getString("sl"));
                    if (bsl.intValue() == 0) {
                        dataMap.put("sl" + j, 1 + "");
                        jeIsDj = true;
                    } else {
                        dataMap.put("sl" + j, obj.getString("sl"));
                    }
                }

                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("je"))) {
                    double je = GeralBsiness.getTwoDecimal(new BigDecimal(obj.getString("je")));
                    DecimalFormat df = new DecimalFormat("##0.00");
                    dataMap.put("je" + j, df.format(je));
                    if (jeIsDj) {
                        //System.out.println("生成6位数的单价"+GeralBsiness.getTwoDecimal2(new BigDecimal(obj.getString("dj"))));
                        dataMap.put("dj" + j, GeralBsiness.getTwoDecimal2(new BigDecimal(df.format(je))));
                    }
                }

                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("se"))) {
                    double se = GeralBsiness.getTwoDecimal(new BigDecimal(obj.getString("se")));
                    DecimalFormat df = new DecimalFormat("##0.00");
                    dataMap.put("se" + j, df.format(se));
                }

                if (!org.springframework.util.StringUtils.isEmpty(obj.getString("slv"))) {
                    String slv = obj.getString("slv");
                    dataMap.put("slv" + j, slv);
                }
            }//for
        }

    }

    //组装json数据至红冲EfpInvoice,将存储至数据库
    public static void composezfInvoiceToDb(JSONObject jsonObject, EfpInvoice efpInvoice) {
        String jym = jsonObject.getString("jym");
        String mw = jsonObject.getString("mw");
        String fpdm = jsonObject.getString("fpdm");
        String fphm = jsonObject.getString("fphm");
        String jqbh = jsonObject.getString("jqbh");
        String bz = jsonObject.getString("bz");
        String kprq = jsonObject.getString("kprq");
        String xfmc = jsonObject.getString("xfmc");
        String xfsh = jsonObject.getString("xfsh");
        String xfdzdh = jsonObject.getString("xfdzdh");
        String xfyhzh = jsonObject.getString("xfyhzh");
        String gfmc = jsonObject.getString("gfmc");
        String gfsh = jsonObject.getString("gfsh");
        String gfdzdh = jsonObject.getString("gfdzdh");
        String gfyhzh = jsonObject.getString("gfyhzh");
        String fhr = jsonObject.getString("fhr");
        String skr = jsonObject.getString("skr");
        String kpr = jsonObject.getString("kpr");
        String hjje = jsonObject.getString("je").replace('￥', '¥');
        String hjse = jsonObject.getString("se").replace('￥', '¥');

        if (gfsh.contains("0000000000")) {
            gfsh = "";
        }
        efpInvoice.setMw(mw);
        efpInvoice.setHjje(new BigDecimal(hjje.substring(1, hjje.length())));
        efpInvoice.setHjse(new BigDecimal(hjse.substring(1, hjse.length())));

        efpInvoice.setFpJym(jym);
        efpInvoice.setXfmc(xfmc);
        efpInvoice.setXfsh(xfsh);
        efpInvoice.setXfdzDh(xfdzdh);
        efpInvoice.setXfyhmcYhzh(xfyhzh);
        efpInvoice.setGfmc(gfmc);
        efpInvoice.setGfsh(gfsh);
        efpInvoice.setGfdzDh(gfdzdh);
        efpInvoice.setGfyhmcYhzh(gfyhzh);
        efpInvoice.setFhr(fhr);
        efpInvoice.setSkr(skr);
        efpInvoice.setKpr(kpr);
        efpInvoice.setFpdm(fpdm);
        efpInvoice.setFphm(fphm);
        efpInvoice.setJqbh(jqbh);
        efpInvoice.setBz(bz);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dates = new Date();
        String createtime = formatDate.format(dates);
        efpInvoice.setCreateTime(new Date());
        efpInvoice.setKprq(DateUtils.getFormatDate("yyyy年MM月dd日", kprq));
    }

    //将所需红冲数据填入EfpInvoice,将存储至数据库 暂时注释
    public static void composezfInvoiceToDb(EfpZf efpZf, EfpInvoice efpInvoice) {

      /*  String kpJson = efpInvoiceCopy.getKpJson();
        JSONObject tableJsonObject = JSON.parseObject(kpJson);*/

       /* efpInvoice.setCzyId(efpZf.("czyId"));
        efpInvoice.setPhoneNo(efpInvoiceCopy.getParm3());
        efpInvoice.setCsmUnionid(efpInvoiceCopy.getParm6());
        efpInvoice.setWspzhm(efpInvoiceCopy.getParm5());*/

    }

    //组装json数据至多个红冲Qd,将存储至数据库
    public static void composezfInvoiceQdToDb(JSONObject jsonObject, List<EfpInvoiceQd> efpInvoiceQds) {
        String fpdm = jsonObject.getString("fpdm");
        String fphm = jsonObject.getString("fphm");
        String xfsh = jsonObject.getString("xfsh");
        String gfsh = jsonObject.getString("gfsh");
        if (gfsh.contains("0000000000")) {
            gfsh = "";
        }

        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            EfpInvoiceQd invoiceQd = new EfpInvoiceQd();
            invoiceQd.setFpdm(fpdm);
            invoiceQd.setFphm(fphm);
            invoiceQd.setXfNsrsbh(xfsh);
            invoiceQd.setGfNsrsbh(gfsh);

            invoiceQd.setWpMc(obj.getString("hwmc"));
            invoiceQd.setWpXh(obj.getString("ggxh"));
            if (obj.getString("dw") != null) {
                invoiceQd.setWpDw(obj.getString("dw"));
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("dj"))) {
                invoiceQd.setDj(new BigDecimal(obj.getString("dj")));
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("sl"))) {
                BigDecimal bsl = new BigDecimal(obj.getString("sl"));
                invoiceQd.setSl(bsl);
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("je"))) {
                invoiceQd.setJe(new BigDecimal(obj.getString("je")));
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("se"))) {
                invoiceQd.setSe(new BigDecimal(obj.getString("se")));
            }
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("slv"))) {
                String slv = obj.getString("slv");
                BigDecimal slvbig = new BigDecimal(slv.replace("%", ""));
                invoiceQd.setSlv(slvbig.divide(new BigDecimal(100)));
            }
            efpInvoiceQds.add(invoiceQd);
        }
    }

    public static void main(String[] args) {

        StringData data = StringData.stringlist("2017.01.01-2018-08-01A172SKT117MA172SKT", 8, 4, 37, 5);
        // System.out.println(data.getStrList());
        HashMap<String, String> dataMap = new HashMap<String, String>();
        //购方和机器编号
        dataMap.put("jqbh", "661619753581");
        //*dataMap.put("ceshi", "销项负数");*//
        dataMap.put("gfmc", "长城物业集团股份有限公司");
        dataMap.put("gfsh", "914403001922197869");
        dataMap.put("gfdzdh", "广东省给对方个人色然后就热太热很遗憾的士速递的士速递广东省给对方个人色然后就热太热很遗憾的士速递的士速递");
        dataMap.put("gfyhzh", "中国建设银行股份有限公司深圳沙河支行44250100004500000146中国建设银dfsdd呜呜呜US霍建华进货价赶回家峻峰是肯定");
        //密码区
        dataMap.put("sm", "销项参数");
        dataMap.put("mmq1", "12345678900000000000000000");
        dataMap.put("mmq2", "12345678900000000000000000");
        dataMap.put("mmq3", "abcdefghijklmnopqrstuvwxyz");
        dataMap.put("mmq4", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        //右上区
        dataMap.put("fpdm", "044031700111");
        dataMap.put("fphm", "72788903");
        dataMap.put("kprq_nian", "2017");
        dataMap.put("kprq_yue", "08");
        dataMap.put("kprq_ri", "19");
        dataMap.put("jym", "85409 78533 01459 19996");
        //销方区
        dataMap.put("xfmc", "饭饭得");
        dataMap.put("xfsh", "91440300691180142U");
        dataMap.put("xfdzdh", "深圳市福田区福田街道dsdfsf测试vessels测福安社区益田路5033号平安金融中心B29、平安金融中心116楼,金融中心116楼 0755-29163888");
        dataMap.put("xfyhzh", "银行5我个汉东湖等会我银行");

        //底部和备注区
        dataMap.put("skr", "张小强");
        dataMap.put("fhr", "张小强");
        dataMap.put("kpr", "张小强"); //对应正数发票代码:044031900111号码:27597221
        dataMap.put("bz1", "机器编号:499942307790\\n项目名称：佳华领汇广场二期二次装修工程项目地址：深圳市龙岗区五和大道和吉华路交汇处测试实施地方的但是犯得上电风扇豆瓣");
        dataMap.put("bz4", "这是备注测试");
        //物品明细区
        for (int i = 1; i <= 1; i++) {
            dataMap.put("wpmc" + i, "*计算机配套产品*罗技（Logitech）M185（M186）无线鼠标 黑色灰边M185（M186）无线鼠标 黑色灰边M185（M186）无线鼠标 黑色灰边M185（M186）无线鼠标 黑色灰边");
            dataMap.put("ggxh" + i, "MA172SKT117JKSSKDscnms");         //*  MA172SKT117MA172SKT117 餐费餐餐费 size(5)  2017.01.01-2018-08-01 size 8  餐费餐餐费餐餐费餐2017.01.01-2018-08-01*//*
            dataMap.put("dw" + i, "单位");
            dataMap.put("sl" + i, "0.9");
            String djs = "5.6603773";
            dataMap.put("dj" + i, "1.11");
            dataMap.put("je" + i, "545.66");
            dataMap.put("slv" +i, i+"%");
            dataMap.put("se" + i, "1.9");
        }


        //* dataMap.put("wpmc1", "(详见销货清单)");
    /*    dataMap.put("wpmc2", "餐费啊");
        dataMap.put("ggxh2", "型号");
        dataMap.put("dw2", "单位啊");
        dataMap.put("sl2", "1");*/

        // DecimalFormat df = new DecimalFormat("%.6f");
        //String dj = GeralBsiness.getTwoDecimal2(new BigDecimal(dj3));


        //*dataMap.put("je2", "32.52");
      /*  dataMap.put("slv2", "3%");
        dataMap.put("se2", "80");*/

        //物品金额计算区
        dataMap.put("hjje", "¥-5511.6");
        dataMap.put("hjse", "¥0.9");
        dataMap.put("kpr", "张小强");
        //String jshjdx = " " + PdfUtils.moneyNumberTocCh(new BigDecimal(192.63));
        dataMap.put("jshjdx", "(负数)陆圆整");
        dataMap.put("jshjxx", "¥-6.00");

        //需要签章的pdf文件路径 E:\feng\zsxx\pdfFile2.1\src\main\resources\pdfTemplate
        String tmplatePdfPath ="E:\\feng\\zsxx\\pdfFile2.1\\src\\main\\resources\\pdfTemplate\\深圳.pdf";
        // 签完章的pdf文件路径
        String outPdfPath ="E:\\feng\\zsxx\\pdfFile2.1\\src\\main\\resources\\pdfTemplate\\签章后.pdf";
        //二维码图片文件路径
        String QrCodeImgPath = "E:\\feng\\zsxx\\pdfFile2.1\\src\\main\\resources\\pdfSign\\生成的二维码.png";
        //ketstore文件路径  E:\feng\project\pdfFile2.1\src\main\resources\pdfSign
        String keyStorePath = "E:\\feng\\zsxx\\pdfFile2.1\\src\\main\\resources\\pdfSign\\Test.pfx";
        //ca图片路径
        String signImgPath = "E:\\feng\\zsxx\\pdfFile2.1\\src\\main\\resources\\pdfSign\\义乌市申通快递有限公司.png";
        //字体路径
        String fontPath = "E:\\feng\\zsxx\\pdfFile2.1\\src\\main\\resources\\pdfSign\\C9.ttf";

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
            InvoiceController C=new InvoiceController();

//           LogUtils.writeLogFileName("CreateDZp.txt","cescccccccccccss");

            PdfUtils.create(dataMap, QrCodeImgPath, tmplatePdfPath, signImgPath, outPdfPath, fontPath, chain, pk, "sign", "Ghent");
            System.out.println("生成完成！"+outPdfPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
