<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>All Tags</title>

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
<style>
.well li {
	font-size: 1.2em;
}
</style>
</head>

<body>

	<%@ page import="codebigbrosub.Tag"%>


	<div id="wrapper">

		<g:render template="/framework/template"></g:render>


		<!-- Page Content -->
		<div id="page-wrapper">
			<div class="container-fluid">
				<div class="row">
					<div class="col-lg-12">
						<div class="panel panel-primary">
							<div class="panel-heading">All tags</div>
							<div class="panel-body">
								<p>The tags are heuristically picked and SVMs. Then crawlers
									fetch related well-known webpages for text data as training
									source based on which classification models are trained.</p>
								
							</div>
							<!-- panel body -->
							<!-- Table -->
							<table class="table table-striped table-bordered table-hover">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Tag Name</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    	<%int i=0; %>
                                    	<g:each var="tag" in="${Tag.list()}">
                                    	
                                    	<tr>
                                            <td>${i++}</td>
                                            <td>${tag.name}</td>
                                        </tr>
                                        </g:each>
                                   </tbody>
							</table>



						</div>
					</div>
					<!-- /.col-lg-12 -->
				</div>
				<!-- /.row -->
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
</body>

</html>
