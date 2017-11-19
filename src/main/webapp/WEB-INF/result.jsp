<!DOCTYPE html>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Gif Splitter Result 結果</title>
</head>
<body>

<h1>Gif Splitter</h1>

<h2>canvas上でアニメーションを再生 Play gif animation on canvas</h2>
<p>
	<canvas id="play" width="600" height="600">
		canvasが有効なブラウザでこのページを開いてください　Sorry, your browser doesn't support the canvas.
	</canvas>
</p>

<h2>画像フレーム一覧 gif frames</h2>
<c:forEach var = "URL" items = "${URLs}">
	<p><img src="${URL}" /></p>
</c:forEach>


<script src="${pageContext.request.contextPath}/javascript/createjs-2015.11.26.min.js"></script>
<script>
var urls = [];
var delay = ${delay}
</script>
<c:forEach var = "URL" items = "${URLs}">
	<script>
		urls.push("${URL}");
	</script>
</c:forEach>
<script src="${pageContext.request.contextPath}/javascript/gifplayer.js"></script>

</body>
</html>
