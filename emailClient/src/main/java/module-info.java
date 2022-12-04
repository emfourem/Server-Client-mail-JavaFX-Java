module com.example.emailclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.project.emailclient to javafx.fxml;
    exports com.project.emailclient;
}