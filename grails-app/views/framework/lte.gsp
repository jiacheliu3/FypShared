<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Admin LTE 2.0 Template</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<!-- bootstrap -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'bootstrap.min.css')}"
	type="text/css">
<!-- plugins -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'font-awesome.min.css')}"
	type="text/css">
<link rel="stylesheet"
	href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
<!-- Theme style -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'AdminLTE.min.css')}"
	type="text/css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: '_all-skins.min.css')}"
	type="text/css">

<g:javascript src="jquery.min.js" />
<g:javascript src="d3.js" />

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<!-- Site wrapper -->
	<div class="wrapper">

		<g:render template="/framework/lte_header"></g:render>

		<!-- =============================================== -->

		<!-- Left side column. contains the sidebar -->
		<g:render template="/framework/lte_leftside"></g:render>

		<!-- =============================================== -->
		<!-- Content Wrapper. Contains page content -->
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>
					Dashboard <small>Control panel</small>
				</h1>
				<ol class="breadcrumb">
					<li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
					<li class="active">Dashboard</li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">
				<!-- Small boxes (Stat box) -->
				<div class="row">
					<div class="col-lg-3 col-xs-6">
						<!-- small box -->
						<div class="small-box bg-aqua">
							<div class="inner">
								<h3>150</h3>
								<p>New Orders</p>
							</div>
							<div class="icon">
								<i class="ion ion-bag"></i>
							</div>
							<a href="#" class="small-box-footer">More info <i
								class="fa fa-arrow-circle-right"></i></a>
						</div>
					</div>
					<!-- ./col -->
					<div class="col-lg-3 col-xs-6">
						<!-- small box -->
						<div class="small-box bg-green">
							<div class="inner">
								<h3>
									53<sup style="font-size: 20px">%</sup>
								</h3>
								<p>Bounce Rate</p>
							</div>
							<div class="icon">
								<i class="ion ion-stats-bars"></i>
							</div>
							<a href="#" class="small-box-footer">More info <i
								class="fa fa-arrow-circle-right"></i></a>
						</div>
					</div>
					<!-- ./col -->
					<div class="col-lg-3 col-xs-6">
						<!-- small box -->
						<div class="small-box bg-yellow">
							<div class="inner">
								<h3>44</h3>
								<p>User Registrations</p>
							</div>
							<div class="icon">
								<i class="ion ion-person-add"></i>
							</div>
							<a href="#" class="small-box-footer">More info <i
								class="fa fa-arrow-circle-right"></i></a>
						</div>
					</div>
					<!-- ./col -->
					<div class="col-lg-3 col-xs-6">
						<!-- small box -->
						<div class="small-box bg-red">
							<div class="inner">
								<h3>65</h3>
								<p>Unique Visitors</p>
							</div>
							<div class="icon">
								<i class="ion ion-pie-graph"></i>
							</div>
							<a href="#" class="small-box-footer">More info <i
								class="fa fa-arrow-circle-right"></i></a>
						</div>
					</div>
					<!-- ./col -->
				</div>
				<!-- /.row -->
			</section>
			<!-- /.content -->
		</div>
		<!-- /.content-wrapper -->
		<div id="page-wrapper" class="content-wrapper">
			<section class="content-header"><h1>General Information</h1></section>
			<section class="content">
				<div class="row">
					<div class="panel panel-primary outer-panel">
						<div class="panel-heading">Basic Information</div>
						<div class="panel-body" id="contentPanel">

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
								<div id="userName">
									<b>User name:</b>

								</div>
								<div id="userId">
									<b>User ID:</b>

								</div>

								<div id="userUrl">
									<b>Weibo user url:</b>
								</div>
							</div>
						</div>
						<!--/.panel-body -->
					</div>
					<!--/.panel -->
				</div>
				<!--/.row -->
				<div class="row">
					<div class="panel panel-primary outer-panel">
						<div class="panel-heading">Statistic</div>
						<div class="panel-body" id="weiboStats">
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

						</div>

					</div>
					<!--/.panel -->
				</div>
				<!--/.row -->

				<div class="row">
					<div class="panel panel-primary outer-panel">
						<div class="panel-heading">Progress Monitor</div>
						<div class="panel-body">
							<div class="panel panel-default">
								<div class="panel-heading">Preparation:</div>
								<div class="panel-body">
									<div id="knowledgeStatus" class="infoZone"></div>
									<div id="crawlerStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->
							<div class="panel panel-default">
								<div class="panel-heading">Keyword Study:</div>
								<div class="panel-body">
									<div id="keywordStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->

							<div class="panel panel-default">
								<div class="panel-heading">Relation Study:</div>
								<div class="panel-body">
									<div id="relationStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->

							<div class="panel panel-default">
								<div class="panel-heading">Network Study:</div>
								<div class="panel-body">
									<div id="networkStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->
							<div class="panel panel-default">
								<div class="panel-heading">Cluster Study:</div>
								<div class="panel-body">
									<div id="clusterStatus" class="alert alert-danger"></div>
								</div>
							</div>
							<!-- End of inner panel -->

							<div class="panel panel-default">
								<div class="panel-heading">Deep Crawl</div>
								<div class="panel-body">
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
						<!-- End of outer panel -->
					</div>
					<!-- /.row -->
			</section>
		</div>
		<!-- /#content-wrapper -->
		<!-- Footer -->
		<g:render template="/framework/lte_footer"></g:render>

		<!-- Control Sidebar -->
		<g:render template="/framework/lte_rightside"></g:render>
	</div>
	<!-- ./wrapper -->

	<g:javascript src="d3.js" />
	<!-- Bootstrap 3.3.5 -->
	<g:javascript src="bootstrap.js" />
	<!-- SlimScroll -->
	<g:javascript src="jquery.slimscroll.min.js" />
	<!-- FastClick -->
	<g:javascript src="fastclick.min.js" />
	<!-- AdminLTE App -->
	<g:javascript src="app.min.js" />
	<!-- AdminLTE for demo purposes -->
	<g:javascript src="demo.js" />
</body>
</html>
