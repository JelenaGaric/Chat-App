$(document).ready(()=> {
	
	$('#loginForm').submit((event)=>{
		event.preventDefault();
		
		let username=$('#username').val();
		let password=$('#password').val();
		
		console.log('username', username);
		console.log('password', password);
	
		$.post({
			url: 'rest/chat/login',
			data: JSON.stringify({username, password}),
			contentType: 'application/json',
			success: function() {
				window.location='./index.html';	
			},
			error: function() {
				alert('Pogresno korisnicko ime ili sifra!');
			}
		});
		
	});
		
		
	
});
