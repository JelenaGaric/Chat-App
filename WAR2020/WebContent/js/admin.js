var loggedIn = JSON.parse(window.localStorage.getItem('loggedIn'));
$(document).ready(()=> {
	
	$("#logoutBtn").click(function(){
		
		$.ajax({
			type:"DELETE",
			url:"rest/chat/users/loggedIn/"+loggedIn.username,
			contentType:"application/json",
			complete: function(data){
				console.log("sent logout to the server");
				window.location = "login.html"
			}
		});
	});
	
	$.get({
		url:"rest/chat/users/loggedIn",
		contentType:"application/json",
		complete: function(data){
			var loggedInUsers =data.responseJSON; 
			
			for(let u of loggedInUsers){
				var li = document.createElement("li");
				li.innerHTML = u.username;
				document.getElementById("loggedInUsers").appendChild(li);
			}
		}
	});
		
		
	$.get({
		url:"rest/chat/users/registered",
		contentType:"application/json",
		complete: function(data){
			var registeredUsers = data.responseJSON; 
			
			for(let u of registeredUsers){
				var li = document.createElement("li");
				li.innerHTML = u.username;
				document.getElementById("registeredUsers").appendChild(li);
			}
		}
	});
});
