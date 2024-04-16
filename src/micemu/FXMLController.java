package micemu;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class FXMLController {
	@FXML
	private ListView<Mixer.Info> audioInputList;
	@FXML
	private ToggleButton runToggle;
	@FXML
	private Circle recordingLight;

	private static String fileName = "E:\\test.wav";
	private final int BUFFER_SIZE = 128000;
	private AudioInputStream audioStream;
	private AudioFormat audioFormat;
	private SourceDataLine sourceLine;
	private Mixer mixer;

	public void initialize() {
		System.out.println("Controller is working");
		fillMixersList();
		addToggleFunc();
	}

	private void addToggleFunc() {
		runToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				System.out.println("Toggle Button is ON");
				updateLight(Color.GREEN);
				playSound();

				// audioFormat = audioStream.getFormat();
				// DataLine.Info infoIn = new DataLine.Info(SourceDataLine.class, audioFormat);

			} else {
				System.out.println("Toggle Button is OFF");
				updateLight(Color.RED);
			}
		});
	}

	private void playSound() {
		try {
			File file = new File(fileName);
			audioStream = AudioSystem.getAudioInputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		audioFormat = audioStream.getFormat();
		DataLine.Info infoIn = new DataLine.Info(SourceDataLine.class, audioFormat);

		try {
			sourceLine = (SourceDataLine) mixer.getLine(infoIn);
			sourceLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}

		sourceLine.start();
		int nBytesRead = 0;
		byte[] abData = new byte[BUFFER_SIZE];
		while (nBytesRead != -1) {
			try {
				nBytesRead = audioStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				@SuppressWarnings("unused")
				int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
			}
		}
		sourceLine.drain();
		sourceLine.close();
	}

	private void updateLight(Paint paint) {
		recordingLight.setFill(paint);
	}

	private void fillMixersList() {
		ObservableList<Mixer.Info> availableMixers = FXCollections.observableArrayList();
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		int defaultMixerIndex = 0;

		for (int i = 0; i < mixerInfos.length; i++) {
			availableMixers.add(mixerInfos[i]);
			if (mixerInfos[i].getName().equals("CABLE Input (VB-Audio Virtual Cable)")) {
				defaultMixerIndex = i;
			}
		}

		audioInputList.setItems(availableMixers);
		audioInputList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
		audioInputList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				mixer = AudioSystem.getMixer(newValue);
			}
		});
		audioInputList.getSelectionModel().select(defaultMixerIndex);
	}
}
