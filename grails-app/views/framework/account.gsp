<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<meta name="layout" content="lte_layout">
<title>General Information</title>
<style>
.progress {
	width: 80%;
	margin: 15px;
}

.progress-value {
	align: center;
	margin: 15px auto;
}

#contentPanel {
	width: 100%
}

#weiboPhoto {
	float: right;
	width: 50%;
}

#weiboInfo {
	width: 50%;
}

.panel-body .alert {
	width: 70%;
	min-width: 300px;
}
/*
	.outer-panel {
		width: 80%;
		min-width: 500px;
	}
	*/
.imageContainer {
	margin-left: auto;
	margin-right: auto;
}

.imageContainer a img {
	display: block;
	margin-left: auto;
	margin-right: auto;
}

.infoZone {
	margin-top: 3px;
	margin-bottom: 3px;
}
</style>
<!-- Liquid gauge -->
<style>
.liquidFillGaugeText {
	font-size: 30px;
	font-family: Helvetica;
	font-weight: bold;
}
</style>
<!-- log zone -->
<style>
.logHolder {
	overflow: auto;
	height: 100%;
	max-height:900px;
}
</style>
<!-- progress monitor -->
<style>
.statusBar {
	width: 40%;
	float: right;
}

.code-danger {
	color: red;
}

.code-warning {
	/*color: yellow;*/
	color: hsl(53, 84%, 62%);
}

.code-info {
	color: blue;
}

.code-success {
	color: green;
}
</style>
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->

</head>

<body>
	<!-- Page Content -->
	<div id="page-wrapper" class="content-wrapper">
		<section class="content-header">
			<h1 class="page-header">General Information</h1>
		</section>
		<div class="container-fluid">

			<div class="row">
				<!--  Main Content -->
				<div class="col-lg-6">

					<div class="box box-primary">
						<div class="box-header with-border">
							<h4>Basic Information</h4>
						</div>
						<div class="box-body" id="contentPanel">
							<div id="weiboPhoto" class="col-lg-4">
								<div class="well well-sm" style="width: 200px; height: 90%;">
									<h4 style="text-align: center">Profile photo:</h4>
									<br />
									<div class="imageContainer">
										<a id="userImageUrl"> <img id="userImage"
											alt="User's profile photo" height="100" width="100">
										</a>
									</div>
								</div>
							</div>
							<div id="weiboInfo" class="col-lg-6">
								<table class="table table-bordered table-hover">
									<tr>
										<td><b>User name:</b></td>
										<td id="userName"></td>
									</tr>
									<tr>
										<td><b>User ID:</b></td>
										<td id="userId"></td>
									</tr>
									<tr>
										<td><b>Weibo user url:</b></td>
										<td id="userUrl"></td>
									</tr>
								</table>
							</div>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-6 -->

				<div class="col-lg-6">
					<div class="box box-primary outer-panel">
						<div class="box-header with-border">
							<h4>Statistic</h4>
						</div>
						<div class="box-body" id="weiboStats">
							<!-- hover data table -->
							<table class="table table-bordered table-hover">
								<col width=40%>
								<col width=60%>
								<tbody>
									<tr>
										<td>Weibo Count:</td>
										<td id="weiboCountZone"></td>
									</tr>
									<tr>
										<td>Following:</td>
										<td id="followingZone"></td>
									</tr>
									<tr>
										<td>Fans:</td>
										<td id="fansZone"></td>
									</tr>
									<tr>
										<td>Grouping:</td>
										<td id="groupingZone"></td>
									</tr>
									<tr>
										<td>Mentioned:</td>
										<td id="atZone"></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-6 -->
			</div>
			<!-- /.row -->

			<div class="row">
				<div class="col-lg-12">
					<div class="box box-primary">
						<div class="box-header with-border">
							<h4>Job Status Monitor</h4>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>
							<!-- /.box-tools -->
						</div>
						<!-- End of box-header -->
						<div class="box-body">

							<div class="col-lg-4">
								<div class="row">
									<div id="gauge">
										<svg id="fillgauge1" width="90%"
											onclick="gauge1.update(NewValue());"></svg>
									</div>
								</div>
								<!-- /.row -->
								<!-- progress monitor -->
								<div class="row">
									<ul class="todo-list">
										<li>
											<!-- drag handle --> <span class="handle"> <i
												class="fa fa-ellipsis-v"></i> <i class="fa fa-ellipsis-v"></i>
										</span> <!-- todo text --> <span class="text">Preparation</span> <code
												id="crawlerStatus" class="code-danger">Not Started</code>
											<div id="knowledgeStatus" class="infoZone"></div>
										</li>
										<li>
											<!-- drag handle --> <span class="handle"> <i
												class="fa fa-ellipsis-v"></i> <i class="fa fa-ellipsis-v"></i>
										</span> <!-- todo text --> <span class="text">Keyword Study</span> <code
												id="keywordStatus" class="code-danger">Not Started</code>
										</li>

										<li>
											<!-- drag handle --> <span class="handle"> <i
												class="fa fa-ellipsis-v"></i> <i class="fa fa-ellipsis-v"></i>
										</span> <!-- todo text --> <span class="text">Friend
												Statistics</span> <code id="statStatus" class="code-warning">In-progress</code>
										</li>
										<li>
											<!-- drag handle --> <span class="handle"> <i
												class="fa fa-ellipsis-v"></i> <i class="fa fa-ellipsis-v"></i>
										</span> <!-- todo text --> <span class="text">User Interaction</span>
											<code id="interactionStatus" class="code-warning">In-progress</code>
										</li>
										<li>
											<!-- drag handle --> <span class="handle"> <i
												class="fa fa-ellipsis-v"></i> <i class="fa fa-ellipsis-v"></i>
										</span> <!-- todo text --> <span class="text">Topics Study</span> <code
												id="clusterStatus" class="code-success">In-progress</code>
										</li>
										<li>
											<!-- drag handle --> <span class="handle"> <i
												class="fa fa-ellipsis-v"></i> <i class="fa fa-ellipsis-v"></i>
										</span> <!-- todo text --> <span class="text">Timeline Study</span> <code
												id="timelineStatus" class="code-info">In-progress</code>
										</li>
										<li>
											<!-- drag handle --> <span class="handle"> <i
												class="fa fa-ellipsis-v"></i> <i class="fa fa-ellipsis-v"></i>
										</span> <!-- todo text --> <span class="text">Crawler</span> <code
												id="deepCrawlerStatusSpan" class="code-danger">Not
												Started</code> <!-- Emphasis label --> <small id="crawlerTimeZone"
											class="label label-danger pull-right"><i
												class="fa fa-clock-o"></i><span id="crawlerTimeTag">
													2 mins</span></small>
											<div id="crawlerEstimation"></div>
											<div class="progress progress-xs">
												<div id="deepCrawlStatusBar"
													class="progress-bar progress-bar-warning progress-bar-striped"
													role="progressbar" aria-valuenow="60" aria-valuemin="0"
													aria-valuemax="100" style="width: 60%">
													<span class="sr-only">60% Complete (warning)</span>
												</div>
											</div>
											<div id="deepCrawlStatusSpan"></div>
										</li>
									</ul>
								</div>
							</div>
							<!-- /.col-4 -->

							<div class="col-lg-8">
								<div class="box box-solid">
									<div class="box-header with-border">
										<i class="fa fa-text-width"></i>
										<h3 class="box-title">Log zone</h3>
									</div>
									<!-- /.box-header -->
									<div class="box-body logHolder">
										<dl id="logZone">
											<dt>This is a place receiving the logs from the backend
												and displaying them.</dt>
											<dd>The logs show the current status of the job running
												in the backend.</dd>
										</dl>
									</div>
									<!-- /.box-body -->
								</div>
								<!-- /.box -->
							</div>
							<!-- ./col-lg-8 -->
						</div>
						<!-- /.col-lg-12 -->
					</div>
					<!-- /.col-lg-12 -->
				</div>
				<!-- /.row -->

			</div>
			<!-- /.container-fluid -->
		</div>
		<!-- /#page-wrapper -->


		<content tag="javascript"> <g:javascript
			src="udf/progressBar.js" /> <g:javascript src="udf/drawInfo.js" />
		<g:javascript src="toolbox/d3.liquidGauge.js" /> <script>
			function asynchroCrawl() {
				console.log("Trigger the work flow with data pre-crawl.");
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroCrawl",
					dataType : "json",
					async : true,
					complete : function(xhr, textStatus) {
						console.log("Work flow is complete.");
					},

					success : function(data, textStatus, xhr) {
						console.log("Work flow is successful.");
					},
					error : function(xhr, status, error) {
						errorNotice("User info");
					}

				});
			}
			function asynchroCrawlerMonitor() {
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroCrawlerStatus",
					dataType : "json",
					async : true
				//error handling is disabled
				}).done(function(jsonData) {
					console.log("Got crawler status");
					console.log(jsonData);
					if (painted == true || painted == 'true') {
						console.log("No need to draw the status.");
						updateCrawlStatus(jsonData);
					} else {
						console.log("Start to display user info");
						if (drawnInfo == false) {
							drawnInfo = showUserInfo(jsonData);
						}
						if (drawnTime == false) {
							drawnTime = predictTime(jsonData);
						}
						if (drawnInfo && drawnTime) {
							console.log("Finished displaying user info");
							painted = true;
						} else {
							console.log("User info displaying failed.");
						}
					}

				});
			}

			function asynchroUser() {
				console.log("request user info");
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroUser",
					dataType : "json",
					async : true,
					error : function(xhr, status, error) {
						errorNotice("User info");
					}
				}).done(function(jsonData) {
					console.log(jsonData);
					showUser(jsonData);
				});
			}
			function addText(zone, text) {
				var text = document.createTextNode(text);
				zone.appendChild(text);
			}
			function showUser(jsonData) {
				var name = jsonData["userName"];
				var id = jsonData["userId"];
				var url = jsonData["userUrl"];
				var face = jsonData["userFace"];
				addText(document.getElementById("userName"), name);
				addText(document.getElementById("userId"), name);
				addText(document.getElementById("userUrl"), url);
				//generate image
				var a = document.getElementById("userImageUrl");
				console.log(a);
				a.href = url;
				var i = document.getElementById("userImage");
				i.src = face;

			}
			function showLog(logs) {
				console.log("Got logs from back end");
				console.log(logs);
				var logZone = document.getElementById("logZone");
				for (var i = 0; i < logs.length; i++) {
					var dd = document.createElement('dd');
					dd.innerHTML = logs[i];
					logZone.appendChild(dd);
				}
			}
			function asynchroLog() {
				console.log("request user info");
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroLog",
					dataType : "json",
					async : true,
					error : function(xhr, status, error) {
						errorNotice("Error getting log");
					}
				}).done(function(jsonData) {
					showLog(jsonData);
				});
			}
		</script> <!-- liquid gauge --> <script>
			function drawProgressGauge() {
				var gauge1 = loadLiquidFillGauge("fillgauge1", 55);
				var config1 = liquidFillGaugeDefaultSettings();
				config1.circleColor = "#FF7777";
				config1.textColor = "#FF4444";
				config1.waveTextColor = "#FFAAAA";
				config1.waveColor = "#FFDDDD";
				config1.circleThickness = 0.2;
				config1.textVertPosition = 0.2;
				config1.waveAnimateTime = 1000;
			}
			drawProgressGauge();
		</script> <script>
			//flags
			var crawlFinished = false;
			var keywordFinished = false;
			var relationFinished = false;
			var networkFinished = false;
			var clusterFinished = false;

			// for user's forward and comment related data
			var relatedData;
			asynchroCrawl();
			//user info
			asynchroUser();
			//monitor deep crawler status
			var painted = false;
			var drawnTime = false;
			var drawnInfo = false;
			asynchroCrawlerMonitor();
			setInterval(function() {
				asynchroCrawlerMonitor();
			}, 5000);
			setInterval(function() {
				asynchroLog();
			}, 20000);
		</script> </content>
</body>

</html>
