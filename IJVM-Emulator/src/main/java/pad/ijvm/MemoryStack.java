package pad.ijvm;

import java.nio.ByteBuffer;
import java.util.Stack;

public class MemoryStack {
	Stack<Word> mainStack = new Stack<Word>();

	void push(byte[] newElement) {
		Word newWord = new Word(newElement[3], newElement[2], newElement[1], newElement[0]);
		mainStack.push(newWord);
	}

	byte[] pop() {
		return mainStack.pop().getByteArray();	
	}

	byte[] peek() {
		return mainStack.peek().getByteArray();	
	}
	
	int[] getStackContents() {
		Word[] tempArray = new Word[mainStack.size()];
		tempArray = mainStack.toArray(new Word[mainStack.size()]);
		int[] tempIntArray = new int[tempArray.length];
		
		for(int i = 0; i<tempArray.length; i++) {
			tempIntArray[i] = ByteBuffer.wrap(tempArray[i].getByteArray()).getInt();
		}
		
		return tempIntArray;
	}

}
