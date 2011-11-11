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
$('#submit').click(function(){
	keywords = []; 
		$('li').each(function(index) {
			keywords[index] = $(this).text();
		});
		console.log(keywords)
		$.ajax({
			type : "POST",
			url : "/GmailViz/keyword",
			data : {
				keywords: keywords
			},
			success : function(msg) {
				alert('Success');
			},
			error: function(msg){
				alert('Failed');
			}
		});
});
$('#clear').click(function(){
	console.log('clear cleicked')
	$('#keyword_list').html('');
	$('#clear').hide();
	$('#submit').hide();
	$('#input_listName').val('');
	$('#input_listName').focus()
});
