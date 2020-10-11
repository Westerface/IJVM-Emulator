package pad.ijvm;

import java.nio.ByteBuffer;

public class Word {
	byte[] byteArray;

	//Data is stored in the byte array as Little Endian
	
	Word(byte three, byte two, byte one, byte zero) {
		byteArray = new byte[4];
		byteArray[0] = zero;
		byteArray[1] = one;
		byteArray[2] = two;
		byteArray[3] = three;
	}
	
	Word(byte zero) {
		byteArray = new byte[4];
		byteArray[0] = zero;
		byteArray[1] = (byte) 0x00;
		byteArray[2] = (byte) 0x00;
		byteArray[3] = (byte) 0x00;
	}

	byte[] getByteArray() {
		return byteArray;
	}
	
	int getInt() {
		return ByteBuffer.wrap(byteArray).getInt();
	}

	boolean equals(Word inWord) {
		byte[] tempArray = inWord.getByteArray();

		for(int i = 0; i<4; i++) {
			if(tempArray[i] != byteArray[i]) {
				return false;
			}
		}

		return true;
	}

}
