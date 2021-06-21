import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private final Car[] cars = new Car[10];
    private View view;
    private Controller controller;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ClientWriter clientWriter;
    private ClientReader clientReader;

    public Client(){
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());
        clientWriter = new ClientWriter(dos);

        for (int i = 0; i < 10; i++){
            cars[i] = new Car();
        }
        controller = new Controller(clientWriter);
        view = new View(cars,0);
        view.addKeyListener(controller);

        clientWriter.start();
        clientReader = new ClientReader(dis,cars,view);
        clientReader.start();
        view.draw();
    }
}
