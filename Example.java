package Project1;

import java.util.ArrayList;
import java.util.List;

public class Example {

	byte[] attributes;
	boolean classification = false;
	
	public Example(byte[] attributes) {
		
		this.attributes = attributes;	
		
	}
	
	public void printAttributes() {
		System.out.print("[");
		for(byte b: attributes) {
			boolean bool = (b ==1) ? true:false;
			System.out.print(bool+",");
		}
		System.out.println("]");
	}
}
