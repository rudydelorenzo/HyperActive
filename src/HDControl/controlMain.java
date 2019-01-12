package HDControl;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
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
    public static Image stopImg, playImg, prevImg, pauseImg, recImg, errImg, shuttleImg, noConnectionImg;
    public static Button s1Button, s2Button, recallButton, saveButton, editButton, deleteButton, playButton, pauseButton, stopButton, nextClip, prevClip, fwdButton, revButton, custom1Button, custom2Button, addHDButton;
    public static ObservableList<ReplayIdentifier> replaysList= FXCollections.observableArrayList();
    public static int currId = 0;
    public static ListView lv;
    public static double xOffset = 0;
    public static double yOffset = 0;
    
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        try { 
            // load a custom font from a specific location (change path!)
            // 12 is the size to use
            prodSansBig = Font.loadFont(new FileInputStream(new File("Product Sans Regular.ttf")), 40);
            prodSansSmall = Font.loadFont(new FileInputStream(new File("Product Sans Regular.ttf")), 15);
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
        noConnectionImg = new Image("file:images/noconnection2.png");
        
        sbarWidth.setPercentWidth(100);
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Blackmagic HyperDeck Control " + version);
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
        if (event.getSource() == saveButton) makeReplay();
        else if (event.getSource() == recallButton) {
            ReplayIdentifier selectedRI = ((ReplayIdentifier)(lv.getSelectionModel().getSelectedItem()));
            if (selectedRI != null) {
                for (int i = 0; i<hyperdecks.size(); i++) {
                    hyperdecks.get(i).playReplay(selectedRI.getId());
                }
            }
        }
    }
    
    public void makeUI() {
        createBottom();
        createReplayList();
        createControlButtons();
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
        
        HBox centerHolder = new HBox(10);
        
        ColumnConstraints colsLeft = new ColumnConstraints();
        colsLeft.setPercentWidth(100);
        RowConstraints rowsLeft = new RowConstraints();
        rowsLeft.setPercentHeight(100);
        
        s1Button = new Button("S1");
        s2Button = new Button("S2");
        recallButton = new Button("RECALL");
        saveButton = new Button("SAVE");
        editButton = new Button("EDIT");
        deleteButton = new Button("DELETE");
        custom1Button = new Button("C1");
        custom2Button = new Button("C2");
        
        saveButton.setOnAction(this);
        recallButton.setOnAction(this);
        
        GridPane leftGrid = new GridPane();
        leftGrid.add(s1Button, 0, 0);
        leftGrid.add(s2Button, 1, 0); 
        leftGrid.add(recallButton, 0, 1, 2, 1);
        leftGrid.add(saveButton, 0, 2, 2, 1);
        leftGrid.add(editButton, 0, 3, 2, 1);
        leftGrid.add(deleteButton, 0, 4, 2, 1);
        leftGrid.add(custom1Button, 0, 6, 2, 1);
        leftGrid.add(custom2Button, 0, 7, 2, 1);
        
        for (int i = 0; i<leftGrid.getChildren().size(); i++) {
            Button tempButton = (Button)(leftGrid.getChildren().get(i));
            tempButton.setFont(prodSansBig);
            tempButton.setStyle("-fx-font-size:30");
            tempButton.setMaxWidth(Double.MAX_VALUE);
            leftGrid.getRowConstraints().add(rowsLeft);
        }
        
        leftGrid.getColumnConstraints().add(colsLeft);
        leftGrid.getColumnConstraints().add(colsLeft);
        leftGrid.setPadding(new Insets(30,0,30,0));
        leftGrid.setAlignment(Pos.CENTER);
        
        centerHolder.getChildren().add(leftGrid);
        centerHolder.setBackground(defaultBg);
        
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
        lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                
                if (click.getClickCount() == 2) {
                    ReplayIdentifier selectedRI = ((ReplayIdentifier)(lv.getSelectionModel().getSelectedItem()));
                    if (selectedRI != null) {
                        for (int i = 0; i<hyperdecks.size(); i++) {
                            hyperdecks.get(i).playReplay(selectedRI.getId());
                        }
                    }
                }
            }
        });
        
        spList.getChildren().add(lv);
        spList.setMargin(lv, new Insets(20));
        
        spList.setBackground(defaultBg);
        
        mainLayout.setLeft(spList);
    }
    
    public void makeReplay() {
        replaysList.add(new ReplayIdentifier(currId, "Issa Replay"));
        for (int i = 0; i<hyperdecks.size(); i++) {
            hyperdecks.get(i).newReplay(currId);
        }
        currId++;
    }
    
    public void recallReplay() {
        
    }
    
    public void createBottom() {
        HBox hb = new HBox(15);
        VBox vbIp = new VBox(7);
        VBox vbPort = new VBox(7);
        VBox vbName = new VBox(7);
        
        StackPane parentSp = new StackPane(hb);
        parentSp.setPadding(new Insets(20));
        parentSp.setBackground(bottomGray);
        
        Text ipLabel = new Text("IP");
        Text portLabel = new Text("Port");
        Text nameLabel = new Text("Name");
        TextField ipTf = new TextField();
        TextField portTf = new TextField("9993");
        TextField nameTf = new TextField("");
        CheckBox connectCb = new CheckBox("Connect?");
        StackPane buttonAndFill = new StackPane();
        Circle fillCircle = new Circle();
        fillCircle.setRadius(30);
        fillCircle.setFill(Color.web("#00796b"));
        addHDButton = new Button("A");
        addHDButton.setStyle("-fx-background-radius: 5em; " +
                "-fx-min-width: 70px; " +
                "-fx-min-height: 70px; ");
        
        buttonAndFill.getChildren().addAll(fillCircle, addHDButton);
        
        vbIp.getChildren().addAll(ipLabel, ipTf);
        vbPort.getChildren().addAll(portLabel, portTf);
        vbName.getChildren().addAll(nameLabel, nameTf);
        
        hb.getChildren().add(vbIp);
        hb.getChildren().add(vbPort);
        hb.getChildren().add(vbName);
        hb.getChildren().add(connectCb);
        
        
        for (int i = 0; i<hb.getChildren().size(); i++) {
            if (hb.getChildren().get(i) instanceof CheckBox) {
                ((CheckBox)hb.getChildren().get(i)).setFont(prodSansBig);
                ((CheckBox)hb.getChildren().get(i)).setStyle("-fx-font-size: 27;" + "-fx-text-fill: #141414;");
            } else if (hb.getChildren().get(i) instanceof VBox) {
                for (int j = 0; j<((VBox)hb.getChildren().get(i)).getChildren().size(); j++) {
                    if (((VBox)hb.getChildren().get(i)).getChildren().get(j) instanceof Text) {
                        ((Text)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setFont(prodSansBig);
                        ((Text)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setStyle("-fx-font-size: 21;");
                        ((Text)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setFill(Color.gray(0.08));
                    } else if (((VBox)hb.getChildren().get(i)).getChildren().get(j) instanceof TextField) {
                        ((TextField)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setFont(prodSansBig);
                        ((TextField)((VBox)hb.getChildren().get(i)).getChildren().get(j)).setStyle("-fx-font-size: 27;");
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
        ScaleTransition enlargeCircle = new ScaleTransition(Duration.seconds(0.95), fillCircle);
        enlargeCircle.setToX(60);
        enlargeCircle.setToY(60);
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