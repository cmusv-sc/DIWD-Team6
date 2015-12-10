$(document).ready(function(){
	var height = 800;
	var width = 1100;
	var svg = d3.select("body")
		        .append("svg")
		        .attr("width", width)
		        .attr("height", height);

	$.ajax({
      	url : "/graphPaper2Person",
      	type : "GET",
      	dataType : "json"
    }).done(function(data){
      	//console.log(JSON.stringify(data));
      	//{"title":"CO_AUTHOR","from":1,"to":0}
      	//{"cluster":"2","label":"author","id":23,"title":"Sebastian Hommel","value":1,"group":"author"}
      	//{"cluster":"1","id":25,"label":"paper","title":"Evolutionary Customer Evaluation: A Dynamic Approach to a Banking Case.","value":2,"group":"paper"}
      	var nodes = data.nodes;
      	var edges = data.edges;
      	drawGraphD3(nodes, edges);
    });

    function drawGraphD3(nodes, edges) {
	    var color = d3.scale.category20();
	    var arr = [];
	    nodes.forEach(function(node){
	    	arr.push(node.id);
	    })
	    console.log(arr);
	    edges.forEach(function(edge){
	      edge.source = edge.from-1;
	      edge.target = edge.to-1;
	    });
	    console.log(JSON.stringify(edges));
	    //console.log(JSON.stringify(nodes));	
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
});