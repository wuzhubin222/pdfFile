package constant;

public class Constant {

    /**
     * 测试环境
     */
   /* public static final String FTP_SERVER_IP = "invoicefileservice.bjzsxx.com";
    public static final int FTP_SERVER_PORT = 21;
    public static final String FTP_SERVER_USER = "block";
    public static final String FTP_SERVER_PASS = "block";
    public static final int PDF_MAX_NUMBER = 500;
    public static final int CA_MAX_NUMBER = 500;
    public static final String PDFFILEURL = "invoicefileservice.bjzsxx.com";
    public static final String XMURL = "http://wxgz1.bjzsxx.com/wxDevTest/";*/


    /**
     * 生产环境
     */
 /*   public static final String FTP_SERVER_IP = "invoicefileservice.bjzsxx.com";
    public static final int FTP_SERVER_PORT = 21;
    public static final String FTP_SERVER_USER = "block";
    public static final String FTP_SERVER_PASS = "block";
    public static final int PDF_MAX_NUMBER = 500;
    public static final int CA_MAX_NUMBER = 500;
    public static final String PDFFILEURL = "invoicefileservice.bjzsxx.com";
    public static final String XMURL = "http://wxgz1.bjzsxx.com/wxDev/";*/

    /**
     * 生产环境invoicefileservice.bjzsxx.com
     * 存储地址 114.67.47.39=> pdf  /ftpdir/block/440300
     *
     */
//     public static final String FTP_SERVER_IP = "114.67.47.39";
   public static final String FTP_SERVER_IP = "192.168.1.3";
   public static final int FTP_SERVER_PORT = 21;
   public static final String FTP_SERVER_USER = "block";
   public static final String FTP_SERVER_PASS = "block";
   public static final int PDF_MAX_NUMBER = 500;
   public static final int CA_MAX_NUMBER = 500;
   public static final String PDFFILEURL = "http://invoicefileservice.bjzsxx.com/";
    //public static final String XMURL = "http://192.168.0.112:8081/wxDevTest/";
   public static final String XMURL ="http://wxgz1.bjzsxx.com/wxDev/";

    /*"/usr/local/pdfFileLog";*/
   public static final String LOG_ROOT_PATH ="/usr/local/pdfFileLog";

    public static final String XMURL1 ="http://114.67.47.58/message/backFp";

   public  static final String  FTP_ROOT_DIR = "/ftpdir/block/";
//   测试
//    public static final String RESULT ="https://openapi-test.colourlife.com/v1/receipt/receipt/result";
//
//   public static final String APPID = "ICEDZ00-7XFX-JX44-YFPS-7JTN5JNVK64G";
//    public static final String TOKEN = "eoOkvAjfZpaSh2tRmaEt";

// 预发布，正式
//        public static final String RESULT ="http://134.175.221.99:5010/v1/receipt/result";
        public static final String RESULT ="https://openapi.colourlife.com/v1/receipt/receipt/result";
    public static final String APPID = "ICEDZSJZS-4QFE-O6YR-BG2N-34UZT8NUOZGD";
    public static final String TOKEN = "u1HLGDGlk1lSKIav4dhW";



       public static final  String CERTPDF = "cert_pdf";
    public static final String CAIMGPATH = "ca/1/1";
    public static final String CERT_UPLOAD = "cert_upload";
}
