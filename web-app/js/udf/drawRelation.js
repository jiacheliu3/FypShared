//draw relationship
function printNetwork(set, id) {
	console.log("id is " + id);
	var node = document.getElementById(id);
	console.log("found" + node);
	for (var i = 0; i < set.length; i++) {
		var className = "leaf";
		var par = document.createElement("button");
		par.className="btn btn-outline btn-primary leaf";
		var content = document.createTextNode(set[i]);
		par.appendChild(content);
		//par.className = "leaf";
		node.appendChild(par);
	}

}
function printMap(array, id) {
	var d = document.getElementById(id);
	for ( var a in array) {
		var button = document.createElement("button");
		var t = document.createTextNode(array[a]);
		button.appendChild(t);
		d.appendChilde(button);
	}
}
function drawRelation() {
	var relation = relatedData;

	// var fed ="${forwarded}";
	// console.log(fed);
	// var fedArr = JSON.parse(fed.replace(/&quot;/g, '"'));
	var fedArr = relation["forwarded"];
	console.log(fedArr);
	printNetwork(fedArr, "fed");

	// var fing="${forwarding}";
	// console.log(fing);
	// var fingArr = JSON.parse(fing.replace(/&quot;/g, '"'));
	var fingArr = relation["forwarding"];
	console.log(fingArr);
	printNetwork(fingArr, "fing");

	// var cing ="${commenting}";
	// console.log(cing);
	// var cingArr = JSON.parse(cing.replace(/&quot;/g, '"'));
	var cingArr = relation["commenting"];
	console.log(cingArr);
	printNetwork(cingArr, "cing");

	// var ced ="${commented}";
	// console.log(ced);
	// var cedArr = JSON.parse(ced.replace(/&quot;/g, '"'));
	var cedArr = relation["commenting"];
	console.log(cedArr);
	printNetwork(cedArr, "ced");
}
