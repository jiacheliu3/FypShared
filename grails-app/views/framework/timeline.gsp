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
<!-- Blocked spotted timeline -->
<style>
	.label{
		font-size:100%;
	}
</style>

</head>
<body>
	<!-- Page Content -->
	<div id="page-wrapper" class="content-wrapper">
		<section class="content-header">
			<h1 class="page-header">User Timeline Study</h1>
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
					<div class="box box-primary">
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
					<div class="box box-primary">
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
	<!-- For the heatmap -->
	<g:javascript src="toolbox/d3.heatmap.js" />
	<!-- For the time series where each weibo is a spot on the time matrix -->
	<g:javascript src="toolbox/d3.timeseries.js" />
	<g:javascript src="udf/forgeData.js" />
	<!-- For the time series where the size of spots stand for weibo volume -->
	<g:javascript src="udf/newSpottedTimeline.js" />
	<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/lodash.js/3.1.0/lodash.min.js"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.4/highlight.min.js"></script>
    
	<script type="text/javascript">
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
				//spotted timeline with no table, 2nd graph
				var spots=jsonData["spots"];
				console.log("The spots on timeline are ");
				console.log(spots);
				timelineChart(spots);
				//spotted timeline in table format, 3rd graph
				var blocks=jsonData["blocks"];
				console.log("Date for table spotted timeline:");
				console.log(blocks);
				var formatBlocks=timelineFormat(blocks);
				drawSpottedTimeline(formatBlocks,"#spottedTimeline");
			
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
			var brushEnabled = false;
			//draw the timeline where each weibo is a spot
			timeseries(domEl, data, brushEnabled);
		}
		function yaTimeline(){
			var data=getFakeJournals();
			console.log("Got data for the spotted timeline");
			console.log(data);
			drawSpottedTimeline(data,"#spottedTimeline");
		}
		asynchroTimeline();
		asynchroTopics();
		//yaTimeline();
		//weiboTimeline();
	</script>
	</content>
</body>
</html>