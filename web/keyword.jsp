<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title>Modal Ajax Login Form</title>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="./scripts/jquery.simplemodal.js"></script>
<script type="text/javascript" src="./scripts/login1.js"></script>
<link rel="stylesheet" type="text/css" href="./resources/css/login.css" />

</head>

<body>
<form action="http://google.co.uk/search" method="get" onsubmit="postTheForm(this); return false;">
    <input type="hidden" name="q" value="Stackoverflow"/>
    <input type ="text" id="keyword" />
    <input type="submit" value="Click here"/>
</form>
<div id="postResults"></div>
</body>
</html>
