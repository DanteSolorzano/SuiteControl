module main.suitecontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens main.suitecontrol to javafx.fxml;
    exports main.suitecontrol;
}