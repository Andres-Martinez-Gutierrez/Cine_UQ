module uniquindio.edu.co.cineuq {
    requires javafx.controls;
    requires javafx.fxml;

    opens uniquindio.edu.co.cineuq to javafx.fxml;
    exports uniquindio.edu.co.cineuq;

    opens uniquindio.edu.co.cineuq.viewController to javafx.fxml;
    exports uniquindio.edu.co.cineuq.viewController;
}
