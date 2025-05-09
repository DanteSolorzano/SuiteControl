module main.suitecontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens main.suitecontrol to javafx.fxml;
    //se agrego porque no cargaba los datos en listar clientes
    opens main.modelo to javafx.base;
    exports main.suitecontrol;
}