function wordCloudFormat(list,max,min){
	console.log("The list before sort is");
	console.log(list);
	var map = scale(list,max,min);
	console.log(map);
	var arr = [];
	for ( var e in map) {
		//console.log(e, map[e]);
		arr.push([ e, map[e] ]);
	}
	console.log("The map after transformation");
	console.log(arr);
	return arr;
}
//draw word cloud
function convertRange(value, low, high) {
	console.log("before" + value);
	var result = (value - low) * (100 - 50) / (high - low) + 50;
	console.log("after" + result);
	return result;
}
function sortObject(obj) {
	var arr = [];
	for ( var prop in obj) {
		if (obj.hasOwnProperty(prop)) {
			arr.push({
				'key' : prop,
				'value' : obj[prop]
			});
		}
	}
	arr.sort(function(a, b) {
		return b.value - a.value;
	});
	// arr.sort(function(a, b) {
	// a.value.toLowerCase().localeCompare(b.value.toLowerCase()); }); //use
	// this to sort as strings
	return arr; // returns array
}
function scale(array,max) {
	var max = typeof max !== 'undefined' ? max : 100;
	var min = typeof min !== 'undefined' ? min : 30;
	console.log("before scaling");
	console.log(array);
	//var max = 100;
	console.log("The max value is "+max+", min is "+min);
	var high = -1;
	var low = 100000;
	var newArray = {};
	for ( var k in array) {
		if (array.hasOwnProperty(k)) {
			var weight=array[k];
			// console.log(typeof array[k]);
			if ( weight> high)
				high = weight;
			if (weight < low)
				low = weight;
		}
	}
	console.log("high: " + high);
	console.log("low: " + low);
	// high-low=0 if they are equal
	if (high == low) {
		console.log("Weights are all equal, assign all to 50.");
		for ( var k in array) {
			if (array.hasOwnProperty(k)) {
				array[k]=50.0;
			}
		}
	} else {
		for ( var k in array) {
			if (array.hasOwnProperty(k)) {
				var v=array[k];
				array[k]=(v - low) * (max - min) / (high - low) + min+0.0;
			}
		}
		
	}
	console.log("After scaling");
	console.log(array);
	return array;
}
function processSimilarMap(obj) {
	console.log("obj is " + obj);
	var myDiv = document.getElementById('similar');
	for ( var o in obj) {
		if (obj.hasOwnProperty(o)) {
			var k = obj[o];
			// document.getElementById("k").innerHTML=k;
			console.log(k);
			var nodes = k.nodes; // cannot find the field here
			console.log(nodes);
			var edges = k.edges; // cannot find the field here
			console.log(edges);
			myDiv.innerHTML += 'nodes: ';
			// printNetwork(nodes,"similar");
			for (var i = 0; i < nodes.length; i++) {
				myDiv.innerHTML += nodes[i] + ' ';

			}
			myDiv.innerHTML += '<br/>edges: ';
			for ( var e in edges) {
				if (edges.hasOwnProperty(e)) {
					myDiv.innerHTML += e + '-' + edges[e] + ' ';
				}
			}

		}
	}
}
function drawWordCloud(data,zone){
	console.log("Data for wordcloud is ");
	console.log(data);
	console.log("start drawing");
	WordCloud(zone, {
		list : data,
		//weightFactor : 50,
		rotateRatio : 0,
		backgroundColor : '#ffffff',//ccccff before
		color : function(word, weight) {
			//console.log("word:" + word + " weight:" + weight);
			if (weight == 50)
				return '#99ccff';
			else if (weight > 50 && weight < 75)
				return '#9933ff';
			else if (weight == 75)
				return '#3399cc';
			else if (weight > 75 && weight < 100)
				return '#990033';
			else if (weight == 100)
				return '#cc0033';
			else
				return '#000000';

		},
	});
	console.log("drawn");
}
