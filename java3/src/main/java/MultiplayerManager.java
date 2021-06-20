import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MultiplayerManager extends Thread {
    private final ArrayList<Socket> socket = new ArrayList<>();
    private ServerSocket serverSocket;
    private final ArrayList<DataOutputStream> dos = new ArrayList<>();
    private final ArrayList<DataInputStream> dis = new ArrayList<>();
    private final ArrayList<Integer> playersIds = new ArrayList<>();
    private final ArrayList<Integer> availableIds = new ArrayList<>();
    private int playerErrorListen;
    private int playerErrorWrite;
    private boolean idsSet = false;
    private boolean connected = false;
    private boolean isServer = false;
    private int playersConnected = 0;
    private Game game;
    private boolean resetNeeded = false;
    private boolean carChanged = false;
    private int carChangedNameId;
    private int carChangedId;
    private boolean disconnected = false;
    private boolean shutdown = false;
    private int myId;

    public void initializeServer(String ip, int port) {
        if (!idsSet){
            for (int i = 1; i < 10; i++){
                availableIds.add(i);
                idsSet = true;
            }
        }
        try {
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
            myId = 0;
            RequestListener requestListener = new RequestListener();
            requestListener.start();
            ServerListener serverListener = new ServerListener();
            serverListener.start();
            ServerWriter serverWriter = new ServerWriter();
            serverWriter.start();
            System.out.println("Server started");
            isServer = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to start server");
        }
    }

    public void connect(String ip, int port) {
        try {
            socket.add(new Socket(ip, port));
            dos.add(new DataOutputStream(socket.get(0).getOutputStream()));
            dis.add(new DataInputStream(socket.get(0).getInputStream()));
            PlayerListener playerListener = new PlayerListener();
            playerListener.start();
            PlayerWriter playerWriter = new PlayerWriter();
            playerWriter.start();
            connected = true;
            System.out.println("Successfully connected to the server");
        } catch (IOException e) {
            System.out.println("Unable to connect to the server");
        }
    }

    public void disconnect() throws InterruptedException {
        disconnected = true;
        sleep(100);
        connected = false;
        sleep(100);
        socket.clear();
        dos.clear();
        dis.clear();
        game.removeAllPlayers();
        System.out.println("Disconnected from server");
    }

    private class RequestListener extends Thread {
        public void run() {
            while (true) {
                if (!isServer || playersConnected > 9) continue;
                try {
                    socket.add(serverSocket.accept());
                    DataOutputStream dosTmp = new DataOutputStream(socket.get(playersConnected).getOutputStream());
                    playersIds.add(availableIds.get(0));
                    availableIds.remove(0);
                    dosTmp.writeInt(4);
                    dosTmp.writeInt(playersIds.get(playersConnected));
                    for (int i = 0; i < 10; i++){
                        dosTmp.writeInt(2);
                        dosTmp.writeInt(i);
                        dosTmp.writeInt(game.getCarNameId(i));
                    }
                    dos.add(dosTmp);
                    dis.add(new DataInputStream(socket.get(playersConnected).getInputStream()));
                    game.addPlayer(playersIds.get(playersConnected));
                    playersConnected++;
                    System.out.println("Player joined game");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void disconnectPlayer(int i){
        dos.remove(i);
        dis.remove(i);
        socket.remove(i);
        int id = playersIds.remove(i);
        availableIds.add(id);
        playersConnected--;
        game.removePlayer(id);
        System.out.println("Player " + (i+2) + " disconnected");
    }

    public void shutdownServer() throws InterruptedException {
        shutdown = true;
        sleep(150);
        isServer = false;
        sleep(150);
        playersConnected = 0;
        availableIds.clear();
        dos.clear();
        dis.clear();
        playersIds.clear();
        socket.clear();
        serverSocket = null;
        game.removeAllPlayers();
        System.out.println("Server down");
    }

    public void setResetNeeded(){
        resetNeeded = true;
    }

    public void setCarChanged(int id){
        carChanged = true;
        carChangedId = 0;
        carChangedNameId = id;
    }

    private class ServerListener extends Thread {
        private void listen() throws IOException {
            for (int i = 0; i < dis.size(); i++) {
                int playerId = playersIds.get(i);
                playerErrorListen = i;
                int flag = dis.get(i).readInt();
                switch (flag) {
                    case 1 -> {
                        double[] values = new double[8];
                        for (int j = 0; j < 8; j++) {
                            values[j] = dis.get(i).readDouble();
                        }
                        game.setValues(values, playerId);
                        boolean[] valuesK = new boolean[256];
                        for (int j = 0; j < 256; j++) {
                            valuesK[j] = dis.get(i).readBoolean();
                        }
                        game.setKeyboardValues(valuesK, playerId);
                    }
                    case 2 -> {
                        int name_id = dis.get(i).readInt();
                        game.setCarName(name_id, playerId);
                        carChanged = true;
                        carChangedId = playerId;
                        carChangedNameId = name_id;
                    }
                    case 3 -> {
                        game.reset();
                        resetNeeded = true;
                    }
                    case 4 -> {
                        disconnectPlayer(i);
                        disconnected = true;
                    }
                }
            }
        }

        public void run() {
            while (true) {
                if (!isServer) continue;
                Thread.interrupted();
                try {
                    listen();
                } catch (IOException e) {
                    disconnectPlayer(playerErrorListen);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }

    private class ServerWriter extends Thread {
        private void write() throws IOException {
            if (shutdown){
                for (int i = 0; i < dos.size(); i++) {
                    playerErrorWrite = i;
                    dos.get(i).writeInt(6);
                }
                shutdown = false;
            }
            if (disconnected){
                for (int i = 0; i < dos.size(); i++) {
                    playerErrorWrite = i;
                    dos.get(i).writeInt(5);
                }
                disconnected = false;
            }
            if (carChanged) {
                for (int i = 0; i < dos.size(); i++) {
                    playerErrorWrite = i;
                    dos.get(i).writeInt(2);
                    dos.get(i).writeInt(carChangedId);
                    dos.get(i).writeInt(carChangedNameId);
                }
                carChanged = false;
            }
            if (resetNeeded) {
                for (int i = 0; i < dos.size(); i++) {
                    playerErrorWrite = i;
                    dos.get(i).writeInt(3);
                }
                resetNeeded = false;
            }
            for (int i = 0; i < dis.size(); i++) {
                playerErrorWrite = i;
                dos.get(i).writeInt(1);
                dos.get(i).writeInt(0);
                double[] values = game.getValues(0);
                for (int j = 0; j < 8; j++) {
                    dos.get(i).writeDouble(values[j]);
                }
                boolean[] valuesK = game.getKeyboardValues(0);
                for (int j = 0; j < 256; j++) {
                    dos.get(i).writeBoolean(valuesK[j]);
                }
                for (int k = 0; k < dos.size(); k++) {
                    if (i == k) continue;
                    int playerId = playersIds.get(k);
                    dos.get(i).writeInt(1);
                    dos.get(i).writeInt(playerId);
                    values = game.getValues(playerId);
                    for (int j = 0; j < 8; j++) {
                        dos.get(i).writeDouble(values[j]);
                    }
                    valuesK = game.getKeyboardValues(playerId);
                    for (int j = 0; j < 256; j++) {
                        dos.get(i).writeBoolean(valuesK[j]);
                    }
                }
            }
        }

        public void run() {
            while (true) {
                if (!isServer) continue;
                try {
                    write();
                    Thread.sleep(100);
                } catch (IOException | InterruptedException e) {
                    disconnectPlayer(playerErrorWrite);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }

    private class PlayerListener extends Thread {
        private void listen() throws IOException, InterruptedException {
            int flag = dis.get(0).readInt();
            switch (flag){
                case 1:
                    int car_id = dis.get(0).readInt();
                    game.addPlayer(car_id);
                    double[] values = new double[8];
                    for (int j = 0; j < 8; j++){
                        values[j] = dis.get(0).readDouble();
                    }
                    game.setValues(values,car_id);
                    boolean[] valuesK = new boolean[256];
                    for (int j = 0; j < 256; j++){
                        valuesK[j] = dis.get(0).readBoolean();
                    }
                    game.setKeyboardValues(valuesK,car_id);
                    break;
                case 2:
                    car_id = dis.get(0).readInt();
                    int name_id = dis.get(0).readInt();
                    game.setCarName(name_id,car_id);
                    break;
                case 3:
                    game.reset();
                    break;
                case 4:
                    myId = dis.get(0).readInt();
                    game.setMainCarId(myId);
                    break;
                case 5:
                    game.removeAllPlayers();
                    break;
                case 6:
                    disconnect();
                    break;
            }
        }

        public void run(){
            while (true) {
                if (!connected) continue;
                try {
                    listen();
                } catch (IOException | InterruptedException e) {
                    try {
                        disconnect();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }

    private class PlayerWriter extends Thread {
        private void write() throws IOException {
            if (disconnected){
                dos.get(0).writeInt(4);
                disconnected = false;
            }
            if (carChanged) {
                dos.get(0).writeInt(2);
                dos.get(0).writeInt(carChangedNameId);
                carChanged = false;
            }
            if (resetNeeded) {
                dos.get(0).writeInt(3);
                resetNeeded = false;
            }
            dos.get(0).writeInt(1);
            double[] values = game.getValues(myId);
            for (int j = 0; j < 8; j++) {
                dos.get(0).writeDouble(values[j]);
            }
            boolean[] valuesK = game.getKeyboardValues(myId);
            for (int j = 0; j < 256; j++) {
                dos.get(0).writeBoolean(valuesK[j]);
            }
        }

        public void run(){
            while (true) {
                if (!connected) continue;
                try {
                    write();
                    Thread.sleep(100);
                } catch (IOException | InterruptedException e) {
                    try {
                        disconnect();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
