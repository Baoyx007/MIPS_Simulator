package haven;

public class Context {
	private int cycle;
	private int curAddress;
	private int dataAddress;

	public Context(int da) {
		this.curAddress = 128;
		this.dataAddress = da;
		this.cycle = 1;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public int getCurAddress() {
		return curAddress;
	}

	public void setCurAddress(int curAddress) {
		this.curAddress = curAddress;
	}

	public int getDataAddress() {
		return dataAddress;
	}

	public void setDataAddress(int dataAddress) {
		this.dataAddress = dataAddress;
	}

}
