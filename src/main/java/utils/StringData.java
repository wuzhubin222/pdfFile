package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StringData {

    private  int fontSize;
    private  List<String> strList=new ArrayList<String>();


                                                //字符串  最大字体  最小字体  宽度  最大行数
    public static StringData  stringlist(String str, int max, int min, int width, int maxLine){
        StringData sd=new StringData();
        int fontSize=max;
        List<FontPixel.PointValue>  strList=null;
        for(int line=1;line<=maxLine;line++){
            for(int i=max;i>=min;i--){
                FontPixel font   =new FontPixel(str,i,width,null,null);
                 strList=font.getValue();
                if(line==strList.size()) {
                    fontSize=i;
                    line=maxLine+1;
                    break;
                }

            }
        }
        sd.setFontSize(fontSize);
        if(strList!=null){
            for(FontPixel.PointValue pointValue:strList){
               // System.out.println(pointValue.getValue());
                    sd.getStrList().add(pointValue.getValue());
            }
        }else{
            sd.getStrList().add(str);
        }
        return sd;
    }



    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public List<String> getStrList() {
        return strList;
    }

    public void setStrList(List<String> strList) {
        this.strList = strList;
    }

}
