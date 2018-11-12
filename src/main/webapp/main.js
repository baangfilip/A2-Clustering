$(function() {
	getBlogs();
	$("#group-blogs input[type=number]").on('keyup', function(e){
		validateInput(e.target);
	});
	$("form").submit(function(event) {
		event.preventDefault();
		$("#form-status").html("Loading..");
		let formValid = true;
		$("#group-blogs input[type=number]").each(function(){
			console.log("valid input? " + validateInput(this));
			if(!validateInput(this)){
				formValid = false;
			}
		});
		if(formValid){
			console.log("get grouped blogs");
			let formArr = $(this).serializeArray();
			let json = formToJson(formArr);
			getBlogs(json);
			$("#form-status").html("");
		}
	});
});

function validateInput(input){
	let $input = $(input);
	if($input.val() < 1){
		$input.addClass("is-invalid");
		$input.parent().find(".invalid-feedback").show();
		return false;
	}else{
		$input.removeClass("is-invalid");
		$input.parent().find(".invalid-feedback").hide();
		return true;
	}
}

function formToJson(form){
	const json = {};
	const fields = form.values();
	for(const field of fields){
		json[field.name] = field.value || '';
	}
	return json;
}

function getBlogs(form){
	let url = `API/blogs/`;
	if(form){
		url += `cluster/?centroids=${form.centroids}&maxIterations=${form.maxIterations}&stopOnNoChange=${form.stopOnNoChange ? 1 : 0}&clean=1&nocache=${new Date().getTime()}`;
	}
	$.getJSON(url, function(data) {
		var blogs = [{
			'text' : 'Blogs',
			'state' : {
				'opened' : true,
			},
			'children' : []
		}];
		if(form){ //its clustered response parse accordingly
			renderScatter(data.pointiterations);
			$.each(data.centroids, function(index, group) {
				let centroid = {
					'text' : `${group.name} (Blogs: ${group.blogs.length} )`,
					'state' : {
						'opened' : false,
					},
					'children' : []
				};
				$.each(group.blogs, function(index, blog) {
					centroid.children.push(blog.title);
				});
				blogs[0].children.push(centroid);
			});
			$("#meta").show();
			$("#blogcount").text(data.numberOfBlogs);
			$("#iterations").text(data.iterations);
		}else{
			$.each(data, function(blog, object) {
				blogs[0].children.push(blog);
			});
		}
		renderTree(blogs);
	});
}
function renderTree(blogs){
	$('#jstree').jstree()
	$('#jstree').jstree(true).settings.core.data = blogs;
	$('#jstree').jstree(true).refresh();
	
}
var clusterColors = {
		0: "#f39c12",
		1: "#9b59b6",
		2: "#e74c3c",
		3: "#1abc9c",
		4: "#27ae60",
		5: "#f39c12",
		6: "#95a5a6",
		7: "#34495e",
		
}
function renderScatter(iterationClusters){
	let data = {
        datasets: [],
        labels: []
    }
	$.each(iterationClusters, function(index, iteration){
		let points = [];
		let labels = [];
		let clusterColor = []; 
		let clusterBorderColor = []; 
		$.each(iteration, function(index, point){
			points.push({x: point.x, y: point.y, label: point.title});
			labels.push(point.title);
			if(point.title.indexOf("Centroid ") === 0){
				clusterBorderColor.push("#000")
				clusterColor.push(clusterColors[point.centroidID]);
			}else{
				clusterBorderColor.push(clusterColors[point.centroidID]);
				clusterColor.push(clusterColors[point.centroidID]);
			}
		});
		data.datasets.push({
            label: 'Iteration ' + index,
            data: points,
            pointBackgroundColor: clusterColor,
            pointBorderColor: clusterBorderColor,
            backgroundColor: clusterColors[index]
        });
		data.labels.push(labels);
		
	})
	console.log(data);
	var ctx = $("#iterationsChart")[0].getContext('2d');
	var scatterChart = new Chart(ctx, {
	    type: 'scatter',
	    data: data,
	    options: {
	        scales: {
	            xAxes: [{
	                type: 'linear',
	                position: 'bottom'
	            }]
	        },
	        tooltips: {
	        	callbacks: {
	        		label: function(tooltipItems, data){
	        			let title = data.labels[tooltipItems.datasetIndex][tooltipItems.index];
	        			return title;
	        		}
	        	}
	        }
	    }
	});
}


