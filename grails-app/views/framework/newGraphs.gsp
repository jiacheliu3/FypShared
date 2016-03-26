<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<meta name="layout" content="lte_layout">

<title>SB Admin 2 - Bootstrap Admin Theme</title>

<link rel="stylesheet"
	href="${resource(dir: 'css/framework', file: 'bootstrap.min.css')}"
	type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css/toolbox', file: 'timeline.css')}"
	type="text/css">

<link rel="stylesheet"
	href="${resource(dir: 'css/framework', file: 'font-awesome.min.css')}"
	type="text/css">


<style>
[data-notify="progressbar"] {
	margin-bottom: 0px;
	position: absolute;
	bottom: 0px;
	left: 0px;
	width: 100%;
	height: 5px;
}

.background {
	background-color: #222d32;
}

.light {
	opacity: 0.7;
}

.zero {
	opacity: 0.5;
}

text {
	text-anchor: middle;
}

#credit {
	position: absolute;
	right: 4px;
	bottom: 4px;
	color: #ddd;
}

#credit a {
	color: #fff;
	font-weight: bold;
}

#genderZone {
	width: 810px;
	height: 180px;
	overflow: auto;
}

.genderImage {
	height: 87px;
	width: 400px;
	position: absolute;
}

.biped {
	width: 40px;
	height: 87px;
}

.numberTag {
	max-width: 350px;
}

.inner h3 {
	font-family: 'Source Sans Pro', sans-serif;
}

.bubbleZone {
	background: #000;
	margin: auto;
	max-height: 600px;
	max-width: 600px;
	overflow: hidden;
}

.black-curtain {
	background: #000;
}

.infoBox {
	height: 200px;
}

#ageStat {
	max-width: 90%;
	max-height: 500px;
	height: 300px;
	margin: auto;
}

#ageWrapper {
	overflow: auto;
}
</style>
<!-- Forgeries -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'mylabels.css')}" type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css/toolbox', file: 'timeseries.css')}"
	type="text/css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<style>
.keywords {
	width: 70px;
	height: 35px;
	outline: #FFFFFF solid 2px; font-size =1em;
	padding: 0px;
	color: #222d32;
	font-weight: bold;
}

.user-image{
	font-size:90px;
}
</style>
</head>

<body>

	<div id="page-wrapper" class="content-wrapper">
		<section class="content-header">
			<h1 class="page-header">User Stats</h1>
		</section>
		<div class="container-fluid">
			<div class="row">
				<div class="col-lg-12">
					<div id="test1">
						<h3>Test graph 1</h3>
					</div>
					<div id="stripePieChart"></div>
				</div>
			</div>
			<!-- /.row -->
			<div class="row">
				<div class="col-lg-12">
					<div id="test1">
						<h3>Timeseries graph here</h3>
					</div>
					<div class="timeseries"></div>

				</div>
			</div>
			<!-- /.row -->
			<div class="row">
				<div class="col-lg-12">
					<!-- Widget: user widget style 1 -->
					<div class="box box-widget widget-user-2">
						<!-- Add the bg color to the header using any of the bg-* classes -->
						<div class="widget-user-header bg-yellow">
							<div class="widget-user-image">

								<div class="icon">
									<i class="ion ion-social-reddit-outline"></i>
								</div>
								<span class="user-image"><i class="ion ion-social-reddit-outline"></i></span>
							</div>
							<!-- /.widget-user-image -->
							<h3 class="widget-user-username" id="longestUserName"></h3>

						</div>
						<div class="box-footer no-padding">
							<ul class="nav nav-stacked">
								<li><a href="#"><b>The longest introduction goes to
											this user.</b> <span id="longestIntroUser"></span></a></li>
								<li><a href="#">Content: <span
										class="pull-right badge bg-aqua" id="longestContent"></span></a></li>
							</ul>
						</div>
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- row -->



			<div class="row">
				<div class="col-lg-12">
					<div class="box box-warning">
						<div class="box-header with-border">
							<h3 class="box-title">A map of China</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>
							<!-- /.box-tools -->
						</div>
						<!-- /.box-header -->
						<div class="box-body">

							<div id="chinaMap" style="margin: auto"></div>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->



			<div class="row">
				<div class="col-lg-12" style="overflow: auto">
					<div class="box box-warning">
						<div class="box-header with-border">
							<h3 class="box-title">Gender graph goes here</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>
							<!-- /.box-tools -->
						</div>
						<!-- /.box-header -->
						<div class="box-body">

							<div class="row">
								<div class="col-lg-3 col-xs-6 numberTag">
									<!-- small box -->
									<div class="small-box bg-aqua">
										<div class="inner">
											<h3 id="allCount"></h3>
											<p>Friends</p>
										</div>
										<div class="icon">
											<i class="ion ion-stats-bars"></i>
										</div>
										<a href="#" class="small-box-footer"> More info <i
											class="fa fa-arrow-circle-right"></i>
										</a>
									</div>
								</div>
								<!-- ./col -->
								<div class="col-lg-3 col-xs-6 numberTag">
									<!-- small box -->
									<div class="small-box bg-green">
										<div class="inner">
											<h3 id="maleCount">
												<sup style="font-size: 20px"></sup>
											</h3>
											<p>Males</p>
										</div>
										<div class="icon">
											<i class="ion ion-male"></i>
										</div>
										<a href="#" class="small-box-footer"> More info <i
											class="fa fa-arrow-circle-right"></i>
										</a>
									</div>
								</div>
								<!-- ./col -->
								<div class="col-lg-3 col-xs-6 numberTag">
									<!-- small box -->
									<div class="small-box bg-yellow">
										<div class="inner">
											<h3 id="femaleCount"></h3>
											<p>Females</p>
										</div>
										<div class="icon">
											<i class="ion ion-female"></i>
										</div>
										<a href="#" class="small-box-footer"> More info <i
											class="fa fa-arrow-circle-right"></i>
										</a>
									</div>
								</div>
								<!-- ./col -->
								<div class="col-lg-3 col-xs-6 numberTag">
									<!-- small box -->
									<div class="small-box bg-red">
										<div class="inner">
											<h3 id="unknownCount"></h3>
											<p>Kept as secrets</p>
										</div>
										<div class="icon">
											<i class="ion ion-social-reddit-outline"></i>
										</div>
										<a href="#" class="small-box-footer"> More info <i
											class="fa fa-arrow-circle-right"></i>
										</a>
									</div>
								</div>
								<!-- ./col -->
								<div class="col-lg-12">
									<div id="newZone"
										style="margin: auto; max-width: 1000px; overflow: auto;"></div>

								</div>
							</div>
							<!-- /.row -->
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->


				</div>
			</div>
			<!-- /.row -->

			<div class="row">
				<div class="col-lg-12" style="overflow: auto">
					<div class="box box-warning">
						<div class="box-header with-border">
							<h3 class="box-title">How do his friends sketch themselves?</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>
							<!-- /.box-tools -->
						</div>
						<!-- /.box-header -->
						<div class="box-body">

							<div id="introStat" class="col-lg-8">
								<h4>Keywords from introductions:</h4>
								<canvas id="wordCloudCanvas"></canvas>
							</div>
							<div class="col-lg-4">
								<p id="introLong"></p>
								<!-- Widget: user widget style 1 -->
								<div class="box box-widget widget-user-2">
									<!-- Add the bg color to the header using any of the bg-* classes -->
									<div class="widget-user-header bg-yellow">
										<div class="widget-user-image">

											<i class="ion ion-ios7-briefcase"></i>
										</div>
										<!-- /.widget-user-image -->
										<h3 class="widget-user-username" id="longestUserName"></h3>

									</div>
									<div class="box-footer no-padding">
										<ul class="nav nav-stacked">
											<li><a href="#"><b>The longest introduction goes
														to this user.</b> <span id="schoolBubbleCount"></span></a></li>
											<li><a href="#">Content: <span
													class="pull-right badge bg-aqua" id="longestContent"></span></a></li>
										</ul>
									</div>
								</div>
								<!-- /.widget-user -->
							</div>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->

			<div class="row">
				<div class="col-lg-12" style="overflow: auto">
					<div class="box box-warning">
						<div class="box-header with-border">
							<h3 class="box-title">Age distribution here</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>
							<!-- /.box-tools -->
						</div>
						<!-- /.box-header -->
						<div class="box-body" id="ageWrapper">

							<div id="ageStat"></div>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->

			<div class="row">
				<div class="col-lg-12">
					<div class="box box-warning">
						<div class="box-header with-border">
							<h3 class="box-title">Gender graph goes here</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>
							<!-- /.box-tools -->
						</div>
						<!-- /.box-header -->
						<div class="box-body">
							<div class="col-lg-8 black-curtain" style="overflow: auto">

								<div id="schoolDist" class="bubbleZone"></div>
							</div>
							<!-- 
							<div class="col-lg-4">
								<span>Name:</span> <span id="schoolBubbleText"></span> <span>Count:</span>
								<span id="schoolBubbleCount"></span>
							</div>
							 -->
							<div class="col-lg-4">
								<!-- Widget: user widget style 1 -->
								<div class="box box-widget widget-user-2">
									<!-- Add the bg color to the header using any of the bg-* classes -->
									<div class="widget-user-header bg-yellow">
										<div class="widget-user-image">

											<i class="ion ion-ios7-briefcase"></i>
										</div>
										<!-- /.widget-user-image -->
										<h3 class="widget-user-username" id="schoolBubbleText">Nadia
											Carmichael</h3>
										<!-- <h5 class="widget-user-desc">Lead Developer</h5> -->
									</div>
									<div class="box-footer no-padding">
										<ul class="nav nav-stacked">
											<li><a href="#"><b>Count:</b> <span
													id="schoolBubbleCount">31</span></a></li>
											<li><a href="#">Tasks <span
													class="pull-right badge bg-aqua">5</span></a></li>
											<li><a href="#">Completed Projects <span
													class="pull-right badge bg-green">12</span></a></li>
											<li><a href="#">Followers <span
													class="pull-right badge bg-red">842</span></a></li>
										</ul>
									</div>
								</div>
								<!-- /.widget-user -->
							</div>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->

			<div class="row">
				<div class="col-lg-12" style="overflow: auto">
					<div class="box box-warning">
						<div class="box-header with-border">
							<h3 class="box-title">Work distribution</h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>
							<!-- /.box-tools -->
						</div>
						<!-- /.box-header -->
						<div class="box-body">
							<div class="col-lg-8 black-curtain">
								<div id="workDist" class="bubbleZone"></div>
							</div>
							<!-- ./col -->
							<div class="col-lg-4">
								<!-- small box -->
								<div class="small-box bg-red infoBox">
									<div class="inner">
										<h3 id="workBubbleCount"></h3>
										<span id="workBubbleText"></span>
									</div>
									<div class="icon">
										<i class="ion ion-social-reddit-outline"></i>
									</div>
									<a href="#" class="small-box-footer"> More info <i
										class="fa fa-arrow-circle-right"></i>
									</a>
								</div>


							</div>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->


		</div>
		<!-- /.container-fluid -->
	</div>
	<!-- /#page-wrapper -->

	<content tag="javascript"> <g:javascript
		src="toolbox/bootstrap.js" /> <g:javascript
		src="toolbox/bootstrap-notify.js" /> <g:javascript
		src="udf/notice.js" /> <g:javascript src="udf/gradient.js" /> <g:javascript
		src="udf/stripePieChart.js" /> <script type="text/javascript"
		src="http://cdnjs.cloudflare.com/ajax/libs/lodash.js/3.1.0/lodash.min.js"></script>
	<script type="text/javascript"
		src="http://cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
	<script type="text/javascript"
		src="http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.4/highlight.min.js"></script>
	<g:javascript src="toolbox/d3.timeseries.js" /> <g:javascript
		src="toolbox/d3map.js" /> <g:javascript src="udf/promises.js" /> <script
		src="http://d3js.org/queue.v1.min.js"></script> <script
		src="http://d3js.org/topojson.v1.min.js"></script> <g:javascript
		src="udf/overlap.js" /> <!-- bubble chart for work and education -->
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
	<g:javascript src="udf/bubbleChart.js" /> <!-- tree map of age --> <g:javascript
		src="udf/treeMap.js" /> <!-- introduction related --> <g:javascript
		src="toolbox/wordcloud2.js" /> <g:javascript
		src="udf/drawWordCloud.js" /> <script>
			function asynchroStat() {
				console.log("Fetching forged data for stats display");
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroStats",
					dataType : "json",
					async : true
				}).done(function(jsonData) {
					//result=['all':allCount,'gender':gender,'age':age,'geo':geo,'tag':tags,'edu':edu,'work':work,'authCount':authCount,'authList':authList,'intro':introKeywords,'introLong':longestIntro];
					console.log("Got stats");
					console.dir(jsonData);
					var geo = jsonData["geo"];
					console.log("Draw map");
					console.log(geo);
					drawChinaMap(geo);
					var gender = jsonData["gender"];
					console.log("Draw gender map");
					drawGender(gender);
					//work 
					console.log("Draw work distribution");
					var work = jsonData["work"];
					drawBubbleChart("#workDist", work);
					var workTable = generateTable(work);
					bubbleClick(workTable, "#workDist", "work");
					//education
					console.log("Draw school distribution");
					var edu = jsonData["edu"];
					drawBubbleChart("#schoolDist", edu);
					var schoolTable = generateTable(edu);
					bubbleClick(schoolTable, "#schoolDist", "school");
					//age
					var age = jsonData["age"];
					drawTreeMap(age, "#ageStat");
					//intor
					var intro = jsonData["intro"];
					introKeywords(intro);
					var introLong = jsonData["introLong"];
					showLongIntro(introLong);
				});
			}
			function introKeywords(intro) {
				//convert the data to desired format
				var arr = wordCloudFormat(intro);
				var zone = document.getElementById('wordCloudCanvas');
				drawWordCloud(arr, zone);
			}
			function showLongIntro(introLong) {
				var nameZone = document.getElementById("longestUserName");
				var contentZone = document.getElementById("longestContent");
				nameZone.innerHTML = introLong["user"];
				contentZone.innerHTML = introLong["content"];
			}
			function drawGender(data) {
				//getMapData();
				var mz = document.getElementById("maleZone");
				var fz = document.getElementById("femaleZone");
				var nz = document.getElementById("newZone");
				var width = 400;
				var height = 100;
				var male = data["male"];
				var female = data["female"];
				var unknown = data["unknown"];
				var all = male + female + unknown;
				var gendered = male + female;
				console.log("Male: " + male + " Female: " + female
						+ " Unknown: " + unknown);
				//set the text
				document.getElementById("maleCount").innerHTML = male;
				document.getElementById("femaleCount").innerHTML = female;
				document.getElementById("unknownCount").innerHTML = unknown;
				document.getElementById("allCount").innerHTML = all;
				//draw the image repetitively
				makePeople(nz, male, female);

				//genderRatio(male, female, mz, fz, width, height);
			}

			//return a list, each element of which is a list containing all parts of a pie chart
			function transformData(data) {
				var tags = data["tags"]; // weibo count under each category
				var all = data["users"];//all user weibo count
				var array = [];
				for ( var user in all) {
					if (all.hasOwnProperty(user)) {
						console.log("For user " + user);
						//find the user's allocation under each tag
						for ( var tag in tags) {
							if (tags.hasOwnProperty(tag)) {
								//console.log("Find info of"+user+" under "+tag);
								//console.log("tag is");
								var tagInfo = tags[tag];
								//console.log(tagInfo);
								var cata = tagInfo[user];
								console.log(user + " has allocation under tag "
										+ tag + " : " + cata);
								if (cata) {
									console.log("add sth");
									array.push({
										"label" : tag,
										"count" : cata
									});
									console.log({
										"label" : tag,
										"count" : cata
									});
								}
							}
						}
					}
				}
				return array;
			}
			function stripePieFormat(data) {
				var c = 1;
				var list = [];
				for ( var i in data) {
					var d = data[i];
					var obj = {};
					obj.index = c / 10;
					c++;
					obj.value = d.count / 100;
					obj.name = d.label;
					list.push(obj);
				}
				return list;
			}
			function asynchroNewPie() {
				console.log("Cluster fired for new pie chart");
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroClusters",
					dataType : "json",
					async : true
				}).done(function(jsonData) {
					console.log("Cluster completed");
					console.log(jsonData);
					//var zone=document.getElementById("clusterData").innerHTML=jsonData;
					console.log("Need to format data");
					var massage = transformData(jsonData);
					console.log("Result of data massaging");
					console.log(massage);
					var data = stripePieFormat(massage);
					console.log("Data for stripe pie chart");
					console.log(data);
					newPie(data);
					//activate popover
					$('[data-toggle="popover"]').popover({
						trigger : "hover"
					});

				});
			}
			function asynchroMap() {
				console.log("Cluster fired for a map of China");
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroMap",
					dataType : "json",
					async : true
				}).done(function(jsonData) {
					console.log("Got data for map");
					console.log(jsonData);
					drawChinaMap(jsonData);
				});
			}

			function promiseTest() {
				//create 3 promises and activate the last when 3 are finished
				function delayedHello(name, delay, callback) {
					setTimeout(function() {
						console.log("Hello, " + name + "!");
						callback(null);
					}, delay);
				}
				var i = 1;
				function getNumber() {
					var num = i;
					i++;
					return num;
				}
				queue().defer(getNumber).defer(getNumber).await(
						function(error, a, b) {
							if (error)
								throw error;
							console.log(a + " " + b);
						});
			}

			asynchroNewPie();
			window.onload = function() {
				var domEl = 'timeseries';
				var data = [ {
					'value' : 1380854103662
				}, {
					'value' : 1363641921283
				} ];
				var brushEnabled = true;
				timeseries(domEl, data, brushEnabled);
			}
			//asynchroMap();
			//asynchroMapData();
			asynchroStat();
		</script> </content>
</html>
