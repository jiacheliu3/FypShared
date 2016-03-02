function sayNothing(job) {
	var zone = document.getElementById(job + "Status");
	var link = document.getElementById(job + "Link");
	// console.log("Experiment start.");
	var pseudoZone = document.getElementById(job + "LinkFalse");
	// console.log(pseudoZone);
	var realZone = document.getElementById(job + "LinkTrue");
	// console.log(realZone);
	var label = document.getElementById(job + "LinkLabel");

	var action;
	// console.log("Found zone for job "+job+": "+zone);

	action = 4;
	//updateButton(action, pseudoZone, realZone, label);
	//updateLink(action,link,label);
	updateCircle(action,link,label);
	updateZone(action, zone);
	return false;

}
function sendCompleteNotice(job) {
	// alert the user that crawler is done
	sendNotice(job);
}
function sendRestartNotice() {
	restartReminder();
}
// The only job it starts is the asynchronous crawler
function asynchroMonitor() {

	// check job status and react
	console.log("Requesting job status");
	var jsonData = $
			.ajax({
				url : "/CodeBigBroSub/framework/statusMonitor",
				dataType : "json",
				async : false,
				// handle success and failure
				// success:
				error : function(xhr, status, error) {
					errorNotice("Status monitoring");
				}
			})
			.done(
					function(jsonData) {
						// check if data is null
						if (!jsonData) {
							console.log("Job is empty.");
							return;
						}
						// translate the status to js variables
						var status = jsonData;
						console.log(status);
						var crawlerStarted = status["crawlStarted"];
						var keywordsStarted = status["keywordsStarted"];
						var relationStarted = status["relationStarted"];
						var networkStarted = status["networkStarted"];
						var clusterStarted = status["clusterStarted"];
						var deepCrawlStarted = status["deepCrawlStarted"];

						var crawlerComplete = status["crawlComplete"];
						var keywordsComplete = status["keywordsComplete"];
						var relationComplete = status["relationComplete"];
						var networkComplete = status["networkComplete"];
						var clusterComplete = status["clusterComplete"];
						var deepCrawlComplete = status["deepCrawlComplete"];

						// decide whether there should be any study started
						var nothingToShow = status["nothingToShow"];
						console.log("No data to study?" + nothingToShow);

						if (crawlerComplete) {
							var pseudoEmpty;
							var accountLabel = document
									.getElementById("accountLinkLabel");
							//accountLabel.className = "label label-success pull-right labelSpan udf-success";
							//accountLabel.innerHTML = 'Complete';
							accountLabel.className = "fa fa-circle-o text-green pull-right";
							var crawlerPanel = document
									.getElementById("crawlerStatus");
							if (crawlerPanel) {
								crawlerPanel.className = "alert alert-success udf-success";
								crawlerPanel.innerHTML = "Complete";
							}
						}
						if (updateStatus('deepCrawl', deepCrawlStarted,
								deepCrawlComplete)) {
						} else {
							// if the crawler is done, notify the user
							if (isTrue(deepCrawlComplete)
									&& deepCrawlerNoticeSent == false) {
								sendRestartNotice();
								deepCrawlerNoticeSent = true;
							}
						}
						// if there is nothing to study and show, mark all jobs
						// as nothing to show
						if (nothingToShow == true || nothingToShow == 'true') {
							sayNothing('keyword');
							sayNothing('relation');
							sayNothing('network');
							sayNothing('cluster');
							console.log("All jobs marked as nothing to study.");
							return;
						}
						// true if the job needs to be started
						if (react('crawl', crawlerStarted, crawlerComplete)) {
							// asynchroCrawl();
						}
						// false if the job is complete
						else {
							if (isTrue(crawlerComplete)
									&& crawlerNoticeSent == false) {
								sendCompleteNotice('crawler');
								crawlerNoticeSent = true;
							}
						}
						// change the page content based on status and decide
						// next step

						if (react('keyword', keywordsStarted, keywordsComplete)) {
							// asynchroWordCloud();
						} else {
							if (isTrue(keywordsComplete)
									&& keywordNoticeSent == false) {
								sendCompleteNotice('keyword');
								keywordNoticeSent = true;
							}
						}
						if (react('relation', relationStarted, relationComplete)) {
							// asynchroRelation();
						} else {
							if (isTrue(relationComplete)
									&& relationNoticeSent == false) {
								sendCompleteNotice('relation');
								relationNoticeSent = true;
							}
						}

						if (react('network', networkStarted, networkComplete,
								keywordsComplete, relationComplete)) {
							// asynchroNetwork();
						} else {
							if (isTrue(networkComplete)
									&& networkNoticeSent == false) {
								sendCompleteNotice('network');
								networkNoticeSent = true;
							}
						}

						if (react('cluster', clusterStarted, clusterComplete,
								networkComplete)) {
							// asynchroCluster();
						} else {
							if (isTrue(clusterComplete)
									&& clusterNoticeSent == false) {
								sendCompleteNotice('cluster');
								clusterNoticeSent = true;
							}

						}

					});
}
//only update the status without manipulating the href
function updateStatus(jobName) {
	var job = arguments[0];
	// console.log("The job is "+job);
	var start = arguments[1];
	var complete = arguments[2];

	// and all other arguments are the dependency to check before next step
	// console.log("Status: " + arguments);

	var zone = document.getElementById(job + "Status");
	var link = document.getElementById(job + "Link");
	// console.log("Experiment start.");
	var pseudoZone = document.getElementById(job + "LinkFalse");
	// console.log(pseudoZone);
	var realZone = document.getElementById(job + "LinkTrue");
	// console.log(realZone);
	var label = document.getElementById(job + "LinkLabel");

	var action;
	// console.log("Found zone for job "+job+": "+zone);
	if (isTrue(complete)) {
		action = 1;
		//updateLink(action,link,label);
		updateCircle(action,link,label);
		updateZone(action, zone);

		return false;
	} else if (isTrue(start)) {
		// set the button color
		action = 2;
		//updateLink(action,link,label);
		updateCircle(action,link,label);
		updateZone(action, zone);
		return false;
	} else {

		// disable the button

		action = 3;

		// check pre-requisites
		var length = arguments.length;
		var togo = true;
		// check all the dependencies to see whether to go on
		for (var i = 3; i < length; i++) {
			if (!isTrue(arguments[i]))
				togo = false;
		}
		// console.log(togo);
		//updateButton(action, pseudoZone, realZone, label);
		//updateLink(action,link,label);
		
		//updateCircle(action,link,label); //No need to update the circle for it's already in the right color
		updateZone(action, zone);
		return togo;
	}
}
// update the section and decide whether the service should be called again
function react() {
	var job = arguments[0];
	// console.log("The job is "+job);
	var start = arguments[1];
	var complete = arguments[2];

	// and all other arguments are the dependency to check before next step
	// console.log("Status: " + arguments);

	var zone = document.getElementById(job + "Status");
	var link = document.getElementById(job + "Link");
	// console.log("Experiment start.");
	var pseudoZone = document.getElementById(job + "LinkFalse");
	// console.log(pseudoZone);
	var realZone = document.getElementById(job + "LinkTrue");
	// console.log(realZone);
	var label = document.getElementById(job + "LinkLabel");

	var action;
	// console.log("Found zone for job "+job+": "+zone);
	if (isTrue(complete)) {
		action = 1;
		//updateLink(action,link,label);
		updateCircle(action,link,label);
		updateZone(action, zone);

		return false;
	} else if (isTrue(start)) {
		// set the button color
		action = 2;
		//updateLink(action,link,label);
		updateCircle(action,link,label);
		updateZone(action, zone);
		return false;
	} else {

		// disable the button

		action = 3;

		// check pre-requisites
		var length = arguments.length;
		var togo = true;
		// check all the dependencies to see whether to go on
		for (var i = 3; i < length; i++) {
			if (!isTrue(arguments[i]))
				togo = false;
		}
		// console.log(togo);
		//updateButton(action, pseudoZone, realZone, label);
		//updateLink(action,link,label);
		updateCircle(action,link,label);
		updateZone(action, zone);
		return togo;
	}
}
function isTrue(value) {
	if (value == true || value == 'true')
		return true;
	else
		return false;
}
// update the zone in account page only
function updateZone(index, zone) {
	// console.log("action is:"+index);
	if (zone == null) {
		// console.log("Zone is null.");
		return;
	}
	if (index == 3) {// not started
		zone.innerHTML = "Not yet started.";
		zone.className = "alert alert-danger udf-danger";
	} else if (index == 2) {// started yet not finished
		zone.innerHTML = "Started but not yet finished.";
		zone.className = "alert alert-warning udf-warning";
	} else if (index == 1) {// complete
		zone.innerHTML = "Job complete. You can go to the corresponding link to check the details.";
		zone.className = "alert alert-success udf-success";
	} else if (index == 4) {
		zone.innerHTML = "Nothing to show since there is no weibo known by the system.";
		zone.className = "alert alert-info udf-info";
	} else {
		console.log("what's wrong with the zone");
	}
}
// use little buckles for task status
function updateCircle(index,link,label){
	// console.log("action is:"+index);
	if (!link) {
		console.log("Link is null.");
		return;
	}
	if (!label) {
		console.log("Label is null.");
		return;
	}
	if (index == 3) {// not started
		disableLink(link);
		label.className = "fa fa-circle-o text-grey pull-right";
		//label.innerHTML = 'Not Started';
	} else if (index == 2) {// started yet not finished
		disableLink(link);
		label.className = "fa fa-circle-o text-yellow pull-right";
		//label.innerHTML = 'Running';
	} else if (index == 1) {// complete
		enableLink(link);
		label.className = "fa fa-circle-o text-green pull-right";
		//label.innerHTML = 'Complete';
	} else if (index == 4) {
		disableLink(link);
		label.className = "fa fa-circle-o text-aqua pull-right";
		//label.innerHTML = 'Nothing';
	} else {
		console.log("what's wrong");
	}
}
// update the link to show status
function updateLink(index, link, label) {
	// console.log("action is:"+index);
	if (!link) {
		console.log("Link is null.");
		return;
	}
	if (!label) {
		console.log("Label is null.");
		return;
	}
	if (index == 3) {// not started
		disableLink(link);
		label.className = "label label-default pull-right labelSpan";
		label.innerHTML = 'Not Started';
	} else if (index == 2) {// started yet not finished
		disableLink(link);
		label.className = "label label-warning  pull-right labelSpan udf-warning";
		label.innerHTML = 'Running';
	} else if (index == 1) {// complete
		enableLink(link);
		label.className = "label label-success  pull-right labelSpan udf-success";
		label.innerHTML = 'Complete';
	} else if (index == 4) {
		disableLink(link);
		label.className = "label label-info  pull-right labelSpan udf-info";
		label.innerHTML = 'Nothing';
	} else {
		console.log("what's wrong");
	}
}
// control the buttons in nav bar
function updateButton(index, pseudo, real, label) {
	// console.log("action is:"+index);
	if (!pseudo) {
		console.log("Pseudo zone is null.");
		return;
	}
	if (!real) {
		console.log("Real zone is null.");
		return;
	}
	if (index == 3) {// not started
		// console.log("Job not started");
		real.style.display = 'none';
		pseudo.style.display = 'block';
		label.className = "label label-default";
		label.innerHTML = 'Not Started';
	} else if (index == 2) {// started yet not finished
		// console.log("Job not finished.");
		real.style.display = 'none';
		pseudo.style.display = 'block';
		label.className = "label label-warning udf-warning";
		label.innerHTML = 'Started. Please wait.';
	} else if (index == 1) {// complete
		real.style.display = 'block';
		pseudo.style.display = 'none';
		label.className = "label label-success udf-success";
		label.innerHTML = 'Complete';
	} else if (index == 4) {
		real.style.display = 'none';
		pseudo.style.dislay = 'block';
		label.className = "label label-info udf-info";
		label.innerHTML = 'Nothing to show';
	} else {
		console.log("what's wrong");
	}
}
function disableLink(zone) {
	//get the <a> inside the link
	var links=zone.getElementsByTagName("a");
	var link=links[0];
	//if the zone has a flag, it cannot be disabled
	var value=link.myFlag;
	console.log("The value of the field is "+value);
	console.log("Now the status of link is: disabled:"+link.disabled+" href:"+link.href+" oldhref:"+link.oldhref);
	if (link.disabled!=true&&link.href!="#") {
		link.oldhref = link.href;
		link.href = "#";
		link.disabled = true;
		console.log("Disabled. Now the status of link is: disabled:"+link.disabled+" href:"+link.href+" oldHref:"+link.oldHref);
	}
	
}
//test function
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
function enableLink(zone) {
	if(zone.value=="important"){
		console.log("No need to enable the zone.");
		return;
	}
	var links=zone.getElementsByTagName("a");
	var link=links[0];
	if (!link.oldhref) {
		console.log("The old href is not found for link!");
		console.log(link);
	}
	console.log("Now the status of link is: disabled:"+link.disabled+" href:"+link.href+" oldhref:"+link.oldhref);
	if (link.disabled==true) {
		link.disabled = false;
		console.log("Retracting the old href for link "+link.oldhref);
		link.href = link.oldhref;
		//link.oldhref = "#";
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
function beforeDeepCrawl() {
	window.alert("The crawler will be working in the background. The status will be tracked and shown in the account page. You will be notified when crawler finishes.");
}