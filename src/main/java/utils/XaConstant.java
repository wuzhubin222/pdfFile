package utils;

/**
 * @Title: XaConstant.java
 * @Package com.xa3ti.shengmilu.base.constant
 * @Description: 常量类
 * @author hchen
 * @date 2014年8月2日 上午10:07:48
 * @version V1.0
 */
public class XaConstant {

    /**
     * @author hchen
     * @ClassName: UserStatus
     * @Description: 后台用户状态常量
     * @date 2014年8月2日 上午10:10:09
     */
    public static class UserStatus {
        /**
         * @Fields status_normal : 正常
         */
        public static final int status_normal = 1;
        /**
         * @Fields status_lock : 锁定或禁用
         */
        public static final int status_lock = 0;
        /**
         * @Fields status_delete : 已删除
         */
        public static final int status_delete = 9;

	}

	public static class RoleId{
		/**
		 * 未验证过的机构的角色id
		 */
		public static final Long organ_role_ids = 5L;
		/**
		 * 验证过的机构的角色id
		 */
		public static final Long organ_role_id = 2L;
		/**
		 * 系统管理员的角色id
		 */
		public static final Long admin_role_id = 1L;
	}

	/**
	 * @ClassName: RoleStatus
	 * @Description: 角色的状态
	 * @author hchen
	 * @date 2014年8月2日 上午10:53:08
	 *
	 */
	public static class RoleStatus {
		/**
		 * @Fields status_normal : 正常
		 */
		public static final int status_normal = 1;
		/**
		 * @Fields status_delete : 已删除
		 */
		public static final int status_delete = 0;
	}

    /**
     * @author hchen
     * @ClassName: ResourcesStatus
     * @Description: 资源的状态
     * @date 2014年8月2日 上午11:14:46
     */
    public static class ResourcesStatus {
        /**
         * @Fields status_normal : 正常
         */
        public static final int status_normal = 1;
        /**
         * @Fields status_delete : 已删除
         */
        public static final int status_delete = 0;
    }

    /**
     * @author hchen
     * @ClassName: ResourceShowType
     * @Description: 资源级别
     * @date 2014年8月8日 下午2:23:32
     */
    public static class ResourceShowType {
        /**
         * @Fields page_level : 页面级资源
         */
        public static final int page_level = 0;
        /**
         * @Fields menu_level : 菜单级资源
         */
        public static final int menu_level = 2;
        /**
         * @Fields button_level : 按钮级资源
         */
        public static final int button_level = 1;
    }

    public static class SessionKey {

        /**
         * @Fields currentUser : session中存放和获取当前用户信息的key
         */
        public static final String currentUser = "currentUser";

        /**
         * @Fields currentMenuData : session中存放和获取当前用户菜单的key
         */
        public static final String currentMenuData = "currentMenuData";

        /**
         * @Fields currentMessageList : 发消息的队列
         */
        public static final String currentMessageList = "messageUserList";
    }

    /**
     * @author hchen
     * @ClassName: Status
     * @Description: 常用状态类型
     * @date 2014年8月13日 下午2:45:25
     */
    public static class Status {
        /**
         * @Fields locked : 锁定状态
         */
        public static final int locked = -1;

        /**
         * @Fields invalid : 无效状态
         */
        public static final int invalid = 0;

        /**
         * @Fields valid : 有效/正常状态
         */
        public static final int valid = 1;

        /**
         * @Fields publish : 发布
         */
        public static final int publish = 2;

        /**
         * @Fields delete : 删除
         */
        public static final int delete = 3;

        /**
         * @Fields publish : 预发布
         */
        public static final int prePublish = 4;
    }


    /**
     * @author hchen   上下架状态
     */
    public static class onStatus {
        /**
         * 下架
         */
        public static final int down = 0;
        /**
         * 上架
         */
        public static final int up = 1;
        /**
         * 预上架
         */
        public static final int beforup = 2;
    }

    public static class AuditStatus {
        /**
         * @Fields noAudit : 待审核
         */
        public static final int noAudit = 0;


        /**
         * @Fields preAudit : 待审核
         */
        public static final int preAudit = 2;

        /**
         * @Fields noPassAudit : 审核不通过
         */
        public static final int noPassAudit = -1;


        /**
         * @Fields preAudit : 已审核
         */
        public static final int audited = 1;
    }



    /**
     * @author hchen
     * @ClassName: TreeNodeIcon
     * @Description: 节点类型
     * @date 2014年8月13日 下午1:59:01
     */
    public static class TreeNodeIcon {
        /**
         * @Fields html_24 : 24*24的页面级菜单图片
         */
        public static final String html_24 = "image/html_24.png";
        /**
         * @Fields menu_24 : 24*24的菜单级菜单图片
         */
        public static final String menu_24 = "image/menu_24.png";
        /**
         * @Fields action_24 : 24*24的按钮级菜单图片
         */
        public static final String action_24 = "image/action_24.png";
    }

    /**
     * 订单状态
     *
     * @author hchen
     */
    public static class orderStatus {
        /**
         * 未支付
         */
        public static final Integer notpay = 1;
        /**
         * 已支付
         */
        public static final Integer alreadyPay = 2;
        /**
         * 已完成
         */
        public static final Integer complete = 3;
        /**
         * 已取消
         */
        public static final Integer cancle = 4;
    }

    /**
     * @author hchen
     * @ClassName: Code
     * @Description: xaResult的code
     * @date 2014年8月15日 下午5:34:20
     */
    public static final class Code {
        /**
         * @Fields ok : 成功
         */
        public static final int success = 1;
        /**
         * @Fields error : 失败
         */
        public static final int error = 0;
        /**
         * token失效
         */
        public static final int code_failure_token = 101;
        /**
         * fresh_token失效
         */
        public static final int code_failure_fresh_token = 102;

        public static final int code_system_failure = 10001;

        public static final int code_business_failure = 20001;

        public static final int code_validation_failure = 30001;

        public static final int code_database_failure = 40001;

        public static final int code_json_failure = 50001;
    }

    /**
     * @author hchen
     * @ClassName: Message
     * @Description: xaResult的message
     * @date 2014年8月15日 下午5:34:20
     */
    public static final class Message {
        /**
         * @Fields ok : 成功
         */
        public static final String success = "000000:成功";
        /**
         * @Fields error : 失败
         */
        public static final String error = "系统错误!";
        public static final String object_not_find = "找不到要操作的记录!";

        public static final String occupation = "已预定";


    }


    /**
     * 登录请求
     *
     * @author zj
     */
    public static class Login {
        /**
         * web登录请求
         */
        public static final int REQUEST_TYPE_WEB = 0;
        /**
         * 手机终端登录请求,可根据需要更改为其他如wap登录请求等
         */
        public static final int REQUEST_TYPE_MOBILE = 1;
        /**
         * login.html登录请求时请求方式的参数,和j_username,j_password一样不可指定其他名称
         */
        public static final String SPRING_REQUEST_KEY = "SPRING_REQUEST_KEY";
    }

    public static class Lock {

        public static final int lock = 1;

        public static final int noLock = 0;
    }


    /**
     * 富文本类型
     *
     * @author Administrator
     */
    public static final class pageConfigType {
        /**
         * 机构机构合作
         */
        public static final String cooperateOrgan = "1";

        /**
         * 关于我们
         */
        public static final String aboutUs = "2";
        /**
         * 使用指南（机构）
         */
        public static final String usageOrgan = "3";
        /**
         * 使用指南（会员）
         */
        public static final String usageMember = "4";
        /**
         * 平台协议
         */
        public static final String protocol = "5";
        /**
         * 统一排版信息
         */
        public static final String footer = "6";

    }

    public static final class LogType {
        /**
         * 登录日志
         */
        public static final int loginLog = 1;

        /**
         * 邀请日志
         */
        public static final int inviteLog = 2;
    }






    /**
     * 对应的messageSub中的类型
     *
     * @author Administrator
     */
    public static final class messageType {
        /**
         * 公告
         */
        public static final int notice = 1;
        /**
         * 消息
         */
        public static final int message = 2;
        /**
         * 系统消息
         */
        public static final int systemMessage = 3;
    }

    public static final class createUserType {
        /**
         * created by model herself
         */
        public static final Long model = (long) 5;
        /**
         * created by photographer himself
         */
        public static final Long goodsshoot = (long) 6;
    }






    /**
     * 购买课程时的汇率
     */
    public static final Double huilv = 30d;


    /**
     * 区分手机与邮箱的标识
     */
    public static final class CheckMobileOrEmail {
        /**
         * @Fields goods_shoot : 手机
         */
        public static final int mobileCode = 1;
        /**
         * @Fields goods_jingpai : 邮箱
         */
        public static final int emailCode = 2;
    }

    /**
     * 用户类型标识符
     */
    public static final class UserType {
        /**
         * @Fields goods_shoot : 企业
         */
        public static final int enterprise = 1;
        /**
         * @Fields goods_jingpai : 个人
         */
        public static final int personage = 2;
        /**
         * @Fields admin : 管理员
         */
        public static final int admin = 0;
    }

    /**
     * 企业登录方式标识符
     */
    public static final class NsrLoginCode {
        /**
         * @Fields goods_shoot : 企业Ukey登录
         */
        public static final int byUkey = 1;
        /**
         * @Fields goods_jingpai : 操作员登录
         */
        public static final int byCzy = 2;
    }

    /**
     * 管理员、操作员区分标识符
     */
    public static final class distinguishAdmin {
        /**
         * @Fields goods_shoot : 管理员
         */
        public static final int admin = 1;
        /**
         * @Fields goods_jingpai : 普通操作员
         */
        public static final int Czy = 2;
    }

    /**
     * 操作员状态标识符
     */
    public static final class CzyStatus {
        /**
         * @Fields goods_shoot : 正常
         */
        public static final int normal = 1;
        /**
         * @Fields goods_jingpai : 锁定
         */
        public static final int lock = 2;

        /**
         * @Fields goods_jingpai : 删除
         */
        public static final int delet = 3;
    }

    /**
     * 发票作废标识符
     */
    public static final class FpCancellation {
        /**
         * @Fields goods_shoot : 正常
         */
        public static final int normal = 0;
        /**
         * @Fields goods_jingpai : 作废
         */
        public static final int cancellation = 1;

    }

    /**
     * 文件类型
     */
    public static final class FileType {
        /**
         * @Fields goods_shoot : 图片
         */
        public static final int type_photo = 1;
        /**
         * @Fields goods_jingpai : WORD
         */
        public static final int type_WORD = 2;
        /**
         * @Fields goods_jingpai : EXCEL
         */
        public static final int type_EXCEL = 3;
        /**
         * @Fields goods_jingpai : 其它
         */
        public static final int type_other = 4;
        /**
         * @Fields goods_jingpai : 7Z
         */
        public static final int type_7Z = 5;
        /**
         * @Fields goods_jingpai : exe
         */
        public static final int type_exe = 6;
        /**
         * @Fields goods_jingpai : txt
         */
        public static final int type_txt = 7;

    }

    /**
     * 图片类型
     */
    public static final class PictureType {
        /**
         * @Fields goods_shoot : 身份证证明图
         */
        public static final int sfzzm = 7001;
        /**
         * @Fields goods_jingpai : 身份证反面图
         */
        public static final int sfzfm = 7002;
        /**
         * @Fields goods_jingpai : 手持身份证照片
         */
        public static final int scsfz = 7003;
        /**
         * @Fields goods_jingpai : 营业执照图
         */
        public static final int yyzz = 8001;
        /**
         * @Fields goods_jingpai : 税务登记证图
         */
        public static final int swdjzt = 8002;
        /**
         * @Fields goods_jingpai : 组织机构代码证图
         */
        public static final int zzjgdmzt = 8003;
        /**
         * @Fields goods_jingpai : 公司logo
         */
        public static final int companyLogo = 8004;

    }

}
