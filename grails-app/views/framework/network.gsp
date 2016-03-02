<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<!-- Apply layout -->
<meta name="layout" content="lte_layout">
<title>Network Study</title>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->

<style>
button {
	margin: 5px 3px;
}

#graphCanvas {
	text-align: center;
	overflow: auto;
	height: 100%;
	width: 95%;
	margin: auto;
}

.label {
	opacity: 0.7;
}

.tag-button {
	color:#000;
	outline: #222d32 solid 2px;
	background-color:#fff;
}
.tag-button:hover{
	outline: #3c8dbc solid 2px;
}
</style>

</head>

<body>

	<!-- Page Content -->
	<div id="page-wrapper" class="content-wrapper">
		<section class="content-header">
			<h1 class="page-header">User Network Study</h1>
		</section>
		<div class="container-fluid">

			<!-- Progress bar zone -->
			<!--
			<div id='networkProgressDiv' class='fakeContent'
				style='width: 900px;'>
				<div class="progress" id="networkProgressZone">
					<div class="progress-bar progress-bar-striped active"
						id="networkProgressBar" style="width: 0%;">
						<span class="sr-only">60% Complete</span>
					</div>
				</div>
				<div class="panel-body" id="networkSuccessMessage"
					style="display: none;">
					<div class="alert alert-success alert-dismissable">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">&times;</button>
						Work Complete
					</div>
				</div>
			</div>
		-->
			<!--  Main Content -->
			<div class="row">
				<div class="col-lg-12">
					<div class="box box-primary">
						<div class="box-header with-border">
							<h4>Network Graph</h4>
						</div>
						<div class="box-body" id="contentPanel">
							<div class="box box-default">

								<table class="table table-striped">
									<tbody id="networkinfo">
										<tr>
											<td><b>User tags</b></td>
											<td><span id="buttons"></span></td>
										</tr>
									</tbody>
								</table>

							</div>
							<div id='networkContentDiv' class='realContent'>
								<div id='networkGraph'>
									<div id="graphCanvas"></div>
								</div>

							</div>
							<div id="weiboTags">
								<div id="tagNetwork"></div>
								<a href="/CodeBigBroSub/tag/listAll">All available tags</a>
							</div>


						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col-12 -->
			</div>
			<!-- /.row -->

		</div>
		<!-- /.container-fluid -->
	</div>
	<!-- /#page-wrapper -->



	<content tag="javascript"> 
	<g:javascript src="udf/asynchro.js" />
	<g:javascript src="udf/progressBar.js" /> 
	<g:javascript
		src="udf/drawNetwork.js" /> 
		<script>
			function asynchroWait4Network() {
				console.log("Start");
				//startBootstrapProgressBar("network", 100);
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroNetwork",
					dataType : "json",
					async : true
				}).done(function(jsonData) {
					console.log("The object got");
					console.log(jsonData);
					// console.log(jsonText);
					//document.getElementById("result").innerHTML = "Done";
					console.log(jsonData.all);

					// set global variable
					similar = jsonData;

					drawEntrance(similar);
					// finially able to stop progress bar
					//stopProgressBar("network");
					//stopBootstrapProgressBar("network");
				});
			}
			function drawEntrance(simArr) {
				// var simArr=similar;
				//var simArr = getData();
				console.log("inside draw()");
				console.log(simArr);

				// draw buttons
				if (buttonDrawn == false) {
					for ( var field in simArr) {
						if (simArr.hasOwnProperty(field)) {
							var panel = document.getElementById("buttons");
							// var className="class=\"tag\"";
							//var button = document.createElement("button");
							//button.className = "btn btn-outline btn-primary tag-button";

							var button=document.createElement("a");
							button.innerHTML="<b>"+field+"</b>";
							//button.className = "tag";
							button.value = field;
							button.onclick = function() {
								drawClique(this.value);
							};
							panel.appendChild(button);
						}
					}
					buttonDrawn = true;
				}
				drawNetwork(simArr.all);
			}
			function generateMap(nodes, links) {
				var m = {};
				console.log("Record nodes");
				for ( var n in nodes) {
					var theNode = nodes[n];
					//console.log(theNode);
					m[theNode.name] = [];
				}
				console.log("Raw table:");
				console.log(m);
				console.log("Record links");
				for ( var l in links) {
					var theLink = links[l];
					//console.log(theLink);
					if (m.hasOwnProperty(theLink.source))
						m[theLink.source].push(l);
					else
						console.log("Field " + theLink.source
								+ " not found in m");
					if (m.hasOwnProperty(theLink.target)) {
						//console.log("adding this to the links");
						//console.log(theLink);
						m[theLink.target].push(theLink);
						console.log("Now the entry has");
						var mmm = m[theLink.target];
						console.log(mmm);
					} else
						console.log("Field " + theLink.target
								+ " not found in m");
				}
				return m;
			}
			function findLinksOf(theNode) {
				var theName = theNode.name;
				console.log("Target at the node called " + theName);
				var theLinks = nodelinkmap[theName];
				console.log(theLinks);
				return theLinks;
			}
			function drawNetwork(obj) {
				console.log("inside drawNetwork()");
				// don't draw if the canvas is not clean
				var canvas = document.getElementById("graphCanvas");
				if (canvas.innerHTML != "") {
					canvas.innerHTML = "";
				}
				var n = obj.nodes;
				var l = obj.links;
				console.log("Nodes");
				console.log(n);
				console.log("Links:");
				console.log(l);
				//show network info
				showNetworkInfo(n, l);

				console.log("Generating node-link map");
				nodelinkmap = generateMap(n, l);
				console.log(nodelinkmap);

				var nd = $.extend(true, {}, n);
				var ld = $.extend(true, {}, l);
				console.log("Here are the substitutes: ");
				console.log(nd);
				console.log(ld);
				// index the nodes and use them in links
				console.log("start indexing");
				var indexMap = {};
				var count = 0;
				for (var i = 0; i < n.length; i++) {
					var node = n[i];
					//console.log(node);
					var name = node["name"];
					//console.log(name);
					indexMap[name] = i;
					// console.log(indexMap[name]);
				}
				console.log("index are: ");
				console.log(indexMap);

				var indexedLinks = [];
				for (var j = 0; j < l.length; j++) {
					var oldLink = l[j];
					var s = oldLink.source;
					var t = oldLink.target;
					var newLink = {};
					newLink["source"] = indexMap[s];
					newLink["target"] = indexMap[t];
					newLink["weight"] = 1;//newly added
					indexedLinks.push(newLink);
				}

				console.log("draw start");
				console.log(indexedLinks);

				var width = 1000, height = 800;
				var svg = d3.select("#graphCanvas").append("svg").attr("width",
						width).attr("height", height);
				svg.append("rect").attr("width", "100%").attr("height", "100%")
						.attr("fill", '#ccccff');
				var force = d3.layout.force().gravity(.5).distance(100).charge(
						-300).size([ width, height ]);
				force.nodes(n).links(indexedLinks).start();
				var link = svg.selectAll(".link").data(indexedLinks).enter()
						.append("line").attr("class", "link").attr("stroke",
								"#fff");
				var node = svg.selectAll(".node").data(n).enter().append("g")
						.on("mouseover", mouseover).on("mouseout", mouseout)
						.attr("class", "node").call(force.drag);
				// node.append("image").attr("xlink:href","https://github.com/favicon.ico").attr("x",
				// -8).attr("y",-8).attr("width", 16).attr("height", 16);
				node.append("circle").attr("class", "circle").attr("r", 12)
						.style(
								"fill",
								function(d) {
									var colors = [ "#ccff99", "#ff99cc",
											"#666699", "#ff9900", "#ffffcc",
											"#009933", "#cc3399", "#ffcc33" ];
									return colors[d.group - 1];
								});

				//node.append("text").attr("dx", 12).attr("dy", ".35em").attr("class","text").text(function(d) {
				//return d.name
				//});
				var tooltip = d3.select("body").append("div").style("position",
						"absolute").style("z-index", "10").style("visibility",
						"hidden").text("a simple tooltip");
				node.on("mouseover", function(d) {
					//set those links that connect to this node
					link.style('stroke-width', function(l) {
						if (d === l.source || d === l.target)
							return 2;
						else
							return 2;
					});
					link.style('stroke', function(l) {
						if (d === l.source || d === l.target)
							return '#003399';
						else
							return '#fff';
					});
					return tooltip.style("visibility", "visible");
				}).on(
						"mousemove",
						function(n) {
							tooltip.text(n.name);
							return tooltip.style("top",
									(event.pageY - 10) + "px").style("left",
									(event.pageX + 10) + "px");
						}).on("mouseout", function() {
					//return link width to normal
					link.style('stroke-width', 2);
					link.style('stroke', '#fff');
					//hide text
					return tooltip.style("visibility", "hidden");
				});

				//on tick function
				function tick() {
					node.attr("cx", function(d) {
						return d.x = Math.max(r, Math.min(w - r, d.x));
					}).attr("cy", function(d) {
						return d.y = Math.max(r, Math.min(h - r, d.y));
					});

					link.attr("x1", function(d) {
						return d.source.x;
					}).attr("y1", function(d) {
						return d.source.y;
					}).attr("x2", function(d) {
						return d.target.x;
					}).attr("y2", function(d) {
						return d.target.y;
					});
				}

				force.on("tick", function() {
					link.attr("x1", function(d) {
						return d.source.x;
					}).attr("y1", function(d) {
						return d.source.y;
					}).attr("x2", function(d) {
						return d.target.x;
					}).attr("y2", function(d) {
						return d.target.y;
					});
					node.attr("transform", function(d) {
						return "translate(" + d.x + "," + d.y + ")";
					});
					//node.attr("cx", function(d) { return d.x; })
					//.attr("cy", function(d) { return d.y; });
				});
				console.log("disable drag");
				node.on('mousedown.drag', null);
			}
			function getData() {
				console.log("Sth wrong in getting data?");
				// load json
				var simArr = similar;
				console.log("inside getData():");
				console.log(simArr);
				return simArr;
			}

			function drawClique(field) {
				console.log(field);
				// console.log(similar);
				var simArr = getData();
				// var simArr=similar;
				console.log("inside drawClique():");
				console.log(simArr[field]);
				drawNetwork(simArr[field]);
			}
			function clearGraph() {
				document.getElementById("graphCanvas").innerHTML = "";
			}

			function mouseover() {
				d3.select(this).select("circle").transition().duration(750)
						.attr("r", 16);
			}

			function mouseout() {
				d3.select(this).select("circle").transition().duration(750)
						.attr("r", 12);
			}
			function showNetworkInfo(nodes, links) {
				var infoZone = document.getElementById("networkinfo");
				//var d = document.createElement('div');
				//d.className = "well well-sm col-lg-12";

				var s1 = document.createElement('tr');
				//s1.className = "col-lg-6";
				var s11 = document.createElement('td');
				s11.innerHTML = "<b>Nodes: </b>";
				var s12 = document.createElement('td');
				s12.innerHTML = +nodes.length;
				s1.appendChild(s11);
				s1.appendChild(s12);

				var s2 = document.createElement('tr');
				//s2.className = "col-lg-6";
				var s21 = document.createElement('td');
				s21.innerHTML = "<b>Links: </b>";
				var s22 = document.createElement('td');
				s22.innerHTML = links.length;
				s2.appendChild(s21);
				s2.appendChild(s22);

				infoZone.appendChild(s1);
				infoZone.appendChild(s2);
				//infoZone.appendChild(d);
			}
		</script> <script>
			var relationFinished = false;
			var keywordFinished = false;
			var buttonDrawn = false;
			var nodelinkmap;
			asynchroWait4Network();
		</script> </content>
</body>

</html>
