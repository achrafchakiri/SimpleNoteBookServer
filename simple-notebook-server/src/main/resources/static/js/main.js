'use strict';

var codeToExecForm = document.querySelector('#codeToExecForm');
var codeToExecInput = document.querySelector('#codeToExecInput');
var codeExecutionError = document.querySelector('#codeExecutionError');
var codeExecutionResult = document.querySelector('#codeExecutionResult');
var codeWaitingResult = document.querySelector('#waiting');

function callServer(language, code) {
	var newCode = code.replace(new RegExp("\n", 'g'), "\\n");
	newCode = newCode.replace(new RegExp("\t", 'g'), "\\t");
	var data = "{\"code\": \"" + language + " " + newCode + "\"}";
	var json = JSON.stringify(data);
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/execute");
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.send(data);
	xhr.onload = function() {
		var response = JSON.parse(xhr.responseText);
		if (xhr.status == 200) {
			codeWaitingResult.style.display = "none";
			codeExecutionError.style.display = "none";
			var output = response.output
			if (output === "") {
				output = "code was correctly executed but there was nothing to print";
			}
			codeExecutionResult.innerHTML = "<p>" + response.result + "</p><p>"
					+ output + "</p>";
			codeExecutionResult.style.display = "block";
		} else {
			codeExecutionResult.style.display = "none";
			codeExecutionError.innerHTML = (response && response.message)
					|| "Some Error Occurred";
		}
	}
	

}

codeToExecForm.addEventListener('submit', function(event) {
	codeWaitingResult.style.display = "block";
	var code = document.getElementById('codeToExecInput').value;
	var language = document.getElementById("language").value;
	if (code.length === 0) {
		codeExecutionError.innerHTML = "Please enter a code statement ";
		codeExecutionError.style.display = "block";
		return;
	}
	callServer(language, code);
	event.preventDefault();
}, true);
