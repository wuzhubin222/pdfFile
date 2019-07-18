package utils;

import entity.BusinessErrorEnum;

public class BusinessException extends RuntimeException{

    private int errorCode;

    private String errorMessage;

    private String logPath;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getLogPath() {
        return logPath;
    }

    public BusinessException() {
        super();
    }

    public BusinessException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMessage = errorMsg;
    }

    public BusinessException(int errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = BusinessErrorEnum.getBusinessErrorEnum(errorCode).getErrorMsg();
    }

    public BusinessException(int errorCode,String errorMsg,String logPath) {
        this.logPath = logPath;
        this.errorCode = errorCode;
        this.errorMessage = BusinessErrorEnum.getBusinessErrorEnum(errorCode).getErrorMsg();
    }

}
