window.ColorSequenceGenerator = (function () {

	var defaultOptions = {
		lightnessStart:80,
		saturationStart:40,
		randomHueOffset:true
	}
    function ColorSequenceGenerator(numberOfColors, options) {
    	return new ColorSequence(computeColors(numberOfColors, options));
    }
     
    var colorGenerator = {
        createColorSequence: function (numberOfColors, p_options) {
        	var options = p_options || {}
         	return new ColorSequenceGenerator(numberOfColors, options);
        },


    };  

    function ColorSequence(colors){
    	var colors = colors;
    	this.getColors = function(){
    		return colors;
    	}
    }
     
    
	function computeColors(numColors, p_options){
		var options = extend({}, defaultOptions, p_options);
		var colors = new Array();
		var offset =  options.randomHueOffset?Math.random():0;
		for(var i=0;i<numColors;i++){
			var hue = (offset + i/numColors)%1;
			var lightness = (options.lightnessStart + Math.random()*20)/100;
			var saturation = (options.saturationStart + Math.random()*20)/100;
			colors.push(HSVtoRGB(hue, saturation, lightness));
		}
		return colors;
	}
	
	function extend(){
	    for(var i=1; i<arguments.length; i++)
	        for(var key in arguments[i])
	            if(arguments[i].hasOwnProperty(key))
	                arguments[0][key] = arguments[i][key];
	    return arguments[0];
	}

	function HSVtoRGB(h, s, v) {
	    var r, g, b, i, f, p, q, t;
	    if (h && s === undefined && v === undefined) {
	        s = h.s, v = h.v, h = h.h;
	    }
	    i = Math.floor(h * 6);
	    f = h * 6 - i;
	    p = v * (1 - s);
	    q = v * (1 - f * s);
	    t = v * (1 - (1 - f) * s);
	    switch (i % 6) {
	        case 0: r = v, g = t, b = p; break;
	        case 1: r = q, g = v, b = p; break;
	        case 2: r = p, g = v, b = t; break;
	        case 3: r = p, g = q, b = v; break;
	        case 4: r = t, g = p, b = v; break;
	        case 5: r = v, g = p, b = q; break;
	    }
	    return rgbToHex (Math.floor(r * 255), Math.floor(g * 255), Math.floor(b * 255))
	}


	function componentToHex(c) {
	    var hex = c.toString(16);
	    return hex.length == 1 ? "0" + hex : hex;
	}

	function rgbToHex(r, g, b) {
	    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
	}
    return colorGenerator;

}());