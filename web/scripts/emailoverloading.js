var SEARCH_PREFIX = "#search/";
var APPS_PREFIX = "https://mail.google.com/a/";
var GMAIL_PREFIX = "http://mail.google.com/mail/";

var R;
var importances = 4;
var timelines = 5;
var importanceList = [ "Delete Now", "Delete Later", "Not Important",
		"Less Important", "Least Imp" ];
var dots = {};
var vars = [], hash;
var counter = 0;
var responseMsg = {};
var region = [];
var path = "";
window.onload = function() {
	R = Raphael("paper", 600, 600);

	$.ajax({
		type : 'POST',
		url : "/GmailViz/overload",
		success : function(msg) {
			responseMsg = eval(' (' + msg + ') ');
			drawChart();
		},
		failure : function(msg) {
			console.log('failed');
		},
		callback : function(msg) {
			console.log('Callback : ' + msg);
		}
	});
};
function runSearch(query) {
	console.log('inside RunSearch')
	console.log(MAIL_HOST)
	window.open(
       (MAIL_HOST == "gmail.com" ? GMAIL_PREFIX : APPS_PREFIX + MAIL_HOST) + SEARCH_PREFIX + encodeURIComponent(query) + MAIL_HOST);
}

function drawChart() {
	var originX = 100;
	var originY = 450;
	var length = 100;
	var height = 100;
	var textY = originY + (height / 2);
	var textX = (originX) / 2;
	var x, y;
	var timestampList = [ responseMsg.maxtimestamp, responseMsg.mintimestamp ]
	var oY = originY;
	var oX = originX;

	for ( var i = originX; i <= originX + 400; i = i + length) {

		for ( var j = originY; j >= originY - 400; j = j - height) {
			R.path("M" + i + "," + oY + ",L" + i + "," + (oY - 400));
			R.path("M" + oX + "," + j + ",L" + (oX + 400) + "," + (j));
			region[i + "" + j] = R.path(path).attr(attr);
		}
	}
	var min = responseMsg.InboxOverLoading[0].ReceivedateValue;
	var max = responseMsg.InboxOverLoading[0].ReceivedateValue;
	for ( var i = 1; i < responseMsg.InboxOverLoading.length; i++) {
		if (responseMsg.InboxOverLoading[i].ReceivedateValue < min) {
			min = responseMsg.InboxOverLoading[i].ReceivedateValue;
		}
		if (responseMsg.InboxOverLoading[i].ReceivedateValue > max) {
			max = responseMsg.InboxOverLoading[i].ReceivedateValue;
		}
	}

	R.text(originX + 200, originY + 20,'Your email-overload for last one month');
	for ( var j = originY, i = 0; j >= originY - 400 && i <= 5; j = j - height, i++) {
		R.text(originX - 35, j, importanceList[i]);
	}

	for ( var i = 0; i < responseMsg.InboxOverLoading.length; i++) {
		x = originX
				+ ((100 * (responseMsg.InboxOverLoading[i].ReceivedateValue - min)) / (max - min));
		y = originY - ((responseMsg.InboxOverLoading[i].Importance) * length);
		var attr = {
			fill : "#333",
			stroke : "#666",
			"stroke-width" : 1,
			"stroke-linejoin" : "round",
			title : responseMsg.InboxOverLoading[i].Subject
		};

		dots[i] = R.circle(x, y, 5).attr(attr);
	}

	for ( var state in dots) {
		(function(st, state) {
			st[0].style.cursor = "hand";
			st[0].onmousedown = function() {
				$('#tooltip').css({visibility:'visible'});
				document.getElementById("tooltip").innerHTML = '<h4><i>SenderName: </i></h4>'
						+ responseMsg.InboxOverLoading[state].Sendername
						+ '<br><h4><i>Subject: </i></h4>'
						+ responseMsg.InboxOverLoading[state].Subject;
				var value = 'from:'+responseMsg.InboxOverLoading[state].Sendername;
				$('#showmail').css({visibility:'visible'});
				$('#showmail').bind('click', function(){
					window.open(
						       (MAIL_HOST == "gmail.com" ? GMAIL_PREFIX : APPS_PREFIX + MAIL_HOST) + SEARCH_PREFIX + encodeURIComponent(value) + MAIL_HOST);	       
				});
			};
		})(dots[state], state);
	}
}