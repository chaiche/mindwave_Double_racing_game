var planes = [];
var num = 0;
var Plane = function(mesh){
	this.mesh = mesh;
	this.mesh.name = "which"+num;
}
function findPlanes(mesh){
	for(var i=0;i<planes.length;i++){
		if(mesh ===planes[i].mesh){
			//console.log(planes[i].mesh.name);
	      // var x = planes[i].mesh.position.clone();
	      // var y = car[this].car.leftfrontSphere.localToWorld(new THREE.Vector3(0,0,0));
	      
	      // var z = x.clone().sub(y);
	      // var l = Math.sqrt(z.x*z.x+z.y*z.y+z.z*z.z);
	      // var tString = "x="+z.x+" y"+z.y+" z" + z.z;
	      //planes[i].mesh.material.color = new THREE.Color( 0xff0000 );
	      
	      return true;
		}
	}
	return false;
}
var tempb,tempc;;

var Road = function (scene) {

	this.loader = new THREE.ObjectLoader();

	this.url = "https://raw.githubusercontent.com/chaiche/graphicsHW/master/models/teapot.json";
	this.url ="https://raw.githubusercontent.com/chaiche/graphicsHW/master/json/chain-fence.json";
  	this.loader.load(this.url,function ( obj ) {

     	this.road = [];
     	var i = 0;
     	for(i=0;i<7;i++){
	      aa = obj.clone();
	      aa.scale.set(4, 4, 4);
	      aa.position.set(i*90,0,-50);
	      aa.name = "which"+num;
	      this.road[i] = aa ;
	      scene.add(this.road[i]);
	      bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
		  bb.visible = false;
		  bb.position.copy(aa.position);
		  bb.position.y = 10;
		  bb.rotation.y = aa.rotation.y;
		  scene.add(bb);
		  planes.push(new Plane(bb));
	      num++;
    	}
    	for(i;i<11;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(i*90,0,-25+50*(i-7));
		    aa.rotation.y = -Math.PI/6;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<19;i++){
			aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(11*90-40,0,200+(i-11)*90);
		    aa.rotation.y = -Math.PI/2;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<20;i++){
			aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(11*90-40-20,0,200+(i-11)*90);
		    aa.rotation.y = Math.PI/3;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<22;i++){
			aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(860-(i-20)*90,0,960);
		    aa.rotation.y = Math.PI;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<29;i++){
			aa = obj.clone();
			aa.scale.set(4, 4, 4);
			aa.position.set((i-22)*90,0,100);
			aa.name = "which"+num;
			this.road[i] = aa ;
			scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<31;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set((i-22)*90,0,130+50*(i-22-7));
		    aa.rotation.y = -Math.PI/6;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<37;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(760,0,250+(i-31)*90);
		    aa.rotation.y = -Math.PI/2;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;		    
		}
		for(i;i<38;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(740,0,800);
		    aa.rotation.y = Math.PI/3;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<40;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(660-(i-38)*90,0,850);
		    aa.rotation.y = Math.PI;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<43;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(680-(i-40)*90,0,960);
		    aa.rotation.y = Math.PI;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.rotation.y = aa.rotation.y;
			bb.position.y = 10;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<45;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(400-(i-43)*90,0,940-(i-43)*50);
		    aa.rotation.y = -Math.PI/6;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<47;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(490-(i-45)*90,0,830-(i-45)*50);
		    aa.rotation.y = -Math.PI/6;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<51;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(340-(i-47)*50,0,720-(i-47)*90);
		    aa.rotation.y = -Math.PI/3;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}

		for(i;i<54;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(250-(i-51)*50,0,830-(i-51)*90);
		    aa.rotation.y = -Math.PI/3;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<61;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(110-(i-54)*90,0,440+(i-54)*50);
		    aa.rotation.y = Math.PI/6;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<67;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(80-(i-61)*90,0,640+(i-61)*50);
		    aa.rotation.y = Math.PI/6;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		for(i;i<71;i++){
		    aa = obj.clone();
		    aa.scale.set(4,4,4);
		    aa.position.set(-370-90*(i-66),0,890+30);
		    aa.rotation.y = Math.PI;
		    aa.name = "which"+num;
		    this.road[i] = aa ;
		    scene.add(this.road[i]);
	      	bb = new THREE.Mesh(new THREE.BoxGeometry(85,20,3), new THREE.MeshBasicMaterial( {color: 0x0000ff} ));
			bb.visible = false;
			bb.position.copy(aa.position);
			bb.position.y = 10;
			bb.rotation.y = aa.rotation.y;
			scene.add(bb);
			planes.push(new Plane(bb));
			num++;
		}
		// for(var i=0;i<roads.length;i++){
			
		// 	console.log(roads[i].mesh.name);
		// }

		// for(var i=0;i<planes.length;i++){
		// 		planes[i].mesh.visible  = true;
		// }

	
		window.GameJsToJava.sendCreateRe("createRoad  "+"createSuccess");
	  });


	this.end = new THREE.Mesh(new THREE.PlaneGeometry(250,10),new THREE.MeshBasicMaterial());
	this.end.position.set(-370-90*(70-66)+170,1,840);
	//this.end.position.set(500,1,0);
	this.end.rotation.x = -Math.PI/2;
	this.end.rotation.z = Math.PI/5;
	this.end.name = "end";
	scene.add(this.end);
	
}