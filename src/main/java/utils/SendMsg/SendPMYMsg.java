package utils.SendMsg;
import java.util.HashMap;
import java.util.Set;



/*
108774	【中慈税通】你的注册验证码为:{1},欢迎注册入驻自助报税开票平台，如非本人操作敬请忽略。
114639	【中慈税通】您的随机验证码是{1}
39062	【精拍app】您的单号为{1}的订单将于{2}到期，到期未确认完成我们将视订单执行完成并付款给商家，请及时联系商家安排拍摄。
39065	【精拍app】您的资料审核已通过，非常感谢您的支持！
39067	【精拍app】您的资料审核未通过，请完善资料后再提交审核，审核意见:{1}
39315	【精拍app】申请提现验证码:{1}，如非本人操作敬请忽略。
61780	【精拍app】您有新订单了！订单编号为{1},请登录后台查看并审核订单

		
		phone="13113624202";
		templateId = "108774";
		String[] sLst = {verityCode};
		try {
			SendPMYMsg.sendMsg(phone,templateId, sLst);
		} catch (Exception e) {
			e.printStackTrace();
		}


ACCOUNT SID：
    8aaf0708567749f001567e0eb72507e2

AUTH TOKEN：
    80b2f9b7833348fea49ff024ff4433fd （APP TOKEN 请到应用管理中获取）

Rest URL(生产)：
    https://app.cloopen.com:8883

AppID(默认)：
    8aaf0708567749f001567e0eb7b507e8

*/


public class SendPMYMsg {
//	private static final String accountSid = "8a48b5514fd49643014feecdc2b73fbd";
//	private static final String accountToken  = "5894434b3c74477a988820f19eb00329";
//	private static final String appId = "8a48b5514fd49643014feecf9e3e3fe6";
//	private static final String serverIP = "app.cloopen.com";
//	private static final String serverPort = "8883";
//	private static final String softVersion = "2013-12-26";
	public static final String accountSid = "8aaf0708567749f001567e0eb72507e2";
	public static final String accountToken  = "80b2f9b7833348fea49ff024ff4433fd";
	public static final String appId = "8aaf0708567749f001567e0eb7b507e8";
	public static final String serverIP = "app.cloopen.com";
	public static final String serverPort = "8883";
	public static final String softVersion = "2013-12-26";

	// ($to,$datas,$tempId);
	
	public static void sendMsg(String to, String datas, String[] tempId) throws Exception{
		CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
		restAPI.init(serverIP, serverPort);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount(accountSid, accountToken);// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId(appId);// 初始化应用ID
		HashMap<String, Object> result = null;
		
		result = restAPI.sendTemplateSMS(to,datas,tempId);

		System.out.println("SDKTestSendTemplateSMS result=" + result);
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
		}
		
	}


	public static HashMap<String, Object> sendMsgBak(String to, String datas, String[] tempId) throws Exception{
		CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
		restAPI.init(serverIP, serverPort);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount(accountSid, accountToken);// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId(appId);// 初始化应用ID

		return restAPI.sendTemplateSMS(to,datas,tempId);

	}
}
