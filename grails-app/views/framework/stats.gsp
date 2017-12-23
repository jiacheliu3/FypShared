<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="">
  <meta name="author" content="">
  <meta name="layout" content="lte_layout">

  <title>User Friends Statistics</title>

  <link rel="stylesheet"
  href="${resource(dir: 'css/toolbox', file: 'timeline.css')}"
  type="text/css">

  <link rel="stylesheet"
  href="${resource(dir: 'css/framework', file: 'font-awesome.min.css')}"
  type="text/css">


  <style>
  [data-notify="progressbar"] {
    margin-bottom: 0px;
    position: absolute;
    bottom: 0px;
    left: 0px;
    width: 100%;
    height: 5px;
  }

  .background {
    background-color: #222d32;
  }

  .light {
    opacity: 0.7;
  }

  .zero {
    opacity: 0.5;
  }

  text {
    text-anchor: middle;
  }

  #credit {
    position: absolute;
    right: 4px;
    bottom: 4px;
    color: #ddd;
  }

  #credit a {
    color: #fff;
    font-weight: bold;
  }
  /* zone for gender related stuff */
  #genderZone {
    width: 810px;
    height: 180px;
    overflow: auto;
  }
  #genderGraphHolder{
    padding-left:20px;
  }
  /* zone for persons hist */
  #genderRowHolder{
    height:100%;
    width:95%;
    margin:auto;
    padding-top:5px;
    padding-bottom:5px;
    background:#fff;
    
    border-style:double;
    border-width:3px;
  }
  .infoBox {
    min-height: 200px;
  }
  #genderHist{
    background:#fff;
    margin:5px auto; 
    max-width:1000px; 
    overflow: auto;
    
    
  }
  .genderImage {
    height: 87px;
    width: 400px;
    position: absolute;
  }
  .biped {
    width: 40px;
    height: 87px;
  }

  .numberTag {
    max-width: 350px;
  }

  .inner h3 {
    font-family: 'Source Sans Pro', sans-serif;
  }
  /* Age zone related stuff */
  .example-modal{
    width:90%;
    min-height:60px;
    background: transparent !important;
  }
  .example-modal .modal {
    margin:auto;
    position: relative;
    top: auto;
    bottom: auto;
    right: auto;
    left: auto;
    display: block;
    z-index: 1;
  }
  .modal{
    margin:auto;
    width:100%;
    min-height:50px;
    background:#fff;
  }
  .modal-dialog{
    width:100%;
  }
  #ageStat {
    max-width: 95%;
    max-height: 500px;
    height: 300px;
    margin: auto;
    /*overflow:auto;*/
  }
  #ageWrapper {
    overflow: auto;
  }
  /* Tags */
  #tagStat {
    max-width: 95%;
    max-height: 500px;
    height: 300px;
    margin: auto;
    /*overflow:auto;*/
  }
  /* Geographics */
  .map-holder{
    overflow:auto;
  }
  #chinaMap{
    margin:auto;
  }
  #chinaMap svg{
    display:block;
    margin:auto;
  }
  /* Keywords */
  .canvasOuter{
    height:100%;
    max-width:1050px;
    min-width:450px;
    max-height:700px;
    min-height:350px;
    padding:auto;
    position:relative;
  }
  .canvasHolder{
    background:#fff;
    overflow:auto;
    margin:auto;
    
    position: absolute;
    top: 50%; left: 50%;
    transform: translate(-50%,-50%);
  }
  canvas{
    display:block !important;
    margin:auto;
    
  }
  .widget-user-image{
    width:90px;
    float:left;
  }
  .widget-user-name{
    float:left;
  }
  .widget-user-username{
    margin-left:10px !important;
  }
  .widget-user-name h3{
    margin-right:20px;
    font-size:40px !important;
  }
  .widget-user-header{
    min-height:100px;
  }
  .user-image{
    font-size:90px;
    padding:auto;
  }
  /* Bubbles */
  .bubbleZone {
    background: #000;
    margin: auto;
    max-height: 600px;
    max-width: 600px;
    overflow: hidden;
  }

  .black-curtain {
    background: #000;
  }
  .small-box{
    color:#fff;
  }
  


  </style>
  <!-- Forgeries -->
  <link rel="stylesheet"
  href="${resource(dir: 'css', file: 'mylabels.css')}" type="text/css">
  <link rel="stylesheet"
  href="${resource(dir: 'css/toolbox', file: 'timeseries.css')}"
  type="text/css">

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
        <style>
        .keywords {
          width: 70px;
          height: 35px;
          outline: #FFFFFF solid 2px; font-size =1em;
          padding: 0px;
          color: #222d32;
          font-weight: bold;
        }
        </style>
      </head>

      <body>
        <div id="page-wrapper" class="content-wrapper">
          <section class="content-header">
            <h1 class="page-header">User Stats</h1>
          </section>
          <div class="container-fluid">

            <div class="row">
              <div class="col-lg-12" style="overflow: auto">
                <div class="box box-warning">
                  <div class="box-header with-border">
                    <h3 class="box-title">Gender graph goes here</h3>
                    <div class="box-tools pull-right">
                      <button class="btn btn-box-tool" data-widget="collapse">
                        <i class="fa fa-minus"></i>
                      </button>
                    </div>
                    <!-- /.box-tools -->
                  </div>
                  <!-- /.box-header -->
                  <div class="box-body">
                    <!-- row of 4 stat boxes -->
                    <div class="col-lg-12">
                      <!-- box 1 -->
                      <div class="col-lg-3 numberTag">
                        <!-- small box -->
                        <div class="small-box bg-aqua">
                          <div class="inner">
                            <h3 id="allCount"></h3>
                            <p>Friends</p>
                          </div>
                          <div class="icon">
                            <i class="ion ion-stats-bars"></i>
                          </div>
                          <a href="#" class="small-box-footer fake-link"><i
                            class="fa fa-arrow-circle-right"></i>
                          </a>
                        </div>
                        <!-- /.small-box -->
                      </div>
                      <!-- ./col-3 -->
                      <!-- box 2 -->
                      <div class="col-lg-3 numberTag">
                        <!-- small box -->
                        <div class="small-box bg-green">
                          <div class="inner">
                            <h3 id="maleCount">
                              <sup style="font-size: 20px"></sup>
                            </h3>
                            <p>Males</p>
                          </div>
                          <div class="icon">
                            <i class="ion ion-male"></i>
                          </div>
                          <a href="#" class="small-box-footer fake-link"><i
                            class="fa fa-arrow-circle-right"></i>
                          </a>
                        </div>
                      </div>
                      <!-- ./col-3 -->
                      <!-- box 3 -->
                      <div class="col-lg-3 numberTag">
                        <!-- small box -->
                        <div class="small-box bg-yellow">
                          <div class="inner">
                            <h3 id="femaleCount"></h3>
                            <p>Females</p>
                          </div>
                          <div class="icon">
                            <i class="ion ion-female"></i>
                          </div>
                          <a href="#" class="small-box-footer fake-link"><i
                            class="fa fa-arrow-circle-right"></i>
                          </a>
                        </div>
                      </div>
                      <!-- ./col-3 -->
                      <!-- box 4 -->
                      <div class="col-lg-3 numberTag">
                        <!-- small box -->
                        <div class="small-box bg-red">
                          <div class="inner">
                            <h3 id="unknownCount"></h3>
                            <p>Kept as secrets</p>
                          </div>
                          <div class="icon">
                            <i class="fa fa-transgender"></i>
                          </div>
                          <a href="#" class="small-box-footer fake-link"><i
                            class="fa fa-arrow-circle-right"></i>
                          </a>
                        </div>
                      </div>
                      <!-- ./col-3 -->
                      <!-- Gender graph -->
                      <div class="col-lg-12">
                        <div id="genderGraphHolder">
                          <h3>What's the portion of male and female in the user's friends?</h3>
                        </div>
                        <div id="genderRowHolder">
                          <div id="genderHist"></div>
                        </div>
                      </div>
                    </div>
                    <!-- /.col-lg-12 -->
                  </div>
                  <!-- /.box-body -->
                </div>
                <!-- /.box -->
              </div>
              <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->

            <div class="row">
              <div class="col-lg-12" style="overflow: auto">
                <div class="box box-warning">
                  <div class="box-header with-border">
                    <h3 class="box-title">Age distribution here</h3>
                    <div class="box-tools pull-right">
                      <button class="btn btn-box-tool" data-widget="collapse">
                        <i class="fa fa-minus"></i>
                      </button>
                    </div>
                    <!-- /.box-tools -->
                  </div>
                  <!-- /.box-header -->

                  <div class="box-body" id="ageWrapper">
                    <div class="col-lg-8">
                      <div id="ageStat"></div>
                    </div>
                    <!-- /.col-lg-8 -->

                    <div class="col-lg-4">
                      <!-- Widget: user widget style 1 -->
                      <div class="box box-widget widget-user-2">
                        <!-- Add the bg color to the header using any of the bg-* classes -->
                        <div class="widget-user-header bg-yellow">
                          <div class="widget-user-image">
                            <i class="fa fa-user-secret user-image"></i>
                          </div>
                          <!-- /.widget-user-image -->
                          <div class="widget-user-name">
                            <h3 class="widget-user-username" id="ageUnknownCount">0</h3>
                          </div>
                        </div>
                        <div class="box-footer no-padding">
                          <ul class="nav nav-stacked">
                            <li>out of <span id="ageSumCount">0</span> choose not to expose their ages.</li>
                          </ul>
                        </div>
                      </div>
                      <!-- /.widget-user -->

                    </div>
                    <!-- /.col-lg-4 -->
                  </div>
                  <!-- /.box-body -->
                </div>
                <!-- /.box -->

              </div>
              <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
              <div class="col-lg-12" style="overflow: auto">
                <div class="box box-warning">
                  <div class="box-header with-border">
                    <h3 class="box-title">How do the user's friends tag themselves?</h3>
                    <div class="box-tools pull-right">
                      <button class="btn btn-box-tool" data-widget="collapse">
                        <i class="fa fa-minus"></i>
                      </button>
                    </div>
                    <!-- /.box-tools -->
                  </div>
                  <!-- /.box-header -->

                  <div class="box-body" id="tagWrapper">
                    <div class="col-lg-8">
                      <div id="tagStat"></div>
                    </div>
                    <!-- /.col-lg-8 -->

                    <div class="col-lg-4">
                      <!-- Widget: user widget style 1 -->
                      <div class="box box-widget widget-user-2">
                        <!-- Add the bg color to the header using any of the bg-* classes -->
                        <div class="widget-user-header bg-yellow">
                          <div class="widget-user-image">
                            <i class="fa fa-user-secret user-image"></i>
                          </div>
                          <!-- /.widget-user-image -->
                          <div class="widget-user-name">
                            <h3 class="widget-user-username" id="maxTagName">Tag Name</h3>
                          </div>
                        </div>
                        <div class="box-footer no-padding">
                          <ul class="nav nav-stacked" id="maxTagNames">
                            
                          </ul>
                        </div>
                      </div>
                      <!-- /.widget-user -->

                    </div>
                    <!-- /.col-lg-4 -->
                  </div>
                  <!-- /.box-body -->
                </div>
                <!-- /.box -->

              </div>
              <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <!-- row -->
            <div class="row">
              <div class="col-lg-12">
                <div class="box box-warning">
                  <div class="box-header with-border">
                    <h3 class="box-title">Where are the user's friends from</h3>
                    <div class="box-tools pull-right">
                      <button class="btn btn-box-tool" data-widget="collapse">
                        <i class="fa fa-minus"></i>
                      </button>
                    </div>
                    <!-- /.box-tools -->
                  </div>
                  <!-- /.box-header -->
                  <div class="box-body">
                    <div class="col-lg-8 map-holder">
                      <div id="chinaMap" style="margin: auto"></div>
                    </div>
                    <div class="col-lg-4">
                      <!-- Widget: user widget style 1 -->
                      <div class="box box-widget widget-user-2">
                        <!-- Add the bg color to the header using any of the bg-* classes -->
                        <div class="widget-user-header bg-yellow">
                          <div class="widget-user-image">
                            <i class="ion ion-android-contact user-image"></i>
                          </div>
                          <!-- /.widget-user-image -->
                          <div class="widget-user-name">
                            <h3 class="widget-user-username" id="authUser">Province</h3>
                          </div>
                        </div>
                        <div class="box-footer no-padding">
                          <ul class="nav nav-stacked">
                            <li><a href="#">Most friends come from this province.</a></li>
                          </ul>
                        </div>
                      </div>
                      <!-- /.widget-user -->

                    </div>
                    <!-- /.col-lg-4 -->
                  </div>
                  <!-- /.box-body -->
                </div>
                <!-- /.box -->
              </div>
              <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->

            <div class="row">
              <div class="col-lg-12" style="overflow: auto">

                <div class="box box-warning">
                  <div class="box-header with-border">
                    <h3 class="box-title">How do his friends sketch themselves?</h3>
                    <div class="box-tools pull-right">
                      <button class="btn btn-box-tool" data-widget="collapse">
                        <i class="fa fa-minus"></i>
                      </button>
                    </div>
                    <!-- /.box-tools -->
                  </div>
                  <!-- /.box-header -->

                  <div class="box-body">

                    <div id="introStat" class="col-lg-8 canvasOuter">
                      <div class="canvasHolder">
                        <canvas id="introWordCloudCanvas"></canvas>
                      </div>
                    </div>
                    <div class="col-lg-4">
                      <!-- Widget: user widget style 1 -->
                      <div class="box box-widget widget-user-2">
                        <!-- Add the bg color to the header using any of the bg-* classes -->
                        <div class="widget-user-header bg-yellow">
                          <div class="widget-user-image">
                            <i class="ion ion-android-contact user-image"></i>
                          </div>
                          <!-- /.widget-user-image -->
                          <div class="widget-user-name">
                            <h3 class="widget-user-username" id="introUser">User Name</h3>
                          </div>
                        </div>
                        <div class="box-footer no-padding">
                          <ul class="nav nav-stacked">
                            <li><a href="#"><b>The longest introduction
                              message goes to this user.</b> </a></li>
                              <li><a href="#" id="introLong">Content: </a></li>
                            </ul>
                          </div>
                        </div>
                        <!-- /.widget-user -->

                      </div>
                      <!-- col-lg-4 -->
                    </div>
                    <!-- /.box-body -->
                  </div>
                  <!-- /.box -->

                </div>
                <!-- /.col-lg-12 -->
              </div>
              <!-- /.row -->

              <div class="row">
                <div class="col-lg-12" style="overflow: auto">

                  <div class="box box-warning">
                    <div class="box-header with-border">
                      <h3 class="box-title">The user's authenticated friends</h3>
                      <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse">
                          <i class="fa fa-minus"></i>
                        </button>
                      </div>
                      <!-- /.box-tools -->
                    </div>
                    <!-- /.box-header -->

                    <div class="box-body">

                      <div id="authStat" class="col-lg-8 canvasOuter">
                        <div class="canvasHolder">
                          <canvas id="authWordCloudCanvas"></canvas>
                        </div>
                      </div>
                      <div class="col-lg-4">
                        <!-- Widget: user widget style 1 -->
                        <div class="box box-widget widget-user-2">
                          <!-- Add the bg color to the header using any of the bg-* classes -->
                          <div class="widget-user-header bg-yellow">
                            <div class="widget-user-image">
                              <i class="ion ion-android-contact user-image"></i>
                            </div>
                            <!-- /.widget-user-image -->
                            <div class="widget-user-name">
                              <h3 class="widget-user-username" id="authUser">User Name</h3>
                            </div>
                          </div>
                          <div class="box-footer no-padding">
                            <ul class="nav nav-stacked">
                              <li><a href="#"><b>The longest authentication message
                                 goes to this user.</b> </a></li>
                                <li><a href="#" id="authLong">Content: </a></li>
                                <li>The target user has <span id="authCount">0</span> friends that are verified by identity.</li>
                              </ul>
                            </div>
                          </div>
                          <!-- /.widget-user -->

                        </div>
                        <!-- col-lg-4 -->
                      </div>
                      <!-- /.box-body -->
                    </div>
                    <!-- /.box -->

                  </div>
                  <!-- /.col-lg-12 -->
                </div>
                <!-- /.row -->

                <div class="row">
                  <div class="col-lg-12">
                    <div class="box box-warning">
                      <div class="box-header with-border">
                        <h3 class="box-title">Education graph goes here</h3>
                        <div class="box-tools pull-right">
                          <button class="btn btn-box-tool" data-widget="collapse">
                            <i class="fa fa-minus"></i>
                          </button>
                        </div>
                        <!-- /.box-tools -->
                      </div>
                      <!-- /.box-header -->
                      <div class="box-body">
                        <div class="col-lg-8 black-curtain" style="overflow: auto">

                          <div id="eduDist" class="bubbleZone"></div>
                        </div>

                        <!-- ./col -->
                        <div class="col-lg-4">
                          <!-- small box -->
                          <div class="small-box bg-yellow">
                            <div class="inner">
                              <h3 id="eduBubbleCount">0</h3>
                              <p>
                                <b>Name:</b> <span id="eduBubbleText"></span>
                              </p>
                            </div>
                            <div class="icon">
                              <i class="ion ion-stats-bars"></i>
                            </div>
                            <a href="#" class="small-box-footer fake-link"><i
                              class="fa fa-arrow-circle-right"></i>
                            </a>
                          </div>
                          <!-- /.small-box -->

                          <!-- small box -->
                          <div class="small-box bg-aqua">
                            <div class="inner">
                              <h3 id="eduKnownCount">0</h3>
                              <p>out of <span id="eduAllCount">0</span> are known</p>
                            </div>
                            <div class="icon">
                              <i class="fa fa-book"></i>
                            </div>
                            <a href="#" class="small-box-footer fake-link"><i
                              class="fa fa-arrow-circle-right"></i>
                            </a>
                          </div>
                          <!-- /.small-box -->
                        </div>
                        <!-- /.col-lg-4 -->

                      </div>
                      <!-- /.box-body -->
                    </div>
                    <!-- /.box -->


                  </div>
                  <!-- /.col-lg-12 -->
                </div>
                <!-- /.row -->

                <div class="row">
                  <div class="col-lg-12" style="overflow: auto">
                    <div class="box box-warning">
                      <div class="box-header with-border">
                        <h3 class="box-title">Work distribution</h3>
                        <div class="box-tools pull-right">
                          <button class="btn btn-box-tool" data-widget="collapse">
                            <i class="fa fa-minus"></i>
                          </button>
                        </div>
                        <!-- /.box-tools -->
                      </div>
                      <!-- /.box-header -->
                      <div class="box-body">
                        <div class="col-lg-8 black-curtain">
                          <div id="workDist" class="bubbleZone"></div>
                        </div>
                        <!-- ./col -->
                        <div class="col-lg-4">
                          <!-- small box -->
                          <div class="small-box bg-yellow">
                            <div class="inner">
                              <h3 id="workBubbleCount">0</h3>
                              <p>
                                <b>Name:</b> <span id="workBubbleText"></span>
                              </p>
                            </div>
                            <div class="icon">
                              <i class="ion ion-stats-bars"></i>
                            </div>
                            <a href="#" class="small-box-footer fake-link"><i
                              class="fa fa-arrow-circle-right"></i>
                            </a>
                          </div>
                          <!-- /.small-box -->

                          <!-- small box -->
                          <div class="small-box bg-aqua">
                            <div class="inner">
                              <h3 id="workKnownCount">0</h3>
                              <p>out of <span id="workAllCount">0</span> are known</p>
                            </div>
                            <div class="icon">
                              <i class="fa fa-book"></i>
                            </div>
                            <a href="#" class="small-box-footer fake-link"><i
                              class="fa fa-arrow-circle-right"></i>
                            </a>
                          </div>
                          <!-- /.small-box -->
                        </div>
                        <!-- /.col-lg-4 -->
                      </div>
                      <!-- /.box-body -->

                    </div>
                    <!-- /.box -->
                  </div>
                  <!-- /.col-lg-12 -->
                </div>
                <!-- /.row -->
              </div>
              <!-- /.container-fluid -->
            </div>
            <!-- /#page-wrapper -->
            <content tag="javascript"> 
              <g:javascript src="toolbox/bootstrap.js" /> 
              <g:javascript
              src="toolbox/bootstrap-notify.js" /> 
              <g:javascript
              src="udf/notice.js" /> <g:javascript src="udf/gradient.js" /> 
              <g:javascript
              src="udf/stripePieChart.js" />

              <script type="text/javascript"
              src="http://cdnjs.cloudflare.com/ajax/libs/lodash.js/3.1.0/lodash.min.js"></script>
              <script type="text/javascript"
              src="http://cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
              <script type="text/javascript"
              src="http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.4/highlight.min.js"></script>
              <g:javascript src="toolbox/d3.timeseries.js" /> <g:javascript
              src="toolbox/d3map.js" /> <g:javascript src="udf/promises.js" /> <script
              src="http://d3js.org/queue.v1.min.js"></script> <script
              src="http://d3js.org/topojson.v1.min.js"></script> <g:javascript
              src="udf/overlap.js" /> <!-- bubble chart for work and education -->
              <script
              src="http://phuonghuynh.github.io/js/bower_components/d3-transform/src/d3-transform.js"></script>
              <script
              src="http://phuonghuynh.github.io/js/bower_components/cafej/src/extarray.js"></script>
              <script
              src="http://phuonghuynh.github.io/js/bower_components/cafej/src/misc.js"></script>
              <script
              src="http://phuonghuynh.github.io/js/bower_components/cafej/src/micro-observer.js"></script>
              <script
              src="http://phuonghuynh.github.io/js/bower_components/microplugin/src/microplugin.js"></script>
              <script
              src="http://phuonghuynh.github.io/js/bower_components/bubble-chart/src/bubble-chart.js"></script>
              <script
              src="http://phuonghuynh.github.io/js/bower_components/bubble-chart/src/plugins/central-click/central-click.js"></script>
              <script
              src="http://phuonghuynh.github.io/js/bower_components/bubble-chart/src/plugins/lines/lines.js"></script>
              <g:javascript src="udf/bubbleChart.js" /> 
              <!-- tree map of age --> 
              <g:javascript
              src="udf/treeMap.js" /> 
              <!-- introduction related --> 
              <g:javascript
              src="toolbox/wordcloud2.js" /> 
              <g:javascript
              src="udf/drawWordCloud.js" /> 
              <script>
              function goWithTheMap(data){
                var known=data["known"];
                var unknown=data["unknown"];
                console.log("Geo data is ");
                console.log(data);
                drawChinaMap(known);
              }
              function mapFormat(data){
                console.log("Format the data for map drawing: "+data);
                var list=[];
                for(var k in data){
                  if(data.hasOwnProperty(k)){
                    var place=[];
                    var v=data[k];
                    console.log("This place is: "+k+": "+v);
                    place.push(k);
                    place.push(v);
                    list.push(place);
                  }
                }
                console.log("The converted data is ");
                console.log(list);
                return list;
              }
              function asynchroStat() {
                console.log("Fetching forged data for stats display");
                var jsonData = $.ajax({
                  url : "/CodeBigBroSub/environment/asynchroStats",
                  dataType : "json",
                  async : true
                }).done(function(jsonData) {

                  //result=['all':allCount,'gender':gender,'age':age,'geo':geo,'tag':tags,'edu':edu,'work':work,'authCount':authCount,'authList':authList,'intro':introKeywords,'introLong':longestIntro];

                  console.log("Got stats");
                  console.dir(jsonData);

                  var friends=jsonData["friends"];

                  //all count
                  var allCount=friends["allCount"];

                  //gender information
                  var gender = friends["gender"];
                  console.log("Draw gender map");
                  drawGender(gender);

                  //age
                  var age = friends["age"];
                  console.log("Display age info ");
                  ageWorkFlow(age,allCount);

                  //tag
                  var tag=friends["tag"];
                  console.log("Display tags");
                  tagWorkFlow(tag);

                  //geographical data
                  var geo = friends["geo"];
                  console.log("Draw map");
                  console.log(geo);
                  //var geoData=mapFormat(geo);
                  goWithTheMap(geo);

                  //introduction
                  console.log("Displaying introduction info");
                  var intro=friends["intro"];
                  keywordWorkFlow(intro,"intro");

                  //authentication
                  console.log("Displaying authentication info");
                  var auth=friends["auth"];
                  authWorkFlow(auth,"auth");

                  //education
                  console.log("Draw school distribution");
                  var edu = friends["edu"];
                  bubbleWorkFlow(edu,"edu",allCount);

                  //work
                  console.log("Draw work distribution");
                  var work = friends["work"];
                  //drawBubbleChart("#workDist", work);
                  //var workTable = generateTable(work);
                  //bubbleClick(workTable, "#workDist", "work");
                  bubbleWorkFlow(work,"work",allCount);

          
          
          
          
        });
}
function ageWorkFlow(data,allCount){
  console.log("The age data is ");
  console.log(data);
  var known=data["known"];
	var unknown=data["unknown"];
	//transform data into the desired format
	var transform=treeMapFormat(known);
	console.log("Converted age list to age range map");
	console.log(transform);
	
  drawTreeMap(transform, "#ageStat");
  console.log("Display age stat");
  
        //unknown count
        document.getElementById("ageUnknownCount").innerHTML=unknown;
        //known count
        var sum=0;
        for(var k in known){
          if(known.hasOwnProperty(k)){
            sum+=parseInt(known[k]);
          }
        }
        sum+=parseInt(unknown);
        console.log(sum+"users in total");
        document.getElementById("ageSumCount").innerHTML=allCount;
      }
      function bubbleWorkFlow(data,name,allCount){
        console.log("This is for "+name+" info. The data is ");
        console.log(data);
        var zone="#"+name+"Dist";
        console.log("Will draw in zone "+zone);
    //count
    var known=0;
    var unknown=0;
    var sum=0;
    for(var k in data){
      if(data.hasOwnProperty(k)){
        if(k!='unknown'){
          known+=data[k];
        }else{
          unknown=data[k];
         }
      }
    }
    var knownSpan=document.getElementById(name+"KnownCount");
    if(!knownSpan)
      console.log("Cannot find "+name+"KnownCount");
    else
      knownSpan.innerHTML=known;
    var allSpan=document.getElementById(name+"AllCount");
    if(!allSpan)
      console.log("Cannot locate "+name+"AllCount");
    else
      allSpan.innerHTML=allCount;
    //draw the bubbles
    drawBubbleChart(zone,data);
    //generate click function
    var table=generateTable(data);
    bubbleClick(table,zone,name);

  }
  function keywordWorkFlow(data,name){
    console.log("This for "+name+" info. The data is ");
    console.log(data);
    var keywords=data[name+"Keyword"];
    var longest=data[name+"Long"];
    var userName=data[name+"User"];
    console.log("The longest "+name+" goes to user "+userName);
    //show keywords
    var arr=wordCloudFormat(keywords,50,10);
    var zone=document.getElementById(name+'WordCloudCanvas');
    drawWordCloud(arr,zone);
    //show the longest content and its owner
    showLong(longest,name);

    
  }        
  function showLong(content,name){
    console.log("The longest content is");
    console.log(content);
    var contentZone=document.getElementById(name+"Long");
    contentZone.innerHTML=content["content"];
    var userZone=document.getElementById(name+"User");
    userZone.innerHTML=content["user"];
  }
  function authWorkFlow(data,name){
    console.log("This for "+name+" info. The data is ");
    console.log(data);
    var keywords=data[name+"Keyword"];
    var longest=data[name+"Long"];
    var userName=data[name+"User"];
    var count=data[name+"Count"];
    console.log("The longest "+name+" goes to user "+userName);
    //show keywords
    var arr=wordCloudFormat(keywords,50,10);
    var zone=document.getElementById(name+'WordCloudCanvas');
    drawWordCloud(arr,zone);
    //show the longest content and its owner
    showLong(longest,name);    
    
    var countZone=document.getElementById(name+"Count");
    console.log("Found the count zone");
    console.log(countZone);
    countZone.innerHTML=count;
  } 
  function drawGender(data) {
        //getMapData();
        var mz = document.getElementById("maleZone");
        var fz = document.getElementById("femaleZone");
        var nz = document.getElementById("genderHist");
        var width = 400;
        var height = 100;
        var male = data["male"];
        var female = data["female"];
        var unknown = data["unknown"];
        var all = male + female + unknown;
        var gendered = male + female;
        console.log("Male: " + male + " Female: " + female
          + " Unknown: " + unknown);
        //set the text
        document.getElementById("maleCount").innerHTML = male;
        document.getElementById("femaleCount").innerHTML = female;
        document.getElementById("unknownCount").innerHTML = unknown;
        document.getElementById("allCount").innerHTML = all;
        //draw the image repetitively
        makePeople(nz, male, female);

        //genderRatio(male, female, mz, fz, width, height);
      }
      function tagWorkFlow(data){
      	//transform the map into a list of maps
      	console.log("Here are the tags");
      	console.log(data);
      	var tags=data["tags"];
      	//console.log("The tags are: ");
      	//console.log(tags);
      	//draw tree map
      	var tagList=[];
      	for(var k in tags){
      		if(tags.hasOwnProperty(k)){
      			var v=tags[k];
      			//console.log("Convert key: "+k+" value: "+v);
      			var map={};
      			map["name"]=k;
      			map["value"]=v;
      			tagList.push(map);
      		}
      	}
      	//console.log("the converted list:");
      	console.log(tagList);
      	drawTreeMap(tagList,"#tagStat");
      	//update text zone
      	var maxTag=data["maxTag"];
      	var maxZone=document.getElementById("maxTagName");
      	maxZone.innerHTML=maxTag;
      	var nameList=data["names"];
      	var nameZone=document.getElementById("maxTagNames");
      	for(var i=0;i<nameList.length;i++){
      		var name=nameList[i];
      		var node=document.createElement("li");
      		node.innerHTML=name;
      		nameZone.appendChild(node);
      	}
      }
      console.log("Get the stats");
      asynchroStat();
      </script> 
    </content>
    </html>