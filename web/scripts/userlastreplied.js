var SEARCH_PREFIX = "#search/";
var APPS_PREFIX = "https://mail.google.com/a/";
var GMAIL_PREFIX = "http://mail.google.com/mail/";
var timestamp = [];
$(document).ready(function() {
	$.ajax({
		type : 'GET',
		url : "/GmailViz/userlastreplied",
		success : function(msg) {
			console.log('lastreplied : ' + msg)
			timestampmsg = msg;
			drawChart(msg);
			var msgObj = eval(' (' + msg + ') ');
			$.ajax({
				type : 'GET',
				url : "/GmailViz/ulreimp",
				success: function(msg) {
					if(msgObj.success == 'true') {
						drawChart1(msg);
					}
				}
			});	
		},
		error : function(msg) {
			alert('Failed');
		}
	});
});

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
		console.log(msg.UserLastReplied[j].year + ',' + msg.UserLastReplied[j].month + ',' + msg.UserLastReplied[j].day)
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
			console.log('DATE:::::::::'+date)
			timestamp[j]  = date;
			console.log(j+':'+timestamp[j]);
		}
		else {
			timestamp[j] = null;
		}
		console.log('timestamp : ' + timestamp[j])
	}
	
	var formatter_medium = new google.visualization.DateFormat({formatType: 'medium'});
	formatter_medium.format(data, 1);
	console.log(data)
	
	var options = {
		'title' : 'Users and your last replies',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
		'legend':'none',
		'curveType': 'function'
	};

//	var chart = new google.visualization.LineChart(document.getElementById('chart_div1'));
//	chart.draw(data, options);
//	google.visualization.events.addListener(chart, 'select', function() {
//		var selectedItem = chart.getSelection()[0];
//		var value = "from:";
//		value += data.getValue(selectedItem.row, 0);
//		console.log(value)
//		runSearch(value);
//	});	
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
			console.log('timestamp:'+timestamp[j])
			message += " and you have last replied since " + timestamp[j];
			
		}
		else {
			message += " and you haven't replied at all.";
		}

		data.setFormattedValue(j,1,message);
	}
	var options = {
		'title' : 'Users and his importance in your Inbox',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
        'legend':'none',
    	'curveType': 'function'
	};

	var chart = new google.visualization.LineChart(document.getElementById('chart_div2'));
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = "from:";
		value += data.getValue(selectedItem.row, 0);
		console.log(value)
		runSearch(value);
	});	
}