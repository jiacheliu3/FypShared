<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>User's Public Privacy Study</title>

<g:javascript src="toolbox/jquery.js" />
<g:javascript src="toolbox/bootstrap-carousel.js" />
	
<!-- Bootstrap Core CSS -->
<link rel="stylesheet"
	href="${resource(dir: 'css/framework', file: 'bootstrap.min.css')}"
	type="text/css">

<!-- Custom CSS -->
<link rel="stylesheet"
	href="${resource(dir: 'css/template', file: 'modern-business.css')}"
	type="text/css">


<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
<!-- Custom Fonts -->
<link rel="stylesheet"
	href="${resource(dir: 'css/framework', file: 'font-awesome.min.css')}"
	type="text/css">

	
</head>

<body>

	
	
	<!-- Header Carousel -->
	<header id="myCarousel" class="carousel slide">
		<!-- Indicators -->
		<ol class="carousel-indicators">
			<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
			<li data-target="#myCarousel" data-slide-to="1"></li>
			<li data-target="#myCarousel" data-slide-to="2"></li>
		</ol>

		<!-- Wrapper for slides -->
		<div class="carousel-inner">
			<div class="item active">
				<div class="fill">
					<asset:image src="gradientoverlay.png" style="width:100%;height:100%;position:relative;"/>
					
				</div>
				
				<div class="carousel-caption" >
					<div class="vert">
						<h2>User's Public Privacy Mining</h2>
					</div>
				</div>
			</div>
			<div class="item">
				<div class="fill">
					<asset:image src="gradientoverlay.png" style="width:100%;height:100%;position:relative;"/>
					
				</div>
				<div class="carousel-caption">
					<h2>Start from Weibo.com and crawl out.</h2>
				</div>
			</div>
			<div class="item">
				<!-- 
				<div class="fill"
					style="background-image: url('http://placehold.it/1900x1080&amp;text=Slide Two');"></div>
				 -->
				 <div class="fill">
					<asset:image src="gradientoverlay.png" style="width:100%;height:100%;position:relative;"/>
					
				</div>
				<div class="carousel-caption">
					<h2>More targeted crawling based on user's tags.</h2>
				</div>
			</div>
			
		</div>

		<!-- Controls -->
		<a class="left carousel-control" href="#myCarousel" data-slide="prev">
			<span class="icon-prev"></span>
		</a> <a class="right carousel-control" href="#myCarousel"
			data-slide="next"> <span class="icon-next"></span>
		</a>
	</header>

	<!-- Page Content -->
	<div class="container">

		<!-- Marketing Icons Section -->
		<div class="row">
			
			<div class="col-lg-12">
				<h1 class="page-header">User's Public Privacy Mining</h1>
			</div>
			<div class="col-md-4">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4>
							<i class="fa fa-fw fa-check"></i> Keywords
						</h4>
					</div>
					<div class="panel-body">
						<ul>
							<li>
								Study user's keywords based on all contents that the user has posted.
								
							</li>
							<br/>
							<li>
								Classify the content based on trained SVM. The SVM is trained to have more than 20 categories.
									
								
							</li>
							
						
						</ul>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4>
							<i class="fa fa-fw fa-gift"></i> Inter-user Interactions
						</h4>
					</div>
					<div class="panel-body">
						<ul>
							<li>
								Based on every piece of microblog, study the interactions like reposts, comments and likes. A user network is formed based on such interaction.
								
							</li>
						
							<br/>
							<li>
								Sub-networks are also formed based on the user's friends that share similar contents on microblogs.
								
							</li>
						</ul>
						
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4>
							<i class="fa fa-fw fa-compass"></i> Clustering
						</h4>
					</div>
					<div class="panel-body">
						<ul>
							<li>
								Cluster the user's microblogs and let them form groups based on similar contents, each having a characteristic tag on the content.
								
							</li>
							<br/>
							<li>
							The user's friends that share similar tags should belong to a clique based on the shared characteristics.
									
								
							</li>
							<br/>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<!-- /.row -->

		<!-- Features Section -->
		<div class="row">
			<div class="col-lg-12">
				<h2 class="page-header">Have A Try</h2>
			</div>
			<div class="col-md-12">
				<!-- Entrance of start -->
				<g:render template="/environment/datainput" />
			</div>
			
		</div>
		
		<!-- Portfolio Section -->
		<div class="row">
			<div class="col-lg-12">
				<h2 class="page-header">Samples</h2>
			</div>
			<div class="col-md-4 col-sm-6">
				<!-- 
				<a href="portfolio-item.html"> <img
					class="img-responsive img-portfolio img-hover"
					src="http://placehold.it/700x450" alt="">
				</a>
				 -->
				 <asset:image src="sample1.png" style="width:100%;height:100%;position:relative;"/>
			</div>
			<div class="col-md-4 col-sm-6">
				<!-- 
				<a href="portfolio-item.html"> <img
					class="img-responsive img-portfolio img-hover"
					src="http://placehold.it/700x450" alt="">
				</a>
				-->
				<asset:image src="sample2.png" style="width:100%;height:100%;position:relative;"/>
			</div>
			<div class="col-md-4 col-sm-6">
				<!-- 
				<a href="portfolio-item.html"> <img
					class="img-responsive img-portfolio img-hover"
					src="http://placehold.it/700x450" alt="">
				</a>
				 -->
				 <asset:image src="sample3.png" style="width:100%;height:100%;position:relative;"/>
			</div>
			<div class="col-md-4 col-sm-6">
				<!-- 
				<a href="portfolio-item.html"> <img
					class="img-responsive img-portfolio img-hover"
					src="http://placehold.it/700x450" alt="">
				</a>
				 -->
				 <asset:image src="sample4.png" style="width:100%;height:100%;position:relative;"/>
			</div>
			<div class="col-md-4 col-sm-6">
				<!-- 
				<a href="portfolio-item.html"> <img
					class="img-responsive img-portfolio img-hover"
					src="http://placehold.it/700x450" alt="">
				</a>
				 -->
				 <asset:image src="sample5.png" style="width:100%;height:100%;position:relative;"/>
			</div>
			<div class="col-md-4 col-sm-6">
				<!-- 
				<a href="portfolio-item.html"> <img
					class="img-responsive img-portfolio img-hover"
					src="http://placehold.it/700x450" alt="">
				</a>
				 -->
				 <asset:image src="sample6.png" style="width:100%;height:100%;position:relative;"/>
			</div>
			
		</div>
		<!-- /.row -->

		
		<!-- /.row -->

		<hr>

		

		<!-- Footer -->
		<footer>
			<div class="row">
				<div class="col-lg-12">
					<p>Copyright &copy; Your Website 2014</p>
				</div>
			</div>
		</footer>

	</div>
	<!-- /.container -->

	

	<!-- Script to Activate the Carousel -->
	<script>
		$('.carousel').carousel({
			interval : 5000
		//changes the speed
		})
	</script>

</body>

</html>
