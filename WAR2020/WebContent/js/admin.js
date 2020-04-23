$(document).ready(()=> {
	
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
