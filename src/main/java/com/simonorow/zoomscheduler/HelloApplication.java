package com.simonorow.zoomscheduler;

import com.simonorow.zoomscheduler.Models.LettuceMeet.LettuceMeetResponse;
import com.simonorow.zoomscheduler.Models.TableTime;
import com.simonorow.zoomscheduler.Models.TableUser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        TableView usersTable = (TableView) scene.lookup("#participants");
        TableColumn<TableUser, String> column1 = new TableColumn<>("Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<TableUser, String> column2 = new TableColumn<>("Email");
        column2.setCellValueFactory(new PropertyValueFactory<>("email"));
        column1.prefWidthProperty().bind(usersTable.widthProperty().divide(2)); // w * 1/2
        column2.prefWidthProperty().bind(usersTable.widthProperty().divide(2)); // w * 1/2
        usersTable.getColumns().add(column1);
        usersTable.getColumns().add(column2);


        TableView timesTable = (TableView) scene.lookup("#availabilityTable");
        TableColumn<TableTime, String> timeColumn1 = new TableColumn<>("Time");
        timeColumn1.setCellValueFactory(new PropertyValueFactory<>("time"));
        TableColumn<TableTime, String> timeColumn2 = new TableColumn<>("Number of available participants");
        timeColumn2.setCellValueFactory(new PropertyValueFactory<>("count"));
        timeColumn1.prefWidthProperty().bind(timesTable.widthProperty().divide(2)); // w * 1/2
        timeColumn2.prefWidthProperty().bind(timesTable.widthProperty().divide(2)); // w * 1/2
        timesTable.getColumns().add(timeColumn1);
        timesTable.getColumns().add(timeColumn2);




        Button importAttendees = (Button) scene.lookup("#importattendees");
        importAttendees.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               importAttendees(scene);
            }
        });

    }

    public static void importAttendees(Scene scene) {
        TextField lettuceMeetTextField = (TextField) scene.lookup("#lettucemeetlink");

        if(lettuceMeetTextField.getText().isEmpty()) {
            showAlertWithHeaderText("Please enter a link.");
            return;
        }

        new Thread(() -> {
            try {
                LettuceMeetResponse response = LettuceMeet.getSchedule(lettuceMeetTextField.getText());

                Map<String, String> users = LettuceMeet.getUsers(response);
                fillParticipantsTable(scene, users);


                Scheduler scheduler = new Scheduler();
                scheduler.findOptimalTime(response);
                fillTimeTable(scene, scheduler.getAvailabilityTimes());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    public static void fillParticipantsTable(Scene scene, Map<String, String> users) {
        TableView tb = (TableView) scene.lookup("#participants");
        for (Map.Entry<String, String> entry : users.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            tb.getItems().add(new TableUser(key, value));
        }
    }

    public static void fillTimeTable(Scene scene, Map<String, Integer> times) {
        TableView timesTable = (TableView) scene.lookup("#availabilityTable");
        for (Map.Entry<String, Integer> entry : times.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            TableTime tableTime = new TableTime(String.valueOf(key), String.valueOf(value));
            timesTable.getItems().add(tableTime);
        }
    }

    // Show a Information Alert with header Text
    private static void showAlertWithHeaderText(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Zoom Scheduler");
        alert.setHeaderText("Information");
        alert.setContentText(text);

        alert.showAndWait();
    }


    public static void main(String[] args) {
        launch();
    }
}