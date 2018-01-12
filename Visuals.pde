//
// 
//ISAAC CLARKE - SOUND 10
//
//

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
  float[] locXX;
  float[] locYY;
  float from;
  float to;
  float limitMarks;
  float op;
  
  void setup(){

    background(255);
    
    //set initial elipse locations to 0,0
    locXX = new float[256];
    locYY = new float[256];
    for(int i = 0; i < 256; i++){
      locXX[i] = 0;
      locYY[i] = 0;
    }

  }
  
  void draw(){
    // ellipse color gets mapped from 0 to 255
    from = 0;
    to = 255;
    
    // if band 42 (bell sounds) is loud make background white.
    if(bands[42] > 0.8){
      background(255);
    }

    // slowly increase the amount of marks up to all 256 bands
    if(frame % 3 == 0){
      limitMarks += 2;
      op += 1;
      if(limitMarks > 256){
        limitMarks = 256;
      }
      spacing = width/sqrt(limitMarks);
      starting = spacing/2;
    }
    
    // go black at end
    if(frame >= 780){
      background(0);
    }

    //grid of ellipses, one for each band.
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

          // go to next band with counter
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
  
  void setBand(int i, float v){
    if(i >= 0 && i < numBands){
      bands[i] = v;  
    }else{
      println("bad reference on vis.setBand"); 
    }
  }
  
  void setFrame(int fr){
    frame = fr;
  }
  
}