module MicEmu {
	requires javafx.controls;
	
	opens micemu to javafx.graphics, javafx.fxml;
}
