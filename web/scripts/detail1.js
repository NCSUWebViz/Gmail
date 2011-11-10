function getImportance(type){
	console.log(type)
	$.ajax({
		type:'GET',
		url:"/GmailViz/userbyyear",
		success: function(msg){
			alert('Success');
		},
		error: function(msg){
			alert('Failed');
		}
	});
}