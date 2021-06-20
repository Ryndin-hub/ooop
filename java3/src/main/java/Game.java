import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Game{

    private final Car[] cars = new Car[10];
    private int mainCarId = 0;
    private final ArrayList<Integer> otherPlayers = new ArrayList<>();

    private final boolean[][] keyboard = new boolean[10][256];

    private final View view;

    private final Records records = new Records();

    private final MultiplayerManager multiplayerManager = new MultiplayerManager();

    public Game() throws IOException {
        for (int i = 0; i < 10; i++) {
            cars[i] = new Car(i);
            cars[i].setRecords(records);
        }
        view = new View(cars, mainCarId);
        keyboard[mainCarId] = view.getKeyboard();
        multiplayerManager.setGame(this);
        multiplayerManager.start();
        new Timer().schedule(new MainLoop(), 0, 1000/30);
    }

    public void update() throws IOException {
        for (int i = 0; i < 10; i++) {
            if (i == mainCarId) {
                cars[i].update(keyboard[i]);
                continue;
            }
            if (otherPlayers.contains(i)) {
                cars[i].update(keyboard[i]);
                continue;
            }
            cars[i].dumbAI(keyboard[i]);
            cars[i].update(keyboard[i]);
        }
    }

    private void parseInput(){
        try {
            String input = view.readConsole();
            if (input == null) return;
            String[] words = input.split(" ");
            switch (words[0]) {
                case "reset" -> {
                    reset();
                    multiplayerManager.setResetNeeded();
                }
                case "car" -> {
                    if (words.length == 1) {
                        System.out.println("Available cars: Audi R8, BMW M3, Bugatti Chiron, Chevrolet Camaro, Dodge Viper, Ferrari 488, Ford Mustang, Honda S2000, Lamborghini Aventador, Mazda RX-8, Mercedes-AMG GT, Mitsubishi Lancer Evolution X, Nissan GT-R, Porsche 911, Subaru WRX STI, Toyota Supra");
                        break;
                    }
                    boolean carFound = cars[mainCarId].setCar(words[1]);
                    multiplayerManager.setCarChanged(cars[mainCarId].getCar_id());
                    if (!carFound) System.out.println("Unable to find this car");
                }
                case "records" -> records.printRecords();
                case "clear" -> view.clear();
                case "server" -> multiplayerManager.initializeServer(words[1], Integer.parseInt(words[2]));
                case "connect" -> multiplayerManager.connect(words[1], Integer.parseInt(words[2]));
                case "disconnect" -> multiplayerManager.disconnect();
                case "shutdown" -> multiplayerManager.shutdownServer();
                default -> System.out.println("Available commands: reset, car [car_name], records, clear, server [ip] [port], connect [ip] [port], disconnect, shutdown");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setValues(double[] values, int car_id){
        cars[car_id].setValues(values[0],values[1],values[2],values[3],values[4],values[5],values[6],values[7]);
    }

    public double[] getValues(int car_id){
        return cars[car_id].getValues();
    }

    public void setKeyboardValues(boolean[] values, int car_id){
        for (int i = 0; i < 256; i++){
            keyboard[car_id][i] = values[i];
        }
    }

    public boolean[] getKeyboardValues(int car_id){
        boolean[] values = new boolean[256];
        for (int i = 0; i < 256; i++){
            values[i] = keyboard[car_id][i];
        }
        return values;
    }

    public int getCarNameId(int id){
        return cars[id].getCar_id();
    }

    public void setCarName(int name_id, int car_id) throws IOException {
        cars[car_id].setCar(name_id);
    }

    public void reset(){
        for (int i = 0; i < 10; i++) {
            cars[i].reset();
        }
    }

    public void setMainCarId(int id){
        keyboard[mainCarId] = new boolean[256];
        mainCarId = id;
        keyboard[mainCarId] = view.getKeyboard();
        view.setMain_car_i(id);
    }

    public void addPlayer(int id){
        if(!otherPlayers.contains(id)){
            otherPlayers.add(id);
        }
    }

    public void removePlayer(int id){
        otherPlayers.remove((Integer) id);
    }

    public void removeAllPlayers(){
        otherPlayers.clear();
    }

    private class MainLoop extends TimerTask {
        @Override
        public void run() {
            try {
                update();
            } catch (IOException e) {
                e.printStackTrace();
            }
            view.render();
            parseInput();
        }
    }
}
