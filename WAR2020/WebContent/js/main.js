var socket;
var loggedIn = JSON.parse(window.localStorage.getItem('loggedIn'));
var registeredUsers = [];
var loggedInUsers = [];
var previousLoggedInUsers = [];
var host="ws://localhost:8080/WAR2020/ws/"+loggedIn.id;

$(document).ready(function(){
	//popunjavanje liste registrovanih korisnika
	$.get({
		url:"rest/chat/users/registered",
		contentType:"application/json",
		complete: function(data){
			registeredUsers = data.responseJSON; 
			setupSocket();
			
			generateChats();
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
	})
});


function generateChats(){

	$.get({
		url:"rest/chat/users/loggedIn",
		contentType:"application/json",
		complete: function(data){
			previousLoggedInUsers = []
			
			for(let us of loggedInUsers){
				previousLoggedInUsers.push(us.id);
			}

			loggedInUsers = data.responseJSON; 
			document.getElementById("numActive").innerHTML = "Active: " + loggedInUsers.length;

			$('#chatroomChatList').nextAll('div').remove();
			
			for(let u of loggedInUsers){
				
				console.log(u.username);
				if(u.id == loggedIn.id){
					continue;
				}
				if(previousLoggedInUsers.includes(u.id)){
					continue;
				}

				var chatList = document.createElement("DIV");
				chatList.id = "chat"+u.id;
				chatList.classList.add("chat_list");
				console.log("chat id " + chatList.id)
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
				heading5.id = "username"+u.id;
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
					
					removeNotifications(chatList);
					
					chatList.classList.add("active_chat");
					
	                var msgHistory = document.getElementById("msgHistory");
	                msgHistory.innerHTML = "";

					makePostBtnForOneUser(u.id);
				}
				var inboxChat= document.getElementById("inboxChat");
				inboxChat.appendChild(chatList);

			}
			

		}
	});
	
}

function setupSocket(){
	try{
		socket = new WebSocket(host);
		console.log("connect: Socket Status: "+socket.readyState);
		socket.onopen = function(){
			console.log("onopen: Socket Status: "+socket.readyState + " (open)");	
			console.log(window.localStorage.getItem('loggedIn'));
			}
		socket.onmessage = function(msg){
			console.log("onmessage: Recieved: "+ msg.data);	
			var message = JSON.parse(msg.data);
			//osvjezenje liste ulogovanih
			if(message.text.startsWith("[System]") && (message.text.includes("logged in") || message.text.includes("logged out"))){
				console.log("refresh");
				let username = findUser(message.senderId);
				
				var systemMessage = {
					    text: ""
				};
				
				if(message.text.includes("logged in")){
					systemMessage.text = username + " just logged in."
				} else if(message.text.includes("logged out")){
					systemMessage.text = username + " just logged out."
				}
				
				flag = message.senderId;
				
				showIncomingMessage(systemMessage, flag);
				generateChats();
			} else {
				if(message.senderId != loggedIn.id)

					showIncomingMessage(message);
			}
			
		}
		socket.onclose = function(){
			socket = null;
			console.log("logout");
			}
		} catch (exception) {
			console.log("Error " + exception);
		}
		
}


function showChatroom() {
    
    var activeChats = document.getElementsByClassName("active_chat");
	for(let element of activeChats){
		element.classList.remove("active_chat");
	}
	
	var chatroomChatList = document.getElementById("chatroomChatList");
	removeNotifications(chatroomChatList);
	chatroomChatList.classList.add("active_chat");
	
	var msgHistory = document.getElementById("msgHistory");
    msgHistory.innerHTML = "";
    //msgHistory.innerHTML = "  <div class=\"incoming_msg\">\r\n              <div class=\"incoming_msg_img\"> <img src=\"https:\/\/ptetutorials.com\/images\/user-profile.png\" alt=\"sunil\"> <\/div>\r\n              <div class=\"received_msg\">\r\n                <div class=\"received_withd_msg\">\r\n                  <p>Test which is a new approach to have all\r\n                    solutions<\/p>\r\n                  <span class=\"time_date\"> 11:01 AM    |    June 9<\/span><\/div>\r\n              <\/div>\r\n            <\/div>\r\n            <div class=\"outgoing_msg\">\r\n              <div class=\"sent_msg\">\r\n                <p>Test which is a new approach to have all\r\n                  solutions<\/p>\r\n                <span class=\"time_date\"> 11:01 AM    |    June 9<\/span> <\/div>\r\n            <\/div>\r\n            <div class=\"incoming_msg\">\r\n              <div class=\"incoming_msg_img\"> <img src=\"https:\/\/ptetutorials.com\/images\/user-profile.png\" alt=\"sunil\"> <\/div>\r\n              <div class=\"received_msg\">\r\n                <div class=\"received_withd_msg\">\r\n                  <p>Test, which is a new approach to have<\/p>\r\n                  <span class=\"time_date\"> 11:01 AM    |    Yesterday<\/span><\/div>\r\n              <\/div>\r\n            <\/div>\r\n            <div class=\"outgoing_msg\">\r\n              <div class=\"sent_msg\">\r\n                <p>Apollo University, Delhi, India Test<\/p>\r\n                <span class=\"time_date\"> 11:01 AM    |    Today<\/span> <\/div>\r\n            <\/div>\r\n            <div class=\"incoming_msg\">\r\n              <div class=\"incoming_msg_img\"> <img src=\"https:\/\/ptetutorials.com\/images\/user-profile.png\" alt=\"sunil\"> <\/div>\r\n              <div class=\"received_msg\">\r\n                <div class=\"received_withd_msg\">\r\n                  <p>We work directly with our designers and suppliers,\r\n                    and sell direct to you, which means quality, exclusive\r\n                    products, at a price anyone can afford.<\/p>\r\n                  <span class=\"time_date\"> 11:01 AM    |    Today<\/span><\/div>\r\n              <\/div>\r\n            <\/div>"; 
    
    $.get({
		url:"rest/chat/messages/chatroom",
		contentType:"application/json",
		complete: function(data){
			makePostBtnForAll();
			var messages = JSON.parse(data.responseText);
			//generisemo samo 50 poruka
			var messagesNum = 0;
			if(messages.length > 50){
				messagesNum = 50;
			} else {
				messagesNum = messages.length;
			}
			for(var i = 0; i < messagesNum; i++){//let message of messages){
				if(messages[i].senderId == loggedIn.id){
					showSentMessage(messages[i].text);
				} else {
					generateIncomingMessage(messages[i]);
				}
			}
			//msgHistory.innerHTML = "";
		}
    });
    
    
}

function showSentMessage(text){
	var msgHistory = document.getElementById("msgHistory");
	var outgoingMsg = document.createElement("DIV");
	outgoingMsg.classList.add("outgoing_msg");
	var sentMsg = document.createElement("DIV");
	sentMsg.classList.add("sent_msg");
	var textP = document.createElement("P");
	textP.innerHTML = text;
	var span = document.createElement("SPAN");
	span.classList.add("time_date");
	span.innerHTML = "11:01 AM    |    June 9";

	sentMsg.appendChild(textP);
	sentMsg.appendChild(span);
	outgoingMsg.appendChild(sentMsg);
	msgHistory.appendChild(outgoingMsg);
	
	var inboxChat = document.querySelector('.msg_history');
	inboxChat.scrollTop = inboxChat.scrollHeight - inboxChat.clientHeight;

}

function makeNotification(message){
	//zasvijetli chat s novom porukom
	var notifiedChat;
	//ako je privatna poruka
	if(message.recieverId != "all"){
		notifiedChat = document.getElementById("chat"+message.senderId);	
		if(notifiedChat != null && !notifiedChat.classList.contains("active_chat")){
			notifiedChat.classList.add("notified_chat");
			var h = document.getElementById("username"+message.senderId);	
			if(h != null){
				var notification = document.createElement("SPAN");
				notification.id = "notification"+message.senderId;
				notification.classList.add("notification");
				notification.innerHTML = "New messages!";
				console.log(h)
				h.appendChild(notification);
			}

		}
	} else if(message.recieverId == "all"){
		notifiedChat = document.getElementById("chatroomChatList");
		if(notifiedChat != null && !notifiedChat.classList.contains("active_chat")){
			notifiedChat.classList.add("notified_chat");
			var h = document.getElementById("chatroomH");	
			if(h != null){
				var notification = document.createElement("SPAN");
				notification.classList.add("notification");
				notification.id = "notificationChatroom";
				notification.innerHTML = "New messages!";
				console.log(h)
				h.appendChild(notification);
			}

		}
	}
	
}

function removeNotifications(element){
	if(element.classList.contains("notified_chat")){
		element.classList.remove("notified_chat");
		if(element.id == "chatroomChatList"){
			document.getElementById("notificationChatroom").remove();
		} else {
			var idString = element.id;
			document.getElementById("notification"+idString[idString.length-1]).remove();
		}
	}
}

function showIncomingMessage(message, flag){
	console.log("flag " + flag)
	
	makeNotification(message);
	
	generateIncomingMessage(message);

}

function generateIncomingMessage(message){
	var msgHistory = document.getElementById("msgHistory");
	var incomingMsg = document.createElement("DIV");
	incomingMsg.classList.add("incoming_msg");
	
	var incomingMsgImg = document.createElement("DIV");
	incomingMsgImg.classList.add("incoming_msg_img");
	var img = document.createElement("IMG");
	img.src = "https://ptetutorials.com/images/user-profile.png";
	incomingMsgImg.appendChild(img);
	
	var receivedMsg = document.createElement("DIV");
	receivedMsg.classList.add("received_msg");
	
	var receivedWithdMsg = document.createElement("DIV");
	receivedWithdMsg.classList.add("received_withd_msg");
	
	
	var textP = document.createElement("P");
	
	if(message.senderId != undefined)
		textP.innerHTML = findUser(message.senderId) +" says: " + message.text;
	//u suprotnom je sistemska poruka
	else
		textP.innerHTML = message.text;
	var span = document.createElement("SPAN");
	span.classList.add("time_date");
	span.innerHTML = "11:01 AM    |    June 9";

	receivedWithdMsg.appendChild(textP);
	receivedWithdMsg.appendChild(span);
	receivedMsg.appendChild(receivedWithdMsg);
	incomingMsg.appendChild(incomingMsgImg);
	incomingMsg.appendChild(receivedMsg);
	msgHistory.appendChild(incomingMsg);
	
	var inboxChat = document.querySelector('.msg_history');
	inboxChat.scrollTop = inboxChat.scrollHeight - inboxChat.clientHeight;
}

function findUser(id){
	for(let user of registeredUsers){
		if(user.id == id){
			return user.username;
		}
	}
	return "";
}

function makePostBtnForAll(){
  $("#btnPost").off();
  $("#btnPost").click(function(){
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
				showSentMessage(text)
				$.get({
					url:"rest/chat/user/"+loggedIn.id,
					contentType:"application/json",
					complete: function(data){
						console.log(data.responseText);
					}
				});
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
					showSentMessage(text);
				}
			});
		}); 
	}