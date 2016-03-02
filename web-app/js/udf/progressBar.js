function initProgressBar(id) {
	var panel = document.getElementById(id + 'ProgressDiv');
	console.log("found panel of progress bar " + panel);

	console.log(id);
	var bar = document.getElementById(id + "ProgressBar");
	console.log("init located element " + bar);
	bar.value = 0;
	bar.max = 100;

}
function startProgressBar(id,speed) {
	var bar = document.getElementById(id + 'ProgressBar');
	console.log("start progressing " + bar);
	var progress = 1;
	var random = function(min, max) {
		return Math.floor(Math.random() * (max - min + 1) + min);
	};
	var timeout = speed;
	var onprogress = function() {
		if (bar.value == 100) {
			return;
		}

		progress += random(1, 5);
		if (progress > 98) {
			progress = 98;
		}

		bar.value = progress;
		bar.innerHTML = progress + '%';
	};
	setInterval(onprogress, timeout);
}
function stopProgressBar(id) {
	var bar = document.getElementById(id + 'ProgressBar');
	bar.value = 100;
	setTimeout(function() {
		var panel = document.getElementById(id + 'ProgressDiv');
		console.log('stop function logged the panel ' + panel);
		panel.style.display = 'none';
		var content = document.getElementById(id + 'ContentDiv');
		console.log("stop function located the real content " + content);
		content.style.display = 'block';
	}, 300);

}
function startBootstrapProgressBar(id,speed) {
	var bar = document.getElementById(id + 'ProgressBar');
	console.log("start progressing " + bar);
	var progress = 1;
	var random = function(min, max) {
		return Math.floor(Math.random() * (max - min + 1) + min);
	};
	var timeout = speed;
	var onprogress = function() {
		var now=bar.style.width;
		if (now == 100) {
			return;
		}

		progress += random(1, 5);
		if (progress > 98) {
			progress = 98;
		}

		bar.setAttribute("style","width:"+progress+"%;");
		
		 //bar.setAttribute("aria-valuenow",progress);
		bar.innerHTML = progress + '%';
	};
	setInterval(onprogress, timeout);
}
function stopBootstrapProgressBar(id) {
	var bar = document.getElementById(id + 'ProgressBar');
	console.log("Located progress bar to stop "+bar);
	bar.style.width = '100%';
	console.log(bar.style.width);

//	setTimeout(function() {
//		var panel = document.getElementById(id + 'ProgressDiv');
//		console.log('stop function logged the panel ' + panel);
//		panel.style.display = 'none';
//		var content = document.getElementById(id + 'ContentDiv');
//		console.log("stop function located the real content " + content);
//		content.style.display = 'block';
//	}, 300);
	setTimeout(function() {
		var panel = document.getElementById(id + 'ProgressDiv');
		var message =document.getElementById(id+'SuccessMessage');
		message.style.display = 'block';
		var bar = document.getElementById(id + 'ProgressZone');
		bar.style.display='none';
		
	}, 100);

}
