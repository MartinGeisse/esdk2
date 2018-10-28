`default_nettype none
`timescale 1ns / 1ps

module RamTest(
	clockIn,
	resetIn,
	sdram_ck_p,
	sdram_ck_n,
	sdram_cke,
	sdram_cs_n,
	sdram_ras_n,
	sdram_cas_n,
	sdram_we_n,
	sdram_ba,
	sdram_a,
	sdram_udm,
	sdram_ldm,
	sdram_udqs,
	sdram_ldqs,
	sdram_dq,
	led0,
	led1,
	led2,
	led3,
	led4,
	led5,
	led6,
	led7
);

// clock / reset
input clockIn;
input resetIn;

// SDRAM
output sdram_ck_p;
output sdram_ck_n;
output sdram_cke;
output sdram_cs_n;
output sdram_ras_n;
output sdram_cas_n;
output sdram_we_n;
output [1:0] sdram_ba;
output [12:0] sdram_a;
output sdram_udm;
output sdram_ldm;
inout sdram_udqs;
inout sdram_ldqs;
inout [15:0] sdram_dq;

// LEDs
output led0;
output led1;
output led2;
output led3;
output led4;
output led5;
output led6;
output led7;

// clock / reset
wire clock, clock90, clock180, clock270, reset, clockOk;
clk_reset clk_reset1(
	.clk_in(clockIn),
	.reset_in(resetIn),
	.ddr_clk_0(clock),
	.ddr_clk_90(clock90),
	.ddr_clk_180(clock180),
	.ddr_clk_270(clock270),
	.ddr_clk_ok(clockOk),
	.reset(reset)
);

// SDRAM
wire wbCycleStrobe;
wire wbWriteEnable;
wire[31:0] wbAddress;
wire[31:0] wbWriteData;
wire[31:0] wbReadData;
wire wbAck;
ddr_sdram ddr_sdram1(

	// internal system signals
	.clk0(clock),
	.clk90(clock90),
	.clk180(clock180),
	.clk270(clock270),
	.reset(~clockOk),

	// internal Wishbone interface
	.wADR_I(wbAddress[25:2]),
	.wSTB_I(wbCycleStrobe),
	.wWE_I(wbWriteEnable),
	.wWRB_I(4'b1111),
	.wDAT_I(wbWriteData),
	.wDAT_O(wbReadData),
	.wACK_O(wbAck),

	// SDRAM signals
	.sd_CK_P(sdram_ck_p),
	.sd_CK_N(sdram_ck_n),
	.sd_CKE_O(sdram_cke),
	.sd_CS_O(sdram_cs_n),
	.sd_RAS_O(sdram_ras_n),
	.sd_CAS_O(sdram_cas_n),
	.sd_WE_O(sdram_we_n),
    .sd_A_O(sdram_a[12:0]),
    .sd_BA_O(sdram_ba[1:0]),
    .sd_D_IO(sdram_dq[15:0]),
    .sd_UDM_O(sdram_udm),
    .sd_LDM_O(sdram_ldm),
    .sd_UDQS_IO(sdram_udqs),
    .sd_LDQS_IO(sdram_ldqs)

);

// LEDs
wire[7:0] leds;
assign led0 = leds[0];
assign led1 = leds[1];
assign led2 = leds[2];
assign led3 = leds[3];
assign led4 = leds[4];
assign led5 = leds[5];
assign led6 = leds[6];
assign led7 = leds[7];

// controller
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

endmodule
