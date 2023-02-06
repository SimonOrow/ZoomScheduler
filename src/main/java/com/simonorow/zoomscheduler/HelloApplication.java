package com.simonorow.zoomscheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Set initial scene.
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 914, 573);
        stage.setTitle("Zoom Scheduler");
        stage.setScene(scene);
        stage.show();

        // Load tableView.
        TableView tb = (TableView) scene.lookup("#participants");
        TableColumn firstNameCol = new TableColumn("Name");
        TableColumn lastNameCol = new TableColumn("Email");
        tb.getColumns().addAll(firstNameCol, lastNameCol);

        // Start web server.
        WebServer.getInstance().startServer();
    }

    public static void main(String[] args) {
        launch();
    }
}