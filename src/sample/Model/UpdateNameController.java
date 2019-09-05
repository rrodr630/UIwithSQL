package sample.Model;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class UpdateNameController {

    @FXML
    private TextField name;

    public String processResult() {
        return name.getText().trim();
    }

}
