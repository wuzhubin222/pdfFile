package controller;

import Dao.EfpZfMapper;
import entity.ConResult;
import excption.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import schedule.BsnAsync;
import service.ICore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Scope("prototype")
@Controller
public class InvoiceController extends BaseController{

    private static BsnAsync bsnAsync;
    @Autowired
    private ICore iCore;

    @Resource
    private EfpZfMapper efpZfMapper;



    /**
     存储invoice、invoiceqd表	失败回滚
     存储filetree表	失败回滚
     上传文件,必须在存储filetree前	失败回滚
     修改copy表字段 失败回滚
     添加发送邮件队列 失败不回滚
     请求微信服务器模板消息	失败不回滚
     请求微信服务器(判断是否关注)短信	失败不回滚
     */
    @ResponseBody
    @RequestMapping(value = "/invoice/cenKp", method = RequestMethod.POST)
    public ConResult cenKp(
            HttpServletResponse response,
            @RequestParam(value = "fpdm") String fpdm,
            @RequestParam(value = "fphm") String fphm,
            @RequestParam(value = "qqlsh") String qqlsh,//临时表parm2
            @RequestParam(value = "jqbh") String jqbh,//机器编号
            @RequestParam(value = "kprq") String kprq,//注意格式
            @RequestParam(value = "jym") String jym,//校验码
            @RequestParam(value = "bz") String bz,
            @RequestParam(value = "code") String code,//目前未用
            @RequestParam(value = "fpmw",defaultValue = "") String fpmw,//申通的传，饭饭得的不传
            @RequestParam(value = "zjm") String zjm){
        response.setHeader("Access-Control-Allow-Origin", "*");
        this.logFilePath = this.getClass().getResource("/").getPath()+"log/cenKp.txt";
//        String log[] = {fpdm,fphm,qqlsh,jqbh,kprq,jym,bz,code,fpmw,zjm.length()+""};
        String log[] = {fpdm,fphm,qqlsh,jqbh,kprq,jym,bz,code,zjm.length()+""};
//        LogUtils.writeLogFile(logFilePath,log);

        if(!fpdm.matches("^\\d{10,14}$")){
            throw new BusinessException(3046,"发票代码格式错误");
        }

        ConResult conResult = iCore.cenKp(fpdm,fphm,qqlsh,jqbh,kprq,jym,bz,code,zjm,"123");
//        LogUtils.writeLogFile(logFilePath,conResult.getCode()+"",conResult.getMessage());
        return conResult;

    }




    /*生成电子发票(现用)*/
    @ResponseBody
    @RequestMapping(value ="/invoice/createDZp",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
    public ConResult createDZp(
            HttpServletResponse response,
           @RequestParam(value = "dzfpResult") String dzfpResult, @RequestParam(value = "qqlsh") String qqlsh) throws Exception {
            response.setHeader("Access-Control-Allow-Origin", "*");
           this.logFilePath = this.getClass().getResource("/").getPath()+"log/createDZp2.txt";
        // 原始LogUtils.writeLogFile(logFilePath,dzfpResult,qqlsh);
       // LogUtils.writeLogFileName("createDZp.txt",dzfpResult,qqlsh );
        ConResult conResult=null;
        try {
            this.logOutput("========dzfpResult========"+dzfpResult,"=======qqlsh======"+qqlsh);
              conResult = iCore.createDZp2(logFilePath,dzfpResult,qqlsh,iCore);
            this.logOutput("========dzfpResult返回结果========"+conResult.getCode()+"",conResult.getMessage());
           // LogUtils.writeLogFileName("createDZp.txt",conResult.getCode()+"",conResult.getMessage());
            return conResult;
        }catch(BusinessException e){
            conResult=new ConResult();
            conResult.setMessage(e.getMessage());
            conResult.setCode(e.getErrorCode());
            return conResult;
        }catch (Exception e){
            /*return new ConResult();*/
            e.printStackTrace();
           // LogUtils.writeLogFileName("createDZpError.txt",e.getMessage());
            this.LOGGER.error(e.getMessage());
            //LogUtils.writeLogFileName("createDZpError.txt",conResult.getCode()+"",conResult.getMessage());
        }catch (Error e){
            /*return new ConResult();*/
          //  LogUtils.writeLogFileName("createDZpError.txt",e.getMessage());
            this.LOGGER.error(e.getMessage());
        }
      //  LogUtils.writeLogFile(logFilePath,conResult.getCode()+"",conResult.getMessage());
        return new ConResult();
    }



   //红冲发票 （现用）
    @ResponseBody
    @RequestMapping(value = "/invoice/createHzfp",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
    public ConResult creatMinusp(
            HttpServletResponse response,
           @RequestParam(value = "dzfpResult") String dzfpResult,
            @RequestParam(value = "qqlsh") String qqlsh)
            throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
       // String logFilePath =this.logFilePath+ File.separator;
        this.logFilePath = this.getClass().getResource("/").getPath()+"log/createhcfp.txt";

//        LogUtils.writeLogFileName("createhcfp.txt",dzfpResult,qqlsh);
        ConResult conResult = iCore.sendHzfp(logFilePath,dzfpResult,qqlsh);
        //System.out.println("==============="+dzfpResult);
//        LogUtils.writeLogFileName("createhcfp.txt",conResult.getCode()+"",conResult.getMessage());
        return conResult;
    }


    //红冲发票 （现用====手动红冲）
    @ResponseBody
    @RequestMapping(value = "/invoice/createHz",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
    public ConResult createHzs(
            HttpServletResponse response,
            @RequestParam(value = "dzfpResult") String dzfpResult)
            throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        // String logFilePath =this.logFilePath+ File.separator;
        this.logFilePath = this.getClass().getResource("/").getPath()+"log/createhcfpsd.txt";

//        LogUtils.writeLogFileName("createhcfpsd.txt",dzfpResult);
        ConResult conResult = iCore.sendHz(logFilePath,dzfpResult);
        //System.out.println("==============="+dzfpResult);
//        LogUtils.writeLogFileName("createhcfpsd.txt",conResult.getCode()+"",conResult.getMessage());
        return conResult;
    }


    /*纸票（现用）*/
  @ResponseBody
  @RequestMapping(value = "/invoice/creatZp",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
  public ConResult creatzps(
          HttpServletResponse response,
          @RequestParam(value = "qqlsh") String qqlsh,
          @RequestParam(value = "zp") String zp
          )
          throws Exception  {
      response.setHeader("Access-Control-Allow-Origin", "*");
//      LogUtils.writeLogFileName("creatJp.txt",zp,qqlsh);
      ConResult conResult = iCore.createZyfp(qqlsh,zp,iCore);
//      LogUtils.writeLogFileName("creatJp.txt",conResult.getCode()+"",conResult.getMessage());
      return conResult;
  }



    /*@ResponseBody
    @RequestMapping(value = "/invoice/backTest",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
    public HashMap creatzps(
            HttpServletResponse response,
            @RequestParam Map<String,String> map)
        throws Exception  {
        response.setHeader("Access-Control-Allow-Origin", "*");
       *//* map.put("1","ceshi");
        map.put("2","feng");
        map.put("3","cheng");
        map.put("4","shui");
        map.put("5","ce");
        map.put("6","busj");
        map.put("7","sjkdj");*//*
        HashMap conResult = iCore.backTest(map);
        return conResult;
    }*/




//    @ResponseBody
//    @RequestMapping(value = "/invoice/createDZpTest", method = RequestMethod.POST)
//    public ConResult createDZpTest(
//            HttpServletResponse response,
//            @RequestParam(value = "dzfpResult") String dzfpResult){
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        this.logFilePath = this.getClass().getResource("/").getPath()+"log/createDZpTest.txt";
//        try {
//            JSONObject jsonObject = JSON.parseObject(dzfpResult).getJSONObject("dzfpresult");
//            jsonObject.remove("qrm_bmp");
//            LogUtils.writeLogFile(logFilePath,jsonObject.toString());
//        }catch (Exception e){
//            LogUtils.writeLogFile(logFilePath,"dzfpResult不是json格式");
//            e.printStackTrace();
//        }
//        ConResult conResult = iCore.createDZpTest(dzfpResult);
//        LogUtils.writeLogFile(logFilePath,conResult.getCode()+"",conResult.getMessage());
//        return conResult;
//    }


    //tcis平推票
//    @ResponseBody
//    @RequestMapping(value = "/invoice/creatJp",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
//    public ConResult creatJp(
//            HttpServletResponse response,
//            @RequestParam(value = "qqlsh") String qqlsh,
//            @RequestParam(value = "jp") String jp )
//            throws BusinessException {
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        this.logFilePath = this.getClass().getResource("/").getPath()+"log/creatJp.txt";
//        LogUtils.writeLogFile(logFilePath,jp,qqlsh);
//
//        ConResult conResult = iCore.createJp(qqlsh,jp);
//        LogUtils.writeLogFile(logFilePath,conResult.getCode()+"",conResult.getMessage());
//        return conResult;
//    }


    //tcis卷筒票
//    @ResponseBody
//    @RequestMapping(value = "/invoice/creatZp",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
//    public ConResult creatZp(
//            HttpServletResponse response,
//            @RequestParam(value = "qqlsh") String qqlsh,
//            @RequestParam(value = "zp") String zp )
//            throws BusinessException {
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        this.logFilePath = this.getClass().getResource("/").getPath()+"log/creatZp.txt";
//        LogUtils.writeLogFile(logFilePath,zp,qqlsh);
//
//        ConResult conResult = iCore.createZp(qqlsh,zp);
//        LogUtils.writeLogFile(logFilePath,conResult.getCode()+"",conResult.getMessage());
//        return conResult;
//    }


    @ResponseBody
    @RequestMapping(value = "/invoice/financeSeal",produces = "application/json;charset=utf-8", method =
            RequestMethod.POST)
    public ConResult financeSeal(
            HttpServletResponse response,
            @RequestPart MultipartFile fileResPdf,
            @RequestParam(value = "etpTaxId") String etpTaxId) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        this.logFilePath = this.getClass().getResource("/").getPath()+"log/financeSeal.txt";
        this.logOutput(etpTaxId);
        ConResult conResult = null;
        try {
            conResult = iCore.financeSeal(null,fileResPdf,etpTaxId);
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("etpTaxId",etpTaxId);
            map.put("Message",e.getMessage());
            this.logOutput(map);
            throw e;
        }
        return conResult;
    }

    @ResponseBody
    @RequestMapping(value = "/invoice/applyFinanceSeal",produces = "application/json;charset=utf-8", method =
            RequestMethod.POST)
    public ConResult applyFinanceSeal(
            HttpServletResponse response,
            @RequestParam(value = "parm2") String parm2
            ) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");
        this.logOutput(parm2);
        ConResult conResult = null;
        try {
            conResult = iCore.applyFinanceSeal(null,parm2);
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("parm2",parm2);
            map.put("Message",e.getMessage());
            this.logOutput(map);
            throw e;
        }
        return conResult;
    }
    /**
     * @Author: 帅帅
     * @Date: 2019/6/26
     * @Description: financeSealExamine
     * @Param response
     * @Param ids id数组
     * @Param parm3 0是重新申请，2是驳回
     * @Return: entity.ConResult
     */
    @ResponseBody
    @RequestMapping(value = "/invoice/financeSealExamine",produces = "application/json;charset=utf-8", method =
            RequestMethod.POST)
    public ConResult financeSealExamine(
            HttpServletResponse response,
            @RequestParam(value = "ids") String [] ids,
            @RequestParam(value = "parm3") String parm3
    ) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");
        this.logOutput(ids.toString(),parm3);
        ConResult conResult = null;
        try {
            conResult = iCore.financeSealExamine(ids,parm3);
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("ids",ids);
            map.put("Message",e.getMessage());
            this.logOutput(map);
            throw e;
        }
        return conResult;
    }

}
