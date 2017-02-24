
var Sky = function(){
  
   this.mesh = new THREE.Object3D();
   
  
   // this.nClouds = 30;

   
   // for(var i=0; i<this.nClouds; i++){
   
   // var ra = Math.floor(Math.random()*10);
   // for(var j=0; j<ra; j++){
   //   var c = new Cloud();
   //   c.mesh.position.x = i*1000/10;
   //   c.mesh.position.z = Math.random()*2000;
  
   //   var s = 1+Math.random()*2;
   //   c.mesh.scale.set(s,s,s);

   //   this.mesh.add(c.mesh);
   //   }  
   // }
  THREE.ImageUtils.crossOrigin = '';
  this.url = "http://i.imgur.com/Xl5vjQu.jpg";
  //this.url = "http://i.imgur.com/FYMgcJy.jpg";
  this.texture = THREE.ImageUtils.loadTexture( this.url );
  this.skyGeometry = new THREE.SphereGeometry(4000, 50, 50);
  this.skyMaterial = new THREE.MeshBasicMaterial({map: this.texture});
  this.skyMaterial.side = THREE.BackSide;
  this.mesh = new THREE.Mesh( this.skyGeometry, this.skyMaterial );
  this.mesh.name = "sky";
  this.mesh.position.y = 1500;
  //this.mesh.rotation.x += Math.PI / 2;

  window.GameJsToJava.sendCreateRe("createSky  "+"createSuccess");
}
