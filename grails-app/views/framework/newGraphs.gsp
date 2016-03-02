<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>SB Admin 2 - Bootstrap Admin Theme</title>

<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'bootstrap.min.css')}"
	type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'timeline.css')}" type="text/css">

<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'font-awesome.min.css')}"
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
</style>
<!-- Forgeries -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'mylabels.css')}" type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'timeseries.css')}" type="text/css">
<g:javascript src="jquery.min.js" />


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
</style>
</head>

<body>

	<div id="wrapper">



		<!-- Page Content -->
		<div id="page-wrapper">
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
				
			</div>
			<!-- /.container-fluid -->
		</div>
		<!-- /#page-wrapper -->

	</div>
	<g:javascript src="bootstrap.js" />
	<g:javascript src="bootstrap-notify.js" />
	<g:javascript src="notice.js" />

	<g:javascript src="gradient.js" />
	<g:javascript src="d3.js" />
	<g:javascript src="stripePieChart.js" />
	<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/lodash.js/3.1.0/lodash.min.js"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.4/highlight.min.js"></script>
	<g:javascript src="timeseries.js" />
	<script>
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
		
		
		asynchroNewPie();
		window.onload = function() {
	          var domEl = 'timeseries';
	          var data = [{'value': 1380854103662},{'value': 1363641921283}];
	          var brushEnabled = true;
	          timeseries(domEl, data, brushEnabled);
	        }
	</script>
</html>
