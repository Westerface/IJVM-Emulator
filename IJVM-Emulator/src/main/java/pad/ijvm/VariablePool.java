package pad.ijvm;

import java.util.LinkedList;

public class VariablePool {
	LinkedList<Variable> variableList;

	VariablePool() {
		variableList = new LinkedList<>();
	}

	void addVariable(byte[] label, byte[] value) {
		Variable temp = new Variable(label, value);

		variableList.add(temp);
	}

	byte[] returnVariable(byte[] inLabel) {
		for(int i = 0; i<variableList.size(); i++) {
			if(variableList.get(i).labelEquals(inLabel)) {
				return variableList.get(i).getValueByte();
			}
		}

		return null;
	}

	void rewriteVariable(byte[] inLabel, byte[] newValue) {
		for(int i = 0; i<variableList.size(); i++) {
			if(variableList.get(i).labelEquals(inLabel)) {
				variableList.get(i).rewriteValue(newValue);
			}
		}
	}

}
