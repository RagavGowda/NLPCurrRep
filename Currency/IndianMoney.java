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

public class IndianMoney {

	private String input = "";

	private String output = ".";

	private static String numbers = "(\\d|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|thirty|forty|fifty|sixty|seventy|eighty|ninety)";

	private static String numExp = "((" + numbers + ")|((" + numbers + "|,)+"
			+ numbers + "+))(\\.\\d+)?";

	private static String quanExp = "(hundred|thousand|lakh|crore|trillion|quadrillion|\\ )*";

	// Match strings which end with 'dollar' or 'dollars'
	private static String regexp1 = "(\\ ?" + numExp + quanExp + ")+ rupees?";

	// Match strings which start with $ symbol
	private static String regexp2 = "\\ ?(" + numExp + quanExp + ")+";

	private static String regexp = "(" + regexp1 + ")|(" + regexp2 + ")";

	private static StringBuffer stat = new StringBuffer();

	private int count = 0;

	public IndianMoney(String[] args) throws Exception {
		if (args.length < 1) {
			throw new Exception("Please specify input corpus\n");
		}
		this.input = args[0];
		if (args.length > 1) {
			this.output = args[1];
		}
	}

	private void run() throws Exception {
		List<File> fileList = read(new File(input));
		for (File file : fileList) {
			String content = processFile(file);
			writeFile(file, content);
		}
		stat.append("Total count: " + count);
	}

	private void writeFile(File file, String content) throws IOException {
		new File(output + File.separator).mkdirs();
		String fileName = output + File.separator + file.getName() + ".out";
		FileWriter writer = new FileWriter(new File(fileName));
		writer.append(content);
		writer.close();
	}

	private String match(String content, int line) {
		// if current line do not have key words: $,'dollar' or 'dollars',
		// ignore it
		if (!Pattern.matches(".*(\\ ?|rupees?).*", content)) {
			return content;
		}
		Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			// statistic data
			count++;
			String mat = matcher.group(0);
			String rep = "";
			if (mat.endsWith(" ")) {
				mat = mat.trim();
				rep = " ***[\\" + mat + "]*** ";
			} else {
				rep = " ***[\\" + mat.trim() + "]*** ";
			}
			stat.append(" Symbol: " + mat + " Line: " + line + "\n");
			matcher.appendReplacement(sb, rep);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public List<File> read(File file) throws Exception {
		List<File> res = new ArrayList<File>();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File dir : files) {
				res.addAll(read(dir));
			}
			return res;
		} else if (file.isFile()) {
			res.add(file);
			return res;
		} else {
			throw new Exception(
					"Cannot read files, not valid file or directory path\n");
		}
	}

	public String processFile(File file) throws Exception {
		stat.append("\n\nFile: " + file.getPath() + "\n");
		StringBuffer originalFile = new StringBuffer();
		// parse Processs from input file
		FileInputStream fstream = new FileInputStream(file);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int line = 0;
		while ((strLine = br.readLine()) != null) {
			// match here
			strLine = match(strLine, line++);
			originalFile.append(strLine + "\r\n");
		}
		in.close();
		return originalFile.toString();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		IndianMoney match = new IndianMoney(args);
		match.run();
		System.out.println(stat);
	}
}