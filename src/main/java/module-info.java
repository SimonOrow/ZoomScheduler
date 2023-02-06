module com.simonorow.zoomscheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;


    opens com.simonorow.zoomscheduler to javafx.fxml;
    exports com.simonorow.zoomscheduler;
}