var socket;
var loggedIn = JSON.parse(window.localStorage.getItem('loggedIn'));
var loggedInUsers = [];
var previousLoggedInUsers = [];
var host="ws://localhost:8080/WAR2020/ws/"+loggedIn.id;

$(document).ready(function(){
	
	generateChats()
	
	console.log(window.localStorage.getItem('loggedIn'));
	

	try{
		socket = new WebSocket(host);
		console.log("connect: Socket Status: "+socket.readyState);
		socket.onopen = function(){
			console.log("onopen: Socket Status: "+socket.readyState + " (open)");	
			
			}
		socket.onmessage = function(msg){
			console.log("onmessage: Recieved: "+ msg.data);	
			//osvjezenje liste ulogovanih
			if(msg.data.startsWith("[System]") && (msg.data.includes("logged in") || msg.data.includes("logged out"))){
				console.log("refresh");
				generateChats();
			}
			if(msg.data.startsWith("[System]") && msg.data.includes("logged out")){
				console.log("refresh");
				generateChats();
			}
		}
		socket.onclose = function(){
			socket = null;
			console.log("logout");
			}
		} catch (exception) {
			console.log("Error " + exception);
		}
});


function generateChats(){

	$.get({
		url:"rest/chat/users/loggedIn",
		contentType:"application/json",
		complete: function(data){
			for(let us of loggedInUsers){
				previousLoggedInUsers.push(us.id);
			}
			//previousLoggedInUsers = loggedInUsers;
			loggedInUsers = data.responseJSON; 
			document.getElementById("numActive").innerHTML = "Active: " + loggedInUsers.length;
			
			for(let u of loggedInUsers){
				
				console.log(u.username);
				if(u.id == loggedIn.id){
					continue;
				}
				if(previousLoggedInUsers.includes(u.id)){
					continue;
				}

				var chatList = document.createElement("DIV");
				chatList.id = u.id;
				chatList.classList.add("chat_list");
				
				var chatPeople = document.createElement("DIV");
				chatList.classList.add("chat_people");

				var chatImg = document.createElement("DIV");
				chatImg.classList.add("chat_img");
				var img = document.createElement("IMG");
				img.src = "https://ptetutorials.com/images/user-profile.png";
				img.alt = "user";

				chatImg.appendChild(img);
				
				var chatIb = document.createElement("DIV");
				chatIb.classList.add("chat_ib");
				
				var heading5 = document.createElement("H5");
				heading5.innerHTML = u.username ;
				var chatDate = document.createElement("SPAN");
				chatDate.classList.add("chat_date");
				chatDate.innerHTML = "Dec 25";
				heading5.appendChild(chatDate);
				
				var p = document.createElement("P");
				p.innerHTML = "Active"
					
				
				chatIb.appendChild(heading5);
				chatIb.appendChild(p);

				chatPeople.appendChild(chatImg);
				chatPeople.appendChild(chatIb);
				
				chatList.appendChild(chatPeople);
				
				chatList.onclick = function() {
					
					var activeChats = document.getElementsByClassName("active_chat");
					for(let element of activeChats){
						element.classList.remove("active_chat");
					}

					document.getElementById(u.id).classList.add("active_chat");
					
	                var msgHistory = document.getElementById("msgHistory");
	                msgHistory.innerHTML = "";

					makePostBtnForOneUser(u.id);
				}
				
				var inboxChat= document.getElementById("inboxChat");
				inboxChat.appendChild(chatList);

			}
			

			makePostBtnForAll();
			
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
			

		}
	});
	
}

function showChatroom() {
    
    var activeChats = document.getElementsByClassName("active_chat");
	for(let element of activeChats){
		element.classList.remove("active_chat");
	}

	document.getElementById("chatroomChatList").classList.add("active_chat");
	
	var msgHistory = document.getElementById("msgHistory");
    msgHistory.innerHTML = "";
    msgHistory.innerHTML = "  <div class=\"incoming_msg\">\r\n              <div class=\"incoming_msg_img\"> <img src=\"https:\/\/ptetutorials.com\/images\/user-profile.png\" alt=\"sunil\"> <\/div>\r\n              <div class=\"received_msg\">\r\n                <div class=\"received_withd_msg\">\r\n                  <p>Test which is a new approach to have all\r\n                    solutions<\/p>\r\n                  <span class=\"time_date\"> 11:01 AM    |    June 9<\/span><\/div>\r\n              <\/div>\r\n            <\/div>\r\n            <div class=\"outgoing_msg\">\r\n              <div class=\"sent_msg\">\r\n                <p>Test which is a new approach to have all\r\n                  solutions<\/p>\r\n                <span class=\"time_date\"> 11:01 AM    |    June 9<\/span> <\/div>\r\n            <\/div>\r\n            <div class=\"incoming_msg\">\r\n              <div class=\"incoming_msg_img\"> <img src=\"https:\/\/ptetutorials.com\/images\/user-profile.png\" alt=\"sunil\"> <\/div>\r\n              <div class=\"received_msg\">\r\n                <div class=\"received_withd_msg\">\r\n                  <p>Test, which is a new approach to have<\/p>\r\n                  <span class=\"time_date\"> 11:01 AM    |    Yesterday<\/span><\/div>\r\n              <\/div>\r\n            <\/div>\r\n            <div class=\"outgoing_msg\">\r\n              <div class=\"sent_msg\">\r\n                <p>Apollo University, Delhi, India Test<\/p>\r\n                <span class=\"time_date\"> 11:01 AM    |    Today<\/span> <\/div>\r\n            <\/div>\r\n            <div class=\"incoming_msg\">\r\n              <div class=\"incoming_msg_img\"> <img src=\"https:\/\/ptetutorials.com\/images\/user-profile.png\" alt=\"sunil\"> <\/div>\r\n              <div class=\"received_msg\">\r\n                <div class=\"received_withd_msg\">\r\n                  <p>We work directly with our designers and suppliers,\r\n                    and sell direct to you, which means quality, exclusive\r\n                    products, at a price anyone can afford.<\/p>\r\n                  <span class=\"time_date\"> 11:01 AM    |    Today<\/span><\/div>\r\n              <\/div>\r\n            <\/div>"; 
    makePostBtnForAll();
}

function makePostBtnForAll(){
  $("#btnPost").off();
  $("#btnPost").click(function(){
		//var msg = document.getElementById("msgTextBox").value;
		let text = document.getElementById("msgTextBox").value;
		let recieverId = "all";
		let senderId = loggedIn.id;
		$.post({
			url:"rest/chat/messages/all",
			data: JSON.stringify({recieverId,senderId,text}),
			contentType:"application/json",
			dataType:"json",
			complete:function(data){
				console.log("sent message to the server");
			}
		});
	}); 
}

function makePostBtnForOneUser(userId){
	  $("#btnPost").off();
	  $("#btnPost").click(function(){
			let text = document.getElementById("msgTextBox").value;
			let recieverId = userId;
			let senderId = loggedIn.id;
			$.post({
				url:"rest/chat/messages/toUser",
				data: JSON.stringify({recieverId,senderId,text}),
				contentType:"application/json",
				dataType:"json",
				complete:function(data){
					console.log("Sent a message to the user with id: " + userId);
				}
			});
		}); 
	}