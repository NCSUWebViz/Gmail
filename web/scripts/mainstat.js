var SEARCH_PREFIX = "#search/";
var APPS_PREFIX = "https://mail.google.com/a/";
var GMAIL_PREFIX = "http://mail.google.com/mail/";

var R;	        
var importances = 4;
var timelines = 5;
var importanceList = ["Not Important", "Less Important", "Important", "Very Important"];
var timestampList = [];
var dots = {};
var vars = [], hash;
var counter=0;
var max;
var min;

var responseMsg = {};
function runSearch(query) {
	console.log('inside RunSearch')
	console.log(MAIL_HOST)
	window.open(
       (MAIL_HOST == "gmail.com" ? GMAIL_PREFIX : APPS_PREFIX + MAIL_HOST) + SEARCH_PREFIX + encodeURIComponent(query) + MAIL_HOST);
}

window.onload = function() {
	
	getUrlVars();
	for(var args in vars) {
		vars[args] = vars[args].replace(/%20/g," ");
	}
	
	$.ajax({
		type : 'POST',
		url : "/GmailViz/getAgeVsCountIntervals",
		data : {
			importance : vars['importance'],
			from : vars['from'],
			to : vars['to']
		},
		success : function(msg) {
			R = Raphael("paper", 600, 600);
			responseMsg = eval(' (' + msg + ') '); 
			if(responseMsg.InboxAgeData.length > 0) {
				min = responseMsg.InboxAgeData[0].TimeStampValue;
				max = responseMsg.InboxAgeData[0].TimeStampValue;				
				for(var i=1; i < responseMsg.InboxAgeData.length; i++) {
					if(responseMsg.InboxAgeData[i].TimeStampValue < min) {
						min = responseMsg.InboxAgeData[i].TimeStampValue;
					}
					if(responseMsg.InboxAgeData[i].TimeStampValue > max) {
						max = responseMsg.InboxAgeData[i].TimeStampValue;
					}
				}
				drawChart();
			}
			else {
				$("#paper").css({color:"red"});
				$("#paper").text('Sorry! No data available for date '+vars['from'].replace(/(\s*$)/g, "")+' and '+vars['from'].replace(/(\s*$)/g, "")+' Please choose other block(s)!!!');
			}
		},
		failure : function(msg) {
			console.log('failed');
		},
		callback : function(msg) {
			console.log('Callback : ' + msg);
		}
	});
};

function getUrlVars() {    
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
}

function drawChart() {
    var originX = 100;
    var originY = 550;
    var length = 50;
    var height = 50;
    var textY = originY + (height/2);			        
    var textX = (originX)/2;
    var x, y;
    
    for(var i = originX; i <= originX + 500; i = i + length) {
    	R.path("M" + i + "," + originY + ",L" + i + "," + (originY-500));
    }
    
	for(var j = originY; j >= originY - 500; j = j - height) {
		R.path("M" + originX + "," + j + ",L" + (originX + 500) + "," + (j));
	}
	
    for(var i=0; i < responseMsg.InboxAgeData.length; i++) {  
    	if(min-max == 0) {
    		x = originX;
    	}
    	else {
    		x = originX + ((100 * (responseMsg.InboxAgeData[i].TimeStampValue - min))/(max-min));
    	}
    	y = originY - (responseMsg.InboxAgeData[i].Importance* height);
        var attr = {
	            fill: "#333",
	            stroke: "#666",
	            "stroke-width": 1,
	            "stroke-linejoin": "round",
	            title: responseMsg.InboxAgeData[i].Subject
	    };        
    	dots[i] = R.circle(x,y,5).attr(attr);
    }
    
    for(var state in dots) {
    	(function (st, state) {
            st[0].style.cursor = "hand";
            st[0].onmousedown = function () {
            	$('#tooltip').css({visibility:'visible'});
            	document.getElementById("tooltip").innerHTML = '<h4>SenderName: </h4>' + responseMsg.InboxAgeData[state].Sendername+ 
        		'<br><h4>Subject: </h4>' + responseMsg.InboxAgeData[state].Subject;
            	var value = 'subject: '+responseMsg.InboxAgeData[state].Subject;
            	$('#showmail').css({visibility:'visible'});
				$('#showmail').bind('click', function(){
					window.open(
						       (MAIL_HOST == "gmail.com" ? GMAIL_PREFIX : APPS_PREFIX + MAIL_HOST) + SEARCH_PREFIX + encodeURIComponent(value) + MAIL_HOST);	       
				});
            };
        })(dots[state], state);
    }
    
	var t = R.text(300, originY + 10, vars['range']);
	t.attr({"font-size": 16, "font-family": "Arial, Helvetica, sans-serif"});
}
