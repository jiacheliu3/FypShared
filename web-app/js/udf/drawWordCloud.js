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
function scale(array) {
	console.log("before scaling");
	console.log(array);
	var max = 100;
	var min = 50;
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
