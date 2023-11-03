package org.example.domain;

import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileModel {
    private String type;
    private String path;
}
