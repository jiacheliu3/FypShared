<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<!-- Apply layout -->
<meta name="layout" content="lte_layout">
<title>Keyword Study</title>

<style type="text/css">
div[class~="progress-bar"] {
	width: 600px;
	height: 40px;
	margin: 10% 3%;
}

.fakeContent {
	vertical-align: middle;
	align: center;
}

button {
	margin: 5px 2px;
}

svg {
	display: block;
	margin: auto;
}

canvas {
	display: block;
	margin: auto;
}

.keywords {
	width:70px;
	height:35px;
	/*outline: #FFFFFF solid 2px;*/ 
	font-size =1em;
	padding:0px;
	color: #222d32;
	font-weight: bold;
}

.keywords:hover {
	color: #222d32;
	outline: #3c8dbc solid 2px;
	/*background-color: #3c8dbc;*/
}

#keywordCanvas {
	overflow: auto;
}
#buttonPanel{
	width:920px;
	margin:auto;
	overflow:auto;
}
.btn-topped{
	overflow:visible;
	
	margin:2px 1px 0;
	
	background-color:#fff;
	border-style: solid;
	border-width:7px 0 0;
    /*
    border-right-color:rgba(161,161,161,0.5);
    border-left-color:rgba(161,161,161,0.5);
    border-bottom-color:rgba(161,161,161,0.5);
	*/
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
			<h1 class="page-header">User Keyword Study</h1>
		</section>
		<div class="container-fluid">
			<div class="row">
				<div class="col-lg-12">
					<!-- Progress bar zone -->
					<!-- 
					<div id='keywordProgressDiv' class='fakeContent'
						style='width: 900px;'>
						<div class="progress" id="keywordProgressZone">
							<div class="progress-bar progress-bar-striped active"
								id="keywordProgressBar" style="width: 0%;">
								<span class="sr-only">60% Complete</span>
							</div>
						</div>
						<div class="panel-body" id="keywordSuccessMessage"
							style="display: none;">
							<div class="alert alert-success alert-dismissable">
								Work Complete
							</div>
						</div>
					</div>
					 -->

					<!-- Main Content -->
					<!-- 
					<div class="panel panel-default" id="keywordList">
					 
						<div class="panel-heading">Keyword List</div>
						<div class="panel-body" id="buttonPanel"></div>

					</div>
					-->
					<div class="box box-primary" id="keywordList">
						<div class="box-header with-border">
							<h4>Keyword List</h4>
						</div>
						<div class="box-body" id="buttonPanel" ></div>
					</div>
					<!-- 
					<div class="panel panel-default" id="keywordCanvas">
						<div class="panel-heading">Word Cloud</div>
						<div class="panel-body">

							<div id='keywordContentDiv' class='realContent'>
								<div id='keywordCanvas'>
									<canvas id="wordCloudCanvas" width="875" height="600"></canvas>

								</div>
							</div>

						</div>

					</div>
					 -->
					<div class="box box-primary" id="keywordCanvas">
						<div class="box box-header">
							<h4>Word Cloud</h4>
						</div>
						<div class="box-body">

							<div id='keywordContentDiv' class='realContent'>
								<div id='keywordCanvas'>
									<canvas id="wordCloudCanvas" width="875" height="600"></canvas>
								</div>
							</div>
						</div>
					</div>

				</div>
				<!-- /.row -->
			</div>
			<!-- /.container-fluid -->
		</div>
		<!-- /#page-wrapper -->

	</div>


	<content tag="javascript"> 
	<g:javascript src="toolbox/wordcloud2.js" /> 
	<g:javascript src="udf/asynchro.js" />
	<g:javascript src="udf/progressBar.js" /> 
	<g:javascript src="udf/drawWordCloud.js" /> 
	<g:javascript src="udf/gradient.js" /> 
	<script>
		//generate buttons in the zone
		function generateButtons(map){
			var contents=[];
			//generate the content of buttons
			for(var i in map) {
			    if (map.hasOwnProperty(i)) {
			        //generate a button
			        var panel=document.getElementById("buttonPanel");
			        var s=String(map[i]);
			        var parts=s.split(',');
			        contents.push(parts[0]);
			    }
			}
			//generate colors
			//var colors=makeColors(100);
			var colors=makeColorsInRange(241,97,50,189,28,80,map.length);
			var fontColors=makeBlackAndWhiteColors(0,100,map.length);
			var fontSize=breakRange(16,8,map.length);
			//re-order colors
			colors=orderBy12(colors);
			//makeButtonWithColor(panel,contents,colors,fontColors,fontSize);
			makeHattedButton(panel,contents,colors,fontSize);
			
			//pass to painter function
			//makeButtons(panel,contents);
		}
		
		//asynchronization
		function asynchroKeywords() {
			//startBootstrapProgressBar('keyword');
			var target="/CodeBigBroSub/environment/asynchroKeywords";
				console.log(target);
				var jsonData = $.ajax({
					url : target,
					dataType : "json",
					async : true
				}).done(function(jsonData) {
					var tagged = true;// ok to study the network
					var keywordFinished = true;

					//scale the data before converting into wordcloud required form
					var list = jsonData;
					//convert the data to desired format
					var arr=wordCloudFormat(list);
					//generate the buttons
					generateButtons(arr);

					var zone=document.getElementById('wordCloudCanvas');
					drawWordCloud(arr,zone);
					
					keywordFinished = true;
					console.log("Stop progress bar");
					//stopBootstrapProgressBar('keyword');
				});
			}
		</script> 
		<script type="text/javascript">
			//flags
			var relationFinished = false;
			var keywordFinished = false;
			// for user's forward and comment related data
			var relatedData;
			console.log("Start keyword call");

			asynchroKeywords();
		</script> </content>
</body>

</html>
