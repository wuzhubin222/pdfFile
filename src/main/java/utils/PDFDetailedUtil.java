package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


/**
 * PDF明细
 * @author Administrator
 *
 */
public class PDFDetailedUtil {
	
	public static void showText(PdfContentByte over,BaseFont font,float x,float y,String data,int Element,int fontSize){
		//if(data!=null){
			over.beginText();
			over.setFontAndSize(font, fontSize);
	    	over.showTextAligned(Element,data,x, y, 0);
			over.endText();
		//}
	}
	/**
	 * 固定数据绘画
	 * @param over
	 * @param data
	 * @param fontPath
	 * @throws IOException 
	 * @throws  Exception 
	 */
	public static void showFixed(PdfContentByte over, JSONObject data, String fontPath, BaseFont readFont, BaseFont defaultFont)  {
		int left=Element.ALIGN_LEFT;
		int right=Element.ALIGN_RIGHT; 
		showText(over,defaultFont,93,667,data.getString("xfmc"),left,9);  
		showText(over,defaultFont,93,691,data.getString("gfmc"),left,9);
		showText(over,defaultFont,171, 644,data.getString("fpdm"),left,9); 
		showText(over,defaultFont,295, 644,data.getString("fphm"),left,9); 
		//当前页数  不能再这里设置
//		showText(over,defaultFont,93,667,data.getString("totalNum"),left,9); 
//		showText(over,defaultFont,93,667,data.getString("curNum"),left,9); 
		/*金额绘画*/
		showText(over,defaultFont,487, 168,data.getString("je")/*.replace("￥", "")*/,right,10);
		showText(over,defaultFont,487, 150,data.getString("je")/*.replace("￥", "")*/,right,10);
		showText(over,defaultFont,584, 168,data.getString("se")/*.replace("￥", "")*/,right,10);
		showText(over,defaultFont,584, 150,data.getString("se")/*.replace("￥", "")*/,right,10);
		showText(over,defaultFont,60,133,data.getString("bz"),left,9); 		//备注
		showText(over,defaultFont,460,88,data.getString("kprq"),left,10); 	//日期 
	}
	private static String getDecima(String value,int leve){
			if("".equals(value)||value==null){
				return "";
			}
		  BigDecimal b=	new BigDecimal(value);
	      return b.setScale(leve, BigDecimal.ROUND_HALF_UP).toString();
	}
	/**
	 * 非固定模板数据绘画
	 * @param over
	 * @param data
	 * @param fontPath
	 * @param index	上一行的位置
	 * @return	返回当前绘画的位置
	 */


	public static int showDynamic(PdfContentByte over,JSONObject data,String fontPath,int index,BaseFont readFont,BaseFont defaultFont){
		int y=595;
		int length= data.getJSONArray("mxxx").size();
		int i=index;
		for(  i=index;i<length;i++){
			System.out.println(i);
			JSONObject item=data.getJSONArray("mxxx").getJSONObject(i);
			/*
			 * 单独计算	ggxh和hwmc 的计算
			 * */
			String ggxh=item.getString("ggxh");
			String hwmc=item.getString("hwmc");
			int maxy=13;
			int ggxhShow=ggxh.length()>6?7:6;
			int ggxhSize=ggxh.length()>6?7:8;
			int hwmcShow=hwmc.length()>26?19:18;
			int hwmcSize=hwmc.length()>26?9:10;

//			int hwmcShow=ggxh.length()>26?19:18;
//			int hwmcSize=ggxh.length()>26?9:8;
//			int ggxhShow=hwmc.length()>6?7:6;
//			int ggxhSize=hwmc.length()>6?7:8;
			StringData stringData=
					StringData.stringlist(hwmc, 9,8, 160, 100);

			JSONArray ggxhList = PdfQdUtils.textWidthSeg(ggxh,ggxhShow*2);
			JSONArray hwmcList = PdfQdUtils.textWidthSeg(hwmc,hwmcShow*2);
			int ggxhLine=new BigDecimal((ggxhList.size()/1.25)).setScale(0, RoundingMode.HALF_UP).intValue();
			if(ggxhLine>stringData.getStrList().size()){
				maxy=maxy*ggxhLine;
			}else{
				maxy=maxy*stringData.getStrList().size();
			}
			// 超出绘画的范围 不进行绘画
			// 当前绘画超过范围 无法进行绘画  必须到下一页进行绘画
			if(y-maxy<179+13){
				i--;
				break;
			}
			for(int j=0;j<ggxhList.size();j++){
				showText(over, defaultFont, 220,
						y-(j*10)+4, ggxhList.getString(j), Element.ALIGN_LEFT, ggxhSize);
			}
			for(int j=0;j<stringData.getStrList().size();j++){
				showText(over, defaultFont, 56,
						y-(j*12), stringData.getStrList().get(j), Element.ALIGN_LEFT, stringData.getFontSize());
			}


			//ggxh,hwmc   (i-index+1+index)
			showText(over, defaultFont,38, y,(i+1)+"",Element.ALIGN_LEFT, 9);
			showText(over, defaultFont,285,y+2, item.getString("dw"), Element.ALIGN_CENTER, 9);
			showText(over, defaultFont,482,y,
					getDecima(item.getString("je"),2),
					Element.ALIGN_RIGHT, 9);
			showText(over, defaultFont,416,y,
					getDecima(item.getString("dj"),6),
					Element.ALIGN_RIGHT, 9);
			showText(over, defaultFont,580,y ,getDecima( item.getString("se"),2),Element.ALIGN_RIGHT, 9);
			System.out.println(item);
			showText(over, defaultFont,355,y, item.getString("sl"), Element.ALIGN_RIGHT, 9);
			showText(over, defaultFont,510,y, item.getString("slv"), Element.ALIGN_RIGHT, 9);
			y=y-maxy;
			if(i-index>30){
				break;
			}
		}
		return index+i;
	}


	public static int addText(PdfStamper stamper, JSONObject data, String fontPath, int index) throws  Exception, Exception{
        PdfContentByte over = stamper.getOverContent(1);
        BaseFont readFont=BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		BaseFont defaultFont=FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
		showFixed(over,data,fontPath,readFont,defaultFont);
		return  showDynamic(over,data,fontPath,index,readFont,defaultFont); 
	}
	/*判断空字符串*/
	public static void ext(JSONObject data,JSONObject ext){
		for(String s:ext.keySet()){
			Object o=data.get(s);
			Object o1=ext.get(s);
			if(o==null){
				data.put(s,o1);
			}

			if(o instanceof  JSONArray && o1 instanceof  JSONObject){
				JSONArray a1=(JSONArray) o;
				for(int i=0;i<a1.size();i++){
					Object o2=a1.get(i);
					if(o2 instanceof  JSONObject){
						ext((JSONObject)o2,(JSONObject) o1);
					}
				}
			}
		}
	}
	public  static void create(JSONObject data,
                               String tmplatePdfPath,
                               String fontPath, PDFDetailedFileName pdfdetailedFileName) throws  Exception, GeneralSecurityException {
		 JSONObject o=JSON.parseObject("{\n" +
				 "    \"mxxx\": {\n" +
				 "        \"dj\": \" \",\n" +
				 "        \"dw\": \" \",\n" +
				 "        \"ggxh\": \" \",\n" +
				 "        \"hwmc\": \"  \",\n" +
				 "        \"je\": \" \",\n" +
				 "        \"se\": \" \",\n" +
				 "        \"sl\": \" \",\n" +
				 "        \"slv\": \" \"\n" +
				 "    }\n" +
				 "}");

		 ext(data,o);
		 int index=0; 
		 int length= data.getJSONArray("mxxx").size();
		 List<PDFDetailedData> list=new ArrayList<PDFDetailedData>();
		 while(index<length){
			PdfReader reader = new PdfReader(tmplatePdfPath); 
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PdfStamper stamper= new PdfStamper(reader, bos);   
			list.add(new PDFDetailedData(reader,bos,stamper));
			try {
				index=addText(stamper,data,fontPath,index);
			} catch (Exception e) { 
				e.printStackTrace();
				throw  e;
			}  	
	 	}
		BaseFont defaultFont=FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED).getBaseFont();
		for(int i=0;i<list.size();i++){
			// 页码绘画
			showText(list.get(i).getStamper().getOverContent(1),
					defaultFont,482, 643,list.size() +"",Element.ALIGN_LEFT,9); 
			// 页码绘画
			showText(list.get(i).getStamper().getOverContent(1),
					defaultFont,547, 644,(i+1)+"",Element.ALIGN_LEFT,9);  
			list.get(i).getStamper().close();
			list.get(i).getReader().close();
			OutputStream fos = new FileOutputStream(pdfdetailedFileName.getFileName());
			fos.write(list.get(i).getBos().toByteArray());
			fos.flush();
			fos.close();
			list.get(i).getBos().close(); 
		}
	}
	  
	public interface PDFDetailedFileName{
		public String getFileName();
	}

	public static String txt2String(File file){
		StringBuilder result = new StringBuilder();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
			String s = null;
			while((s = br.readLine())!=null){//使用readLine方法，一次读一行
				result.append(System.lineSeparator()+s);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result.toString();
	}
	public static void main(String ...args) throws  Exception{

		String dzfpResult="{\n" +
				"    \"dzfpresult\": {\n" +
				"        \"bz\": \"\",\n" +
				"        \"fhr\": \"梁明珠\",\n" +
				"        \"fpdm\": \"044031900111\",\n" +
				"        \"fphm\": \"32218550\",\n" +
				"        \"gfdzdh\": \"\",\n" +
				"        \"gfmc\": \"高女士\",\n" +
				"        \"gfsh\": \"000000000000000\",\n" +
				"        \"gfyhzh\": \"\",\n" +
				"        \"je\": \"¥23.72\",\n" +
				"        \"jqbh\": \"661821139876\",\n" +
				"        \"jshjdx\": \"ⓧ贰拾陆元捌角\",\n" +
				"        \"jshjxx\": \"26.80\",\n" +
				"        \"jym\": \"77549156542935977149\",\n" +
				"        \"kpr\": \"匡双铃\",\n" +
				"        \"kprq\": \"2019年05月29日\",\n" +
				"        \"mw\": \"/+<*+38<4622*-812<*>823<0-8/>>*97435*>/<<<73+*64-<-04184+*365--56/-52>*-177*>3*67+0<4</0*97435*>/<<<73+*0/5+\",\n" +
				"        \"mxxx\": [\n" +
				"            {\n" +
				"                \"dj\": \"18.141592920353983\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
			//	"                \"hwmc\": \"*焙烤食品*利拉比利时风味饼干焦糖味400g\",\n" +
				"                \"je\": \"18.14\",\n" +
				"                \"se\": \"2.36\",\n" +
				"                \"sl\": \"1\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dj\": \"\",\n" +
				"                \"sl\": \"\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*焙烤食品*利拉比利时风味饼干焦糖味400g\",\n" +
				"                \"je\": \"-14.3\",\n" +
				"                \"se\": \"-1.86\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dj\": \"31.858407079646021\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*乳制品*12 礼盒装\",\n" +
				"                \"je\": \"31.86\",\n" +
				"                \"se\": \"4.14\",\n" +
				"                \"sl\": \"1\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dj\": \"\",\n" +
				"                \"sl\": \"\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*乳制品*12 礼盒装\",\n" +
				"                \"je\": \"-25.12\",\n" +
				"                \"se\": \"-3.27\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dj\": \"19.380530973451329\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*焙烤食品*Orion 好丽友 营养早餐点心零食 注心 下午茶 网红零食 蛋黄派12枚276g/盒\",\n" +
				"                \"je\": \"19.38\",\n" +
				"                \"se\": \"2.52\",\n" +
				"                \"sl\": \"1\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dj\": \"\",\n" +
				"                \"sl\": \"\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*焙烤食品*Orion 好丽友 营养早餐点心零食 注心 下午茶 网红零食 蛋黄派12枚276g/盒\",\n" +
				"                \"je\": \"-15.28\",\n" +
				"                \"se\": \"-1.99\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dj\": \"11.061946902654869\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*日用杂品*BLD贝览得化妆棉卸妆棉轻薄轻柔双面双效三层 独立包装便携款72片\",\n" +
				"                \"je\": \"11.06\",\n" +
				"                \"se\": \"1.44\",\n" +
				"                \"sl\": \"1\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dj\": \"\",\n" +
				"                \"sl\": \"\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*日用杂品*BLD贝览得化妆棉卸妆棉轻薄轻柔双面双效三层 独立包装便携款72片\",\n" +
				"                \"je\": \"-8.72\",\n" +
				"                \"se\": \"-1.13\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dj\": \"31.769911504424780\",\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*茶及饮料*15包）\",\n" +
				"                \"je\": \"31.77\",\n" +
				"                \"se\": \"4.13\",\n" +
				"                \"sl\": \"1\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"dw\": \"\",\n" +
				"                \"ggxh\": \"\",\n" +
				"                \"hwmc\": \"*茶及饮料*15包）\",\n" +
				"                \"je\": \"-25.07\",\n" +
				"                \"se\": \"-3.26\",\n" +
				"                \"slv\": \"0.13\"\n" +
				"            }\n" +
				"        ],\n" +
				"        \"qrm\": \"01,10,044031900111,32218550,23.72,20190529,77549156542935977149,0285,\",\n" +
				"        \"qrm_bmp\": \"\",\n" +
				"        \"se\": \"¥3.08\",\n" +
				"        \"skr\": \"陈跃\",\n" +
				"        \"xfdzdh\": \"深圳市龙华区民治街道北站社区樟坑华侨新村彩悦大厦1010 13316575146\",\n" +
				"        \"xfmc\": \"深圳惠聚天下网络科技有限公司\",\n" +
				"        \"xfsh\": \"91440300MA5F4HK137\",\n" +
				"        \"xfyhzh\": \"中国工商银行股份有限公司深圳民治支行 4000103809100524284\"\n" +
				"    }\n" +
				"}";

		String path="E:\\feng\\zsxx\\pdfFileV2.3\\src\\main\\resources\\";
		String fontPath=path+"pdfSign\\C9.ttf";
		String  tmplatePdfPath =path+"pdfTemplate/商品清单模板.pdf";
		StringBuffer sb=new StringBuffer();
//		JSONObject.parseObject(dzfpResult);
		PDFDetailedUtil.create(JSON.parseObject(dzfpResult).getJSONObject("dzfpresult"), tmplatePdfPath,
				fontPath, ()->{
					sb.append("1");
					return path + "outFile/" + 1 + 2+ sb.length() + ".pdf";
				});
		/*
		* java.lang.NullPointerException
  at java.math.BigDecimal.<init>(BigDecimal.java:806)
  at utils.PDFDetailedUtil.getDecima(PDFDetailedUtil.java:61)
  at utils.PDFDetailedUtil.showDynamic(PDFDetailedUtil.java:129)
  at utils.PDFDetailedUtil.addText(PDFDetailedUtil.java:148)
  at utils.PDFDetailedUtil.create

  java.lang.NullPointerException
	at java.math.BigDecimal.<init>(BigDecimal.java:806)
	at utils.PDFDetailedUtil.getDecima(PDFDetailedUtil.java:61)
	at utils.PDFDetailedUtil.showDynamic(PDFDetailedUtil.java:129)
	at utils.PDFDetailedUtil.addText(PDFDetailedUtil.java:148)
	at utils.PDFDetailedUtil.create(PDFDetailedUtil.java:162)
		* */
	}
}

class PDFDetailedData{
	private PdfReader reader;
	private ByteArrayOutputStream bos;
	private PdfStamper stamper; 
	public PDFDetailedData(PdfReader reader,ByteArrayOutputStream bos,PdfStamper stamper){
		this.reader=reader;
		this.bos=bos;
		this.stamper=stamper;
	}
	public PdfReader getReader() {
		return reader;
	}
	public void setReader(PdfReader reader) {
		this.reader = reader;
	}
	public ByteArrayOutputStream getBos() {
		return bos;
	}
	public void setBos(ByteArrayOutputStream bos) {
		this.bos = bos;
	}
	public PdfStamper getStamper() {
		return stamper;
	}
	public void setStamper(PdfStamper stamper) {
		this.stamper = stamper;
	} 
}
