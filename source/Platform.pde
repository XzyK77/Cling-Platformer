class Platform{                                                                    //Platform!
  int x, y, w, h, xSpeed;
  boolean touched;
  
  Platform(){ this(0,0,0,0,0,0); }                                                 //Default Constructor
  
  Platform(int yLoc, int wSize, int hSize, int speed){                             //Secondary Constructor for main class use
    x = displayWidth;
    y = yLoc;
    w = wSize;
    h = hSize;
    xSpeed = speed;
    touched = false;
  }
  
  Platform(int yLoc, int yOffset, int xOffset, int wSize, int hSize, int speed){   //Used for initialization.
    x = displayWidth + xOffset;
    y = yLoc + yOffset;
    w = wSize;
    h = hSize;
    xSpeed = speed;
    touched = false;
  }
  
  void moveAndDisplay(){                                                           //keeps track of if it has been touched by dude to change the color
    if(touched == false){ fill(255,0,0); }
    else{ fill(0,255,0); }
    x -= xSpeed; 
    rect(x,y,w,h); 
  }                                              
}
