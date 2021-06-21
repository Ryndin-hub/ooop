import java.io.DataInputStream;
import java.io.IOException;

public class ClientReader extends Thread{
    private final DataInputStream dis;
    private Car cars[];
    private View view;

    public ClientReader(DataInputStream _dis, Car[] _cars, View _view){
        dis = _dis;
        cars = _cars;
        view = _view;
    }

    private void readData() throws IOException {
        int flag = dis.readInt();
        switch (flag) {
            case 1 -> {
                int carId = dis.readInt();
                cars[carId].position.x = dis.readDouble();
                cars[carId].position.y = dis.readDouble();
                cars[carId].direction.x = dis.readDouble();
                cars[carId].direction.y = dis.readDouble();
                cars[carId].tiresTrail = dis.readBoolean();
            }
            case 2 -> {
                int carId = dis.readInt();
                cars[carId].carNameId = dis.readInt();
            }
            case 3 -> view.setCheckpointCurrent(dis.readInt());
            case 4 -> view.setMain_car_i(dis.readInt());
        }
    }

    public void run(){
        while (true){
            try {
                readData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
