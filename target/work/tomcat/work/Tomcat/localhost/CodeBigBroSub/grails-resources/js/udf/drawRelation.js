//draw relationship
function printNetwork(set, id) {
	console.log("id is " + id);
	var node = document.getElementById(id);
	console.log("found" + node);
	for (var i = 0; i < set.length; i++) {
		var className = "leaf";
		var par = document.createElement("button");
		par.className = "btn btn-outline btn-primary leaf";
		var content = document.createTextNode(set[i]);
		par.appendChild(content);
		// par.className = "leaf";
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
/* Deprecated */
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
function displayInfo(relation) {
	var fedArr = relation["forwarded"];
	var fedZone = document.getElementById("fedInfo");
	// showNumber("fed",fedZone,fedArr);
	showNumberInButton("fed", fedArr);
	var fingArr = relation["forwarding"];
	var fingZone = document.getElementById("fingInfo");
	// showNumber("fing",fingZone,fingArr);
	showNumberInButton("fing", fingArr);
	var cingArr = relation["commenting"];
	var cingZone = document.getElementById("cingInfo");
	// showNumber("cing",cingZone,cingArr);
	showNumberInButton("cing", cingArr);
	var cedArr = relation["commented"];
	var cedZone = document.getElementById("cedInfo");
	// showNumber("ced",cedZone,cedArr);
	showNumberInButton("ced", cedArr);

}
function drawSupport(support) {
	console.log("The support is ");
	console.log(support);
	if (support) {
		//console.log("Able to proceed with support info:");
	} else {
		console.log("support not found");
		return;
	}
	var comment = support["commented"];
	var forward = support["forwarded"];
	var like = support["liked"];
	var mention=support["mentioning"];

	var total = support["total"];
	console.log("Got comments map from user");
	console.log(comment);
	console.log("Got likes map from user");
	console.log(like);
	console.log("Got forwards map from user");
	console.log(forward);
	console.log("Got mentioning map from user");
	console.log(mention);
	console.log("total is "+total);

	drawSupportTable(comment, "comment", total);
	drawSupportTable(forward, "forward", total);
	drawSupportTable(like, "like", total);
    drawSupportTable(mention, "mention", total);
}
function drawSupportTable(map, name, total) {
	console.log("Processing table " + name);
	console.log("Support table to draw: ");
	console.log(map);
	var zone = document.getElementById(name + "TableBody");

    function isEmpty(obj) {
        return Object.keys(obj).length === 0;
    }
    // If no info
    if(isEmpty(map)){
    	for (var i=0;i<3;i++){
            var tr = document.createElement("tr");
            var u = document.createElement("td");
            u.innerHTML = "empty";
            var c = document.createElement("td");
            c.innerHTML = " ";
            var r = document.createElement("td");
            r.innerHTML = " ";

            tr.appendChild(u);
            tr.appendChild(c);
            tr.appendChild(r);

            zone.appendChild(tr);
		}

		console.log("Created empty table");
    	return;
	}

    // If the map is not empty
	for ( var f in map) {
		if (map.hasOwnProperty(f)) {
			var count = map[f];
			var tr = document.createElement("tr");
			var u = document.createElement("td");
			u.innerHTML = f;
			var c = document.createElement("td");
			c.innerHTML = count;
			var r = document.createElement("td");
			var ratio = Math.round(count) / total;
			var perc = Math.floor(ratio * 10000) / 100 + "%";
			r.innerHTML = perc;

			tr.appendChild(u);
			tr.appendChild(c);
			tr.appendChild(r);

			zone.appendChild(tr);
		}
	}
}
function drawPattern(patterns) {
	console.log("The patterns are ");
	console.log(patterns);

	var total=0;
	for(var f in patterns){
		var p = patterns[f];
		console.log(p);
		if(p.support!=null)
			total+=p.support
		else{
			console.log("No support count" );
		}
	}
	console.log("Total is "+total);
	drawPatternTable(patterns, "pattern", total);

}
function drawPatternTable(array, name, total) {
	console.log("Processing table " + name);
	var zone = document.getElementById(name + "TableBody");
	if (!zone) {
		console.log("Cannot locate table for " + name);
	}
	for ( var idx in array) {
		// get element and extract fields
		var obj = array[idx];
		var names = obj["names"];// array of names included
		if (!names || names.length == 0) {
			console.log("No names for this pattern!");
		} else {
			var nameString = names.toString();
			console.log("Convert the user names to string:" + nameString);
		}
		var count = obj["support"];

		var tr = document.createElement("tr");
		var u = document.createElement("td");
		u.innerHTML = nameString;
		var c = document.createElement("td");
		c.innerHTML = count;
		var r = document.createElement("td");
		var ratio = Math.round(count) / total;
		var perc = Math.floor(ratio * 10000) / 100 + "%";
		r.innerHTML = perc;

		tr.appendChild(u);
		tr.appendChild(c);
		tr.appendChild(r);
		// create hover action on table row
		var keywords = obj["keywords"];
		console.log("Keywords under this pattern are");
		console.log(keywords);
		if (!keywords || keywords.length == 0) {
			console.log("No keywords for this pattern.");
			r.setAttribute("data-toggle", "popover");
			r["title"] = "Keywords extracted from the content shared by the users.";
			r.setAttribute("data-content", "");
			// name.setAttribute("trigger","hover");
			r.setAttribute("data-placement", "buttom");
		} else {
			r.setAttribute("data-toggle", "popover");
			r["title"] = "Keywords extracted from the content shared by the users.";
			r.setAttribute("data-content", keywords.toString());
			// name.setAttribute("trigger","hover");
			r.setAttribute("data-placement", "buttom");
		}
		zone.appendChild(tr);
	}
	// activate popover event on table rows
	$('[data-toggle="popover"]').popover({
		trigger : "hover"
	});

}
/* Deprecated */
function showNumber(jobName, zone, list) {
	if (!zone) {
		console.log("Zone is not found for " + jobName);
		return;
	}
	var h = document.createElement('h4');
	var span = document.createElement('span');

	span.innerHTML = list.length;
	span.className = "label label-primary";
	h.appendChild(span);
	zone.appendChild(h);
}
function showNumberInButton(name, list) {
	var zone = document.getElementById(name + "Size");
	if (!zone) {
		console.log("Zone is not found for " + jobName);
		return;
	}
	zone.innerHTML = list.length;

}