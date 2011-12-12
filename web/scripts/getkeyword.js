//var msg ={"keywords" : [{"keyword":"OOLS","importance":"0.0"},{"keyword":"Gmail","importance":"7.0"},{"keyword":"Viz","importance":"6.0"}]}



//Load the Visualization API and the piechart package.
google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
});
function drawChart1(msg) {
	// console.log('You choose Bar or Column or ')
	var data = new google.visualization.DataTable();
	msg = eval(' (' + msg + ') ');
	data.addColumn('string', 'keyword');
	data.addColumn('number', 'Importance');
	data.addRows(msg.Keywords.length);
	for ( var j = 0; j < msg.Keywords.length; j++) {
		// console.log(msg.IndividualContactImportance[j].email_id + " " +
		// parseInt(msg.IndividualContactImportance[j].importance));
		data.setValue(j, 0, msg.Keywords[j].keyword);
		data.setValue(j, 1, parseInt(msg.Keywords[j].importance));
	}
	var options = {
		'title' : 'Keywords Importance',
		is3D : true,
		'width' : '100%',
		'height' : '100%',
		'backgroundColor':'transparent',
	};

	// Instantiate and draw our chart, passing in some options.
	
		// console.log(type)
		var chart = new google.visualization.ColumnChart(document
				.getElementById('keyword_chart'));
	chart.draw(data, options);
}
