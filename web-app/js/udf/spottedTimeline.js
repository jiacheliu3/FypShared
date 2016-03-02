function truncate(str, maxLength, suffix) {
	if (str.length > maxLength) {
		str = str.substring(0, maxLength + 1);
		str = str.substring(0, Math.min(str.length, str.lastIndexOf(" ")));
		str = str + suffix;
	}
	return str;
}


function drawSpottedTimeline(data) {

	var margin = {
		top : 20,
		right : 200,
		bottom : 0,
		left : 20
	}, width = 800, height = 350;
	var minRadius=5;
	var maxRadius=15;
	var circleInterval=40;

	//var circleSize=
	//var start_year = 2004, end_year = 2013;
	var start_year=201501,end_year=201512;
	var c = d3.scale.category20c();
	var x = d3.scale.linear().range([ 0, width ]);
	var xAxis = d3.svg.axis().scale(x).orient("top");
	
	var formatYears = d3.format("0000");
	xAxis.tickFormat(formatYears);
	
	var svg = d3.select("#spottedTimeline").append("svg").attr("width",
			width + margin.left + margin.right).attr("height",
			height + margin.top + margin.bottom).style("margin-left",
			margin.left + "px").append("g").attr("transform",
			"translate(" + margin.left + "," + margin.top + ")");

	x.domain([ start_year, end_year ]);
	var xScale = d3.scale.linear().domain([ start_year, end_year ]).range(
			[ 0, width ]);
	console.log("The domain of x is ");
	console.log(x);
	console.log("The scaled xScale ");
	console.log(xScale);

	svg.append("g").attr("class", "x axis").attr("transform",
			"translate(0," + 0 + ")").call(xAxis);

	for (var j = 0; j < data.length; j++) {
		var g = svg.append("g").attr("class", "journal");

		var circles = g.selectAll("circle").data(data[j]['articles']).enter()
				.append("circle");

		var text = g.selectAll("text").data(data[j]['articles']).enter()
				.append("text");

		var rScale = d3.scale.linear().domain(
				[ 0, d3.max(data[j]['articles'], function(d) {
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
				"class", "label").text(truncate(data[j]['name'], 30, "..."))
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
}
