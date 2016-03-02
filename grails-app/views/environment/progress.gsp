<%@ page import="codebigbrosub.Environment"%>
<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'environment.label', default: 'Environment')}" />
<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>
<body>
	<a href="#show-environment" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>
	<div id="result">Here is the result</div>
	<div id="testPanel">
		<div id="testProgressDiv" class='fakeContent'>
			<progress class='progress' id='testProgressBar' value='20' max='100'
				style='width: 90%; height: 20px;'>20%</progress>
		</div>
		<div id="testContentDiv" class='realContent'>
			<div id="testContent">Here is the real content</div>
		</div>
	</div>
	<button type='button' id='init'>Init</button>
	<button type='button' id='start'>Start</button>
	<button type='button' id='stop'>Stop</button>
	<div>How come no shit in here</div>
	<script>
		function initProgressBar(id) {
			var panel = document.getElementById(id + 'ProgressDiv');
			console.log("found panel of progress bar " + panel);

			console.log(id);
			var bar = document.getElementById(id + "ProgressBar");
			console.log("init located element " + bar);
			bar.value = 0;
			bar.max = 100;

		}
		function startProgressBar(id) {
			var bar = document.getElementById(id + 'ProgressBar');
			console.log("start progressing " + bar);
			var progress = 1;
			var random = function(min, max) {
				return Math.floor(Math.random() * (max - min + 1) + min);
			};
			var timeout = 30;
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
				console.log("stop function located the real content "+ content);
				content.style.display = 'block';
			}, 300);

		}
		var initElement = document.getElementById("init");
		initElement.addEventListener('click', function() {
			initProgressBar("test");
		});
		var startElement = document.getElementById("start");
		startElement.addEventListener('click', function() {
			startProgressBar("test");
		});
		var stopElement = document.getElementById("stop");
		stopElement.addEventListener('click', function() {
			stopProgressBar("test");
		});
		$('.realContent').css('display', 'none');
		$('.fakeContent').css('display', 'block');
	</script>

</body>
</html>