module MicEmu {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	
	opens micemu to javafx.graphics, javafx.fxml;
}
