var clock = new THREE.Clock();

var Car = function () {
  
  this.name = "123";

  this.car = new CarModel();   // 車模型

  this.sphere = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0xffff00} ));  
  
  this.C = new THREE.Vector3();
  
  this.car_speed = 5;   // 車速
  this.car_theta = 0.35;   //  車角度
  this.speed = this.car_speed * 2;   //  更改車速

  this.isCheck = false;

  this.checkClock = new THREE.Clock();
};
Car.prototype.setCarPosition = function(x,y,z){
  this.car.carModel.position.set(x,y,z);
  this.sphere.position.set(x,y,z);
  this.C = new THREE.Vector3(x,y,z);
}
Car.prototype.update = function(){
	//運算車移動的位置
  this.speed = this.car_speed * 2;
  RC = this.car.carModel.localToWorld (new THREE.Vector3(-12,0,-24/Math.tan(this.car_theta)));
  this.sphere.position.copy (RC);
  var omega = this.speed * Math.tan(this.car_theta)/24;
  var deltaT = clock.getDelta();
  var vv = this.C.clone().sub(RC).applyAxisAngle (new THREE.Vector3(0,1,0), omega*deltaT);
  tempC = this.C.clone();
  tempcar_position = this.car.carModel.position.clone();
  temprocartation = this.car.carModel.rotation.y ;
  this.C = vv.add(RC);
  this.car.carModel.position.copy(this.C);
  this.car.carModel.rotation.y += omega*deltaT;

  var tmp = "x"+this.car.carModel.position.x + "z"+this.car.carModel.position.z+"r"+this.car.carModel.rotation.y+"a";
  //var a = window.GameJsToJava.sendData(tmp);
  tmp= this.name+"x"+this.car.carModel.position.x + "z"+this.car.carModel.position.z+"r"+this.car.carModel.rotation.y;
  //window.GameJsToJava.sendDataByHTTP(tmp);

  this.car.bodyto_carforwardtwheel.rotation.y = this.car_theta;
  var omegabw = this.speed / 5;
  this.car.bodyto_carRightwheel.rotation.z -= omegabw * deltaT;
  this.car.bodyto_carLeftwheel.rotation.z -= omegabw * deltaT;
  var omegafw = this.speed / (5 * Math.cos(this.car_theta));
  this.car.bodyto_carforwardtwheel.rotation.z -= omegafw * deltaT;

  var whichdir = this.check();

  if(whichdir == 0 || whichdir == 1){
    if(this.car_speed>=0)
      this.car_speed = -this.car_speed*0.5;
    this.isCheck  = true;
    this.count = 0;
    this.checkClock = new THREE.Clock();
    //console.log(this.checkClock.getDelta());
  }
  else if(whichdir==2 || whichdir ==3){
    this.car_speed = 15;
    this.isCheck = true;
    this.count = 0;
    this.checkClock = new THREE.Clock();
  }
  else if(whichdir ==-1){

  }

  if(this.isCheck){
    this.count++;
    //console.log(this.count);
    if(this.checkClock.getElapsedTime()>0.15){
      this.isCheck = false;
    }
  }

  this.checkend();
}


var raycaster = new THREE.Raycaster(new THREE.Vector3(0,0,0),new THREE.Vector3(0,0,0),0,21.5406592285);

Car.prototype.check = function(){

  var origin = new THREE.Vector3(0,0,0);
  
  var x = this.car.leftfrontSphere.localToWorld(new THREE.Vector3(0,0,0));
  
  for(var j = 0;j<4;j++){
      switch(j){
        case 0:
          x = this.car.leftfrontSphere.localToWorld(new THREE.Vector3(0,0,0));
          break;
        case 1:
          x = this.car.rightfrontSphere.localToWorld(new THREE.Vector3(0,0,0));
          break;
        case 2:
          x = this.car.leftbackSphere.localToWorld(new THREE.Vector3(0,0,0));
          break;
        case 3:
          x = this.car.rightbackSphere.localToWorld(new THREE.Vector3(0,0,0));
            break;
        default: break;
      
      }
      
      //console.log(x);
      var y = this.car.carModel.localToWorld(new THREE.Vector3(0,0,0));
      //console.log(y);



      var z = x.clone().sub(y);

      z.y = 0;

      //z = tempc.position.clone();

      raycaster.set(y,z.normalize());

      var intersects = raycaster.intersectObjects(scene.children);
      if (intersects.length > 0) {
        for(var i =0;i<intersects.length;i++){
          if(findPlanes(intersects[i].object)){
            return j;
            break;  
          }
           //console.log(intersects[i].object);
        }
      }else{
        for(var i=0;i<planes.length;i++){
          planes[i].mesh.material.color =new THREE.Color( 0x0000ff );;
        }
      }
  }
  return -1;
}

var raycaster_end = new THREE.Raycaster();
Car.prototype.checkend = function(){


  var y = this.car.carModel.localToWorld(new THREE.Vector3(0,2,0));
  raycaster_end.set(y,new THREE.Vector3(0,-1,0));

  var intersects = raycaster_end.intersectObjects(scene.children);
  if (intersects.length > 0) {
    for(var i =0;i<intersects.length;i++){
      if(intersects[i].object.name=="end")
        window.GameJsToJava.sendEnd();
        //console.log(intersects[i].object);
      } 
  }

}

