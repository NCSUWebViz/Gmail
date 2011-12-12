$('#input_listName').bind('keypress', function(e){
	if (e.keyCode == 13){
        // add value of list item to input field
		$('#keyword_list').append('<li>'+$('#input_listName').val());
		$('#clear').show();
		$('#submit').show();
		$('#input_listName').val('');
		$('#input_listName').focus()
    }

});
var keywords = [];
$(document).ready(function(){
	$.ajax({
		type : 'POST',
		url : "/GmailViz/getKeywords",
		success : function(msg) {
			console.log('keyword:' + msg)
			drawChart1(msg);
		}
	});
});
$('#submit').click(function(){
	$('li').each(function(index) {
		keywords = keywords + $(this).text() + ",";
	});
	console.log(keywords)
	$.ajax({
		type : "POST",
		url : "/GmailViz/keywords",
		data : {
			keywords: keywords
		},
		success : function(msg) {
			console.log('Success : ' + msg);
		},
		error: function(msg){
			console.log('Failure : ' + msg);
		}
	});
});
$('#clear').click(function(){
	$('#keyword_list').html('');
	$('#clear').hide();
	$('#submit').hide();
	$('#input_listName').val('');
	$('#input_listName').focus()
});