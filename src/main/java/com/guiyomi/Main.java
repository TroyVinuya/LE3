package com.guiyomi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("TextEditor.fxml"));
        primaryStage.setTitle(" ");
        primaryStage.setScene(new Scene(root, 200, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
