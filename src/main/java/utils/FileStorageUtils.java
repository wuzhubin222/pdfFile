package utils;

import constant.Constant;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Date;

public class FileStorageUtils {

    /**
     * 新nfs下载
     *
     * @param localFilePath
     * @param remoteFileName
     */
    @Deprecated
    public static void downFile(String localFilePath, String remoteFileName) throws IOException {
        String remoteFileNamePath = remoteFileName;
        if (!remoteFileName.startsWith(Constant.FTP_ROOT_DIR)) {
            remoteFileNamePath = Constant.FTP_ROOT_DIR + remoteFileName;
        }
        System.out.println("downFile----------------start---------");
        System.out.println("remoteFileNamePath:" + remoteFileNamePath);
        System.out.println("localFilePath:" + localFilePath);
        FileUtils.copyFile(new File(remoteFileNamePath), new File(localFilePath));
        System.out.println("downFile----------------end-------------");

    }

    /**
     * nfs上传
     *
     * @param remoteFilePath
     * @param remoteFileName
     * @param inputStream
     */
    @Deprecated
    public static void uploadFile(String remoteFilePath, String remoteFileName, InputStream inputStream) throws IOException {
        if (!remoteFilePath.startsWith(Constant.FTP_ROOT_DIR)) {
            remoteFilePath = Constant.FTP_ROOT_DIR + remoteFilePath;
        }
        if (!remoteFilePath.endsWith("/")) {
            remoteFilePath = remoteFilePath + "/";
        }
        File newfile = new File(remoteFilePath);
        if (!newfile.exists()) {
            newfile.mkdir();
        }
        String destFile = remoteFilePath + remoteFileName;
        System.out.println("uploadFile----------------start-------------");
        System.out.println("remoteFilePath:" + remoteFilePath);
        System.out.println("destFile:" + destFile);
        FileUtils.copyInputStreamToFile(inputStream, new File(destFile));
        System.out.println("uploadFile----------------end-------------");
    }

    public static String genUploadPath(Date sqRq , Date date,String nsrsbh,String cityCode) {
        String pathSplitor = "/";
        String folderName = "";
        String basePath = genCommonPath(sqRq,date,nsrsbh,cityCode);
        File dir = new File(basePath);
        int length  = dir.listFiles().length;
        if (length==0){
            folderName = "0";
        }else{
            File maxNumberDir = new File(basePath+"/"+length);
            int fileCount = maxNumberDir.listFiles().length;
            if (fileCount>=Constant.CA_MAX_NUMBER){
                folderName = (length++)+"";
            }
        }
        String ret  = basePath+pathSplitor+folderName+pathSplitor;
        System.out.println("genUploadPath ******************** retPath:"+ret);
        return ret;
    }

    public static String getHttpUrl(String path,String fileName) {
        String retPath = path.replace(Constant.FTP_ROOT_DIR,Constant.PDFFILEURL)+fileName;
        System.out.println("getHttpUrl ******************** retPath:"+retPath);
        return retPath;
    }

    private static String genCommonPath(Date sqRq, Date date,String nsrsbh,String cityCode){
        String pathSplitor = "/";
        String sq_rq = DateUtils.getFormatString("yyyy-MM-dd",sqRq);
        String regYearAndMonth = sq_rq.split("-")[0] + sq_rq.split("-")[1];
        String regDay = sq_rq.split("-")[2];
        String kp_rq = DateUtils.getFormatString("yyyy-MM-dd", date);
        String kpYearAndMonth = kp_rq.split("-")[0] + kp_rq.split("-")[1];
        String kpDay = kp_rq.split("-")[2];
        StringBuffer pb = new StringBuffer(Constant.FTP_ROOT_DIR);
        pb.append(pathSplitor);
        pb.append(cityCode);
        pb.append(pathSplitor);
        pb.append(regYearAndMonth);
        pb.append(pathSplitor);
        pb.append(regDay);
        pb.append(pathSplitor);
        pb.append(cityCode);
        pb.append(pathSplitor);
        pb.append(nsrsbh);
        pb.append(pathSplitor);
        pb.append(kpYearAndMonth);
        pb.append(pathSplitor);
        pb.append(kpDay);
        pb.append(pathSplitor);
        return pb.toString();
    }
}
