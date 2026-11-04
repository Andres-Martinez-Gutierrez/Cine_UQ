module uniquindio.edu.co.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens uniquindio.edu.co.demo to javafx.fxml;
    exports uniquindio.edu.co.demo;
}