package HDControl;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javafx.animation.*;
import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class controlMain extends Application implements EventHandler<ActionEvent>, RefreshListener {
    
    public static final String version = "0.0.9a";
    public static Stage primaryStage;
    public static Socket client;
    public static Scene mainControlScene, mainLoginScene, mainNewUserScene;
    public static InetAddress ip;
    public static ArrayList<Hyperdeck> hyperdecks = new ArrayList();
    public static ArrayList<User> users = new ArrayList();
    public static Background recRed = new Background(new BackgroundFill(Color.web("#d32f2f"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background playBlue = new Background(new BackgroundFill(Color.web("#1976d2"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background unkPurple = new Background(new BackgroundFill(Color.web("#6a1b9a"), CornerRadii.EMPTY, Insets.EMPTY));
    public static Background rewindYellow = new Background(new BackgroundFill(Color.web("#fbc02d"), CornerRadii.EMPTY, Insets.EMPTY));
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
    public static Image stopImg, playImg, prevImg, pauseImg, recImg, errImg, shuttleImg, noConnectionImg, addImg, starImg, playBlackImg, stopBlackImg, recBlackImg, skipFwdBlackImg, skipBackBlackImg, revBlackImg, fwdBlackImg, handleImg, decorImg, closeImg;
    public static Image iconImg, smallIconImg, searchImg, clearImg, loginLogoImg;
    public static Button s1Button, s2Button, recallButton, saveButton, editButton, deleteButton, clearButton, recordButton, stopButton, playButton, nextClip, prevClip, fwdButton, revButton, custom1Button, custom2Button, addHDButton;
    public static Button spd25, spd50, spd75, spd100, spd200, spd800, spd1600, searchButton, clearSearchButton;
    public static Button loginButton, closeButton, newUserButton, logoutButton;
    public static ToggleButton starToggle, reverseToggle, fwdToggle, starFilterToggle;
    public static ObservableList<ReplayIdentifier> replaysList= FXCollections.observableArrayList();
    public static ObservableList<ReplayIdentifier> foundList= FXCollections.observableArrayList();
    public static int currId = 0;
    public static ListView lv;
    public static double xOffset = 0;
    public static double yOffset = 0;
    public static HBox centerHolder = new HBox(10);
    public static HBox decorButtons;
    public static BorderPane root = new BorderPane();
    public static BorderPane loginRoot = new BorderPane();
    public static BorderPane newUserRoot = new BorderPane();
    public static Slider speedSlider;
    public static TextField searchField;
    public static TextField userNameField, newUserNameField, newUserRealNameField, newUserTypeField;
    public static PasswordField passwordField, newUserPasswordField;
    public static StackPane loginWindowContent = new StackPane();
    
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        try {
            prodSansBig = Font.loadFont(new FileInputStream(new File("Product-Sans-Regular.ttf")), 40);
            prodSansSmall = Font.loadFont(new FileInputStream(new File("Product-Sans-Regular.ttf")), 15);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        stopImg = new Image("file:src/images/straight/stop.png");
        recImg = new Image("file:src/images/straight/rec.png");
        playImg = new Image("file:src/images/straight/play.png");
        prevImg = new Image("file:src/images/straight/preview.png");
        pauseImg = new Image("file:src/images/straight/pause.png");
        errImg = new Image("file:src/images/straight/err.png");
        shuttleImg = new Image("file:src/images/straight/shuttle.png");
        starImg = new Image("file:src/images/rounded/star.png", 100, 100, true, true);
        noConnectionImg = new Image("file:src/images/straight/noconnection2.png");
        addImg = new Image("file:src/images/straight/add.png");
        playBlackImg = new Image("file:src/images/rounded/playBlack.png");
        stopBlackImg = new Image("file:src/images/rounded/stopBlack.png");
        skipFwdBlackImg = new Image("file:src/images/rounded/skipFwd.png");
        skipBackBlackImg = new Image("file:src/images/rounded/skipBack.png");
        recBlackImg = new Image("file:src/images/rounded/recordBlack.png");
        revBlackImg = new Image("file:src/images/rounded/reverseBlack.png");
        fwdBlackImg = new Image("file:src/images/rounded/forwardBlack.png");
        decorImg = new Image("file:src/images/decoration.png", 270, 50, true, true);
        loginLogoImg = new Image("file:src/images/decoration.png");
        iconImg = new Image("file:src/images/icon.png");
        smallIconImg = new Image("file:src/images/icon.png", 50, 50, true, true);
        searchImg = new Image("file:src/images/rounded/search.png", 50, 50, true, true);
        clearImg = new Image("file:src/images/rounded/clear.png", 50, 50, true, true);
        
        //load logins
        loadFile("logins.csv", users);
        
        sbarWidth.setPercentWidth(100);
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        showLoginWindow();
        
        primaryStage.show();
    }
    
    public void showControlWindow() {
        primaryStage.setTitle("HyperActive " + version);
        primaryStage.getIcons().add(iconImg);
        makeControlUI();
        if (mainControlScene == null) {
            mainControlScene = new Scene(root, 1600, 900);
            mainControlScene.setFill(Color.TRANSPARENT);
        }
        primaryStage.setScene(mainControlScene);
    }
    
    public void showLoginWindow() {
        primaryStage.setTitle("Login | HyperActive " + version);
        primaryStage.getIcons().add(iconImg);
        makeLoginUI();
        if (mainLoginScene == null) {
            mainLoginScene = new Scene(loginRoot, 700, 300);
            mainLoginScene.setFill(Color.TRANSPARENT);
        }
        primaryStage.setScene(mainLoginScene);
    }
    
    public void showNewUserWindow() {
        primaryStage.setTitle("New user | HyperActive " + version);
        primaryStage.getIcons().add(iconImg);
        makeNewUserUI();
        if (mainNewUserScene == null) {
            mainNewUserScene = new Scene(newUserRoot, 400, 300);
            mainNewUserScene.setFill(Color.TRANSPARENT);
        }
        primaryStage.setScene(mainNewUserScene);
    }
    
    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == loginButton) login();
        else if (event.getSource() == closeButton) close();
        else if (event.getSource() == s1Button) switchToSlot(1);
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
        else if (event.getSource() == newUserButton) createNewUser();
        else if (event.getSource() == logoutButton) logout();
        else if (event.getSource() == starFilterToggle) {
            if (starFilterToggle.isSelected()) {
                foundList.clear();
                for (int i = 0; i<lv.getItems().size(); i++) {
                    if (((ReplayIdentifier)lv.getItems().get(i)).isStarred()) foundList.add(replaysList.get(i));
                }
                lv.setItems(foundList);
            } else {
                foundList.clear();
                lv.setItems(replaysList);
            }
        } else if (event.getSource() == searchButton) {
            starFilterToggle.setSelected(false);
            foundList.clear();
            for (int i = 0; i<replaysList.size(); i++) {
                if (((ReplayIdentifier)replaysList.get(i)).getName().toLowerCase().contains(searchField.getText().toLowerCase())) foundList.add(replaysList.get(i));
            }
            lv.setItems(foundList);
        } else if (event.getSource() == clearSearchButton) {
            foundList.clear();
            lv.setItems(replaysList);
        }
        
    }
    
    public void makeControlUI() {
        createBottom();
        createControlButtons();
        createReplayList();
        createStatusBar();
        
        GridPane decorations = new GridPane();
        ColumnConstraints fullCol = new ColumnConstraints();
        fullCol.setPercentWidth(100);
        RowConstraints fullRow = new RowConstraints();
        fullRow.setPercentHeight(100);
        decorations.getColumnConstraints().addAll(fullCol, fullCol, fullCol);
        decorations.getRowConstraints().addAll(fullRow);
        decorButtons = new HBox(15);
        decorButtons.setPadding(new Insets(0,8,0,8));
        decorButtons.setAlignment(Pos.CENTER_RIGHT);
        ImageView closeIV = new ImageView();
        closeIV.setPreserveRatio(true);
        closeIV.setFitHeight(15);
        closeIV.setId("closeButton");
        closeIV.setStyle("/CSS/decorationsStylingCSS.css");
        closeIV.setPickOnBounds(true);
        closeIV.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    //primaryStage.close();
                    Timeline timeline = new Timeline();
                    KeyFrame key = new KeyFrame(Duration.millis(500),
                                   new KeyValue (primaryStage.getScene().getRoot().opacityProperty(), 0)); 
                    timeline.getKeyFrames().add(key);   
                    timeline.setOnFinished((ae) -> close()); 
                    timeline.play();
                }
            }
        });
        //
        ImageView maxIV = new ImageView();
        maxIV.setPreserveRatio(true);
        maxIV.setFitWidth(15);
        maxIV.setId("maximizeButton");
        maxIV.setStyle("/CSS/decorationsStylingCSS.css");
        maxIV.setPickOnBounds(true);
        maxIV.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    primaryStage.setMaximized(true);
                    makeControlUI();
                }
            }
        });
        ImageView minIV = new ImageView();
        minIV.setPreserveRatio(true);
        minIV.setFitWidth(15);
        minIV.setId("minimizeButton");
        minIV.setStyle("/CSS/decorationsStylingCSS.css");
        minIV.setPickOnBounds(true);
        minIV.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    primaryStage.setIconified(true);
                }
            }
        });
        decorButtons.getChildren().add(minIV);
        decorButtons.getChildren().add(maxIV);
        decorButtons.getChildren().add(closeIV);
        decorations.setId("decorations");
        decorations.getStylesheets().add("/CSS/decorationsStylingCSS.css");
        ImageView ivDecor = new ImageView(decorImg);
        ivDecor.setPreserveRatio(true);
        ivDecor.setFitHeight(20);
        ImageView ivLogo = new ImageView(smallIconImg);
        ivLogo.setPreserveRatio(true);
        ivLogo.setFitHeight(20);
        HBox decorImgHolder = new HBox(10);
        decorImgHolder.getChildren().add(ivLogo);
        decorImgHolder.getChildren().add(ivDecor);
        decorImgHolder.setAlignment(Pos.CENTER);
        decorations.add(decorImgHolder, 1, 0);
        decorations.add(decorButtons, 2, 0);
        
        decorations.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        decorations.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (primaryStage.maximizedProperty().getValue() == false) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
                
            }
        });
        
        root.setCenter(mainLayout);
        if (primaryStage.maximizedProperty().getValue() == false) {
            root.setTop(decorations);
            root.setStyle("-fx-background-color: transparent; -fx-padding: 20px");
            DropShadow windowShadow = new DropShadow();
            windowShadow.setBlurType(BlurType.GAUSSIAN);
            windowShadow.setColor(Color.BLACK);
            windowShadow.setHeight(16);
            windowShadow.setWidth(16);
            root.setEffect(windowShadow);
        } else {
            root.setTop(null);
            root.setStyle("-fx-background-color: transparent;");
        }
        
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
        
        topGrid.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        if (primaryStage.maximizedProperty().getValue() == true) {
                            primaryStage.setMaximized(false);
                            makeControlUI();
                        } else {
                            primaryStage.setMaximized(true);
                            makeControlUI();
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
        fwdToggle.setSelected(true);
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
        
        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        //speed slider
        speedSlider = new Slider(0, 200, 100);
        speedSlider.setSnapToTicks(true);
        speedSlider.setId("custom-slider");
        speedSlider.getStylesheets().add("/CSS/SpeedSliderCSS.css");
        speedSlider.setOrientation(Orientation.VERTICAL);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(10f);
        speedSlider.setBlockIncrement(1f);
        speedSlider.setLabelFormatter(new StringConverter<Double>(){
            DecimalFormat df = new DecimalFormat( "#,##0" );
            @Override
            public String toString(Double object) {
                return df.format(200-object+0) + "%";
            }

            @Override
            public Double fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

        });
        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    playSpeed(200-((int)Math.round(new_val.floatValue())));
            }
        });
        
        speedSlider.setPadding(new Insets(30,0,30,0));
        
        Region spacer4 = new Region();
        HBox.setHgrow(spacer4, Priority.ALWAYS);
        
        centerHolder.getChildren().add(leftGrid);
        centerHolder.getChildren().add(spacer);
        centerHolder.getChildren().add(mainControls);
        centerHolder.getChildren().add(spacer2);
        centerHolder.getChildren().add(speedButtons);
        centerHolder.getChildren().add(spacer3);
        centerHolder.getChildren().add(speedSlider);
        centerHolder.getChildren().add(spacer4);
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
        VBox vbList = new VBox();
        vbList.setPrefWidth(400);
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
        
        HBox hbSearch = new HBox(8);
        searchField = new TextField();
        searchField.setPrefHeight(40);
        searchField.getStylesheets().add("/CSS/SearchTextBoxCSS.css");
        searchButton = new Button("");
        searchButton.setPrefHeight(40);
        searchButton.setGraphic(new ImageView(searchImg));
        ((ImageView)searchButton.getGraphic()).setPreserveRatio(true);
        ((ImageView)searchButton.getGraphic()).setFitHeight(25);
        searchButton.setOnAction(this);
        clearSearchButton = new Button("");
        clearSearchButton.setPrefHeight(40);
        clearSearchButton.setGraphic(new ImageView(clearImg));
        ((ImageView)clearSearchButton.getGraphic()).setPreserveRatio(true);
        ((ImageView)clearSearchButton.getGraphic()).setFitHeight(25);
        clearSearchButton.setOnAction(this);
        starFilterToggle = new ToggleButton("");
        starFilterToggle.setOnAction(this);
        starFilterToggle.setPrefHeight(40);
        starFilterToggle.setGraphic(new ImageView(starImg));
        ((ImageView)starFilterToggle.getGraphic()).setPreserveRatio(true);
        ((ImageView)starFilterToggle.getGraphic()).setFitHeight(25);
        hbSearch.getChildren().add(searchField);
        hbSearch.getChildren().add(searchButton);
        hbSearch.getChildren().add(clearSearchButton);
        hbSearch.getChildren().add(starFilterToggle);
        hbSearch.setHgrow(searchField, Priority.ALWAYS);
        hbSearch.setHgrow(searchButton, Priority.ALWAYS);
        hbSearch.setHgrow(clearSearchButton, Priority.ALWAYS);
        hbSearch.setHgrow(starFilterToggle, Priority.ALWAYS);
        
        vbList.getChildren().add(lv);
        vbList.setMargin(lv, new Insets(20,20,0,20));
        vbList.setVgrow(lv, Priority.ALWAYS);
        vbList.getChildren().add(hbSearch);
        vbList.setMargin(hbSearch, new Insets(20));
        
        vbList.setBackground(defaultBg);
        
        centerHolder.getChildren().add(0, vbList);
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
        if (primaryStage.maximizedProperty().getValue() == false) {
            parentSp.setStyle("-fx-background-radius: 0 0 10 10; -fx-background-color : #b0bec5;");
        } else {
            parentSp.setBackground(bottomGray);
        }
        
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
            makeControlUI();
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
        
        //mainLayout.setStyle("-fx-background-color: transparent;");
        mainLayout.setBottom(parentSp);
    }
    
    public void makeLoginUI() {
        BorderPane loginMain = new BorderPane();
        loginWindowContent.getChildren().add(loginMain);
        loginMain.setBackground(new Background(new BackgroundFill(Color.web("#0f0f0f"), CornerRadii.EMPTY, Insets.EMPTY)));
        loginMain.setEffect(dropShadow);
        ImageView bigLogoIV = new ImageView();
        bigLogoIV.setPreserveRatio(true);
        bigLogoIV.setImage(loginLogoImg);
        bigLogoIV.setFitWidth(500);
        HBox imageHolder = new HBox(bigLogoIV);
        imageHolder.setPadding(new Insets(12));
        imageHolder.setAlignment(Pos.CENTER);
        loginMain.setTop(imageHolder);
        
        GridPane loginInfo = new GridPane();
        loginInfo.setHgap(10);
        loginInfo.setVgap(8);
        loginInfo.setPadding(new Insets(10));
        Text userNameLabel = new Text("Username:");
        userNameLabel.setFont(prodSansBig);
        userNameLabel.setStyle("-fx-fill: #f9f9f9;"
                + "-fx-font-size: 14pt;");
        userNameField = new TextField();
        userNameField.setPromptText("Enter your username");
        Text passwordLabel = new Text("Password:");
        passwordLabel.setFont(prodSansBig);
        passwordLabel.setStyle("-fx-fill: #f9f9f9;"
                + "-fx-font-size: 14pt;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        
        loginInfo.getChildren().addAll(userNameLabel, userNameField, passwordLabel, passwordField);
        loginInfo.setRowIndex(userNameLabel, 0);
        loginInfo.setRowIndex(userNameField, 0);
        loginInfo.setRowIndex(passwordLabel, 1);
        loginInfo.setRowIndex(passwordField, 1);
        loginInfo.setColumnIndex(userNameLabel, 0);
        loginInfo.setColumnIndex(userNameField, 1);
        loginInfo.setColumnIndex(passwordLabel, 0);
        loginInfo.setColumnIndex(passwordField, 1);
        loginInfo.setAlignment(Pos.CENTER);
        
        HBox loginButtons = new HBox(8);
        loginButton = new Button("Login");
        loginButton.setOnAction(this);
        closeButton = new Button("Exit");
        closeButton.setOnAction(this);
        loginButtons.getChildren().addAll(loginButton, closeButton);
        loginButtons.setAlignment(Pos.CENTER);
        VBox loginVBox = new VBox(8);
        loginVBox.setPadding(new Insets(20));
        loginVBox.setAlignment(Pos.TOP_CENTER);
        loginVBox.getChildren().addAll(loginInfo, loginButtons);
        loginMain.setCenter(loginVBox);
        
        
        loginRoot.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        loginRoot.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (primaryStage.maximizedProperty().getValue() == false) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
                
            }
        });
        
        loginMain.setStyle("-fx-border-radius: 10 10 10 10;"
                + "-fx-background-radius: 10 10 10 10;"
                + "-fx-background-color: #0f0f0f");
        loginRoot.setPadding(new Insets(20));
        loginRoot.setBackground(Background.EMPTY);
        loginRoot.setCenter(loginWindowContent);
    }
    
    public void login() {
        String unameUsed = userNameField.getText();
        String passUsed = passwordField.getText();
        String hashedPass = hashPassword(passUsed);
        
        for (User user : users) {
            if (user.getUsername().equals(unameUsed)) {
                if (user.getHashedPass().equals(hashedPass)) {
                    //signed in correctly
                    BorderPane welcomePane = new BorderPane();
                    welcomePane.setStyle("-fx-border-radius: 10 10 10 10;"
                        + "-fx-background-radius: 10 10 10 10;"
                        + "-fx-background-color: #f9f9f9");
                    Text welcomeText = new Text("Welcome back, " + user.getName() + ".");
                    welcomePane.setBackground(new Background(new BackgroundFill(Color.web("#f9f9f9"), CornerRadii.EMPTY, Insets.EMPTY)));
                    welcomePane.setCenter(welcomeText);
                    welcomeText.setFont(prodSansBig);
                    FadeTransition welcomeFade = new FadeTransition(Duration.millis(1500), welcomePane);
                    welcomeFade.setFromValue(0.0);
                    welcomeFade.setToValue(1.0);
                    PauseTransition pauseAfterLogin = new PauseTransition(Duration.millis(750));
                    SequentialTransition welcomeTransition = new SequentialTransition(welcomeFade, pauseAfterLogin);
                    welcomeTransition.setOnFinished((e) -> {
                        if (user.getAccountType().equals("administrator")) {
                            //do something for administrators
                            primaryStage.close();
                            showNewUserWindow();
                            primaryStage.show();
                            return;
                        } else if (user.getAccountType().equals("user")) {
                            //do something for users
                            primaryStage.close();
                            showControlWindow();
                            primaryStage.show();
                            return;
                        } else { 
                            //account type unsupported
                        }
                    });
                    welcomeTransition.play();
                    loginWindowContent.getChildren().add(welcomePane);
                    return;
                } else {
                    //wrong password used
                    System.out.println("wrong pass");
                    return;
                }
            }
        }
        
        //username not found
        System.out.println("bad username");
    }
    
    public static void loadFile(String filename, ArrayList tempList ) {
        String temp = "";
        try {
                BufferedReader file = new BufferedReader(new FileReader(filename));
                while (file.ready()) {
                temp = file.readLine();
                String tempArray[] = temp.split(",");
                
                tempList.add(new User(tempArray[0], tempArray[1], tempArray[2], tempArray[3]));
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public String hashPassword(String plaintext) {
        
        try {
            String hashedPass = "";
            
            MessageDigest passwordDigest;
            passwordDigest = MessageDigest.getInstance("SHA-256");
            
            byte[] hash = passwordDigest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
            
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            return "Hashing Failed";
        }
    }
    
    public void makeNewUserUI() {
        BorderPane newUserMain = new BorderPane();
        
        GridPane decorations = new GridPane();
        decorations.setId("decorations");
        decorations.getStylesheets().add("/CSS/decorationsStylingCSS.css");
        ImageView ivDecor = new ImageView(decorImg);
        ivDecor.setPreserveRatio(true);
        ivDecor.setFitHeight(20);
        ImageView ivLogo = new ImageView(smallIconImg);
        ivLogo.setPreserveRatio(true);
        ivLogo.setFitHeight(20);
        HBox decorImgHolder = new HBox(10);
        decorImgHolder.getChildren().add(ivLogo);
        decorImgHolder.getChildren().add(ivDecor);
        decorImgHolder.setAlignment(Pos.CENTER);
        decorations.add(decorImgHolder,0,0);
        decorations.setAlignment(Pos.CENTER);
        newUserMain.setTop(decorations);
        newUserMain.setStyle("-fx-background-color: transparent; -fx-padding: 20px");
        newUserMain.setEffect(dropShadow);
        
        Text newUserName = new Text("Username:");
        newUserName.setFont(prodSansBig);
        newUserName.setStyle("-fx-fill: #212121;"
                + "-fx-font-size: 14pt;");
        newUserNameField = new TextField();
        newUserNameField.setPromptText("Enter new username");
        Text newUserRealName = new Text("Name:");
        newUserRealName.setFont(prodSansBig);
        newUserRealName.setStyle("-fx-fill: #212121;"
                + "-fx-font-size: 14pt;");
        newUserRealNameField = new TextField();
        newUserRealNameField.setPromptText("Enter new user's real name");
        Text newUserPassword = new Text("Password:");
        newUserPassword.setFont(prodSansBig);
        newUserPassword.setStyle("-fx-fill: #212121;"
                + "-fx-font-size: 14pt;");
        newUserPasswordField = new PasswordField();
        newUserPasswordField.setPromptText("Enter new password");
        Text newUserType = new Text("Privileges:");
        newUserType.setFont(prodSansBig);
        newUserType.setStyle("-fx-fill: #212121;"
                + "-fx-font-size: 14pt;");
        newUserTypeField = new TextField();
        newUserTypeField.setPromptText("User/Administrator");
        
        GridPane newUserStuff = new GridPane();
        newUserStuff.setAlignment(Pos.CENTER);
        newUserStuff.setPadding(new Insets(8));
        newUserStuff.setHgap(10);
        newUserStuff.setVgap(7);
        //column row        
        newUserStuff.add(newUserName, 0,0);
        newUserStuff.add(newUserNameField, 1,0);
        newUserStuff.add(newUserRealName, 0, 1);
        newUserStuff.add(newUserRealNameField, 1, 1);
        newUserStuff.add(newUserPassword, 0, 2);
        newUserStuff.add(newUserPasswordField, 1, 2);
        newUserStuff.add(newUserType, 0, 3);
        newUserStuff.add(newUserTypeField, 1, 3);
        
        newUserButton = new Button("Create User");
        newUserButton.setOnAction(this);
        logoutButton = new Button("Logout");
        logoutButton.setOnAction(this);
        HBox newUserButtons = new HBox(8);
        newUserButtons.setAlignment(Pos.CENTER);
        newUserButtons.getChildren().addAll(logoutButton, newUserButton);
        VBox newUserContent = new VBox(10);
        newUserContent.getChildren().addAll(newUserStuff, newUserButtons);
        newUserContent.setAlignment(Pos.CENTER);
        
        newUserContent.setStyle("-fx-border-radius: 0 0 10 10;"
                + "-fx-background-radius: 0 0 10 10;"
                + "-fx-background-color : #f9f9f9;");
        newUserMain.setCenter(newUserContent);
        
        decorations.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        decorations.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (primaryStage.maximizedProperty().getValue() == false) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
                
            }
        });
        
        newUserRoot.setPadding(new Insets(5));
        newUserRoot.setBackground(Background.EMPTY);
        newUserRoot.setCenter(newUserMain);
    }
    
    public void createNewUser() {
        users.add(new User(newUserNameField.getText(), newUserRealNameField.getText(), hashPassword(newUserPasswordField.getText()), newUserTypeField.getText()));
        saveFile("logins.csv", users);
        loadFile("logins.csv", users);
        logout();
    }
    
    public static void saveFile(String filename, ArrayList <User> tempList ) {
        try {
            PrintWriter file = new PrintWriter(new FileWriter(filename));

            for (int i = 0; i < tempList.size(); i++) {
                String toSave = "";
                toSave = tempList.get(i).getUsername();
                toSave +="," + tempList.get(i).getName();
                toSave += "," + tempList.get(i).getHashedPass();
                toSave +="," + tempList.get(i).getAccountType();
                
                file.println(toSave);
            }
            file.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

    }//end saveFile

    
    public void logout() {
        primaryStage.close();
        showLoginWindow();
        primaryStage.show();
    }
    
    public void close() {
        primaryStage.close();
        Platform.exit();
        System.exit(0);
    }
}