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
	<script>
	 function drawChart() {
	      var jsonData = $.ajax({
	        url: "${createLink(controller:'environment', action:'asynchro')}",
	        dataType: "json",
	        async: true
	      }).done(function(jsonData) {
	    	  
	    	  console.log(jsonData);
		      //console.log(jsonText);
		      document.getElementById("result").innerHTML=jsonData;
	    	});;
	 
	      
	    }
	    drawChart();
	</script>
	
</body>
</html>