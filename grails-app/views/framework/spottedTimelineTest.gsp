<html>
<head>
<link rel="stylesheet"
	href="${resource(dir: 'css/framework', file: 'bootstrap.min.css')}"
	type="text/css">
<!-- plugins -->
<link rel="stylesheet"
	href="${resource(dir: 'css/framework', file: 'font-awesome.min.css')}"
	type="text/css">
<link rel="stylesheet"
	href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
<!-- Theme style -->
<link rel="stylesheet"
	href="${resource(dir: 'css/template', file: 'AdminLTE.min.css')}"
	type="text/css">
<!-- Must-use JS libraries go first -->
<g:javascript src="toolbox/jquery.min.js" />
<!-- Graph specific -->
<g:javascript src="toolbox/d3plus.full.js" />
<!-- Network styles -->
<style>
path.link {
	fill: none;
	stroke: #666;
	stroke-width: 1.5px;
}

path.link.twofive {
	opacity: 0.25;
}

path.link.fivezero {
	opacity: 0.50;
}

path.link.sevenfive {
	opacity: 0.75;
}

path.link.onezerozero {
	opacity: 1.0;
}

circle {
	fill: #ccc;
	stroke: #fff;
	stroke-width: 1.5px;
}

text {
	fill: #000;
	font: 10px sans-serif;
	pointer-events: none;
}
/*
.open-path{
  stroke:#f00 !important;
  stroke-width:5px;
}
*/
.arrow {
	stroke: #f00;
	stroke-width: 5px;
}

.networkCanvas {
	background: #f1f1f1;
	max-width: 1080px;
	max-height: 800px;
	overflow: auto;
}
</style>

</head>
<body>
	<div id="simpleNetworkCanvas" class="networkCanvas"></div>
	<div id="fullNetworkCanvas" class="networkCanvas"></div>


	<g:javascript src="toolbox/bootstrap.js" />
	<g:javascript src="udf/forgeData.js" />
	<g:javascript src="udf/newSpottedTimeline.js" />
	<g:javascript src="udf/drawDirectedNetwork.js" />
	<script>
		function yaTimeline() {
			var data = getFakeJournals();
			console.log("Got data for the spotted timeline");
			console.log(data);
			drawSpottedTimeline(data);
		}
		var data = getFakeJournals();
		drawSpottedTimeline(data, "body");
	</script>
	<script>
		/* Test network */
		function asynchroInteraction() {
			// show a progress bar in the zone
			//startProgressBar('relation',300);
			//startBootstrapProgressBar('relation', 300);
			var jsonData = $.ajax({
				url : "/CodeBigBroSub/environment/asynchroInteraction",
				dataType : "json",
				async : true
			}).done(function(jsonData) {
				console.log("Interaction data is ");
				console.log(jsonData);
				//first separate relation data and network data
				var interaction = jsonData["interaction"];
				var network = jsonData["network"];
				//displayInfo(jsonData);
				//drawRelation();
				//drawInteraction(interaction);
				drawNetwork(network);
			});
		}
		function drawNetwork(networks) {
			console.log("Network data");
			console.log(networks);

			//draw simple network
			var simpleJson = networks["simple"];
			var simple = JSON.parse(simpleJson);
			drawDirectedNetwork(simple, "#simpleNetworkCanvas");
			//draw full network
			var fullJson = networks["complex"];
			var full = JSON.parse(fullJson);
			drawDirectedNetwork(full, "#fullNetworkCanvas");

		}
		asynchroInteraction();
	</script>
</body>
</html>