function truncate(str, maxLength, suffix) {
	if (str.length > maxLength) {
		str = str.substring(0, maxLength + 1);
		str = str.substring(0, Math.min(str.length, str.lastIndexOf(" ")));
		str = str + suffix;
	}
	return str;
}


function drawSpottedTimeline(data) {
	console.log("Data for the spotted timeline table:");
	console.log(data);
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

	var margin = {
		top : 20,
		right : 200,
		bottom : 0,
		left : 20
	}, width = 800, height = 350;
	var minRadius=5;
	var maxRadius=15;
	var circleInterval=40;

	//var start_year = 2004, end_year = 2013;
//	var start_year=201501,end_year=201512;
//	var c = d3.scale.category20c();
//	var x = d3.scale.linear().range([ 0, width ]);
//	var xAxis = d3.svg.axis().scale(x).orient("top");
//	
//	var formatYears = d3.format("0000");
//	xAxis.tickFormat(formatYears);
//	x.domain([ start_year, end_year ]);
//	var xScale = d3.scale.linear().domain([ start_year, end_year ]).range(
//			[ 0, width ]);
	
	//new x axis is the time in hours, one row is one month/other time unit
	var start=0,end=11;//use index for column number
	var c = d3.scale.category20c();
	var x = d3.scale.linear().range([ 0, width ]);
	var xAxis = d3.svg.axis().scale(x).orient("top");
	
	//var formatHours = d3.format("0000");
	//xAxis.tickFormat(formatHours);
	x.domain([ start, end ]);
	var xScale = d3.scale.linear().domain([ start, end ]).range(
			[ 0, width ]);
	
	var svg = d3.select("#spottedTimeline").append("svg").attr("width",
			width + margin.left + margin.right).attr("height",
			height + margin.top + margin.bottom).style("margin-left",
			margin.left + "px").append("g").attr("transform",
			"translate(" + margin.left + "," + margin.top + ")");


	svg.append("g").attr("class", "x axis").attr("transform",
			"translate(0," + 0 + ")").call(xAxis);

	for (var j = 0; j < data.length; j++) {
		var theRow=data[j];
		console.log("The row is: ");
		console.log(data);
		
		var g = svg.append("g").attr("class", "journal");

		var circles = g.selectAll("circle").data(theRow['articles']).enter()
				.append("circle");

		var text = g.selectAll("text").data(theRow['articles']).enter()
				.append("text");

		var rScale = d3.scale.linear().domain(
				[ 0, d3.max(theRow['articles'], function(d) {
					return d[1];
				}) ]).range([ minRadius, maxRadius ]);

		circles.attr("cx", function(d, i) {
			return xScale(d[0]);
		}).attr("cy", j * circleInterval + circleInterval).attr("r", function(d) {
			return rScale(d[1]);
		}).style("fill", function(d) {
			return c(j);
		});

		text.attr("y", j * circleInterval + circleInterval+5).attr("x", function(d, i) {
			return xScale(d[0]) - 5;
		}).attr("class", "value axis-label").text(function(d) {
			return d[1];
		}).style("fill", function(d) {
			return c(j);
		}).style("display", "none");

		g.append("text").attr("y", j * circleInterval + circleInterval+5).attr("x", width + circleInterval).attr(
				"class", "label").text(truncate(theRow['name'], 30, "..."))
				.style("fill", function(d) {
					return c(j);
				}).on("mouseover", mouseover).on("mouseout", mouseout);
	}
	

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
