//
// eco32.v -- ECO32 top-level description
//


module eco32(clk_in,
             reset_in,
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
             fpga_init_b);

    // clock and reset
    input clk_in;
    input reset_in;
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

    output fpga_init_b;

  // clk_reset
  wire ddr_clk_0;
  wire ddr_clk_90;
  wire ddr_clk_180;
  wire ddr_clk_270;
  wire ddr_clk_ok;
  wire clk;
  wire reset;

  // ram
  wire ram_en;
  wire ram_wr;
  wire [1:0] ram_size;
  wire [25:0] ram_addr;
  wire [31:0] ram_data_in;
  wire [31:0] ram_data_out;
  wire ram_wt;






  ram ram1(
    .ddr_clk_0(ddr_clk_0),
    .ddr_clk_90(ddr_clk_90),
    .ddr_clk_180(ddr_clk_180),
    .ddr_clk_270(ddr_clk_270),
    .ddr_clk_ok(ddr_clk_ok),
    .clk(clk),
    .reset(reset),
    .en(ram_en),
    .wr(ram_wr),
    .size(ram_size[1:0]),
    .addr(ram_addr[25:0]),
    .data_in(ram_data_in[31:0]),
    .data_out(ram_data_out[31:0]),
    .wt(ram_wt),
    .sdram_ck_p(sdram_ck_p),
    .sdram_ck_n(sdram_ck_n),
    .sdram_cke(sdram_cke),
    .sdram_cs_n(sdram_cs_n),
    .sdram_ras_n(sdram_ras_n),
    .sdram_cas_n(sdram_cas_n),
    .sdram_we_n(sdram_we_n),
    .sdram_ba(sdram_ba[1:0]),
    .sdram_a(sdram_a[12:0]),
    .sdram_udm(sdram_udm),
    .sdram_ldm(sdram_ldm),
    .sdram_udqs(sdram_udqs),
    .sdram_ldqs(sdram_ldqs),
    .sdram_dq(sdram_dq[15:0])
  );


endmodule
