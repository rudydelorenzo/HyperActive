
package HDControl;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CustomListCell extends ListCell<ReplayIdentifier>{
    
    private Text name;
    private Text id;
    private VBox vb;
    private ImageView imgView;
    private HBox hb;
    
    public CustomListCell() {
        super();
        name = new Text();
        id = new Text();
        vb = new VBox(name, id);
        imgView = new ImageView();
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(35);
        hb = new HBox(6);
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.getChildren().addAll(imgView, vb);
    }
    
    @Override
    public void updateItem(ReplayIdentifier item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null || item.getName() == null) {
            setText(null);
            setGraphic(null);
        } else {
            vb.getStylesheets().add("CSS/ReplayListCellCSS.css");
            name.setText(item.getName());
            name.getStyleClass().add("text");
            id.setText("Replay ID: " + Integer.toString(item.getId()));
            id.getStyleClass().add("textSmall");
            if (item.isStarred()) {
                imgView.setImage(controlMain.starImg);
            } else {
                imgView.setImage(null);
            }
            
            setGraphic(hb);
        }
    }
}