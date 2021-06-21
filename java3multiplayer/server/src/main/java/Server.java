import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    Model model = new Model();
    private final ArrayList<Socket> socket = new ArrayList<>();
    private ServerSocket serverSocket;
    private final ArrayList<DataOutputStream> dos = new ArrayList<>();
    private final ArrayList<DataInputStream> dis = new ArrayList<>();
    private final ArrayList<ServerWriter> sw = new ArrayList<>();
    private final ArrayList<ServerReader> sr = new ArrayList<>();
    private RequestListener requestListener;
    private ThreadPool threadPoolWrite = new ThreadPool(10, 4);
    private ThreadPool threadPoolRead = new ThreadPool(10, 4);
    private TaskAdderWrite taskAdderWrite = new TaskAdderWrite();
    private TaskAdderRead taskAdderRead = new TaskAdderRead();
    private ArrayList<Thread> threadReader = new ArrayList<>();
    private int playersConnected = 0;
    private boolean isStarted = false;

    public Server() throws IOException {

    }

    public void initializeServer(String ip, int port) {
        try {
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
            requestListener = new RequestListener();
            requestListener.start();
            System.out.println("Server started");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to start server");
        }
    }

    private class RequestListener extends Thread {
        public void run() {
            while (true) {
                try {
                    socket.add(serverSocket.accept());
                    dos.add(new DataOutputStream(socket.get(playersConnected).getOutputStream()));
                    dis.add(new DataInputStream(socket.get(playersConnected).getInputStream()));
                    sr.add(new ServerReader(dis.get(playersConnected), model, playersConnected));
                    sw.add(new ServerWriter(dos.get(playersConnected), model, playersConnected));
                    for (int i = 0; i < 10; i++){
                        dos.get(playersConnected).writeInt(2);
                        dos.get(playersConnected).writeInt(i);
                        dos.get(playersConnected).writeInt(model.getCarName(i));
                    }
                    dos.get(playersConnected).writeInt(4);
                    dos.get(playersConnected).writeInt(playersConnected);
                    model.addPlayer(playersConnected);
                    if (!isStarted) {
                        taskAdderWrite.start();
                        taskAdderRead.start();
                        isStarted = true;
                    }
                    playersConnected++;
                    System.out.println("Player joined game");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TaskAdderWrite extends Thread{
        public void run(){
            while (true){
                for (int i = 0; i < playersConnected; i++) {
                    try {
                        Task task = new Task(sw.get(i),i);
                        threadPoolWrite.execute(task);
                        sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class TaskAdderRead extends Thread{
        public void run(){
            while (true){
                for (int i = 0; i < playersConnected; i++) {
                    try {
                        Task task = new Task(sr.get(i),i);
                        threadPoolRead.execute(task);
                        sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
