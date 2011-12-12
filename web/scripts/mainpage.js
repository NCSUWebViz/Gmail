$(document).ready(
	function() {
		$("#analyze_link").click(function() {
			$.ajax({
				type : "POST",
				url : "/GmailViz/home",
				success : function(msg) {
					console.log(msg);	
					msg = eval(' (' + msg + ') ');
					
					if(msg.success == 'true') {
						console.log('success');
					}
					else if(msg.success == 'false') {
						console.log('failure');
					}
				}
			});
		});
});