<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<img src="./resources/images/gmail.PNG"
	style="height: 71px; width: 409px;" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
	<script type="text/javascript" src="./scripts/jquery.simplemodal.js"></script>
	<script type="text/javascript" src="./scripts/db.js"></script>
	<link rel="stylesheet" type="text/css" href="./resources/css/login.css" />
</head>

<body>
	<br>
	<div id="login_form" style='display:none'>		
		<div id="status" align="left">		
			<center>
				<div id="login_response">
					<!-- spanner -->
				</div> 
			</center>
			<form id="login" action="/">
				<div id="message"></div>
				<label>JDBC URL</label><input type="password" name="jdbc" id="jdbc"><br />
				<label>Database username</label><input type="password" name="email" id="email"><br />
				<label>Database password</label><input type="password" name="password" id="password"><br />
				<label>&nbsp;</label><input value="Login" name="Login" id="submit" class="big" type="submit" /><br>				
				<div id="ajax_loading">
					<img align="middle" src="./resources/images/spinner.gif">&nbsp;Please wait Analyzing your Inbox...
				</div>
			</form>	
	 	</div>
	</div>
</body>
</html>