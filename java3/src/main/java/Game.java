import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Game{

    private final Car[] cars = new Car[10];

    private final boolean[][] keyboard = new boolean[10][255];

    private final View view;

    private final Records records = new Records();

    public Game() throws IOException {
        for (int i = 0; i < 10; i++) {
            cars[i] = new Car(i);
            cars[i].setRecords(records);
        }
        view = new View(cars, 0);
        keyboard[0] = view.getKeyboard();
        new Timer().schedule(new MainLoop(), 0, 1000/30);
    }

    public void update() throws IOException {
        cars[0].update(keyboard[0]);
        for (int i = 1; i < 10; i++) {
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
                case "reset":
                    for (int i = 0; i < 10; i++) {
                        cars[i].reset();
                    }
                    break;
                case "car":
                    if (words.length == 1){
                        System.out.println("Available cars: Audi R8, BMW M3, Bugatti Chiron, Chevrolet Camaro, Dodge Viper, Ferrari 488, Ford Mustang, Honda S2000, Lamborghini Aventador, Mazda RX-8, Mercedes-AMG GT, Mitsubishi Lancer Evolution X, Nissan GT-R, Porsche 911, Subaru WRX STI, Toyota Supra");
                        break;
                    }
                    boolean carFound = cars[0].setCar(words[1]);
                    if (!carFound) System.out.println("Unable to find this car");
                    break;
                case "records":
                    records.printRecords();
                    break;
                case "clear":
                    view.clear();
                    break;
                default:
                    System.out.println("Available commands: reset, car [car_name], records, clear");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MainLoop extends TimerTask {
        @Override
        public void run() {
            long start = System.nanoTime();
            try {
                update();
            } catch (IOException e) {
                e.printStackTrace();
            }
            long finish = System.nanoTime();
            //System.out.println("update: " + (finish - start));
            start = System.nanoTime();
            view.render();
            parseInput();
            finish = System.nanoTime();
            //System.out.println("repaint: " + (finish - start));
        }
    }

}
