
void setup() {
  Serial.begin(115200);
  Serial.println("Hello world!");
  pinMode(4, INPUT_PULLUP);
  pinMode(8, OUTPUT);
  digitalWrite(8, 1);
  delay(1000);
}

void sendByte(uint8_t value) {
  noInterrupts();
  digitalWrite(8, 0); // start
  digitalWrite(8, value & 1); // bit 0
  value = value >> 1;
  digitalWrite(8, value & 1); // bit 1
  value = value >> 1;
  digitalWrite(8, value & 1); // bit 2
  value = value >> 1;
  digitalWrite(8, value & 1); // bit 3
  value = value >> 1;
  digitalWrite(8, value & 1); // bit 4
  value = value >> 1;
  digitalWrite(8, value & 1); // bit 5
  value = value >> 1;
  digitalWrite(8, value & 1); // bit 6
  value = value >> 1;
  digitalWrite(8, value & 1); // bit 7
  digitalWrite(8, 1); // stop -- might be able to take some time off this
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  interrupts();
}

void loop() {
  int dataByte = Serial.read();
  if (dataByte < 0) {
    return;
  }
  /*
  Serial.print("you sent: ");
  Serial.print(dataByte);
  Serial.print(" / ");
  Serial.print((char)dataByte);
  Serial.print("\n");
  */
  sendByte((uint8_t)dataByte);
}

