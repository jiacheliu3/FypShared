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
	href="${resource(dir: 'css', file: 'sb-admin-2.css')}" type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'morris.css')}" type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'font-awesome.min.css')}"
	type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'metisMenu.min.css')}"
	type="text/css">
<g:javascript src="jquery.min.js" />



<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

	<div id="wrapper">

		<!-- Navigation -->
		<g:render template="/framework/template"></g:render>

		<!-- Page Content -->
		<div id="page-wrapper">
			<div class="container-fluid">
				<div class="row">
					<div class="col-lg-12">
						<h1 class="page-header">Blank</h1>
					</div>
					<!-- /.col-lg-12 -->
				</div>
				<!-- /.row -->
				<div class="row">
					<div id="chart_div"></div>
				</div>
			</div>
			<!-- /.container-fluid -->
		</div>
		<!-- /#page-wrapper -->

	</div>



	<g:javascript src="metisMenu.min.js" />
	<g:javascript src="raphael-min.js" />

	<g:javascript src="morris.min.js" />
	<g:javascript src="morris-data.js" />
	<g:javascript src="sb-admin-2.js" />
	<g:javascript src="d3.js" />
	<g:javascript src="piechart.js" />
	<script>
		
	function extractData(d) {
		return d.count;
	}
		var dataset = [ {
			label : 'Abulia',
			count : 10
		}, {
			label : 'Betelgeuse',
			count : 20
		}, {
			label : 'Cantaloupe',
			count : 30
		}, {
			label : 'Dijkstra',
			count : 40
		} ];
		drawPieChart(dataset,extractData);
	</script>
</body>

</html>
