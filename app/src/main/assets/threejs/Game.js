
var scene, renderer, camera;
var controls;

var keyboard = new KeyboardState();

var object = [];

var gameStart = false;
var myObject = 0;

var w_model = 3;
var canRun = false;

var c = 5;

var fpsInterval,starttime,now,elapsed,then;
init();
startAnimation(60);




function timedCount(){
 document.getElementById('time').innerHTML=c;
 c=c-1;
 if(c!=-1) {
   setTimeout("timedCount()",1000);
   renderer.render(scene, camera);
 }
 else {
  document.getElementById('time').innerHTML= "";
  gameStart = true;
 }
}

function stopCount(){
 clearTimeout(t)
}


function startAnimation(fps){
  fpsInterval = 1000/ fps;
  then = Date.now();
  starttime = then;
  animate();
}
function init() {

  scene = new THREE.Scene();
  renderer = new THREE.WebGLRenderer();
  renderer.setSize(window.innerWidth, window.innerHeight);
  renderer.setClearColor(0x888888);
  document.body.appendChild(renderer.domElement);

  camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10000);
  camera.position.set(-150,50,0);
  camera.lookAt(0,0,0);
  controls = new THREE.OrbitControls(camera, renderer.domElement);
  
  cameraHUD = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 0.1, 100000);
  cameraHUD.position.set(0,400,0);
  cameraHUD.lookAt(new THREE.Vector3(0,0,0));
  scene.add(cameraHUD);

  var gridXZ = new THREE.GridHelper(100, 10);
  gridXZ.setColors(new THREE.Color(0xff0000), new THREE.Color(0xffffff));
  //scene.add(gridXZ);
  window.addEventListener('resize', onWindowResize, false);
  
  var light = new THREE.PointLight(0xffffff, 1);
  light.position.set(150, 150, 150);
  scene.add(light);
  var amblight = new THREE.AmbientLight(0x404040); // soft white light
  scene.add(amblight);

  //createCar(0);
  //setwhichModel(3);
  
  statusP = window.document.getElementById('statusPrint');

  THREE.ImageUtils.crossOrigin = '';



  //createCar(0);
//   createObject(0,1);
//   createObject(1,2);
  // setmyObject(0);
//   createBackground();
  // createSky();
  // createRoad();
   //gameStart =true;

   window.GameJsToJava.prepare();

}

function createCar(i){

  myObject = i;
  for(var i =0;i<2;i++){
    createObject(i,1);
  }
  var c = object[myObject].model.modelObject3d.localToWorld(new THREE.Vector3(-150,30,0));
  camera.position.copy(c);
  camera.lookAt(object[myObject].model.modelObject3d.position);
  gameStart = true;
}
function setmyObject(i){
  myObject = i;
  window.GameJsToJava.sendCreateRe("setmyObject  "+"createSuccess");
}
function createObject(which,whichmodel){
    object[which] = new Obj(which,whichmodel);
    object[which].name = "id:"+which;
    scene.add(object[which].model.modelObject3d);
    scene.add(object[which].sphere);
    object[which].setCarPosition(0,0,which*50);
}
function createSky(){
  sky = new Sky();
  //scene.add(sky.mesh);
}
function createBackground(){

  

  var iTexture = THREE.ImageUtils.loadTexture('http://jyunming-chen.github.io/tutsplus/images/grasslight-big.jpg');
  //iTexture = THREE.ImageUtils.loadTexture(tmp);
  //'http://jyunming-chen.github.io/tutsplus/images/grasslight-big.jpg'
  //../image/grasslight-big.jpg
  iTexture.wrapS = iTexture.wrapT = THREE.RepeatWrapping;
  iTexture.repeat.set(25,25);
  iTexture.anisotropy = 16;

  var groundMat = new THREE.MeshBasicMaterial({color:0x00A600,specular:0x111111,map:iTexture});
  
  var mesh = new THREE.Mesh(new THREE.PlaneBufferGeometry(2000,2000),groundMat);
  
  //mesh.position.y = -250;
  mesh.rotation.x = -Math.PI /2;
  scene.add(mesh);
  window.GameJsToJava.sendCreateRe("createBackground  "+"createSuccess");
}
function createRoad(){
  
  road = new Road(scene);

  //window.GameJsToJava.sendCreateRe("createRoad  "+"createSuccess");
  
}

function onWindowResize() {
  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(window.innerWidth, window.innerHeight);
}

function animate() {

  controls.update();
  
  keyboard.update();


  if (keyboard.down("space")) {
        gameStart = !gameStart;
  }




  requestAnimationFrame(animate);

  if(gameStart){

    now = Date.now();
    elapsed =now-then;
    if(elapsed>fpsInterval){
      if (keyboard.pressed("up")) {
      		if(object[myObject].car_speed<150 && !object[myObject].isCheck) object[myObject].car_speed +=1;
      }
      else if (keyboard.pressed("down")) {
      		if(object[myObject].car_speed>-10) object[myObject].car_speed -=1;
      }
      else if (keyboard.pressed("space")) {
      		object[myObject].car_speed = 0;
      }
      if (keyboard.pressed("left")) {
      	if(object[myObject].car_theta<0.8) object[myObject].car_theta+=0.013;
      }
      else if (keyboard.pressed("right")) {
      	if(object[myObject].car_theta>-0.8) object[myObject].car_theta-=0.013;
      }

      object[myObject].update();
      
      statusP.innerHTML = object[myObject].name+"  theta = "+object[myObject].car_theta.toFixed (3) + " speed = "+object[myObject].car_speed.toFixed (3)*1.5;

      var c = object[myObject].model.modelObject3d.localToWorld(new THREE.Vector3(-150,30,0));
      camera.position.copy(c);
      camera.lookAt(object[myObject].model.modelObject3d.position);
      cameraHUD.position.set(c.x+150,200,c.z);
      cameraHUD.lookAt(object[myObject].model.modelObject3d.position);

      for(var j=0;j<2;j++){
        if(object[j].canRun){
            object[j].model.modelbody.updateAnimation(0.017 * 1000);
        }
      }
    
        
      then = now - (elapsed % fpsInterval);
      //console.log(then);

      //renderer.render(scene, camera);

      var WW = window.innerWidth;
      var HH = window.innerHeight;
      renderer.enableScissorTest(true);
      
      renderer.setViewport(0, 0, WW, HH);
      camera.aspect = WW / HH;
      camera.updateProjectionMatrix();
      renderer.setScissor(0, 0, WW, HH);
      renderer.clear();
      renderer.render(scene, camera);


      renderer.setViewport(WW-WW / 3, HH - HH / 3, WW / 7, HH / 4);
      renderer.setScissor(WW-WW / 3, HH - HH / 3, WW / 7, HH / 4);
      renderer.clear();

      renderer.render(scene, cameraHUD);

      renderer.enableScissorTest(false);

    }
  }
  //console.log("2");


}

function setGameStart(){
     timedCount();
     object[myObject].model.modelObject3d.position.set(0,0,myObject*50);
}
function carcontroller(i){
  if(gameStart){
    switch (i){
      case 1: object[myObject].car_theta = 0.8;
              break;
      case 2: object[myObject].car_theta = 0.6;
              break;
      case 3: object[myObject].car_theta = 0.4;
              break;
      case 4: object[myObject].car_theta = 0.2;
              break;
      case 5: object[myObject].car_theta = 0.02;
              break;
      case 6: object[myObject].car_theta = -0.2;
              break;
      case 7: object[myObject].car_theta = -0.4;
              break;
      case 8: object[myObject].car_theta = -0.6;
              break;
      case 9: object[myObject].car_theta = -0.8;
              break;
      default:
    }
  }
}

function setwhichModel(i){
  w_model = i;
}

function carChangeSpeed(i){
  if(gameStart){
    if(!object[myObject].isCheck){
      object[myObject].car_speed = i;
      //object[myObject].update();
    }
  }
}
function changeOtherCarPosition(x,z,r){
  if(gameStart){
    if(myObject ==1){
        object[0].model.modelObject3d.position.set(x,0,z);
        object[0].model.modelObject3d.rotation.y = r;
    }
    else{
        object[1].model.modelObject3d.position.set(x,0,z);
        object[1].model.modelObject3d.rotation.y = r;
    }
  }
}

function callRender(){
  renderer.render(scene, camera);
  window.GameJsToJava.sendCreateRe("render");
}

