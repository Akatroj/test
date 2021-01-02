package agh.cs.lab.AnimalSimulator.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private static final int sceneH = 790;
    private static final int sceneW = 1020;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../Resources/MainLayout.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, sceneW, sceneH);
        scene.getStylesheets().add(getClass().getResource("../Resources/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(
                (WindowEvent t) -> {
                    Platform.exit();
                    System.exit(0);
                }
        );
        primaryStage.setResizable(false);
        primaryStage.setTitle("Animal Simulator");
        primaryStage.show();
    }
}