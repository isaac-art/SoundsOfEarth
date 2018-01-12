
    //////////////////////////////////
   // VISUALS OF EARTH //////////////
  // Lior Ben-Gai, Novenber 2016 ///
 // soogbet.net/voe ///////////////
//////////////////////////////////

/*
THE RULES:

1. SIZE = 256 BY 256
2. NO USE OF EXTERNAL FILES EXCEPT THE SOUND. 
3. USE ONLY GREYSCALE COLOUR (0 - 255)
4. NO USER INTERACTION 

*/


// Define how many FFT bands we want
int bands = 256;

// Create a smoothing factor
float smooth_factor = 0.8;

// Declare a scaling factor
float scale=0.5;




  ///////////////////////////////////////////
 // NO NEED TO TOUCH THIS CODE  ////////////
///////////////////////////////////////////


// Import Minim Library
import ddf.minim.analysis.*;
import ddf.minim.*;
Minim minim;
AudioPlayer sample;
FFT fft;

// Create a smoothed values array
float[] sum;
int frame = 0;
// create a class to draw your Visuals
Visuals vis;

// fonts
PFont fL;
PFont fS;

// flag for showing debug data
boolean debug = false;

void setup() {
  //256
  size(256, 256);
  
  minim = new Minim(this);
  sample = minim.loadFile("SOE.mp3", 1024);
  sample.loop();

  // Create and patch the FFT analyzer
  fft = new FFT( sample.bufferSize(), sample.sampleRate() );
  
  // calculate the averages by grouping frequency bands linearly. use 30 averages.
  fft.linAverages( bands );
  
  fL = createFont("SourceCodePro-Regular.ttf", 16);
  fS = createFont("SourceCodePro-Regular.ttf", 12);
  textAlign(LEFT, BOTTOM);
  
  sum = new float[bands];
   
  // Setup the visuals class
  vis = new Visuals(fft.specSize());
  vis.setup();
}      

void draw() {
    // 1. get the FFT and push it to the visualizer
    fft.forward(sample.mix);
    for (int i = 0; i < bands; i++) {
      // smooth the FFT data by smoothing factor
      sum[i] += (fft.getAvg(i) - sum[i]) * smooth_factor;
      // use smoothed, scaled values
      vis.setBand(i,sum[i]*scale); 
      // use raw values
      //vis.setBand(i,fft.getAvg(i)); 
    }
    
    // 2. DRAW VISUALS
    vis.draw();
    
    
    // 3. DRAW DEBUG
    if(debug){
      pushStyle();
      noStroke();
      fill(127,220);
      rect(0,0,width,height);
      stroke(255,0,0);
      float scl = 128;
      int c = 0;
      
      // draw the raw sound samples (one every four samples)
      for(int i = 0; i < sample.bufferSize() - 1; i+=4){
        point(c, 82  + sample.left.get(i)*scl);
        point(c, 164 + sample.right.get(i)*scl);
        c++;
      }
      // draw the smoothed and scaled FFT 
      stroke(0,200,0);
      for (int i = 0; i < bands; i++) {
        // draw the bands with a scale factor
          line(i,height, i, height -sum[i]*height*scale);        
      }
      
      stroke(0);
      line(mouseX,mouseY-48,mouseX,height);
      noStroke();
      fill(255);
      rect(mouseX-1,height, 2, -sum[mouseX]*height*scale);
      
      fill(0);
      rect(mouseX,mouseY-38, 48, 32);
      fill(255);
      textFont(fL);
      text(mouseX, mouseX + 8, mouseY - 20);
      textFont(fS);
      text(sum[mouseX]*scale, mouseX, mouseY - 6);
      
      fill(0);
      rect(0,0,width,8);
      float pos = (1.0*sample.position()/sample.length()) *width; 
      stroke(255);
      line(pos,0,pos,8);
      fill(255);
      text(frame, 4, 12);
      popStyle();
    }
    
    if(sample.isPlaying()){
      vis.setFrame(frame++);  
    }
    if(sample.position() < 60){
      frame = 0;
    }
   
}

void keyPressed(){
  // 'D' for debug mode
  if(key == 'd'){
    debug = !debug;
  }
  // SPACE to pause/play the sound
  if(key == ' '){
     if(sample.isPlaying()){
      sample.pause();
    }else{
      sample.loop();
    }
  }
  // 'R' to rewind
  if(key == 'r'){
     sample.rewind();   
  }
  
}