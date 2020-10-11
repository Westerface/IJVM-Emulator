package pad.ijvm;

import pad.ijvm.interfaces.IJVMInterface;

import java.io.File;
import java.io.IOException;

public class MachineFactory {

	public static IJVMInterface createIJVMInstance(File binary) throws IOException {
		Machine newMachine = new Machine(binary);
		return newMachine;
	}
	
}

