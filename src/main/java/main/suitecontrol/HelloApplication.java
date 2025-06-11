package main.suitecontrol;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.ResourceBundle.ConfigProperties;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Locale locale = ConfigProperties.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("labels", locale);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        fxmlLoader.setResources(bundle); //  Esto es lo importante
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("SuiteControl");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();

    }
}
