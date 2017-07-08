package image;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Arrays;

import org.apache.log4j.Logger;

import exmaple.Trivial;

public class Main {
	/**
	 * @param args
	 */
	static String HOME_WANIMAL = "http://wanimal1983.tumblr.com/";
	static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Catcher catcher = new Catcher();
		catcher.catchingAllPage();

	}

}


