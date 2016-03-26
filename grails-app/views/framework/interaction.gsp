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
.labelName{
	font-weight:900;
	font-size:120%;
	text-shadow: 2px 0 0 #ccc, -2px 0 0 #fff, 0 2px 0 #fff, 0 -2px 0 #fff, 1px 1px #fff, -1px -1px 0 #fff, 1px -1px 0 #fff, -1px 1px 0 #fff;
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

			<!-- Network zone -->
			<div class="row">
				<div class="col-lg-12">
					<div class="box box-primary">
						<div class="box-header with-border">
							<h4>They have been mentioned by the user</h4>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>

						</div>
						<div class="box-body">
							<div class="col-lg-9">
								<div id="simpleNetworkCanvas" class="networkCanvas"></div>
							</div>
							<!-- /.col-lg-8 -->
							<div class="col-lg-3">
								<div class="box box-default">
									<div class="box-header with-border">
										<h4>Simple network</h4>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-minus"></i>
											</button>
										</div>

									</div>
									<div class="box-body">
										<ul class="list-unstyled">
											<li>One user has a link pointing to another if he or she
												has commented/reposted/liked/mentioned by the latter.</li>
											<li>The links have no weight.</li>
											<li>A <b>clique</b> contains users that has links
												pointing to any other member in the clique.
											</li>
											<li>Different cliques can be identified by different
												colors.</li>
										</ul>
									</div>
									<!-- End of box body -->
								</div>
								<!-- End of box -->
							</div>
							<!-- /.col-lg-4 -->
						</div>
						<!-- End of box body -->
					</div>
					<!-- End of box -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->

			<div class="row">
				<div class="col-lg-12">
					<div class="box box-primary">
						<div class="box-header with-border">
							<h4>They have been mentioned by the user</h4>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse">
									<i class="fa fa-minus"></i>
								</button>
							</div>
						</div>
						<div class="box-body">
							<div class="col-lg-9">
								<div id="fullNetworkCanvas" class="networkCanvas"></div>
							</div>
							<!-- /.col-lg-8 -->
							<div class="col-lg-3">
								<div class="box box-default">
									<div class="box-header with-border">
										<h4>Full network</h4>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-minus"></i>
											</button>
										</div>

									</div>
									<div class="box-body">
										<ul class="list-unstyled">
											<li>One user has a link pointing to another if he or she
												has commented/reposted/liked/mentioned by the latter.</li>
											<li>The weight notifies how many time that the source
												user has interacted with the target.</li>
											<li>A <b>clique</b> contains users that has links
												pointing to any other member in the clique.
											</li>
											<li>Different cliques can be identified by different
												colors.</li>
										</ul>
									</div>
									<!-- End of box body -->
								</div>
								<!-- End of box -->
							</div>
							<!-- /.col-lg-4 -->
						</div>
						<!-- End of box body -->
					</div>
					<!-- End of box -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->


			<!-- Pattern -->
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

	<content tag="javascript"> <g:javascript
		src="udf/asynchro.js" /> <g:javascript src="udf/progressBar.js" /> <g:javascript
		src="udf/drawRelation.js" /> <g:javascript
		src="udf/drawDirectedNetwork.js" /> <script>
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
    					var interaction=jsonData["interaction"];
    					var network=jsonData["network"];
    					//displayInfo(jsonData);
    					//drawRelation();
    					drawInteraction(interaction);
    					drawNetwork(network);		
    				});
    			}
        		function drawInteraction(interaction){
        			console.log("In drawInteraction here is the data i got:");
    				console.log(interaction);
        			var spt=interaction["support"];
        			var ptn=interaction["pattern"];
					console.log("Support goes to drawSupport");
					console.log(spt);
        			drawSupport(spt);

        			console.log("Pattern goes to drawPattern");
        			console.log(ptn);
        			drawPattern(ptn);
            	}
				function drawNetwork(networks){
					console.log("Network data");
					console.log(networks);
					
					//draw simple network
					var simpleJson=networks["simple"];
					var simple=JSON.parse(simpleJson);
					drawDirectedNetwork(simple,"#simpleNetworkCanvas");
					//draw full network
					var fullJson=networks["complex"];
					var full=JSON.parse(fullJson);
					drawDirectedNetwork(full,"#fullNetworkCanvas");
					
				}			
			</script> 
		<script>
				console.log("Start user interaction call");
				asynchroInteraction();

				//var data=forgeNetworkData();
				//console.log("The data is ");
				//console.log(data);
				//drawDirectedNetwork(data,"#simpleNetworkCanvas");
				
				//var yadata=forgeNetworkData();
				//drawDirectedNetwork(yadata,"#fullNetworkCanvas");
				</script> 
			</content>
		</body>
		</html>