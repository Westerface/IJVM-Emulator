package pad.ijvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class IJDB {

	Machine machine;
	PrintStream out;
	boolean exit;
	boolean running;
	String ijvmFilePath;
	String inputFilePath;
	boolean[] breakpointArray;

	IJDB() {
		out = new PrintStream(System.out);
		exit = false;
		running = false;
	}

	void handleInput() throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		String line = in.next();
		
		if(line == null) {
			out.println("No input. Type 'help' for list of commands.");
			in.close();
			return;
		}

		if(line.equals("help")) {
			help();
		}
		else if(line.equals("file")) {
			ijvmFilePath = in.next();
			loadFile(ijvmFilePath);
		}
		else if(line.equals("run")) {
			run(false);
		}
		else if(line.equals("input")){
			inputFilePath = in.next();
			setInputtoFile(inputFilePath);
		}
		else if(line.equals("break")){
			addBreakpoint(in.nextInt());
		}
		else if(line.equals("step")){
			step();
		}
		else if(line.equals("continue")){
			run(true);
		}
		else if(line.equals("frameinfo")){
			currentFrameInfo();
		}
		else if(line.equals("backtrace")){
			backtrace();
		}
		else if(line.equals("exit")){
			exit = true;
		}
		else {
			out.println("Unknown Command. Type 'help' for list of commands.");
		}
		
		in.close();
	}

	private void help() {
		out.println("Commands: ");
		out.println("  'help' for list of commands");
		out.println("  'file <file-path>'for loading the IJVM Binary File");
		out.println("  'run' for running the IJVM Code. Code is executed till a breakpoint is encountered");
		out.println("  'break <address>' for adding a breakpoint at specified address");
		out.println("  'input <file-path>' for the IJVM standard input to contain contents of specified file");
		out.println("  'step' for performing one instruction");
		out.println("  'continue' for continuing the execution of code till next breakpoint");
		out.println("  'frameinfo' for printing local stack and variables of current frame");
		out.println("  'backtrace' for printing a call-stack of all frames");
		out.println("  'exit' for exiting the IJDB\n");
		
	}

	private void loadFile(String line) {
		try {
			machine = (Machine) MachineFactory.createIJVMInstance(new File(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
	       
		breakpointArray = new boolean[machine.instructions.length];
	}

	private void run(boolean continueExec) throws FileNotFoundException {
		if(running && !continueExec) {
			boolean[] tempBreakpointArray = breakpointArray;
			loadFile(ijvmFilePath);
			setInputtoFile(inputFilePath);
			breakpointArray = tempBreakpointArray;
		}
		
		running = true;
		while(machine.getProgramCounter()<machine.instructions.length && !machine.haltStatus()) {
			if(breakpointArray[machine.getProgramCounter()] && !continueExec) {
				out.println("\nBreakpoint encountered at line " + machine.getProgramCounter());
				return;
			}
			step();
			continueExec = false;
		}
		out.println("Execution complete, machine halted. Type 'run' to run again");
	}

	private void setInputtoFile(String filePath) throws FileNotFoundException {
		File binary = new File(filePath);
		FileInputStream fileInputStream = new FileInputStream(binary);
		machine.setInput(fileInputStream);		
	}

	private void addBreakpoint(int index) {
		breakpointArray[index] = true;
	}

	private void step() {
		if(machine.haltStatus()) {
			out.println("Machine is halted. To run the program again, type run");
		}
		else {
			machine.step();
		}
	}

	private void currentFrameInfo() {
		machine.resources.printFrameInformation();
		
	}

	private void backtrace() {
		machine.resources.printFrameTrace();
	}

	void start() throws FileNotFoundException {
		help();
		while(!exit) {
			handleInput();
		}
	}

	public static void main(String argv[]) throws FileNotFoundException {
		new IJDB().start();
	}

}
