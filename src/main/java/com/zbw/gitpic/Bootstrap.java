package com.zbw.gitpic;

import com.zbw.gitpic.utils.ThreadPool;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author zbw
 * @create 2018/2/24 17:28
 */
public class Bootstrap extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        final Scene scene = new Scene(root, 400, 400);
        scene.getStylesheets().add(Bootstrap.class.getResource("/css/jfoenix-components.css").toExternalForm());
        scene.getStylesheets().add(Bootstrap.class.getResource("/css/jfoenix-components-style.css").toExternalForm());
        primaryStage.setTitle("gitPic");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(Bootstrap.class.getClassLoader().getResourceAsStream("images/github_logo.png")));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        ThreadPool.getInstance().shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
