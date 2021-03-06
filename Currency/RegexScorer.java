import java.util.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A scorer for regular expressions.
 * It reads both the system output file and the key file and reports P/R/F. It requires that expressions should be annotated with brakets [...]
 * 
 * @author Ang Sun (Feb. 2012)
 * @version 1.0
 *
 */
public class RegexScorer {
	
	static int numCorrect = 0;
static int i = 0;
static int n = 0;
	static int numWrong = 0;	
	static int numTotalKeys = 0;
	static String keyFileContent;
	static String sysFileContent;
static String[] tot = new String[1000];
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("RegexScorer requires 2 arguments: [SystemOutputFile] [KeyFile]");
			return;
		}
		File sysFile = new File(args[0]);
		sysFileContent = readFileToString(sysFile);
		Map<Integer, Integer> sysOffsets = getRegexAnnotations(sysFileContent);
		File keyFile = new File(args[1]);
		keyFileContent = readFileToString(keyFile);
		Map<Integer, Integer> keyOffsets = getRegexAnnotations(keyFileContent);
		score(sysOffsets, keyOffsets);
	}
	
	private static void score(Map<Integer, Integer> sysOffsets, Map<Integer, Integer> keyOffsets) {
		sysFileContent = sysFileContent.replaceAll("\\[|\\]", "");		
		for (Integer leftBracket : sysOffsets.keySet()) {

		i++;
	
			int rightBracket = sysOffsets.get(leftBracket);
			if (keyOffsets.containsKey(leftBracket) && keyOffsets.get(leftBracket) == rightBracket) {
				numCorrect++;
				System.out.println("Correct Match:\t" + sysFileContent.substring(leftBracket, rightBracket));
			} else 

{
				++numWrong;
				System.out.println(" ** Detected :\t" + sysFileContent.substring(leftBracket, rightBracket));

tot[i]=sysFileContent.substring(leftBracket, rightBracket);
}
}
System.out.println ("*********************************************************");


try
{
    PrintWriter pr = new PrintWriter("file.txt");    

    for(int i=0; i<numWrong ; i++)
    {
        pr.println(tot[i]);
    }
    pr.close();
}
catch (Exception e)
{
    e.printStackTrace();
    System.out.println("No such file exists.");
}



	System.out.println ("*********************************************************");

System.out.println (" EXPESSIONS UPDATED TO file.TXT ");

		

		System.out.println("===================================================");
		//System.out.println (numCorrect + " correct.");
		System.out.println (numWrong + " Expressions Recognised");
		
		int precision = (int) 100.0 * numCorrect / (numCorrect + numWrong);
		int recall = (int) 100.0 * numCorrect / numTotalKeys;
		int f1 = 2 * precision * recall / (precision + recall);
		System.out.println ("precision: " + precision + "%");
		System.out.println ("recall: " + recall + "%");
		System.out.println ("F1: " + f1 + "%");
	}
	
	/**
	 * build offset table from fileContent. 
	 * @param fileContent
	 * @return
	 * @throws IOException
	 */
	private static Map<Integer, Integer> getRegexAnnotations (String fileContent) throws IOException {
		Map<Integer, Integer> annos = new HashMap();
		int indexOfBracket = 0;
		Stack<Integer> stack = new Stack();
		for (int i = 0; i < fileContent.length(); i++) {
			if (fileContent.charAt(i) == '[') {
				stack.push(i - indexOfBracket);
				indexOfBracket++;
			} else if (fileContent.charAt(i) == ']'){
				int leftBracket = stack.pop();
				int rightBracket = i - indexOfBracket;
				annos.put(leftBracket, rightBracket);
				indexOfBracket++;
			}
		}
		return annos;
	}
	
	private static String readFileToString(File file) throws IOException {
		final int BUFFER_SIZE = 1024 * 4;
		char[] buffer = new char[BUFFER_SIZE];
		int readCount;
		Reader reader = new BufferedReader(new FileReader(file));

		StringWriter out = new StringWriter();
		try {
			while ((readCount = reader.read(buffer)) >= 0) {
				out.write(buffer, 0, readCount);
			}
		} finally {
			closeQuitely(reader);
		}

		return out.toString();
	}

	private static void closeQuitely(Reader in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException ex) {
			}
		}
	}
}
