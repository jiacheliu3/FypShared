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

<title>Cluster Study</title>
<style>
.legend {
	font-size: 12px;
}

rect {
	stroke-width: 2;
}

#pieCharts {
	text-align: center;
	align: center;
}

td {
	max-height: 150px;
	min-height: 60px;
}

#overall p {
	margin-bottom: 0;
	vertical-align: middle;
}
#stripePieChart{
	width:100%;
	overflow:auto;
}
</style>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
</head>

<body>
	<!-- Page Content -->
	<div id="page-wrapper" class="content-wrapper">
		<section class="content-header">
			<h1 class="page-header">User Topic Study</h1>
		</section>
		<div class="container-fluid">
			<div class="col-lg-12">
				<div class="row">
					<div class="box box-primary">
						<div class="box-header with-border">
							<h4>Clusters</h4>
						</div>
						<div class="box-body">
							<div style="width: 100%;">
								<div style="width: 100%; height: 100%;">
									<h4>User's overall weibo count:</h4>
								</div>
								<div class="col-lg-7" id="overall" style="height: 100%;"></div>

							</div>
							<!-- hover data table -->
							<table id="example2" class="table table-bordered table-hover">
								<col style="max-width: 900px">
								<col width="300">
								<thead>
									<tr>
										<th class="col-md-8">Tag</th>
										<th class="col-md-4">Count</th>
									</tr>
								</thead>
								<tbody id="accordion">
								</tbody>
							</table>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.box -->

				<!-- Start of row -->
				<div class="row">
					<div class="col-lg-12" style="padding: 0;">
						<div class="box box-primary">
							<div class="box-header with-border">
								<h4>Pie chart</h4>
							</div>
							<!-- .panel-heading -->
							<div class="box-body" id="pieCharts">
								<div id="chart_div"></div>
							</div>
							<!-- .panel-body -->
						</div>

					</div>
				</div>
				<!-- /.row -->
				
				<!-- Start of row -->
				<div class="row">
					<div class="col-lg-12" style="padding: 0;">
						<div class="box box-primary">
							<div class="box-header with-border">
								<h4>Yet Another Pie chart</h4>
							</div>
							<!-- .panel-heading -->
							<div class="box-body">
								<div  id="stripePieChart"></div>
							</div>
							<!-- .panel-body -->
						</div>

					</div>
				</div>
				<!-- /.row -->
				
				<div class="row">
					<div class="box box-primary">
						<div class="box-header with-border">
							<h4>Topics</h4>
						</div>
						<div class="box-body">
							<!-- hover data table -->
							<table id="example2" class="table table-bordered table-hover">
								<thead>
									<tr>
										<th class="col-md-3">Index</th>
										<th class="col-md-9">Keywords</th>
									</tr>
								</thead>
								<tbody id="topicTable">
								</tbody>
							</table>
						</div>
						<!-- /.col-lg-12 -->
					</div>
					<!-- End of panel body -->
				</div>
				<!-- /.row -->
			</div>
			<!-- /.col-12 -->
		</div>
		<!-- /.container-fluid -->
	</div>
	<!-- /#page-wrapper -->
	</div>
	<!-- End of wrapper -->

	<content tag="javascript"> 
	<g:javascript src="toolbox/colorchain.js" /> 
	<g:javascript src="udf/piechart.js" />
	<g:javascript src="udf/stripePieChart.js" />
		 <script>
			function drawTopics(data) {
				var topics = data["topics"];
				console.log("Here are the topics: ");
				console.log(topics);
				var table = document.getElementById("topicTable");
				if (!table) {
					console.log("Table not located for topics!");
				}
				for ( var i in topics) {
					var obj = topics[i];
					var row = document.createElement("tr");
					var indexCell = document.createElement("td");
					var index = parseInt(i) + 1;
					indexCell.innerHTML = index;
					var wordsCell = document.createElement("td");
					var words = obj["words"];
					function mapToString(map) {
						var s = "";
						for ( var m in map) {
							if (map.hasOwnProperty(m)) {
								s += m + " ";
							}
						}
						return s;
					}
					var wordsContent = mapToString(words);
					console.log("Words are " + wordsContent);
					wordsCell.innerHTML = wordsContent;
					row.appendChild(indexCell);
					row.appendChild(wordsCell);
					table.appendChild(row);
				}

			}
			function report(data) {
				var overall = document.getElementById("overall");
				var overallMap = data['users'];
				console.log(overallMap);
				for ( var user in overallMap) {
					if (overallMap.hasOwnProperty(user)) {
						console.log("Processing " + user);
						var line = document.createElement("p");
						line.innerHTML = "<b>" + user + ": " + "</b>"
								+ overallMap[user];
						overall.appendChild(line);
					}
				}

				//var content = document.createTextNode();
				//par.appendChild(content);
				//par.className = "leaf";
				//node.appendChild(par);

				var tagZone = document.getElementById("accordion");
				var tags = data['tags'];
				for ( var tag in tags) {
					if (tags.hasOwnProperty(tag)) {
						console.log("This tag is " + tag);
						//create a panel
						//var panel=document.createElement("div");
						//panel.style=height="400px";
						//panel.className="panel panel-default col-lg-3";
						//panel.style.margin="0";
						//var header=document.createElement("div");
						//header.className="panel-heading";
						//set max height in case the tag is too long
						//header.style.maxHeight="300px";
						//header.style.overflow="scroll";
						//var title=document.createElement("h4");
						//title.classname="panel-title";
						//title.innerHTML="Tag: "+tag;
						//header.appendChild(title);
						//panel.appendChild(header);
						//tagZone.appendChild(panel);

						//var body=document.createElement("div");
						//body.className="panel-collapse collapse in";
						//var content=document.createElement("div");
						//content.className="panel-body";
						//body.appendChild(content);
						//tagZone.appendChild(content);
						//panel.appendChild(content);
						//tagZone.appendChild(panel);

						//generate contents under each tag
						//var allocation=tags[tag];
						//for(var eachUser in allocation){
						//if(allocation.hasOwnProperty(eachUser)){
						//var l=document.createElement("p");
						//l.innerHTML="<b>"+eachUser+": "+"</b>"+allocation[eachUser];
						//content.appendChild(l);
						//}
						//}

						//change to table representation
						var row = document.createElement("tr");
						var name = document.createElement("td");
						name.innerHTML = tag;
						//get closest weibo
						var tagWeiboMap = data["delegates"];
						var tagContent = tagWeiboMap[tag];
						console.log("Setting the weibo for tag " + tag + " :");
						console.log(tagContent);
						row.setAttribute("data-toggle", "popover");
						row["title"] = "Closest weibo";
						row.setAttribute("data-content", tagContent);
						//name.setAttribute("trigger","hover");
						row.setAttribute("data-placement", "top");

						var count = document.createElement("td");
						var allocation = tags[tag];
						var content = "";
						for ( var eachUser in allocation) {
							if (allocation.hasOwnProperty(eachUser)) {
								content = content + " " + "<b>" + eachUser
										+ ": " + "</b>" + allocation[eachUser];
							}
						}
						count.innerHTML = content;
						row.appendChild(name);
						row.appendChild(count);
						tagZone.appendChild(row);

					}

				}

			}
			function getColors(number) {
				var options = {};
				var colors = ColorSequenceGenerator.createColorSequence(number,
						options).getColors();
				console.log("Got the colors:");
				console.log(colors);
				return colors;
			}
			function drawPieChart(dataset, extractDataFunc) {

				var width = 360;
				var labelWidth = 200;
				var height = 360;
				var radius = Math.min(width, height) / 2;
				var innerRadius = 75;
				//count number of labels
				var count = dataset.length;
				console.log("Found " + count + " labels");
				var dataReg = [];
				for ( var d in dataset) {
					dataReg.push(dataset[d].label);
				}
				// decide the colors
				var colors = getColors(count);

				var color = d3.scale.category20b();
				var svg = d3.select('#chart_div').append('svg').attr('width',
						width + labelWidth + 300).attr('height', height)
						.append('g').attr(
								'transform',
								'translate(' + (width / 2) + ',' + (height / 2)
										+ ')');
				var arc = d3.svg.arc().outerRadius(radius).innerRadius(
						innerRadius);
				var pie = d3.layout.pie().value(function(d) {
					return extractDataFunc(d)
				}).sort(null);
				var path = svg.selectAll('path').data(pie(dataset)).enter()
						.append('path').attr('d', arc).attr('fill',
								function(d, i) {
									//return color(d.data.label);
									var theColor = colors[i];
									//console.log("The color for this label is "+theColor);
									return theColor;
								});
				// add a legend
				var legendVacancy = 70;
				var legendRectSize = 18;
				var legendSpacing = 4;
				var legend = svg.selectAll('.legend').data(dataReg).enter()
						.append('g').attr('class', 'legend').attr(
								'transform',
								function(d, i) {
									//var height = legendRectSize + legendSpacing;
									//var offset = height * color.domain().length / 2;
									//var horz = -2 * legendRectSize;
									//var vert = i * height - offset;
									var marginUp = 20;
									var h = legendRectSize + legendSpacing;
									var horz = width;
									var offset = h * 18 / 2;
									var vert = marginUp + i * h - offset;
									return 'translate(' + horz + ',' + vert
											+ ')';
								}).attr('width', legendVacancy);
				legend.append('rect').attr('width', legendRectSize).attr(
						'height', legendRectSize).style('fill', function(d, i) {
					return colors[i];
				}).style('stroke', function(d, i) {
					return colors[i];
				});
				legend.append('text').attr('x', legendRectSize + legendSpacing)
						.attr('y', legendRectSize - legendSpacing).text(
								function(d) {
									return d;
								}).attr('min-width', '30px').attr("max-width",
								'50px');

			}
			//return a list, each element of which is a list containing all parts of a pie chart
			function transformData(data) {
				var tags = data["tags"]; // weibo count under each category
				var all = data["users"];//all user weibo count
				var array = [];
				for ( var user in all) {
					if (all.hasOwnProperty(user)) {
						console.log("For user " + user);
						//find the user's allocation under each tag
						for ( var tag in tags) {
							if (tags.hasOwnProperty(tag)) {
								//console.log("Find info of"+user+" under "+tag);
								//console.log("tag is");
								var tagInfo = tags[tag];
								//console.log(tagInfo);
								var cata = tagInfo[user];
								console.log(user + " has allocation under tag "
										+ tag + " : " + cata);
								if (cata) {
									console.log("add sth");
									array.push({
										"label" : tag,
										"count" : cata
									});
									console.log({
										"label" : tag,
										"count" : cata
									});
								}
							}
						}
					}
				}
				return array;
			}
			function extractData(d) {
				return d.count;
			}
			//format the data for stripe shape pie chart
			function stripePieFormat(data) {
				var c = 1;
				var list = [];
				//convert the data names and collect sum value
				var sum=0;
				for ( var i in data) {
					var d = data[i];
					var obj = {};
					obj.index = c / 10;
					c++;
					obj.value = d.count;
					sum+=d.count;
					obj.name = d.label;
					list.push(obj);
				}
				//convert the value to percentage
				for(var i in list){
					var obj=list[i];
					obj.value=obj.value/sum;
				}
				return list;
			}
			function asynchroCluster() {
				console.log("Cluster fired");
				var jsonData = $.ajax({
					url : "/CodeBigBroSub/environment/asynchroClusters",
					dataType : "json",
					async : true
				}).done(function(jsonData) {
					console.log("Cluster completed");
					console.log(jsonData);
					//var zone=document.getElementById("clusterData").innerHTML=jsonData;
					console.log("Need to format data");
					report(jsonData);
					var massage = transformData(jsonData);
					console.log("Result of data massaging");
					console.log(massage);
					console.log("Draw simple pie chart.");
					drawPieChart(massage, extractData);
					console.log("Draw topics");
					drawTopics(jsonData);
					console.log("Draw a better pie chart");
					var data = stripePieFormat(massage);
					newPie(data);
					
					//activate popover
					$('[data-toggle="popover"]').popover({
						trigger : "hover"
					});

				});
			}
		</script> <script>
	asynchroCluster();
	//sample data
	var dataset = [ {
		label : 'Abulia',
		count : 10
	}, {
		label : 'Betelgeuse',
		count : 20
	}, {
		label : 'Cantaloupe',
		count : 30
	}, {
		label : 'Dijkstra',
		count : 40
	} ];
	//console.log("Sample data " + dataset);
	//$('[data-toggle="popover"]').popover({ trigger: "hover" });
</script> 
</content>
</body>


</html>
