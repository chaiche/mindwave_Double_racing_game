

var CarModel = function(){

  this.carModel = new THREE.Object3D();
  this.carbody = new THREE.Mesh(new THREE.BoxGeometry(40, 16, 20),
                                new THREE.MeshLambertMaterial({
    color: 0xff0000,
    transparent: true,
    opacity: 0.5
  }));

//   var loader = new THREE.ObjectLoader();
// loader.load("https://raw.githubusercontent.com/chaiche/graphicsHW/gh-pages/images/porsche-cayman-threejs/porsche-cayman.json",function ( obj ) {
//      jsonModel = obj.clone();
//      jsonModel.scale.set(10, 10, 10);
//      jsonModel.position.set(70,0,0);
//      ob.add(jsonModel);
// });




  this.carModel.add(this.carbody);
  
  this.carRightwheel = new THREE.Mesh(new THREE.CylinderGeometry(5, 5, 2,32),new THREE.MeshLambertMaterial({color: 0x000000}));
  this.carRightwheel.rotation.x = Math.PI/2;
  this.bodyto_carRightwheel = new THREE.Object3D();
  this.bodyto_carRightwheel.position.set(-12,-8,8);
  this.carbody.add(this.bodyto_carRightwheel);
  this.bodyto_carRightwheel.add(this.carRightwheel);
  this.sphere1 = new THREE.Mesh( new THREE.SphereGeometry( 2, 6, 6 ), new THREE.MeshBasicMaterial( {color: 0x0f0fff} ));
  this.sphere1.position.x = 3;
  this.bodyto_carRightwheel.add(this.sphere1);
  
  this.carright_back = new THREE.Object3D();
  this.carright_back.position.set(-12,-13,10);
  this.carbody.add(this.carright_back);
  
  this.carLeftwheel = this.carRightwheel.clone();
  this.bodyto_carLeftwheel = new THREE.Object3D();
  this.bodyto_carLeftwheel.position.set(-12,-8,-8);
  this.carbody.add(this.bodyto_carLeftwheel);
  this.bodyto_carLeftwheel.add(this.carLeftwheel);
  this.sphere2 = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0x00ff00} ));
  this.sphere2.position.x = 3;
  this.bodyto_carLeftwheel.add(this.sphere2);
  
  this.carleft_back = new THREE.Object3D();
  this.carleft_back.position.set(-12,-13,-10);
  this.carbody.add(this.carleft_back);
  
  this.carforwardtwheel = this.carRightwheel.clone();
  this.bodyto_carforwardtwheel = new THREE.Object3D();
  this.bodyto_carforwardtwheel.position.set(12,-8,0);
  this.carbody.add(this.bodyto_carforwardtwheel);
  this.bodyto_carforwardtwheel.add(this.carforwardtwheel);
  this.sphere3 = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0xffff00} ));
  this.sphere3.position.x = 3;
  this.bodyto_carforwardtwheel.add(this.sphere3);
  this.carbody.position.y = 13;


  this.leftfrontSphere = new THREE.Object3D(); 
  this.leftfrontSphere.position.set(20,0,-10);
  this.leftfrontSphere.name = "leftfrontSphere";
  this.carbody.add(this.leftfrontSphere);
  
  this.rightfrontSphere = new THREE.Object3D(); 
  this.rightfrontSphere.position.set(20,0,10);
  this.rightfrontSphere.name = "rightfrontSphere";
  this.carbody.add(this.rightfrontSphere);
  
  this.leftbackSphere = new THREE.Object3D(); 
  this.leftbackSphere.position.set(-20,0,-10);
  this.leftbackSphere.name = "leftbackSphere";
  this.carbody.add(this.leftbackSphere);

  this.rightbackSphere = new THREE.Object3D(); 
  this.rightbackSphere.position.set(-20,0,10);
  this.rightbackSphere.name = "rightbackSphere";
  this.carbody.add(this.rightbackSphere);



}