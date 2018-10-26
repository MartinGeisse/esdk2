`default_nettype none
`timescale 1ns / 1ps

module RamTestController(
	pinClock,
	pinLeds,
	pinWbCycleStrobe,
	pinWbWriteEnable,
	pinWbAddress,
	pinWbWriteData,
	pinWbReadData,
	pinWbAck
);

input pinClock;
output[7:0] pinLeds;
output pinWbCycleStrobe;
output pinWbWriteEnable;
output[31:0] pinWbAddress;
output[31:0] pinWbWriteData;
input[31:0] pinWbReadData;
input pinWbAck;


wire s24;
wire mp4;
wire mp0;
wire s17;
wire s19;
wire s18;
wire s32;
reg[7:0] r6;
wire s35;
reg[7:0] r0;
wire s23;
reg[17:0] s0;
wire mp2;
reg[7:0] r5;
wire s31;
wire[4:0] s14;
wire[31:0] s8;
wire s1;
wire s20;
wire[7:0] mp5;
wire[7:0] s3;
wire[31:0] s9;
wire[7:0] s15;
wire s30;
reg[7:0] r4;
reg r2;
wire[31:0] s4;
wire s33;
wire s26;
reg[7:0] r3;
wire s25;
wire[7:0] s7;
wire[7:0] mp3;
wire s27;
wire s28;
wire[4:0] s10;
wire s2;
wire[7:0] s11;
wire s21;
wire[4:0] s6;
wire[31:0] s12;
wire[9:0] mp1;
reg r1;
reg[31:0] r11;
wire s36;
wire s29;
wire[31:0] s5;
wire s22;
reg[7:0] r7;
wire s34;
wire[7:0] s16;
reg[7:0] r10;
reg[7:0] r9;
wire[31:0] s13;
reg[7:0] r8;

reg [17:0] mem0 [1023:0];

assign s24 = mp3[4] & s25;
assign s17 = r1;
assign s19 = mp3[1:0] == 2'h3;
assign s18 = mp3[4] & s19;
assign s32 = mp3[5] & s33;
assign s35 = ~s36;
assign s23 = mp3[1:0] == 2'h1;
assign s31 = mp3[1:0] == 2'h1;
assign s14 = {mp3[1:0], 3'h0};
assign s8 = s9 >> s10;
assign s1 = 1'b0;
assign s20 = mp3[4] & s21;
assign s3 = mp3[4] ? s4[7:0] : s7;
assign s9 = {r7, r8, r9, r10};
assign s15 = mp3[7] ? s16 : 8'h00;
assign s30 = mp3[5] & s31;
assign s4 = s5 >> s6;
assign s33 = mp3[1:0] == 2'h0;
assign s26 = mp3[5] & s27;
assign s25 = mp3[1:0] == 2'h0;
assign s7 = mp3[5] ? s8[7:0] : s11;
assign s27 = mp3[1:0] == 2'h3;
assign s28 = mp3[5] & s29;
assign s10 = {mp3[1:0], 3'h0};
assign s2 = 1'b0;
assign s11 = mp3[6] ? s12[7:0] : s15;
assign s21 = mp3[1:0] == 2'h2;
assign s6 = {mp3[1:0], 3'h0};
assign s12 = s13 >> s14;
assign s36 = r2;
assign s29 = mp3[1:0] == 2'h2;
assign s5 = {r3, r4, r5, r6};
assign s22 = mp3[4] & s23;
assign s34 = pinWbAck;
assign s16 = {7'h00, s17};
assign s13 = r11;

kcpsm3 m0(
	.read_strobe(mp0),
	.clk(pinClock),
	.address(mp1),
	.instruction(s0),
	.write_strobe(mp2),
	.interrupt(s1),
	.reset(s2),
	.port_id(mp3),
	.in_port(s3),
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
		if (s34) begin
			r1 <= 1'b0;
		end
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s18) begin
		r3 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s20) begin
		r4 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s22) begin
		r5 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s24) begin
		r6 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s26) begin
		r7 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s28) begin
		r8 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s30) begin
		r9 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (mp2 & s32) begin
		r10 <= mp5;
	end
end
initial begin
end
always @(posedge pinClock) begin
	if (s34 & s35) begin
		r11 <= pinWbReadData;
	end
end
assign pinLeds = r0;
assign pinWbCycleStrobe = s17;
assign pinWbWriteEnable = s36;
assign pinWbAddress = s5;
assign pinWbWriteData = s9;

endmodule

