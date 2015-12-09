$(document).ready(function() {
  // var nodes = null;
  // var edges = null;
  // var network = null;
  var height = 800;
  var width = 1100;
  var svg = d3.select("#svgDiv")
              .append("svg")
              .attr("width", width)
              .attr("height", height);

  $('#journalGraphBtn').click(function(){
    var journalName = $('#journalName').val();
    $.ajax({
      url : "/journalGraph",
      type : "POST",
      data : {
        journalName : journalName
      },
      dataType: "text"
    }).done(function(data) {
      var textVal = "Potential collaborators in "+keyword;
      $('#textArea').val(data);
      $('#graphTitle').text(textVal);
      draw(data);
    })
  });

  $('#findCollabBtn').click(function() {
    var keyword = $('#keyword').val();    
    $.ajax({
      url : "/findCollaborators",
      type : "POST",
      data : {
        keyword : keyword
      },
      dataType: "json"
    }).done(function(data) {
      var textVal = "Potential collaborators in "+keyword;
      $('#textArea').val(data);
      $('#graphTitle').text(textVal);
      draw(data);
    })
  });

  $('#findTopPapersBtn').click(function() {
    var keyword = $('#keyword').val();
    $.ajax({
      url : "/graphTopKByKeyword",
      type : "POST",
      data : {
        keyword : keyword
      },
      dataType: "json"
    }).done(function(data) {
      var textVal = "Top 10 related papers in "+keyword;
      $('#textArea').val(data);
      $('#graphTitle').text(textVal);
      draw(data);
    })
  });

  $('#findExpertBtn').click(function(){
    var keyword = $('#keyword').val();
    alert(keyword);
    $.ajax({
      url : "/findExpert",
      type : "POST",
      data : {
        keyword : keyword
      },
      dataType: "json"
    }).done(function(data) {
      var textVal = "Experts in "+keyword;
      $('#textArea').val(data);
      $('#graphTitle').text(textVal);
      draw(data);
    })
  });

  $('#getMultiLevelCoauthorBtn').click(function() {
    var authorName = $('#authorName').val();
    $.ajax({
      url : "/getCoCoAuthor",
      type : "POST",
      data : {
        name : authorName
      },
      dataType: "json"
    }).done(function(data) {
      var textVal = ""+authorName+"'s multi-depth coauther(s)";
      $('#textArea').val(JSON.stringify(data));
      $('#graphTitle').text(textVal);
      var nodes = data.nodes;
      var edges = data.edges;
      //{"cluster":"1","id":0,"label":"author","title":"Robert Neßelrath",
      //"value":2,"group":"coAuthor"
      //add author
      nodes.push({
        cluster : "0",
        id : nodes.length,
        label : "author",
        title : authorName,
        value : 1,
        group : "author"
      })
      //{"title":"CO_AUTHOR","from":1,"to":0}
      var sourceId = nodes.length-1;
      for(i = 0; i < nodes.length-1; i++) {
        if(nodes[i].cluster == "1") {
          edges.push({
            title : "CO_AUTHOR",
            from : sourceId,
            to : nodes[i].id
          });
        }
      }
      drawGraphD3(nodes, edges);
    })
  });


  $('#getCoauthorBtn').click(function() {
    var authorName = $('#authorName').val();
    $.ajax({
      url : "/getCoAuthor",
      type : "POST",
      data : {
        name : authorName
      },
      dataType: "json"
    }).done(function(data) {
      var textVal = ""+authorName+"'s coauther(s)";
      $('#textArea').val(JSON.stringify(data));
      $('#graphTitle').text(textVal);
      var nodes = data.nodes;
      var edges = [];
      nodes.push({
        cluster : "0",
        id : nodes.length,
        label : "author",
        title : authorName,
        value : 1,
        group : "author"
      })
      var sourceId = nodes.length-1;
      for(i = 0; i < nodes.length-1; i++) {
        edges.push({
          title : "CO_AUTHOR",
          from : sourceId,
          to : i
        });
      }
      console.log(JSON.stringify(edges));
      drawGraphD3(nodes, edges);
    })
  }); 

  function paperToPerson() {
    $.ajax({
      url : "/graphPaper2Person",
      type : "GET",
      dataType : "json"
    }).done(function(data){
      draw(data);
    });
  }

  function personToPerson() {
    alert("person to person");
    $.ajax({
      url : "/graphPerson2Person",
      type : "GET",
      dataType : "json"
    }).done(function(data){
      draw(data);
    });
  }

  function drawGraphD3(nodes, edges) {
    var color = d3.scale.category20();
    edges.forEach(function(edge){
      //edge.weight = 1;
      edge.source = edge.from;
      edge.target = edge.to;
    });
    //console.log(JSON.stringify(edges));
    var force = d3.layout.force()
                  .nodes(nodes)
                  .links(edges)
                  .size([width, height])
                  .linkDistance(120)
                  .charge(-300)
                  .on("tick", tick)
                  .start();
    
    var link = svg.selectAll(".link")
                  .data(force.links())
                  .enter()
                  .append("line")
                  .attr("class", "link");

    var node = svg.selectAll(".node")
                  .data(force.nodes())
                  .enter()
                  .append("g")
                  //.append("circle")
                  .attr("class", "node")
                  //.attr("r", 8)
                  //.style("fill", function(d){return color(d.cluster);})
                  .call(force.drag);
    node.append("circle")
        .attr("r", 12)
        .style("fill", function(d) { return color(d.cluster); });

    node.append("text")
        .attr("x", 12)
        .attr("dy", ".35em")
        .text(function(d) {
          return d.title;
        });
    function tick() {
      link
        .attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });

      node
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
    }
  }

  // function draw(data) {
  //   //var test = null;
  //   //var data = JSON.parse(data);
  //   //var container = $('#networkGraph');
  //   var container = document.getElementById('networkGraph');
  //   var options = {
  //     stabilize:false,
  //     edges: {
  //       color: {
  //         color: "gray",
  //         highlight: "gray",
  //       },
  //     },
  //     nodes: {
  //       shape: 'dot',
  //       radiusMin: 10,
  //       radiusMax: 30,
  //     },
  //     groups: {
  //       user: {
  //         color:"#F2545A",
  //         shape:"star",
  //       },
  //       feature: {
  //         shape:"triangle",
  //         color:"#EC8F93",
  //       },
  //       service: {
  //         color:"#EF777C",
  //         shape:"dot",
  //       }
  //     },
  //     tooltip: {
  //       delay: 300,
  //       fontColor: "black",
  //       fontSize: 14, // px
  //       fontFace: "verdana",
  //       color: {
  //         border: "#666",
  //         background: "#FFFFC6"
  //       }
  //     }
  //   };

  //   var network = new vis.Network(container, data, options);
  //   //network.focusOnNode(19);
  //   // network.on('select', function(properties) {
  //   //   var select_node = $.grep(data.nodes, function(e){
  //   //     return e["id"] == properties.nodes[0];
  //   //   })[0];
  //   //   if(select_node["group"] == "service"){
  //   //     var select_edges = $.grep(data.edges, function(e) { 
  //   //       return e["from"] == select_node["id"] });
  //   //     var textVal = "";
  //   //     textVal += "<h3>"+select_node["label"]+"</h3>";
  //   //     textVal += "<div><h4>Keywords:</h4><h4>"
  //   //     for (var i = select_edges.length - 1; i >= 0; i--) {
  //   //       textVal += "<span class=\"label label-primary\">"+select_edges[i]["to"]+"</span>\n";
  //   //     };
  //   //     textVal += "</h4></div>";
  //   //     textVal += "<div><img src=\""+select_node["image"]+"\" class=\"img-responsive\"></div>";
  //   //     $("#testText").html(textVal);
  //   //   }
  //   // });
  // }
})
