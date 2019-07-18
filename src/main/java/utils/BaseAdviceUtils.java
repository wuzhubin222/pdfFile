package utils;

import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @ClassName: BaseAdviceUtils
 * @Description: 基础日志打印抽象类
 * @author: sheng
 * @Date 2019/6/3 16:30
 * @Version 1.0
 */
public abstract class BaseAdviceUtils extends BaseUtils {

    @ModelAttribute
         String interceptParam(){
        LOGGER.info("Class: "+this.getClass().getName()+" method: "+
                Thread.currentThread().getStackTrace()[1].getMethodName()+" line:"+
                Thread.currentThread().getStackTrace()[1].getLineNumber()+getParameterStr());
        return "result";
    }


}
