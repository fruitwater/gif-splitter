<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Gif Splitter Top　トップページ</title>
</head>
<body>
	<h2>gifアニメをキャンバス上で再生します Play Gif Animation on Canvas</h2>
	<h3>gifファイルをアップロードしてください Upload gif file</h3>
	<form action="upload" method="post" enctype="multipart/form-data">
		<p>
			<input type="file" name="upload">
			<input type="submit" value="アップロード">
		</p>
	</form>
</body>
</html>