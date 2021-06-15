import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Game();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
