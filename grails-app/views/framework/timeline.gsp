<!DOCTYPE html>
<meta charset="utf-8">
<html>
<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<!-- Apply layout -->
<meta name="layout" content="lte_layout">

<title>Timeline Study</title>
<link rel="stylesheet"
	href="${resource(dir: 'css/toolbox', file: 'timeseries.css')}" type="text/css">
<style>
rect.bordered {
	stroke: #E6E6E6;
	stroke-width: 2px;
}

text.mono {
	font-size: 12pt;
	font-family: Consolas, courier;
	fill: #aaa;
}

text.axis-workweek {
	font-size: 10pt;
	fill: #000;
}

text.axis-worktime {
	font-size: 10pt;
	fill: #000;
}
text.axis-label{
	font-size:14px;
}
#chart{
	width:100;
	overflow:auto;
}
</style>

</head>
<body>
	<!-- Page Content -->
	<div id="page-wrapper" class="content-wrapper">
		<section class="content-header">
			<h1 class="page-header">User Keyword Study</h1>
		</section>
		<div class="container-fluid">
			<div class="row">
				<div class="col-lg-12">
					<div class="box box-primary" id="keywordList">
						<div class="box-header with-border">
							<h4>Timeline on topics</h4>
						</div>
						<div class="box-body" id="buttonPanel">
							<div id="chart"></div>
							<div id="dataset-picker"></div>
						</div>
					</div>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->

			<div class="row">
				<div class="col-lg-12">
					<div class="box box-primary" id="keywordList">
						<div class="box-header with-border">
							<h4>Timeline on weibo items</h4>
						</div>
						<div class="box-body" id="buttonPanel">
							<div class="timeline" style="overflow:auto;"></div>

						</div>
					</div>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
			
			<div class="row">
				<div class="col-lg-12">
					<div class="box box-primary" id="keywordList">
						<div class="box-header with-border">
							<h4>Yet another timeline</h4>
						</div>
						<div class="box-body">
							<div id="spottedTimeline" style="overflow:auto;"></div>

						</div>
					</div>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
		</div>
		<!-- /.container-fluid -->
	</div>
	<!-- /.content-wrapper -->
	<content tag="javascript">
	<g:javascript src="toolbox/timeseries.js" />
	<g:javascript src="udf/forgeData.js" />
	<g:javascript src="udf/spottedTimeline.js" />
	<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/lodash.js/3.1.0/lodash.min.js"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.4/highlight.min.js"></script>
    
	<script type="text/javascript">
		//define the function to draw
		function heatmapChart(data, rows, cols) {
			var margin = {
				top : 50,
				right : 0,
				bottom : 20,
				left : 120
			}, width = 960 - margin.left - margin.right;
			//height = 430 - margin.top
			//	- margin.bottom, 
			//calculate the height of svg
			var gridSize = Math.floor(width / 12), legendElementWidth = gridSize, buckets = 9, colors = [
					"#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb", "#41b6c4",
					"#1d91c0", "#225ea8", "#253494", "#081d58" ], // alternatively colorbrewer.YlGnBu[9]
			//days = [ "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su" ],
			//days = [ "Films", "Music", "Fashion", "ACG", "Sports" ], 
			//times = [
			//"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
			//"Oct", "Nov", "Dec" ];
			days = rows, times = cols;
			var height = margin.top + margin.bottom + rows.length * gridSize + 50;

			//calculate the location of legends
			var legendY = margin.top + rows.length * gridSize;
			console.log("Put legend at Y " + legendY);

			var svg = d3.select("#chart").append("svg").attr("width",
					width + margin.left + margin.right).attr("height",
					height + margin.top + margin.bottom).append("g").attr(
					"transform",
					"translate(" + margin.left + "," + margin.top + ")");
			var offsetY = 0;
			var dayLabels = svg
					.selectAll(".dayLabel")
					.data(days)
					.enter()
					.append("text")
					.text(
							function(d) {
								console.log("The fker in the label is " + d);
								var txt = "";
								console.log("The content has length "
										+ d.length);
								if (d.length <= 10)
									txt = d;
								else {
									var l = d.length;
									var rows = l / 10 + 1;
									console.log("Supposed to have " + rows
											+ " rows");
									for (var i = 0; i < rows; i++) {
										var t = '<tspan x="'+i*gridSize+'" dy="'+offsetY+'">'
												+ d.substring(i * 10,
														i * 10 + 11)
												+ '</tspan>';
										console.log("Another row " + t);
										txt += t;
										offsetY += 10;
									}
									console.log("The generated text is " + txt);

									txt = d.substring(0, 8) + "..";
								}
								return txt;
							}).attr("x", 0).attr("y", function(d, i) {
						return i * gridSize;
					}).style("text-anchor", "end").attr("transform",
							"translate(-6," + gridSize / 1.5 + ")").attr(
							"class", function(d, i) {
								return "dayLabel axis axis-workweek";
							});
			var timeLabels = svg.selectAll(".timeLabel").data(times).enter()
					.append("text").text(function(d) {
						return d;
					}).attr("x", function(d, i) {
						return i * gridSize;
					}).attr("y", 0).style("text-anchor", "middle").attr(
							"transform", "translate(" + gridSize / 2 + ", -6)")
					.attr("class", function(d, i) {
						return "timeLabel axis axis-worktime";
					});

			var colorScale = d3.scale.quantile().domain(
					[ 0, buckets - 1, d3.max(data, function(d) {
						return d.value;
					}) ]).range(colors);

			var cards = svg.selectAll(".hour").data(data, function(d) {
				return d.day + ':' + d.hour;
			});

			cards.append("title");

			cards.enter().append("rect").attr("x", function(d) {
				return (d.hour - 1) * gridSize;
			}).attr("y", function(d) {
				return (d.day - 1) * gridSize;
			})//.attr("rx", 4).attr("ry", 4)
			.attr("class", "hour").attr("width", gridSize).attr("height",
					gridSize).style("fill", colors[0]);

			cards.transition().duration(1000).style("fill", function(d) {
				return colorScale(d.value);
			});

			cards.select("title").text(function(d) {
				return d.value;
			});

			cards.exit().remove();

			var legend = svg.selectAll(".legend").data(
					[ 0 ].concat(colorScale.quantiles()), function(d) {
						return d;
					});

			legend.enter().append("g").attr("class", "legend");

			legend.append("rect").attr("x", function(d, i) {
				return legendElementWidth * i;
			}).attr("y", legendY).attr("width", legendElementWidth).attr(
					"height", gridSize / 2).style("fill", function(d, i) {
				return colors[i];
			});

			legend.append("text").attr("class", "mono").text(function(d) {
				return "≥ " + Math.round(d);
			}).attr("x", function(d, i) {
				return legendElementWidth * i;
			}).attr("y", legendY + gridSize);

			legend.exit().remove();

		}
		function asynchroTimeline() {
			console.log("Start");
			//startBootstrapProgressBar("network", 100);
			var jsonData = $.ajax({
				url : "/CodeBigBroSub/environment/asynchroTimeline",
				dataType : "json",
				async : true
			}).done(function(jsonData) {
				console.log("Got data");
				console.log(jsonData);
				var spots=jsonData["spots"];
				console.log("The spots on timeline are ");
				console.log(spots);
				timelineChart(spots);
			
			});
		}
		
		function asynchroTopics(){
			console.log("Start");
			//startBootstrapProgressBar("network", 100);
			var jsonData = $.ajax({
				url : "/CodeBigBroSub/environment/asynchroTopics",
				dataType : "json",
				async : true
			}).done(function(raw) {
				console.log("Topics got");
				console.log(raw);
				var cells = raw["cells"];
				var rows = raw["rows"];
				var cols = raw["columns"];
				var data = topicToGrid(cells);
				console.log("The target data is " + data);
				heatmapChart(data, rows, cols);
			
			});
		}
		function timelineChart(data){
			var domEl = 'timeline';
			var brushEnabled = true;
			timeseries(domEl, data, brushEnabled);
		}
		function weiboTimeline() {
			var data = [ {
				'value' : 1380854103662
			}, {
				'value' : 1363641921283
			} ];
			timelineChart(data);
		}
		function yaTimeline(){
			var data=getFakeTimeline();
			console.log("Got data for the spotted timeline");
			console.log(data);
			drawSpottedTimeline(data);
		}
		asynchroTimeline();
		asynchroTopics();
		yaTimeline();
		//weiboTimeline();
	</script>
	</content>
</body>
</html>