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
</style>
<!-- Forgeries -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'mylabels.css')}" type="text/css">
<g:javascript src="jquery.min.js" />


<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<style>
	.keywords {
	width:70px;
	height:35px;
	outline: #FFFFFF solid 2px; font-size =1em;
	padding:0px;
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
						<h1 class="page-header">Blank</h1>
						<button type="button" onclick="notifyMe();">Hit me</button>
						<button type="button" onclick="testRestartReminder();">Start
							again</button>
						<button type="button" onclick="causeError();">Try error</button>
						<a href="#" data-toggle="popover" title="Popover Header"
							data-content="Some content inside the popover">Toggle popover</a>
						<p data-toggle="popover" title="Popover Header"
							data-content="Some content inside the popover">Paragraph</p>
						<table>
							<thead>
								<tr>
									<th>Index</th>
									<th>Name</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td data-toggle="popover" title="Popover Header"
										data-content="Some content inside the popover">1</td>
									<td>Tom</td>
								</tr>
							</tbody>
						</table>
						<a href="#" data-toggle="popover" title="Popover Header"
							data-content="Some content inside the popover">Toggle popover</a>
					</div>
					<!-- /.col-lg-12 -->
				</div>
				<!-- /.row -->

				<!-- Another row for testing hyperlinks -->
				<div class="row">
					<a id="totest" href="/CodeBigBroSub/framework/account"> Click
						me motherf**ker </a>
					<button type="button" onclick="crippleMe();">Disable the
						link</button>
					<button type="button" onclick="healMe();">Reactivate the
						link</button>
				</div>

				<!-- Another row for testing gradient colors -->
				<div class="row">
					<div id="colorForge"></div>
				</div>

				<!-- Testing on labels -->
				<div class="row">
					<div class="background">
						<h3>Not opaque</h3>
						<span class="label label-info">Info</span> <span
							class="label label-warning">Warning</span> <span
							class="label label-danger">Danger</span> <span
							class="label label-success">Success</span>
					</div>
					<div class="background">
						<h3>0.7</h3>

						<span class="label label-info light">Info</span> <span
							class="label label-warning light">Warning</span> <span
							class="label label-danger light">Danger</span> <span
							class="label label-success light">Success</span>
					</div>
					<div class="background">
						<h3>0.5</h3>

						<span class="label label-info zero">Info</span> <span
							class="label label-warning zero">Warning</span> <span
							class="label label-danger zero">Danger</span> <span
							class="label label-success zero">Success</span>
					</div>
					<div class="background">
						<h3>Forgeries</h3>
						<span class="label label-info my-label-info">Info</span> <span
							class="label label-warning my-label-warning">Warning</span> <span
							class="label label-danger my-label-danger">Danger</span> <span
							class="label label-success my-label-success">Success</span>
					</div>
				</div>
				<!-- /.row -->
				<div class="row">
					<div id="gradientTest" style="width:840px;"></div>
				</div>
				<!-- /.row -->
			</div>
			<!-- /.container-fluid -->
		</div>
		<!-- /#page-wrapper -->

	</div>
	<g:javascript src="bootstrap.js" />
	<g:javascript src="metisMenu.min.js" />
	<g:javascript src="raphael-min.js" />

	<g:javascript src="morris.min.js" />
	<g:javascript src="morris-data.js" />
	<g:javascript src="sb-admin-2.js" />

	<g:javascript src="bootstrap-notify.js" />
	<g:javascript src="notice.js" />

	<g:javascript src="gradient.js" />

</body>
<script>
function gradientButtons(){
	var zone=document.getElementById("gradientTest");
	//generate contents
	var arr=makeArray(100);
	console.log("The contents before sort are "+arr);
	//re-order contents
	var newArr=orderBy12(arr);
	console.log("The contents are "+newArr);
	//generate colors
	var colors=makeColors(100);
	//re-order colors
	colors=orderBy12(colors);
	makeButtonWithColor(zone,newArr,colors);
}
function makeArray(len){
	var arr=[];
	for(var i=1;i<=len;i++){
		arr.push(i);
	}
	return arr;
}
	function crippleMe(){
		var a=document.getElementById("totest");
		console.log("Now the status of link is: disabled:"+a.disabled+" href:"+a.href+" oldHref:"+a.oldHref);
		if(a.href!="#"&&a.disabled!=true){
			a.disabled=true;
			console.log("Disabling "+a.href);
			a.oldHref=a.href;
			a.href="#";
			console.log("Disabled. Now the status of link is: disabled:"+a.disabled+" href:"+a.href+" oldHref:"+a.oldHref);
		}else{
			console.log("Already disabled.");
		}
	}
	function healMe(){
		var a=document.getElementById("totest");
		console.log("Now the status of link is: disabled:"+a.disabled+" href:"+a.href+" oldHref:"+a.oldHref);
		if(a.disabled==true){
			a.disabled=false;
			console.log("Reactivating "+a.oldHref);
			a.href=a.oldHref;
			//a.oldHref="#";
			console.log("Enabled. Now the status of link is: disabled:"+a.disabled+" href:"+a.href+" oldHref:"+a.oldHref);
		}else{
			console.log("Nothing to enable");
		}
	}
</script>
<script>
function causeError(){

	 $.ajax({
	      type: "POST",
	      contentType: "application/json; charset=utf-8",
	      dataType: "json",
	      url : "${createLink(controller:'environment', action:'createError')}",
							async : false,
							// Error!
							error : function(xhr, status, error) {
								errorNotice("division");
							}
						})
				.done(
						function(jsonData) {
							console
									.log("Surprisingly the job returns with successful results ");
							console.log(jsonData);
						});
	}
</script>
<script>
	function makeButtons() {
		var zone = document.getElementById("colorForge");
		var colors = makeColors(100);
		for (var i = 0; i < 100; i++) {
			var theColor = colors[i];
			var button = document.createElement("button");
			button.className = "btn btn-outline btn-primary";
			button.innerHTML = i;
			button.style.backgroundColor = theColor;
			button.style.outline = "#fff thick";
			zone.appendChild(button);
		}

	}
	function makeColors(num) {
		var startHue = 207;
		var endHue = 89;
		var startSat = 87;
		var endSat = 58;
		var startLig = 67;
		var endLig = 75;
		var hueList = breakRange(startHue, endHue, num);
		var satList = breakRange(startSat, endSat, num);
		var ligList = breakRange(startLig, endLig, num);
		console.log("The hues: " + hueList);
		console.log("The saturations: " + satList);
		console.log("The lightness: " + ligList);
		var list = [];
		for (var i = 0; i < num; i++) {
			theColor = "hsl(" + hueList[i] + "," + satList[i] + "%,"
					+ ligList[i] + "%)";
			//console.log(theColor);
			list.push(theColor);
		}
		console.log("The colors are");
		console.log(list);
		return list;
	}
	function breakRange(start, end, count) {
		var step = (end - start) / count;
		console.log("Making a range with " + count + " members from " + start
				+ " to " + end + " at step of " + step);
		var list = [];
		for (var i = 0; i < count; i++) {
			var e = step * i;
			//console.log("Append "+e);
			list.push(start + step * i);
		}
		console.log("The list is " + list);
		return list;
	}
</script>
<script>
	$('[data-toggle="popover"]').popover({
		trigger : "hover"
	});
	makeButtons();
	var arr=gradientButtons();
	console.log("Made array");
	console.log(arr);
</script>
</html>
