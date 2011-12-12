//CONTROLLING EVENTS IN jQuery
$(document).ready(function() {

	// Click out event!
	$("#backgroundPopup").click(function() {
		disablePopup();
		getImportance('PieChart'); //default view is always a Pie Chart
	});
});
function getImportance(type) {
	console.log(type)

	$.ajax({
		type : 'GET',
		url : "/GmailViz/impcontact",
		success : function(msg) {
				drawPieChart1(msg); //individual importance
			if (type == 'BarChart' || type == 'LineChart'
					|| type == 'ColumnChart') {
				drawChart1(msg, type); //individual importance
			}
		},
		error : function(msg) {
			alert('Failed');
		}
	});
}
// Load the Visualization API and the piechart package.
google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});

// Set a callback to run when the Google Visualization API is loaded.

function drawPieChart1(msg) {
	console.log('You choose Pie')
	var data = new google.visualization.DataTable();

	msg = eval('(' + msg + ')');
	console.log(msg);
	data.addColumn('string', 'Users');
	data.addColumn('number', 'Importance');

	for ( var j = 0; j < msg.IndividualContactImportance.length; j++) {
		my_email=msg.IndividualContactImportance[j].email_id;
		ind=my_email.indexOf("@");
		email_slice=my_email.slice(0,ind);
		data
				.addRows([
						[
								email_slice,
								parseInt(msg.IndividualContactImportance[j].importance) ], ]);
	}
	console.log(data)
	var options = {
		'title' : 'Individual Users importance',
		'is3D' : 'true',
		'width' : '100%',
		'height' : '100%',
		'backgroundColor' : 'transparent',
		'legendTextStyle' : {fontName: 'Verdana', fontSize: 10},
		'chartArea': {left:20,top:0,width:"70%",height:"75%"}
	};

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.PieChart(document
			.getElementById('chart_div1'));
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = data.getValue(selectedItem.row, 0);
		$.ajax({
			type : 'GET',
			data : {
				emailid : value
			},
			url : "/GmailViz/userbyyear",
			success : function(msg) {
				drawYearChart(msg);
			},
			error : function(msg) {
				alert('Failed');
			}
		});

	});
	console.log('Youe exit pIE')
}
function drawYearChart(msg) {
	loadPopUp(msg);
	centerPopup();
}
function drawPieChart2(msg) {
	// console.log('You choose Pie')
	var data = new google.visualization.DataTable();
	console.log(msg);
	msg = eval('(' + msg + ')');

	data.addColumn('string', 'Users');
	data.addColumn('number', 'Importance');

	for ( var j = 0; j < msg.GroupedContactImportance.length; j++) {
		// console.log(msg.GroupedContactImportance[j].email_id + " " +
		// parseInt(msg.GroupedContactImportance[j].importance));
		my_email=msg.GroupedContactImportance[j].email_id;
		ind=my_email.indexOf("@");
		email_slice=my_email.slice(0,ind);
		data
				.addRows([
						[
								msg.GroupedContactImportance[j].email_id,
								parseInt(msg.GroupedContactImportance[j].importance) ], ]);
	}
	var options = {
			'title' : 'Individual Users importance',
			'is3D' : 'true',
			'width' : '100%',
			'height' : '100%',
			'backgroundColor' : 'transparent',
			'legendTextStyle' : {fontName: 'Verdana', fontSize: 10},
			'chartArea': {left:20,top:0,width:"70%",height:"75%"}
	};

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.PieChart(document
			.getElementById('chart_div2'));
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = data.getValue(selectedItem.row, 0);
		console.log('The user selected ' + value);
		console.log('The user selected ' + value)
		$.ajax({
			type : 'GET',
			data : {
				emailid : value
			},
			url : "/GmailViz/userbyyear",
			success : function(msg) {
				drawYearChart(msg);
			},
			error : function(msg) {
				alert('Failed');
			}
		});

	});
	console.log('Youe exit pIE')
}

function drawChart1(msg, type) {
	// console.log('You choose Bar or Column or ')
	var data = new google.visualization.DataTable();
	console.log(msg);
	msg = eval('(' + msg + ')');

	data.addColumn('string', 'Users');
	data.addColumn('number', 'Importance');
	data.addRows(msg.IndividualContactImportance.length);
	for ( var j = 0; j < msg.IndividualContactImportance.length; j++) {
		// console.log(msg.IndividualContactImportance[j].email_id + " " +
		// parseInt(msg.IndividualContactImportance[j].importance));
		data.setValue(j, 0, msg.IndividualContactImportance[j].email_id);
		data.setValue(j, 1,
				parseInt(msg.IndividualContactImportance[j].importance));
	}
	var options = {
		'title' : 'Users Importance',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'vAxis.logScale' : true
	};

	// Instantiate and draw our chart, passing in some options.
	if (type == 'ColumnChart') {
		// console.log(type)
		var chart = new google.visualization.ColumnChart(document
				.getElementById('chart_div1'));
	}
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = data.getValue(selectedItem.row, 0);
		console.log('The user selected ' + value);
		console.log('The user selected ' + value)
		$.ajax({
			type : 'GET',
			data : {
				emailid : value
			},
			url : "/GmailViz/userbyyear",
			success : function(msg) {
				drawYearChart(msg);
			},
			error : function(msg) {
				alert('Failed');
			}
		});

	});
	console.log('Youe exit pIE')
}
function drawChart2(msg, type) {
	// console.log('You choose Bar or Column or ')
	var data = new google.visualization.DataTable();
	console.log(msg);
	msg = eval('(' + msg + ')');

	data.addColumn('string', 'Users');
	data.addColumn('number', 'Importance');
	data.addRows(msg.GroupedContactImportance.length);
	for ( var j = 0; j < msg.GroupedContactImportance.length; j++) {
		// console.log(msg.GroupedContactImportance[j].email_id + " " +
		// parseInt(msg.GroupedContactImportance[j].importance));
		data.setValue(j, 0, msg.GroupedContactImportance[j].email_id);
		data.setValue(j, 1,
				parseInt(msg.GroupedContactImportance[j].importance));
	}
	var options = {
		'title' : 'Grouped Users Importance',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'vAxis.logScale' : true
	};
	if (type == 'ColumnChart') {
		// console.log(type)
		var chart = new google.visualization.ColumnChart(document
				.getElementById('chart_div2'));
	}
	chart.draw(data, options);
	google.visualization.events.addListener(chart, 'select', function() {
		var selectedItem = chart.getSelection()[0];
		var value = data.getValue(selectedItem.row, 0);
		console.log('The user selected ' + value);
		console.log('The user selected ' + value)
		$.ajax({
			type : 'GET',
			data : {
				emailid : value
			},
			url : "/GmailViz/userbyyear",
			success : function(msg) {
				drawYearChart(msg);
			},
			error : function(msg) {
				alert('Failed');
			}
		});

	});
	console.log('Youe exit pIE')
}