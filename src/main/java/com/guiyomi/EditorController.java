package com.guiyomi;

import java.io.File;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

public class EditorController {

    @FXML
    private Button NewBtn;

    @FXML
    private Button LoadBtn;

    @FXML
    private Button QuitBtn;

    @FXML
    private Label Label;
    private String lastEditedFile;

    public void updateLastEditedFileLabel(String filePath) {
        lastEditedFile = filePath;
        
        if (filePath == null || filePath.isEmpty()) {
            Label.setText(" ");  
        } else {
            Label.setText(lastEditedFile);  
        }
    }

    @FXML
    private void handleNewFile(ActionEvent event) {
        TextEditor editor = new TextEditor(null, this);  
        editor.show();
        updateLastEditedFileLabel(" ");
    }
 
    @FXML
    private void handleLoadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            TextEditor editor = new TextEditor(file, this); 
            editor.show();
            updateLastEditedFileLabel(file.getName());  
        }
    }

    @FXML
    private void handleQuit(ActionEvent event) {
        Platform.exit();
    }
}
