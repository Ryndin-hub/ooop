import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Model {
    private final Car[] cars = new Car[10];
    private final ArrayList<Integer> players = new ArrayList<>();
    private final Records records = new Records();
    private final boolean[][] keyboard = new boolean[10][256];

    public Model() throws IOException {
        for (int i = 0; i < 10; i++) {
            cars[i] = new Car(i);
            cars[i].setRecords(records);
        }
        new Timer().schedule(new MainLoop(), 0, 1000/30);
    }

    public void reset(){
        for (int i = 0; i < 10; i++) {
            cars[i].reset();
        }
    }

    public boolean[][] getKeyboard() {
        return keyboard;
    }

    public Car[] getCars() {
        return cars;
    }

    public void changeCar(int carName, int carId) throws IOException {
        cars[carId].setCar(carName);
    }

    public int getCarName(int i){
        return cars[i].getCar_id();
    }

    public void addPlayer(int id){
        if(!players.contains(id)){
            players.add(id);
            for (int i = 0; i < 256; i++){
                keyboard[id][i] = false;
            }
        }
    }


    public void update() throws IOException {
        for (int i = 0; i < 10; i++) {
            if (players.contains(i)) {
                cars[i].update(keyboard[i]);
                continue;
            }
            cars[i].dumbAI(keyboard[i]);
            cars[i].update(keyboard[i]);
        }
    }

    private class MainLoop extends TimerTask {
        @Override
        public void run() {
            try {
                update();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
