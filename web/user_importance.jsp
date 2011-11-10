<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<img src="./resources/images/gmail.PNG"
	style="height: 71px; width: 409px;" />
<br />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Important Users</title>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.6.4.js"></script>
<script type="text/javascript" src="./scripts/viz.js"></script>
<script type="text/javascript" src="./scripts/detailviz.js"></script>
<link rel="stylesheet" type="text/css"
	href="./resources/css/contact.css" />
</head>
<body onload="getImportance('PieChart');">
	<select id="selectedList"
		onchange="getImportance(this.options[this.selectedIndex].value)">
		<option value="PieChart">Pie Chart</option>
		<option value="ColumnChart">Column Chart</option>
	</select>

	<div id="chart_div1"></div>
	<div id="chart_div2"></div>
	<div id="popupContact">
		<div id="chart_div"></div>
	</div>
	<div id="backgroundPopup"></div>
</body>
</html>