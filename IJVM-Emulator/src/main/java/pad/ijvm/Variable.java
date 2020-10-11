package pad.ijvm;

import java.nio.ByteBuffer;

public class Variable {
	byte[] label;
	Word value;

	Variable(byte inLabel, byte[] inWord) {
		label = new byte[2];
		label[0] = (byte) 0x00;
		label[1] = inLabel;
		value = new Word(inWord[3], inWord[2], inWord[1], inWord[0]);
	}

	Variable(byte[] inLabel, byte[] inWord) {
		label = inLabel;
		value = new Word(inWord[3], inWord[2], inWord[1], inWord[0]);
	}

	byte[] getValueByte() {
		return value.getByteArray();
	}

	int getValueInt() {
		return ByteBuffer.wrap(value.getByteArray()).getInt();
	}

	void rewriteValue(byte[] newValue) {
		value = new Word(newValue[3], newValue[2], newValue[1], newValue[0]);
	}

	boolean labelEquals(byte[] inLabel) {
		if(inLabel.length == label.length) {
			for(int i = 0; i<label.length; i++) {
				if(label[i] != inLabel[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
