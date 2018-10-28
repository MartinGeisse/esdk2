`default_nettype none
`timescale 1ns / 1ps

module RamTest(
	clockIn,
	resetIn,
	led0,
	led1,
	led2,
	led3,
	led4,
	led5,
	led6,
	led7
);

input clockIn;
input resetIn;
output led0;
output led1;
output led2;
output led3;
output led4;
output led5;
output led6;
output led7;

wire clock, clock90, clock180, clock270, reset, clockOk;

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
    .pinReset(reset),
    .pinLeds(leds),
    .pinWbCycleStrobe(wbCycleStrobe),
    .pinWbWriteEnable(wbWriteEnable),
    .pinWbAddress(wbAddress),
    .pinWbWriteData(wbWriteData),
    .pinWbReadData(wbReadData),
    .pinWbAck(wbAck)
);


// clock / reset
clk_reset clk_reset1(
	.clk_in(clockIn),
	.reset_in(resetIn),
	.ddr_clk_0(clock),
	.ddr_clk_90(clock90),
	.ddr_clk_180(clock180),
	.ddr_clk_270(clock270),
	.ddr_clk_ok(clockOk),
	// .clk(clk), TODO remove
	.reset(reset)
);

// test
reg ackReg;
initial ackReg = 0;
always @(posedge clock) begin
	if (reset) begin
		ackReg <= 0;
	end else if (wbCycleStrobe) begin
        ackReg <= ~ackReg;
    end
end
assign wbAck = ackReg;
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

