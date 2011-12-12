var popupstatus = 0;
var msg;
function loadPopUp(msg) {
	console.log('popupStatus:' + popupstatus +' msg:'+msg)
	if (popupstatus == 0) {
		console.log('popupstatus:' + popupstatus)
		$("#backgroundPopup").css({
			"opacity" : "0.9"
		});
		$("#backgroundPopup").fadeIn("slow");
		$("#popupContact").fadeIn("slow");
		drawChart(msg);
		popupstatus = 1;
	}
}

function centerPopup() {
	// request data for centering
	var windowWidth = document.documentElement.clientWidth;
	var windowHeight = document.documentElement.clientHeight;
	var popupHeight = $("#popupContact").height();
	var popupWidth = $("#popupContact").width();
	// centering
	console.log('popupstatus:' + popupHeight + popupHeight)
	$("#popupContact").css({
		"position" : "absolute",
		"top" : windowHeight / 2 - popupHeight / 2,
		"left" : windowWidth / 2 - popupWidth / 2
	});
	// only need force for IE6

	$("#backgroundPopup").css({
		"height" : windowHeight
	});

}
google.load('visualization', '1.0', {'packages':['corechart']});
function drawChart(msg) {
	console.log('msg:' + msg)
	msg = eval('(' + msg + ')');
	console.log(msg.UserYearlyData[0].Year)
//	console.log(msg.UserYearlyData.length);
//	console.log(msg.UserYearlyData[0].Stat[0].Count);
//	
//	for(var i=0; i<msg.UserYearlyData.length; i++) {
//		console.log(msg.UserYearlyData[i].Type);
//		msg.UserYearlyData[i].Stat[0].Year;
//	}

	var data = new google.visualization.DataTable();
    data.addColumn('string', 'Year');
    data.addColumn('number', 'Sent');
    data.addColumn('number', 'Received')
    
    data.addRows(msg.UserYearlyData.length);
    for(i=0;i<msg.UserYearlyData.length;i++){
    	data.setValue(i, 0, msg.UserYearlyData[i].Year);
    	data.setValue(i, 1, parseInt(msg.UserYearlyData[i].Sent));
    	data.setValue(i, 2, parseInt(msg.UserYearlyData[i].Received));
    }
//    data.setValue(0, 0, '2011');
//    data.setValue(0, 1, 6);
//    data.setValue(0, 2, 9);
    
//    data.setValue(1, 0, '2005');
//    data.setValue(1, 1, 1170);
//    
//    data.setValue(2, 0, '2006');
//    data.setValue(2, 1, 660);
//    
//    data.setValue(3, 0, '2007');
//    data.setValue(3, 1, 1030);
    

    var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
    chart.draw(data, {width: 700, height: 440, title: 'Mails Sent/Received Per Year', isStacked:true,
                      hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}
                     });
    google.visualization.events.addListener(chart, 'select', function(){
//        data.addRows(2);
//        data.setValue(0, 0, 'Oct');
//        data.setValue(0, 1, 2);
//        data.setValue(0,2,5);
//        
//        data.setValue(1, 0, 'Nov');
//        data.setValue(1, 1, 4);
//        data.setValue(1,2,4);
//        
//        chart.draw(data, {width: 700, height: 440, title: 'Mails Sent/Received Per Month',
//            hAxis: {title: 'Month', titleTextStyle: {color: 'red'}}
//           });
    	var selectedItem = chart.getSelection()[0];
		var value = data.getValue(selectedItem.row, 0);
		console.log('The user selected year' + value);
    	$.ajax({
			type : 'GET',
			data : {year : value},
			url : "/GmailViz/userbymonth",
			success : function(msg) {
				drawMonthlyChart(msg);
			},
			error : function(msg) {
				alert('Failed');
			}
		});

    	
    });
    console.log('exit draw chart')
}
function drawMonthlyChart(msg){
//	console.log('msg:' + msg)
	msg = eval('(' + msg + ')');
//	console.log(msg.UserYearlyData[0].Year)

	var data = new google.visualization.DataTable();
    data.addColumn('string', 'Month');
    data.addColumn('number', 'Sent');
    data.addColumn('number', 'Received')
    
    data.addRows(msg.UserMonthlyData.length);
    for(i=0;i<msg.UserMonthlyData.length;i++){
    	data.setValue(i, 0, msg.UserMonthlyData[i].Month);
    	data.setValue(i, 1, parseInt(msg.UserMonthlyData[i].Sent));
    	data.setValue(i, 2, parseInt(msg.UserMonthlyData[i].Received));
    }
    var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
    chart.draw(data, {width: 700, height: 440, title: 'Mails Sent/Received Per Month', isStacked:true,
                      hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}
                     });
}
function disablePopup() {
	// disables popup only if it is enabled
	if (popupstatus == 1) {
		$("#backgroundPopup").fadeOut("slow");
		$("#popupContact").fadeOut("slow");
		popupstatus = 0;
	}
}
