package utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 帅帅
 * @date 2018/5/18 14:11
 */
public class Test {


    static void test1() throws IOException, DocumentException {
        PdfReader reader = new PdfReader("D:\\1\\91610132710152658R.pdf");//指定将和 图片拼接的 PDF
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("D:\\1\\91610132710152658R999.pdf"));
        //生成的PDF 路径
        PdfContentByte overContent = stamper.getOverContent(1);
//添加图片
        Image image = Image.getInstance("D:\\1\\91610132710152658R.png");//图片名称
        image.scaleAbsolute(80, 40);//宽 高   腐蚀类型
        image.setAbsolutePosition(30,30);//左边距、底边距
        overContent.addImage(image);
        overContent.stroke();
        stamper.close();
        reader.close();
    }






    /*
    *  StringBuffer sb=new StringBuffer();
        List<String> array=new ArrayList<String>();
        String key="id";
        String data="czdate";
        String tabelName="cd_efp_invoice_qd";
        array.add("fpdm");
        array.add("fphm");
        array.add("wp_dw");
        sb.append("select * from "+tabelName+"\n");
        sb.append("where "+key+" not in (\n");
        sb.append("\tselect "+key+" from "+tabelName+" as temp\n");
        sb.append("\tinner join (\n");
        sb.append("\t\tselect \n\t\t\t");
        for(int i=0;i<array.size();i++){
            sb.append(array.get(i)+",");
        }
        sb.append("min("+data+") as "+data+"\n");
        sb.append("\t\tfrom "+tabelName+"\n");
        sb.append("\t\tgroup by ");
        for(int i=0;i<array.size();i++){
            sb.append(array.get(i));
            if(i<array.size()-1){
                sb.append(",");
            }
        }
        sb.append("\n\t) as c on ");
        for(int i=0;i<array.size();i++){
            sb.append("c."+array.get(i)+"=temp."+array.get(i));
            sb.append(" and ");
        }
        sb.append("c."+data+"=temp."+data);
        sb.append(")");

        System.out.println(sb.toString());
    * */




    /*public static void main(String[] args) throws Exception {

        test1();


//        // 模板文件路径
//        String templatePath = "D:\\1\\91610132710152658R.pdf";
//        // 生成的文件路径
//        String targetPath = "D:\\1\\91610132710152658RNew3.pdf";
//        // 书签名
//        String fieldName = "field";
//        // 图片路径
//        String imagePath = "D:\\1\\91610132710152658R.png";
//
//        // 读取模板文件
//        InputStream input = new FileInputStream(new File(templatePath));
//        PdfReader reader = new PdfReader(input);
//        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(targetPath));
//        // 提取pdf中的表单
//        AcroFields form = stamper.getAcroFields();
//        form.addSubstitutionFont(BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
//
//
//
//        // 通过域名获取所在页和坐标，左下角为起点
//      int pageNo = form.getFieldPositions(fieldName).get(0).page;
//     Rectangle signRect = form.getFieldPositions(fieldName).get(0).position;
//    float x = signRect.getLeft();
//     float y = signRect.getBottom();




//        // 读图片
//        Image image = Image.getInstance(imagePath);
//        // 获取操作的页面
//       PdfContentByte under = stamper.getOverContent(pageNo);
//        // 根据域的大小缩放图片
//       image.scaleToFit(signRect.getWidth(), signRect.getHeight());
//        // 添加图片
//       image.setAbsolutePosition(x, y);
//        under.addImage(image);
//
//        stamper.close();
//        reader.close();
    }*/
}
