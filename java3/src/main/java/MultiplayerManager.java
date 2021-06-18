import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MultiplayerManager extends Thread {
    private String ip = "localhost";
    private int port = 22222;
    private ArrayList<Socket> socket = new ArrayList<Socket>();
    private ServerSocket serverSocket;
    private ArrayList<DataOutputStream> dos = new ArrayList<DataOutputStream>();
    private ArrayList<DataInputStream> dis = new ArrayList<DataInputStream>();
    private boolean connected = false;
    private boolean isServer = false;
    private int playersConnected = 0;
    private Game game;
    private boolean resetNeeded = false;
    private boolean carChanged = false;
    private int carChangedNameId;
    private int carChangedId;
    private int myId;
    private RequestListener requestListener;
    private ServerListener serverListener;
    private ServerWriter serverWriter;
    private PlayerListener playerListener;
    private PlayerWriter playerWriter;

    public void initializeServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
            myId = 0;
            requestListener = new RequestListener();
            requestListener.start();
            serverListener = new ServerListener();
            serverListener.start();
            serverWriter = new ServerWriter();
            serverWriter.start();
            System.out.println("Server started");
            isServer = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            socket.add(new Socket(ip, port));
            dos.add(new DataOutputStream(socket.get(0).getOutputStream()));
            dis.add(new DataInputStream(socket.get(0).getInputStream()));
            playerListener = new PlayerListener();
            playerListener.start();
            playerWriter = new PlayerWriter();
            playerWriter.start();
            connected = true;
        } catch (IOException e) {
            System.out.println("Unable to connect to the server");
        }
        System.out.println("Successfully connected to the server");
    }

    private class RequestListener extends Thread {
        public void run() {
            while (true) {
                try {
                    socket.add(serverSocket.accept());
                    DataOutputStream dosTmp = new DataOutputStream(socket.get(playersConnected).getOutputStream());
                    dosTmp.writeInt(4);
                    dosTmp.writeInt(playersConnected+1);
                    for (int i = 0; i < 10; i++){
                        dosTmp.writeInt(2);
                        dosTmp.writeInt(i);
                        dosTmp.writeInt(game.getCarNameId(i));
                    }
                    dos.add(dosTmp);
                    dis.add(new DataInputStream(socket.get(playersConnected).getInputStream()));
                    playersConnected++;
                    game.addPlayer(playersConnected);
                    System.out.println("Player joined game");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
                int flag = dis.get(i).readInt();
                switch (flag) {
                    case 1:
                        double[] values = new double[8];
                        for (int j = 0; j < 8; j++) {
                            values[j] = dis.get(i).readDouble();
                        }
                        game.setValues(values, i+1);
                        boolean[] valuesK = new boolean[256];
                        for (int j = 0; j < 256; j++) {
                            valuesK[j] = dis.get(i).readBoolean();
                        }
                        game.setKeyboardValues(valuesK, i+1);
                        break;
                    case 2:
                        int name_id = dis.get(i).readInt();
                        game.setCarName(name_id, i+1);
                        carChanged = true;
                        carChangedId = i+1;
                        carChangedNameId = name_id;
                        break;
                    case 3:
                        System.out.println("server got reset request");
                        game.reset();
                        resetNeeded = true;
                        break;
                }
            }
        }

        public void run() {
            while (true) {
                Thread.interrupted();
                try {
                    listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ServerWriter extends Thread {
        private void write() throws IOException {
            if (carChanged) {
                for (int i = 0; i < dos.size(); i++) {
                    dos.get(i).writeInt(2);
                    dos.get(i).writeInt(carChangedId);
                    dos.get(i).writeInt(carChangedNameId);
                }
                carChanged = false;
            }
            if (resetNeeded) {
                System.out.println("server send reset request");
                for (int i = 0; i < dos.size(); i++) {
                    dos.get(i).writeInt(3);
                }
                resetNeeded = false;
            }
            for (int i = 0; i < dis.size(); i++) {
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
                    dos.get(i).writeInt(1);
                    dos.get(i).writeInt(k+1);
                    values = game.getValues(k+1);
                    for (int j = 0; j < 8; j++) {
                        dos.get(i).writeDouble(values[j]);
                    }
                    valuesK = game.getKeyboardValues(k+1);
                    for (int j = 0; j < 256; j++) {
                        dos.get(i).writeBoolean(valuesK[j]);
                    }
                }
            }
        }

        public void run() {
            while (true) {
                try {
                    write();
                    Thread.sleep(100);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class PlayerListener extends Thread {
        private void listen() throws IOException {
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
                    System.out.println("player got reset request");
                    game.reset();
                    break;
                case 4:
                    myId = dis.get(0).readInt();
                    game.setMainCarId(myId);
                    break;
            }
        }

        public void run(){
            while (true) {
                try {
                    listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class PlayerWriter extends Thread {
        private void write() throws IOException {
            if (carChanged) {
                dos.get(0).writeInt(2);
                dos.get(0).writeInt(carChangedId);
                carChanged = false;
            }
            if (resetNeeded) {
                System.out.println("player send reset request");
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
                try {
                    write();
                    Thread.sleep(100);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
