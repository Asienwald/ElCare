//http://github.com/cwilso/volume-meter/
// Volume Audio Process ////////////////////////////////////////////////
function createAudioMeter(audioContext,clipLevel,averaging,clipLag) {
	var processor = audioContext.createScriptProcessor(512);
	processor.onaudioprocess = volumeAudioProcess;
	processor.clipping = false;
	processor.lastClip = 0;
	processor.volume = 0;
	processor.clipLevel = clipLevel || 0.98;
	processor.averaging = averaging || 0.95;
	processor.clipLag = clipLag || 750;

	// this will have no effect, since we don't copy the input to the output,
	// but works around a current Chrome bug.
	processor.connect(audioContext.destination);

	processor.checkClipping =
		function(){
			if (!this.clipping)
				return false;
			if ((this.lastClip + this.clipLag) < window.performance.now())
				this.clipping = false;
			return this.clipping;
		};

	processor.shutdown =
		function(){
			this.disconnect();
			this.onaudioprocess = null;
		};

	return processor;
}
function volumeAudioProcess( event ) {
	var buf = event.inputBuffer.getChannelData(0);
    var bufLength = buf.length;
	var sum = 0;
    var x;

	// Do a root-mean-square on the samples: sum up the squares...
    for (var i=0; i<bufLength; i++) {
    	x = buf[i];
    	if (Math.abs(x)>=this.clipLevel) {
    		this.clipping = true;
    		this.lastClip = window.performance.now();
    	}
    	sum += x * x;
    }

    // ... then take the square root of the sum.
    var rms =  Math.sqrt(sum / bufLength);

    // Now smooth this out with the averaging factor applied
    // to the previous sample - take the max here because we
    // want "fast attack, slow release."
    this.volume = Math.max(rms, this.volume*this.averaging);
}
////////////////////////////////////////////////////////////////////////
var audioContext = null;
var meter = null;
var canvasContext = null;
var WIDTH=400;
var HEIGHT=10;
var rafID = null;

function startAudio() {

    // grab our canvas
  document.getElementById( "meter" ).width=WIDTH;
  document.getElementById( "meter" ).height=HEIGHT;
	canvasContext = document.getElementById( "meter" ).getContext("2d");
	
    // monkeypatch Web Audio
    window.AudioContext = window.AudioContext || window.webkitAudioContext;
	
    // grab an audio context
    audioContext = new AudioContext();

    // Attempt to get audio input
    try {
        // monkeypatch getUserMedia
        navigator.getUserMedia = 
        	navigator.getUserMedia ||
        	navigator.webkitGetUserMedia ||
        	navigator.mozGetUserMedia ||
			navigator.msGetUserMedia;

        // ask for an audio input
        navigator.getUserMedia(
        {
            "audio": {
                "mandatory": {
                    "googEchoCancellation": "false",
                    "googAutoGainControl": "false",
                    "googNoiseSuppression": "false",
                    "googHighpassFilter": "false"
                },
                "optional": []
            },
        }, gotStream, didntGetStream);
    } catch (e) {
        alert('getUserMedia threw exception :' + e);
    }

}
function didntGetStream() {
    alert('Stream generation failed.');
}

var mediaStreamSource;
var soundTick=0;function gotStream(stream) {
    // Create an AudioNode from the stream.
    mediaStreamSource = audioContext.createMediaStreamSource(stream);

    // Create a new volume meter and connect it.
    meter = createAudioMeter(audioContext);
    mediaStreamSource.connect(meter);
    
    rec = new Recorder(mediaStreamSource);
    
    // kick off the visual updating
    drawLoop();
}

function drawLoop( time ) {
    // clear the background
    canvasContext.clearRect(0,0,WIDTH,HEIGHT);

    // check if we're currently clipping
    if (soundTriggerCondition(meter))
        canvasContext.fillStyle = "red";
    else
        canvasContext.fillStyle = "green";

    // draw a bar based on the current volume
    canvasContext.fillRect(0, 0, meter.volume*WIDTH*1.4, HEIGHT);
    
    // Fall Sound Alert
    if (soundTriggerCondition(meter)){
      soundTick++;
      if (soundTick==3){loudSound();}
    }else{soundTick=0;}
	
    //console.log("soundTick: "+soundTick);
    
  
    // set up the next visual callback
    rafID = window.requestAnimationFrame( drawLoop );
}
//////////////////////////////////////////////////////////

function soundTriggerCondition(meter){
	return meter.checkClipping();
 
}


var currentlyRecording = false;
var rec;
function loudSound(){
  //alert("noise");
  if (!currentlyRecording){
	rec.clear();rec.record();
	setTimeout(stopRecording, 10000);
	currentlyRecording=true;
  }
}
function stopRecording(){
	//mediaStreamSource.getTracks().forEach(t => t.stop());
	rec.stop();
	rec.exportWAV((blob) => {
	  audioRecordings.addSample(blob);
	  sendAudioData(blob);
	  audioRecordings.showSamples(document.getElementById("recordings"));
	});
	currentlyRecording=false;
}

//https://stackoverflow.com/questions/43903963/post-html5-audio-data-to-server
audioRecordings = {
  samples:[],
  addSample: function(blob){
    this.samples.push({
      blob,
      date:Date()
    });
  },
  showSamples: function(mainElement){
    mainElement.innerHTML = "";
    for (index in this.samples.reverse()){
      let blob=this.samples[index].blob;
      const url = window.URL.createObjectURL(blob);
      let group = document.createElement('li');
      
      let dateHeading = document.createElement('span');
      dateHeading.innerHTML = this.samples[index].date+":<br>";
      group.appendChild(dateHeading);
      
      let au = new Audio(url);
      au.controls = true;
      group.appendChild(au);
      
      //au.play();
      /*
      let a = document.createElement('a');
      a.href = url;
      a.innerHTML = 'Download';
      a.download = 'filename.wav';
      group.appendChild(a);
      */
      mainElement.appendChild(group); 
	  mainElement.innerHTML += "<br>";
    }
  }
}

function sendAudioData(blob){
  var http = new XMLHttpRequest();
  
  var url = '/web/post/sound';
  http.open('POST', url, true);

  var fd=new FormData();
  fd.append("audio_data",blob, "recorded.wav");
      
      
  //Send the proper header information along with the request
  //http.setRequestHeader('Content-type', 'audio/flac');

  http.onreadystatechange = function() {//Call a function when the state changes.
      if(http.readyState == 4 && http.status == 200) {
          //alert(http.responseText);
      }
  }
  http.send(fd);
}
