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
  
  void spawnPlatform(int y, int yOffset, int xOffset, int w, int h, int s){         //Creates new Platform within array and handles index usage automatically
    p[index] = new Platform(y,yOffset,xOffset,w,h,s);
    index++;
    if(index >= arraySize){ index = 0; }
  }

  void moveAndDisplayPlatforms(){ for(int i = 0; i < arraySize; i++){  p[i].moveAndDisplay(); }}  //moves platforms

}
