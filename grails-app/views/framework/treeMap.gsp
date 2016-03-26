<!doctype html>
<meta charset="utf-8">

<!-- load D3plus with all dependencies -->

<g:javascript src="toolbox/d3plus.full.js" />
<g:javascript src="udf/treeMap.js" />
<!-- create container element for visualization -->
<div style="width:100%;height:500px;">
<h3>Gogogo</h3>
<div id="viz"></div>
</div>


<script>

  // sample data array
  //var sample_data = [
    //{"value": 100, "name": "alpha"},
    //{"value": 70, "name": "beta"},
    //{"value": 40, "name": "gamma"},
    //{"value": 15, "name": "delta"},
    //{"value": 5, "name": "epsilon"},
    //{"value": 1, "name": "zeta"}
  //];
  //var data={"20-30":5,"30-40":25,"40-50":10,"50-60":0};
  var data={"unknown":15,"known":[1,2,3,4,5,50,20]};
  //var d=treeMapFormat(data);
  drawTreeMap(data,"#viz");

  

</script>