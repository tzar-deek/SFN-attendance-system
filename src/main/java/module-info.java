module com.skillsfusionnetwork.student_attendance_sys {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires atlantafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.skillsfusionnetwork.student_attendance_sys to javafx.fxml;
    exports com.skillsfusionnetwork.student_attendance_sys;
}