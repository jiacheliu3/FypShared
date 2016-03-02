function newPie(topics) {
	
	var width = 800;
	var height = 610;
	var radius = Math.min(width, height) / 1.2;
	var spacing = .08;

	var color = d3.scale.linear().range(
			[ "hsl(-180,50%,50%)", "hsl(180,50%,50%)" ]).interpolate(
			interpolateHsl);

	var arc = d3.svg.arc().startAngle(0.0).endAngle(function(d) {
		return (d.value * 2 * Math.PI);
	}).innerRadius(function(d) {
		return d.index * radius;
	}).outerRadius(function(d) {
		return (d.index + spacing) * radius;
	});
	var formatter = d3.format(".2%");
	var svg = d3.select("#stripePieChart").append("svg").attr("width", width).attr(
			"height", height).append("g").attr("transform",
			"translate(" + width / 2 + "," + height / 2 + ")");

	var gradient = svg.selectAll("linearGradient").data(fields).enter().append(
			"linearGradient").attr("y1", "0%").attr("y2", "0%")
			.attr("x1", "0%").attr("x2", "100%").attr("gradientUnits",
					"objectBoundingBox").attr('id', function(d) {
				return "gradient-" + d.index * 10
			})
	gradient.append("stop").attr("offset", "0%").attr("stop-opacity", "1")
			.attr("stop-color", function(d) {
				return color(d.index);
			});
	gradient.append("stop").attr("offset", function(d) {
		return 30 + "%"
	}).attr("stop-opacity", ".5").attr("stop-color", function(d) {
		return color(d.index);
	});
	gradient.append("stop").attr("offset", function(d) {
		return 80 + "%"
	}).attr("stop-opacity", "0.7").attr("stop-color", function(d) {
		return color(d.index);
	});

	var field = svg.selectAll("g").data(fields).enter().append("g");

	field.append("path");
	field.append("rect");
	field.append("text");

	d3.transition().duration(0).each(tick);
	d3.select(self.frameElement).style("height", height + "px");

	function tick() {
		field = field.each(function(d) {
			this._value = d.value;
		}).data(fields).each(function(d) {
			d.previousValue = this._value;
		});
		console.log("The field arg in tick()");
		console.log(field);

		var colors=[];
		field.select("path").transition().ease("linear").attrTween("d",
				arcTween).style("opacity", function(d) {
			return .7;
		}).style("fill", function(d) {
			colors.push(color(d.index));
			return color(d.index);
		});

		field.select("text").attr("x", function(d) {
			return -(((fields().length / 10) + spacing) * radius) - 50;
		}).attr("y", function(d) {
			return -(((d.index + spacing) * radius)) + 20;
		}).text(function(d) {
			//return d.value;
			return formatter(d.value);
		}).style("font-size", "15px").transition().ease("linear").attr(
				"transform", function(d) {
					return "rotate(0)" + "translate(0,0)" + "rotate(0)"
				});

		field.select("rect").attr("x", function(d) {
			return -(((fields().length / 10) + spacing) * radius) - 100;
		}).attr("y", function(d) {
			return -(((d.index + spacing) * radius));
		}).attr("height", function(d) {
			return ((d.index + spacing) * radius) - (d.index * radius)
		}).attr("width", function(d) {
			return ((fields().length / 10) + spacing) * radius + 90;
		}).text(function(d) {
			return formatter(d.value);
		}).attr("fill", function(d) {
			return "url(#gradient-" + d.index * 10 + ")"
		}).transition().attr("transform", function(d) {
			return "rotate(0)" + "translate(0,0)" + "rotate(0)"
		});

		// setTimeout(tick, 3000);
		
		//format data for legend
		var dataReg = [];
		for ( var d in topics) {
			dataReg.push(topics[d].name);
		}
		console.log("data reg for legends are ");
		console.log(dataReg);
		//get the colors for legends
		console.log("The colors are ");
		console.log(colors);
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
							var horz = 200;
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

	function arcTween(d) {
		var i = d3.interpolateNumber(d.previousValue, d.value);
		return function(t) {
			d.value = i(t);
			return arc(d);
		};
	}

	function fields() {
		//console.log("The converted data is ");
		//console.log(topics);
		return topics;
		// [ {
		// index : .1,
		// value : Math.random()
		// }, {
		// index : .2,
		// value : Math.random()
		// }, {
		// index : .3,
		// value : Math.random()
		// }, {
		// index : .4,
		// value : Math.random()
		// }, {
		// index : .5,
		// value : Math.random()
		// }, {
		// index : .6,
		// value : Math.random()
		// } ];
	}

	// Avoid shortest-path interpolation.
	function interpolateHsl(a, b) {
		var i = d3.interpolateString(a, b);
		return function(t) {
			return d3.hsl(i(t));
		};
	}
}