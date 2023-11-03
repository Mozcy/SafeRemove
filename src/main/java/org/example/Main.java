package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.domain.FileModel;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Main extends Application {

    // 创建数据模型列表
    private final static ObservableList<FileModel> data = FXCollections.observableArrayList();


    public static void main(String[] args) {
        launch();   //启动JavaFX程序
    }

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();

        stage.setTitle("安全删除");
        Scene scene = new Scene(root, 500, 300);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/icons/waste100.png"));
        setLabel(root);
        stage.show();
    }

    private void setLabel(StackPane root) {
        Label label = new Label("拖拽文件或文件夹到此处");
        label.setPrefSize(500, 300);
        ImageView icon = new ImageView(new Image("icons/file100.png"));
        icon.setStyle("-fx-alignment: CENTER;");
        icon.setFitHeight(60);
        icon.setFitWidth(60);

        VBox vbox = new VBox(icon, label);
        vbox.setPrefSize(500, 300);
        vbox.setPadding(new javafx.geometry.Insets(100, 10, 10, 10));
        vbox.setAlignment(Pos.CENTER);
        label.setStyle("-fx-alignment: CENTER;");
        label.setPadding(new javafx.geometry.Insets(-80, 10, 10, 10));


        vbox.setOnDragOver(event -> {
            if (event.getGestureSource() != vbox && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        vbox.setOnDragEntered(event -> {
            if (event.getGestureSource() != vbox && event.getDragboard().hasFiles()) {
                label.setText("松开鼠标以完成拖拽");
            }
        });
        vbox.setOnDragExited(event -> {
            if (event.getGestureSource() != vbox && event.getDragboard().hasFiles()) {
                label.setText("拖拽文件到此处");
            }
        });
        vbox.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            event.setDropCompleted(true);
            event.consume();
            setTable(root, dragboard.getFiles());
        });

        root.getChildren().add(vbox);
    }

    private void setTable(StackPane root, List<File> fileList) {
        TableView<FileModel> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("请拖拽文件或文件夹"));
        tableView.setPrefHeight(270);

        tableView.setOnDragOver(event -> {
            if (event.getGestureSource() != tableView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        tableView.setOnDragDropped(event -> {
            for (File file : event.getDragboard().getFiles()) {
                boolean directory = file.isDirectory();
                FileModel fileModel = new FileModel(directory ? "文件夹" : "文件", file.getAbsolutePath());
                // 不一样的文件才放入数据列表中
                if (!data.stream().anyMatch(it -> it.getPath().equals(fileModel.getPath()))) {
                    // 添加数据到列表
                    data.add(fileModel);
                } else {
                }
            }
        });

        // 创建表格列
        TableColumn<FileModel, String> typeColumn = new TableColumn<>("类型");
        TableColumn<FileModel, Integer> pathColumn = new TableColumn<>("路径");
        TableColumn<FileModel, Integer> buttonColumn = new TableColumn<>("操作");
        typeColumn.setStyle("-fx-alignment: CENTER;");
        pathColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        buttonColumn.setStyle("-fx-alignment: CENTER;");
        // 设置表格宽度的权重比
        typeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.69));
        buttonColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));


        // 操作移除按钮, 点击移除当前行
        buttonColumn.setCellFactory(column -> new TableCell<FileModel, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Button deleteButton = new Button("移除");
                    deleteButton.setOnAction(event -> {
                        getTableView().getItems().remove(getIndex());
                    });
                    setGraphic(deleteButton);
                    setText(null);
                }
            }
        });

        // 设置表格列与数据模型属性的关联
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        buttonColumn.setCellValueFactory(new PropertyValueFactory<>("button"));

        // 将表格列添加到表格视图
        tableView.getColumns().addAll(typeColumn, pathColumn, buttonColumn);


        for (File file : fileList) {
            boolean directory = file.isDirectory();
            // 添加数据到列表
            data.add(new FileModel(directory ? "文件夹" : "文件", file.getAbsolutePath()));
        }

        // 将数据绑定到表格视图
        tableView.setItems(data);
        setDeleteAll(root, tableView);
    }

    private void setDeleteAll(StackPane root, TableView tableView) {
        Button button = new Button("安全删除");
        button.setPrefWidth(500);
        button.setPrefHeight(30);
        button.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        VBox vBox = new VBox(tableView, button);
        button.setOnAction(e -> {
            if (data.size() != 0) {
                Iterator<FileModel> iterator = data.stream().iterator();
                while (iterator.hasNext()) {
                    FileModel fileModel = iterator.next();
                    File file = new File(fileModel.getPath());
                    Delete.delete(file);
                }
                data.clear();
                System.out.println(data.size());
                tableView.refresh();
            } else {
                Alert dialog = new Alert(Alert.AlertType.WARNING);
                dialog.setTitle("消息");
                dialog.setHeaderText("警告");
                dialog.setContentText("请拖入需要删除的文件或文件夹");
                dialog.show();
            }
        });
        root.getChildren().add(vBox);
    }
}