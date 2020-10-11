package pad.ijvm;

public class Frame {
	VariablePool variablePool;
	MemoryStack stack;
	int programCounter;
	
	Frame(int inProgramCounter) {
		programCounter = inProgramCounter;
		variablePool = new VariablePool();
		stack = new MemoryStack();
	}

	int[] getStackContents() {
		return stack.getStackContents();
	}

	void addVariable(byte[] label, byte[] value) {
		variablePool.addVariable(label, value);
	}
		
	byte[] returnVariable(byte[] inLabel) {
		return variablePool.returnVariable(inLabel);
	}
	
	void rewriteVariable(byte[] inLabel, byte[] newValue) {
		variablePool.rewriteVariable(inLabel, newValue);
	}
		
	void updateProgramCounter(int newProgramCounter) {
		programCounter = newProgramCounter;
	}
	
	int getProgramCounter() {
		return programCounter;
	}

}
