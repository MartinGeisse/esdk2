`default_nettype none
`timescale 1ns / 1ps

module RamTest(
	clock,
	led0,
	led1,
	led2,
	led3,
	led4,
	led5,
	led6,
	led7
);

input clock;
output led0;
output led1;
output led2;
output led3;
output led4;
output led5;
output led6;
output led7;

wire[7:0] leds;
assign led0 = leds[0];
assign led1 = leds[1];
assign led2 = leds[2];
assign led3 = leds[3];
assign led4 = leds[4];
assign led5 = leds[5];
assign led6 = leds[6];
assign led7 = leds[7];

wire wbCycleStrobe;
wire wbWriteEnable;
wire[31:0] wbAddress;
wire[31:0] wbWriteData;
wire[31:0] wbReadData;
wire wbAck;

RamTestController ramTestController1(
    .pinClock(clock),
    .pinLeds(leds),
    .pinWbCycleStrobe(wbCycleStrobe),
    .pinWbWriteEnable(wbWriteEnable),
    .pinWbAddress(wbAddress),
    .pinWbWriteData(wbWriteData),
    .pinWbReadData(wbReadData),
    .pinWbAck(wbAck)
);

// test
reg ackReg;
initial ackReg = 0;
always @(posedge clock) begin
    if (wbCycleStrobe) begin
        ackReg <= ~ackReg;
    end
end
reg[31:0] ram[255:0];
reg[31:0] ramReadData;
always @(posedge clock) begin
    if (wbCycleStrobe) begin
        if (wbWriteEnable) begin
            ram[wbAddress[7:0]] <= wbWriteData;
        end
        ramReadData <= ram[wbAddress[7:0]];
    end
end
assign wbReadData = ramReadData;

endmodule

