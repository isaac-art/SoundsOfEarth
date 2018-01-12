import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.analysis.*; 
import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SoundsOfEarth extends PApplet {


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
float smooth_factor = 0.8f;

// Declare a scaling factor
float scale=0.5f;




  ///////////////////////////////////////////
 // NO NEED TO TOUCH THIS CODE  ////////////
///////////////////////////////////////////


// Import Minim Library


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

public void setup() {
  
  
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

public void draw() {
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
      float pos = (1.0f*sample.position()/sample.length()) *width; 
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

public void keyPressed(){
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
class Visuals{
  // this is where the sound spectrum data is stored
  float[] bands;
  // how many values are in the array
  int numBands;
  // frame counter (resets when sound restarts)
  int frame;
  
  // Define your global variables here:
  float spacing;
  float starting;
  float locX;
  float locY;
  int k = 0;
  float rotation = PI/10;
  float[] locXX;
  float[] locYY;
  float from;
  float to;
  float limitMarks;
  float op;
  
  public void setup(){

    background(255);
    

    locXX = new float[257];
    locYY = new float[257];
    for(int i = 0; i < 257; i++){
      locXX[i] = 0;
      locYY[i] = 0;
    }

  }
  
  public void draw(){
    
    from = 0;
    to = 255;
    
    if(bands[42] > 0.8f){
      background(255);
      
    }

    if(frame % 3 == 0){
      limitMarks += 2;
      op += 1;
      if(limitMarks > 256){
        limitMarks = 256;
      }
      spacing = width/sqrt(limitMarks);
      starting = spacing/2;
    }

    if(frame >= 780){
      background(0);
    }


    for(int i = 0; i < sqrt(limitMarks); i++){

      for(int j = 0; j < sqrt(limitMarks); j++){
          //starting positions in a grid
          locX = j * spacing + starting;
          locY = i * spacing + starting;

          fill(map(bands[k],0,1,from,to), op);
          
          pushMatrix();
          translate(locX, locY);

          ellipse(0, 0, bands[k]*8, bands[k]*8);
          popMatrix();

          if(k > 255){
            k = 0;
          }else{
            k += 1;
          }

      }

    }
    
    
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  ///////////////////////////////////////////
 // NO NEED TO TOUCH THIS CODE  ////////////
///////////////////////////////////////////

  Visuals(int _numBands){
    numBands = _numBands;
    bands = new float[numBands];
  }
  
  public void setBand(int i, float v){
    if(i >= 0 && i < numBands){
      bands[i] = v;  
    }else{
      println("bad reference on vis.setBand"); 
    }
  }
  
  public void setFrame(int fr){
    frame = fr;
  }
  
}
  public void settings() {  size(256, 256); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SoundsOfEarth" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
