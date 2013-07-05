/** 
 * Copyright 2013 Peter Cliff, peter.cliff@bl.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.bl.dpt.utils.freqy.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.concurrent.Task;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import uk.ac.ox.bodleian.beam.drr.wclouds.Cloud;

public class FreqyTask extends Task<Object> {

	private ArrayList<File> fileList;
	private int n;
	private String inPath;
	private String outPath;
	private File allText = new File("./vUnLKLy2EXxist_deleteme.txt");

	public void setN(int n) {
		this.n = n;
	}

	public void setIn(String in) {
		this.inPath = in;
	}

	public void setOut(String out) {
		this.outPath = out;
	}

	@Override
	protected Object call() throws Exception {
		Tika tika = new Tika();
		fileList = new ArrayList<File>();
		
		StringBuilder skipped = new StringBuilder();

		try {
			File f = new File(inPath);
			if (f.isDirectory()) {
				buildFileList(inPath);
			} else {
				File single = new File(inPath);
				fileList.add(single);
			}
			
			updateProgress(0, fileList.size());

			int i = 0;

			for (File current : fileList) {
				// if cancel has been called we stop here and will never write the output file.
				if ( isCancelled() ) {
					break;
				}
				i++;
				
				
				//TODO This throws an error to the console sometimes. Need to handle that.
				//TODO Audit report of what was skipped?
				
				try {
					String text = tika.parseToString(current);
					FileUtils.writeStringToFile(allText, text, true);
				} catch (IOException e) {
					skipped.append("<li>Skipping file: "
							+ current.getAbsolutePath() + " - IO error</li>");
				} catch (TikaException e) {
					skipped.append("<li>Skipping file: "
							+ current.getAbsolutePath()
							+ " - Tika extraction error</li>");
				}
				
				updateProgress(i, fileList.size());

			}

			// Does cancel mean finally never runs?!
			if ( isCancelled() ) {
				return null;
			}
			
			Cloud cloud = new Cloud();
			cloud.absorb(FileUtils.readFileToString(allText), n);

			File o = new File(outPath);

			if (!(o.exists())) {
				FileUtils.writeStringToFile(o, "<h1>Collection Summary</h1>\n");
				FileUtils.writeStringToFile(o, "<h2>" + f.getAbsolutePath()
						+ "</h2>", true);
				FileUtils.writeStringToFile(o, "<hr />", true);
				FileUtils.writeStringToFile(o, cloud.toHTML(), true);
				FileUtils.writeStringToFile(o, "<!--h2>Skipped Files</h2><p>The following files are not included in this summary due to errors: </p>", true );
				FileUtils.writeStringToFile(o, "<ul>" + skipped.toString() + "</ul-->", true);
				FileUtils.writeStringToFile(o, "<hr/>", true);
				FileUtils.writeStringToFile(o, "<small><a href=\"https://github.com/petecliff/freqy\">made by freqy</a></small><br />", true);	
				FileUtils.writeStringToFile(o, "<small>please handle your digital data with care. freqy is for guidance only</small>", true);				

			}

		} finally {
			allText.delete();
		}
		return null;
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
