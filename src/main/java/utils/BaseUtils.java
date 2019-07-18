package utils;

//import com.fpapi.base.exception.BaseException;
import excption.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @ClassName: BaseUtils
 * @Description: (这里用一句话描述这个类的作用)
 * @author: sheng
 * @Date 2019/6/4 9:45
 * @Version 1.0
 */
public abstract class BaseUtils {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected HttpServletRequest request2;

    protected String getParameterStr(){
        StringBuilder sb = new StringBuilder("   ip "+ request2.getRemoteAddr() +"  请求路径 "+request2.getRequestURI()+"  --- 参数 {");
        Map<String, String[]> parameterMap = request2.getParameterMap();
        for (String s : parameterMap.keySet()) {
            sb.append("  ").append(s).append(":").append(parameterMap.get(s)[0]);
        }
        sb.append(" }");
        return sb.toString();
    }

    protected String getState(Exception ex){
        StackTraceElement stackTraceElement= ex.getStackTrace()[0];
        return "   Class: "+stackTraceElement.getClassName()+
                "   Line: "+stackTraceElement.getLineNumber()+
                "   Method: "+stackTraceElement.getMethodName()+"    ";
    }

    /**
     * 输出log日志,需要异常
     * @return
     * @param ex
     */
    protected boolean logOutput(BusinessException ex){
        if (ex != null){
            LOGGER.error(getState(ex) + ex.getMessage() + "  ---  " + ex.getCause());

            return true;
        }
        return false;
    }

    /**
     * 输出log日志,需要异常,需要提示信息
     * @return
     */
    protected boolean logOutput(BaseException ex, Map<String, Object> map){
        if (map != null && map.size()>0){
            StringBuilder sb = new StringBuilder("    异常 ___  ");
            for (String s : map.keySet()) {
                sb.append(s).append(":").append(map.get(s).toString()).append("  ");
            }
            LOGGER.error(getState(ex) + ex.getMessage() + "  ---  " + ex.getCause() + "  ---  " + sb);
            return true;
        }
        return false;
    }

    /**
     * 输出log日志,需要异常,需要提示信息
     * @return
     */
    protected boolean logOutput(BaseException ex, String... str){
        if (str != null && str.length>0){
            StringBuilder sb = new StringBuilder("    异常 ___  ");
            for (String s : str) {
                sb.append(s).append(str).append("  ");
            }
            LOGGER.error(getState(ex) + ex.getMessage() + "  ---  " + ex.getCause() + "  ---  " + sb);
            return true;
        }
        return false;
    }

    /**
     * 输出log日志,需要提示信息
     * @return
     */
    protected boolean logOutput(Map<String, Object> map){
        if (map != null && map.size()>0){
            StringBuilder sb = new StringBuilder("    异常 ___  ");
            for (String s : map.keySet()) {
                sb.append(s).append(":").append(map.get(s).toString()).append("  ");
            }
            LOGGER.error("Class: "+this.getClass().getName()+" method: "+
                    Thread.currentThread().getStackTrace()[1].getMethodName()+" line:"+
                    Thread.currentThread().getStackTrace()[1].getLineNumber() + "  ---  " + sb);
            return true;
        }
        return false;
    }

    /**
     * 输出log日志,需要提示信息
     * @return
     */
    protected boolean logOutput(String... str){
        if (str != null && str.length>0){
            StringBuilder sb = new StringBuilder("    参数 ___  ");
            for (String s : str) {
                sb.append(s).append("  ");
            }
            LOGGER.info("Class: "+this.getClass().getName()+" method: "+
                    Thread.currentThread().getStackTrace()[1].getMethodName()+" line:"+
                    Thread.currentThread().getStackTrace()[1].getLineNumber() + "  ---  " + sb);
            return true;
        }
        return false;
    }

    /**
     * 输出log日志,需要异常
     * @return
     */
    protected boolean logOutputException(Exception ex){
        if (ex != null){
            LOGGER.error(getState(ex) + ex.getMessage() + "  ---  " + ex.getCause(), ex);
            return true;
        }
        return false;
    }


}
