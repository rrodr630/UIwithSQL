package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Model.DataSource;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        //We need to have the data ready before program starts or at the start of the program
        Controller controller = loader.getController();
        controller.listArtist();

        primaryStage.setTitle("Music Database");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        if( !DataSource.getInstance().open() ) {
            System.out.println("FATAL ERROR: Couldn't connect to database.");
            Platform.exit(); //closes UI if program couldn't connect to database at starting
        }
    }

    @Override
    public void stop() throws Exception {
        DataSource.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
