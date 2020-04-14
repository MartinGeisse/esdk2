
void setup() {
  pinMode(4, INPUT_PULLUP);
  pinMode(8, OUTPUT);
  digitalWrite(8, 1);
  noInterrupts();
}

void sendByte(uint8_t value) {
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
  digitalWrite(8, 1); // stop
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  digitalWrite(8, 1);
  digitalWrite(8, 1);
//   delay(1);
}

void loop() {
  while (digitalRead(4)) {
  }
  delay(100);
  if (digitalRead(4)) {
    return;
  }

  sendByte((uint8_t)0x6f);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x80);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x6f);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x02);
  sendByte((uint8_t)0x37);
  sendByte((uint8_t)0x04);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x80);
  sendByte((uint8_t)0x93);
  sendByte((uint8_t)0x04);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);

  sendByte((uint8_t)0x23);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x94);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x93);
  sendByte((uint8_t)0x84);
  sendByte((uint8_t)0x14);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x05);
  sendByte((uint8_t)0xc0);
  sendByte((uint8_t)0x12);
  sendByte((uint8_t)0xef);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0xc0);
  sendByte((uint8_t)0x00);

  sendByte((uint8_t)0x6f);
  sendByte((uint8_t)0xf0);
  sendByte((uint8_t)0x1f);
  sendByte((uint8_t)0xff);
  sendByte((uint8_t)0x6f);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x63);
  sendByte((uint8_t)0x0a);
  sendByte((uint8_t)0x05);
  sendByte((uint8_t)0x02);
  sendByte((uint8_t)0x93);
  sendByte((uint8_t)0x02);
  sendByte((uint8_t)0x80);
  sendByte((uint8_t)0x3e);

  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);

  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x93);
  sendByte((uint8_t)0x82);
  sendByte((uint8_t)0xf2);
  sendByte((uint8_t)0xff);

  sendByte((uint8_t)0xe3);
  sendByte((uint8_t)0x90);
  sendByte((uint8_t)0x02);
  sendByte((uint8_t)0xfe);
  sendByte((uint8_t)0x13);
  sendByte((uint8_t)0x05);
  sendByte((uint8_t)0xf5);
  sendByte((uint8_t)0xff);
  sendByte((uint8_t)0x6f);
  sendByte((uint8_t)0xf0);
  sendByte((uint8_t)0x1f);
  sendByte((uint8_t)0xfd);
  sendByte((uint8_t)0x67);
  sendByte((uint8_t)0x80);
  sendByte((uint8_t)0x00);
  sendByte((uint8_t)0x00);

  while (!digitalRead(4)) {
  }
  delay(100);
}

