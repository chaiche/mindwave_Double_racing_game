

var Model = function(which,whichmodel){

  this.modelObject3d = new THREE.Object3D();
  this.modelbody = new THREE.Mesh(new THREE.BoxGeometry(40, 16, 20),
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

  if(whichmodel ==0){
    this.modelObject3d.add(this.modelbody);
    window.GameJsToJava.sendCreateRe("which:"+which+"model:"+whichmodel+" createSuccess");
  }
  
  this.carRightwheel = new THREE.Mesh(new THREE.CylinderGeometry(5, 5, 2,32),new THREE.MeshLambertMaterial({color: 0x000000}));
  this.carRightwheel.rotation.x = Math.PI/2;
  this.bodyto_carRightwheel = new THREE.Object3D();
  this.bodyto_carRightwheel.position.set(-12,-8,8);
  this.modelbody.add(this.bodyto_carRightwheel);
  this.bodyto_carRightwheel.add(this.carRightwheel);
  this.sphere1 = new THREE.Mesh( new THREE.SphereGeometry( 2, 6, 6 ), new THREE.MeshBasicMaterial( {color: 0x0f0fff} ));
  this.sphere1.position.x = 3;
  this.bodyto_carRightwheel.add(this.sphere1);
  
  this.carright_back = new THREE.Object3D();
  this.carright_back.position.set(-12,-13,10);
  this.modelbody.add(this.carright_back);
  
  this.carLeftwheel = this.carRightwheel.clone();
  this.bodyto_carLeftwheel = new THREE.Object3D();
  this.bodyto_carLeftwheel.position.set(-12,-8,-8);
  this.modelbody.add(this.bodyto_carLeftwheel);
  this.bodyto_carLeftwheel.add(this.carLeftwheel);
  this.sphere2 = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0x00ff00} ));
  this.sphere2.position.x = 3;
  this.bodyto_carLeftwheel.add(this.sphere2);
  
  this.carleft_back = new THREE.Object3D();
  this.carleft_back.position.set(-12,-13,-10);
  this.modelbody.add(this.carleft_back);
  
  this.carforwardtwheel = this.carRightwheel.clone();
  this.bodyto_carforwardtwheel = new THREE.Object3D();
  this.bodyto_carforwardtwheel.position.set(12,-8,0);
  this.modelbody.add(this.bodyto_carforwardtwheel);
  this.bodyto_carforwardtwheel.add(this.carforwardtwheel);
  this.sphere3 = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0xffff00} ));
  this.sphere3.position.x = 3;
  this.bodyto_carforwardtwheel.add(this.sphere3);
  this.modelbody.position.y = 13;


  this.leftfrontSphere = new THREE.Object3D();
  //this.leftfrontSphere = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0xffff00} ));  
   
  this.leftfrontSphere.position.set(20,0,-10);
  this.leftfrontSphere.name = "leftfrontSphere";
  this.modelObject3d.add(this.leftfrontSphere);
  

  this.rightfrontSphere = new THREE.Object3D();
  //this.rightfrontSphere = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0xffff00} ));  
    
  this.rightfrontSphere.position.set(20,0,10);
  this.rightfrontSphere.name = "rightfrontSphere";
  this.modelObject3d.add(this.rightfrontSphere);
  
  this.leftbackSphere = new THREE.Object3D();
  //this.leftbackSphere = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0xffff00} ));  
    
  this.leftbackSphere.position.set(-20,0,-10);
  this.leftbackSphere.name = "leftbackSphere";
  this.modelObject3d.add(this.leftbackSphere);

  this.rightbackSphere = new THREE.Object3D(); 
  //this.rightbackSphere = new THREE.Mesh( new THREE.SphereGeometry( 2, 32, 32 ), new THREE.MeshBasicMaterial( {color: 0xffff00} ));  
    
  this.rightbackSphere.position.set(-20,0,10);
  this.rightbackSphere.name = "rightbackSphere";
  this.modelObject3d.add(this.rightbackSphere);


  this.load = function(src,scale,rotation){
    var loader = new THREE.ObjectLoader();
    loader.load(src,function ( obj ) {

     jsonModel = obj.clone();
     //animation = new THREE.Animation(jsonModel, jsonModel.animations);

     jsonModel.scale.set(scale,scale,scale);
     jsonModel.position.set(0,0,0);
     jsonModel.rotation.y = rotation;

     object[which].model.modelbody = jsonModel.clone();

     object[which].model.modelObject3d.add(object[which].model.modelbody);

     //animation.play();
     window.GameJsToJava.sendCreateRe("which:"+which+"model:"+whichmodel+" createSuccess");

    });
  }

  if(whichmodel>0){
    if(whichmodel==1){
      src = "https://raw.githubusercontent.com/chaiche/programmingGame/master/json/porsche-cayman-threejs/porsche-cayman.json";
      this.leftfrontSphere.position.set(22,0,-10);
      this.rightfrontSphere.position.set(22,0,10);
      this.leftbackSphere.position.set(-25,0,-10);
      this.rightbackSphere.position.set(-25,0,10);
      this.load(src,18,-Math.PI/2);

    }
    else if(whichmodel ==2){

      this.leftfrontSphere.position.set(2,0,-2);
      this.rightfrontSphere.position.set(2,0,2);
      this.leftbackSphere.position.set(-2,0,-2);
      this.rightbackSphere.position.set(-2,0,2);

      var loader = new THREE.JSONLoader();
  loader.load('https://jyunming-chen.github.io/tutsplus/models/laalaa.js', function (geometry, mat) {
    geometry.computeMorphNormals();
    var mat = new THREE.MeshBasicMaterial(
        {
          map: THREE.ImageUtils.loadTexture("https://jyunming-chen.github.io/tutsplus/models/laalaa.png"),
          morphTargets: true, morphNormals: true
        });

    mesh = new THREE.MorphAnimMesh(geometry, mat);
    object[which].model.modelbody = mesh.clone();
    object[which].model.modelbody.position.set(0,12,0);
    object[which].model.modelbody.rotation.y = Math.PI; 
    object[which].model.modelbody.scale.set(0.5,0.5,0.5);
    object[which].model.modelbody.parseAnimations();
    object[which].model.modelbody.playAnimation('run', 10);
    object[which].model.modelObject3d.add(object[which].model.modelbody);
    object[which].canRun = true;

    window.GameJsToJava.sendCreateRe("which:"+which+"model:"+whichmodel+" createSuccess");

  });
    }
    

  }







}