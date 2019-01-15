package HDControl;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javafx.animation.*;
import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.Duration;

public class controlMain extends Application implements EventHandler<ActionEvent>, RefreshListener {
    
    public static final String version = "0.0.1a";
    public static Stage primaryStage;
    public static Socket client;
    public static Scene mainScene;
    public static InetAddress ip;
    public static ArrayList<Hyperdeck> hyperdecks = new ArrayList();
    public static Background recRed = new Background(new BackgroundFill(Color.web("#d32f2f"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background playBlue = new Background(new BackgroundFill(Color.web("#1976d2"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background unkPurple = new Background(new BackgroundFill(Color.web("#6a1b9a"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background stopGray = new Background(new BackgroundFill(Color.web("#757575"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background bottomGray = new Background(new BackgroundFill(Color.web("#b0bec5"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background defaultBg = new Background(new BackgroundFill(Color.web("#f0f0f0"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Font prodSansBig, prodSansSmall;
    public static BorderPane mainLayout = new BorderPane();
    public static GridPane topGrid;
    public static ColumnConstraints sbarWidth = new ColumnConstraints();
    public static RowConstraints statusBarItemHeight = new RowConstraints();
    public static DropShadow dropShadow = new DropShadow();
    public static DropShadow dropShadowBottomOnly = new DropShadow();
    public static Image stopImg, playImg, prevImg, pauseImg, recImg, errImg, shuttleImg, noConnectionImg, addImg, starImg, playBlackImg, stopBlackImg, recBlackImg, skipFwdBlackImg, skipBackBlackImg, revBlackImg, fwdBlackImg;
    public static Button s1Button, s2Button, recallButton, saveButton, editButton, deleteButton, clearButton, recordButton, stopButton, playButton, nextClip, prevClip, fwdButton, revButton, custom1Button, custom2Button, addHDButton;
    public static Button spd25, spd50, spd75, spd100, spd200, spd800, spd1600;
    public static ToggleButton starToggle, reverseToggle, fwdToggle;
    public static ObservableList<ReplayIdentifier> replaysList= FXCollections.observableArrayList();
    public static int currId = 0;
    public static ListView lv;
    public static double xOffset = 0;
    public static double yOffset = 0;
    public static HBox centerHolder = new HBox(10);
    
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        try {
            prodSansBig = Font.loadFont(new FileInputStream(new File("Product-Sans-Regular.ttf")), 40);
            prodSansSmall = Font.loadFont(new FileInputStream(new File("Product-Sans-Regular.ttf")), 15);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        stopImg = new Image("file:images/stop.png");
        recImg = new Image("file:images/rec.png");
        playImg = new Image("file:images/play.png");
        prevImg = new Image("file:images/preview.png");
        pauseImg = new Image("file:images/pause.png");
        errImg = new Image("file:images/err.png");
        shuttleImg = new Image("file:images/shuttle.png");
        starImg = new Image("file:images/star.png");
        noConnectionImg = new Image("file:images/noconnection2.png");
        addImg = new Image("file:images/add.png");
        playBlackImg = new Image("file:images/playBlack.png");
        stopBlackImg = new Image("file:images/stopBlack.png");
        skipFwdBlackImg = new Image("file:images/skipFwd.png");
        skipBackBlackImg = new Image("file:images/skipBack.png");
        recBlackImg = new Image("file:images/recordBlack.png");
        revBlackImg = new Image("file:images/reverseBlack.png");
        fwdBlackImg = new Image("file:images/forwardBlack.png");
        
        sbarWidth.setPercentWidth(100);
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("HyperActive " + version);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        
        makeUI();
        
        mainScene = new Scene(mainLayout, 1600, 900);
        mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                //replaysList.set(0, new ReplayIdentifier(replaysList.get(0).getId(), "newo-nameo"));
            }
        });     
        
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    
    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == s1Button) switchToSlot(1);
        else if (event.getSource() == s2Button) switchToSlot(2);
        else if (event.getSource() == recallButton) recallReplay();
        else if (event.getSource() == saveButton) makeReplay();
        else if (event.getSource() == editButton) editReplayName();
        else if (event.getSource() == starToggle) replayToggleStar();
        else if (event.getSource() == deleteButton) removeReplay();
        else if (event.getSource() == clearButton) clearReplays();
        else if (event.getSource() == recordButton) startRecord();
        else if (event.getSource() == stopButton) sayStop();
        else if (event.getSource() == playButton) sayPlay();
        else if (event.getSource() == nextClip) nextClip();
        else if (event.getSource() == prevClip) prevClip();
        else if (event.getSource() == spd25) shuttleSpeed(25);
        else if (event.getSource() == spd50) shuttleSpeed(50);
        else if (event.getSource() == spd75) shuttleSpeed(75);
        else if (event.getSource() == spd100) shuttleSpeed(100);
        else if (event.getSource() == spd200) shuttleSpeed(200);
        else if (event.getSource() == spd800) shuttleSpeed(800);
        else if (event.getSource() == spd1600) shuttleSpeed(1600);
        
    }
    
    public void makeUI() {
        createBottom();
        createControlButtons();
        createReplayList();
        createStatusBar();
    }
    
    public void createStatusBar() {
        
        sbarWidth.setPercentWidth(100);
        
        statusBarItemHeight.setPrefHeight(100);
                
        topGrid = new GridPane();
        if (hyperdecks.isEmpty()) {
            Label nohdlabel = new Label("No hyperdecks connected.".toUpperCase());
            Label connectBelowLabel = new Label("Connect one below.".toUpperCase());
            nohdlabel.setFont(prodSansBig);
            connectBelowLabel.setFont(prodSansSmall);
            VBox vb = new VBox(nohdlabel);
            vb.getChildren().add(connectBelowLabel);
            ImageView iv = new ImageView();
            iv.setPreserveRatio(true);
            iv.setFitHeight(70);
            HBox hb = new HBox(15);
            hb.getChildren().add(iv);
            hb.getChildren().add(vb);
            StackPane sp = new StackPane(hb);

            sp.setMargin(hb, new Insets(12));
            sp.setBackground(unkPurple);
            nohdlabel.setTextFill(Color.WHITE);
            connectBelowLabel.setTextFill(Color.WHITE);
            iv.setImage(noConnectionImg);

            sp.setAlignment(Pos.CENTER_LEFT);
            topGrid.add(sp, 0, 0);
            topGrid.getColumnConstraints().addAll(sbarWidth);
        } else {
            for (int i = 0; i<hyperdecks.size(); i++) {
                topGrid.add(hyperdecks.get(i).getStackPane(), i, 0);
                topGrid.getColumnConstraints().addAll(sbarWidth);
            }
        }
        
        topGrid.getRowConstraints().add(statusBarItemHeight);
        //topGrid.setGridLinesVisible(true);
        topGrid.setAlignment(Pos.CENTER);
        
        dropShadow.setRadius(15.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.GRAY);
        
        topGrid.setEffect(dropShadow);
        
        topGrid.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        topGrid.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (primaryStage.maximizedProperty().getValue() == false) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
                
            }
        });
        topGrid.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        if (primaryStage.maximizedProperty().getValue() == true) {
                            primaryStage.setMaximized(false);
                        } else {
                            primaryStage.setMaximized(true);
                        }
                    }
                }
            }
        });
        
        mainLayout.setTop(topGrid);
        
    }
    
    public void createControlButtons() {
        
        centerHolder = new HBox(10);
        
        ColumnConstraints colsLeft = new ColumnConstraints();
        colsLeft.setPercentWidth(100);
        ColumnConstraints colsFwdRev = new ColumnConstraints();
        colsFwdRev.setPercentWidth(70);
        RowConstraints rowsLeft = new RowConstraints();
        rowsLeft.setPercentHeight(100);
        //left column
        s1Button = new Button("S1");
        s2Button = new Button("S2");
        recallButton = new Button("RECALL");
        saveButton = new Button("SAVE");
        editButton = new Button("EDIT");
        starToggle = new ToggleButton("STAR");
        
        deleteButton = new Button("DELETE");
        clearButton = new Button("CLEAR");
        custom1Button = new Button("C1");
        custom2Button = new Button("C2");
        
        s1Button.setOnAction(this);
        s2Button.setOnAction(this);
        recallButton.setOnAction(this);
        saveButton.setOnAction(this);
        editButton.setOnAction(this);
        starToggle.setOnAction(this);
        
        deleteButton.setOnAction(this);
        clearButton.setOnAction(this);
        custom1Button.setOnAction(this);
        custom2Button.setOnAction(this);
        
        GridPane leftGrid = new GridPane();
        leftGrid.add(s1Button, 0, 0);
        leftGrid.add(s2Button, 1, 0); 
        leftGrid.add(recallButton, 0, 1, 2, 1);
        leftGrid.add(saveButton, 0, 2, 2, 1);
        leftGrid.add(editButton, 0, 3, 2, 1);
        leftGrid.add(starToggle, 0, 4, 2, 1);
        leftGrid.add(deleteButton, 0, 6, 2, 1);
        leftGrid.add(clearButton, 0, 7, 2, 1);
        
        for (int i = 0; i<leftGrid.getChildren().size(); i++) {
            if (leftGrid.getChildren().get(i) instanceof Button) {
                Button tempButton = (Button)(leftGrid.getChildren().get(i));
                tempButton.setFont(prodSansBig);
                tempButton.setStyle("-fx-font-size:30");
                tempButton.setMaxWidth(Double.MAX_VALUE);
                //tempButton.setMaxHeight(Double.MAX_VALUE);
                leftGrid.getRowConstraints().add(rowsLeft);
            } else if (leftGrid.getChildren().get(i) instanceof ToggleButton){
                ToggleButton tempButton = (ToggleButton)(leftGrid.getChildren().get(i));
                tempButton.setFont(prodSansBig);
                tempButton.setStyle("-fx-font-size:30");
                tempButton.setMaxWidth(Double.MAX_VALUE);
                //tempButton.setMaxHeight(Double.MAX_VALUE);
                leftGrid.getRowConstraints().add(rowsLeft);
            }
        }
        
        leftGrid.getColumnConstraints().add(colsLeft);
        leftGrid.getColumnConstraints().add(colsLeft);
        leftGrid.setPadding(new Insets(30,0,30,0));
        leftGrid.setAlignment(Pos.CENTER);
        
        //center column
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        recordButton = new Button("RECORD");
        stopButton = new Button("STOP");
        playButton = new Button("PLAY");
        nextClip = new Button("FWD CLIP");
        prevClip = new Button("BACK CLIP");
        
        recordButton.setOnAction(this);
        stopButton.setOnAction(this);
        playButton.setOnAction(this);
        nextClip.setOnAction(this);
        prevClip.setOnAction(this);
        
        GridPane mainControls = new GridPane();
        mainControls.add(recordButton, 0, 0);
        mainControls.add(stopButton, 0, 1);
        mainControls.add(playButton, 0, 2);
        mainControls.add(nextClip, 0, 3);
        mainControls.add(prevClip, 0, 4);
        
        recordButton.setGraphic(new ImageView(recBlackImg));
        stopButton.setGraphic(new ImageView(stopBlackImg));
        playButton.setGraphic(new ImageView(playBlackImg));
        nextClip.setGraphic(new ImageView(skipFwdBlackImg));
        prevClip.setGraphic(new ImageView(skipBackBlackImg));
        
        for (int i = 0; i<mainControls.getChildren().size(); i++) {
            Button tempButton = (Button)(mainControls.getChildren().get(i));
            ((ImageView)tempButton.getGraphic()).setPreserveRatio(true);
            ((ImageView)tempButton.getGraphic()).setFitHeight(70);
            tempButton.setFont(prodSansBig);
            tempButton.setStyle("-fx-font-size:50");
            tempButton.setMaxWidth(Double.MAX_VALUE);
            //tempButton.setMaxHeight(Double.MAX_VALUE);
            mainControls.getRowConstraints().add(rowsLeft);
        }
        
        mainControls.getColumnConstraints().add(colsLeft);
        mainControls.setPadding(new Insets(30,0,30,0));
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        //speedButtons
        GridPane speedButtons = new GridPane();
        spd25 = new Button("25%");
        spd25.setOnAction(this);
        spd50 = new Button("50%");
        spd50.setOnAction(this);
        spd75 = new Button("75%");
        spd75.setOnAction(this);
        spd100 = new Button("100%");
        spd100.setOnAction(this);
        spd200 = new Button("200%");
        spd200.setOnAction(this);
        spd800 = new Button("800%");
        spd800.setOnAction(this);
        spd1600 = new Button("1600%");
        spd1600.setOnAction(this);
        ToggleGroup directionGroup = new ToggleGroup();
        reverseToggle = new ToggleButton();
        reverseToggle.setToggleGroup(directionGroup);
        reverseToggle.setOnAction(this);
        reverseToggle.setGraphic(new ImageView(revBlackImg));
        ((ImageView)reverseToggle.getGraphic()).setPreserveRatio(true);
        ((ImageView)reverseToggle.getGraphic()).setFitHeight(70);
        fwdToggle = new ToggleButton();
        fwdToggle.setToggleGroup(directionGroup);
        fwdToggle.setOnAction(this);
        fwdToggle.setGraphic(new ImageView(fwdBlackImg));
        ((ImageView)fwdToggle.getGraphic()).setPreserveRatio(true);
        ((ImageView)fwdToggle.getGraphic()).setFitHeight(70);
        directionGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });
        
        speedButtons.add(reverseToggle, 0, 0, 1, 7);
        speedButtons.add(fwdToggle, 2, 0, 1, 7);
        speedButtons.add(spd25, 1, 0, 1, 1);
        speedButtons.add(spd50, 1, 1, 1, 1);
        speedButtons.add(spd75, 1, 2, 1, 1);
        speedButtons.add(spd100, 1, 3, 1, 1);
        speedButtons.add(spd200, 1, 4, 1, 1);
        speedButtons.add(spd800, 1, 5, 1, 1);
        speedButtons.add(spd1600, 1, 6, 1, 1);
        
        for (int i = 0; i<speedButtons.getChildren().size(); i++) {
            if (speedButtons.getChildren().get(i) instanceof Button){
                Button tempButton = (Button)(speedButtons.getChildren().get(i));
                tempButton.setFont(prodSansBig);
                tempButton.setStyle("-fx-font-size:30");
                tempButton.setMaxWidth(Double.MAX_VALUE);
                tempButton.setMaxHeight(Double.MAX_VALUE);
                speedButtons.getRowConstraints().add(rowsLeft);
            } else if (speedButtons.getChildren().get(i) instanceof ToggleButton){
                ToggleButton tempButton = (ToggleButton)(speedButtons.getChildren().get(i));
                tempButton.setFont(prodSansBig);
                tempButton.setStyle("-fx-font-size:30");
                tempButton.setMaxWidth(Double.MAX_VALUE);
                tempButton.setMaxHeight(Double.MAX_VALUE);
                //speedButtons.getRowConstraints().add(rowsLeft);
            }
        }
        
        speedButtons.getColumnConstraints().add(colsFwdRev);
        speedButtons.getColumnConstraints().add(colsLeft);
        speedButtons.getColumnConstraints().add(colsFwdRev);
        speedButtons.setPadding(new Insets(30,0,30,0));
        
        centerHolder.getChildren().add(leftGrid);
        centerHolder.getChildren().add(spacer);
        centerHolder.getChildren().add(mainControls);
        centerHolder.getChildren().add(spacer2);
        centerHolder.getChildren().add(speedButtons);
        centerHolder.setBackground(defaultBg);
        centerHolder.setEffect(dropShadow);
        centerHolder.setAlignment(Pos.CENTER_LEFT);
        
        //centerHolder.setSpacing(100);
        
        mainLayout.setCenter(centerHolder);
        
    }
    
    @Override
    public void onRefresh() {
        createStatusBar();
        //System.out.println("got event");
    }
    
    public boolean addHyperdeck(String ip, boolean connect, String name) {
        boolean addReturn = hyperdecks.add(new Hyperdeck(ip, connect, name));
        hyperdecks.get(hyperdecks.size()-1).setRefreshListener(this);
        return addReturn;
    }
    
    public boolean addHyperdeck(String ip, int port, boolean connect, String name) {
        boolean addReturn = hyperdecks.add(new Hyperdeck(ip, port, connect, name));
        hyperdecks.get(hyperdecks.size()-1).setRefreshListener(this);
        return addReturn;
    }
    
    public void createReplayList() {
        StackPane spList = new StackPane();
        spList.setPrefWidth(400);
        lv = new ListView();
        lv.setItems(replaysList);
        lv.setCellFactory(new Callback<ListView<ReplayIdentifier>, ListCell<ReplayIdentifier>>() {
            @Override
            public ListCell<ReplayIdentifier> call(ListView<ReplayIdentifier> lv) {
                return new CustomListCell();
            }
        });
        lv.getStylesheets().add("CSS/ReplayListCellCSS.css");
        lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    recallReplay();
                }
            }
        });
        lv.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> starToggle.setSelected(((ReplayIdentifier)newValue).isStarred()));
        spList.getChildren().add(lv);
        spList.setMargin(lv, new Insets(20));
        
        spList.setBackground(defaultBg);
        
        centerHolder.getChildren().add(0, spList);
    }
    
    public void makeReplay() {
        replaysList.add(0, new ReplayIdentifier(currId, "Untitled Replay " + currId));
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                hyperdecks.get(i).newReplay(currId);
            }
        }
        currId++;
    }
    
    public void recallReplay() {
        ReplayIdentifier selectedRI = ((ReplayIdentifier)(lv.getSelectionModel().getSelectedItem()));
        if (selectedRI != null) {
            for (int i = 0; i<hyperdecks.size(); i++) {
                if (hyperdecks.get(i).isManaged()) {
                    hyperdecks.get(i).playReplay(selectedRI.getId());
                }
            }
        }
    }
    
    public void replayToggleStar() {
        ReplayIdentifier selectedRI = ((ReplayIdentifier)(lv.getSelectionModel().getSelectedItem()));
        int selectedIndex = lv.getSelectionModel().getSelectedIndex();
        if (selectedRI != null) {
            selectedRI.toggleStarred();
            replaysList.set(selectedIndex, selectedRI);
        }
    }
    
    public void switchToSlot(int slotid) {
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                hyperdecks.get(i).say("slot select: slot id: " + slotid);
            }
        }
    }
    
    public void startRecord() {
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                hyperdecks.get(i).say("record");
            }
        }
    }
    
    public void sayStop() {
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                hyperdecks.get(i).say("stop");
            }
        }
    }
    
    public void sayPlay() {
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                hyperdecks.get(i).say("play");
            }
        }
    }
    
    public void nextClip() {
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                hyperdecks.get(i).say("goto: clip id: +1");
            }
        }
    }
    
    public void prevClip() {
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                hyperdecks.get(i).say("goto: clip id: -1");
            }
        }
    }
    
    public void editReplayName() {
        ReplayIdentifier selectedRI = ((ReplayIdentifier)(lv.getSelectionModel().getSelectedItem()));
        int selectedIndex = lv.getSelectionModel().getSelectedIndex();
        if (selectedRI != null) {
            TextInputDialog newNameDialog = new TextInputDialog(selectedRI.getName());
 
            newNameDialog.setTitle("Replay Name Editor");
            newNameDialog.setHeaderText("Enter name for the replay:");
            newNameDialog.setContentText("New name:");

            Optional<String> result = newNameDialog.showAndWait();

            result.ifPresent(name -> {
                selectedRI.setName(name);
                replaysList.set(selectedIndex, selectedRI);
            });
        }
    }
    
    public void removeReplay() {
        int selectedIndex = lv.getSelectionModel().getSelectedIndex();
        replaysList.remove(selectedIndex);
    }
    
    public void clearReplays() {
        replaysList.clear();
    }
    
    public void shuttleSpeed(int speed) {
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                if (reverseToggle.isSelected()) {
                    speed = -speed;
                }
                hyperdecks.get(i).say("shuttle: speed: " + speed);
            }
        }
    }
    
    public void playSpeed(int speed) {
        for (int i = 0; i<hyperdecks.size(); i++) {
            if (hyperdecks.get(i).isManaged()) {
                if (reverseToggle.isSelected()) {
                    speed = -speed;
                }
                hyperdecks.get(i).say("play: speed: " + speed);
            }
        }
    }
    
    public void createBottom() {
        HBox hb = new HBox(15);
        VBox vbIp = new VBox(7);
        VBox vbPort = new VBox(7);
        VBox vbName = new VBox(7);
        VBox vbConnect = new VBox(7);
        
        StackPane parentSp = new StackPane(hb);
        parentSp.setPadding(new Insets(20));
        parentSp.setBackground(bottomGray);
        
        Text ipLabel = new Text("IP");
        Text portLabel = new Text("Port");
        Text nameLabel = new Text("Name");
        Text connectLabel = new Text("Connect?");
        TextField ipTf = new TextField();
        TextField portTf = new TextField("9993");
        TextField nameTf = new TextField("");
        CheckBox connectCb = new CheckBox("");
        StackPane buttonAndFill = new StackPane();
        Circle fillCircle = new Circle();
        fillCircle.setRadius(30);
        fillCircle.setFill(Color.web("#00796b"));
        addHDButton = new Button("");
        addHDButton.setGraphic(new ImageView(addImg));
        ((ImageView)addHDButton.getGraphic()).setPreserveRatio(true);
        ((ImageView)addHDButton.getGraphic()).setFitHeight(50);
        addHDButton.setStyle("-fx-background-radius: 5em; " +
                "-fx-min-width: 80px; " +
                "-fx-min-height: 80px; " + 
                "-fx-max-width: 80px; " +
                "-fx-max-height: 80px; " + 
                "-fx-background-color: #ffab40; " +
                "-fx-text-fill: black; ");
        DropShadow dSButton = new DropShadow();
        dSButton.setBlurType(BlurType.GAUSSIAN);
        dSButton.setOffsetX(0.0);
        dSButton.setOffsetY(3.0);
        dSButton.setRadius(7);
        dSButton.setColor(Color.web("#444444"));
        addHDButton.setEffect(dSButton);
        
        buttonAndFill.getChildren().addAll(fillCircle, addHDButton);
        
        vbIp.getChildren().addAll(ipLabel, ipTf);
        vbPort.getChildren().addAll(portLabel, portTf);
        vbName.getChildren().addAll(nameLabel, nameTf);
        vbConnect.getChildren().addAll(connectLabel, connectCb);
        
        hb.getChildren().add(vbIp);
        hb.getChildren().add(vbPort);
        hb.getChildren().add(vbName);
        hb.getChildren().add(vbConnect);
        
        
        for (int i = 0; i<hb.getChildren().size(); i++) {
            if (hb.getChildren().get(i) instanceof VBox) {
                for (int j = 0; j<((VBox)hb.getChildren().get(i)).getChildren().size(); j++) {
                    if (((VBox)hb.getChildren().get(i)).getChildren().get(j) instanceof Text) {
                        ((Text)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setFont(prodSansBig);
                        ((Text)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setStyle("-fx-font-size: 21;");
                        ((Text)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setFill(Color.gray(0.08));
                    } else if (((VBox)hb.getChildren().get(i)).getChildren().get(j) instanceof TextField) {
                        ((TextField)((VBox)hb.getChildren().get(i)).getChildren().get(j)).getStylesheets().add("/CSS/BottomTextFieldCSS.css");
                    } else if (((VBox)hb.getChildren().get(i)).getChildren().get(j) instanceof CheckBox) {
                        ((CheckBox)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setFont(prodSansBig);
                        ((CheckBox)((VBox)hb.getChildren().get(i)).getChildren().get(j)).getStylesheets().add("/CSS/BottomCheckBoxCSS.css");
                        ((CheckBox)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setStyle("selected-box-color: #1976d2; box-color: white; mark-color: white;");
                    }
                }
            }
        }
        
        Text resultLabel = new Text("Hyperdeck added!");
        resultLabel.setFont(prodSansBig);
        resultLabel.setStyle("-fx-font-size: 40");
        resultLabel.setFill(Color.WHITE);
        resultLabel.setOpacity(0);
                
        hb.getChildren().add(buttonAndFill);
        
        FadeTransition fadeInCircle = new FadeTransition(Duration.seconds(.1), fillCircle);
        fadeInCircle.setFromValue(0.0);
        fadeInCircle.setToValue(1.0);
        ScaleTransition enlargeCircle = new ScaleTransition(Duration.seconds(0.9), fillCircle);
        enlargeCircle.setToX(70);
        enlargeCircle.setToY(70);
        FadeTransition fadeInResult = new FadeTransition(Duration.seconds(.25), resultLabel);
        fadeInResult.setFromValue(0.0);
        fadeInResult.setToValue(1.0);
        PauseTransition pauseAndReset = new PauseTransition(Duration.seconds(0.75));
        pauseAndReset.setOnFinished((e) -> {
            addHyperdeck(ipTf.getText(), Integer.parseInt(portTf.getText()), connectCb.isSelected(), nameTf.getText());
            ipTf.setText("");
            portTf.setText("9993");
            nameTf.setText("");
            connectCb.setSelected(false);
        });
        FadeTransition fadeOutResult = new FadeTransition(Duration.seconds(.5), resultLabel);
        fadeOutResult.setFromValue(1.0);
        fadeOutResult.setToValue(0.0);
        FadeTransition fadeOutFill = new FadeTransition(Duration.seconds(.5), fillCircle);
        fadeOutFill.setFromValue(1.0);
        fadeOutFill.setToValue(0.0);
        ParallelTransition pt = new ParallelTransition(fadeOutResult, fadeOutFill);         
        SequentialTransition st = new SequentialTransition(fadeInCircle, enlargeCircle, fadeInResult, pauseAndReset, pt);
        st.setOnFinished((e) -> {
            makeUI();
        });
        addHDButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<Node> workingCollection = FXCollections.observableArrayList(buttonAndFill.getChildren());
                Collections.swap(workingCollection, 0, 1);
                buttonAndFill.getChildren().setAll(workingCollection);
                parentSp.getChildren().add(resultLabel);
                st.play();
            }
        });
        
        hb.setStyle("-fx-max-hegiht: 100px; " +
                "-fx-min-height: 100px; ");
        
        hb.setAlignment(Pos.CENTER);
        
        mainLayout.setBottom(parentSp);
    }
}