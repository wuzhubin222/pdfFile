package utils;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 计算字体长度工具类
 *
 */
public class FontPixel {

	private  String newValue;
	private String value;
	private int fontSize;
	private Integer lineHeight;
	private Integer width;
	private Integer height;
	private java.awt.FontMetrics fm =null;
	 
	public FontPixel(String value,int fontSize){
		this(value,fontSize,null); 
	}
	public FontPixel(String value,int fontSize,Integer lineHeight){
		this(value,fontSize,null,null,lineHeight);
	}
	public FontPixel(String value,int fontSize,Integer width,Integer height,Integer lineHeight){
		this.newValue=value;
		this.value=value.replace(" ","1");
		this.fontSize=fontSize;
		this.width=width;
		this.height=height;
		this.lineHeight=lineHeight;
		cal();
	}
	private Integer getWidth(String value){
		return  fm.stringWidth(value);
		
	}
	public void cal(){
	    fm = sun.font.FontDesignMetrics.getMetrics(new java.awt.Font("宋体", java.awt.Font.PLAIN, fontSize));
        int h = fm.getHeight();
		if(lineHeight==null){
			lineHeight=h;
		}
	}
	
	public List<PointValue> getValue(){
		List<PointValue> result=new ArrayList<PointValue>();
		if(width==null&&height==null){
			result.add(new PointValue(0,0,value));
			return result;
		}
		StringBuffer strSplit=new StringBuffer();
		StringBuffer newStrSplit=new StringBuffer();

		char []chars=value.toCharArray();
		char[]newChars=newValue.toCharArray();

		int index=0;
		for(int i=0;i<chars.length;i++){
			String item=strSplit.toString();
			String newItem=newStrSplit.toString();
			newStrSplit.append(newChars[i]);
			strSplit.append(chars[i]); 
			if(getWidth(strSplit.toString())>=width&&i>0){
				//System.out.println("宽度："+getWidth(newItem)+"--Str："+strSplit+"--："+fontSize);
			//	System.out.println(strSplit);
				result.add(new PointValue(0,index*lineHeight,newItem));
				index++;
				i--; 
				strSplit=new StringBuffer();
				newStrSplit=new StringBuffer();
				if(height!=null&&index*lineHeight>height){
					return result;
				} 
			} 
		}
		if(strSplit.length()>0){
			result.add(new PointValue(0,index*lineHeight,newStrSplit.toString()));
		}
		return result;
	}
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	public Integer getLineHeight() {
		return lineHeight;
	}
	public void setLineHeight(Integer lineHeight) {
		this.lineHeight = lineHeight;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public void setValue(String value) {
		this.value = value;
	}

	class PointValue{
		private int x;
		private int y;
		private String value;
		public PointValue(int x,int y,String value){
			this.x=x;
			this.y=y;
			this.value=value;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}

	}


}
