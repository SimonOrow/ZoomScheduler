package com.simonorow.zoomscheduler;

import com.simonorow.zoomscheduler.Models.LettuceMeet.LettuceMeetResponse;
import com.simonorow.zoomscheduler.Models.TableUser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

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
        TableColumn<TableUser, String> column1 = new TableColumn<>("Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<TableUser, String> column2 = new TableColumn<>("Email");
        column2.setCellValueFactory(new PropertyValueFactory<>("email"));
        column1.prefWidthProperty().bind(tb.widthProperty().divide(2)); // w * 1/2
        column2.prefWidthProperty().bind(tb.widthProperty().divide(2)); // w * 1/2

        tb.getColumns().add(column1);
        tb.getColumns().add(column2);


        Button importAttendees = (Button) scene.lookup("#importattendees");
        importAttendees.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               importAttendees(scene);
            }
        });

    }

    public static void importAttendees(Scene scene) {
        TextField lettuceMeetTextField = (TextField) scene.lookup("#lettucemeetlink");
        new Thread(() -> {
            try {
                LettuceMeetResponse response = LettuceMeet.getSchedule(lettuceMeetTextField.getText());
                Scheduler scheduler = new Scheduler();
                scheduler.findOptimalTime(response);
                System.out.println(scheduler.getAvailabilityTimes());

                Map<String, String> users = LettuceMeet.getUsers(response);
                fillTable(scene, users);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    public static void fillTable(Scene scene, Map<String, String> users) {
        TableView tb = (TableView) scene.lookup("#participants");
        for (Map.Entry<String, String> entry : users.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            tb.getItems().add(new TableUser(key, value));
        }
    }

    public static void main(String[] args) {
        launch();
    }
}