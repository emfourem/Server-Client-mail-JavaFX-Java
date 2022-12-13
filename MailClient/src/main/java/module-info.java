module com.example.emailclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.project.emailclient to javafx.fxml;
    exports com.project.emailclient;
  exports com.project.emailclient.controller;
  opens com.project.emailclient.controller to javafx.fxml;
}