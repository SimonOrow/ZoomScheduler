module com.simonorow.zoomscheduler {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.simonorow.zoomscheduler to javafx.fxml;
    exports com.simonorow.zoomscheduler;
}