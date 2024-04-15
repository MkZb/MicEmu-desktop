package micemu;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Line.Info;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class FXMLController {
	@FXML
	private ListView<String> audioInputList;

	public void initialize() {
		System.out.println("Controller is working");
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		ObservableList<String> items = FXCollections.observableArrayList();
		
		for (Mixer.Info info : mixerInfos) {
			System.out.println(info);
			items.add(info.toString());
			// System.out.println("Mixer: " + info.getName());
			Mixer mixer = AudioSystem.getMixer(info);

			Line.Info[] sourceLineInfos = mixer.getSourceLineInfo();
			Line.Info[] targetLineInfos = mixer.getTargetLineInfo();

			System.out.println(" Source Lines:");
			printLineInfo(sourceLineInfos);

			System.out.println(" Target Lines:");
			printLineInfo(targetLineInfos);

			System.out.println();
		}
		
		audioInputList.setItems(items);
		audioInputList.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
		audioInputList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected: " + newValue);
            }
        });
	}
	

	private static void printLineInfo(Info[] lineInfos) {
		for (Line.Info lineInfo : lineInfos) {
			System.out.println("    " + lineInfo);
		}
	}
}
