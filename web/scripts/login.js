	img1 = new Image(16, 16);
img1.src = "./resources/images/spinner.gif";

img2 = new Image(220, 19);
img2.src = "./resources/images/ajax-loader.gif";

$(document).ready(
	function() {
		$("#login_link").click(function() {
			$('#login_form').modal();
		});

		$("#status > form").submit(
			function(event) {
				event.preventDefault(); 
				$('#submit').hide();
				$('#ajax_loading').show();
				
				var loginUsername = $("#email").val();
				var loginPassword = $("#password").val();

				$.ajax({
						type : "POST",
						url : "/GmailViz/login",
						data : {
							loginUsername : loginUsername,
							loginPassword : loginPassword
						},
						success : function(msg) {
							console.log(msg);
							msg = eval(' (' + msg + ') ');
							
							if(msg.success == 'true') {
								window.location = location.protocol + '//' + location.host + "/GmailViz/Index.jsp";								
							}
							else if(msg.success == 'false') {
								$('#ajax_loading').hide();
								$('#submit').show();
								$('#message').css({color:"red"});
								$('#message').css({"text-align":"center"});
								document.getElementById("message").innerHTML = "Invalid credentials";
								$("#email").val(''); 
								$("#password").val('');
								$("#email").focus();	
							}														
						}
				});
		});
});