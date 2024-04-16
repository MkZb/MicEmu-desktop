package micemu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

	private final int BUFFER_SIZE = 1024;
	private AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, false);
	private SourceDataLine sourceLine;
	private Mixer mixer;

	int serverPort = 32228;
	DatagramSocket socket;

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
				openSocket();
			} else {
				System.out.println("Toggle Button is OFF");
				updateLight(Color.RED);
				closeSocket();
			}
		});
	}

	private void recievePackets() {
		Task<Void> reciveAndPlayAudioTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				DataLine.Info infoIn = new DataLine.Info(SourceDataLine.class, audioFormat);

				try {
					sourceLine = (SourceDataLine) mixer.getLine(infoIn);
					sourceLine.open(audioFormat);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
					System.exit(1);
				}

				sourceLine.start();

				byte[] buffer = new byte[BUFFER_SIZE];

				try {
					while (true) {
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(packet);
						sourceLine.write(packet.getData(), 0, packet.getLength());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		};

		new Thread(reciveAndPlayAudioTask).start();
	}

	private void openSocket() {
		try {
			socket = new DatagramSocket(serverPort);
			recievePackets();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeSocket() {
		if (socket != null) {
			socket.close();
		}
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
