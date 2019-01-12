package HDControl;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Callback;

public class controlMain extends Application implements EventHandler<ActionEvent>, RefreshListener {
    
    public static final String version = "0.0.1a";
    public static Stage primaryStage;
    public static Socket client;
    public static InetAddress ip;
    public static ArrayList<Hyperdeck> hyperdecks = new ArrayList();
    public static Background recRed = new Background(new BackgroundFill(Color.web("#d32f2f"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background playBlue = new Background(new BackgroundFill(Color.web("#1976d2"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background unkPurple = new Background(new BackgroundFill(Color.web("#6a1b9a"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background stopGray = new Background(new BackgroundFill(Color.web("#757575"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Font prodSansBig, prodSansSmall;
    public static BorderPane mainLayout = new BorderPane();
    public static GridPane topGrid;
    public static ColumnConstraints sbarWidth = new ColumnConstraints();
    public static RowConstraints statusBarItemHeight = new RowConstraints();
    public static DropShadow dropShadow = new DropShadow();
    public static Image stopImg, playImg, prevImg, pauseImg, recImg, errImg, shuttleImg;
    public static Button s1Button, s2Button, recallButton, saveButton, editButton, deleteButton, playButton, pauseButton, stopButton, nextClip, prevClip, fwdButton, revButton, custom1Button, custom2Button;
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
        
        sbarWidth.setPercentWidth(100);
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Blackmagic HyperDeck Control " + version);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        
        addHyperdeck("192.168.0.30", true, "Living Room HD");
        
        createStatusBar();
        createReplayList();
        createControlButtons();
        createBottom();
        
        Scene mainScene = new Scene(mainLayout, 1600, 900);
        mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                //replaysList.set(0, new ReplayIdentifier(replaysList.get(0).getId(), "newo-nameo"));
                for (int i = 0; i<hyperdecks.size(); i++) {
                    hyperdecks.get(i).playReplay(currId-1);
                }
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
    
    public void createStatusBar() {
        
        //System.out.println("CREATE SB FUNCTION RUN");
                
        sbarWidth.setPercentWidth(100);
        
        statusBarItemHeight.setPrefHeight(100);
                
        topGrid = new GridPane();
        for (int i = 0; i<hyperdecks.size(); i++) {
            topGrid.add(hyperdecks.get(0).getStackPane(), 0, 0);
            topGrid.getColumnConstraints().addAll(sbarWidth);
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
        //leftGrid.setGridLinesVisible(true);
        leftGrid.setAlignment(Pos.CENTER);
        
        centerHolder.getChildren().add(leftGrid);
        
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
        
        TextField ipTf = new TextField();
    }
}
