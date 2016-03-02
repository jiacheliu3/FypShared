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
				<div class="col-lg-12">

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
				<!-- /.col-12 -->
			</div>
			<!-- /.row -->

			<div class="row">
				<!--  Main Content -->
				<div class="col-lg-12">

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
							<!-- 
    							<ul>
    								<li id="weiboCountZone"><span class="col-lg-3">Weibo
    									Count:</span></li>
    									<li id="followingZone"><span class="col-lg-3">Following:</span>
    									</li>
    									<li id="fansZone"><span class="col-lg-3">Fans:</span></li>
    									<li id="groupingZone"><span class="col-lg-3">Grouping:</span>
    									</li>
    									<li id="atZone"><span class="col-lg-3">Mentioned:</span></li>
    								</ul>
    								 -->
						</div>
					</div>
					<!-- /.box -->

					<!-- outer-panel-->
					<div class="box box-primary outer-panel">
						<div class="box-header">
							<h4>Progress Monitor</h4>
						</div>
						<div class="box-body">
							<div class="box box-default">
								<div class="box-header">Preparation:</div>
								<div class="box-body">
									<div id="knowledgeStatus" class="infoZone"></div>
									<div id="crawlerStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->
							<div class="box box-default">
								<div class="box-header">Keyword Study:</div>
								<div class="box-body">
									<div id="keywordStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->
							<div class="box box-default">
								<div class="box-header">Relation Study:</div>
								<div class="box-body">
									<div id="relationStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->
							<div class="box box-default">
								<div class="box-header">Network Study:</div>
								<div class="box-body">
									<div id="networkStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->
							<div class="box box-default">
								<div class="box-header">Cluster Study:</div>
								<div class="box-body">
									<div id="clusterStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->
							<div class="box box-default">
								<div class="box-header">Deep Crawl</div>
								<div class="box-body">
									<div id="crawlerEstimation" class="infoZone"></div>
									<div class="alert alert-danger" id="deepCrawlStatus">Not
										yet started.</div>
									<div class="progress progress-striped active">
										<span id="deepCrawlStatusSpan" class="progress-value"></span>
										<div class="progress-bar progress-bar-success"
											role="progressbar" id="deepCrawlStatusBar" aria-valuenow="40"
											aria-valuemin="0" aria-valuemax="100" style="width: 0%">
											<span class="sr-only">40% Complete (success)</span>
										</div>
									</div>
								</div>
							</div>
							<!-- End of inner panel -->
						</div>
						<!-- End of panel body -->
					</div>
					<!-- End of outer panel -->

				</div>
				<!-- /.col-12 -->
			</div>
			<!-- /.row -->
		</div>
		<!-- /.container-fluid -->
	</div>
	<!-- /#page-wrapper -->


	<content tag="javascript"> 
	<g:javascript src="udf/progressBar.js" />
	<g:javascript src="udf/drawInfo.js" />
	<script>
	//asynchronization
	function asynchroWordCloud() {
		// show a progress bar in the zone
		//initProgressBar('keyword');
		//startBootstrapProgressBar('keyword',30);
		//startProgressBar('keyword',30);
		
		var jsonData = $.ajax(
		{
			url : "${createLink(controller:'environment', action:'asynchroKeywords')}",
			dataType : "json",
			async : false,
			error: function(xhr, status, error) {
				errorNotice("Word Cloud generation");
			}
		}).done(function(jsonData) {
			
			keywordFinished = true;
			console.log("Keyword finished, fire network");
					//stopBootstrapProgressBar("keyword");
					if (relationFinished) {
						console.log("Last job finished, fire network");
						// go on with network
						//asynchroNetwork();
					}
					
				});
	}
	function asynchroRelation() {
		// show a progress bar in the zone
		//initProgressBar('relation');
		//startProgressBar('relation',300);
		//startBootstrapProgressBar('relation',300);
		//document.getElementById("relationStatus").innerHTML="Relation study started.";
		var jsonData = $.ajax(
		{
			url : "${createLink(controller:'environment',action:'asynchroRelation')}",
			dataType : "json",
			async : true,
			error: function(xhr, status, error) {
				errorNotice("Interaction study");
			}
		}).done(function(jsonData) {

			console.log("Relation finished, fire network");
			relationFinished=true;
			console.log("Status of keyword is "+keywordFinished);
			if (keywordFinished == true) {
				console.log("Last job finished, fire network");
						//asynchroNetwork();
					}
				});
	}
	function asynchroNetwork() {

		console.log("Network fired");
		
		//document.getElementById("networkStatus").innerHTML="Network study now started.";
		var jsonData = $.ajax(
		{
			url : "${createLink(controller:'environment', action:'asynchroNetwork')}",
			dataType : "json",
			async : true,
			error: function(xhr, status, error) {
				errorNotice("Network study");
			}
		}).done(function(jsonData) {
			networkFinished=true;
			console.log("Network completed");
							//asynchroCluster();
						});
	}

	function asynchroCrawl(){
		//document.getElementById("crawlerStatus").innerHTML="Crawler is collecting user's microblogs for study. Please be patient.";
		var jsonData = $.ajax(
		{
			url : "${createLink(controller:'environment', action:'asynchroCrawl')}",
			dataType : "json",
			async : true,
			error: function(xhr, status, error) {
				errorNotice("Crawler");
			}
		}).done(function(jsonData) {
			console.log("Crawler completed");
			
			
			crawlFinished=true;
					//draw the received data
					//showCrawlerResult();
					
				});
	}
	function asynchroCrawlerMonitor(){
		

		var jsonData = $.ajax(
		{
			url : "${createLink(controller:'environment', action:'asynchroCrawlerStatus')}",
			dataType : "json",
			async : true
					//error handling is disabled
				}).done(function(jsonData) {
					console.log("Got crawler status");
					console.log(jsonData);
					if(painted==true||painted=='true'){
						console.log("No need to draw the status.");
						updateCrawlStatus(jsonData);
					}else{
						console.log("Start to display user info");
						if(drawnInfo==false){
							drawnInfo=showUserInfo(jsonData);
						}
						if(drawnTime==false){
							drawnTime=predictTime(jsonData);
						}
						if(drawnInfo&&drawnTime){
							console.log("Finished displaying user info");
							painted=true;
						}else{
							console.log("User info displaying failed.");
						}
					}
					
				});
			}
			function asynchroCluster() {
				console.log("Cluster fired");
		//document.getElementById("clusterStatus").innerHTML="Clustering in progress;";
		var jsonData = $.ajax(
		{
			url : "${createLink(controller:'environment', action:'asynchroClusters')}",
			dataType : "json",
			async : true,
			error: function(xhr, status, error) {
				errorNotice("Cluster");
			}
		}).done(function(jsonData) {
			console.log("Cluster completed");
			console.log(jsonData);
			clusterFinished=true;
							//var zone=document.getElementById("clusterData").innerHTML=jsonData;
							//report(jsonData);
							
						});
	}

	function asynchroUser(){
		console.log("request user info");
		var jsonData = $.ajax(
		{
			url : "${createLink(controller:'environment', action:'asynchroUser')}",
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
		</script> 
		<script>
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
		</script> 
		</content>
</body>

</html>
