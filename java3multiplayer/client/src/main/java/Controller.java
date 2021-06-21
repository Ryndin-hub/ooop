import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller implements KeyListener {
    private final InputStreamReader fileInputStream = new InputStreamReader(System.in);
    private final BufferedReader bufferedReader = new BufferedReader(fileInputStream);
    private final ClientWriter clientWriter;

    public Controller(ClientWriter _clientWriter){
        clientWriter = _clientWriter;
    }

    private String readConsole() throws IOException {
        if (bufferedReader.ready()) {
            return bufferedReader.readLine();
        }
        return null;
    }

    private int findCar(String carName) throws IOException {
        BufferedReader cars_settings = new BufferedReader(new FileReader("src/main/resources/cars_cfg.txt"));
        String s;
        String[] words;
        carName = carName.substring(0, 1).toUpperCase() + carName.substring(1);
        while ((s = cars_settings.readLine()) != null) {
            words = s.split(" ");
            for (String word : words) {
                if (word.equals(carName)) {
                    String input = cars_settings.readLine();
                    if (!input.equals("////")) return -1;
                    input = cars_settings.readLine();
                    return Integer.parseInt(input);
                }
            }
        }
        return -1;
    }

    private void parseInput(String input) throws IOException {
        String[] words = input.split(" ");
        switch (words[0]) {
            case "reset" -> clientWriter.reset();
            case "car" -> {
                if (words.length == 1) {
                    System.out.println("Available cars: Audi R8, BMW M3, Bugatti Chiron, Chevrolet Camaro, Dodge Viper, Ferrari 488, Ford Mustang, Honda S2000, Lamborghini Aventador, Mazda RX-8, Mercedes-AMG GT, Mitsubishi Lancer Evolution X, Nissan GT-R, Porsche 911, Subaru WRX STI, Toyota Supra");
                    break;
                }
                int carId = findCar(words[1]);
                if (carId == -1) System.out.println("Unable to find this car");
                else clientWriter.changeCar(carId);
            }
            case "records" -> clientWriter.getRecords();
            default -> System.out.println("Available commands: reset, car [car_name], records");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        String input;
        try {
            input = readConsole();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }
        if (input != null){
            try {
                parseInput(input);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        clientWriter.keyboard(true,e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        clientWriter.keyboard(false,e.getKeyCode());
    }
}
