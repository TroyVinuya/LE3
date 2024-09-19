package com.guiyomi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TextEditor {

    private Stage stage;
    private TextArea textArea;
    private File currentFile;
    private boolean isModified = false;

    private ComboBox<String> fontSizeSelector;
    private ComboBox<String> fontFamilySelector;
    private ColorPicker colorPicker;

    private EditorController controller;

    public TextEditor(File file, EditorController controller) {
        this.stage = new Stage();
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.textArea = new TextArea();
        this.controller = controller;  

        if (file != null) {
            loadFile(file);
            this.currentFile = file;
        }

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveFile());

        fontSizeSelector = new ComboBox<>();
        fontSizeSelector.getItems().addAll("10", "12", "14", "16", "18", "20", "24", "28", "32");
        fontSizeSelector.setValue("14");  
        fontSizeSelector.setOnAction(e -> updateTextStyle());

        fontFamilySelector = new ComboBox<>();
        fontFamilySelector.getItems().addAll("Arial", "Verdana", "Times New Roman", "Courier New", "Georgia");
        fontFamilySelector.setValue("Arial"); 
        fontFamilySelector.setOnAction(e -> updateTextStyle());

        colorPicker = new ColorPicker(Color.BLACK); 
        colorPicker.setOnAction(e -> updateTextStyle());

        HBox controlsLayout = new HBox(10, new Label("Font:"), fontFamilySelector, new Label("Size:"), fontSizeSelector, new Label("Color:"), colorPicker, saveButton);
        BorderPane layout = new BorderPane();
        layout.setTop(controlsLayout);
        layout.setCenter(textArea);

        Scene scene = new Scene(layout, 600, 400);
        this.stage.setScene(scene);

        this.stage.setOnCloseRequest(this::handleCloseRequest);

        this.textArea.textProperty().addListener((observable, oldValue, newValue) -> isModified = true);

        if (file == null) {
            this.stage.setTitle("New File");
        } else {
            this.stage.setTitle(file.getName());
        }
    }

    public void show() {
        this.stage.show();
    }

    private void updateTextStyle() {
        String selectedFontFamily = fontFamilySelector.getValue();
        int selectedFontSize = Integer.parseInt(fontSizeSelector.getValue());
        Color selectedColor = colorPicker.getValue();
        
        textArea.setFont(Font.font(selectedFontFamily, FontWeight.NORMAL, FontPosture.REGULAR, selectedFontSize));
        textArea.setStyle("-fx-text-fill: #" + selectedColor.toString().substring(2, 8) + ";");
    }

    void loadFile(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            this.textArea.setText(content);
        } catch (IOException e) {
            showAlert("Error", "Unable to open file.");
        }
    }

    private void saveFile() {
        if (this.currentFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            this.currentFile = fileChooser.showSaveDialog(this.stage);
        }

        if (this.currentFile != null) {
            try (FileWriter writer = new FileWriter(this.currentFile)) {
                writer.write(this.textArea.getText());
                this.isModified = false;
                this.stage.setTitle(this.currentFile.getName());
                showSuccessDialog();

                controller.updateLastEditedFileLabel(this.currentFile.getName());
            } catch (IOException e) {
                showAlert("Error", "Unable to save file.");
            }
        }
    }

    private void handleCloseRequest(WindowEvent event) {
        if (this.isModified) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes.");
            alert.setContentText("Do you want to save before closing?");
            ButtonType saveButton = new ButtonType("Save");
            ButtonType discardButton = new ButtonType("Discard");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    saveFile();
                } else if (result.get() == cancelButton) {
                    event.consume();  
                }
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
