import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FInal_Project extends PApplet {

final int P_NONE = 0;                                          //Constants used for side differenciation
final int P_TOP = 1;
final int P_LH = 2;
final int P_RH = 3;
final int P_BOT = 4;

int numPs, pSpeed;                                             //Number of Platforms, Platform speed
PlatformList pl;                                               //List of Platforms
int tickCount, tickResets;                                     //Tick count and amount of tick resets
int frameRate;                                                 //Framerate (used in and around framerate())
CoolDude coolDude;                                             //CoolDude
boolean SPACE_KP, A_KP, W_KP, D_KP, S_KP;                      //KeyPressed switches (holding down the key was WAY too fast, had to restrict to framerate this way)
float touchCount;                                              //amount of platform touches

public void setup(){
  
  numPs = 19;                                                  //Really 20, cause 0 counts :P
  pSpeed = 4;                                                  //Platform speed
  pl = new PlatformList(numPs);                                //platformList creates and contains its own Platforms (see Platform and PlatformList)
  
  touchCount = 0;                                              //Counts that start at 0
  tickCount = 0;
  tickResets = 0;
  
  coolDude = new CoolDude();                                   //new default CoolDude
  
                                                //meh
  textSize(11);                                                
  
  frameRate = 60;
  frameRate(frameRate);
  
  tick();                                                      //call tick() to start the first round of platforms
  
}

public void draw(){
  
  background(255);                                             //White background
  
  tickCount++;                                                 //Keeps track of number of seconds that go by
  if(tickCount >= (frameRate * 2)){                            //frameRate = 1 second
    tick();                                                    
    tickCount = 0; 
    tickResets++; 
  }                          
  
  if((tickResets == 3) && (coolDude.isVisible == false)){ coolDude = new CoolDude(300,-16,20,20); }        //Creates new CoolDude after 3 ticks
  
  pl.moveAndDisplayPlatforms();                                //Slide everything to the left, then do calculations...
  coolDude.shiftLeft();
  
  strokeWeight(10);                                            //Keeps track of how many touches per second are happeing
  float avgTouchCount = touchCount / tickResets;               //Depending on the average, display different messages
  String displayMessage = " ";
  if(avgTouchCount >= 0){
    textSize(32);
    fill(0,0,0);
    text(str(avgTouchCount), 250, 50);
  }
  if(avgTouchCount <= 1)                                     { displayMessage = "Gotta stop the bad!";         fill(255,0,0); }
  else if((avgTouchCount >= 1.001f) && (avgTouchCount <= 1.5f)){ displayMessage = "AAAHHHHHAAHHHH!";             fill(210,0,0);}
  else if((avgTouchCount >= 1.501f) && (avgTouchCount <= 2.0f)){ displayMessage = "RRRRAAAAAWWWWRRRR!!!!!";      fill(150,0,0);}
  else if((avgTouchCount >= 2.001f) && (avgTouchCount <= 2.5f)){ displayMessage = "doing ok!";                   fill(150,0,150);}
  else if((avgTouchCount >= 2.501f) && (avgTouchCount <= 2.6f)){ displayMessage = "Just breathing!";             fill(0,0,150);}
  else if((avgTouchCount >= 2.601f) && (avgTouchCount <= 2.7f)){ displayMessage = "I am the calm of the storm!"; fill(0,0,210);}
  else if(avgTouchCount >= 2.701f)                            { displayMessage = "Walking in His ways.";        fill(0,0,255);}
  text(displayMessage, coolDude.x - 15, coolDude.y - 15);
  
  strokeWeight(1);
  
  if(coolDude.isVisible == true){                              //If(dude isVisible)
    coolDude.applyNaturalForces();                             //Slows dude down
    applyKeys();                                               //applys speed to dude in different directions based on key booleans
    resolveCollisions();                                       //calculates collisions between dude and platforms
    coolDude.move();                                            
    coolDude.display();                                        //Takes on the color from the messages above
    if(coolDude.y > displayHeight + coolDude.h){ coolDude.y = -coolDude.h;}    //Wraps the dude to the top if he falls through the bottom.
  }
}

public void tick(){                                                    //Happens every 2 seconds
  
  int[] spawnLocation = new int[4];                             //defines spawn locations for platforms
  int section = displayHeight / 4;                              //Even spacing based on the screen
  
  for(int i = 0; i < spawnLocation.length; i++){                //for(all spawnlocations)
    
    if(i == 3){ spawnLocation[i] = 1; }                         //Always want the bottom platform to spawn
    else{ spawnLocation[i] = PApplet.parseInt(random(2)); }                  //Spawn using random int
    
    if(spawnLocation[i] == 1){                                  //1 = spawn, 0 = no spawn
      switch(i){
        case 0: pl.spawnPlatform(section * 0 + 20, PApplet.parseInt(random(45)), PApplet.parseInt(random(45)), PApplet.parseInt(random(350, 450)), PApplet.parseInt(random(35,150)), pSpeed); break;
        case 1: pl.spawnPlatform(section * 1 + 20, PApplet.parseInt(random(45)), PApplet.parseInt(random(45)), PApplet.parseInt(random(350, 450)), PApplet.parseInt(random(35,150)), pSpeed); break;
        case 2: pl.spawnPlatform(section * 2 + 20, PApplet.parseInt(random(45)), PApplet.parseInt(random(45)), PApplet.parseInt(random(350, 450)), PApplet.parseInt(random(35,150)), pSpeed); break;
        case 3: pl.spawnPlatform(section * 3 + 20, PApplet.parseInt(random(45)), PApplet.parseInt(random(45)), PApplet.parseInt(random(350, 450)), PApplet.parseInt(random(35,150)), pSpeed); break;
        default: println("DEFAULT::tick()::switch");
      }
    }
  }
}

public int checkCollisions(CoolDude d, Platform p){                     //ONLY checks, does nothing but return which side the dude will hit
    
  if((d.y + d.h <= p.y) && (d.y + d.h + d.ySpeed >= p.y) && (d.x + d.w >= p.x) && (d.x <= p.x + p.w)){ return P_TOP; }
  if((d.x + d.w <= p.x) && (d.x + d.w + d.xSpeed >= p.x) && (d.y + d.h >= p.y) && (d.y <= p.y + p.h)){ return P_LH; }
  if((d.x >= p.x + p.w) && (d.x + d.xSpeed <= p.x + p.w) && (d.y + d.h >= p.y) && (d.y <= p.y + p.h)){ return P_RH; }
  if((d.y >= p.y + p.h) && (d.y + d.ySpeed <= p.y + p.h) && (d.x + d.w >= p.x) && (d.x <= p.x + p.w)){ return P_BOT; }
  return P_NONE;
      
}
  
public void resolveCollisions(){                                                                        //Figures out what to do with collisions and certain conditions

  if(coolDude.pIndex == 500){                                                                    //if(coolDude does not have platform index){ find one }
    for(int i = 0; i < pl.p.length; i++){                                                        //for(each platform)
      int platformSide = checkCollisions(coolDude, pl.p[i]);                                     //check for collsions with the dude
      if(!(platformSide == P_NONE)){                                                             //if(there is a collision with this platform)
        coolDude.pIndex = i;                                                                     //store platform index with dude
        pl.p[i].touched = true;                                                                  //tell the platfrom it has been touched
        touchCount++;                                                                            //add to touch count
      }
    }
  }
    
  if(!(coolDude.pIndex == 500)){                                                                 //if(coolDude has an index of a platform)
    int platformSide = checkCollisions(coolDude, pl.p[coolDude.pIndex]);                         //check for collisions with the platform
    switch(platformSide){                                                                        //depending on the side of the platform being collided with...
        
      case P_NONE:  
        coolDude.pLoc = P_NONE; 
        coolDude.canJump = false;
        coolDude.pIndex = 500;                                                                   //Since not colliding with platform, set index to none(500), can't jump cause probably in the air somewhere.
        break;
          
      case P_TOP:   
        coolDude.pLoc = P_TOP;
        coolDude.canJump = true;                                                                 //Tells dude he is able to jump
        coolDude.ySpeed = pl.p[coolDude.pIndex].y - (coolDude.y + coolDude.h);                   //Sets the speed of dude to the distance between edges.
        break;
          
      case P_LH:    
        coolDude.pLoc = P_LH;
        coolDude.canJump = true;
        coolDude.xSpeed = pl.p[coolDude.pIndex].x - (coolDude.x + coolDude.w);
        coolDude.ySpeed = 0;                                                                     //Stops the tangent axis movement to simulate a "stick" to the platforms
        break; 
          
      case P_RH:    
        coolDude.pLoc = P_RH; 
        coolDude.canJump = true;
        coolDude.xSpeed = (pl.p[coolDude.pIndex].x + pl.p[coolDude.pIndex].w) - coolDude.x;
        coolDude.ySpeed = 0;
        break;
        
      case P_BOT:   
        coolDude.pLoc = P_BOT;
        coolDude.canJump = true; 
        coolDude.ySpeed = (pl.p[coolDude.pIndex].y + pl.p[coolDude.pIndex].h) - coolDude.y;
        coolDude.xSpeed = 0;
        break;
      default: println("DEFAULT::FINAL_PROJECT::COLLISIONS::!COOLDUDE==500::SOMETHING DEFENITLY WENT WRONG"); break;
    }
  }
}

public void applyKeys(){                                                                                 //does what the keys are reflecting on the keyboard
  if((SPACE_KP == true) && (coolDude.canJump)){                                                   //Space and jump are tied together
      coolDude.canJump = false;                                                                   //Once dude jumps... NO DOUBLE JUMPING!!!
      if((coolDude.pLoc == P_TOP) && (W_KP == true)){ coolDude.ySpeed -= 25; }                    //Hidden big jump mechanic
      else if(coolDude.pLoc == P_TOP){ coolDude.ySpeed = -15; }                                   //normal jump
      
      if(coolDude.pLoc == P_LH){ coolDude.ySpeed -= 15; coolDude.xSpeed -= 15; }                  //able to jump off sides of 
      if(coolDude.pLoc == P_RH){ coolDude.ySpeed -= 15; coolDude.xSpeed += 15; }
      if(coolDude.pLoc == P_BOT){ coolDude.ySpeed += 2; }                                         
  }
  if(D_KP == true){ 
    if((coolDude.pLoc == P_NONE) || (coolDude.pLoc == P_TOP)){coolDude.xSpeed += 2; }
    if((coolDude.pLoc == P_BOT) || (coolDude.pLoc == P_RH)){coolDude.x += 2; }                    //NOTICE!!! this directly affects location rather than speed on axis!
  }
  if(A_KP == true){ 
    if((coolDude.pLoc == P_NONE) || (coolDude.pLoc == P_TOP)){coolDude.xSpeed -= 2; }
    if((coolDude.pLoc == P_BOT) || (coolDude.pLoc == P_LH)){ coolDude.x -= 2; }                   //This is so the dude moves slower while climbing on sides and bottom of platforms
  }
  if(W_KP == true){ 
    if((coolDude.pLoc == P_LH) || (coolDude.pLoc == P_RH)){ coolDude.y -= 2; }
  }
  if(S_KP == true){ if((coolDude.pLoc == P_LH) || (coolDude.pLoc == P_RH) || (coolDude.pLoc == P_BOT)){ coolDude.y += 2; }}
}

public void keyPressed(){                                                                                //Freaking keypressed events dont like to be held down   
  if(key == ' '){ SPACE_KP = true; }                                                              //and have akward "gap" between first press and actual hold
  if(key == 'd'){ D_KP = true; }                                                                  //To simulate smooth hold functionality, tied each input to boolean
  if(key == 'a'){ A_KP = true; }                                                                  //values that are only on or off.
  if(key == 'w'){ W_KP = true; }
  if(key == 's'){ S_KP = true; }
}

public void keyReleased(){
  if(key == ' '){ SPACE_KP = false; }
  if(key == 'd'){ D_KP = false; }
  if(key == 'a'){ A_KP = false; }
  if(key == 'w'){ W_KP = false; }
  if(key == 's'){ S_KP = false; }
}
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
  
  public void applyNaturalForces(){                                             //Slows dude down in all directions
    if((pLoc == P_NONE)){ ySpeed += .5f; }                                //represents Gravity 
    if(xSpeed > 0){                                                      //Horizontal slow down to 0 in both directions
      xSpeed -= 1.5f; 
      if(xSpeed < 0){ xSpeed = 0; }  
    }
    if(xSpeed < 0){ 
      xSpeed += 1.5f;
      if(xSpeed > 0){ xSpeed = 0; }
    }
  }
  
  public void shiftLeft(){ x -= pSpeed; }                                       //Moves dude to the left with Platforms
  
  public void move(){                                                            
    y += ySpeed;
    x += xSpeed;
    if(ySpeed > 17){ ySpeed = 17; }                                      //keeps dude within Max values so he doesnt go too fast
    if(xSpeed > 7){ xSpeed = 7; }
    if(xSpeed < -7){ xSpeed = -7; }
  }
  
  public void display(){ rect(x,y,w,h); }                                       //Yep, a square.  It does take on the fill color of the text displayed above the square in game (done on purpose).
}
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
  
  public void moveAndDisplay(){                                                           //keeps track of if it has been touched by dude to change the color
    if(touched == false){ fill(255,0,0); }
    else{ fill(0,255,0); }
    x -= xSpeed; 
    rect(x,y,w,h); 
  }                                              
}
class PlatformList{                                                                 //Lists of objects are better for handling groups of them!
  int index;
  int arraySize;                                                                    //equivelant to .length
  Platform[] p;
  
  PlatformList(int size){
    index = 0;
    arraySize = size;
    p = new Platform[size];
    for(int i = 0; i < arraySize; i++){ p[i] = new Platform(); }                    //Fills Array with dummy Platforms
  }
  
  public void spawnPlatform(int y, int yOffset, int xOffset, int w, int h, int s){         //Creates new Platform within array and handles index usage automatically
    p[index] = new Platform(y,yOffset,xOffset,w,h,s);
    index++;
    if(index >= arraySize){ index = 0; }
  }

  public void moveAndDisplayPlatforms(){ for(int i = 0; i < arraySize; i++){  p[i].moveAndDisplay(); }}  //moves platforms

}
  public void settings() {  size(1500,900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FInal_Project" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
