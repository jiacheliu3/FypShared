//the known ages are in a list
function drawTreeMap(data,selector){
	console.log("Data for treemap");
	console.log(data);
	// instantiate d3plus
	var visualization = d3plus.viz()
	    .container(selector)  // container DIV to hold the visualization
	    .data(data)  // data to use with the visualization
	    .type("tree_map")   // visualization type
	    .id("name")         // key for which our data is unique on
	    .size("value")      // sizing of blocks
	    .draw();            // finally, draw the visualization!
}
function treeMapFormat(known){
	console.log("Here are the known ages");
	console.log(known);
	//put the known age list in a map
	var span=10;
	var map={};
	console.log("Known list is "+known);
	for(var i=0;i<known.length;i++){
		var age=known[i];
		var index=Math.floor(age/span);
		if(isNaN(index)){
			console.log("The age is not a number: "+age);
			continue;
		}
		var key=10*index+"-"+(10*index+10);
		//console.log("Converted key is "+key);
		if(map.hasOwnProperty(key)){
			map[key]+=1;
		}else{
			map[key]=1;
		}
	}
	//transform the map into an object list
	var list=[];
	for(var k in map){
		if(map.hasOwnProperty(k)){
			var v=map[k];
			if(v != parseInt(v)){
				console.log("Pair "+k+": "+v+" doesn't have an integer value");
			}else{
				list.push({'value':v,'name':k});
			}
			
		}
	}
	console.log("Transformed data for tree map");
	console.log(list);
	return list;
}