package utils;

import entity.ConResult;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/zpwebsocket/{phone}")
public class WebSocketzpUtils {

    private static int onlineCount = 0;
    private  static List<String> sendMessage=new ArrayList<String>();

    /*private static Map<String,Session> socketMaps = new HashMap<String,Session>();*/
    private static Map<String,Map<String,Session>> socketMaps=new HashMap<String,Map<String,Session>>();

    private Session session;


    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("phone") String phone) throws IOException {
//        this.session = session;
        if(!socketMaps.containsKey(phone)){
            socketMaps.put(phone,new HashMap<String, Session>());
        }
        session.getUserProperties().put("phone",phone);
        socketMaps.get(phone).put(session.getId(),session);

//        onlineCount++;
//        System.out.println("有新连接加入！当前在线人数为" + onlineCount);
//        ConResult conResult = new ConResult(1,session.getId());
//        System.out.println(session.getId());
        session.getBasicRemote().sendText("{}");
//        sendMessage(session.getId(),JSON.toJSONString(conResult));
    }

    /** ``
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session){
        String phone=   session.getUserProperties().get("phone").toString();
       if( socketMaps.containsKey( phone) ){
           String sessionId=session.getId();
           if(socketMaps.get(phone).containsKey(sessionId)){
               socketMaps.get(phone).remove(sessionId);
           }
       }

    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session)   {
            System.out.println(message);
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        onClose(session);
        System.out.println("websocket发生错误");
    }


    public  Boolean sendMessage(String socketId,String message){
        return sendMessage(socketId,message,true);
    }
    /*boolean isSend*/
    public  Boolean sendMessage(String socketId,String message,boolean isSend){
        try {

            if(socketMaps.containsKey(socketId)){
                Map<String,Session> sessionMap = socketMaps.get(socketId);
                if (isSend) {
                    System.out.println("phone:" + socketId + ":" + message);
//                    LogUtils.writeLogFileName("zpsocketlog.txt", "code"+"手机号码"+socketId+"message数据"+message);
                }
                for(Session session:sessionMap.values()) {
                    if (session == null) {
                        return false;
                    }

                    session.getBasicRemote().sendText(message);
                }
            }else{
                System.out.println("not phone " +socketId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true;
    }


    public static void qunfa(String text){
    }

    public static  Map<String,Map<String,Session>> getSocketMaps() {
        return socketMaps;
    }




}
