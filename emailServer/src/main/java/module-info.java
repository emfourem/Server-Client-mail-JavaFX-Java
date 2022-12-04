module com.project.emailserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.project.emailserver to javafx.fxml;
    exports com.project.emailserver;
}