class CoolDude{                                                         //The coolest dude!
  int x, y, w, h, pLoc, pIndex;
  float ySpeed, xSpeed;
  boolean isVisible, canJump;
  
  CoolDude(){ this(0,0,0,0,0,0,false); }                                //Default Constructor. feeds into next Constructor and plugs in default values
  
  CoolDude(int xLoc, int yLoc, int wSize, int hSize){ this(xLoc, yLoc, wSize, hSize, 0,0,true); }  //Partial Constructor: used for first spawning in dude
  
  CoolDude(int xLoc, int yLoc, int wSize, int hSize, int speedX, int speedY, boolean visibility){  //Used for EVERYTHING
    x = xLoc;
    y = yLoc;
    w = wSize;
    h = hSize;
    xSpeed = speedX;
    ySpeed = speedY;
    isVisible = visibility;
    canJump = false;
    pLoc = 0;                                                            //Used to reference the side of the platform being collided with.
    pIndex = 500;                                                        //500 is used bacause 0 refers to a platform within the array :(... so its just a placeholder number.
  }
  
  void applyNaturalForces(){                                             //Slows dude down in all directions
    if((pLoc == P_NONE)){ ySpeed += .5; }                                //represents Gravity 
    if(xSpeed > 0){                                                      //Horizontal slow down to 0 in both directions
      xSpeed -= 1.5; 
      if(xSpeed < 0){ xSpeed = 0; }  
    }
    if(xSpeed < 0){ 
      xSpeed += 1.5;
      if(xSpeed > 0){ xSpeed = 0; }
    }
  }
  
  void shiftLeft(){ x -= pSpeed; }                                       //Moves dude to the left with Platforms
  
  void move(){                                                            
    y += ySpeed;
    x += xSpeed;
    if(ySpeed > 17){ ySpeed = 17; }                                      //keeps dude within Max values so he doesnt go too fast
    if(xSpeed > 7){ xSpeed = 7; }
    if(xSpeed < -7){ xSpeed = -7; }
  }
  
  void display(){ rect(x,y,w,h); }                                       //Yep, a square.  It does take on the fill color of the text displayed above the square in game (done on purpose).
}
