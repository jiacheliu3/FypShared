function showFullText(){
	//show the full text on the circle in th central
	$("circle").click(function(){
		console.log("I am clicked!");
		console.log(this);
		$(this).find('.text').html(function(){
			console.log(this);
			console.log("The full text should be "+this.fullText);
			return this.fullText;
		});
	})
}
function trun(string){
	if(string.length>7)
		return string.substring(0,7)+"..";
	return string;
}
function generateTable(map){
	var table={};
	for(var k in map){
		if(map.hasOwnProperty(k)){
			var v=map[k];
			var trunString=trun(k);
			table[trunString]={'key':k,'value':v};
		}
	}
	console.log(table);
	return table;
}
function bubbleClick(table,selector,name){
	$(selector+" circle,text").click(function(){
		console.log(this);
		var textElement=$(this).parent().find("text.text")[0];
		console.log("The text inside is ");
		console.log(textElement);
		var t=textElement.innerHTML;
		var thing=table[t];
		console.log(thing);
		var bt=document.getElementById(name+"BubbleText");
		var bc=document.getElementById(name+"BubbleCount");
		bt.innerHTML=thing.key;
		bc.innerHTML=thing.value;
	})
}
function drawBubbleChart(selector,map){
	console.log("The data passed to bubble chart is ");
	console.log(map);
	var mapData=bubbleFormat(map);
//	var mapData=[
//			        {text: "Java", count: "236"},
//			        {text: ".Net", count: "382"},
//			        {text: "Php", count: "170"},
//			        {text: "Ruby", count: "123"},
//			        {text: "D", count: "12"},
//			        {text: "Python", count: "170"},
//			        {text: "C/C++", count: "382"},
//			        {text: "Pascal", count: "10"},
//			        {text: "Something", count: "170"},
//			      ];
	console.log("The bubble chart data is to be put in the zone: "+selector);
	console.log(mapData);
	if(mapData.length==0){
		console.log("Empty data for bubble chart.");
		return;
	}
	generateBubbleChart(selector,mapData);
}
// format the data from a map/object to the required form of bubble chart
function bubbleFormat(map){
	var list=[];
	for(var m in map){
		if(map.hasOwnProperty(m)){
			var v=map[m];
			//check if the value is integer
			if(v != parseInt(v)){
				console.log("Pair "+m+": "+v+" doesn't have an integer value");
			}
			
			//truncate the text if too long
			m=trun(m);
			list.push({'text':m,'count':v});
		}
	}
	return list;
}
// sample data format: [{text: "Java", count: "236"}]
function generateBubbleChart(selector,data){
	
	var bubbleChart = new d3.svg.BubbleChart({
	    supportResponsive: true,
	    //container: => use @default
	    size: 600,
	    //viewBoxSize: => use @default
	    innerRadius: 600 / 3.5,
	    //outerRadius: => use @default
	    radiusMin: 50,
	    //radiusMax: use @default
	    //intersectDelta: use @default
	    //intersectInc: use @default
	    //circleColor: use @default
	    container:selector,
	    data: {
	      items: data,
	      eval: function (item) {return item.count;},
	      classed: function (item) {return item.text.split(" ").join("");}
	    },
	    plugins: [
//	        {
//	        name: "central-click",
//	        options: {
//	          text: "(See more detail)",
//	          style: {
//	            "font-size": "12px",
//	            "font-style": "italic",
//	            "font-family": "Source Sans Pro, sans-serif",
//	            //"font-weight": "700",
//	            "text-anchor": "middle",
//	            "fill": "white"
//	          },
//	          attr: {dy: "65px"},
//	          centralClick: function(d) {
//	        	  console.log("I'm clicked!"+d);
//	            alert("Here is more details!!");
//	          }
//	        }
//	      },
	      {
	        name: "lines",
	        options: {
	          format: [
	            {// Line #0
	              textField: "count",
	              classed: {count: true},
	              style: {
	                "font-size": "28px",
	                "font-family": "Source Sans Pro, sans-serif",
	                "text-anchor": "middle",
	                fill: "white"
	              },
	              attr: {
	                dy: "0px",
	                x: function (d) {return d.cx;},
	                y: function (d) {return d.cy;}
	              }
	            },
	            {// Line #1
	              textField: "text",
	              classed: {text: true},
	              style: {
	                "font-size": "11px",
	                "font-family": "Source Sans Pro, sans-serif",
	                "text-anchor": "middle",
	                fill: "white",
	                'max-width':'120px',
	                'overflow':"hidden"
	              },
	              attr: {
	                dy: "20px",
	                x: function (d) {return d.cx;},
	                y: function (d) {return d.cy;},
	                fullText:function(d){
	                	//console.log(d);
	                	return d.item.text;
	                }
	              }
	            }
	          ],
	          centralFormat: [
	            {// Line #0
	              style: {"font-size": "50px"},
	              attr: {}
	            },
	            {// Line #1
	              style: {"font-size": "30px"},
	              attr: {dy: "40px"}
	            }
	          ]
	        }
	      }]
	  });

    
}