package utils;

import com.alibaba.fastjson.JSON;
import entity.ConResult;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket")
public class WebSocketUtils1 {


    public static void main(String[] args) {
      /*  ConResult conResult = new ConResult(1, efpInvoiceCopy.get("gfmc") + "^" + efpInvoiceCopy.get("hjje"));
        WebSocketUtils1 ss = new WebSocketUtils1();
        Map map = ss.getSocketMaps();
        ss.qunfa(map, JSON.toJSONString(conResult), conResult);*/
    }


    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    public static Map<String,Session> socketMaps = new HashMap<String,Session>();

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocketUtils1> webSocketSet = new CopyOnWriteArraySet<WebSocketUtils1>();
    private  static List<Object> lastMessage=new ArrayList<Object>();
    private  static int lastSize=5;

    public synchronized  static List<Object> getMessage(){
        List<Object> result=new ArrayList<Object>();
        synchronized (lastMessage){
            for(int i=lastMessage.size();i>0;i--){
                result.add(lastMessage.get(i-1));
            }
        }
        return result;
    }
    public synchronized static void addMessage(Object mesage){
        synchronized (lastMessage){
            if(lastMessage.size()>=lastSize){
                lastMessage.remove(0);
            }
            lastMessage.add(mesage);
        }
    }
   // private static Session session;

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据

    @OnMessage
    public synchronized void onMessageA(String mes,Session session) throws IOException {
        if(mes.equals("lastMeesage")){
            List<Object> lastMessage=getMessage();
            session.getBasicRemote().sendText(JSON.toJSONString(lastMessage));
        }else{
            session.getBasicRemote().sendText("{}");
        }
       /* session.getBasicRemote().sendText(conResult);*/
        //无数据交互后自动关闭所需时间
//        session.setMaxIdleTimeout(5000);
    }
    public  Session session;
    // public static Session session;

    @OnOpen
    public synchronized void onOpen(Session session) {

        socketMaps.put(session.getId(),session);
        System.out.println("sessiongetId"+session.getId());
        //无数据交互后自动关闭所需时间
        //session.setMaxIdleTimeout(5000);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public synchronized void onClose(Session session) {
        socketMaps.remove(session.getId()); // 从set中删除
        subOnlineCount(); // 在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

//    @OnMessage
//    public void onMessage(String message, Session session) {
//        System.out.println("来自客户端的消息:" + message);
//
//        // 群发消息
//        for (WebSocketUtils1 item : webSocketSet) {
//                item.sendMessage(message);
//
//        }
//    }
    @OnError
    public synchronized void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        socketMaps.remove(session.getId());
        error.printStackTrace();
    }
   /* public static void sendMessage(String message) throws IOException {

        this.session.getBasicRemote().sendText(message);

        // this.session.getAsyncRemote().sendText(message);
    }*/

//    public static Boolean sendMessage(String message){
//        try {
//
//            System.out.println("来自客户端的接收信息"+message);
//
//           /* Session session = webSocketSet.(socketId);
//            if(session == null){
//                return false;
//            }*/
//            session.getBasicRemote().sendText(message);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return true;
//    }


    public synchronized Boolean qunfa(Map<String, Session> socketMaps, String message, ConResult conResult){
        try {
            addMessage(conResult);
            for(String item:socketMaps.keySet()) {

                Session session = socketMaps.get(item);
                if (session == null) {
                    return false;
                }
                session.getBasicRemote().sendText(message);
//                LogUtils.writeLogFile(this.getClass().getResource("/").getPath() + "log/zpsocketlog.txt", "code"+conResult.getCode()+"推送结果"+conResult.getMessage()+"message数据"+message);
           }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true;
    }


    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
    public static synchronized void addOnlineCount() {
        WebSocketUtils1.onlineCount++;
    }
    public static synchronized void subOnlineCount() {
        WebSocketUtils1.onlineCount--;
    }
    public static Map<String, Session> getSocketMaps() {
        return socketMaps;
    }

}
