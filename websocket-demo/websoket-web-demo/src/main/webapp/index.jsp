<%@page pageEncoding="utf-8" language="java" %>
<html>
<body>
<h2>Hello World!</h2>
<div id='content'>

</div>
</body>
<script>
    var websocket = null;
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://" + document.location.host + "/ws/admin");
    } else {
        alert('当前浏览器 Not support websocket')
    }

    //连接发生错误的回调方法
    websocket.onerror = function () {
        setMessageInnerHTML("WebSocket连接发生错误");
    };

    //连接成功建立的回调方法
    websocket.onopen = function () {
        setMessageInnerHTML("WebSocket连接成功");
    }

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        setMessageInnerHTML(event.data);
    }

    //连接关闭的回调方法
    websocket.onclose = function () {
        setMessageInnerHTML("WebSocket连接关闭");
    }

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }

    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }

    function setMessageInnerHTML(msg) {
        console.log(msg)

    }
</script>
</html>
