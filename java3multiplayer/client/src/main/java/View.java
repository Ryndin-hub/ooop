import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class View extends JFrame {

    private BufferedImage track_img;
    private final BufferedImage particles_img;
    private final BufferedImage cars_img;
    private final BufferedImage final_img;
    private BufferedImage checkpoint_img;
    private BufferedImage[] carsImages = new BufferedImage[16];

    private final Graphics2D g2;
    private final Graphics2D g3;
    private final Graphics2D g4;

    private final Car[] cars;
    private int main_car_i;
    Vector[] saved_positions = new Vector[10];

    private Vector[] checkpointPositions;
    private double[] checkpointAngles;
    private int checkpointCurrent;

    private Vector[] driftLastPoint11 = new Vector[10];
    private Vector[] driftLastPoint12 = new Vector[10];
    private Vector[] driftLastPoint21 = new Vector[10];
    private Vector[] driftLastPoint22 = new Vector[10];

    private int window_width = 1280, window_height = 720;
    private final int track_width = 5999;
    private final int track_height = 2851;

    public View(Car[] _cars, int _main_car_i) throws IOException {
        cars = _cars;
        main_car_i = _main_car_i;
        setCheckpoints();
        setCarsImages();

        try {
            track_img = ImageIO.read(getClass().getResourceAsStream("background.png"));
            checkpoint_img = ImageIO.read(getClass().getResourceAsStream("checkpoint.png"));
        } catch (IOException ignore) {
        }
        particles_img = new BufferedImage(track_width, track_height, BufferedImage.TYPE_INT_ARGB);
        cars_img = new BufferedImage(track_width, track_height, BufferedImage.TYPE_INT_ARGB);
        final_img = new BufferedImage(track_width, track_height, BufferedImage.TYPE_INT_ARGB);

        g2 = (Graphics2D) particles_img.getGraphics();
        g3 = (Graphics2D) cars_img.getGraphics();
        g4 = (Graphics2D) final_img.getGraphics();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(window_width, window_height);
        setVisible(true);
    }

    private class Drawer extends Thread{
        public void run(){
            while (true){
                repaint();
            }
        }
    }

    public void draw(){
        Drawer drawer = new Drawer();
        drawer.start();
    }

    public void setMain_car_i(int _main_car_i){
        main_car_i = _main_car_i;
    }

    public void setCheckpointCurrent(int i){
        checkpointCurrent = i;
    }

    private void drawCheckpoint(){
        g3.translate(checkpointPositions[checkpointCurrent].x, checkpointPositions[checkpointCurrent].y);
        g3.rotate(-checkpointAngles[checkpointCurrent]);
        g3.drawImage(checkpoint_img,-80, -80,null);
        g3.rotate(checkpointAngles[checkpointCurrent]);
        g3.translate(-checkpointPositions[checkpointCurrent].x, -checkpointPositions[checkpointCurrent].y);
    }

    private void drawCar(int id){
        Vector position = saved_positions[id];
        Vector direction = cars[id].direction;
        double angle = -Math.atan2(direction.x, direction.y);
        int pox = (int) (position.x);
        int poy = (int) (position.y);
        g3.translate(pox, poy);
        g3.rotate(angle);
        if (id != main_car_i && Vector.distanceBetween(position,cars[main_car_i].position) < 100){
            AlphaComposite acomp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
            g3.setComposite(acomp);
        }
        g3.drawImage(carsImages[cars[id].carNameId],-50,-50,null);
        AlphaComposite acomp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g3.setComposite(acomp);
        g3.rotate(-angle);
        g3.translate(-pox, -poy);

        if (cars[id].tiresTrail) {
            Vector driftNewPoint11 = new Vector (10,-25);
            Vector driftNewPoint12 = new Vector (18,-25);
            Vector driftNewPoint21 = new Vector (-10,-25);
            Vector driftNewPoint22 = new Vector (-18,-25);
            driftNewPoint11.rotate(angle);
            driftNewPoint12.rotate(angle);
            driftNewPoint21.rotate(angle);
            driftNewPoint22.rotate(angle);
            driftNewPoint11.add(position);
            driftNewPoint12.add(position);
            driftNewPoint21.add(position);
            driftNewPoint22.add(position);
            if (driftLastPoint11[id] != null){
                if (id == main_car_i) g2.setColor(new Color(0, 0, 0, 50));
                else g2.setColor(new Color(0, 0, 0, 30));
                Polygon polygon1 = new Polygon();
                polygon1.addPoint((int)driftNewPoint11.x, (int)driftNewPoint11.y);
                polygon1.addPoint((int)driftNewPoint12.x, (int)driftNewPoint12.y);
                polygon1.addPoint((int)driftLastPoint12[id].x, (int)driftLastPoint12[id].y);
                polygon1.addPoint((int)driftLastPoint11[id].x, (int)driftLastPoint11[id].y);
                g2.fillPolygon(polygon1);
                Polygon polygon2 = new Polygon();
                polygon2.addPoint((int)driftNewPoint21.x, (int)driftNewPoint21.y);
                polygon2.addPoint((int)driftNewPoint22.x, (int)driftNewPoint22.y);
                polygon2.addPoint((int)driftLastPoint22[id].x, (int)driftLastPoint22[id].y);
                polygon2.addPoint((int)driftLastPoint21[id].x, (int)driftLastPoint21[id].y);
                g2.fillPolygon(polygon2);
            }
            driftLastPoint11[id] = driftNewPoint11;
            driftLastPoint12[id] = driftNewPoint12;
            driftLastPoint21[id] = driftNewPoint21;
            driftLastPoint22[id] = driftNewPoint22;
        } else {
            driftLastPoint11[id] = null;
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        window_width = getWidth();
        window_height = getHeight();
        Vector pos = cars[main_car_i].position;
        int x_img_start = (int) pos.x - window_width / 2;
        int y_img_start = (int) pos.y - window_height / 2;

        if (x_img_start < 0) x_img_start = 0;
        if (y_img_start < 0) y_img_start = 0;
        if (x_img_start + window_width > track_width) x_img_start = track_width - window_width;
        if (y_img_start + window_height > track_height) y_img_start = track_height - window_height;

        g3.setBackground(new Color(0, 0, 0, 0));
        g3.clearRect(x_img_start, y_img_start, window_width, window_height);

        for (int i = 0; i < 10; i++) {
            saved_positions[i] = new Vector(cars[i].position);
        }
        drawCheckpoint();
        for (int i = 0; i < 10; i++){
            if (i == main_car_i) continue;
            drawCar(i);
        }
        drawCar(main_car_i);

        g4.drawImage(track_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g4.drawImage(particles_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g4.drawImage(cars_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g2d.drawImage(final_img.getSubimage(0, 0, window_width, window_height), 0, 0, null);
    }

    private void setCarsImages() throws IOException {
        String[] cars_names = {"Audi","BMW","Bugatti","Chevrolet","Dodge","Ferrari","Ford","Honda","Lamborghini","Mazda","Mercedes","Mitsubishi","Nissan","Porsche","Subaru","Toyota"};
        for (int i = 0; i < 16; i++){
            carsImages[i] = ImageIO.read(getClass().getResourceAsStream("Cars images/" + cars_names[i] + ".png"));
        }
    }

    private void setCheckpoints(){
        checkpointPositions = new Vector[27];
        checkpointAngles = new double[27];
        checkpointPositions[0] = new Vector(3676,540);
        checkpointAngles[0] = 0;
        checkpointPositions[1] = new Vector(4830,549);
        checkpointAngles[1] = 0;
        checkpointPositions[2] = new Vector(5116,826);
        checkpointAngles[2] = -1.112503719211325;
        checkpointPositions[3] = new Vector(5468,1000);
        checkpointAngles[3] = -0.3288960991333516;
        checkpointPositions[4] = new Vector(5536,1299);
        checkpointAngles[4] = -2.1060975101506334;
        checkpointPositions[5] = new Vector(5251,1348);
        checkpointAngles[5] = -3.558744174829935;
        checkpointPositions[6] = new Vector(4545,1073);
        checkpointAngles[6] = -3.0944246497615637;
        checkpointPositions[7] = new Vector(4251,1323);
        checkpointAngles[7] = -1.753669373145075;
        checkpointPositions[8] = new Vector(4526,1672);
        checkpointAngles[8] = 3.5947220671039837E-4;
        checkpointPositions[9] = new Vector(4948,1635);
        checkpointAngles[9] = -0.28753200995894623;
        checkpointPositions[10] = new Vector(5288,2148);
        checkpointAngles[10] = -1.7584388300659155;
        checkpointPositions[11] = new Vector(3941,1971);
        checkpointAngles[11] = -3.7340575093724144;
        checkpointPositions[12] = new Vector(3771,1098);
        checkpointAngles[12] = -4.192728790824393;
        checkpointPositions[13] = new Vector(2533,1041);
        checkpointAngles[13] = -2.9214534507935124;
        checkpointPositions[14] = new Vector(2026,1672);
        checkpointAngles[14] = -2.358457839969165;
        checkpointPositions[15] = new Vector(1034,1817);
        checkpointAngles[15] = -3.1283481945408504;
        checkpointPositions[16] = new Vector(564,1400);
        checkpointAngles[16] = 1.5659537135433514;
        checkpointPositions[17] = new Vector(696,656);
        checkpointAngles[17] = 0.7980391069067672;
        checkpointPositions[18] = new Vector(1480,520);
        checkpointAngles[18] = -0.012317676442719083;
        checkpointPositions[19] = new Vector(1785,836);
        checkpointAngles[19] = -1.8907599358774347;
        checkpointPositions[20] = new Vector(1404,925);
        checkpointAngles[20] = -3.6439499270920903;
        checkpointPositions[21] = new Vector(1028,956);
        checkpointAngles[21] = -2.4040807972281097;
        checkpointPositions[22] = new Vector(1002,1418);
        checkpointAngles[22] = -0.7592230738443682;
        checkpointPositions[23] = new Vector(1651,1430);
        checkpointAngles[23] = 0.5052835132100837;
        checkpointPositions[24] = new Vector(2315,611);
        checkpointAngles[24] = 0.6347643042246949;
        checkpointPositions[25] = new Vector(3027,539);
        checkpointAngles[25] = 0.012544783416844618;
        checkpointPositions[26] = new Vector(3676,540);
        checkpointAngles[26] = 0;
    }
}
