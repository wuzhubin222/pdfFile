package utils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/websocket/{param}")
public class WebSocketUtils {
    /*/socket/{param}*/
    public static Map<String,Session> socketMaps = new HashMap<String,Session>();

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value ="param") String param){
        socketMaps.put(session.getId(),session);
        //无数据交互后自动关闭所需时间
//        session.setMaxIdleTimeout(5000);
        sendMessage(session.getId(),"连接成功"+param);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session){
        socketMaps.remove(session.getId());  //从set中删除
        System.out.println("有一连接关闭！当前在线人数为" + socketMaps.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        if(message.equals("total")){
            sendMessage(session.getId(),"连接总数"+socketMaps.size());
        }else {
            sendMessage(session.getId(),"无法识别此消息");
        }
        System.out.println("来自客户端 "+session.getId()+" 的消息:" + message);
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误"+session.getId()+"   "+error.toString());
    }

    public static Boolean sendMessage(String socketId,String message){
        try {

            System.out.println("来自客户端的接收信息"+message);

            Session session = socketMaps.get(socketId);
            if(session == null){
                return false;
            }
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return true;
    }


}
