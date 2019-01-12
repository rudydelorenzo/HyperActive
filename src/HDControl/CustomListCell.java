
package HDControl;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CustomListCell extends ListCell<ReplayIdentifier>{
    
    private Text name;
    private Text id;
    private VBox vb;
    
    public CustomListCell() {
        super();
        name = new Text();
        id = new Text();
        vb = new VBox(name, id);
    }
    
    @Override
    public void updateItem(ReplayIdentifier item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null || item.getName() == null) {
            setText(null);
            setGraphic(null);
        } else {
            name.setText(item.getName());
            name.setFont(controlMain.prodSansBig);
            name.setStyle("-fx-font-size:30");
            id.setText("Replay ID: " + Integer.toString(item.getId()));
            id.setFont(controlMain.prodSansBig);
            id.setStyle("-fx-font-size:15");
            setGraphic(vb);
        }
    }
}
