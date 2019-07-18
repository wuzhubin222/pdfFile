package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import constant.Constant;
import entity.ConResult;
import excption.BusinessException;
import excption.BusinessException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.ConResException;
import utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseFileController
{


    @ExceptionHandler(ConResException.class)
    @ResponseBody
    public String handleConResException(ConResException conResException)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("errcode",conResException.getErrorCode()+"");
        jsonObject.put("errmsg",conResException.getErrorMessage());
        return jsonObject.toJSONString();
    }

}
