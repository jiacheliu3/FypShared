<!DOCTYPE html>
<html>
<head>
<title>Hello Bubble Chart</title>
<meta charset="utf-8">

<link
	href='http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,600,200italic,600italic&subset=latin,vietnamese'
	rel='stylesheet' type='text/css'>
<style>
.bubbleChart {
	min-width: 100px;
	max-width: 700px;
	height: 700px;
	margin: 0 auto;
}

.bubbleChart svg {
	background: #000000;
}

.text {
	white-space: pre-wrap;
}
</style>
</head>
<body>
	<h3>Test bubble chart</h3>
	<div class="bubbleChart" style="background: #000000"></div>
	<h3>Lalala</h3>
	
	<div id="lalala" style="background: #000000"></div>
	<div>
		<h3>The cell information</h3>
		<span>Full text:</span>
		<span id="sampleBubbleText"></span>
		<span>Count:</span>
		<span id="sampleBubbleCount"></span>
	</div>
	
	<!-- create container element for visualization -->
<div id="viz"></div>

	<script
	src="http://phuonghuynh.github.io/js/bower_components/jquery/dist/jquery.min.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/d3/d3.min.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/d3-transform/src/d3-transform.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/cafej/src/extarray.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/cafej/src/misc.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/cafej/src/micro-observer.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/microplugin/src/microplugin.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/bubble-chart/src/bubble-chart.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/bubble-chart/src/plugins/central-click/central-click.js"></script>
<script
	src="http://phuonghuynh.github.io/js/bower_components/bubble-chart/src/plugins/lines/lines.js"></script>
<g:javascript src="toolbox/d3.js" />
<g:javascript src="toolbox/d3plus.js" />
<g:javascript src="toolbox/topojson.min.js" />
<g:javascript src="toolbox/d3.bubblechart.js" />
<g:javascript src="udf/bubbleChart.js" />
	<script>
		$(document).ready(function() {
			//var data=[
			//        {text: "Java", count: "236"},
			//       {text: ".Net", count: "382"},
			//       {text: "Php", count: "170"},
			//       {text: "Rubyaaaaaaaaaa", count: "123"},
			//      {text: "D", count: "12"},
			//      {text: "Python", count: "170"},
			//       {text: "C/C++", count: "382"},
			//       {text: "Pascal", count: "10"},
			//      {text: "Somethingaaaaaaaaaaaaa", count: "170"},
			//    ];
			var data = {
				"Java" : 236,
				".Net" : 382,
				"Php" : 170,
				"Ruby on Rails" : 123,
				"D" : 12,
				"Python" : 170,
				"C/C++" : "382"
			};
			console.log("Draw the bubble chart.");
			//drawBubbleChart("#lalala",data);
			console.log("Wrap text in the circle");
			console.log("Add onclick listener.");
			//showFullText();
			var table=generateTable(data);
			console.log("The table repo is");
			console.log(table);
			//bubbleClick(table,"#lalala","sample");

		});
	</script>
</body>
</html>