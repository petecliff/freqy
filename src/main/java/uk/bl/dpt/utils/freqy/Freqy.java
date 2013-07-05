package uk.bl.dpt.utils.freqy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import uk.ac.ox.bodleian.beam.drr.wclouds.Cloud;

public class Freqy {

	private List<File> fileList;
	private File allText = new File("./vUnLKLy2EXxist_deleteme.txt");

	public static void main(String[] args) {
		Freqy mi = new Freqy();
		doHeader();
		if (args.length == 2) {
			try {
				mi.go(args[0], args[1], "" + 2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args.length == 3) {
			try {
				mi.go(args[0], args[1], args[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			doUsageAndExit();
		}
	}
	
	private static void doHeader() {
		System.out.println("");
		System.out.println("Collection Summary - Frequent Keywords");
		System.out.println("--------------------------------------");
	}

	private static void doUsageAndExit() {
		System.out.println("Usage: java -jar freqy.jar source output [n]");
		System.out.println("       source  -  source file or directory");
		System.out.println("       output  -  output filename");
		System.out.println("            n  -  optional (default 2) - number of words");
		System.out.println("");
	}

	public void go(String source, String htmlout, String nstr)
			throws IOException {
		
		int n = 2;

		try {
			n = Integer.parseInt(nstr);
		} catch (NumberFormatException e) {
			System.out.println("Please give a valid number of words");
			System.exit(0);
		}
		
		File o = new File(htmlout);
		if (o.exists()) {
			System.out.println("Output file exists - exiting");
			System.exit(0);
		}

		Tika tika = new Tika();
		fileList = new ArrayList<File>();

		try {			
			File f = new File(source);
			if (f.isDirectory()) {
				System.out.println("Scanning files, might take a while...");
				buildFileList(source);
			} else {
				File single = new File(source);
				fileList.add(single);
			}
		
			System.out.println("Found " + fileList.size());

			int i = 0;

			for (File current : fileList) {
				i++;
				System.out.println(i + "/" + fileList.size());
				try {
					String text = tika.parseToString(current);
					FileUtils.writeStringToFile(allText, text, true);
				} catch (IOException e) {
					System.out.println("Skipping file: " + current.getAbsolutePath() + " - IO error");
				} catch (TikaException e) {
					System.out.println("Skipping file: " + current.getAbsolutePath() + " - Tika extraction error");
				}
			}

			Cloud cloud = new Cloud();
			cloud.absorb(FileUtils.readFileToString(allText), n);

			FileUtils.writeStringToFile(o, "<h1>Collection Summary</h1>\n");
			FileUtils.writeStringToFile(o, "<h2>" + f.getAbsolutePath() + "</h2>", true);
			FileUtils.writeStringToFile(o, "<hr />", true);
			FileUtils.writeStringToFile(o, cloud.toHTML(), true);

			System.out.println("Summary file created at: " + o.getAbsolutePath());
			System.out.println("done");

		} finally {
			allText.delete();
		}
	}

	private void buildFileList(String root) {
		walkFileTree(new File(root));
	}

	private void walkFileTree(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				walkFileTree(f);
			}
		} else {
			fileList.add(file);
		}
	}
}
