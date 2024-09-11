package main.java.ml;

import main.java.ml.extractor.Execution;


public class Launcher {
	
	public static void main(String[] args) throws Exception {
		Execution.exec("bookkeeper");
		Execution.exec("storm");

	}

}
