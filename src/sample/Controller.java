package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import sample.Model.Album;
import sample.Model.Artist;
import sample.Model.DataSource;
import sample.Model.UpdateNameController;

import java.io.IOException;
import java.util.Optional;

public class Controller {

    @FXML
    private TableView artistTable; //table view can contain albums and artists, this is more flexible, but more dangerous to hacks

    @FXML
    //will not show an actual progress (bar filling) because data is being queried in a different class,
    //however it does show that the program is working and just waiting for data
    private ProgressBar progressBar;

    @FXML
    //we need this to make it the owner of the dialog box when clicked on update artist (this will block any actions until box is close)
    private BorderPane mainBorderPane;

    @FXML
    public void listArtist() {
        Task<ObservableList<Artist>> task = new GetAllArtistTask(); //initializes the task to get data from DB
        artistTable.itemsProperty().bind(task.valueProperty()); //binds the "downloaded" data from DB to artist list

        //show a progress bar while querying list
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setVisible(true);

        // Hide progress bar when done with task
        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();
    }


    public void listAlbumsForArtist() {
        final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
        if (artist == null) {
            System.out.println("No artist selected.");
            return;
        }

        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(
                        DataSource.getInstance().queryAlbumsForArtistId(artist.getId()));
            }
        };
        artistTable.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

    /**
     * The only thing is doing now is updating the 3rd element in list (position 2) AC DC for AC/DC. Change it so that
     * after selecting an artist and clicking the update button a dialog box pops-up asking to input the name and set
     * that new name as the new updated name.
     */
    public void updateArtist() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow()); //if dialog open, then you have to close it b4 doing anything else

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("updateName.fxml"));

        try {

            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch(IOException e) {
            System.out.println("Couldn't load the dialog. " + e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("OK pressed.");
            UpdateNameController updateNameController = fxmlLoader.getController();

            //In case selected item is an album
            if(  !(artistTable.getSelectionModel().getSelectedItem() instanceof Artist)  ) {
                System.out.println("Not an artist");
                listArtist();
                return;
            }

            final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();

            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return DataSource.getInstance().updateArtistName(artist.getId(), updateNameController.processResult());
                }
            };

            task.setOnSucceeded(e -> {
                if (task.valueProperty().get()) {
                    artist.setName(updateNameController.processResult());
                    artistTable.refresh();
                }
            });
            new Thread(task).start();
        }
        else
            System.out.println("Cancel pressed.");
    }
}// end of Controller class

/**
 * Get all artists (data from database) is a long task that may leave the UI interrupted for seconds, even minutes, now
 * because we dont want to make the user wait or not do something else at the same time tht we get the data from the database
 * we will be implementing a thread that is supported by a class in javafx (Task). In conclusion this class represents the long
 * task that a thread will be created from in order to not leave the UI interrupted.
 */
class GetAllArtistTask extends Task {

    //The main reason why we are creating this class is because we will be using this task in different places
    //mainly when we start program, but also when the user explicitly asks (data binding)
    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(
                DataSource.getInstance().queryArtist(DataSource.ORDER_BY_ASC)); //queryArtist returns a List<Artist> which is automatically converted into an observable list
    }
}