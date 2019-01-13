package HDControl;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Hyperdeck  {
    
    private InetAddress ip;
    private Socket client;
    private int port;
    private String name;
    private Thread listeningThread;
    private String status = "NOT CONNECTED";
    private int slot;
    private PrintWriter out;
    private BufferedReader in;
    private RefreshListener listener;
    private boolean managed;
    private boolean connected = false;
    private ArrayList<Replay> replays = new ArrayList();
    private int pendingReplayId = -1;
    
    public Hyperdeck(String ip, int port, boolean connect, String name) {
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host Exception thrown while converting String to IP address");
        }
        this.port = port;
        this.name = name;
        if (connect) {
            start();
        }
        managed = true;
        Platform.runLater(sendUpdate);
    }
    
    public Hyperdeck(String ip, boolean connect, String name) {
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host Exception thrown while converting String to IP address");
        }
        this.port = 9993;
        this.name = name;
        if (connect) {
            start();
        }
        managed = true;
        Platform.runLater(sendUpdate);
    }
    
    Runnable monitor = new Runnable() {
        public void run() {
            try {
                connect();
                if (client != null) {
                    System.out.println("Connected to HyperDeck: " + ip);

                    out = new PrintWriter(client.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    say("notify: transport: true");
                    say("transport info");

                    String received = "";
                    String temp;

                    while ((temp = in.readLine()) != null) {
                        if (!temp.equals("")) {
                            received += temp;
                            //System.out.println(temp);
                            if (temp.startsWith("status: ")) {
                                status = temp.substring(temp.indexOf(": ")+2);
                            } else if (temp.startsWith("display timecode:")) {
                                if (pendingReplayId >= 0) {
                                    replays.get(pendingReplayId).setTimecode(temp.substring(temp.indexOf(": ")+2));
                                    if (replays.get(pendingReplayId).hasAll()) pendingReplayId = -1;
                                }
                            } else if (temp.startsWith("clip count:")) {
                                if (pendingReplayId >= 0) {
                                    replays.get(pendingReplayId).setClipId(Integer.parseInt(temp.substring(temp.indexOf(": ")+2))+1);
                                    if (replays.get(pendingReplayId).hasAll()) pendingReplayId = -1;
                                }
                            } else if (temp.startsWith("slot id:") || temp.startsWith("active slot:")) {
                                if (pendingReplayId >= 0) {
                                    replays.get(pendingReplayId).setSlotId(Integer.parseInt(temp.substring(temp.indexOf(": ")+2)));
                                    if (replays.get(pendingReplayId).hasAll()) pendingReplayId = -1;
                                } else {
                                    slot = Integer.parseInt(temp.substring(temp.indexOf(": ")+2));
                                }
                            }
                            Platform.runLater(sendUpdate);
                        } else {
                            //end of message from hyperdeck
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("IOException while running thread for IP " + ip);
            }
        }
    };
    
    public void start() {
        if (listeningThread == null) {
            listeningThread = new Thread(monitor, "HyperDeck Thread");
            listeningThread.start();
            System.out.println("started thread");
        }
    }
    
    public void connect() {
        try {
            status = "connecting";
            System.out.println("Connecting to " + ip);
            client = new Socket(ip, port);
            connected = true;
        } catch (IOException e) {
            System.out.println("Failed to connect to IP " + ip);
            status = "Not connected";
            Platform.runLater(sendUpdate);
        }
    }
    
    public String getStatus() {
        return status;
    }
    
    public boolean getManagedState() {
        return managed;
    }
    
    public void setManagedState(boolean state) {
        managed = state;
        Platform.runLater(sendUpdate);
    }
    
    public StackPane getStackPane() {
        Label statusLabel = new Label(status.toUpperCase());
        Label nameLabel;
        nameLabel = new Label((name).toUpperCase());
        if (connected) {
            nameLabel = new Label((name + " (Slot " + slot + ")").toUpperCase());
        }
        statusLabel.setFont(controlMain.prodSansBig);
        nameLabel.setFont(controlMain.prodSansSmall);
        VBox vb = new VBox(statusLabel);
        vb.getChildren().add(nameLabel);
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setFitHeight(70);
        HBox hb = new HBox(15);
        hb.getChildren().add(iv);
        hb.getChildren().add(vb);
        StackPane sp = new StackPane(hb);
        
        sp.setMargin(hb, new Insets(12));
        if (status.equalsIgnoreCase("record")) {
            sp.setBackground(controlMain.recRed);
            statusLabel.setTextFill(Color.WHITE);
            nameLabel.setTextFill(Color.WHITE);
            iv.setImage(controlMain.recImg);
        } else if (status.equalsIgnoreCase("preview")) {
            sp.setBackground(controlMain.stopGray);
            statusLabel.setTextFill(Color.WHITE);
            nameLabel.setTextFill(Color.WHITE);
            iv.setImage(controlMain.prevImg);
        } else if (status.equalsIgnoreCase("stopped")) {
            sp.setBackground(controlMain.stopGray);
            statusLabel.setTextFill(Color.WHITE);
            nameLabel.setTextFill(Color.WHITE);
            iv.setImage(controlMain.stopImg);
        } else if (status.equalsIgnoreCase("play")) {
            sp.setBackground(controlMain.playBlue);
            statusLabel.setTextFill(Color.WHITE);
            nameLabel.setTextFill(Color.WHITE);
            iv.setImage(controlMain.playImg);
        } else if (status.equalsIgnoreCase("shuttle")) {
            sp.setBackground(controlMain.playBlue);
            statusLabel.setTextFill(Color.WHITE);
            nameLabel.setTextFill(Color.WHITE);
            iv.setImage(controlMain.shuttleImg);
        } else {
            sp.setBackground(controlMain.unkPurple);
            statusLabel.setTextFill(Color.WHITE);
            nameLabel.setTextFill(Color.WHITE);
            iv.setImage(controlMain.errImg);
        }
        
        if (!managed) {
            statusLabel.setText(("(U) ").toUpperCase().concat(statusLabel.getText()));
        }
        
        sp.setAlignment(Pos.CENTER_LEFT);
        return sp;
    }
    
    public void say(String message) {
        if (connected) {
            String readyToSend = (message + "\n");
            out.println(readyToSend);
        }
    }
    
    public void setRefreshListener(RefreshListener listener) {
        this.listener = listener;
    }   
    
    Runnable sendUpdate = new Runnable() {
        public void run() {
            if (listener != null) listener.onRefresh();
        }
    };
    
    public void newReplay(int id) {
        say("slot info");
        say("clips count");
        say("transport info");
        replays.add(id, new Replay());
        pendingReplayId = id;
    }
    
    public void playReplay(int id) {
        try {
            say("slot select: slot id: " + replays.get(id).getSlotId());
            say("goto: clip id: " + replays.get(id).getClipId());
            say("goto: timecode: +" + replays.get(id).getTimecode());
        } catch (NullPointerException e) {
            System.out.println("Replay ID " + id + " didn't exist on HyperDeck \"" + name);
        }
    }
    
    public boolean isManaged() {
        return managed;
    }
}