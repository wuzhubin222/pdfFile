package excption;

import entity.BusinessErrorEnum;

public class BusinessException extends RuntimeException{

    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private String errorMessage;

    public BusinessException(String errorMsg) {
        super();
    }

    public BusinessException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMessage = errorMsg;
    }

    public BusinessException(int errorCode, String errorMsg,Throwable t) {
        super(errorMsg,t);
        this.errorCode = errorCode;
        this.errorMessage = errorMsg;
    }

    public BusinessException(int errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = BusinessErrorEnum.getBusinessErrorEnum(errorCode).getErrorMsg();
    }

}
