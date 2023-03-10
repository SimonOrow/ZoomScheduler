module com.simonorow.zoomscheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;
    requires com.google.gson;

    opens com.simonorow.zoomscheduler to javafx.fxml;
    opens com.simonorow.zoomscheduler.Models to com.google.gson, javafx.base;
    exports com.simonorow.zoomscheduler;
    opens com.simonorow.zoomscheduler.Models.LettuceMeet to com.google.gson;
    opens com.simonorow.zoomscheduler.Models.ZoomGetUsers to com.google.gson;
    opens com.simonorow.zoomscheduler.Models.ZoomMeeting to com.google.gson;
    opens com.simonorow.zoomscheduler.Models.SendGridRequest to com.google.gson;
}