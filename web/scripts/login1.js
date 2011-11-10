// Preload Images
img1 = new Image(16, 16);
img1.src = "./resources/images/spinner.gif";

img2 = new Image(220, 19);
img2.src = "./resources/images/ajax-loader.gif";

// When DOM is ready
$(document).ready(function() {
					// Launch MODAL BOX if the Login Link is clicked
					$("#login_link").click(function() {
						$('#login_form').modal();
					});
					// When the form is submitted
					$("#status > form").submit(function() {
										// Hide 'Submit' Button
										$('#submit').hide();
										// Show Gif Spinning Rotator
										$('#ajax_loading').show();
										// 'this' refers to the current submitted form
										var loginUsername=$("#email").val();
										var loginPassword = $("#password").val();
//										var str = $(this).serialize();
//										console.log(str)
//										 -- Start AJAX Call --
										$.ajax({
													type : "POST",
													url : "/GmailViz/home", 
													data : {loginUsername:loginUsername, loginPassword:loginPassword},
													success : function(msg) {
														window.location = "http://localhost:8080/GmailViz/imp_contact.jsp"
													}
												});
									}); // end submit event

				});

function go_to_private_page() {
	window.location = 'private.php'; // Members Area
}