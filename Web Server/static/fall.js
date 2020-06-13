 // More API functions here:
    // https://github.com/googlecreativelab/teachablemachine-community/tree/master/libraries/pose

    // the link to your model provided by Teachable Machine export panel
    const URL = "https://teachablemachine.withgoogle.com/models/F7q56wJxy/";
    let model, webcam, ctx, labelContainer, maxPredictions;

    async function init() {
        const modelURL = URL + "model.json";
        const metadataURL = URL + "metadata.json";

        // load the model and metadata
        // Refer to tmImage.loadFromFiles() in the API to support files from a file picker
        // Note: the pose library adds a tmPose object to your window (window.tmPose)
        model = await tmPose.load(modelURL, metadataURL);
        maxPredictions = model.getTotalClasses();

        // Convenience function to setup a webcam
        const size = 350;
        const flip = true; // whether to flip the webcam
        webcam = new tmPose.Webcam(size, size, flip); // width, height, flip
        await webcam.setup(); // request access to the webcam
        await webcam.play();
        window.requestAnimationFrame(loop);

        // append/get elements to the DOM
        const canvas = document.getElementById("canvas");
        canvas.width = size; canvas.height = size;
        ctx = canvas.getContext("2d");
        labelContainer = document.getElementById("label-container");
        for (let i = 0; i < maxPredictions; i++) { // and class labels
            labelContainer.appendChild(document.createElement("div"));
        }
    }

    async function loop(timestamp) {
        webcam.update(); // update the webcam frame
        await predict();
        window.requestAnimationFrame(loop);
    }
    
    async function predict() {
        // Prediction #1: run input through posenet
        // estimatePose can take in an image, video or canvas html element
        const { pose, posenetOutput } = await model.estimatePose(webcam.canvas);
        // Prediction 2: run input through teachable machine classification model
        const prediction = await model.predict(posenetOutput);

        for (let i = 0; i < maxPredictions; i++) {
            const classPrediction =
                prediction[i].className + ": " + prediction[i].probability.toFixed(2);
            labelContainer.childNodes[i].innerHTML = classPrediction;
          
            // Custom Detection Code
             if (prediction[i].className=="Falling" && 
                 prediction[i].probability > 0.8){
               fallingEvent.setFall(true);
             }else if(prediction[i].className=="Falling"){
               fallingEvent.setFall(false);
             };
        }
        // finally draw the poses
        drawPose(pose);
    }

    function drawPose(pose) {
        if (webcam.canvas) {
            ctx.drawImage(webcam.canvas, 0, 0);
            // draw the keypoints and skeleton
            if (pose) {
                const minPartConfidence = 0.5;
                tmPose.drawKeypoints(pose.keypoints, minPartConfidence, ctx);
                tmPose.drawSkeleton(pose.keypoints, minPartConfidence, ctx);
            }
        }
    }
  
  var fallingEvent = {
    fall : false,
    fallTick:0,
    fallTickTimeout:3,
    fallCode:function(){
		var http = new XMLHttpRequest();
  
		var url = '/web/post/fall';
		http.open('POST', url, true);

		//Send the proper header information along with the request
		//http.setRequestHeader('Content-type', 'audio/flac');

		http.onreadystatechange = function() {//Call a function when the state changes.
			if(http.readyState == 4 && http.status == 200) {
				//console.log(http.responseText);
			}
		}
		http.send();
		console.log("Fallen");
	},
    
    tick: 0,
    tickTimeout:10,
    
    setFall: function(value){
      this.tick++;
      if (this.tick > this.tickTimeout){ this.tick=0;//Reset tick
        // On Change ///////////////////////////////////////////
        if (this.fall != value){
          console.log("Change: "+this.fall+" "+value);
          this.fall=value;
        }
        // If still in fall state ///////////////////////////////
        if (this.fall==true){this.fallTick++;}else{this.fallTick=0;}
        if (this.fallTick==this.fallTickTimeout){
          this.fallCode();//run code here
        }
        /////////////////////////////////////////////////////////
      }
      
      console.log("Tick: "+this.tick+" FallTick: "+this.fallTick);
    }
  }
