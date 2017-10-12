(function(){
	var canvas = document.getElementById("play");
	var ctx = canvas.getContext("2d");
	var stage = new createjs.Stage(canvas);
	blackOut(stage);
	stage.update();
})();





function blackOut(stage){
	var shape = new createjs.Shape();
	shape.graphics.beginFill("black"); 
	shape.graphics.drawRect(0, 0, 600, 600);
	stage.addChild(shape);
}