package entity;

public enum BusinessErrorEnum{

    MSGN(-1,"不存在的错误码"),

    MSG0(0,"成功"),

    MSG1(1,"系统繁忙，请稍候再试"),

    ///////////////////////////////////////////////////////////////////////

    MSG1000(1000,"获取验证码过快，请稍候再试"),

    MSG1001(1001,"请输入正确的验证码"),

    MSG1002(1002,"验证码已过期"),

    MSG1003(1003,"获取验证码失败，请稍候再试"),

    MSG1004(1004,"操作失败，此手机号已注册"),

    MSG1005(1005,"此微信号已绑定其它号码"),

    MSG1006(1006,"手机号码未注册"),

    MSG1007(1007,"密码错误"),


    //////////////////////////////////////////////////////////////////////

    MSG2000(2000,"手机号码格式错误"),

    MSG2001(2001,"密码格式错误"),

    MSG2002(2002,"参数openid不合法"),

    MSGdwdw(120017,"测试错误");

    int errorCode;

    String errorMsg;

    BusinessErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public static BusinessErrorEnum getBusinessErrorEnum(int code){
        for(BusinessErrorEnum businessErrorEnum : BusinessErrorEnum.values()){
            if(businessErrorEnum.getErrorCode() == code){
                return businessErrorEnum;
            }
        }
        return BusinessErrorEnum.MSGN;
    }

}
