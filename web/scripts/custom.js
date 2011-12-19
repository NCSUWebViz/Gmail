img1 = new Image(16, 16);
img1.src = "./resources/images/spinner.gif";

img2 = new Image(220, 19);
img2.src = "./resources/images/ajax-loader.gif";

var SEARCH_PREFIX = "#search/";
var APPS_PREFIX = "https://mail.google.com/a/";
var GMAIL_PREFIX = "http://mail.google.com/mail/";
var timestamp = [];
var foo1;
var email;
function analyze() {
	var foo = $('#login_form').modal();
	$('#ajax_loading').show();
	$.ajax({
		type : "POST",
		url : "/GmailViz/home",
		success : function(msg) {
			msg = eval(' (' + msg + ') ');
			if(msg.success == 'true') {
				$('#ajax_loading').html('Analyzed');
			}
			else if(msg.success == 'false') {
				$('#ajax_loading').css({color:"red"});
				$('#ajax_loading').css({"text-align":"center"});
				document.getElementById("ajax_loading").innerHTML = "Analysis Failed!";
			}
			foo.close();
		}
	});
}


function getDetails() {
	$.ajax({
		type : "POST",
		url : "/GmailViz/details",
		success : function(msg) {
			console.log('message : ' + msg)
			msg = eval(' (' + msg + ') ');

			if(msg.Details.success == 'true') {
				if(msg.Details.email == '') {
					window.location = location.protocol + '//' + location.host + "/GmailViz/Login.jsp";
				}
				else {
					init();
				}
			}
			else {
				return "";
			}
		}
	});
}

function logout() {
	$.ajax({
		type : "POST",
		url : "/GmailViz/logout",
		success : function(msg) {
			msg = eval(' (' + msg + ') ');
			if(msg.success == 'true') {
				window.location = location.protocol + '//' + location.host + "/GmailViz/Login.jsp";
			}
		}
	});
}

function onLoad() {
	email = getDetails();
}


$(document).ready(function() {
	if($("#slides-container").length > 0)
	{
		$("#myController").jFlow({
			slides: "#slides",
			controller: ".jFlowControl", // must be class, use . sign
			slideWrapper : "#jFlowSlide", // must be id, use # sign
			selectedWrapper: "jFlowSelected",  // just pure text, no sign
			easing: "swing",
			width: "850px",
			auto: true, // set to false to disable auto-slide
			height: "315px",
			duration: 1500,
			prev: ".jFlowPrev", // must be class, use . sign
			next: ".jFlowNext" // must be class, use . sign
		});
	}

	$('ul.dropdown').superfish({
		autoArrows: false 
	});  
	foo1 = $('#login_form1').modal();
	$('#ajax_loading1').show();
	onLoad();
});




function init(){
	$.ajax({
		type : 'POST',
		url : "/GmailViz/details",
		success : function(msg) {
			console.log('MSG:::::::'+msg)
			msg = eval(' (' + msg + ') ');
				console.log('MSG:::::::'+msg.Details.email)
				$("#account").text(msg.Details.email);
			
		}
	});
	$.ajax({
			type : 'GET',
			url : "/GmailViz/userlastreplied",
			success : function(msg) {
				timestampmsg = msg;
				drawChart(msg);
				
				$.ajax({
					type : 'GET',
					url : "/GmailViz/ulreimp",
					success : function(msg) {
						console.log('ulreimp');
						console.log(msg);
						var msgObj = eval(' (' + msg + ') ');
						if(msgObj.success == 'true') {
							drawChart1(msg); //individual importance
							foo1.close();
						}
						else {
							$('#ajax_loading1').html('1 Failed');
						}
					}
				});	
			}
	});
	$.ajax({
		type : 'GET',
		url : "/GmailViz/impcontact",
		success : function(msg) {
			console.log('impcontact');
			console.log(msg);
			msg = eval(' (' + msg + ') ');
			if(msg.success == 'true') {
				drawPieChart1(msg);
			}
			else {
				$('#ajax_loading1').html('2 Failed');
			}
		}
	});
}
function runSearch(query) {
	console.log('inside RunSearch')
	console.log(MAIL_HOST)
	window.open(
       (MAIL_HOST == "gmail.com" ? GMAIL_PREFIX : APPS_PREFIX + MAIL_HOST) + SEARCH_PREFIX + encodeURIComponent(query) + MAIL_HOST);
}

google.load("visualization", "1", {packages:["corechart"]});


function drawChart(msg) {
	msg = eval('(' + msg + ')');
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Email-id');
	data.addColumn('date', 'Last Replied');
	data.addRows(msg.UserLastReplied.length);
	var formatter_medium = new google.visualization.DateFormat({formatType: 'long'});
	for ( var j = 0; j < msg.UserLastReplied.length; j++) {
		if((msg.UserLastReplied[j].year != '') && (msg.UserLastReplied[j].month != '') && (msg.UserLastReplied[j].day != '')) {
			year = parseInt(msg.UserLastReplied[j].year);
			month = parseInt(msg.UserLastReplied[j].month);
			day = parseInt(msg.UserLastReplied[j].day);
		
			console.log('year:'+year+' month:'+month+' day:'+day)
			
			my_email=msg.UserLastReplied[j].email_id;
			ind=my_email.indexOf("@");
			email_slice=my_email.slice(0,ind);
			data.setValue(j, 0, email_slice);
			data.setValue(j, 1, new Date(year, month-1, day));
			data.setFormattedValue(j,1,new Date(year, month-1, day));
			date  = new Date(year, month-1, day);
			timestamp[j]  = date;
		}
		else {
			timestamp[j] = null;
		}
		//console.log('timestamp : ' + timestamp[j])
	}
	  var formatter_medium = new google.visualization.DateFormat({formatType: 'medium'});
	formatter_medium.format(data, 1);
//	formatter_medium.format(data,1);
	console.log(data)
	
	var options = {
		'title' : 'Users and when was his last replied',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
		'legend':'none',
		'curveType': 'function'
	};
//
//	var chart = new google.visualization.LineChart(document.getElementById('slide-image'));
//	chart.draw(data, options);
}
function drawChart1(msg) {
	console.log("drawchart 1: " + msg);
	msg = eval('(' + msg + ')');
	
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Email-id');
	data.addColumn('number', 'Importance');
	data.addRows(msg.Importance.length);
	
	for ( var j = 0; j < msg.Importance.length; j++) {
		var my_email=msg.Importance[j].email_id
		var ind=my_email.indexOf("@");
		var email_slice=my_email.slice(0,ind);
		var message;
		
		data.setValue(j, 0, email_slice);
		data.setValue(j, 1, parseInt(msg.Importance[j].user_imp));
		message = "This user's importance is " + parseInt(msg.Importance[j].user_imp);
		
		if(timestamp[j] != null) {
			message += " and you have last replied since " + timestamp[j];
		}
		else {
			message += " and you haven't replied at all.";
		}

		data.setFormattedValue(j,1,message);
	}
	var options = {
		'title' : 'Users and his importance in your inbox',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
        'legend':'none',
        'vAxis.gridlineColor': '#f00',
        'hAxis.gridlineColor': '#f00',
        'curveType': 'function'
	};

	var chart = new google.visualization.LineChart(document.getElementById('slide-image slide-image-right'));
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = "from:";
		value += data.getValue(selectedItem.row, 0);
		console.log(value)
		runSearch(value);
	});	

}

function drawPieChart1(msg) {
	console.log("draw pie chart : " + msg);
	var data = new google.visualization.DataTable();

	//msg = eval('(' + msg + ')');
	data.addColumn('string', 'Users');
	data.addColumn('number', 'Importance');

	for ( var j = 0; j < msg.IndividualContactImportance.length; j++) {
		var my_email=msg.IndividualContactImportance[j].email_id;
		var ind=my_email.indexOf("@");
		var email_slice=my_email.slice(0,ind);
		data.addRows([
		              [email_slice,
					   parseInt(msg.IndividualContactImportance[j].importance) 
					  ],
					]);
	}
//	console.log(data)
	var options = {
		'title' : 'Individual Users importance',
		'is3D' : 'true',
		'width' : '100%',
		'height' : '100%',
		'backgroundColor' : 'transparent',
		'chartArea': {left:20,top:0,width:"70%",height:"75%"}
	};

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.PieChart(document.getElementById('slide-image slide-image-right-pie1'));
	chart.draw(data, options);
}
