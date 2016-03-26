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
	<title>User Interaction Study</title>
	<style>
	button {
		margin: 5px 2px;
	}

	.panel div {
		padding: 5px;
	}

	.panel-heading {
		margin: 0;
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
    			<h1 class="page-header">User Interaction Study</h1>
    		</section>
    		<div class="container-fluid">
    			<div class="row">
    				<!-- col-12 zone -->
    				<div class="col-lg-12">

    					<div class="col-lg-6">
    						<div class="box box-primary">
    							<div class="box-header with-border">
    								<h4>They have commented user's weibo</h4>
    								<div class="box-tools pull-right">
    									<button class="btn btn-box-tool" data-widget="collapse">
    										<i class="fa fa-minus"></i>
    									</button>
    								</div>
    								<!-- /.box-tools -->
    							</div>
    							<!-- End of box-header -->
    							<div class="box-body" id="commentTablePanel">
    								<table class="table table-condensed">
    									<thead>
    										<tr>
    											<th>User</th>
    											<th>Count</th>
    											<th>Ratio</th>
    										</tr>
    									</thead>
    									<tbody id="commentTableBody">
    									</tbody>
    								</table>
    							</div>
    							<!-- End of box body -->
    						</div>
    						<!-- End of box -->
    					</div>
    					<!--/.col-lg-6 -->
    					<div class="col-lg-6">
    						<div class="box box-primary">
    							<div class="box-header with-border">
    								<h4>They have reposted user's weibo</h4>
    								<div class="box-tools pull-right">
    									<button class="btn btn-box-tool" data-widget="collapse">
    										<i class="fa fa-minus"></i>
    									</button>
    								</div>
    							</div>
    							<div class="box-body" id="forwardTablePanel">
    								<table class="table table-condensed">
    									<thead>
    										<tr>
    											<th>User</th>
    											<th>Support</th>
    											<th>Ratio</th>
    										</tr>
    									</thead>
    									<tbody id="forwardTableBody">
    									</tbody>
    								</table>
    							</div>
    							<!-- End of box body -->
    						</div>
    						<!-- End of box -->
    					</div>
    					<!--/.col-lg-6 -->
    					<div class="col-lg-6">
    						<div class="box box-primary">
    							<div class="box-header with-border">
    								<h4>They have liked the user's weibo</h4>
    								<div class="box-tools pull-right">
    									<button class="btn btn-box-tool" data-widget="collapse">
    										<i class="fa fa-minus"></i>
    									</button>
    								</div>

    							</div>
    							<div class="box-body" id="likeTablePanel">
    								<table class="table table-condensed">
    									<thead>
    										<tr>
    											<th>User</th>
    											<th>Support</th>
    											<th>Ratio</th>
    										</tr>
    									</thead>
    									<tbody id="likeTableBody">
    									</tbody>
    								</table>

    							</div>
    							<!-- End of box body -->
    						</div>
    						<!-- End of box -->
    					</div>
    					<!-- /.col-lg-6 -->

    					<!--/.col-lg-6 -->
    					<div class="col-lg-6">
    						<div class="box box-primary">
    							<div class="box-header with-border">
    								<h4>They have been mentioned by the user</h4>
    								<div class="box-tools pull-right">
    									<button class="btn btn-box-tool" data-widget="collapse">
    										<i class="fa fa-minus"></i>
    									</button>
    								</div>

    							</div>
    							<div class="box-body" id="mentionTablePanel">
    								<table class="table table-condensed">
    									<thead>
    										<tr>
    											<th>User</th>
    											<th>Support</th>
    											<th>Ratio</th>
    										</tr>
    									</thead>
    									<tbody id="mentionTableBody">
    									</tbody>
    								</table>

    							</div>
    							<!-- End of box body -->
    						</div>
    						<!-- End of box -->
    					</div>
    					<!-- /.col-lg-6 -->
    				</div>
    				<!-- End of col-12 -->
    			</div>
    			<!-- End of row -->

    			<div class="row">
    				<div class="col-lg-12">
    					<div class="box box-primary">
    						<div class="box-header with-border">

    							<h4>Users That Often Appear Together</h4>
    							<div class="box-tools pull-right">
    								<button class="btn btn-box-tool" data-widget="collapse">
    									<i class="fa fa-minus"></i>
    								</button>
    							</div>
    						</div>
    						<div class="box-body" id="patternTablePanel">
    							<p>Users that ofter appear together will be displayed in a
    								row in the following table.</p>
    								<table class="table table-condensed">
    									<thead>
    										<tr>
    											<th>User</th>
    											<th>Support</th>
    											<th>Ratio</th>
    										</tr>
    									</thead>
    									<tbody id="patternTableBody">
    									</tbody>
    								</table>
    							</div>
    							<!-- End of box body -->
    						</div>
    						<!-- End of box -->

    					</div>
    					<!-- End of col-12 -->
    				</div>
    				<!-- End of row -->

    			</div>
    			<!-- /.container-fluid -->
    		</div>
    		<!-- /#page-wrapper -->

    		<content tag="javascript"> 
    			<g:javascript src="udf/asynchro.js" />
    			<g:javascript src="udf/progressBar.js" /> 
    			<g:javascript
    			src="udf/drawRelation.js" /> <script>
    			function showNumber(jobName, zone, list) {
    				if (!zone) {
    					console.log("Zone is not found for " + jobName);
    					return;
    				}
    				var h = document.createElement('h4');
    				var span = document.createElement('span');

    				span.innerHTML = list.length;
    				span.className = "label label-primary";
    				h.appendChild(span);
    				zone.appendChild(h);
    			}
    			function showNumberInButton(name, list) {
    				var zone = document.getElementById(name + "Size");
    				if (!zone) {
    					console.log("Zone is not found for " + jobName);
    					return;
    				}
    				zone.innerHTML = list.length;

    			}
    			function drawSupportTable(map, name, total) {
    				console.log("Processing table " + name);
    				var zone = document.getElementById(name + "TableBody");
    				for ( var f in map) {
    					if (map.hasOwnProperty(f)) {
    						var count = map[f];
    						var tr = document.createElement("tr");
    						var u = document.createElement("td");
    						u.innerHTML = f;
    						var c = document.createElement("td");
    						c.innerHTML = count;
    						var r = document.createElement("td");
    						var ratio = Math.round(count) / total;
    						var perc = Math.floor(ratio * 10000) / 100 + "%";
    						r.innerHTML = perc;

    						tr.appendChild(u);
    						tr.appendChild(c);
    						tr.appendChild(r);

    						zone.appendChild(tr);
    					}
    				}

    			}
    			function drawPatternTable(array, name, total) {
    				console.log("Processing table " + name);
    				var zone = document.getElementById(name + "TableBody");
    				if (!zone) {
    					console.log("Cannot locate table for " + name);
    				}
    				for ( var idx in array) {
					//get element and extract fields
					var obj = array[idx];
					var names = obj["names"];//array of names included
					var nameString = names.toString();
					console.log("Convert the user names to string:"
						+ nameString);
					var count = obj["support"];

					var tr = document.createElement("tr");
					var u = document.createElement("td");
					u.innerHTML = nameString;
					var c = document.createElement("td");
					c.innerHTML = count;
					var r = document.createElement("td");
					var ratio = Math.round(count) / total;
					var perc = Math.floor(ratio * 10000) / 100 + "%";
					r.innerHTML = perc;

					tr.appendChild(u);
					tr.appendChild(c);
					tr.appendChild(r);
					//create hover action on table row
					var keywords = obj["keywords"];
					console.log("Keywords under this pattern are");
					console.log(keywords);
					r.setAttribute("data-toggle", "popover");
					r["title"] = "Keywords extracted from the content shared by the users.";
					r.setAttribute("data-content", keywords.toString());
					//name.setAttribute("trigger","hover");
					r.setAttribute("data-placement", "buttom");

					zone.appendChild(tr);
				}
				//activate popover event on table rows
				$('[data-toggle="popover"]').popover({
					trigger : "hover"
				});

			}
			function drawSupport(jsonData) {
				console.log("In drawSupport here is the data i got:");
				console.log(support);
				var support = jsonData["support"];
				console.log("Attempt for support");
				//console.log(support);
				if (support) {
					console.log("Able to proceed with support info:");
				} else {
					console.log("support not found");
					return;
				}
				var comment = support["commented"];
				var forward = support["forwarded"];
				var like = support["liked"];

				var total = support["total"];
				console.log("Got comments map from user");
				console.log(comment);
				console.log("Got likes map from user");
				console.log(like);
				console.log("Got forwards map from user");
				console.log(forward);

				drawSupportTable(comment, "comment", total);
				drawSupportTable(forward, "forward", total);
				drawSupportTable(like, "like", total);

				//draw support
				var patterns = jsonData["patterns"];
				console.log("Here is the patterns raw data.");
				console.log(patterns);
				drawPatternTable(patterns, "pattern", total);
			}

			function displayInfo(relation) {
				var fedArr = relation["forwarded"];
				var fedZone = document.getElementById("fedInfo");
				//showNumber("fed",fedZone,fedArr);
				showNumberInButton("fed", fedArr);
				var fingArr = relation["forwarding"];
				var fingZone = document.getElementById("fingInfo");
				//showNumber("fing",fingZone,fingArr);
				showNumberInButton("fing", fingArr);
				var cingArr = relation["commenting"];
				var cingZone = document.getElementById("cingInfo");
				//showNumber("cing",cingZone,cingArr);
				showNumberInButton("cing", cingArr);
				var cedArr = relation["commenting"];
				var cedZone = document.getElementById("cedInfo");
				//showNumber("ced",cedZone,cedArr);
				showNumberInButton("ced", cedArr);

			}
			function asynchroInteraction() {
				// show a progress bar in the zone
				//startProgressBar('relation',300);
				//startBootstrapProgressBar('relation', 300);
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroRelation",
					dataType : "json",
					async : true
				}).done(function(jsonData) {
					console.log("relationship:");
					console.log(jsonData);
					relatedData = jsonData;
					// stop progress bar and show
					//stopBootstrapProgressBar('relation');
					//display data
					//displayInfo(jsonData);
					//drawRelation();
					drawSupport(jsonData);
					relationFinished = true;
					console.log("Relation finished, fire network");

				});
			}
			</script> <script>
				//flags
				var relationFinished = false;
				var keywordFinished = false;
				// for user's forward and comment related data
				var relatedData;
				console.log("Start relation call");
				asynchroInteraction();
			</script> </content>
		</body>

		</html>
