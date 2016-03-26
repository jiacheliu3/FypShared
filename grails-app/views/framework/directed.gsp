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
</style>
<!-- Liquid gauge -->
<style>
.liquidFillGaugeText {
	font-size:30px;
	font-family: Helvetica;
	font-weight: bold;
}
</style>

</head>
<body>
	<!-- Page Content -->
	<div id="page-wrapper" class="content-wrapper">
		<section class="content-header">
			<h1 class="page-header">Directed network</h1>
		</section>
		<div class="container-fluid">

			<div class="row">
				<div class="col-lg-12">
					<div id="canvas"></div>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div id="another"></div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-4">
					<div id="gauge">
						<svg id="fillgauge1" width="90%"
							onclick="gauge1.update(NewValue());"></svg>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-12">
					<svg viewBox="0 0 1000 1000" version="1.1">
					    <defs>
					        <!-- A circle of radius 200 -->
					        <circle id="s1" cx="200" cy="200" r="200" fill="yellow"
							stroke="black" stroke-width="3" />
					        <!-- An ellipse (rx=200,ry=150) -->
					        <ellipse id="s2" cx="200" cy="150" rx="200" ry="150"
							fill="salmon" stroke="black" stroke-width="3" />
					    </defs>
					    <use x="100" y="100" xlink:href="#s1" />
					    <use x="100" y="650" xlink:href="#s2" />
					</svg>
				</div>
			</div>
			<!-- /.row -->
			
			<div class="row">
				<div class="col-lg-12">
					<p id="formatText"></p>
				</div>
			</div>

		</div>
		<!-- /.container-fluid -->
	</div>
	<!-- /.content-wrapper -->
	<content tag="javascript"> <g:javascript
		src="udf/drawDirectedNetwork.js" /> <g:javascript
		src="toolbox/d3.liquidGauge.js" /> <script type="text/javascript">
			var data = forgeNetworkData();
			console.log("The data is ");
			console.log(data);
			drawDirectedNetwork(data, "#canvas");
			var anotherData = forgeNetworkData();
			drawDirectedNetwork(anotherData, "#another");
		</script> <script>
		var gauge1 = loadLiquidFillGauge("fillgauge1", 55);
		var config1 = liquidFillGaugeDefaultSettings();
		config1.circleColor = "#FF7777";
		config1.textColor = "#FF4444";
		config1.waveTextColor = "#FFAAAA";
		config1.waveColor = "#FFDDDD";
		config1.circleThickness = 0.2;
		config1.textVertPosition = 0.2;
		config1.waveAnimateTime = 1000;
	</script> 
	<script>
		var formatDateRange=d3.time.format("%H-%H");
		var t1=formatDateRange("00-23");
		console.log(t1);
		document.getElementById("formatTime").innerHTML=t1;
	</script>
	</content>
</body>
</html>