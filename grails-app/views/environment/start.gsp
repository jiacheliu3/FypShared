<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
 <meta charset="UTF-8"> 
<g:set var="entityName"
	value="${message(code: 'environment.label', default: 'Environment')}" />
<title><g:message code="default.create.label"
		args="[entityName]" /></title>
<%@ page import="codebigbrosub.User"%>
<%@ page import="java.util.Map"%>
<%@ page import="codebigbrosub.EnvironmentController.KeywordWrapper"%>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
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
  stroke: #fff;
  stroke-opacity: .6;
  stroke-width:3px;
}

.node text {
  pointer-events: none;
  font: 14px sans-serif;
}
.node circle {
  fill: #99ccff;
  stroke: #fff;
  stroke-width: 1.5px;
} 
button{
	-webkit-box-shadow:rgba(0,0,0,0.0.1) 0 1px 0 0;
	-moz-box-shadow:rgba(0,0,0,0.0.1) 0 1px 0 0;
	box-shadow:rgba(0,0,0,0.0.1) 0 1px 0 0;
	background-color:#5B74A8;
	border:1px solid #29447E;
	font-family:'Lucida Grande',Tahoma,Verdana,Arial,sans-serif;
	font-size:12px;
	font-weight:700;
	padding:2px 6px;
	color:#fff;
	border-radius:5px;
	-moz-border-radius:5px;
	-webkit-border-radius:5px;
	min-height:40px;
	min-width:40px;
	padding:5px 2px;
}
div[class~="progress-bar"]{
	height:40px;
	margin:10% 3%;
	
}
.fakeContent{
	vertical-align: middle;
	align:center;
}
.panel{
	border-style:solid;
	border-width:1px;
	margin:10px 5px;
	border-color:#2e8853;
	padding:5px;
}
</style>

<g:javascript src="wordcloud2.js" />
<g:javascript src="d3.js"/>

<g:javascript src="progressBar.js" />
	<g:javascript src="drawNetwork.js" />
	<g:javascript src="drawRelation.js"/>
	<g:javascript src="drawWordCloud.js"/>
	
</head>
<body>
	<%
		User u=(User)request.getAttribute("user");
		//println("keywords: "+u.keywords);
		//println("forwarding: "+u.forwarding);
		//println("forwarded: "+u.forwarded);
		//println("commenting: "+u.commented);
		//println("commented: "+u.commented);
		Map simMap=(Map)request.getParameter("similar");
		//println(simMap);
		
		
		
	 %>
	 
	<div id="content">
		<div>Here are the details of the user you are looking for:</div>
		<div class="interval"></div>

		<div>

			Personal information:
			<p>Name:</p>
			<p>Age:</p>

		</div>
		<div class="interval"></div>
		<div>

			Information digged out of Weibo.com:
			<div id="weiboUserName">
				<div id="weiboPhoto">
					Profile photo: <br /> <a href=${u?.url}><img src=${u?.faceUrl}
						alt="User's profile photo not found"
						onError="/CodeBigBroSub/images/user_not_found.jpeg" height="100"
						width="100"></a>
				</div>
				<p>
					User name:<b> ${u?.weiboName}
					</b>

				</p>
				<p>
					User ID:<b> ${u?.weiboId}
					</b>
				</p>

				<p>
					Weibo user url:<a href=${u?.url}> ${u.url }</a>
				</p>
			</div>
			<div id="weiboKeywords" class="panel">
				<p><h3>User keywords(keywords extracted from user's microblogs):</h3></p>
				<!-- 			<g:each in="${u?.keywords}">Keyword:${it.key } Weight:${it.value }</g:each>			 -->
				<div id='keywordContentDiv' class='realContent'>
					<div id='keywordCanvas'>
						<canvas id="wordCloudCanvas" width="900" height="400"
							style="border: 1px solid #000000;"></canvas>
						
					</div>
				</div>
				<div id='keywordProgressDiv' class='fakeContent' style='width:900px;height:400px;'>
					<div style="width:850px">
					<div class="progress-bar progress-bar-striped active" id="keywordProgressBar" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
			    	<span class="sr-only">45% Complete</span>
			    	</div>
				</div>
			
				<div class="interval"></div>
			</div>
			<div id="weiboRelationship" class="panel"> 
				<p><h3>Here's the user's networking on weibo.com</h3></p>
				<div id='relationPanel'>
					<div id='relationContentDiv' class='realContent' style='width:900px;height:250px;'>
						
						<p>User has forwarded their weibo:</p>
						<div id="fing"></div>
						<br/>
						<p>They have forwarded user's weibo: </p>
						<div id="fed"></div>
						<br/>
						<p>User has comments from them:</p>
						<div id="ced"></div>
						<br/>
						<p>User has commented their weibo: </p>
						<div id="cing"></div>
						<br/>
					</div>
					<div id='relationProgressDiv' class='fakeContent' style='width:900px;height:250px;'>
						<div style="width:850px">	
						<div class="progress-bar progress-bar-striped active" id="relationProgressBar" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
			    		<span class="sr-only">45% Complete</span>
			    		</div>
					</div>
				</div>
			</div>
			<div id="weiboTags">
				<p><h3>Tags of user:</h3></p>
				<div>
					<h4>Add tags of user here</h4>
				</div>
				<div id="tagNetwork" ></div>
				<a href="/CodeBigBroSub/tag">All available tags</a>
			</div>
			<div class="interval"></div>
			<div id='usersData'></div>
			<div class="interval"></div>
			<div><p><h3>User's Network</h3></p> </div>
			<div id='networkPanel' class="panel">
				<div id='networkContentDiv' class='realContent'>
					<div id='networkGraph'>
						<div id="buttons"></div>
						<div id="graphCanvas"></div>
						<div id="result">Studying user's network...</div>
					</div>
					<button onclick="clearGraph()" type="button" value="Die!" style="height:40px;">Clear the Graph</button>
					<button onclick="drawEntrance();" type="button" value="Create" style="width:40px;height:40px;">Draw</button>
				</div>
				<div id='networkProgressDiv' class='fakeContent' style='height:500px;width:900px;'>
					<div style="width:850px">	
					<div class="progress-bar progress-bar-striped active" id="networkProgressBar" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
			    	<span class="sr-only">45% Complete</span>
			    	</div>
				</div>
			</div>
			<div class="interval"></div>
		</div>
	</div>
	<script>
	//asynchronization
	function asynchroWordCloud() {
		// show a progress bar in the zone
		initProgressBar('keyword');
		startBootstrapProgressBar('keyword',30);
		//startProgressBar('keyword',30);
		var jsonData = $.ajax(
						{
							url : "${createLink(controller:'environment', action:'asynchroKeywords')}",
							dataType : "json",
							async : false
						}).done(function(jsonData) {
					var tagged = true;// ok to study the network
					

					var list = jsonData;

					console.log(list);
					var map = list;
					// var map = JSON.parse(list.replace(/&quot;/g, '"'));
					console.log(map);

					var arr = [];
					for ( var e in map) {
						console.log(e, map[e]);
						arr.push([ e, map[e] ]);
					}

					console.log("start scaling");
					// var result=scale(map);
					// var result=map;
					console.log("List of keywords is" + arr);
					console.log("start drawing");
					WordCloud(document.getElementById('wordCloudCanvas'), {
						list : arr,
						weightFactor : 50,
						rotateRatio : 0,
						backgroundColor : '#ccccff',
						color : '#ffffff'
					});
					console.log("drawn");
					keywordFinished = true;
					console.log("Keyword finished, fire network");
					if (relationFinished) {
						console.log("Last job finished, fire network");
						// go on with network
						asynchroNetwork();
					}
					console.log("Stop progress bar");
					//stopProgressBar('keyword');
					stopBootstrapProgressBar('keyword');
				});
	}
	function asynchroRelation() {
		// show a progress bar in the zone
		initProgressBar('relation');
		//startProgressBar('relation',300);
		startBootstrapProgressBar('relation',300);
		var jsonData = $.ajax(
						{
							url : "${createLink(controller:'environment',action:'asynchroRelation')}",
							dataType : "json",
							async : true
						}).done(function(jsonData) {
					console.log("relationship:");
					console.log(jsonData);
					relatedData = jsonData;
					drawRelation();

					relationFinished = true;
					// stop progress bar and show
					//stopProgressBar('relation');
					stopBootstrapProgressBar('relation');
					console.log("Relation finished, fire network");
					console.log("Status of keyword is "+keywordFinished);
					if (keywordFinished == true) {
						console.log("Last job finished, fire network");
						asynchroNetwork();
					}
				});
	}
	function asynchroNetwork() {
		console.log("Check job status: " + keywordFinished + ", "
				+ relationFinished);

		console.log("Network fired");
		var jsonData = $.ajax(
						{
							url : "${createLink(controller:'environment', action:'asynchroNetwork')}",
							dataType : "json",
							async : true
						}).done(function(jsonData) {

					console.log(jsonData);
					// console.log(jsonText);
					document.getElementById("result").innerHTML = "Done";
					console.log(jsonData.all);

					// set global variable
					similar = jsonData;
					console.log("The object got");
					// console.log(similar);
					checkGetData();
					drawEntrance();
					// finially able to stop progress bar
					//stopProgressBar("network");
					stopBootstrapProgressBar("network");
				});
	}
	function hangOnTillNetworkReady() {
		initProgressBar('network');
		startBootstrapProgressBar("network",3000);
	}
			
	</script>
	<script>
	//set all progress bars visible and real content invisible
	$('.realContent').css('display', 'none');
	$('.fakeContent').css('display', 'block');
	//flags
	var relationFinished=false;
	var keywordFinished=false;
	// for user's forward and comment related data
	var relatedData;
	asynchroWordCloud();
	asynchroRelation();
	hangOnTillNetworkReady()


	

	
	</script>
</body>
</html>
