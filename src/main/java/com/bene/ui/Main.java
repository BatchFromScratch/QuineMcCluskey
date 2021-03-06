package com.bene.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * Erstellt das JavaFX-Panel
     *
     * @param primaryStage Die Instanz, die das Fenster darstellt
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Quine-McCluskey-Verfahren");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();
    }

    /**
     * Startet den Ablauf
     */
    public static void main(String[] args) {
        launch(args);
    }
}
