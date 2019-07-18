package utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.*;
import com.itextpdf.text.List;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import entity.TextSealPos;
import impl.GeralBsiness;
import model.EfpJpInvoiceCopy;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.util.*;

import static utils.PdfUtils.countLeng;

/**
 * 生成pdf版式清单文件
 */
public class PdfQdUtils {


    //共28行 36列
    //初始化文字坐标位置
    static HashMap<String, TextSealPos> initTextSealPos(HashMap<String, TextSealPos> posMap) {
        posMap.put("gfmc", new TextSealPos(93, 691, 9));
        posMap.put("xfmc", new TextSealPos(93, 667, 9));
        posMap.put("fpdm", new TextSealPos(171, 644, 9));
        posMap.put("fphm", new TextSealPos(295, 644, 9));
        posMap.put("totalNum", new TextSealPos(482, 643, 9));
        posMap.put("curNum", new TextSealPos(547, 644, 9));
        posMap.put("hjje", new TextSealPos(487, 168, 10));
        posMap.put("hjje2", new TextSealPos(487, 150, 10));
        posMap.put("hjse", new TextSealPos(584, 168, 10));
        posMap.put("hjse2", new TextSealPos(584, 150,10));
        posMap.put("bz", new TextSealPos(60, 133, 9));
        posMap.put("kprq", new TextSealPos(460, 88, 10));

        /*原始注释*/
     /*for (int i = 1; i <= 28; i++) {
          int j = i * 15;
          posMap.put("wpmc" + i, new TextSealPos(56, 608 - j, 9));
          posMap.put("ggxh" + i, new TextSealPos(230, 608 - j, 9));
          posMap.put("dw" + i, new TextSealPos(280, 608 - j, 9)); //285, 608 - j, 9
          posMap.put("sl" + i, new TextSealPos(355, 608 - j, 9));
          posMap.put("dj" + i, new TextSealPos(416, 608 - j, 9));
          posMap.put("je" + i, new TextSealPos(482, 608 - j, 9));
          posMap.put("slv" + i, new TextSealPos(510, 608 - j, 9));
          posMap.put("se" + i, new TextSealPos(580, 608 - j, 9));
          posMap.put("xh" + i, new TextSealPos(38, 608 - j, 9));
      }*/
        return posMap;
    }



    static void adJust(HashMap<String, String> dataMap,HashMap<String, TextSealPos> posMap){
        String[] keys = new String[]{"gfmc","xfmc"};
        for(String key : keys){
            //9号字体 最大宽度54  8号 60  7号70 根据长度切换字号，7号时长度还超过就变成2行  6号 80
            String value = dataMap.get(key);
            if(value!=null){
                System.out.println(key +":"+countLeng(value));
                int length = countLeng(value);
                if(length <= 90){
                    continue;
                }
                if(length <= 65){//改为8号字体
                    posMap.get(key).setSize(9);
                }else if(length <= 80){//改为7号字体
                    posMap.get(key).setSize(8);

                }else{//改为7号字体并变成2
                    JSONArray jsonArray = PdfQdUtils.textWidthSeg(value,90);
                    TextSealPos textSealPos = posMap.get(key);
                    textSealPos.setSize(textSealPos.getSize()-2);
                    if(jsonArray.size()>2){//增加一行
                        dataMap.put(key,jsonArray.getString(0));
                        textSealPos.setY(textSealPos.getY()+4);
                        posMap.put(key+"Add",new TextSealPos(textSealPos.getX(),textSealPos.getY()-9,textSealPos.getSize()));
                        dataMap.put(key+"Add",jsonArray.getString(1));
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


    //填充文字至pdf文件模板
    static void addTextSeal(PdfStamper stamper, HashMap<String, String> dataMap,HashMap<String, TextSealPos> posMap) throws IOException, DocumentException {
        initTextSealPos(posMap);
        adJust(dataMap,posMap);
        PdfContentByte over = stamper.getOverContent(1);
        BaseFont bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        for (String key : dataMap.keySet()) {
            if (!posMap.containsKey(key)) {
                continue;
            }
            TextSealPos textSealPos = posMap.get(key);
            over.beginText();
            over.setFontAndSize(bf, textSealPos.getSize());
            //对齐方式以(x,y)点为中心、显示的文字、x坐标增加则右移动、y坐标增加则上移、旋转角度
            if (key.contains("sl") || key.contains("dj") || key.contains("je") || key.contains("se")) {//右对齐
                over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else if (key.contains("slv")) {//居中
                over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);

            } else {//左对齐
                over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            }
            over.endText();
        }
    }

    public static JSONArray textWidthSeg(String str,int length) {
        //字符串宽度
        int count = 0;
        char[] c = str.toCharArray();
        //字符串位置分割的起点和终点
        int start = 0;
        int end = 0;
        //间隔宽度距离
        int widLength = length;
        //上一次分割的宽度位置
        int recPos = 0;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < c.length; i++) {
            String len = Integer.toBinaryString(c[i]);
            if (len.length() > 8) {  //8
                count += 2;
            } else {
                count++;
            }
            if(Math.abs(count-widLength)<=1){
                recPos = count;
                end = i;
                jsonArray.add(str.substring(start,end));
                start = i;
                widLength += length;
            }
        }
        if(count>recPos){
            jsonArray.add(str.substring(end,c.length));
        }
        return jsonArray;
    }

    //dataMap,posMap,tmplatePdfPath,signImgPath, outPdfPath,fontPath,chain, pk, reason, location
    /*qdDataMap,posMap,tmplatePdfPath, fontPath, chain, pk, "sign", "Ghent"*/
   /* dataMap,posMap,tmplatePdfPath,tmpOutPdfPath, fontPath, chain, pk, "sign", "Ghent"*/

    public static void create(HashMap<String, String> dataMap, HashMap<String, TextSealPos> posMap,
                              String tmplatePdfPath, String signImgPath,String outPdfPath,
                              String fontPath,Certificate[] chain, PrivateKey pk,
                              String reason, String location) throws IOException, DocumentException, GeneralSecurityException {
        PdfReader reader = new PdfReader(tmplatePdfPath);
        //创建签章工具PdfStamper ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
/*
*  PdfReader reader = new PdfReader(tmplatePdfPath);
        PdfStamper stamper =null;
* */
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper stamper= new PdfStamper(reader, bos);

        addTextSealx(stamper,dataMap,fontPath);
        stamper.close();
        reader.close();
        OutputStream fos = new FileOutputStream(outPdfPath);
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();
        bos.close();
    }






    public static java.util.List<String> splitStr(String str, int length){
        java.util.List<String> strList=new ArrayList<String>();
        int begin=0;

        while(str.length()>begin){
            int strLength=length;
            if(begin+strLength>str.length()){
                strLength=str.length()-begin;
            }
            strList.add(str.substring(begin, begin+strLength));
            begin+=length;
        }
        return strList;
    }

    static void addTextSealx(PdfStamper stamper, HashMap<String, String> dataMap,String fontPath) throws IOException, DocumentException {
        HashMap<String, TextSealPos> posMap = initTextSealPos();
        initTextSealPos(posMap);
        adJust(dataMap,posMap);
        PdfContentByte over = stamper.getOverContent(1);
        BaseFont bf;
        java.util.List<String> list=new ArrayList<String>();
        list.add("wpmc");
        list.add("ggxh");
        list.add("dw");
        list.add("sl");
        list.add("dj");
        list.add("je");
        list.add("slv");
        list.add("se");
        list.add("xh");
        for (String key : dataMap.keySet()) {
            boolean isContinue=false;
            for(String s:list){
                if(key.length()>s.length()){
                String check=key.substring(0,s.length());
                if(check.equals(s))
                {
                    isContinue=true;
                    break;
                }
                }
            }
            if (!posMap.containsKey(key)||isContinue) {
                continue;
            }
            //修改字体
            if (key.contains("fsh") ||
                    key.contains("mmq") ||
                    key.contains("xh")
                    ) {
                bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }else {
                bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
            }

            TextSealPos textSealPos = posMap.get(key);
            over.beginText();
            over.setFontAndSize(bf, textSealPos.getSize());
            if (key.contains("sl") || key.contains("dj") || key.contains("je") || key.contains("slv")
                    || key.contains("se")) {
                over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else if (key.contains("mmq") ||  key.contains("dw")) {
                over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            } else {
                over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(key), textSealPos.getX(), textSealPos.getY(), 0);
            }
            over.endText();
        }
        int ling=1;
        bf = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
        for(int i=1;i<=32;i++){
            boolean isLine=false;
            int addLine=0;
            int j = ling * 13;
            if (ling>30){
                break;
            }
            for(String str:list){
                String name=str+i;
                if(dataMap.containsKey(name)){
                    if(!isLine){
                        ling++;
                        isLine=true;
                        posMap.put("dw" + i, new TextSealPos(285, 610 - j, 9)); //285, 608 - j, 9
                        posMap.put("sl" + i, new TextSealPos(355, 608 - j, 9));
                        posMap.put("dj" + i, new TextSealPos(416, 608 - j, 9));
                        posMap.put("je" + i, new TextSealPos(482, 608 - j, 9));
                        posMap.put("slv" + i, new TextSealPos(510,608 - j, 9));
                        posMap.put("se" + i, new TextSealPos(580,608 - j, 9));
                        /*posMap.put("kprq", new TextSealPos(460, 88 - j,10));*/
                        posMap.put("xh" + i, new TextSealPos(38,608 -j, 9));
                       /* posMap.put("bz", new TextSealPos(60, 133, 9));*/
                   /*
                        posMap.put("hjje", new TextSealPos(487, 168, 10));
                        posMap.put("hjje2", new TextSealPos(487, 150, 10));
                        posMap.put("hjse", new TextSealPos(584, 168, 10));
                        posMap.put("hjse2", new TextSealPos(584, 150, 10));
                        posMap.put("bz", new TextSealPos(60, 133, 9));
                        \posMap.put("xh" + i, new TextSealPos(38, 608 -j, 9)
                        */
                    }
                    posMap.put("wpmc" + i, new TextSealPos(56, 608 - j, 10));
                    posMap.put("ggxh" + i, new TextSealPos(245, 614 - j, 9));//原始195, 237 - j, 9
                    TextSealPos textSealPos= posMap.get(name);
                    String value=dataMap.get(name);
                    over.beginText();
                    over.setFontAndSize(bf,textSealPos.getSize());
                    if (name.contains("sl") || name.contains("dj") || name.contains("je") || name.contains("slv")
                            || name.contains("se")) {
                        over.showTextAligned(Element.ALIGN_RIGHT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    } else if ( name.contains("dw")) {
                        over.showTextAligned(Element.ALIGN_CENTER, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    }else if(str.equals("ggxh")){
                        int length=value.length();
                        int showLength=6;
                        int fontSize=8;
                        if(length>6){
                            fontSize=7;
                            showLength=7;
                        }
                        JSONArray listStr = PdfQdUtils.textWidthSeg(value,showLength*2);
                        for(int index=0;index<listStr.size();index++){
                            String s=listStr.getString(index);
                            over.setFontAndSize(bf,fontSize);
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX()-25,
                                    textSealPos.getY()-(index*10)-2, 0);
                            over.endText();
                            over.beginText();
                        }
                        int num=listStr.size();
                        //2 行显示3条数据
                        //1 行显示2条数据 调整
                        int v=new BigDecimal((num/1.25)).setScale(0, RoundingMode.HALF_UP).intValue();
                        if(v>0&&((v-1))>addLine){
                            addLine=(int) ((v-1));
                        }
                    }else if(str.equals("wpmc")){
                        int length=value.length();
                        int showLength=18;
                        int fontSize=9;
                        if(length>26){
                            fontSize=8;
                            showLength=19;
                        }
                        JSONArray listStr = PdfQdUtils.textWidthSeg(value,showLength*2);
                        for(int index=0;index<listStr.size();index++){
                            String s=listStr.getString(index);
                            over.setFontAndSize(bf,fontSize);
                            over.showTextAligned(Element.ALIGN_LEFT, s, textSealPos.getX(),
                                    textSealPos.getY()-(index*12), 0);
                            over.endText();
                            over.beginText();
                        }
                        int num=listStr.size();
                        if(num>0&&(num-1)>addLine){
                            System.out.println(num+":"+length);
                            addLine=num-1;
                        }
                    } else{
                        over.showTextAligned(Element.ALIGN_LEFT, dataMap.get(name), textSealPos.getX(), textSealPos.getY(), 0);
                    }

                    over.endText();
                }
            }
            ling+=addLine;
        }
    }

    //初始化文字位置
    static HashMap<String, TextSealPos> initTextSealPos() {
        HashMap<String, TextSealPos> posMap = new HashMap<String, TextSealPos>();
        posMap.put("jqbh", new TextSealPos(75, 322, 9));
        posMap.put("gfmc", new TextSealPos(93, 691,9));
        posMap.put("gfsh", new TextSealPos(128, 128, 12));
        posMap.put("gfdzdh", new TextSealPos(108, 270, 9));
        posMap.put("gfyhzh", new TextSealPos(108, 255, 9));

        posMap.put("totalNum", new TextSealPos(482, 643, 9));
        posMap.put("curNum", new TextSealPos(547, 644, 9));

        posMap.put("xfmc", new TextSealPos(93, 667, 9));
        posMap.put("xfsh", new TextSealPos(108, 76, 12));
        posMap.put("xfdzdh", new TextSealPos(108, 63, 9));
        posMap.put("xfyhzh", new TextSealPos(108, 49, 9));

        posMap.put("mmq1", new TextSealPos(480, 298, 12));
        posMap.put("mmq2", new TextSealPos(480, 283, 12));
        posMap.put("mmq3", new TextSealPos(480, 268, 12));
        posMap.put("mmq4", new TextSealPos(480, 253, 12));

        posMap.put("fpdm", new TextSealPos(171, 644, 9));
        posMap.put("fphm", new TextSealPos(295, 644, 9));
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
        posMap.put("bz", new TextSealPos(60, 133, 9));
        //posMap.put("bz1", new TextSealPos(370, 90, 11));

        for (int i = 1; i <= 26; i++) {
            int j = i * 15;
            posMap.put("wpmc" + i, new TextSealPos(28, 237 - j, 9));
            posMap.put("ggxh" + i, new TextSealPos(150,260- j, 9)); //原始195, 237 - j, 9
            posMap.put("dw" + i, new TextSealPos(236, 237 - j, 9));
            posMap.put("sl" + i, new TextSealPos(318, 237 - j, 9));
            posMap.put("dj" + i, new TextSealPos(390, 237 - j, 9));
            posMap.put("je" + i, new TextSealPos(478, 237 - j, 9));
            posMap.put("slv" + i, new TextSealPos(502, 237 - j, 9));
            posMap.put("se" + i, new TextSealPos(590, 237 - j, 9));
            posMap.put("xh" + i, new TextSealPos(38, 608 - j, 9));
        }
        return posMap;
    }

    public static  String getRand(int begin,int end){
        Random ran=new Random();
        int length=begin+ ran.nextInt(end-begin);
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            if(ran.nextInt(500)%2==0){
                sb.append("啊");
            }else{
                sb.append("a");
            }
        }
        return  sb.toString();
    }
    public static void composeMapToPdf(JSONObject jsonObject, HashMap<String, String> dataMap,int startPos,int endPos,HashMap<String, TextSealPos> posMap){

        String gfmc = jsonObject.getString("gfmc");
        String xfmc = jsonObject.getString("xfmc");
        String fpdm = jsonObject.getString("fpdm");
        String fphm = jsonObject.getString("fphm");
        String hjje = jsonObject.getString("je").replace("￥","");
        String hjse = jsonObject.getString("se").replace("￥","");
        String kprq = jsonObject.getString("kprq"); //格式:yyyy年MM月dd日
        String bz = "";

        dataMap.put("gfmc", gfmc);
        dataMap.put("xfmc", xfmc);
        dataMap.put("fpdm", fpdm);
        dataMap.put("fphm", fphm);

        dataMap.put("kprq", kprq);
        dataMap.put("bz", bz);

        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");

        if(jsonArray.size() == endPos){
            dataMap.put("hjje", hjje);
            dataMap.put("hjje2", hjje);
            dataMap.put("hjse", hjse);
            dataMap.put("hjse2", hjse);
        }
        //当前行数
        int row = 0;
        for (int i = startPos; i < endPos; i++) {
            int xb = i-startPos+1; //下标
            JSONObject obj = jsonArray.getJSONObject(i);

            //单页容纳最大明细列数
            int maxCol = 36;
            String wpmc = obj.getString("hwmc");
            int size = countLeng(wpmc);
//            int size = wpmc.length();

            //确定明细第一行的位置
            int y = 608 - ((row+1)*15);
            posMap.put("wpmc" + xb, new TextSealPos(56, y, 9));//明细名称的第1行位置
            posMap.put("ggxh" + xb, new TextSealPos(230, y, 9));
            posMap.put("dw" + xb, new TextSealPos(285, y, 9));
            posMap.put("sl" + xb, new TextSealPos(355, y, 9));
            posMap.put("dj" + xb, new TextSealPos(416, y, 9));
            posMap.put("je" + xb, new TextSealPos(482, y, 9));
            posMap.put("slv" + xb, new TextSealPos(510, y, 9));
            posMap.put("se" + xb, new TextSealPos(580, y, 9));
            posMap.put("xh" + xb, new TextSealPos(38, y, 9));
            row++;

            JSONArray splArray = PdfQdUtils.textWidthSeg(wpmc,maxCol);
            //当前明细名称长度超过（大于）1行
            if(size > maxCol){
                //写入明细名称第一行数据
//                dataMap.put("wpmc" + xb, wpmc.substring(0,maxCol));
                dataMap.put("wpmc" + xb, splArray.getString(0));

                //当前明细名称占rn行
                int rn = size % maxCol == 0 ? size / maxCol : size / maxCol + 1;

                //从第二行开始循环明细的行
                for(int k=2;k<=rn;k++){
                    y = 608 - ((row+1)*15);
                    posMap.put("wpmc" + xb + (k-1), new TextSealPos(56, y, 9));//明细名称的第k行位置
                    row++;
                    dataMap.put("wpmc" + xb + (k-1), splArray.getString(k-1));
//                    if(k*maxCol<=size){
//                        dataMap.put("wpmc" + xb + (k-1), wpmc.substring(maxCol*(k-1),maxCol*k));//明细名称的第k行的内容
//                    }else {
//                        dataMap.put("wpmc" + xb + (k-1), wpmc.substring(maxCol*(k-1),size));//明细名称的第k行的内容
//                    }
                }

            }else {//当前明细名称长度不超过1行
                dataMap.put("wpmc" + xb, splArray.getString(0));
            }


            dataMap.put("ggxh" + xb, obj.getString("ggxh"));
            dataMap.put("dw" + xb, obj.getString("dw"));
//            dataMap.put("xh" + xb, xb + "");
            dataMap.put("xh" + xb, (i+1) + "");
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("dj"))) {
                //修改单价
               /* System.out.println(GeralBsiness.getTwoDecimal2(new BigDecimal(obj.getString("dj"))));*/
                dataMap.put("dj" + xb, GeralBsiness.getTwoDecimal2(new BigDecimal(obj.getString("dj"))));
            }
            boolean jeIsDj = false;
            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("sl"))) {
                BigDecimal bsl = new BigDecimal(obj.getString("sl"));
                if(bsl.intValue()==0){
                    dataMap.put("sl" + xb, 1+"");
                    jeIsDj = true;
                }else {
                    dataMap.put("sl" + xb, obj.getString("sl"));
                }
            }

            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("je"))) {
                double je = GeralBsiness.getTwoDecimal(new BigDecimal(obj.getString("je")));
                DecimalFormat df = new DecimalFormat("##0.00");
                dataMap.put("je" + xb, df.format(je));
                if(jeIsDj){
                    dataMap.put("dj" + xb, df.format(je));
                }
            }

            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("se"))) {
                double se = GeralBsiness.getTwoDecimal(new BigDecimal(obj.getString("se")));
                DecimalFormat df = new DecimalFormat("##0.00");
                dataMap.put("se" + xb, df.format(se));
            }

            if (!org.springframework.util.StringUtils.isEmpty(obj.getString("slv"))) {
                String slv = obj.getString("slv");
                dataMap.put("slv" + xb, slv);
            }
        }//for

    }


    public static JSONObject countTotalPageNum(JSONObject jsonObject){
        //单页容纳最大明细行数
        int maxRow = 28;
        //单页容纳最大明细列数
        int maxCol = 36;
        //计算当前json数据占用行数
        int row=0;
        int xb = 1;
        JSONArray sizeArray = new JSONArray();
        JSONArray jsonArray = jsonObject.getJSONArray("mxxx");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String wpmc = obj.getString("hwmc");
            String ggxh = obj.getString("ggxh");

            //wpmc
            //ggxh
            int size = countLeng(wpmc);
         //   int sizey=countLeng(ggxh);

//            int size = wpmc.length();

            if(size > maxCol){
                //当前明细所占用行数
                int rn = size % maxCol == 0 ? size / maxCol : size / maxCol + 1;
                row += rn;
            }else {
                row++;
            }

            if(row > maxRow * xb){
                xb++;
                sizeArray.add(i+1);
            }
        }
        if(row<=maxRow * xb){
            sizeArray.add(jsonArray.size());
        }
        JSONObject returnObj = new JSONObject();
        int totalNum = row % maxRow == 0 ? row / maxRow : row / maxRow + 1;
        returnObj.put("totalNum",totalNum);
        returnObj.put("dolength",sizeArray);
        return returnObj;
    }

    public static void main(String[] args) {

        HashMap<String, String> dataMap = new HashMap<String, String>();
        //int j=27;

        //购方和机器编号
        dataMap.put("gfmc", "深圳市凯福瑞科技有限公司科技有限公司科技有限公司科技");
        dataMap.put("xfmc", "深圳市凯福瑞科技有限公司科技有限公司科技有限公司科技");
        dataMap.put("fpdm", "035021700111");
        dataMap.put("fphm", "04931467");
        dataMap.put("totalNum", "1");
        dataMap.put("curNum", "1");
        for (int i = 1; i <=25; i++) {
            dataMap.put("wpmc" + i,getRand(5,40));
            dataMap.put("ggxh" + i, getRand(7,40));
            dataMap.put("dw" + i, "单位1");
            dataMap.put("sl" + i, "1");
            String dj3="-132.15646469";
            dataMap.put("dj" + i, GeralBsiness.getTwoDecimal2(new BigDecimal(dj3)));
            dataMap.put("je" + i, "-132.52");
            dataMap.put("slv" + i, i + "%");
            dataMap.put("se" + i, "4.72");
            dataMap.put("xh" + i, i + "");
        }
        dataMap.put("hjje", "-16.98");
        dataMap.put("hjje2","-16.98");
        dataMap.put("fsh", "fsh");
        dataMap.put("mmq", "mmq");
        dataMap.put("kprq", "2017年06月13日");
        dataMap.put("hjse", "-20.18");
        dataMap.put("hjse2", "-20.18");
        dataMap.put("bz", "备注备注备备注备s");


        //需要签章的pdf文件路径
        String tmplatePdfPath = "E:\\feng\\project\\pdfFile2.1\\src\\main\\resources\\pdfTemplate\\商品清单模板.pdf";
        // 签完章的pdf文件路径
        String outPdfPath = "E:\\feng\\project\\pdfFile2.1\\src\\main\\resources\\pdfTemplate\\签章后.pdf";
        //二维码图片文件路径
        String QrCodeImgPath = "E:\\feng\\project\\pdfFile2.1\\src\\main\\resources\\pdfSign\\生成的二维码.png";
        //ketstore文件路径
        String keyStorePath = "E:\\feng\\project\\pdfFile2.1\\src\\main\\resources\\pdfSign\\Test.pfx";
        //ca图片路径
        String signImgPath = "E:\\feng\\project\\pdfFile2.1\\src\\main\\resources\\pdfSign\\义乌市申通快递有限公司.png";
        //字体路径
        String fontPath = "E:\\feng\\project\\pdfFile2.1\\src\\main\\resources\\pdfSign\\C9.ttf";
        HashMap<String, TextSealPos> posMap = PdfUtils.initTextSealPos();

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


        PdfQdUtils.create(dataMap,posMap,tmplatePdfPath,signImgPath, outPdfPath,fontPath,chain, pk, reason, location);

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("完成");
    }

    public static KeyStore signCheck(String keyStorePath, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(keyStorePath), password.toCharArray());
        return ks;
    }


}
