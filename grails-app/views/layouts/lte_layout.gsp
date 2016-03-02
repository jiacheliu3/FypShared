<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- title -->
<title><g:layoutTitle default="Admin LTE layout template" /></title>
<g:layoutHead />
<!-- bootstrap -->
<link rel="stylesheet"
	href="${resource(dir: 'css/framework', file: 'bootstrap.min.css')}"
	type="text/css">
<!-- plugins -->
<link rel="stylesheet"
	href="${resource(dir: 'css/framework', file: 'font-awesome.min.css')}"
	type="text/css">
<link rel="stylesheet"
	href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
<!-- Theme style -->
<link rel="stylesheet"
	href="${resource(dir: 'css/template', file: 'AdminLTE.min.css')}"
	type="text/css">
<!-- Self-defined scheme for labels -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'mylabels.css')}"
	type="text/css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="${resource(dir: 'css/template', file: '_all-skins.min.css')}"
	type="text/css">

<!-- Must-use JS libraries go first -->
<g:javascript src="toolbox/jquery.min.js" />
<g:javascript src="toolbox/d3.js" />

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

<!-- Put the header here -->
<g:layoutHead />
</head>

<body class="hold-transition skin-blue sidebar-mini">
	<!-- Site wrapper -->
	<div class="wrapper">
		<!-- header of page -->
		<g:render template="/framework/lte_header"></g:render>
		<!-- Left side column. contains the sidebar -->
		<g:render template="/framework/lte_leftside"></g:render>

		<!-- Content here -->
		<g:layoutBody />

		<!-- Control Sidebar -->
		<g:render template="/framework/lte_rightside"></g:render>

		<!-- Footer -->
		<g:render template="/framework/lte_footer"></g:render>


	</div>
	<!-- ./wrapper -->

	<!-- Toolbox -->
	<!-- Generally needed by all pages -->
	<g:javascript src="toolbox/d3.js" />
	<!-- Bootstrap 3.3.5 -->
	<g:javascript src="toolbox/bootstrap.js" />
	<!-- SlimScroll -->
	<g:javascript src="toolbox/jquery.slimscroll.min.js" />
	<!-- FastClick -->
	<g:javascript src="toolbox/fastclick.min.js" />
	<!-- From the template -->
	<!-- AdminLTE App -->
	<g:javascript src="template/app.min.js" />
	<!-- AdminLTE for demo purposes -->
	<g:javascript src="template/demo.js" />
	<!-- Status monitor -->
	<!-- Notification plugin -->
	<g:javascript src="toolbox/bootstrap-notify.js" />
	<g:javascript src="udf/notice.js" />
	<g:javascript src="udf/statusMonitor.js" />
	
	<!-- Page specific -->
	<g:pageProperty name="page.javascript" />

	
	<script>
		var deepCrawl = document.getElementById("deepCrawlLink");
		deepCrawl.onclick = beforeDeepCrawl;
		asynchroMonitor();
		setInterval(function() {
			asynchroMonitor()
		}, 5000);

		var crawlerNoticeSent = false;
		var keywordNoticeSent = false;
		var relationNoticeSent = false;
		var networkNoticeSent = false;
		var clusterNoticeSent = false;
		var deepCrawlerNoticeSent = false;
	</script>
</body>
</html>
