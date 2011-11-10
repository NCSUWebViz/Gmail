var chart;

$(document).ready(
		function() {
			console.log('I')
			
			$.ajax({
				type:'GET',
				url:"/GmailViz/impcontact",
				success: function(msg){
//					if(type=='PieChart'){
						getImportance(msg);
						getImportance2(msg);
//					}
//					else if(type=='BarChart'||type=='LineChart'||type=='ColumnChart'){
//						drawChart1(msg,type);
//						drawChart2(msg,type);	
//					}
				},
				error: function(msg){
					alert('Failed');
				}
			});	
		});
function getImportance(msg){
	var data =new Array();
	msg = eval('(' + msg + ')');
    for (var j = 0; j < msg.IndividualContactImportance.length; j++) {
    	data[j] = Array(msg.IndividualContactImportance[j].email_id,parseInt(msg.IndividualContactImportance[j].importance));
    }
	chart = new Highcharts.Chart({
		chart : {
			renderTo : 'container',
			plotBackgroundColor : null,
			plotBorderWidth : null,
			plotShadow : false
		},
		title : {
			text : 'Friend Importance'
		},
		tooltip : {
			formatter : function() {
				return '<b>' + this.point.name + '</b>: '
						+ this.percentage + ' %';
			}
		},
		plotOptions : {
			pie : {
				allowPointSelect : true,
				cursor : 'pointer',
				dataLabels : {
					enabled : false
				},
				showInLegend : true
			}
		},
		series : [ {
			type : 'pie',
			name : 'Browser share',
			data : data
		} ]
	});
}

function getImportance2(msg){
	var data =new Array();
	msg = eval('(' + msg + ')');
    for (var j = 0; j < msg.GroupedContactImportance.length; j++) {
    	data[j] = Array(msg.GroupedContactImportance[j].email_id,parseInt(msg.GroupedContactImportance[j].importance));
    }
	console.log(data)
	chart2 = new Highcharts.Chart({
		chart : {
			renderTo : 'container2',
			plotBackgroundColor : null,
			plotBorderWidth : null,
			plotShadow : false
		},
		title : {
			text : 'Group Importance'
		},
		tooltip : {
			formatter : function() {
				return '<b>' + this.point.name + '</b>: '
						+ this.percentage + ' %';
			}
		},
		plotOptions : {
			pie : {
				allowPointSelect : true,
				cursor : 'pointer',
				dataLabels : {
					enabled : false
				},
				showInLegend : true
			}
		},
		series : [ {
			type : 'pie',
			name : 'Browser share',
			data : data
		} ]
	});
}