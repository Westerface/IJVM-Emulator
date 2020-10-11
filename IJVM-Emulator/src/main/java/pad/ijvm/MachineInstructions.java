package pad.ijvm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Stack;

public class MachineInstructions {

	static final byte NOP = 0x00;
	static final byte BIPUSH = 0x10;
	static final byte DUP = 0x59;
	static final byte POP = 0x57;
	static final byte SWAP = 0x5F;
	static final byte IADD = 0x60;
	static final byte ISUB = 0x64;
	static final byte IAND = 0x7E;
	static final byte IOR = (byte) 0xb0;
	static final byte GOTO = (byte) 0xA7;
	static final byte IFEQ = (byte) 0x99;
	static final byte IFLT = (byte) 0x9b;
	static final byte IF_ICMPEQ = (byte) 0x9f;
	static final byte LDC_W = 0x13;
	static final byte ISTORE = 0x36;
	static final byte ILOAD = 0x15;
	static final byte IINC = (byte) 0x84;
	static final byte WIDE = (byte) 0xC4;
	static final byte INVOKEVIRTUAL = (byte) 0xB6;
	static final byte IRETURN = (byte) 0xAC;
	static final byte IN = (byte) 0xfc;
	static final byte OUT = (byte) 0xfd;
	static final byte HALT = (byte) 0xFF;
	static final byte ERR = (byte) 0xfe;
	static final byte NETBIND = (byte) 0xE1;
	static final byte NETCONNECT = (byte) 0xE2;
	static final byte NETIN = (byte) 0xE3;
	static final byte NETOUT = (byte) 0xE4;
	static final byte NETCLOSE = (byte) 0xE5;
	static final byte NEWARRAY = (byte) 0xD1;
	static final byte IASTORE = (byte) 0xD3;
	static final byte IALOAD = (byte) 0xD2;

	static final byte ZERO = (byte) 0x00;
	static final byte ONE = (byte) 0x01;

	byte[] instructions;
	Word[] constantPool;
	Stack<Frame> frameStack = new Stack<>();
	NetworkResources network;
	HeapMemory heap;
	PrintStream out;
	InputStream in;

	MachineInstructions (byte[] inputInstructions, Word[] inputConstantPool) {
		out = new PrintStream(System.out);
		in = System.in;
		instructions = inputInstructions;
		constantPool = inputConstantPool;
		frameStack.push(new Frame(0));
		network = new NetworkResources();
		heap = new HeapMemory();
	}

	boolean process() {
		int programCounter = currentFrame().getProgramCounter();
		boolean halted = false;
		byte current = instructions[programCounter];
		if(programCounter == 246) {
			System.out.print("hello\n");
		}
		programCounter++;

		switch(current) {
		case BIPUSH:
			BIPUSH(instructions[programCounter]);
			++programCounter;
			break;
		case IADD: 
			IADD();
			break;
		case OUT:
			OUT();
			break;
		case DUP:
			DUP();
			break;
		case ERR: 
			System.err.println("ERR");
			break;
		case GOTO:
			programCounter = GOTO(programCounter);
			break;
		case HALT:
			halted = true;
			break;
		case IAND: 
			IAND();
			break;
		case IFEQ:
			programCounter = IFEQ(programCounter);
			break;
		case IFLT:
			programCounter = IFLT(programCounter);
			break;
		case IF_ICMPEQ: 
			programCounter = IF_ICMPEQ(programCounter);
			break;
		case IINC:
			programCounter = IINC(programCounter+1, ZERO, instructions[programCounter]);
			++programCounter;
			break;
		case ILOAD:
			ILOAD(ZERO, instructions[programCounter]);
			++programCounter;
			break;
		case IN: 
			IN();
			break;
		case INVOKEVIRTUAL:
			currentFrame().updateProgramCounter(programCounter);
			programCounter = INVOKEVIRTUAL(programCounter);
			break;
		case IOR:
			IOR();
			break;
		case IRETURN: 
			programCounter = IRETURN();
			break;
		case ISTORE:
			ISTORE(ZERO, instructions[programCounter]);
			++programCounter;
			break;
		case ISUB:
			ISUB();
			break;
		case LDC_W: 
			programCounter = LDC_W(programCounter);
			break;
		case NOP:
			//out.println("NOP");	//De-comment to print NOP to console
			break;
		case POP:
			POP();
			break;
		case SWAP: 
			SWAP();
			break;
		case WIDE:
			programCounter = WIDE(programCounter);
			break;
		case NETBIND:	//Network Commands
			NETBIND();
			break;
		case NETCONNECT:
			NETCONNECT();
			break;
		case NETIN:
			NETIN();
			break;
		case NETOUT:
			NETOUT();
			break;
		case NETCLOSE:
			NETCLOSE();
			break;
		case NEWARRAY:	//Heap Memory Instructions
			NEWARRAY();
			break;
		case IASTORE:
			programCounter = IASTORE(programCounter);
			break;
		case IALOAD:
			programCounter = IALOAD(programCounter);
			break;
		}

		currentFrame().updateProgramCounter(programCounter);
		return halted;
	}



	//Heap Memory Bonus Instruction Functions

	private int IALOAD(int programCounter) {
		byte variablePoolArrayIndex = (byte) getInt(currentStack().pop());
		byte[] label = currentStack().pop();

		currentStack().push(heap.returnVariable(variablePoolArrayIndex, label));

		return programCounter;
	}

	private int IASTORE(int programCounter) {
		byte variablePoolArrayIndex = (byte) getInt(currentStack().pop());
		byte[] label = currentStack().pop();
		byte[] value = currentStack().pop();


		if(heap.returnVariable(variablePoolArrayIndex, label) == null) {
			heap.addVariable(variablePoolArrayIndex, label, value);
		}
		else {
			heap.rewriteVariable(variablePoolArrayIndex, label, value);
		}

		return programCounter;
	}

	private void NEWARRAY() {
		currentStack().pop();
		currentStack().push(putInt(heap.addArray(), 4));
	}


	//Network Bonus Functions

	void NETBIND() {
		int port = getInt(currentStack().pop());
		byte status = network.createServer(port) ? ONE : ZERO;

		currentStack().push(new Word(status).getByteArray());
	}

	void NETCONNECT() {
		byte[] ipAddress = currentStack().pop();
		int port = getInt(currentStack().pop());
		byte status = network.connectToServer(ipAddress, port) ? ONE : ZERO;

		currentStack().push(new Word(status).getByteArray());
	}

	void NETIN() {
		currentStack().push(network.read());
	}

	void NETOUT() {
		network.print(currentStack().pop());
	}

	void NETCLOSE() {
		network.closeConnection();
	}


	//General Instruction Functions

	private void IN() {
		int input = -1;
		try {
			input = in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(input == -1) {
			input = 0;
		}

		byte[] newElement = putInt(input, 4);
		currentStack().push(newElement);
	}

	private int IRETURN() {
		byte[] frameTOS = currentStack().peek();
		frameStack.pop();
		currentStack().push(frameTOS);
		return currentFrame().getProgramCounter();
	}

	private int INVOKEVIRTUAL(int programCounter) {
		int newProgramCounter = getInt(constantPool[getShort(instructions, programCounter)].getByteArray());
		programCounter += 2;
		currentFrame().updateProgramCounter(programCounter);

		Frame tempFrame = currentFrame();
		frameStack.push(new Frame(newProgramCounter));
		programCounter = currentFrame().getProgramCounter();

		int parameterCount = getShort(instructions, programCounter);
		programCounter += 2;
		currentFrame().updateProgramCounter(programCounter);

		currentStack().push(tempFrame.stack.peek()); //Remove this for updated SimpleCalc.ijvm

		for(int i = 0; i<parameterCount; i++) {
			currentStack().push(tempFrame.stack.pop());
		}

		for(int i = 0; i<parameterCount; i++) {
			ISTORE(ZERO, (byte) (i));
		}

		programCounter += 2; //We don't need the number of variables which would be stored during the course of INVOKEVIRTUAL, but need to move on in the program
		currentFrame().updateProgramCounter(programCounter);
		return programCounter;
	}

	int WIDE(int programCounter) {
		byte current = instructions[programCounter];
		programCounter++;

		switch(current) {
		case (byte) 0x84:
			programCounter = IINC(programCounter+2, instructions[programCounter], instructions[programCounter+1]);
		break;
		case (byte) 0x36:
			ISTORE(instructions[programCounter], instructions[programCounter+1]);
		++programCounter;
		break;
		case (byte) 0x15:
			ILOAD(instructions[programCounter], instructions[programCounter+1]);
		++programCounter;
		break;
		}

		return programCounter + 1;//As 1 extra byte is being read
	}


	//Functions ILOAD, ISTORE and IINC have byte1 and byte2 in arguments as same functions are used for storing variables with wide labels as well.
	private void ILOAD(byte byte1, byte byte2) {
		byte[] label = {byte1, byte2};
		currentStack().push(currentFrame().returnVariable(label));
	}

	private void ISTORE(byte byte1, byte byte2) {
		byte[] label = {byte1, byte2};
		if(currentFrame().returnVariable(label) == null) {
			currentFrame().addVariable(label, currentStack().pop());
		}
		else {
			currentFrame().rewriteVariable(label, currentStack().pop());
		}
	}

	private int IINC(int programCounter, byte byte1, byte byte2) {//programCounter passed here should point to the instruction having the increment value
		byte[] label = {byte1, byte2};
		int increment = instructions[programCounter];
		int oldValue = getInt(currentFrame().returnVariable(label));

		currentFrame().rewriteVariable(label, putInt(increment+oldValue, 4));

		return programCounter;
	}

	private int LDC_W(int programCounter) {
		int constantLocation = getShort(instructions,programCounter);
		byte[] constant = constantPool[constantLocation].getByteArray();
		currentStack().push(constant);
		return programCounter+2;
	}

	private void IOR() {
		byte[] firstWord = currentStack().pop();
		byte[] secondWord = currentStack().pop();

		int firstElement = getInt(firstWord);
		int secondElement = getInt(secondWord);

		byte[] finalWord = putInt(firstElement|secondElement, 4);

		currentStack().push(finalWord);
	}

	private void SWAP() {
		byte[] firstElement = currentStack().pop();
		byte[] secondElement = currentStack().pop();

		currentStack().push(firstElement);
		currentStack().push(secondElement);		
	}

	private void POP() {
		currentStack().pop();		
	}

	private void ISUB() {
		byte[] firstWord = currentStack().pop();
		byte[] secondWord = currentStack().pop();

		int firstElement = getInt(firstWord);
		int secondElement = getInt(secondWord);

		byte[] finalWord = putInt(secondElement-firstElement, 4);

		currentStack().push(finalWord);
	}

	private void BIPUSH(int newInt) {
		byte[] newElement = putInt(newInt, 4);
		currentStack().push(newElement);

	}

	private void OUT() {
		char temp = (char)getInt(currentStack().pop());
		out.print(temp);
	}

	private void DUP() {
		currentStack().push(currentStack().peek());

	}

	private int GOTO(int programCounter) {
		programCounter += (getShort(instructions, programCounter) - 1);//this -1 works because you have already incremented the program counter
		return programCounter;
	}

	private void IAND() {
		byte[] firstWord = currentStack().pop();
		byte[] secondWord = currentStack().pop();

		int firstElement = getInt(firstWord);
		int secondElement = getInt(secondWord);

		byte[] finalWord = putInt(firstElement&secondElement, 4);

		currentStack().push(finalWord);
	}

	private int IFEQ(int programCounter) {
		if(getInt(currentStack().pop()) == 0) {
			programCounter += (getShort(instructions, programCounter) - 1) ;
		}
		else {
			programCounter += 2;
		}

		return programCounter;
	}

	private int IFLT(int programCounter) {
		if(getInt(currentStack().pop()) < 0) {
			programCounter += (getShort(instructions, programCounter) - 1) ;
		}
		else {
			programCounter += 2;
		}

		return programCounter;
	}

	private int IF_ICMPEQ(int programCounter) {
		byte[] firstWord = currentStack().pop();
		byte[] secondWord = currentStack().pop();

		int firstElement = getInt(firstWord);
		int secondElement = getInt(secondWord);

		if(firstElement == secondElement) {
			programCounter += (getShort(instructions, programCounter) - 1) ;
		}
		else {
			programCounter += 2;
		}

		return programCounter;
	}

	private void IADD() {
		byte[] firstWord = currentStack().pop();
		byte[] secondWord = currentStack().pop();

		int firstElement = getInt(firstWord);
		int secondElement = getInt(secondWord);

		byte[] finalWord = putInt(firstElement+secondElement, 4);

		currentStack().push(finalWord);
	}

	int TOS() {
		return getInt(currentStack().peek());
	}


	//Functions for IJDB
	void printFrameTrace() {
		Stack<Frame> clonedFrameStack = (Stack<Frame>) frameStack.clone();

		Frame tempFrame = clonedFrameStack.pop();
		if(clonedFrameStack.isEmpty()) {
			out.print("No Methods Called!\n");
		}

		while(!clonedFrameStack.isEmpty()) {
			int oldProgramCounter = clonedFrameStack.peek().programCounter - 3;
			int newProgramCounter = getInt(constantPool[getShort(instructions, oldProgramCounter+1)].getByteArray());
			out.print("\nCall from PC: " + oldProgramCounter);
			out.print("\nCall to PC: " + newProgramCounter);
			out.print("\nArguments:\n");

			if(getShort(instructions, newProgramCounter) == 0) {
				out.print("No Arguments!\n");
			}
			else {
				for(int i = 0; i<getShort(instructions, newProgramCounter); i++) {
					out.printf("   %d: 0x%x\n", i, tempFrame.variablePool.variableList.get(i).getValueInt());
				}
				tempFrame = clonedFrameStack.pop();
			}
		}
	}

	void printFrameInformation() {
		int[] tempCurrentStack = currentFrame().getStackContents();
		out.print("Current Stack:\n");
		for(int i = 0; i < tempCurrentStack.length; i++) {
			out.printf("  0x%x\n", (byte) tempCurrentStack[i]);
		}

		if(currentFrame().variablePool.variableList.size() == 0) {
			out.print("Empty Variable Pool!\n");
		}
		else {
			out.print("Variable Pool:\n");
			for(int i = 0; i<currentFrame().variablePool.variableList.size(); i++) {
				out.printf("   %d: 0x%x\n", i, currentFrame().variablePool.variableList.get(i).getValueInt());
			}
		}

	}

	//Supporting Functions

	private Frame currentFrame() {
		return frameStack.peek();
	}

	private MemoryStack currentStack() {
		return frameStack.peek().stack;
	}

	private byte[] putInt(int value, int size) {
		return ByteBuffer.allocate(size).putInt(value).array();
	}

	private int getInt(byte[] byteArray) {
		return ByteBuffer.wrap(byteArray).getInt();
	}

	private int getInt(byte[] byteArray, int index) {
		return ByteBuffer.wrap(byteArray).getInt(index);
	}

	private int getShort(byte[] byteArray) {
		return ByteBuffer.wrap(byteArray).getShort();
	}

	private int getShort(byte[] byteArray, int index) {
		return ByteBuffer.wrap(byteArray).getShort(index);
	}

	public void setOutput(PrintStream newOut) {
		out = newOut;
	}

	public void setInput(InputStream newIn) {
		in = newIn;		
	}

	int getProgramCounter() {
		return currentFrame().getProgramCounter();
	}

	int[] getStackContents() {
		return currentFrame().getStackContents();
	}




}
