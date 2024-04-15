module MicEmu {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.base;
	
	opens micemu to javafx.graphics, javafx.fxml;
}
