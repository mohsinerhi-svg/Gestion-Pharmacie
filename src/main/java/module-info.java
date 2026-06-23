module ma.sup {
    // Les modules JavaFX dont tu as besoin (d'après ton pom.xml)
    requires javafx.controls;
    requires javafx.fxml;
    
    // Nécessaire pour ton connecteur MySQL (JDBC)
    requires java.sql; 

    // Permet à JavaFX d'accéder à tes classes (pour lire les fichiers FXML)
    opens ma.sup to javafx.fxml;
    
    // Expose ton package
    exports ma.sup;
}