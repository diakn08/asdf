module org.example.excelproject1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.excelproject1 to javafx.fxml;
    exports org.example.excelproject1;
}