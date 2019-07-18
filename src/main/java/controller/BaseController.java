package controller;

import entity.ConResult;
import excption.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.BaseAdviceUtils;

public abstract class BaseController extends BaseAdviceUtils {

    String logFilePath = this.getClass().getResource("/").getPath();

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ConResult handleBusinessException(BusinessException businessException) throws Exception{
        this.logOutput(businessException);
        ConResult conResult = new ConResult(businessException.getErrorCode(),businessException.getErrorMessage());
        return conResult;
    }

//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public ConResult handException(Exception e){
//        ConResult conResult = new ConResult(323,e.toString());
//        return conResult;
//    }




}
