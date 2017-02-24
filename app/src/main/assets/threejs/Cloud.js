var Cloud = function(){
 
 this.mesh = new THREE.Object3D();

 var geom = new THREE.BoxGeometry(20,20,20);


 var mat = new THREE.MeshPhongMaterial({
   color:0xd8d0d1,
   transparent:true,
   opacity: Math.random(),
 });


 var nBlocs = 3+Math.floor(Math.random()*3);
 for (var i=0; i<nBlocs; i++ ){
   
   var m = new THREE.Mesh(geom, mat);

   m.position.x = i*15;
   m.position.y = Math.random()*10;
   m.position.z = Math.random()*10;
   m.rotation.z = Math.random()*Math.PI*2;
   m.rotation.y = Math.random()*Math.PI*2;

   
   var s = .1 + Math.random()*.2;
   m.scale.set(s,s,s);

   m.castShadow = true;
   m.receiveShadow = true;

   this.mesh.add(m);
 }
}