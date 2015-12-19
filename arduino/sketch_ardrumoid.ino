  const int BTNLEDPIN = 8;

  int val0_old , val1_old, val2_old, val3_old, val4_old;
  int val0;   //Symbal
  int val1;   //Hihat
  int val2;  //Snare
  int val3;  //Tom
  int val4;  //Kick

void setup() {
  Serial.begin(115200);
  pinMode(BTNLEDPIN, OUTPUT);
}

void loop() {
  val0 = analogRead(0); //Connect the sensor to analog pin 0
  val1 = analogRead(1);
  val2 = analogRead(2); 
  val3 = analogRead(3);
  val4 = analogRead(4);

  if (val0 > 50 && val0 > val0_old) {
   Serial.print("0"); 
   }
  val0_old = val0;
  if (val1 > 40 && val1 > val1_old) {
   Serial.print("1"); 
   }
  val1_old = val1;
  if (val2 > 40 && val2 > val2_old) {
   Serial.print("2"); 
   }
  val2_old = val2;

  if (val3 > 40 && val3 > val3_old) {
      Serial.print("3"); 
   }
  val3_old = val3;
    
    if (val4 > 1000 && val4 > val4_old +10) {
      Serial.print("4"); 
   }
  if(val4 > 1020){
    digitalWrite(BTNLEDPIN, LOW);
  }else{
    digitalWrite(BTNLEDPIN, HIGH);
  }
  val4_old = val4;
  delay(13);
}
