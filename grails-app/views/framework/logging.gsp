<html>
<head>
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
<!-- Must-use JS libraries go first -->
<g:javascript src="toolbox/jquery.min.js" />
</head>
<body>
	<div class="col-lg-8">
		<div class="box box-solid">
			<div class="box-header with-border">
				<i class="fa fa-text-width"></i>
				<h3 class="box-title">Log zone</h3>
			</div>
			<!-- /.box-header -->
			<div class="box-body logHolder">
				<dl id="logZone">
					<dt>Description lists</dt>
					<dd>A description list is perfect for defining terms.</dd>
					<dt>Keyword extractor</dt>
					<dd>
						[21:36:55] 318BF620FFC558C1AABDB48436427768 INFO SepManager -
						Create promise on 36 to 45 of contents.<br /> [21:36:55]
						318BF620FFC558C1AABDB48436427768 INFO SepManager - Create promise
						on 45 to 54 of contents.<br /> [21:36:55]
						318BF620FFC558C1AABDB48436427768 INFO SepManager - Create promise
						on 54 to 63 of contents.<br /> [21:36:55]
						318BF620FFC558C1AABDB48436427768 INFO SepManager - Mission 7 has 9
						items to segment.<br /> [21:36:55]
						318BF620FFC558C1AABDB48436427768 DEBUG SepManager - Process index
						45 in contents.<br /> [21:36:55] 318BF620FFC558C1AABDB48436427768
						INFO SepManager - Mission 7 has 9 items to segment.<br />
						[21:36:55] 318BF620FFC558C1AABDB48436427768 DEBUG SepManager -
						Process index 54 in contents.<br /> [21:36:55]
						318BF620FFC558C1AABDB48436427768 DEBUG SepManager - Process index
						27 in contents.<br />
					</dd>
				</dl>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- /.box -->
	</div>
	<!-- ./col-lg-8 -->

<g:javascript src="toolbox/bootstrap.js" />
<script>
	function showLog(logs) {
		console.log("Got logs from back end");
		console.log(logs);
		var logZone = document.getElementById("logZone");
		for (var i = 0; i < logs.length; i++) {
			var dd = document.createElement('dd');
			dd.innerHTML = logs[i];
			logZone.appendChild(dd);
		}
	}
	function asynchroLog() {
		console.log("request logs");
		var jsonData = $.ajax({
			url : "/CodeBigBroSub/environment/asynchroLog",
			dataType : "json",
			async : true,
			error : function(xhr, status, error) {
				errorNotice("Error getting log");
			}
		}).done(function(jsonData) {
			showLog(jsonData);
		});
	}
	setInterval(function() {
		asynchroLog();
	}, 20000);
</script>
</body>
</html>