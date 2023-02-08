package com.simonorow.zoomscheduler;

import com.simonorow.zoomscheduler.Models.LettuceMeet.LettuceMeetResponse;
import com.simonorow.zoomscheduler.Models.TableTime;
import com.simonorow.zoomscheduler.Models.TableUser;
import com.simonorow.zoomscheduler.Models.ZoomMeeting.BasicMeetingInfo;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.simonorow.zoomscheduler.Zoom.GenerateMeetingDate;

public class MainApplication extends Application {

    static LettuceMeetResponse lettuceMeetResponse = null;

    @Override
    public void start(Stage stage) throws IOException {
        // Set initial scene.
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("mainUI.fxml"));
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

        Button scheduleButton = (Button) scene.lookup("#scheduleButton");
        scheduleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    beginScheduling(scene);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        String zoomID = System.getenv("ZOOM_ACCOUNT_ID");
        String clientID = System.getenv("ZOOM_CLIENT_ID");
        String clientSecret = System.getenv("ZOOM_CLIENT_SECRET");
        String sendGridAPIKey = System.getenv("SENDGRID_API_KEY");

        if(zoomID == null || clientID == null || clientSecret == null || sendGridAPIKey == null) {
            importAttendees.setDisable(true);
            scheduleButton.setDisable(true);
            infoAlert("Environment variables are missing. Please add missing variables and restart the application.");
        }
    }

    public static void importAttendees(Scene scene) {
        TextField lettuceMeetTextField = (TextField) scene.lookup("#lettucemeetlink");

        if(lettuceMeetTextField.getText().isEmpty()) {
            infoAlert("Please enter a link.");
            return;
        }

        new Thread(() -> {
            try {
                LettuceMeetResponse response = LettuceMeet.getSchedule(lettuceMeetTextField.getText());
                lettuceMeetResponse = response;
                Map<String, String> users = LettuceMeet.getUsers(response);
                fillParticipantsTable(scene, users);


                Scheduler scheduler = new Scheduler();
                scheduler.findOptimalTime(response);
                fillTimeTable(scene, MapUtil.sortByValue(scheduler.getAvailabilityTimes()));

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

    // Show an Information Alert with header Text
    private static void infoAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Zoom Scheduler");
        alert.setHeaderText("Information");
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void beginScheduling(Scene scene) throws Exception {
        TableView timesTable = (TableView) scene.lookup("#availabilityTable");
        TableTime time = (TableTime) timesTable.getSelectionModel().getSelectedItem();

        Map<String, String> users = LettuceMeet.getUsers(lettuceMeetResponse);
        String meetDate = lettuceMeetResponse.data.event.pollDates.get(0);
        Date date = GenerateMeetingDate(meetDate, String.valueOf(time.getTime()));
        System.out.println("Date Generated: " + date);

        if(time == null || users == null) {
            infoAlert("Please ensure data is loaded and you have selected a time.");
            return;
        }

        String zoomToken = Zoom.ZoomToken();
        String zoomUserId = Zoom.getUserId(zoomToken);
        BasicMeetingInfo basicMeetingInfo = Zoom.scheduleMeeting(zoomToken, zoomUserId, date);
        SendEmail.sendEmail(users, basicMeetingInfo);
        infoAlert("The meeting has been scheduled and all emails have been sent.");
    }

    public void exitProgram(ActionEvent actionEvent) {
       System.exit(0);
    }

    public void about(ActionEvent actionEvent) {
        infoAlert("Made By Simon Orow\n\nFor the DeveloperWeek 2023 Hackathon");
    }

    public static void main(String[] args) {
        launch();
    }
}