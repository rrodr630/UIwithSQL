package sample.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Artist {

    //Simple int and String because we want to take full advantage of data binding
    //javafx has a property field that cn be set to this variables
    private SimpleIntegerProperty id;
    private SimpleStringProperty name; //main.fxml line 20

    public Artist() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
    }

    public int getId() { return id.get(); }

    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }

    public void setName(String name) { this.name.set(name); }
}