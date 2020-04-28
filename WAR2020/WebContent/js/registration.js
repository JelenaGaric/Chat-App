$(document).ready(()=> {
	
	$('#registrationForm').submit((event)=>{
		event.preventDefault();
		
		let username=$('#username').val();
		let password=$('#password').val();
		let repeatPassword=$('#repeatPassword').val();

		if(password != repeatPassword){
			alert("Passwords do not match!");
			return;
		}
		
		if(username == ""){
			alert("Username is empty!");
			return;
		}
	
		$.post({
			url: 'rest/chat/users/register',
			data: JSON.stringify({username, password}),
			contentType: 'application/json',
			success: function() {
				window.location='./login.html';	
			},
			error: function(response) {
				console.log(JSON.stringify(response));
				alert(response.responseText);
			}
		});
		
	});
		
		
	
});
