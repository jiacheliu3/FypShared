function drawDirectedNetwork(data, selector) {
	var links = data["links"];
	var nodes = data["nodes"];
	// generate the name:pagerank map
	var rankMap = {};
	nodes.forEach(function(d) {
		rankMap[d.name] = d.pagerank;
	});

	console.log("The data are");
	console.log(data);

	// convert the nodes to a index map
	var nodeMap = {};
	for(var i=0;i<nodes.length;i++){
		var n=nodes[i];
		nodeMap[n.name]={
			name:n.name,
			value:n.pagerank
		};
	}
	

	// Compute the distinct nodes from the links.
	links.forEach(function(link) {
		link.source = nodeMap[link.source];// || (nodes[link.source] = {
			// name : link.source
		// });
		link.target = nodeMap[link.target];// || (nodes[link.target] = {
			// name : link.target
		// });
		link.value = +link.value;
	});

	var width = 960, height = 500, color = d3.scale.category20c();

	var force = d3.layout.force().nodes(d3.values(nodeMap)).links(links).size(
			[ width, height ]).linkDistance(60).charge(-300).on("tick", tick)
			.start();

	// Set the range
	var v = d3.scale.linear().range([ 0, 100 ]);

	// Scale the range of the data
	v.domain([ 0, d3.max(links, function(d) {
		return d.value;
	}) ]);

	// asign a type per value to encode opacity
	links.forEach(function(link) {
		if (v(link.value) <= 25) {
			link.type = "twofive";
		} else if (v(link.value) <= 50 && v(link.value) > 25) {
			link.type = "fivezero";
		} else if (v(link.value) <= 75 && v(link.value) > 50) {
			link.type = "sevenfive";
		} else if (v(link.value) <= 100 && v(link.value) > 75) {
			link.type = "onezerozero";
		}
	});

	var svg = d3.select(selector).append("svg").attr("width", width).attr(
			"height", height);

	// build the arrow.
	svg.append("svg:defs").selectAll("marker").data([ "end" ]) // Different
																// link/path
																// types can be
																// defined here
	.enter().append("svg:marker") // This section adds in the arrows
	.attr("id", String).attr("viewBox", "0 -5 10 10").attr("refX", 17).attr(
			"refY", -1.0).attr("markerWidth", 6).attr("markerHeight", 6).attr(
			"orient", "auto").attr("class", "auto").attr("style",
			"fill:none;stroke:#000;").append("svg:path").attr("d",
			"M0,-3L10,0L0,3");

	// add the links and the arrows
	var path = svg.append("svg:g").selectAll("path").data(force.links())
			.enter().append("svg:path").attr("class", function(d) {
				return "link " + d.type + " open-path";
			}).attr("marker-end", "url(#end)");

	// define the nodes
	console.log("The nodes are ");
	console.log(force.nodes());
	var node = svg.selectAll(".node").data(force.nodes()).enter().append("g")
			.attr("class", "node").call(force.drag);

	// the range of pagerank
	var pagerank = [];
	nodes.forEach(function(n) {
		pagerank.push(n["pagerank"]);
	});
	console.log("The pagerank falls in the range of ");
	console.log(pagerank);
	var rscale = d3.scale.linear().domain(d3.extent(pagerank)).range([ 5, 10 ]);
	console.log("The radius is mapped to range");
	var mx = d3.max(pagerank);
	var mn = d3.min(pagerank);
	console.log("max: " + rscale(mx) + " min:" + rscale(mn));

	// the range of colors based on group
	// var groupMap={};
	// points.forEach(function(n){
	// groupMap.push(n["group"]);
	// });

	// add the nodes
	node.append("circle").attr("r", function(d) {
		var pr = rankMap[d.name];
		if (!pr)
			pr = 1;
		console.log("The pagerank is" + pr);
		return rscale(pr);
	})
	// .style("fill", function(d) { return color(groupName[d.name]); });
	.style("fill", function(d) {
		return color(d.name);
	});

	// add the text
	node.append("text").attr("class","labelName").attr("x", 12).attr("dy", ".35em").style("visibility",
	"hidden").text(function(d) {
		return d.name;
	});
	
	// set the text as only visible on hover
	var tooltip = d3.select("body").append("div").style("position",
	"absolute").style("z-index", "10").style("visibility",
	"hidden").text("a simple tooltip");
	node.on("mouseover", function(d) {
		return tooltip.style("visibility", "visible");
		}).on(
			"mousemove",
			function(n) {
				tooltip.text(n.name);
				return tooltip.style("top",
						(event.pageY - 10) + "px").style("left",
						(event.pageX + 10) + "px");
			}).on("mouseout", function() {
		// hide text
		return tooltip.style("visibility", "hidden");
	});

	// add the curvy lines
	function tick() {
		path.attr("d",
				function(d) {
					var dx = d.target.x - d.source.x, dy = d.target.y
							- d.source.y, dr = Math.sqrt(dx * dx + dy * dy);
					return "M" + d.source.x + "," + d.source.y + "A" + dr + ","
							+ dr + " 0 0,1 " + d.target.x + "," + d.target.y;
				});

		node.attr("transform", function(d) {
			return "translate(" + d.x + "," + d.y + ")";
		});
	}

}
function forgeNetworkData(){
	var nodes=`[{"name":"Harry",
		"pagerank":1.5},
		{"name":"Sally",
			"pagerank":1.2},
			{"name":"Mario",
				"pagerank":10.5},
				{"name":"Alice",
					"pagerank":5},
					{"name":"Eveie",
						"pagerank":0.6}
				]`;
	var links=`[
	  {
		    "source": "Harry",
		    "target": "Sally",
		    "value": 1.2
		  },
		  {
		    "source": "Harry",
		    "target": "Mario",
		    "value": 1.3
		  },
		  {
		    "source": "Sarah",
		    "target": "Alice",
		    "value": 0.2
		  },
		  {
		    "source": "Eveie",
		    "target": "Alice",
		    "value": 0.5
		  },
		  {
		    "source": "Peter",
		    "target": "Alice",
		    "value": 1.6
		  },
		  {
		    "source": "Mario",
		    "target": "Alice",
		    "value": 0.4
		  },
		  {
		    "source": "James",
		    "target": "Alice",
		    "value": 0.6
		  },
		  {
		    "source": "Harry",
		    "target": "Carol",
		    "value": 0.7
		  },
		  {
		    "source": "Harry",
		    "target": "Nicky",
		    "value": 0.8
		  },
		  {
		    "source": "Bobby",
		    "target": "Frank",
		    "value": 0.8
		  },
		  {
		    "source": "Alice",
		    "target": "Mario",
		    "value": 0.7
		  },
		  {
		    "source": "Harry",
		    "target": "Lynne",
		    "value": 0.5
		  },
		  {
		    "source": "Sarah",
		    "target": "James",
		    "value": 1.9
		  },
		  {
		    "source": "Roger",
		    "target": "James",
		    "value": 1.1
		  },
		  {
		    "source": "Maddy",
		    "target": "James",
		    "value": 0.3
		  },
		  {
		    "source": "Sonny",
		    "target": "Roger",
		    "value": 0.5
		  },
		  {
		    "source": "James",
		    "target": "Roger",
		    "value": 1.5
		  },
		  {
		    "source": "Alice",
		    "target": "Peter",
		    "value": 1.1
		  },
		  {
		    "source": "Johan",
		    "target": "Peter",
		    "value": 1.6
		  },
		  {
		    "source": "Alice",
		    "target": "Eveie",
		    "value": 0.5
		  },
		  {
		    "source": "Harry",
		    "target": "Eveie",
		    "value": 0.1
		  },
		  {
		    "source": "Eveie",
		    "target": "Harry",
		    "value": 2
		  },
		  {
		    "source": "Henry",
		    "target": "Mikey",
		    "value": 0.4
		  },
		  {
		    "source": "Elric",
		    "target": "Mikey",
		    "value": 0.6
		  },
		  {
		    "source": "James",
		    "target": "Sarah",
		    "value": 1.5
		  },
		  {
		    "source": "Alice",
		    "target": "Sarah",
		    "value": 0.6
		  },
		  {
		    "source": "James",
		    "target": "Maddy",
		    "value": 0.5
		  },
		  {
		    "source": "Peter",
		    "target": "Johan",
		    "value": 0.7
		  }
		]`;
	var data={"links":JSON.parse(links),"nodes":JSON.parse(nodes)};
	return data;
}