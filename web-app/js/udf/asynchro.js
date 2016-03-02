
function asynchroWordCloud() {
	// show a progress bar in the zone
	initProgressBar('keyword');
	startProgressBar('keyword');
	var jsonData = $.ajax(
					{
						url : "${createLink(controller:'environment', action:'asynchroKeywords')}",
						dataType : "json",
						async : true
					}).done(function(jsonData) {
				var tagged = true;// ok to study the network
				var keywordFinished = true;

				var list = jsonData;

				console.log(list);
				var map = list;
				// var map = JSON.parse(list.replace(/&quot;/g, '"'));
				console.log(map);

				var arr = [];
				for ( var e in map) {
					console.log(e, map[e]);
					arr.push([ e, map[e] ]);
				}

				console.log("start scaling");
				// var result=scale(map);
				// var result=map;
				console.log("List of keywords is" + arr);
				console.log("start drawing");
				WordCloud(document.getElementById('wordCloudCanvas'), {
					list : arr,
					weightFactor : 50,
					rotateRatio : 0.5,
					backgroundColor : '#ccccff',
					color : '#ffffff'
				});
				console.log("drawn");
				keywordFinished = true;
				console.log("Keyword finished, fire network");
				if (relationFinished) {
					console.log("Last job finished, fire network");
					// go on with network
					asynchroNetwork();
				}
				console.log("Stop progress bar");
				stopProgressBar('keyword');
			});
}
function asynchroRelation() {
	// show a progress bar in the zone
	initProgressBar('relation');
	startProgressBar('relation');
	var jsonData = $.ajax(
					{
						url : "${createLink(controller:'environment',action:'asynchroRelation')}",
						dataType : "json",
						async : true
					}).done(function(jsonData) {
				console.log("relationship:");
				console.log(jsonData);
				relatedData = jsonData;
				drawRelation();

				relationFinished = true;
				// stop progress bar and show
				stopProgressBar('relation');
				console.log("Relation finished, fire network");
				if (keywordFinished == true) {
					console.log("Last job finished, fire network");
					asynchroNetwork();
				}
			});
}
function asynchroNetwork() {
	console.log("Check job status: " + keywordFinished + ", "
			+ relationFinished);

	console.log("Network fired");
	var jsonData = $.ajax(
					{
						url : "${createLink(controller:'environment', action:'asynchroNetwork')}",
						dataType : "json",
						async : true
					}).done(function(jsonData) {

				console.log(jsonData);
				// console.log(jsonText);
				document.getElementById("result").innerHTML = "Done";
				console.log(jsonData.all);

				// set global variable
				similar = jsonData;
				console.log("The object got");
				// console.log(similar);
				draw();
				// finially able to stop progress bar
				stopProgressBar("network");
			});
	;

}
function hangOnTillNetworkReady() {
	initProgressBar('network');
	startProgressBar("network");
}
