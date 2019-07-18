package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

public class QrCodeUtils {

    public static void proQrCodeImg(String content,int width,int height,String outPath) throws WriterException, IOException {

        Hashtable<EncodeHintType,Object> hints= new Hashtable<EncodeHintType,Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height,hints);

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outPath);
            MatrixToImageWriter.writeToStream(bitMatrix,"png",outputStream);
            outputStream.flush();
            outputStream.close();
        }finally {
            if(outputStream != null){
                outputStream.close();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String text = "01,10,044031700111,07381346,154.37,20170828,53228432521674497387,B835,";
        try {
            proQrCodeImg(text,150,150,"D:\\pdfFileV2.0\\src\\main\\resources\\pdfSign\\测试生成的二维码.png");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
