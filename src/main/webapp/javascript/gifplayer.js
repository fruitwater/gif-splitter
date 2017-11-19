(function(){
	let canvas = document.getElementById("play");
	let ctx = canvas.getContext("2d");
	let stage = new createjs.Stage(canvas);
	blackOut(stage);
	stage.update();
	
	//preload.jsを宣言する
	let preload = new createjs.LoadQueue();
	//queue.loadFile({id:"chara",src:"http://jsrun.it/assets/u/H/j/W/uHjWj.png"});

	let list = [];
	
	//マニフェストのリストを作成
	for(let i = 0;i<urls.length;i++){
		let obj = {id:"file"+i,src:urls[i]};
		list.push(obj);
	}
	
	preload.loadManifest(list);
	
	preload.addEventListener("complete", function(){
		let images = [];
		let frames = [];
		let index = 0;
		let timerId;
		
		for(let i = 0; i < list.length;i++){
			let img = preload.getResult(list[i].src);
			let bitmap = new createjs.Bitmap(img);
			images.push(bitmap);
			frames.push(i);
		}
		
		function timer(){
			timerId = setTimeout(function(){
				index++;
				if(index === images.length){
					index = 0;
				}
				stage.removeAllChildren();
				stage.addChild(images[index]);
				stage.update();
				timer();
				console.log(index);
			},delay*10);
		}
		
		timer();
		
	});
	
})();


function blackOut(stage){
	var shape = new createjs.Shape();
	shape.graphics.beginFill("black"); 
	shape.graphics.drawRect(0, 0, 600, 600);
	stage.addChild(shape);
}
