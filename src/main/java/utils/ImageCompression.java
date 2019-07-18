package utils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageCompression {

    public static void main(String[] args) {
        reduceLessSize("C:\\Users\\zccx\\Desktop\\91320594MA1N8JDP2Q\\微信图片_20180105162625.jpg",
                "C:\\Users\\zccx\\Desktop\\91320594MA1N8JDP2Q\\申请协议.jpg",300);
    }


    /**
     * 按比例压缩至小于并接近于指定大小
     * @param imgsrc 源图片地址
     * @param imgdist 目标图片地址
     * @param kb 文件大小(单位kb)
     */
    public static void reduceLessSize(String imgsrc,String imgdist,long kb){
        File srcfile = new File(imgsrc);
        long length = srcfile.length();
        float rate = 0.5f;
        if (length > 5000000) {
            rate = 0.1f;
        } else if (length > 4000000) {
            rate = 0.3f;
        } else if (length > 3000000) {
            rate = 0.4f;
        } else if (length > 2000000) {
            rate = 0.5f;
        } else if (length > 1000000) {
            rate = 0.6f;
        }
        reduceImg(imgsrc, imgdist, 0, 0, 0.9f);
        File distfile = new File(imgdist);
        while (distfile.length() > kb * 1000) {
            rate -= 0.1;
            reduceImg(imgsrc, imgdist, 0, 0, rate);
        }
    }


    /**
     * 采用指定宽度、高度或压缩比例 的方式对图片进行压缩
     *
     * @param imgsrc     源图片地址
     * @param imgdist    目标图片地址
     * @param widthdist  压缩后图片宽度（当rate==null时，必传）
     * @param heightdist 压缩后图片高度（当rate==null时，必传）
     * @param rate       压缩比例
     */
    static void reduceImg(String imgsrc, String imgdist, int widthdist,
                                 int heightdist, Float rate) {
        try {
            File srcfile = new File(imgsrc);
            if (!srcfile.exists()) {
                return;
            }
            // 如果rate不为空说明是按比例压缩
            if (rate != null && rate > 0) {
                // 获取文件高度和宽度
                int[] results = getImgWidth(srcfile);
                if (results == null || results[0] == 0 || results[1] == 0) {
                    return;
                } else {
                    widthdist = (int) (results[0] * rate);
                    heightdist = (int) (results[1] * rate);
                }
            }

            // 开始读取文件并进行压缩
            Image src = javax.imageio.ImageIO.read(srcfile);
            BufferedImage tag = new BufferedImage((int) widthdist,
                    (int) heightdist, BufferedImage.TYPE_INT_RGB);

            tag.getGraphics().drawImage(
                    src.getScaledInstance(widthdist, heightdist,
                            Image.SCALE_SMOOTH), 0, 0, null);

            FileOutputStream out = new FileOutputStream(imgdist);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
            out.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 获取图片宽度
     * @param file 图片文件
     * @return 宽度
     */
    static int[] getImgWidth(File file) {
        InputStream is = null;
        BufferedImage src = null;
        int result[] = {0, 0};
        try {
            is = new FileInputStream(file);
            src = javax.imageio.ImageIO.read(is);
            result[0] = src.getWidth(null); // 得到源图宽
            result[1] = src.getHeight(null); // 得到源图高
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
