
<style>
	.links{
		width:200px;
		padding:0;
	}
	div.labelSpan span{
		text-align:center;
	}
	.linkItem{
		width:200px;
		height:20px;
	}
	.labelSpan{
		width:200px;
		display:inline-block;
		text-align:center;
	}
	
</style>
<style>
	/* For all pages */
	.panel-heading{
		font-size:1.5em;
	}
	.panel-heading .panel-heading{
		font-size:1.2em;
	}
	.panel-body{
		font-size:1.1em;
	}
</style>
<div id="wrapper">

	<!-- Navigation -->
	<nav class="navbar navbar-default navbar-static-top" role="navigation"
		style="margin-bottom: 0">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target=".navbar-collapse">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="index.html">USER'S PUBLIC PRIVACY STUDY</a>
		</div>
		<!-- /.navbar-header -->

		<ul class="nav navbar-top-links navbar-right">
			<!-- 
			<li class="dropdown"><a><span
					class="glyphicon glyphicon-envelope"></span></a></li>
			<li class="dropdown"><a><span
					class="glyphicon glyphicon-cog"></span></a></li>
			<li class="dropdown"><a><span
					class="glyphicon glyphicon-download-alt"></span></a></li>
			 -->
		</ul>

		<!-- /.navbar-top-links -->

		<div class="navbar-default sidebar" role="navigation">
			<div class="sidebar-nav navbar-collapse">
				<ul class="nav" id="side-menu">
					<li class="sidebar-search">
						<div class="input-group custom-search-form">
							<input type="text" class="form-control" placeholder="Search...">
							<span class="input-group-btn">
								<button class="btn btn-default" type="button" style="margin:0;">GO</button>
							</span>
						</div> <!-- /input-group -->
					</li>
					<li><a href="#"><i class="fa fa-user"></i>&nbsp; Personal Information</a>
						<ul class="nav nav-second-level">
							<li><a href="#">
							<i class="fa fa-meh-o"></i>&nbsp; Basic Information</a></li>
							<li><a href="#">
							<i class="fa fa-puzzle-piece"></i>&nbsp;Interests</a></li>
						</ul> <!-- /.nav-second-level --></li>

					<li><a href="#"><i class="fa fa-files-o fa-fw"></i> &nbsp; Weibo.com<span class="fa arrow"></span></a>
						<ul class="nav nav-second-level">
							<li id="accountLink" class="links">
								<div id="accountLinkTrue">
									<a class="active"
									href="${createLink(controller: 'framework', action: 'account')}">
									<i class="fa fa-meh-o"></i>&nbsp; Account
										Information</a>
								</div>
								<div class="labelSpan">
									<span id= "accountLinkLabel" class = "label label-default">Not started</span>
								</div>
							</li>
							<li id="keywordLink" class="links">
								<!-- 
								<div class="labelSpan">
								<span id= "keywordLinkLabel" class = "label label-default">Not started</span>
								</div>
								 -->
								<div id="keywordLinkFalse" class="linkItem">
									<a href="#">
									<i class="fa fa-newspaper-o"></i>&nbsp; Keyword Study</a>
									
								</div>
								<div id="keywordLinkTrue" class="linkItem">
									<a 
									href="${createLink(controller: 'framework', action: 'keywords')}">
									<i class="fa fa-newspaper-o"></i>&nbsp; Keyword Study</a>
									
								</div>
								<div class="labelSpan">
										<span id= "keywordLinkLabel" class = "label label-default">Not started</span>
									</div>
							</li>
							<li id="relationLink" class="links">
								<!-- 
								<div class="labelSpan">
									<span id="relationLinkLabel" class ="label label-default">Not started</span>
								</div>
								 -->
								<div id="relationLinkFalse" class="linkItem">
									<a href="#">
									<i class="fa fa-users"></i>&nbsp; Relationship
										Study</a>
									
								</div>
								
								<div id="relationLinkTrue" class="linkItem">
									<a 
									href="${createLink(controller: 'framework', action: 'relation')}">
									<i class="fa fa-users"></i>&nbsp; Relationship
										Study</a>
								</div>
								<div class="labelSpan" style="text-align:center; width:100%;">
										<span id="relationLinkLabel" class ="label label-default">Not started</span>
								</div>
							</li>
							<li id="networkLink" class="links">
								
								<div id="networkLinkTrue" class="linkItem">
									<a 
								href="${createLink(controller: 'framework', action: 'network')}">
									<i class="fa fa-sitemap"></i>&nbsp; Network
									Study</a>
									
								</div>
								<div id="networkLinkFalse" class="linkItem">
									<a href="#">
									<i class="fa fa-sitemap"></i>&nbsp; Network	Study</a>
									
								</div>
								<div class="labelSpan">
										<span id="networkLinkLabel" class = "label label-default">Not started</span>
									</div>
							</li>
							<li id="clusterLink" class="links">
								
								<div id="clusterLinkFalse" class="linkItem">
									
									<a href="#"><i class="fa fa-object-group"></i>&nbsp; Cluster Study</a>
									
								</div>
								<div id="clusterLinkTrue" class="linkItem">
									<a 
									href="${createLink(controller: 'framework', action: 'cluster')}">
									<i class="fa fa-object-group"></i>&nbsp; Cluster
										Study</a>
								
								</div>
								<div class="labelSpan">
									<span id="clusterLinkLabel" class="label label-default">Not started</span>
								</div>
							</li>
							<li id="deepCrawlLink" class="links" style="height:50px;margin-up:5px;margin-down:5px;">
								<div id="deepCrawlLinkTrue" class="linkItem" style="font-size:1.2em;">
									
									<a id="deepCrawlLink"
									href="${createLink(controller: 'environment', action: 'asynchroDeepCrawl')}">
									<i class="fa fa-download"></i>&nbsp; <b>Start Deep Crawl</b></a>
								</div>
							</li>
						</ul>
					</li>
					<li><a href="#"><i class="fa fa-facebook"></i>&nbsp; Facebook</a>
					<li><a
						href="${createLink(controller: 'framework', action: 'newIndex')}">
						<i class="fa fa-trash"></i>&nbsp; Start
							a New Search</a></li>
					


				</ul>
			</div>
			<!-- /.sidebar-collapse -->
		</div>
		<!-- /.navbar-static-side -->
	</nav>


</div>
<!-- Notification plugin -->
<g:javascript src="bootstrap-notify.js" />
<g:javascript src="notice.js" />
<g:javascript src="statusMonitor.js" />

<script>
var deepCrawl=document.getElementById("deepCrawlLink");
deepCrawl.onclick=beforeDeepCrawl;
asynchroMonitor();
setInterval(function(){asynchroMonitor()},5000);

var crawlerNoticeSent=false;
var keywordNoticeSent=false;
var relationNoticeSent=false;
var networkNoticeSent=false;
var clusterNoticeSent=false;
var deepCrawlerNoticeSent=false;
</script>

