module yoanemoudilou.cahiertexte {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires jbcrypt;
    requires kernel;
    requires layout;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires mysql.connector.j;

    opens yoanemoudilou.cahiertexte.ui.login to javafx.fxml;
    opens yoanemoudilou.cahiertexte.ui.dashboard to javafx.fxml;
    opens yoanemoudilou.cahiertexte to javafx.fxml;
    exports yoanemoudilou.cahiertexte;
}