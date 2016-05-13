package capture;

public class GestureVars {
    
    public HelperConstants helperConstants = new HelperConstants();
    public DiffModelConstants diffModelConstants = new DiffModelConstants();
    
    public GestureVars() {
	
    }
    
    public class HelperConstants{
	private int SAMPLE_SIZE = 4;
	private int TIME_THRESHOLD = 800;

	public int getTIME_THRESHOLD() {
	    return TIME_THRESHOLD;
	}

	public void setTIME_THRESHOLD(int tIME_THRESHOLD) {
	    TIME_THRESHOLD = tIME_THRESHOLD;
	}

	public int getSAMPLE_SIZE() {
	    return SAMPLE_SIZE;
	}

	public void setSAMPLE_SIZE(int sAMPLE_SIZE) {
	    SAMPLE_SIZE = sAMPLE_SIZE;
	}

    }

    public class DiffModelConstants{
	
	public DiffModelConstants() {
	}
	
	private int SENSITIVITY_VALUE = 20;
	private int BLUR_SIZE = 10;
	private int FRAME_WIDTH = 480;
	private int PADDING = 90;
	private int THRESHOLD = 40;
	private int SEQ_SIZE = 3;
	
	public int getSENSITIVITY_VALUE() {
	    return SENSITIVITY_VALUE;
	}
	public void setSENSITIVITY_VALUE(int sENSITIVITY_VALUE) {
	    SENSITIVITY_VALUE = sENSITIVITY_VALUE;
	}
	public int getBLUR_SIZE() {
	    return BLUR_SIZE;
	}
	public void setBLUR_SIZE(int bLUR_SIZE) {
	    BLUR_SIZE = bLUR_SIZE;
	}
	public int getFRAME_WIDTH() {
	    return FRAME_WIDTH;
	}
	public void setFRAME_WIDTH(int fRAME_WIDTH) {
	    FRAME_WIDTH = fRAME_WIDTH;
	}
	public int getPADDING() {
	    return PADDING;
	}
	public void setPADDING(int pADDING) {
	    PADDING = pADDING;
	}
	public int getTHRESHOLD() {
	    return THRESHOLD;
	}
	public void setTHRESHOLD(int tHRESHOLD) {
	    THRESHOLD = tHRESHOLD;
	}
	public int getSEQ_SIZE() {
	    return SEQ_SIZE;
	}
	public void setSEQ_SIZE(int sEQ_SIZE) {
	    SEQ_SIZE = sEQ_SIZE;
	}
	
	
    }


}
