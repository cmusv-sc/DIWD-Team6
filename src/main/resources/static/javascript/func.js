$(document).ready(function() {
  // var nodes = null;
  // var edges = null;
  // var network = null;

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
      $('#textArea').val(data);
      $('#graphTitle').text(textVal);
      draw(data);
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
      //alert(data);
      var textVal = ""+authorName+"'s coauther(s)";
      //var t = document.createElement("h4");
      //t.value = authorName;
      //$('#graphTitle').html(textVal);
      //var graphTitle = document.getElementById("graphTitle");
      //graphTitle.appendChild(t);
      $('#textArea').val(data);
      $('#graphTitle').text(textVal);
      draw(data);
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

  function draw(data) {
    //var test = null;
    //var data = JSON.parse(data);
    //var container = $('#networkGraph');
    var container = document.getElementById('networkGraph');
    var options = {
      stabilize:false,
      edges: {
        color: {
          color: "gray",
          highlight: "gray",
        },
      },
      nodes: {
        shape: 'dot',
        radiusMin: 10,
        radiusMax: 30,
      },
      groups: {
        user: {
          color:"#F2545A",
          shape:"star",
        },
        feature: {
          shape:"triangle",
          color:"#EC8F93",
        },
        service: {
          color:"#EF777C",
          shape:"dot",
        }
      },
      tooltip: {
        delay: 300,
        fontColor: "black",
        fontSize: 14, // px
        fontFace: "verdana",
        color: {
          border: "#666",
          background: "#FFFFC6"
        }
      }
    };

    var network = new vis.Network(container, data, options);
    //network.focusOnNode(19);
    // network.on('select', function(properties) {
    //   var select_node = $.grep(data.nodes, function(e){
    //     return e["id"] == properties.nodes[0];
    //   })[0];
    //   if(select_node["group"] == "service"){
    //     var select_edges = $.grep(data.edges, function(e) { 
    //       return e["from"] == select_node["id"] });
    //     var textVal = "";
    //     textVal += "<h3>"+select_node["label"]+"</h3>";
    //     textVal += "<div><h4>Keywords:</h4><h4>"
    //     for (var i = select_edges.length - 1; i >= 0; i--) {
    //       textVal += "<span class=\"label label-primary\">"+select_edges[i]["to"]+"</span>\n";
    //     };
    //     textVal += "</h4></div>";
    //     textVal += "<div><img src=\""+select_node["image"]+"\" class=\"img-responsive\"></div>";
    //     $("#testText").html(textVal);
    //   }
    // });
  }
})
