package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import entity.ConResult;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.EfpInvoice;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class localEmailUtils {
    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(new ConResult()));
    }

    public static boolean sendEmailTest(String accetpAccount) {
        HtmlEmail email = new HtmlEmail();
        email.setSSLOnConnect(true);
        email.setSslSmtpPort("465");
        email.setHostName("smtp.exmail.qq.com");

        EmailAttachment attachment = new EmailAttachment();
        attachment.setDisposition("attachment");
        attachment.setDescription("发票附件");
        attachment.setName("123456789.pdf");
        email.setAuthentication("dzfp@jartekin.com", "Jtcxweb66");
        try {
            URL urlPdf = new URL("http://60.205.137.20/440300/20174/24/91440300662670636Q/20174/4/1/04403160011134927682.pdf");
            attachment.setURL(urlPdf);

            email.attach(attachment);
            email.addTo(accetpAccount);
            email.setFrom("dzfp@jartekin.com", "票慧");
            email.setSubject("【电子发票】您收到一张新的电子发票");
            File file = new File("E:\\tcisWeChat\\trunk\\wxDevlop\\src\\main\\resources\\票慧二维码.jpg");
            String cid = email.embed(file);
            email.setCharset("UTF-8");

            email.setHtmlMsg("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><title>返回邮件</title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/></head><body style=\"margin: 0; padding: 0;\"><div id=\"content\">    <div id=\"main\">        <table align=\"center\" background=\"http://mimg.127.net/xm/mail_res/common/bg.png\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family:verdana;font-size:14px;line-height:180%\" width=\"606\">            <tbody>            <tr>                <td background=\"http://mimg.127.net/xm/mail_res/common/top.png\" colspan=\"3\" height=\"66\">                </td>            </tr>            <tr>                <td width=\"34\">&nbsp;</td>                <td width=\"538\">                    <p style=\"margin:0;padding:25px 0 15px\">                        <strong>尊敬的客户您好</strong>：                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        您于2016-10-10日购物并选择开具电子发票，我们将电子发票发送给您，以便作为您的<strong>维权保修凭证</strong>、<strong>报销凭证</strong>。                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        发票信息如下：                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        开票日期：2016-10-10日                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        发票代码：033001600111                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        发票号码：17189201                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        销方名称：金华市瓜田文化传播有限公司                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        购方名称：深圳投石信息科技有限公司                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        价税合计：￥1950.00                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        <strong>附件是电子发票PDF文件</strong>，供下载使用。                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        <img height=\"140\" src=\"cid:" + cid + "\"  width=\"140\"  />                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        关注票慧微信公众号 微信收取电子发票                    </p>                    <div style=\"text-indent:2em;text-align:right;margin-right:30px;padding:0 0 0 0\">                        <b style=\"margin-right:35px;\">中税信息</b>                    </div>                    <p style=\"text-indent:2em;text-align:right;margin:0;padding:0 0 20px 0\">                        http://www.bjzsxx.com/                    </p>                    <div style=\"border-top:1px dotted #ccc;height:1px;margin-top:10px\"></div>                </td>                <td width=\"34\">&nbsp;</td>            </tr>            <tr>                <td colspan=\"3\" height=\"20\">&nbsp;</td>            </tr>            <tr>                <td background=\"http://mimg.127.net/xm/mail_res/common/bottom.jpg\" colspan=\"3\" height=\"20\" style=\"font-size:0;line-height:0\">&nbsp;</td>            </tr>            </tbody>        </table>    </div></div></body></html>");


            email.send();
        } catch (EmailException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean sendEmail(String emailAccount, EfpInvoice invoice, String pdfUrl) {
        HtmlEmail email = new HtmlEmail();
        email.setSSLOnConnect(true);
        email.setSslSmtpPort("465");
        email.setTLS(false);

        EmailAttachment attachment = new EmailAttachment();
        attachment.setDisposition("attachment");
        attachment.setDescription("发票附件");
        attachment.setName(invoice.getFpdm() + invoice.getFphm() + ".pdf");


        email.setHostName("smtp.exmail.qq.com");
        email.setAuthentication("dzfp@bjzsxx.com", "eJHvrHGz4q6XbhUj");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = invoice.getCreateTime();
        try {
            URL urlPdf = new URL(pdfUrl);
            attachment.setURL(urlPdf);

            email.attach(attachment);
            email.addTo(emailAccount);
            email.setFrom("dzfp@bjzsxx.com", "票慧");
            email.setSubject("【电子发票】您收到一张新的电子发票");
            String picPath = EmailUtils.class.getResource("/").getPath() + "票慧二维码.jpg";
            File file = new File(picPath);
            String cid = email.embed(file);
            email.setCharset("UTF-8");

            String msg = composeHtmlMsg(formatter.format(date), invoice.getFpdm(), invoice.getFphm(), invoice
                    .getXfmc(), invoice.getGfmc(), invoice.getHjje().add(invoice.getHjse()) + "", cid);
            email.setHtmlMsg(msg);
            email.send();
        } catch (EmailException e) {
            return false;
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    public static void sendEmail(String emailAccount, EfpInvoice invoice, JSONArray pdfUrls)
            throws EmailException, MalformedURLException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = invoice.getCreateTime();

        HtmlEmail email = new HtmlEmail();
        email.setSSLOnConnect(true);
        email.setSslSmtpPort("465");
        email.setTLS(false);
        for (int i = 0; i < pdfUrls.size(); i++) {
            String url = pdfUrls.getString(i);
            EmailAttachment attachment = new EmailAttachment();
            attachment.setDisposition("attachment");
            attachment.setDescription("发票附件");
            String[] names = url.split("/");
            attachment.setName(names[(names.length - 1)]);


            URL urlPdf = new URL(url);
            attachment.setURL(urlPdf);
            email.attach(attachment);
        }
        email.setHostName("smtp.exmail.qq.com");
        email.setAuthentication("dzfp@bjzsxx.com", "eJHvrHGz4q6XbhUj");

        email.addTo(emailAccount);
        email.setFrom("dzfp@bjzsxx.com", "票慧");
        email.setSubject("【电子发票】您收到一张新的电子发票");
        String picPath = EmailUtils.class.getResource("/").getPath() + "票慧二维码.jpg";
        File file = new File(picPath);
        String cid = email.embed(file);
        email.setCharset("UTF-8");

        String msg = composeHtmlMsg(formatter.format(date), invoice.getFpdm(), invoice.getFphm(), invoice
                .getXfmc(), invoice.getGfmc(), invoice.getHjje().add(invoice.getHjse()) + "", cid);
        email.setHtmlMsg(msg);
        email.send();
    }

    public static void sendEmail(String pdfUrl, String kprq, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String emailAccount)
            throws EmailException, MalformedURLException {
        HtmlEmail email = new HtmlEmail();
        email.setSSLOnConnect(true);
        email.setSslSmtpPort("465");
        email.setTLS(false);

        EmailAttachment attachment = new EmailAttachment();
        attachment.setDisposition("attachment");
        attachment.setDescription("发票附件");
        attachment.setName(fpdm + fphm + ".pdf");


        email.setHostName("smtp.exmail.qq.com");
        email.setAuthentication("dzfp@bjzsxx.com", "eJHvrHGz4q6XbhUj");


        URL urlPdf = new URL(pdfUrl);
        attachment.setURL(urlPdf);
        email.attach(attachment);
        email.addTo(emailAccount);
        email.setFrom("dzfp@bjzsxx.com", "票慧");
        email.setSubject("【电子发票】您收到一张新的电子发票");
        String picPath = EmailUtils.class.getResource("/").getPath() + "票慧二维码.jpg";
        File file = new File(picPath);
        String cid = email.embed(file);
        email.setCharset("UTF-8");

        String msg = composeHtmlMsg(kprq, fpdm, fphm, xfmc, gfmc, jshj, cid);
        email.setHtmlMsg(msg);
        email.send();
    }

    public static void sendEmail(JSONArray pdfUrls, String kprq, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String emailAccount)
            throws EmailException, MalformedURLException {
        HtmlEmail email = new HtmlEmail();
        email.setSSLOnConnect(true);
        email.setSslSmtpPort("465");
        email.setTLS(false);
        for (int i = 0; i < pdfUrls.size(); i++) {
            String url = pdfUrls.getString(i);
            EmailAttachment attachment = new EmailAttachment();
            attachment.setDisposition("attachment");
            attachment.setDescription("发票附件");
            String[] names = url.split("/");
            attachment.setName(names[(names.length - 1)]);


            URL urlPdf = new URL(url);
            attachment.setURL(urlPdf);
            email.attach(attachment);
        }
        email.setHostName("smtp.exmail.qq.com");
        email.setAuthentication("dzfp@bjzsxx.com", "eJHvrHGz4q6XbhUj");

        email.addTo(emailAccount);
        email.setFrom("dzfp@bjzsxx.com", "票慧");
        email.setSubject("【电子发票】您收到一张新的电子发票");
        String picPath = EmailUtils.class.getResource("/").getPath() + "票慧二维码.jpg";
        File file = new File(picPath);
        String cid = email.embed(file);
        email.setCharset("UTF-8");

        String msg = composeHtmlMsg(kprq, fpdm, fphm, xfmc, gfmc, jshj, cid);
        email.setHtmlMsg(msg);
        email.send();
    }

    private static String composeHtmlMsg(String kprq, String fpdm, String fphm, String xfmc, String gfmc, String jshj, String cid) {
        String msg = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><title>返回邮件</title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/></head><body style=\"margin: 0; padding: 0;\"><div id=\"content\">    <div id=\"main\">        <table align=\"center\" background=\"http://mimg.127.net/xm/mail_res/common/bg.png\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family:verdana;font-size:14px;line-height:180%\" width=\"606\">            <tbody>            <tr>                <td background=\"http://mimg.127.net/xm/mail_res/common/top.png\" colspan=\"3\" height=\"66\">                </td>            </tr>            <tr>                <td width=\"34\">&nbsp;</td>                <td width=\"538\">                    <p style=\"margin:0;padding:25px 0 15px\">                        <strong>尊敬的客户您好</strong>：                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        您于" + kprq + "购物并选择开具电子发票，我们将电子发票发送给您，以便作为您的<strong>维权保修凭证</strong>、<strong>报销凭证</strong>。                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        发票信息如下：                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        开票日期：" + kprq + "                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        发票代码：" + fpdm + "                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        发票号码：" + fphm + "                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        销方名称：" + xfmc + "                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 0 0\">                        购方名称：" + gfmc + "                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        价税合计：￥" + jshj + "元                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        <strong>附件是电子发票PDF文件</strong>，供下载使用。                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        <img height=\"140\" src=\"cid:" + cid + "\"  width=\"140\"  />                    </p>                    <p style=\"text-indent:2em;margin:0;padding:0 0 20px 0\">                        关注票慧微信公众号 微信收取电子发票                    </p>                    <div style=\"border-top:1px dotted #ccc;height:1px;margin-top:10px\"></div>                </td>                <td width=\"34\">&nbsp;</td>            </tr>            <tr>                <td colspan=\"3\" height=\"20\">&nbsp;</td>            </tr>            <tr>                <td background=\"http://mimg.127.net/xm/mail_res/common/bottom.jpg\" colspan=\"3\" height=\"20\" style=\"font-size:0;line-height:0\">&nbsp;</td>            </tr>            </tbody>        </table>    </div></div></body></html>";
        return msg;
    }

    public static boolean sendTextEmail(String emailAccount, String text) {
        HtmlEmail email = new HtmlEmail();
        email.setSSLOnConnect(true);
        email.setSslSmtpPort("465");


        email.setHostName("smtp.exmail.qq.com");
        email.setAuthentication("dzfp@bjzsxx.com", "eJHvrHGz4q6XbhUj");
        try {
            email.addTo(emailAccount);
            email.setFrom("dzfp@bjzsxx.com", "票慧");
            email.setSubject("通知");
            email.setCharset("UTF-8");
            email.setTextMsg(text);
            String s = email.send();
            System.out.println("=");
        } catch (EmailException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
