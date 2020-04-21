
var socket;
var host="ws://localhost:8080/WAR2020/ws";
$(document).ready(function(){
	$("#btnPost").click(function(){
		var msg = document.getElementById("msgTextBox").value;
		$.ajax({
			url:"rest/chat/post/" + msg,
			type:"POST",
			data: {},
			contentType:"application/json",
			dataType:"json",
			complete:function(data){
				console.log("sent message to the server");
			}
		});
	});
	try{
		socket = new WebSocket(host);
		console.log("connect: Socket Status: "+socket.readyState);
		socket.onopen = function(){
			console.log("onopen: Socket Status: "+socket.readyState + " (open)");	
			}
		socket.onmessage = function(msg){
			console.log("onmessage: Recieved: "+ msg.data);	
			}
		socket.onclose = function(){
			socket = null;
			}
		} catch (exception) {
			console.log("Error " + exception);
		}
});