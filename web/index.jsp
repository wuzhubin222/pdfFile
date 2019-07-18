<%--
  Created by IntelliJ IDEA.
  User: zccx
  Date: 2017/1/11
  Time: 12:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <script src="./static/jquery-1.11.0.min.js"></script>
    <script src="./static/test.js"></script>
    <title>pdfFile</title>
  </head>
  <%--<script type="text/javascript">--%>
    <%--var websocket = null;--%>

    <%--//判断当前浏览器是否支持WebSocket--%>
    <%--if('WebSocket' in window){--%>
      <%--websocket = new WebSocket("ws://192.168.3.9:8080/test/websocket");--%>
    <%--}--%>
    <%--else{--%>
      <%--alert('Not support websocket')--%>
    <%--}--%>

    <%--//连接发生错误的回调方法--%>
    <%--websocket.onerror = function(){--%>
      <%--alert("已断开连接");--%>
    <%--};--%>

    <%--//连接成功建立的回调方法--%>
    <%--websocket.onopen = function(event){--%>
      <%--console.log("已连接至服务端");--%>
    <%--}--%>

    <%--//接收到消息的回调方法--%>
    <%--websocket.onmessage = function(event){--%>
      <%--alert(event.data);--%>
    <%--}--%>

    <%--//连接关闭的回调方法--%>
    <%--websocket.onclose = function(){--%>
      <%--alert("关闭");--%>
    <%--}--%>

    <%--//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。--%>
    <%--window.onbeforeunload = function(){--%>
      <%--websocket.close();--%>
    <%--}--%>

    <%--//关闭连接--%>
    <%--function closeWebSocket(){--%>
      <%--websocket.close();--%>
    <%--}--%>

    <%--//发送消息--%>
    <%--function send(){--%>
      <%--var message = document.getElementById('text').value;--%>
      <%--websocket.send(message);--%>
    <%--}--%>
  <%--</script>--%>
  <body>
  <h1>it works!</h1>
  <%--<button onclick="Stest()">测试的方法</button>--%>
  </body>

</html>
