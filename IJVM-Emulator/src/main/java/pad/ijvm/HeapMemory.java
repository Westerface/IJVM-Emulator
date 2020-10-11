package pad.ijvm;

import java.util.LinkedList;

public class HeapMemory {

	LinkedList<VariablePool> variablePoolArray;
	
	HeapMemory() {
		variablePoolArray = new LinkedList<>();
	}
	
	void addVariable(byte variablePoolArrayIndex, byte[] label, byte[] value) {
		variablePoolArray.get(variablePoolArrayIndex).addVariable(label, value);
	}
		
	byte[] returnVariable(byte variablePoolArrayIndex, byte[] inLabel) {
		return variablePoolArray.get(variablePoolArrayIndex).returnVariable(inLabel);
	}
	
	void rewriteVariable(byte variablePoolArrayIndex, byte[] inLabel, byte[] newValue) {
		variablePoolArray.get(variablePoolArrayIndex).rewriteVariable(inLabel, newValue);
	}
	
	int addArray() {
		variablePoolArray.add(new VariablePool());
		return variablePoolArray.size() - 1;
	}
	
	
}
