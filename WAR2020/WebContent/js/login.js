$(document).ready(()=> {
	
	$('#loginForm').submit((event)=>{
		event.preventDefault();
		
		let username=$('#username').val();
		let password=$('#password').val();
	
		$.post({
			url: 'rest/chat/users/login',
			data: JSON.stringify({username, password}),
			contentType: 'application/json',
			success: function(user) {
				window.localStorage.setItem('loggedIn', JSON.stringify(user));
				
				if(user.role == "ADMIN")
					window.location='./adminHomePage.html';
				else if(user.role == "USER")
					window.location='./index.html';	
			},
			error: function() {
				alert('Pogresno korisnicko ime ili sifra!');
			}
		});
		
	});
		
		
	
});
