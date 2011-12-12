<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Important / Age</title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link rel="stylesheet" href="./resources/css/demo.css" type="text/css" media="screen">
	<link rel="stylesheet" href="./resources/css/demo-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="./resources/css/styles.css" type="text/css">
	<script type="text/javascript" src="./scripts/raphael.js"></script>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
	<script type="text/javascript" src="./scripts/jquery.simplemodal.js"></script>
	<script type="text/javascript">
	    var MAIL_HOST = "${host}";
	</script>
	<script type="text/javascript" src="./scripts/mainstat.js"></script>
</head>
<body>
	<table height=100% width=100% border=1>
		<tr>
			<td width="60%">
				<div id="canvas">
					<div id="paper">
					</div>
				</div>
			</td>
			<td width="40%">
				<div id = "tooltip">
				</div>
			</td>	
		</tr>
	</table>
	<a href="GmailViz.jsp">Back</a>
</body>

</html>