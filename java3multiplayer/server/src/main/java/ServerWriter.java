import java.io.DataOutputStream;
import java.io.IOException;

public class ServerWriter implements Runnable{
    private final DataOutputStream dos;
    private final Car[] cars;
    private int[] carNames = new int[10];
    private final Model model;
    private int id;

    public ServerWriter(DataOutputStream _dos, Model _model, int _id) {
        dos = _dos;
        model = _model;
        cars = model.getCars();
        for (int i = 0; i < 10; i++){
            carNames[i] = cars[i].getCar_id();
        }
        id = _id;
    }

    public void setId(int _id){
        id = _id;
    }

    private void writeData() throws IOException {
        for (int i = 0; i < 10; i++) {
            dos.writeInt(1);
            dos.writeInt(i);
            dos.writeDouble(cars[i].getPosition().x);
            dos.writeDouble(cars[i].getPosition().y);
            dos.writeDouble(cars[i].getDirection().x);
            dos.writeDouble(cars[i].getDirection().y);
            dos.writeBoolean(cars[i].getTrail());
            if (carNames[i] != cars[i].getCar_id()){
                dos.writeInt(2);
                dos.writeInt(i);
                dos.writeInt(cars[i].getCar_id());
                carNames[i] = cars[i].getCar_id();
            }
        }
        dos.writeInt(3);
        dos.writeInt(cars[id].getCheckpointCurrent());
    }

    public void run() {
        try {
            writeData();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
