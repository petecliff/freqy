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

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FreqyUI extends Application {
	private static final String FIELD_ERROR = "-fx-text-fill: red;";
	private static final String FIELD_UNSET = "-fx-text-fill: grey;";
	private static final String FIELD_SET = "-fx-text-fill: black;";

	private Text titleText;
	private Button goButton;
	private Button exitButton;
	private Label dirInLabel;
	private Label fileOutLabel;
	private TextField dirInField;
	private TextField fileOutField;

	private Stage stageRef;
	private Button dirInButton;
	private Button fileOutButton;

	private File dirInFile;
	private File fileOutFile;

	private Slider nSlider;

	private double n = 2.0;

	private ProgressBar progress;
	private Text done;

	@Override
	public void start(Stage stage) throws Exception {
		stageRef = stage;

		stage.setTitle("freqy");

		AnchorPane root = new AnchorPane();

		// Add title
		titleText = getTitle();
		root.getChildren().add(titleText);
		AnchorPane.setTopAnchor(titleText, 5.0);
		AnchorPane.setRightAnchor(titleText, 106.0);

		// Add buttons
		goButton = getGoButton();
		root.getChildren().add(goButton);
		AnchorPane.setTopAnchor(goButton, 205.0);
		AnchorPane.setRightAnchor(goButton, 14.0);

		exitButton = getExitButton();
		root.getChildren().add(exitButton);
		AnchorPane.setTopAnchor(exitButton, 205.0);
		AnchorPane.setLeftAnchor(exitButton, 14.0);

		// Input picker
		dirInLabel = new Label("IN");
		root.getChildren().add(dirInLabel);
		AnchorPane.setTopAnchor(dirInLabel, 104.0);
		AnchorPane.setLeftAnchor(dirInLabel, 24.0);

		dirInField = new TextField("Select input directory");
		dirInField.setPrefWidth(200.0);
		dirInField.setEditable(false);
		root.getChildren().add(dirInField);
		AnchorPane.setTopAnchor(dirInField, 100.0);
		AnchorPane.setLeftAnchor(dirInField, 60.0);

		dirInButton = getDirInButton();
		root.getChildren().add(dirInButton);
		AnchorPane.setTopAnchor(dirInButton, 100.0);
		AnchorPane.setLeftAnchor(dirInButton, 265.0);

		// Output picker
		fileOutLabel = new Label("OUT");
		root.getChildren().add(fileOutLabel);
		AnchorPane.setTopAnchor(fileOutLabel, 145.0);
		AnchorPane.setLeftAnchor(fileOutLabel, 24.0);

		fileOutField = new TextField("Select output file");
		fileOutField.setPrefWidth(200.0);
		fileOutField.setEditable(false);
		root.getChildren().add(fileOutField);
		AnchorPane.setTopAnchor(fileOutField, 140.0);
		AnchorPane.setLeftAnchor(fileOutField, 60.0);

		fileOutButton = getFileOutButton();
		root.getChildren().add(fileOutButton);
		AnchorPane.setTopAnchor(fileOutButton, 140.0);
		AnchorPane.setLeftAnchor(fileOutButton, 265.0);

		dirInField.setStyle(FIELD_UNSET);
		fileOutField.setStyle(FIELD_UNSET);

		// Set n slider
		nSlider = getNSlider();
		root.getChildren().add(nSlider);
		AnchorPane.setTopAnchor(nSlider, 191.0);
		AnchorPane.setLeftAnchor(nSlider, 90.0);

		// progress bar
		progress = new ProgressBar();
		progress.setVisible(false);
		root.getChildren().add(progress);
		AnchorPane.setTopAnchor(progress, 170.0);
		AnchorPane.setLeftAnchor(progress, 110.0);

		// done message
		done = new Text("Complete");
		done.setVisible(false);
		root.getChildren().add(done);
		AnchorPane.setTopAnchor(done, 170.0);
		AnchorPane.setLeftAnchor(done, 125.0);

		// TODO Implement cancel button

		Scene scene = new Scene(root, 320, 240);
		scene.setFill(Color.ALICEBLUE);

		stage.setScene(scene);
		stage.show();
	}

	private Slider getNSlider() {
		Slider s = new Slider();
		s.setMax(3);
		s.setMin(1);
		s.setMajorTickUnit(1.0);
		s.setMinorTickCount(0);
		s.setShowTickLabels(true);
		s.setShowTickMarks(true);
		s.setValue(n);
		s.setSnapToTicks(true);
		s.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> obval,
					Number oldVal, Number newVal) {
				n = newVal.intValue();
			}
		});
		return s;
	}

	private Button getGoButton() {
		Button b = new Button();
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dirInField.setStyle(FIELD_UNSET);
				fileOutField.setStyle(FIELD_UNSET);

				if (dirInFile == null) {
					dirInField.setText("Please set input directory");
					dirInField.setStyle(FIELD_ERROR);
					return;
				}
				if (!(dirInFile.exists())) {
					dirInField.setText("Please set input directory");
					dirInField.setStyle(FIELD_ERROR);
					return;
				}
				if (fileOutFile == null) {
					fileOutField.setText("Please set a filename");
					fileOutField.setStyle(FIELD_ERROR);
					return;
				}
				if (fileOutFile.exists()) {
					fileOutField.setText("File exists");
					fileOutField.setStyle(FIELD_ERROR);
					return;
				}
				goButton.setDisable(true);

				final FreqyTask task = new FreqyTask();
				task.setIn(dirInFile.getAbsolutePath());
				task.setOut(fileOutFile.getAbsolutePath());
				task.setN(2);

				progress.setVisible(true);
				progress.progressProperty().bind(task.progressProperty());

				new Thread(task).start();

				// Set up a mini task to monitor the UI and renable buttons, etc. when done.
				Task<Void> updateUiTask = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						goButton.setDisable(true);
						while (!task.isDone()) {
							Thread.sleep(1000);
						}
						progress.setVisible(false);
						done.setVisible(true);
						fileOutFile = null;
						fileOutField.setText("");
						dirInFile = null;
						dirInField.setText("");
						goButton.setDisable(false);
						return null;
					}
				};
				
				new Thread(updateUiTask).start();
				
			}
		});
		b.setText("Go");
		return b;
	}

	private Button getExitButton() {
		Button b = new Button();
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});
		b.setText("Exit");
		return b;
	}

	private Button getDirInButton() {
		Button b = new Button();
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser dirChooser = new DirectoryChooser();
				dirChooser
						.setTitle("Select top directory of the collection...");
				dirInFile = dirChooser.showDialog(stageRef);
				if (dirInFile != null) {
					dirInField.setText(dirInFile.getAbsolutePath());
					dirInField.setStyle(FIELD_SET);
				}
			}
		});
		b.setText("Pick");
		return b;
	}

	private Button getFileOutButton() {
		Button b = new Button();
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select file for summary...");
				fileOutFile = fileChooser.showSaveDialog(stageRef);
				if (fileOutFile != null) {
					fileOutField.setText(fileOutFile.getAbsolutePath());
					fileOutField.setStyle(FIELD_SET);
				}
			}
		});
		b.setText("Pick");
		return b;
	}

	private Text getTitle() {
		Text t = new Text();
		t.setText("freqy");
		t.setFill(Color.GREY);
		t.setFont(Font.font("null", FontWeight.BOLD, 36));
		t.setX(106.0);
		t.setY(30.0);
		t.setTextAlignment(TextAlignment.CENTER);
		t.setEffect(new GaussianBlur(5.0));
		Reflection r = new Reflection();
		r.setFraction(0.9);
		t.setEffect(r);
		return t;
	}

	public static void main(String[] argv) {
		Application.launch(argv);
	}

}
