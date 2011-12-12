var R;	        
var importances = 4;
var timelines = 5;
var importanceList = ["Not Important", "Less Important", "Important", "Very Important"];
var timestampList = [];
var dots = {};
var vars = [], hash;
var counter=0;

var responseMsg = {};

window.onload = function() {
	R = Raphael("slide-image slide-image-right-mainstat", 550, 250);
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
			responseMsg = eval(' (' + msg + ') '); 
			console.log(responseMsg);
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
    var length = 10;
    var height = 10;
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
    	x = originX + (responseMsg.InboxAgeData[i].TimeStampValue * length);
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
            	console.log("<b>Subject: </b>" + responseMsg.InboxAgeData[state].Subject);
            	document.getElementById("tooltip").innerHTML = '<b>Subject: </b>' + responseMsg.InboxAgeData[state].Subject;
            };
        })(dots[state], state);
    }
    
	var t = R.text(300, originY + 10, vars['range']);
	t.attr({"font-size": 16, "font-family": "Arial, Helvetica, sans-serif"});
}