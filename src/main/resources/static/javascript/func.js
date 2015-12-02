$(document).ready(function() {
  var nodes = null;
  var edges = null;
  var network = null;

  $('#btn1').click(function() {
    alert($('#para1').val());
    $.ajax({
      url : "/test",
      type : "POST",
      data : {
        content : $('#para1').val()
      },
      dataType: "text"
    }).done(function(data) {
      alert(data);
      $('#para1').val(data);
    })
  }); 

  function draw() {
    var test = null;
    $.ajax({
        url: "graphTest",
        type: "GET",
        contentType: "applcation/json"
      }).done(function(data) {
          var data = JSON.parse(data);
          var container = document.getElementById('mynetwork');
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
          network.focusOnNode(19);
          network.on('select', function(properties) {
            var select_node = $.grep(data.nodes, function(e){
              return e["id"] == properties.nodes[0];
            })[0];
            if(select_node["group"] == "service"){
              var select_edges = $.grep(data.edges, function(e) { 
                return e["from"] == select_node["id"] });
              var textVal = "";
              textVal += "<h3>"+select_node["label"]+"</h3>";
              textVal += "<div><h4>Keywords:</h4><h4>"
              for (var i = select_edges.length - 1; i >= 0; i--) {
                textVal += "<span class=\"label label-primary\">"+select_edges[i]["to"]+"</span>\n";
              };
              textVal += "</h4></div>";
              textVal += "<div><img src=\""+select_node["image"]+"\" class=\"img-responsive\"></div>";
              $("#testText").html(textVal);
            }
          });
      });
  }
})
