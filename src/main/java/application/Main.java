package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/AboutUs.fxml"));
            root.setStyle("-fx-background-color: #241920;");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("All-In-One-Checker");
            primaryStage.setResizable(false);
            Image mainIcon = new Image(getClass().getResourceAsStream("/Images/MailLogo.png"));
            primaryStage.getIcons().add(mainIcon);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}