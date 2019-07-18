package utils;

import com.itextpdf.text.pdf.PdfReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestImage {



    public static void main(String[] args) {
        pdf2Image("C:\\Users\\CSF\\Desktop\\pdf\\签章后.pdf", "E:\\pdf", 110);
    }


    /***
     * PDF文件转PNG图片，全部页数
     *
     * @param PdfFilePath pdf完整路径
     * -- @param imgFilePath 图片存放的文件夹
     * @param dpi dpi越大转换后越清晰，相对转换速度越慢
     * @return
     */
    public static String pdf2Image(String PdfFilePath, String dstImgFolder, int dpi) {
        File file = new File(PdfFilePath);
        String imagePDFName=null;
        PDDocument pdDocument;
        try {
            String imgPDFPath = file.getParent();
            int dot = file.getName().lastIndexOf('.');
             imagePDFName = file.getName().substring(0, dot); // 获取图片文件名
            String imgFolderPath = dstImgFolder;
//            if (dstImgFolder.equals("")) {
//                imgFolderPath = imgPDFPath + File.separator + imagePDFName;// 获取图片存放的文件夹路径
//            } else {
//                imgFolderPath = dstImgFolder + File.separator + imagePDFName;
//            }
//
//            if (createDirectory(dstImgFolder)) {

                pdDocument = PDDocument.load(file);
                PDFRenderer renderer = new PDFRenderer(pdDocument);
                /* dpi越大转换后越清晰，相对转换速度越慢 */
                PdfReader reader = new PdfReader(PdfFilePath);
                int pages = reader.getNumberOfPages();
                StringBuffer imgFilePath = null;
                List<BufferedImage> list=new ArrayList<BufferedImage>();
                int height=0;
                int width=0;

                for (int i = 0; i < pages; i++) {
                    BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                    width=image.getWidth();
                    list.add(image);
                    height+=image.getHeight();
                }

                BufferedImage image=new BufferedImage(width,height,1);
                Graphics2D g= image.createGraphics();
                int y=0;
                for(int i=0;i<list.size();i++){
                    g.drawImage(list.get(i),0,y,null);
                    y=y+list.get(i).getHeight();
                }
                ImageIO.write(image, "jpg",new File(dstImgFolder+"//"+imagePDFName+".jpg"));

                g.dispose();
                return dstImgFolder+"//"+imagePDFName+".jpg";
          //  }
//            else {
//                throw new BusinessException(3046, "生成jpg图片文件失败");
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean createDirectory(String folder) {
        File dir = new File(folder);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }




}
