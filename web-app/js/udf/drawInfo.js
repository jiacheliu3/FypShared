function showUserInfo(jsonData) {
	if(!jsonData["weiboCountSpan"]){
		//info is invalid
		return false;
	}
	var zone=document.getElementById("weiboStats");
	function clinch(panel,element){
		if(panel==undefined){
			console.log("Panel not found");
			return;
		}
		if(!element){
			console.log("Element not found"+element);
		}
		//console.log("Element is ");
		//console.log(element);
		var d=document.createElement('div');
		d.className="col-lg-3";
		d.innerHTML=element;
		panel.appendChild(d);
	}
	//console.log("Check the data first");
	//console.log(jsonData);
	
	
	clinch(document.getElementById("weiboCountZone"),jsonData["weiboCountSpan"]);
	clinch(document.getElementById("followingZone"),jsonData["following"]);
	clinch(document.getElementById("fansZone"),jsonData["followed"]);
	clinch(document.getElementById("groupingZone"),jsonData["grouping"]);
	clinch(document.getElementById("atZone"),jsonData["mentioned"]);
	console.log("Finished displaying user link zones.");
	return true;
	
	
}
function predictTime(jsonData) {
	var status=document.getElementById("knowledgeStatus");
	var zone=document.getElementById("crawlerEstimation");
	var pageCount=jsonData["pageCount"];
	var threadCount=jsonData["botCount"];
	console.log("Page:"+pageCount+" Thread:"+threadCount);
	if(!pageCount||!threadCount){
		//the info is not available
		return false;
	}
	
	var time=pageCount/threadCount;
	function write(zone,text){
		var d=document.createElement('div');
		d.innerHTML=text;
		zone.appendChild(d);
	}
	//see how much is know already
	var known=jsonData["knownCount"];
	var all=jsonData["weiboCount"];
	var ratio=known/all;
	if(ratio==1){
		write(status,known+" out of "+all+" microblogs are known by the system already. No need to crawl user data since all is known already.");
		time=0;
	}
	else if(ratio>0.8){
		write(status,known+" out of "+all+" microblogs are known by the system already. You may not want to crawl all the user's microblogs because it may take a long time to finish.");
	}else if(ratio>0.5){
		write(status,known+" out of "+all+" microblogs are known by the system already. It's up to you whether to crawl all the user's microblogs.");
	}else{
		write(status,known+" out of "+all+" microblogs only are known by the system. It's suggested a deep crawl is done on all the user's microblogs.");
	}
	//predict time needed
	if(time=0){
		write(zone,"Deep crawling not needed.");
	}
	else if(time<=10){
		write(zone,"Estimated time needed is ~10s. Please don't step away.");
	}else if(time<=30){
		write(zone,"Estimated time is ~30s. Have a drink and get back.");
	}else if(time<=60){
		write(zone,"1 min is not so long. Be patient.");
	}else if(time>=1200){
		write(zone,"The user has quite some contents to crawl Please do sth else and don't close the page.");
	}else{
		write(zone,"The job can't finish in around 10 min. Please take a rest and get back.");
	}
	console.log("Finished time prediction.");
	return true;
}
function updateCrawlStatus(jsonData){
	var progress=document.getElementById('deepCrawlStatusBar');
	var got=jsonData["gotCount"];
	var all=jsonData["weiboCount"];
	if(!got)
		got=0;
	//test with growth
	var ratio=got/all*100;
	progress.style.width=ratio+"%";
	//console.log("Now the progress is "+ratio);
	var textSpan=document.getElementById("deepCrawlStatusSpan");
	//console.log("Found text span "+textSpan);
	textSpan.innerHTML=got+"/"+all+" microblogs finished";
}
