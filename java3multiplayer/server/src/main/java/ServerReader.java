import java.io.DataInputStream;
import java.io.IOException;

public class ServerReader implements Runnable{
    private final DataInputStream dis;
    private final boolean[][] keyboard;
    private final Model model;
    private final int id;

    public ServerReader(DataInputStream _dis, Model _model, int _id){
        dis = _dis;
        model = _model;
        keyboard = model.getKeyboard();
        id = _id;
    }

    private void readData() throws IOException {
        int flag = dis.readInt();
        switch (flag) {
            case 0 -> {}
            case 1 -> {
                int i = dis.readInt();
                keyboard[id][i] = dis.readBoolean();
            }
            case 2 -> {
                model.changeCar(dis.readInt(),id);
            }
            case 3 -> model.reset();
        }
    }

    public void run(){
        try {
            readData();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
