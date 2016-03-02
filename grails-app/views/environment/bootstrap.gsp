<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'environment.label', default: 'Environment')}" />
<title><g:message code="default.create.label"
		args="[entityName]" /></title>
<%@ page import="codebigbrosub.User"%>
<%@ page import="java.util.Map"%>
<%@ page import="codebigbrosub.EnvironmentController.KeywordWrapper"%>

<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
<g:javascript src="progressBar.js" />
</head>
<body>
<button type="button" id="start">Move out</button>
<button type="button" id="stop">Move out</button>
<div class="progress" id="tryProgressDiv">
  <div class="progress-bar progress-bar-striped active" id="tryProgressBar" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100">
    <span class="sr-only">45% Complete</span>
  </div>
</div>
<script>
	document.getElementById("start").onclick=function(){
		startBootstrapProgressBar("try",5);
	}
	document.getElementById("stop").onclick=function(){
		stopBootstrapProgressBar("try",5);
	}

</script>
</body>
</html>