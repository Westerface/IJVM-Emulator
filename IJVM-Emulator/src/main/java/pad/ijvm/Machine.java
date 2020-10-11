package pad.ijvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;

import pad.ijvm.interfaces.IJVMInterface;

public class Machine implements IJVMInterface {

	byte[] bytes;
	byte[] instructions;
	boolean halted;
	Word[] constantPool;
	int readPointer;
	int programCounter;
	int i = 0;

	MachineInstructions resources;
	PrintStream out;

	public Machine(File binary) throws IOException {
		readPointer = 0;
		halted = false;

		bytes = new byte[(int) binary.length()];
		FileInputStream fileInputStream = new FileInputStream(binary);
		fileInputStream.read(bytes);
		fileInputStream.close();

		if(bytes[0] == (byte) 0x1D && bytes[1] == (byte) 0xEA && bytes[2] == (byte) 0xDF && bytes[3] == (byte) 0xAD) {
			readPointer += 4;
			parseData();
			resources = new MachineInstructions(instructions, constantPool);
		}

		programCounter = resources.getProgramCounter();

	}

	public int topOfStack() {
		int tos = resources.TOS();
		return tos;
	}

	public int[] getStackContents() {
		return resources.getStackContents();
	}

	public byte[] getText() {
		return instructions;
	}

	public int getProgramCounter() {
		return programCounter;
	}

	public int getLocalVariable(int i) {
		return ByteBuffer.wrap(resources.frameStack.peek().returnVariable(ByteBuffer.allocate(2).putShort((short) i).array())).getInt();
	}

	public int getConstant(int i) {
		return ByteBuffer.wrap(resources.constantPool[i].getByteArray()).getInt();
	}

	public void step() {
		if(!halted) {
			halted = resources.process();
			programCounter = resources.getProgramCounter();
		}
	}

	public boolean haltStatus() {
		return halted;
	}

	public void parseData() {
		readPointer += 4; //as we don't need to use the origin address

		int size = ByteBuffer.wrap(bytes).getInt(readPointer);
		constantPool = new Word[size];
		readPointer += 4;
		for(int i = 0; i<size/4; i++) {
			constantPool[i] = new Word(bytes[readPointer+3], bytes[readPointer+2], bytes[readPointer+1], bytes[readPointer]);
			readPointer+=4;
		}

		readPointer += 4; //as we don't need to use the origin address
		size = ByteBuffer.wrap(bytes).getInt(readPointer);
		readPointer += 4;

		instructions = new byte[size];

		for(int j = 0; j<size; j++) {
			instructions[j] = bytes[readPointer];
			readPointer++;
		}
	}

	public void run() {
		while(!halted && programCounter < instructions.length) {
			step();
		}
	}

	public byte getInstruction() {
		return instructions[programCounter];
	}

	public void setOutput(PrintStream newOut) {
		resources.setOutput(newOut);
	}

	public void setInput(InputStream in) {
		resources.setInput(in);
	}

}
