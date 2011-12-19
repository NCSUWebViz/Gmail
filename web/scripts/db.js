img1 = new Image(16, 16);
img1.src = "./resources/images/spinner.gif";

img2 = new Image(220, 19);
img2.src = "./resources/images/ajax-loader.gif";

$(document).ready(
	function() {
		$('#login_form').modal();

		$("#status > form").submit(
			function(event) {
				event.preventDefault(); 
				$('#submit').hide();
				$('#ajax_loading').show();
				
				var dbUsername = $("#email").val();
				var dbPassword = $("#password").val();
				var dbJDBC = $("#jdbc").val();
				
				$.ajax({
						type : "POST",
						url : "/GmailViz/db",
						data : {
							dbJDBC : dbJDBC,
							dbUsername : dbUsername,
							dbPassword : dbPassword
						},
						success : function(msg) {
							console.log(msg);
							msg = eval(' (' + msg + ') ');
							
							if(msg.success == 'true') {
								window.location = location.protocol + '//' + location.host + "/GmailViz/Index.jsp";								
							}										
						}
				});
		});
});