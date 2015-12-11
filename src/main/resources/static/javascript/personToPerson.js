$(document).ready(function(){
	$('#card').hide();
	var height = 800;
	var width = 1100;
	var svg = d3.select("#svgDiv")
		        .append("svg")
		        .attr("width", width)
		        .attr("height", height);

	$.ajax({
      	url : "/graphPerson2Person",
      	type : "GET",
      	dataType : "json"
    }).done(function(data){
      	//draw(data);
      	//console.log(JSON.stringify(data));
      	//{"title":"CO_AUTHOR","from":1,"to":0}
    	//"cluster":"1","id":0,"label":"author","title":"Massimo Zancanaro","value":2,"group":"coAuthor"
      	var nodes = data.nodes;
      	var edges = data.edges;
      	drawGraphD3(nodes, edges);
    });

    function drawGraphD3(nodes, edges) {
	    var color = d3.scale.category20();
	    edges.forEach(function(edge){
	      edge.source = edge.from;
	      edge.target = edge.to;
	    });
	    console.log(JSON.stringify(edges));
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
	      var authorName = d3.select(this).select("text").text();
	      console.log(authorName);
	      getAuthorStatus(authorName);
	    }

	    function getAuthorStatus(author) {
	    	$.ajax({
	    		url : "/getAuthorStatus",
	    		type : "POST",
	    		data :{name : author},
	    		dataType : "json"
	    	}).done(function(data) {
	    		console.log(JSON.stringify(data));
	    		$('#rst').empty();
	    		var row1 = "<tr><th>Author Name</th><td>"+author+"</td></tr>"
      			$('#rst').append(row1);
      			var papers = data.nodes;
      			for(i = 0; i < papers.length; i++) {
      				var row = "<tr><th></th><td>"+papers[i].title+"</td></tr>";
      				if(i == 0) {
      					row = "<tr><th>Papers</th><td>"+papers[i].title+"</td></tr>";
      				}
      				$('#rst').append(row);
      			}
	    		$('#card').show();
	    	});
	    }

	    $('#closeBtn').click(function(){
	    	$('#card').hide();
	    }) 
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