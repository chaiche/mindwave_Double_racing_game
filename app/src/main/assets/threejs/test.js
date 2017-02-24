


var test = function(){


	this.a = 1;
	this.b = 2;

	this.init = function(){
		console.log(this.a);
	}
}

var test1 = new test();

