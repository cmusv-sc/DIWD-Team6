$(document).ready(function() {
  // var nodes = null;
  // var edges = null;
  // var network = null;
  
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
      var nodes = data.nodes;
      nodes.push({
        cluster : 10,
        label : "keyword",
        title : keyword,
        value : 1,
        group : "keyword"
      });
      var idx = nodes.length-1;
      var edges = [];
      for(i = 0; i < idx; i++) {
        edges.push({
          source : idx,
          target : i
        });
      }
      drawGraphD3(nodes, edges);
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
      var nodes = data.nodes;
      var edges = data.edges;
      edges.forEach(function(edge){
        edge.source = edge.from-1;
        edge.target = edge.to-1;
      });
      console.log(JSON.stringify(edges));
      drawGraphD3(nodes, edges);
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
      var nodes = data.nodes;
      nodes.push({
        cluster : 10,
        label : "keyword",
        title : keyword,
        value : 1,
        group : "keyword"
      });
      var idx = nodes.length-1;
      var edges = [];
      for(i = 0; i < idx; i++) {
        edges.push({
          source : idx,
          target : i
        });
      }
      drawGraphD3(nodes, edges);
    })
  });

  $('#getCoauthorBtn').click(function() {
    var authorName = $('#authorName').val();
    var select = $('#select option:selected').val();
    if(select == 2) {
      $.ajax({
        url : "/getCoCoAuthor",
        type : "POST",
        data : {
          name : authorName
        },
        dataType: "json"
      }).done(function(data) {
        var nodes = data.nodes;
        var edges = data.edges;
        //add author
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
          if(nodes[i].cluster == "1") {
            edges.push({
              title : "CO_AUTHOR",
              from : sourceId,
              to : nodes[i].id
            });
          }
        }
        edges.forEach(function(edge){
          //edge.weight = 1;
          edge.source = edge.from;
          edge.target = edge.to;
        });
        drawGraphD3(nodes, edges);
      })
    }
    else {
      $.ajax({
        url : "/getCoAuthor",
        type : "POST",
        data : {
          name : authorName
        },
        dataType: "json"
      }).done(function(data) {
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
          edges.forEach(function(edge){
          //edge.weight = 1;
          edge.source = edge.from;
          edge.target = edge.to;
        });
        drawGraphD3(nodes, edges);
      })
    }
  }); 
  
  $('#timelineBtn').click(function(){
    var startYear = $('#startYear').val();
    var endYear = $('#endYear').val();
    var authorList = $('#authorName1').val();
    if($('#authorName2').val() != "") {
      authorList = authorList + ", "+$('#authorName2').val();
    }
    if($('#authorName3').val() != "") {
      authorList = authorList + ", "+$('#authorName3').val();
    }
    $.ajax({
      url : "/timelineOfAuthors",
      type : "POST",
      data : {
        startYear : startYear,
        endYear : endYear,
        authorList : authorList
      },
      dataType : "json"
    }).done(function(data) {
      var authors = data.author;
      var nodes = [];
      var edges = [];
      var id = 0;
      authors.forEach(function(author){
        var node = {title : author.name, cluster : 1, id : id};
        id = id + 1;
        nodes.push(node);
        var yps = author.year;
        yps.forEach(function(yp){
          var nodey = {title : yp.year, cluster : 4, id : id};
          id = id + 1;
          nodes.push(nodey);
          edges.push({source : node.id, target : nodey.id});
          var publications = yp.publication;
          publications.forEach(function(p) {
            var nodep = {title : p.title, cluster : 7, id : id};
            id = id + 1;
            nodes.push(nodep);
            edges.push({source : nodey.id, target : nodep.id});
          })
        })
      });
      drawGraphD3(nodes, edges);
    });
  });

  $('#journalGraphBtn').click(function(){
    var journalName = $('#journalName').val();
    $.ajax({
      url : "/journalGraph",
      type : "POST",
      data : {
        name : journalName
      },
      dataType : "json"
    }).done(function(data){
      var nodes = [];
      var edges = [];
      var id = 0;
      nodes.push({title : journalName, id : id, cluster : 1});
      id = id + 1;
      cons = data.Contributions;
      cons.forEach(function(d){
        var title = d.Name + ": " + d.Contribution;
        nodes.push({title : title, id : id, cluster : 6});        
        edges.push({source : 0, target : id});
        id = id + 1;
      });
      drawGraphD3(nodes, edges);
    });
  });

  $('#categorizeBtn').click(function(){
    var keyword = $('#keyword').val();
    var startYear = $('#startYear').val();
    var endYear = $('#endYear').val();
    var channel = $('#channel').val();
    var data = {
      startYear : startYear,
      endYear : endYear,
      channel : channel,
      keywordList : keyword
    }
    if(keyword == "" || channel == "") {
      data =  {
        startYear : startYear,
        endYear : endYear
      }
    }
    $.ajax({
      url : "/categorize",
      type : "POST",
      data : data,
      dataType : "json"
    }).done(function(data) {
      var nodes = [];
      var edges = [];
      var database = data.Database;
      var software = data.Software;
      var os = "Operating System";
      var OS = data[os];
      var web = data.Web;
      var other = data.Other;
      var id = 0;
      var nodeD = {title : "Database", id : id, cluster : 1};
      id = id + 1;
      var nodeS = {title : "Software", id : id, cluster : 1};
      id = id + 1;
      var nodeO = {title : "Operating System", id : id, cluster : 1};
      id = id + 1;
      var nodeW = {title : "Web", id : id, cluster : 1};
      id = id + 1;
      var nodeOT = {title : "Other", id : id, cluster : 1};
      id = id + 1;
      nodes.push(nodeD);
      nodes.push(nodeS);
      nodes.push(nodeO);
      nodes.push(nodeW);
      nodes.push(nodeOT);
      for(i = 0; i < database.length; i++) {
        if(i > 50) {break;}
        nodes.push({title : database[i].title, id : id, cluster : 6});
        edges.push({source : nodeD.id, target : id});
        id = id + 1;
      };
      for(i = 0; i < software.length; i++) {
        if(i > 50) {break;}
        nodes.push({title : software[i].title, id : id, cluster : 6});
        edges.push({source : nodeS.id, target : id});
        id = id + 1;
      };
      for(i = 0; i < OS.length; i++) {
        if(i > 50) {break;}
        nodes.push({title : OS[i].title, id : id, cluster : 6});
        edges.push({source : nodeO.id, target : id});
        id = id + 1;
      };
      for(i = 0; i < web.length; i++) {
        if(i > 50) {break;}
        nodes.push({title : web[i].title, id : id, cluster : 6});
        edges.push({source : nodeW.id, target : id});
        id = id + 1;
      };
      for(i = 0; i < other.length; i++) {
        if(i > 50) {break;}
        nodes.push({title : other[i].title, id : id, cluster : 6});
        edges.push({source : nodeOT.id, target : id});
        id = id + 1;
      };
      drawGraphD3(nodes, edges);
    })
  });

  $('#evolutionBtn').click(function() {
    var startYear = $('#startYear').val();
    var endYear = $('#endYear').val();
    var journalName = $('#journalName').val();
    $.ajax({
      url : "/getJournalEvolution",
      type : "POST",
      data : {
        startYear : startYear,
        endYear : endYear,
        name : journalName
      },
      dataType : "json"
    }).done(function(data) {
      var evo = data.Evolution;
      $('#rst').empty();
      $('#rst').append("<tr><th>Year</th><th>Focused Topic</th></tr>");
      var i = 0;
      evo.forEach(function(e) {
        var row = "<tr><td>"+e.Year+"</td><td>"+e.topic+"</td></tr>";
        if(i % 2 == 0) {
          row = '<tr class="success"><td>'+e.Year+'</td><td>'+e.topic+'</td></tr>';
        }
        i++;
        $('#rst').append(row);
      })
    });
  });

  $('#topPaperChannelBtn').click(function() {
    var year = $('#year').val();
    var channel = $('#channel').val();
    $.ajax({
      url : "/getTopKCitedPaper",
      type : "POST",
      data : {
        year : year,
        name : channel
      },
      dataType : "json"
    }).done(function(data) {
      var rst = data.Result;
      $('#rst').empty();
      $('#rst').append("<tr><th>Rank</th><th>Paper</th></tr>");
      var i = 0;
      rst.forEach(function(e){
        var row = "<tr><td>"+e.rank+"</td><td>"+e.Name+"</td></tr>";
        if(i % 2 == 0) {
          row = '<tr class="info"><td>'+e.rank+'</td><td>'+e.Name+'</td></tr>';
        }
        i++;
        $('#rst').append(row);
      });
    });
  });

  function drawGraphD3(nodes, edges) {
    $('#svgDiv').empty()
    var height = 800;
    var width = 1100;
    var zoom = d3.behavior.zoom()
                  .translate([0, 0])
                  .scaleExtent([0.1, 10])
                  .scale(1)
                  .on("zoom", zoomed);
    
    var svg = d3.select("#svgDiv")
                .append("svg")
                .attr("width", width)
                .attr("height", height);
                //.call(zoom);
    var container = svg.append("g");
    function zoomed() {
      container.attr("transform", "translate(" + d3.event.translate + ") scale(" + d3.event.scale + ")");
    }
    
    var color = d3.scale.category20();

    var force = d3.layout.force()
                  .nodes(nodes)
                  .links(edges)
                  .size([width, height])
                  .linkDistance(120)
                  .charge(-300)
                  .on("tick", tick)
                  .start();
    
    var link = container.selectAll(".link")
                  .data(force.links())
                  .enter()
                  .append("line")
                  .attr("class", "link");

    var node = container.selectAll(".node")
                  .data(force.nodes())
                  .enter()
                  .append("g")
                  .attr("class", "node")
                  .on("click", fClick)
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

    function fClick() {
      var t = d3.select(this).select("text").text();
      console.log(t);
    }
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
})


