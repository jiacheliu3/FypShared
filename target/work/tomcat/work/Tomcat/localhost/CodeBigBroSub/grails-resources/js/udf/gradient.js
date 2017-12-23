// For an array that is passed to the function, re-order it in a snake-and-ladder manner
function orderBy12(array) {
	var siz = array.length;
	var row = 0;
	var arr12 = [];
	var newArr = [];
	for ( var i in array) {
		//if the array is going to be filled, push the last one and clear buffer
		if (arr12.length >= 11 || i == siz - 1) {
			arr12.push(array[i]);
			//console.log("Now the buffer has length "+arr12.length);
			newArr=appendArray(newArr,arr12,row%2);
			console.log("Now the array is "+newArr);
			arr12=[];//renew the buffer
			row++;
		}else{
			arr12.push(array[i]);
		}
	}
	//console.log("The re-ordering finished with "+row+" rows.");
	console.log("The array is now "+newArr);
	return newArr;
}
function appendArray(arr,suffixArr,label){
	console.log("To append array "+suffixArr+" to "+arr);
	var newArr=[];
	//append the suffix to array
	if(label==0){
		//console.log("Appending array "+suffixArr);
		newArr=arr.concat(suffixArr);
	}else if(label==1){
		//console.log("Reversing array "+suffixArr);
		suffixArr=suffixArr.reverse();
		//console.log("Appending array "+suffixArr);
		newArr=arr.concat(suffixArr);
	}
	return newArr;
}
function makeButtons(zone, contents) {
	var count = contents.length;
	var colors = makeColors(count);
	for (var i = 0; i < count; i++) {
		var theColor = colors[i];
		var text = contents[i];
		var button = document.createElement("button");
		button.className = "btn btn-outline btn-primary keywords";
		button.innerHTML = text;
		button.style.backgroundColor = theColor;
		zone.appendChild(button);
	}

}
function makeButtonWithColor(zone,contents,colors,fontColors,fontSize){
	var count = contents.length;
	for (var i = 0; i < count; i++) {
		var theColor = colors[i];
		var text = contents[i];
		var button = document.createElement("button");
		//button.className = "btn btn-outline btn-primary keywords";
		button.className = "btn btn-outline btn-primary keywords";
		button.innerHTML = text;
		button.style.backgroundColor = theColor;
		button.style.color=fontColors[i];
		var siz=fontSize[i];
		button.style.fontSize=siz+"px";
		zone.appendChild(button);
	}
}
//make colors between blue and white
function makeColors(num) {
	var startHue = 241;
	var endHue = 189;
	var startSat = 97;
	var endSat = 28;
	var startLig = 75;
	var endLig = 100;
	var hueList = breakRange(startHue, endHue, num);
	var satList = breakRange(startSat, endSat, num);
	var ligList = breakRange(startLig, endLig, num);
	//console.log("The hues: " + hueList);
	//console.log("The saturations: " + satList);
	//console.log("The lightness: " + ligList);
	var list = [];
	for (var i = 0; i < num; i++) {
		theColor = "hsl(" + hueList[i] + "," + satList[i] + "%," + ligList[i]
				+ "%)";
		//console.log(theColor);
		list.push(theColor);
	}
	console.log("The colors are");
	console.log(list);
	return list;
}
//make colors in range
function makeColorsInRange(startHue,startSat,startLig,endHue,endSat,endLig,num) {
	var hueList = breakRange(startHue, endHue, num);
	var satList = breakRange(startSat, endSat, num);
	var ligList = breakRange(startLig, endLig, num);
	//console.log("The hues: " + hueList);
	//console.log("The saturations: " + satList);
	//console.log("The lightness: " + ligList);
	var list = [];
	for (var i = 0; i < num; i++) {
		theColor = "hsl(" + hueList[i] + "," + satList[i] + "%," + ligList[i]
				+ "%)";
		//console.log(theColor);
		list.push(theColor);
	}
	console.log("The colors are");
	console.log(list);
	return list;
}
//make gray scale colors
function makeBlackAndWhiteColors(startLig,endLig,num){
	var ligList = breakRange(startLig, endLig, num);
	var list = [];
	for (var i = 0; i < num; i++) {
		var hex=Math.floor(ligList[i]).toString(16);
		var theColor='#'+hex+hex+hex;
		//var theColor = "hsl(0,0,"+ligList[i]+"%)";
		//console.log(theColor);
		list.push(theColor);
	}
	return list;
}
function makeHattedButton(zone,contents,colors,fontSize){
	var count = contents.length;
	for (var i = 0; i < count; i++) {
		var theColor = colors[i];
		var text = contents[i];
		var button = document.createElement("button");
//		button.className = "btn btn-primary keywords btn-topped";
		button.className = "keywords btn-topped";
		button.innerHTML = text;
		button.style.borderTopColor=colors[i];
		var siz=fontSize[i];
		button.style.fontSize=siz+"px";
		zone.appendChild(button);
	}
}
function breakRange(start, end, count) {
	var step = (end - start) / count;
	console.log("Making a range with " + count + " members from " + start
			+ " to " + end + " at step of " + step);
	var list = [];
	for (var i = 0; i < count; i++) {
		var e = step * i;
		// console.log("Append "+e);
		list.push(start + step * i);
	}
	console.log("The list is " + list);
	return list;
}