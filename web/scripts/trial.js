var temp = "{\"folderSizes\": [{\"folderName\":\"INBOX\",\"folderSize\":\"1659\"},{\"folderName\":\"NCSU\",\"folderSize\":\"57\"},{\"folderName\":\"Personal\",\"folderSize\":\"14\"},{\"folderName\":\"Receipts\",\"folderSize\":\"4\"},{\"folderName\":\"Travel\",\"folderSize\":\"0\"},{\"folderName\":\"Work\",\"folderSize\":\"0\"},{\"folderName\":\"All Mail\",\"folderSize\":\"2688\"},{\"folderName\":\"Drafts\",\"folderSize\":\"0\"},{\"folderName\":\"Important\",\"folderSize\":\"351\"},{\"folderName\":\"Sent Mail\",\"folderSize\":\"896\"},{\"folderName\":\"Spam\",\"folderSize\":\"6\"},{\"folderName\":\"Starred\",\"folderSize\":\"1\"},{\"folderName\":\"Trash\",\"folderSize\":\"0\"}],\"success\": \"true\"}";
var myObject = eval('(' + temp + ')');
google.load("visualization", "1", {packages:["corechart"]});
google.setOnLoadCallback(drawChart);
function drawChart() {
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Folder Name');
  data.addColumn('number', 'Folder Size');
  data.addRows(myObject.folderSizes.length);
  for(i=0;i<myObject.folderSizes.length;i++){
	  data.setValue(i, 0, myObject.folderSizes[i].folderName);
	  data.setValue(i, 1, parseInt(myObject.folderSizes[i].folderSize));
  }
  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
  chart.draw(data, {width: 800, height: 640, title: 'Your MailBox Aggregates',
                    hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}
                   });
}