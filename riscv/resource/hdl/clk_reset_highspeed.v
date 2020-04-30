//
// clk_reset_highspeed.v -- clock and reset generator
//


module clk_reset_highspeed(clkIn, resetIn,
                 highspeedClk0, highspeedClk180,
                 dcmLocked, reset);
    input clkIn;
    input resetIn;
    output highspeedClk0;
    output highspeedClk180;
    output dcmLocked;
    output reset;

  reg reset_p;
  reg reset_s;
  reg [23:0] reset_counter;
  wire reset_counting;

  wire clkInBuffered;
  IBUFG ClkInBuffer(
    .I(clkIn),
    .O(clkInBuffered)
  );

  wire clk50Unbuffered, clk50;
  wire highspeedClk0Unbuffered;
  wire highspeedClk180Unbuffered;
  DCM_SP dcm50(
    .RST(1'b0),
    .CLKIN(clkInBuffered),
    .CLKFB(clk50),
    .CLK0(clk50Unbuffered),
    .CLK2X(highspeedClk0Unbuffered),
    .CLK2X180(highspeedClk180Unbuffered),
    .LOCKED(dcmLocked),
    .PSCLK(1'b0),
    .PSEN(1'b0),
    .PSINCDEC(1'b0)
  );

  defparam dcm50.CLKDV_DIVIDE = 2.0;
  defparam dcm50.CLKFX_DIVIDE = 1;
  defparam dcm50.CLKFX_MULTIPLY = 4;
  defparam dcm50.CLKIN_DIVIDE_BY_2 = "FALSE";
  defparam dcm50.CLKIN_PERIOD = 20.0;
  defparam dcm50.CLKOUT_PHASE_SHIFT = "NONE";
  defparam dcm50.CLK_FEEDBACK = "1X";
  defparam dcm50.DESKEW_ADJUST = "SYSTEM_SYNCHRONOUS";
  defparam dcm50.DLL_FREQUENCY_MODE = "LOW";
  defparam dcm50.DUTY_CYCLE_CORRECTION = "TRUE";
  defparam dcm50.PHASE_SHIFT = 0;
  defparam dcm50.STARTUP_WAIT = "FALSE";

  BUFG clk50Buffer(
    .I(clk50Unbuffered),
    .O(clk50)
  );

  BUFG highspeedClkBuffer0(
    .I(highspeedClk0Unbuffered),
    .O(highspeedClk0)
  );

  BUFG highspeedClkBuffer180(
    .I(highspeedClk180Unbuffered),
    .O(highspeedClk180)
  );

  //------------------------------------------------------------

  assign reset_counting = (reset_counter == 24'hFFFFFF) ? 0 : 1;

  always @(posedge highspeedClk180) begin
    reset_p <= resetIn;
    reset_s <= reset_p;
    if (reset_s | ~dcmLocked) begin
      reset_counter <= 24'h000000;
    end else begin
      if (reset_counting == 1) begin
        reset_counter <= reset_counter + 1;
      end
    end
  end

  assign reset = reset_counting;

endmodule
