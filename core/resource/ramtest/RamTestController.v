`default_nettype none
`timescale 1ns / 1ps

module RamTestController(
	pinClock,
	pinReset,
	pinLeds,
	pinWbCycleStrobe,
	pinWbWriteEnable,
	pinWbAddress,
	pinWbWriteData,
	pinWbReadData,
	pinWbAck
);

input pinClock;
input pinReset;
output[7:0] pinLeds;
output pinWbCycleStrobe;
output pinWbWriteEnable;
output[31:0] pinWbAddress;
output[31:0] pinWbWriteData;
input[31:0] pinWbReadData;
input pinWbAck;


wire s32;
wire s26;
reg[7:0] r3;
reg[7:0] r7;
wire s24;
reg[7:0] r9;
wire s35;
wire s19;
reg[7:0] r6;
wire[7:0] mp3;
wire[31:0] s4;
wire[7:0] s15;
wire[7:0] s6;
wire[4:0] s13;
wire s27;
wire[7:0] s10;
wire[31:0] s11;
wire mp2;
wire s34;
reg[31:0] r11;
wire[31:0] s8;
wire mp0;
wire s28;
wire s30;
reg[7:0] r0;
reg[17:0] s0;
wire s16;
wire[31:0] s3;
wire s29;
wire s33;
reg[7:0] r4;
wire s21;
reg r1;
wire s20;
wire s31;
wire s23;
wire[7:0] s2;
reg[7:0] r10;
wire s17;
wire s18;
reg[7:0] r5;
wire[4:0] s5;
wire[31:0] s12;
wire[7:0] s14;
wire[7:0] mp5;
wire[31:0] s7;
wire s25;
wire mp4;
wire s1;
wire[9:0] mp1;
wire[4:0] s9;
wire s22;
reg r2;
reg[7:0] r8;

reg [17:0] mem0 [1023:0];

assign s32 = mp3[1:0] == 2'h0;
assign s26 = mp3[1:0] == 2'h3;
assign s24 = mp3[1:0] == 2'h0;
assign s35 = r2;
assign s19 = mp3[4] & s20;
assign s4 = {r3, r4, r5, r6};
assign s15 = {7'h00, s16};
assign s6 = mp3[5] ? s7[7:0] : s10;
assign s13 = {mp3[1:0], 3'h0};
assign s27 = mp3[5] & s28;
assign s10 = mp3[6] ? s11[7:0] : s14;
assign s11 = s12 >> s13;
assign s34 = ~s35;
assign s8 = {r7, r8, r9, r10};
assign s28 = mp3[1:0] == 2'h2;
assign s30 = mp3[1:0] == 2'h1;
assign s16 = r1;
assign s3 = s4 >> s5;
assign s29 = mp3[5] & s30;
assign s33 = pinWbAck;
assign s21 = mp3[4] & s22;
assign s20 = mp3[1:0] == 2'h2;
assign s31 = mp3[5] & s32;
assign s23 = mp3[4] & s24;
assign s2 = mp3[4] ? s3[7:0] : s6;
assign s17 = mp3[4] & s18;
assign s18 = mp3[1:0] == 2'h3;
assign s5 = {mp3[1:0], 3'h0};
assign s12 = r11;
assign s14 = mp3[7] ? s15 : 8'h00;
assign s7 = s8 >> s9;
assign s25 = mp3[5] & s26;
assign s1 = 1'b0;
assign s9 = {mp3[1:0], 3'h0};
assign s22 = mp3[1:0] == 2'h1;

kcpsm3 m0(
	.read_strobe(mp0),
	.clk(pinClock),
	.address(mp1),
	.instruction(s0),
	.write_strobe(mp2),
	.interrupt(s1),
	.reset(pinReset),
	.port_id(mp3),
	.in_port(s2),
	.interrupt_ack(mp4),
	.out_port(mp5)
);
initial $readmemh("mem0.mif", mem0, 0, 1023);

always @(posedge pinClock) begin
	s0 <= mem0[mp1];
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & mp3[3]) begin
		r0 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & mp3[7]) begin
		r1 <= 1'b1;
		r2 <= mp5[0];
	end else begin
		if (s33) begin
			r1 <= 1'b0;
		end
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s17) begin
		r3 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s19) begin
		r4 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s21) begin
		r5 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s23) begin
		r6 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s25) begin
		r7 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s27) begin
		r8 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s29) begin
		r9 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s31) begin
		r10 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (s33 & s34) begin
		r11 <= pinWbReadData;
	end
end
assign pinLeds = r0;
assign pinWbCycleStrobe = s16;
assign pinWbWriteEnable = s35;
assign pinWbAddress = s4;
assign pinWbWriteData = s8;

endmodule

