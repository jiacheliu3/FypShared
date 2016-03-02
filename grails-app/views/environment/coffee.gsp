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

<style>
#content {
	margin: 10px;
	padding: 10px;
}

#weiboPhoto {
	position: relative;
	float: right;
	bottom: 50px;
	margin-right: 50px;
}

.tags {
	text-align: center;
	padding: 5px;
}
</style>
<script type="text/javascript" src="network.js"></script>
<script type="text/javascript" src="start.js"></script>
<script>console.log("aaa");
	
</script>


</head>
<body>
<div id="test"></div>
<script type="text/javascript">
	var a = say();


	console.log(a);
	document.getElementById("test").innerHTML = a;
</script>
</body>