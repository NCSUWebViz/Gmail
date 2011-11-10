var datainfo = {"NCSU": [{"Period":"Today","folderSize":"56"}],"success": "true"} 
var paper = new Raphael(document.getElementById('nav'), document.getElementById('nav').width, document.getElementById('nav').height); //initialize the canvas...
var text = paper.text(700,560,' '); //initialize the text to be displayed...
var cwidth = 0;
var x=100;
var y=100;
var flag=true;
function postMessage(a){
	text.attr('text',a);
	text.attr({
		'font-size': '40px'
	});
} //used by Event_ hover as handler function
function drawMailBoxes(JSONObject){
	var data = JSONObject;
	console.log(data)
	var anim = Raphael.animation({cx: 10, cy: 20}, 2e3);
	for (i=0;i<data.folderSizes.length;i++){
		if(i>data.folderSizes.length/2 && flag!=false){
			cwidth=75;
			y=200;
			flag=false;
			console.log(flag);
		}
		var button = paper.image("./resources/images/"+data.folderSizes[i].folderName+".png",x+cwidth,y,80,100);
		button.animate(anim, 500);
		button.attr({
			cursor:'pointer'
		});
		button.data("Name",data.folderSizes[i].folderName);
		button.data("Size",data.folderSizes[i].folderSize);
		button.data("Period",datainfo.NCSU[0].Period);
		button.data("fSize",datainfo.NCSU[0].folderSize);
		button.hover(function () {
				this.animate({width: 120, height: 150}, 2000, 'bounce');
				postMessage(this.data("Name")+' has '+this.data("Size")+ ' mail(s)\n '+this.data("Period")+ ' ' + this.data("fSize"));
        	},function () {
        		this.animate({width: 80, height: 100}, 2000, 'bounce');
        		postMessage(' ');
		});
		button.click(function(){
			var temp = 
			$.ajax({
				  type: 'POST',
				  url: '/GmailViz/folder',
				  data: data,
				  success: success,
				  dataType: dataType
				});
		});
		cwidth+=150;
    }
	
}