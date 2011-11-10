
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
<div id="header"><img src="./resources/images/gmail.PNG" style="height: 71px; width: 409px; "/></div>
Analyse your Inbox:<br />

<span style="font-size: 15px;">
<a id="login_link" href="#">LOGIN</a>
</span>


<div id="login_form" style='display:none'>

<div id="status" align="left">

<center><h1>LOGIN</h1> 

<div id="login_response"><!-- spanner --></div> </center>

<form id="login" action="javascript:alert('success!');">
<input type="hidden" name="action" value="user_login">
<input type="hidden" name="module" value="login">
<label>E-Mail</label><input type="text" name="email" id="email"><br />  
<label>Password</label><input type="password" name="password" id="password"><br />  
<label>&nbsp;</label><input value="Login" name="Login" id="submit" class="big" type="submit" />

<div id="ajax_loading">
<img align="absmiddle" src="./resources/images/spinner.gif">&nbsp;Please wait Analyzing your Inbox...
</div>

</form>

 </div>

</div>

</body>
</html>