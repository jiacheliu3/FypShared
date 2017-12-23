function genderRatio(maleNum,femaleNum,maleZone,femaleZone,width,height){
	var sum=maleNum+femaleNum;
	console.log(sum+" people in total.");
	var malePerc=maleNum/sum;
	var femalePerc=Math.round(femaleNum/sum);
	var maleWidth=width*malePerc;
	console.log("Male should process "+maleWidth);
	maleZone.style.clip="rect("+0+"px,"+maleWidth+"px,"+height+"px,"+0+"px)";
	femaleZone.style.clip="rect("+0+"px,"+width+"px,"+height+"px,"+maleWidth+"px)";
}
function makePeople(zone,male,female){
	console.log("Going to draw "+male+" men and "+female+" women.");
	var maleLeft=male;
	var femaleLeft=female;
	var total=male+female;
	if(total>100){
		console.log("The total is "+total+". Activate approximation.");
		maleLeft=Math.round(100*male/total);
		femaleLeft=100-maleLeft;
		total=100;
		console.log("After approx. there are "+maleLeft+" men and "+femaleLeft+" women.");
	}
	for(var i=0;i<total;i++){
		var g;
		if(maleLeft>0){
			maleLeft--;
			g='male';
		}else if(femaleLeft>0){
			femaleLeft--;
			g='female';
		}else{
			console.log("In the "+i+"th round there is no person left.");
			return;
		}
		var pic=document.createElement("img");
		pic.src="/CodeBigBroSub/assets/"+g+".png";
		pic.className="biped";
		zone.appendChild(pic);
	}
	
}