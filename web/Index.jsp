<!--
widget, a free CSS web template by spyka Webmaster (www.spyka.net)

Download: http://www.spyka.net/web-templates/widget/

License: Creative Commons Attribution
//-->
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>GmailViz - An analysis of your Inbox</title>
	<link rel="stylesheet" href="./resources/css/styles.css" type="text/css" />
	
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<script type="text/javascript" src="http://code.jquery.com/jquery-1.6.4.js"></script>
	<script type="text/javascript" src="./scripts/jquery.simplemodal.js"></script>
	<script type="text/javascript" src="./scripts/slider.js"></script>
	<script type="text/javascript" src="./scripts/superfish.js"></script>
	<script type="text/javascript">
		    var MAIL_HOST = "${host}";
	</script>
</head>

<body class="homepage">
	<div id="container">
		<div id="header">
			<h1>
				<a href="/GmailViz/Index.jsp">Gmail<strong>Viz</strong>
				</a>
			</h1>
			<h2>Helps you analyze your inbox...</h2>
			
		</div>
		<div id="nav">
			<ul class="sf-menu dropdown">
				<li><a id="analyze" href="#" onClick="analyze()">Analyze</a>
				</li>
				<li><a href="Index.jsp">Home</a>
				<li><a class="has_submenu" href="examples.html">Aggregates</a>
					<ul>
						<li><a href="GmailViz.jsp">Visualize Gmail</a>
						</li>
						<li><a href="emailoverloading.jsp">Email Overload</a>
						</li>
						<li><a href="UserImportance.jsp">User Importance</a>
						</li>
						<li><a href="Userlastreplied.jsp">User Last Replied</a>
						</li>
						<li><a href="Keyword.jsp">Keyword Analysis</a>
						</li>
					</ul></li>
				
				<li><a href="#" onClick="logout()">Logout</a>
				</li>
			</ul>
		</div>

		<div id="slides-container" class="slides-container">
			<div id="slides">
				<div>
					<img src="./resources/images/gmail.PNG" style="height: 71px; width: 409px;" /> <br><br><br><br>
									<h2>Welcome to Gmail Viz, <div id="account"></div></h2>
									<p>
										A project for Web Viz class at NC-State University that helps
										you analyze your inbox for fun!<br> Disclaimer: No
											malicious attempt to get your username and password is made
											while we analyse your inbox...
									</p>
				</div>
				<div>
					<div class="slide-image slide-image-right" id="slide-image slide-image-right-mainstat"></div>
					<div class="slide-text">
						<h2>Mail importance vs time</h2>
						<p>Mail importance is plotted against time and it is divided into 4 categories on y axis and 5 divisions on x axis  
						</p>
					</div>
				</div>

				<div>
					<div class="slide-image slide-image-right" id="slide-image slide-image-right"></div>
					<div class="slide-text">
						<h2>User and his importance in your inbox</h2>
						<p>This chart gives the top 10 contacts present in your inbox
							and their importance. If a particular user in on left side of
							hAxis and his importance is closer to zero on vAxis then he is
							more important to you.</p>

					</div>
				</div>
					<div>
					<div class="slide-image slide-image-right" id="slide-image slide-image-right-pie1"></div>
					<div class="slide-text">
						<h2>User Importances</h2>
						<p>User Importance according to how often you contact them and
							 how quickly you respond to them after they have send a mail
							 to them. You can edit user importance <a href="imp_contact.jsp">here</a> 
						</p>
					</div>
				</div>
				
			</div>
			<div class="controls">
				<span class="jFlowNext"><span>Next</span>
				</span><span class="jFlowPrev"><span>Prev</span>
				</span>
			</div>
			<div id="myController" class="hidden">
				<span class="jFlowControl">Slide 1</span><span class="jFlowControl">Slide
					1</span><span class="jFlowControl">Slide 1</span><span class="jFlowControl">Slide 1</span>
			</div>
		</div>

		<div id="body">
			<div id="content">
				<div class="box">
					<h2>Introduction</h2>
					<p>
						Gmail Viz is a project that is aimed to analyze for free!!!<br>
							We ask you to login to your gmail account and once your sign-in
							details are authenticated with Gmail servers we run our
							algorithms to scan your inbox and build results for the analysis.<br>
								We have used oracle10g database in the backend to store your
								data in the backend. Once you logout the database is cleared. So
								you are assured that we will not be using your account details
								in any malicious manner. 
					</p>

				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
 	<div id="footer">

		<div id="footer-links">
			<p>
				GmailViz &copy; 2011. Website powered by design provided on <a
					href="http://www.spyka.net">spyka.net</a> 
			</p>
		</div>
	</div>
	<div id="popupContact">
		<div id="chart_div"></div>
	</div>
	<div id="backgroundPopup">
	</div>
	
	<div id="login_form1" style='display: none'>
		<div id="ajax_loading1">
			<img align="center" src="./resources/images/spinner.gif">&nbsp;Please wait while we populate the data...
		</div>
	</div>
	
	<div id="login_form" style='display: none'>
		<div id="ajax_loading">
			<img align="center" src="./resources/images/spinner.gif">&nbsp;Analyzing your inbox...This might take some minutes.
		</div>
	</div>
</body>

<script type="text/javascript" src="./scripts/custom.js"></script>
<script type="text/javascript" src="./scripts/raphael.js"></script>
<script type="text/javascript" src="./scripts/gmailviztrailer.js"></script>
</html>