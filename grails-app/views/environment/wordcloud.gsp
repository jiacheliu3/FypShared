
<%@ page import="codebigbrosub.Environment"%>
<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'environment.label', default: 'Environment')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
<%@ page import="codebigbrosub.User"%>
<script type="text/javascript" src="wordcloud2.js">
	
</script>
</head>
<body>
<% %>
	<script>
	var list ="${cloudJson}";

	console.log(list);

	var map=JSON.parse(list.replace(/&quot;/g,'"'));
	console.log(map);

	var arr=[];
	for(var e in map){
		console.log(e,map[e]);
		arr.push([e,map[e]]);
	}
	console.log(arr);
	</script>
	<a href="#list-environment" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>
	<div class="nav" role="navigation">
		<ul>
			<li><a class="home" href="${createLink(uri: '/')}"><g:message
						code="default.home.label" /></a></li>
			<li><g:link class="create" action="create">
					<g:message code="default.new.label" args="[entityName]" />
				</g:link></li>
		</ul>
	</div>
	<div id="list-environment" class="content scaffold-list" role="main">
		<h1>
			<g:message code="default.list.label" args="[entityName]" />
		</h1>
		<g:if test="${flash.message}">
			<div class="message" role="status">
				${flash.message}
			</div>
		</g:if>

		<canvas id="my_canvas" width="800px" height="400px"></canvas>
		<script>
			//window.alert("0");
			var list = [ [ 'foo', 200 ], [ 'bar', 100 ] ];
			//window.alert(list);
			WordCloud(document.getElementById('my_canvas'), {
				list : arr
			});
		</script>
	</div>
</body>
</html>
