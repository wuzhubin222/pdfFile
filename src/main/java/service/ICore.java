package service;

import entity.ConResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

public interface ICore {

    public String toPDF(String json) throws Exception;


    //    tcis推送电子发票
    ConResult cenKp(String fpdm,String fphm,String qqlsh,String jqbm,String kprq,String jym,
               String bz,String code,String zjm,String fpmw);

    //tcis电子票并生成pdf文件
//    ConResult createDZp(String fpdm,String fphm,String qqlsh,String jqbm,String kprq,String jym,
//    String bz,String code,String zjm,String fpmw);

    //灰色修改后
    /*ConResult createDZp(String dzfpResult,String qqlsh);*/

    //红冲pdf发票
    ConResult sendHzfp(String logPath,String dzfpResult,String qqlsh) throws Exception;

    ConResult sendHz(String logPath,String dzfpResult) throws Exception;

    //添加pdf文件合并
    ConResult createDZp2(String logPath,String dzfpResult,String qqlsh,ICore core) throws Exception;



    HashMap<String, String> transactionalCreateDZp2(String logPath, String dzfpResult, String qqlsh) throws Exception;


    ConResult createZyfp(String qqlsh, String zp,ICore iCore)throws Exception;

    HashMap<String, String> transactionalCreateZyfp(String qqlsh, String zp);

    
    //tcis推送平推票
//    ConResult createZp(String qqlsh,String Json);

    //tcis推送卷票
//    ConResult createJp(String qqlsh,String jp);

    HashMap sendTest(String qqlsh);

    ConResult financeSeal(String logFilePath, MultipartFile fileResPdf, String etpTaxId) throws Exception;


    /*手动生成pdf*/
    ConResult Createpdf1(String logPath,String dzfpResult,String qqlsh,ICore core) throws Exception;
    HashMap<String, String> Createpdf(String logPath, String dzfpResult, String qqlsh) throws Exception;


    ConResult applyFinanceSeal(String logFilePath, String parm2) throws Exception;

    ConResult financeSealExamine(String [] id, String parm3);
}
