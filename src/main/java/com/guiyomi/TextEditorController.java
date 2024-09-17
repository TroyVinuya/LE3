package com.guiyomi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;

public class TextEditorController extends Application {
    private File currentFile = null;
    private TextArea textArea = new TextArea();
    private boolean isModified = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simple Text Editor");

        Button newFileButton = new Button("New File");
        Button loadFileButton = new Button("Load File");
        Button quitButton = new Button("Quit");

        newFileButton.setOnAction(e -> newFile(primaryStage));
        loadFileButton.setOnAction(e -> loadFile(primaryStage));
        quitButton.setOnAction(e -> quit(primaryStage));

        VBox vbox = new VBox(10, newFileButton, loadFileButton, quitButton);
        Scene scene = new Scene(vbox, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void newFile(Stage primaryStage) {
        if (promptToSave(primaryStage)) {
            openEditorWindow(primaryStage);
            textArea.clear();
            currentFile = null;
        }
    }

    private void loadFile(Stage primaryStage) {
        if (promptToSave(primaryStage)) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null) {
                openEditorWindow(primaryStage);
                try {
                    textArea.setText(Files.readString(file.toPath()));
                    currentFile = file;
                    isModified = false;
                } catch (IOException e) {
                    showAlert("Error", "Unable to open file");
                }
            }
        }
    }

    private void quit(Stage primaryStage) {
        if (promptToSave(primaryStage)) {
            System.exit(0);
        }
    }

    private boolean promptToSave(Stage primaryStage) {
        if (!isModified) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes.");
        alert.setContentText("Do you want to save before exiting?");
        ButtonType saveButton = new ButtonType("Save");
        ButtonType discardButton = new ButtonType("Discard");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == saveButton) {
                saveFile(primaryStage);
                return true;
            } else if (result.get() == discardButton) {
                return true;
            }
        }

        return false;
    }

    private void openEditorWindow(Stage primaryStage) {
        Stage editorStage = new Stage();
        textArea.setOnKeyTyped(e -> isModified = true);

        Button saveButton = new Button("Save");

        saveButton.setOnAction(e -> saveFile(editorStage));

        VBox editorLayout = new VBox(10, textArea, saveButton);
        Scene editorScene = new Scene(editorLayout, 400, 400);

        editorStage.setScene(editorScene);
        editorStage.setTitle("Text Editor");
        editorStage.show();

        editorStage.setOnCloseRequest((WindowEvent event) -> {
            if (!promptToSave(primaryStage)) {
                event.consume();
            }
        });

    }

    private void saveFile(Stage stage) {
        if (currentFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                currentFile = file;
            }
        }

        if (currentFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                writer.write(textArea.getText());
                isModified = false;

                showSuccessDialog();
            } catch (IOException e) {
                showAlert("Error", "Unable to save file");
            }
        }
    }
    private void showSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Save");
        alert.setHeaderText(null);
        alert.setContentText("File was saved successfully!");
        alert.showAndWait();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}