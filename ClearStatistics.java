package spelling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Class that contains functions to clear the statistic files and return correct information message
 * @author yyap601
 *
 */
public class ClearStatistics {
	
	// function to return information message and at the same time clear all the statistic files
	public static String clearStats(){
		String infoMsg = "Attempt History Cleared !!!";
		clear();
		return infoMsg;
	}
	
	// function to clear all the statistic files
	private static void clear(){
		clearFile(new File(".spelling_aid_tried_words"));
		clearFile(new File(".spelling_aid_failed"));
		clearFile(new File(".spelling_aid_statistics"));
	}
	
	// function to clear a single statistic file
	public static void clearFile(File file){
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
