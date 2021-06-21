import java.io.DataOutputStream;
import java.io.IOException;

public class ClientWriter extends Thread{
    private final DataOutputStream dos;
    private boolean resetNeeded = false;
    private boolean carChanged = false;
    private int carChangedName;
    private boolean getRecords = false;
    private boolean keyboardState[] = new boolean[256];
    private boolean keyboardChanged[] = new boolean[256];

    public ClientWriter(DataOutputStream _dos){
        dos = _dos;
        for (int i = 0; i < 256; i++){
            keyboardState[i] = false;
            keyboardChanged[i] = false;
        }
    }

    public void reset(){
        resetNeeded = true;
    }

    public void changeCar(int carName){
        carChanged = true;
        carChangedName = carName;
    }

    public void getRecords(){
        getRecords = true;
    }

    public void keyboard(boolean s,int i){
        keyboardState[i] = s;
        keyboardChanged[i] = true;
    }

    private void writeData() throws IOException {
        if (carChanged) {
            dos.writeInt(2);
            dos.writeInt(carChangedName);
            carChanged = false;
        }
        if (resetNeeded) {
            dos.writeInt(3);
            resetNeeded = false;
        }
        if (getRecords){
            dos.writeInt(4);
            getRecords = false;
        }
        for (int i = 0; i < 256; i++){
            if (keyboardChanged[i]){
                dos.writeInt(1);
                dos.writeInt(i);
                dos.writeBoolean(keyboardState[i]);
                keyboardChanged[i] = false;
            }
        }
        //dos.writeInt(0);
    }

    public void run(){
        while (true){
            try {
                writeData();
                Thread.sleep(50);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
