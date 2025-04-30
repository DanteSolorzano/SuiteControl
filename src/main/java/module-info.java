module main.suitecontrol {
    requires javafx.controls;
    requires javafx.fxml;


    opens main.suitecontrol to javafx.fxml;
    exports main.suitecontrol;
}