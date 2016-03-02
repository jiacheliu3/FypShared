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
.link {
  stroke: #ccc;
}

.node text {
  pointer-events: none;
  font: 10px sans-serif;
}
</style>


<script type="text/javascript" src="d3.js"></script>

<script>console.log("aaa");
	
</script>


</head>
<body>
	<script>
		var dat="${data}";
		console.log(dat);
		var net="${network}";
		
		console.log("draw start");
		var width = 960, height = 500

		var svg = d3.select("body").append("svg").attr("width", width).attr(
				"height", height);

		var force = d3.layout.force().gravity(.05).distance(100).charge(-100)
				.size([ width, height ]);
		

		var obj=JSON.parse(net.replace(/&quot;/g, '"'));
		//var obj=JSON.parse(dat);
		console.log("obj:"+obj);
		
			var n=obj.nodes;
			var l=obj.links;
			console.log(n);
			console.log(l);
			force.nodes(n).links(l).start();

			var link = svg.selectAll(".link").data(l).enter().append("line").attr("class", "link");

			var node = svg.selectAll(".node").data(n).enter().append("g").attr("class", "node").call(force.drag);

			node.append("image").attr("xlink:href","https://github.com/favicon.ico").attr("x", -8).attr("y",-8).attr("width", 16).attr("height", 16);

			node.append("text").attr("dx", 12).attr("dy", ".35em").text(
					function(d) {
						return d.name
					});

			force.on("tick", function() {
				link.attr("x1", function(d) {
					return d.source.x;
				}).attr("y1", function(d) {
					return d.source.y;
				}).attr("x2", function(d) {
					return d.target.x;
				}).attr("y2", function(d) {
					return d.target.y;
				});

				node.attr("transform", function(d) {
					return "translate(" + d.x + "," + d.y + ")";
				});
			});


		console.log("drawn whatever");
	</script>
	</body>