function timelineFormat(data){
		console.log("Before formatting:");
		console.log(data);
		var objList=[];
		var timemap={
				'0':"00-02",
				'1':"02-04",
				'2':'04-06',
				'3':'06-08',
				'4':'08-10',
				'5':'10-12',
				'6':'12-14',
				'7':'14-16',
				'8':'16-18',
				'9':'18-20',
				'10':'20-22',
				'11':'22-24'
			};
		for(var k in data){
			if(data.hasOwnProperty(k)){
				console.log("The key is "+k);
				var obj={};
				var name=k;
				obj.name=name;
				
				//convert the map to a list of list
				var map=data[k];
				var articles=[];
				var total=0;
				var count=0;
				for(var m in map){
					if(map.hasOwnProperty(m)){
						var l=[];
						l.push(parseInt(m));
						l.push(map[m]);
						total+=map[m];
						articles.push(l);
						//count++;
					}
				}
				console.log(articles);
				obj.articles=articles;
				obj.total=total;
				
				console.log("Converted to object:");
				console.log(obj);
				objList.push(obj);
			}
		}
		return objList;
	}
function drawSpottedTimeline(data,selector) {

	console.log("Got the data for spotted timeline.");
	console.log(data);
	
	function truncate(str, maxLength, suffix) {
		if (str.length > maxLength) {
			str = str.substring(0, maxLength + 1);
			str = str.substring(0, Math.min(str.length, str.lastIndexOf(" ")));
			str = str + suffix;
		}
		return str;
	}

	var margin = {
		top : 20,
		right : 200,
		bottom : 0,
		left : 20
	}, width = 700, height = 650;

	var timemap={
			'0':"00-02",
			'1':"02-04",
			'2':'04-06',
			'3':'06-08',
			'4':'08-10',
			'5':'10-12',
			'6':'12-14',
			'7':'14-16',
			'8':'16-18',
			'9':'18-20',
			'10':'20-22',
			'11':'22-24'
		};
	//new x axis is the time in hours, one row is one month/other time unit
	var start=0,end=11;//use index for column number
	var c = d3.scale.category20c();
	var x = d3.scale.linear().range([ 0, width ]);
	var xAxis = d3.svg.axis().scale(x).orient("top");
	x.domain([ start, end ]);
	var xScale = d3.scale.linear().domain([ start, end ]).range(
			[ 0, width ]);
	var fakeScale=function(year){
		var yearMap={
			'2004':0,
			'2005':1,
			'2006':2,
			'2007':3,
			'2008':4,
			'2009':5,
			'2010':6,
			'2011':7,
			'2012':8,
			'2013':9,
			'2014':10,
			'2016':11
		};
		return yearMap[year];
	}
	//calculate the interval between nodes
	var columnCount=12;
	var graphWidth=width;
	var interval=graphWidth/(columnCount-1);
	
//	var start_year = 2004, end_year = 2013;
//	var c = d3.scale.category20c();
//	var x = d3.scale.linear().range([ 0, width ]);
//	var xAxis = d3.svg.axis().scale(x).orient("top");
//	var formatYears = d3.format("0000");
//	xAxis.tickFormat(formatYears);
//	x.domain([ start_year, end_year ]);
//	var xScale = d3.scale.linear().domain([ start_year, end_year ]).range(
//			[ 0, width ]);
	
	var svg = d3.select(selector).append("svg").attr("width",
			width + margin.left + margin.right).attr("height",
			height + margin.top + margin.bottom).style("margin-left",
			margin.left + "px").append("g").attr("transform",
			"translate(" + margin.left + "," + margin.top + ")");

	// var dataset = [[ [2002, 8], [2003, 1], [2004, 1], [2005, 1], [2006, 3],
	// [2007, 3], [2009, 3], [2013, 3]], [ [2004, 5], [2005, 1], [2006, 2],
	// [2010,
	// 20], [2011, 3] ] ,[ [2001, 5], [2005, 15], [2006, 2], [2010, 20], [2012,
	// 25]
	// ]];
	// var dataset = [ [2001, 5], [2005, 15], [2006, 2], [2010, 20], [2012, 25]
	// ];

	svg.append("g").attr("class", "x axis").attr("transform",
			"translate(0," + 0 + ")").call(xAxis);

	for (var j = 0; j < data.length; j++) {
		var theRow = data[j];
		var columns=data[j]['articles'];
		var rowName=data[j]['name'];
		
		console.log("The row is: ");
		console.log(theRow);

		var g = svg.append("g").attr("class", "journal");

		var circles = g.selectAll("circle").data(columns).enter().append(
				"circle");

		var text = g.selectAll("text").data(columns).enter().append("text");

		var rScale = d3.scale.linear().domain(
				[ 0, d3.max(columns, function(d) {
					return d[1];
				}) ]).range([ 2, 9 ]);

		circles.attr("cx", function(d, i) {
			//console.log("cx depends on 0th of "+d);
			//return xScale(d[0]);
			var target=d[0];
			console.log("The target column is "+target);
			return target*interval;
		}).attr("cy", j * 30 + 20).attr("r", function(d) {
			//console.log("cx depends on 1th of "+d);
			return rScale(d[1]);
		}).style("fill", function(d) {
			return c(j);
		});

		text.attr("y", j * 30 + 25).attr("x", function(d, i) {
			return d[0]*interval - 5;
		}).attr("class", "value").text(function(d) {
			return d[1];
		}).style("fill", function(d) {
			return c(j);
		}).style("display", "none");

		g.append("text").attr("y", j * 30 + 25).attr("x", width + 20).attr(
				"class", "label").text(truncate(rowName, 30, "..."))
				.style("fill", function(d) {
					return c(j);
				}).on("mouseover", mouseover).on("mouseout", mouseout);
	}
	;

	function mouseover(p) {
		var g = d3.select(this).node().parentNode;
		d3.select(g).selectAll("circle").style("display", "none");
		d3.select(g).selectAll("text.value").style("display", "block");
	}

	function mouseout(p) {
		var g = d3.select(this).node().parentNode;
		d3.select(g).selectAll("circle").style("display", "block");
		d3.select(g).selectAll("text.value").style("display", "none");
	}
	//match the text on x axis to the labels
	var labels=d3.selectAll(".tick").select("text").forEach(function(d){
		console.log("The element is ");
		console.log(d);
		for(var i=0;i<d.length;i++){
			var t=d[i];
			console.log("The text is ");
			var txt=t.innerHTML;
			var label=timemap[txt];
			console.log("The label is: "+label);
			t.innerHTML=label;
		}
	});
}