import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Random;

public class Car {
    private BufferedImage car_img;
    private final BufferedImage checkpoint_img = ImageIO.read(getClass().getResourceAsStream("checkpoint.png"));
    private String[] cars_names = {"Audi","BMW","Bugatti","Chevrolet","Dodge","Ferrari","Ford","Honda","Lamborghini","Mazda","Mercedes-AMG","Mitsubishi","Nissan","Porsche","Subaru","Toyota"};

    private final int number;

    private double timerStart;

    private String car_name;
    private int car_id;
    private double mass_cf;
    private double braking_cf;
    private double drag_cf; // less - more speed
    private double rolling_resistance_cf; // less - more speed
    private double max_engine_cf;
    private double small_tyre_cf; // more - better rotation at low speed
    private double tyre_cf; // more - better rotation
    private double drift_cf; // less - more power in drift
    private double drift_angle_cf; // angle at witch drift starts

    private final Vector position = new Vector();
    private final Vector direction = new Vector();
    private final Vector velocity = new Vector();
    private final Vector acceleration = new Vector();

    private boolean isBraking = false;
    private final Vector fBraking = new Vector();
    private double engineForce = 0;
    private final Vector fTraction = new Vector();
    private final Vector fDrag = new Vector();
    private final Vector fRollingResistance = new Vector();
    private final Vector fLongitudinal = new Vector();
    private boolean isDrifting = false;

    private int checkpointCurrent;
    private Vector[] checkpointPositions;
    private double[] checkpointAngles;

    private Records records;

    public Car(int i) throws IOException {
        direction.set(1,0);
        position.set(3550 - i % 5 * 130,497 + i / 5 * 85);
        number = i;
        setCheckpoints();
        timerStart = System.currentTimeMillis();
        int rnd = new Random().nextInt(cars_names.length);
        setCar(cars_names[rnd]);
    }

    public void reset(){
        direction.set(1,0);
        position.set(3550 - number % 5 * 130,497 + number / 5 * 85);
        velocity.set(0,0);
        isDrifting = false;
        checkpointCurrent = 0;
        timerStart = System.currentTimeMillis();
    }

    public void setCar(int name_id) throws IOException {
        setCar(cars_names[name_id]);
    }

    public boolean setCar(String carName) throws IOException {
        BufferedReader cars_settings = new BufferedReader(new FileReader("src/main/resources/cars_cfg.txt"));
        String s;
        String[] words;
        carName = carName.substring(0, 1).toUpperCase() + carName.substring(1);
        while((s = cars_settings.readLine())!=null){
            words = s.split(" ");
            for (String word : words){
                if (word.equals(carName)){
                    car_name = s;
                    String input = cars_settings.readLine();
                    if (!input.equals("////")) return false;
                    input = cars_settings.readLine();
                    car_id = Integer.parseInt(input);
                    input = cars_settings.readLine();
                    car_img = ImageIO.read(getClass().getResourceAsStream("Cars images/" + input + ".png"));
                    input = cars_settings.readLine();
                    mass_cf = Double.parseDouble(input);
                    input = cars_settings.readLine();
                    braking_cf = Double.parseDouble(input);
                    input = cars_settings.readLine();
                    drag_cf = Double.parseDouble(input);
                    input = cars_settings.readLine();
                    rolling_resistance_cf = Double.parseDouble(input);
                    input = cars_settings.readLine();
                    max_engine_cf = Double.parseDouble(input);
                    input = cars_settings.readLine();
                    small_tyre_cf = Double.parseDouble(input);
                    input = cars_settings.readLine();
                    tyre_cf = Double.parseDouble(input);
                    input = cars_settings.readLine();
                    drift_cf = Double.parseDouble(input);
                    input = cars_settings.readLine();
                    drift_angle_cf = Double.parseDouble(input);
                    return true;
                }
            }
        }
        return false;
    }

    private void setCheckpoints(){
        checkpointCurrent = 0;
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

    public void setRecords(Records records){
        this.records = records;
    }

    public double[] getValues(){
        double[] values = new double[8];
        values[0] = position.x;
        values[1] = position.y;
        values[2] = direction.x;
        values[3] = direction.y;
        values[4] = velocity.x;
        values[5] = velocity.y;
        values[6] = acceleration.x;
        values[7] = acceleration.y;
        return values;
    }

    public void setValues(double x, double y, double dX, double dY, double vX, double vY, double aX, double aY){
        position.x = x;
        position.y = y;
        direction.x = dX;
        direction.y = dY;
        velocity.x = vX;
        velocity.y = vY;
        acceleration.x = aX;
        acceleration.y = aY;
    }

    public void update(boolean[] keyboard) throws IOException {
        if (keyboard[37] || keyboard[65]) {
            if (velocity.getSize() < 10) direction.rotate(Math.toRadians((-small_tyre_cf) * (velocity.getSize())));
            else direction.rotate(Math.toRadians((-tyre_cf) * 200 / (10 + velocity.getSize())));
        }
        if (keyboard[39] || keyboard[68]) {
            if (velocity.getSize() < 10) direction.rotate(Math.toRadians((small_tyre_cf) * (velocity.getSize())));
            else direction.rotate(Math.toRadians((tyre_cf) * 200 / (10 + velocity.getSize())));
        }

        if (keyboard[38] || keyboard[87]) {
            engineForce = max_engine_cf;
        } else {
            engineForce = 0;
        }

        if (isDrifting) {
            engineForce = engineForce / drift_cf;
        }

        if (keyboard[84]) {
            System.out.println((int)position.x + "," + (int)position.y);
            //System.out.println(- Math.PI/2 + Math.atan2(direction.x, direction.y));
        }

        isBraking = keyboard[32] || keyboard[83] || keyboard[40];

        calculateRotation();
        calculateBraking();
        calculateTraction();
        calculateDrag();
        calculateRollingResistance();
        calculateLongitudinalForce();
        calculateAcceleration();
        calculateVelocity();
        calculatePosition();
        checkCheckpoint();
    }

    private Vector driftLastPoint11;
    private Vector driftLastPoint12;
    private Vector driftLastPoint21;
    private Vector driftLastPoint22;

    public void draw(Graphics2D graphics_car, Graphics2D graphics_tires_traces, Vector saved_position, Vector main_car_saved_position, boolean is_main_car) {
        if (is_main_car){
            graphics_car.translate(checkpointPositions[checkpointCurrent].x, checkpointPositions[checkpointCurrent].y);
            graphics_car.rotate(-checkpointAngles[checkpointCurrent]);
            graphics_car.drawImage(checkpoint_img,-80, -80,null);
            graphics_car.rotate(checkpointAngles[checkpointCurrent]);
            graphics_car.translate(-checkpointPositions[checkpointCurrent].x, -checkpointPositions[checkpointCurrent].y);
        }

        double angle = -Math.atan2(direction.x, direction.y);
        int pox = (int) (saved_position.x);
        int poy = (int) (saved_position.y);
        graphics_car.translate(pox, poy);
        graphics_car.rotate(angle);
        if (!is_main_car && Vector.distanceBetween(saved_position,main_car_saved_position) < 100){
            AlphaComposite acomp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
            graphics_car.setComposite(acomp);
        }
        graphics_car.drawImage(car_img,-50,-50,null);
        AlphaComposite acomp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        graphics_car.setComposite(acomp);
        graphics_car.rotate(-angle);
        graphics_car.translate(-pox, -poy);

        if (isDrifting || isBraking) {
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
            if (driftLastPoint11 != null){
                if (is_main_car) graphics_tires_traces.setColor(new Color(0, 0, 0, 50));
                else graphics_tires_traces.setColor(new Color(0, 0, 0, 30));
                Polygon polygon1 = new Polygon();
                polygon1.addPoint((int)driftNewPoint11.x, (int)driftNewPoint11.y);
                polygon1.addPoint((int)driftNewPoint12.x, (int)driftNewPoint12.y);
                polygon1.addPoint((int)driftLastPoint12.x, (int)driftLastPoint12.y);
                polygon1.addPoint((int)driftLastPoint11.x, (int)driftLastPoint11.y);
                graphics_tires_traces.fillPolygon(polygon1);
                Polygon polygon2 = new Polygon();
                polygon2.addPoint((int)driftNewPoint21.x, (int)driftNewPoint21.y);
                polygon2.addPoint((int)driftNewPoint22.x, (int)driftNewPoint22.y);
                polygon2.addPoint((int)driftLastPoint22.x, (int)driftLastPoint22.y);
                polygon2.addPoint((int)driftLastPoint21.x, (int)driftLastPoint21.y);
                graphics_tires_traces.fillPolygon(polygon2);
            }
            driftLastPoint11 = driftNewPoint11;
            driftLastPoint12 = driftNewPoint12;
            driftLastPoint21 = driftNewPoint21;
            driftLastPoint22 = driftNewPoint22;
        } else {
            driftLastPoint11 = null;
        }
    }

    private void calculateRotation(){
        double dif = tyre_cf * (velocity.getSize() / 60.0);
        double difAngle = velocity.getRelativeAngleBetween(direction);
        if (!Double.isNaN(difAngle)){
            velocity.rotate(difAngle/((75)*(5*dif)));
            isDrifting = Math.abs(Math.toDegrees(difAngle)) > drift_angle_cf && velocity.getSize() > 1;
        }
    }

    private void calculateTraction() {
        fTraction.set(direction);
        fTraction.normalize();
        fTraction.scale(engineForce);
    }

    private void calculateDrag() {
        double speed = velocity.getSize();
        fDrag.set(velocity);
        fDrag.scale(speed);
        fDrag.scale(-drag_cf);
    }

    private void calculateRollingResistance() {
        fRollingResistance.set(velocity);
        fRollingResistance.scale(-rolling_resistance_cf);
    }

    private void calculateLongitudinalForce() {
        if (isBraking && !Double.isNaN(fBraking.getSize())) {
            fLongitudinal.set(fBraking);
        }
        else {
            fLongitudinal.set(fTraction);
        }
        fLongitudinal.add(fDrag);
        fLongitudinal.add(fRollingResistance);
    }

    private void calculateAcceleration() {
        acceleration.set(fLongitudinal);
        acceleration.scale(1/mass_cf);
    }

    private void calculateVelocity() {
        velocity.add(acceleration);
    }

    private void calculatePosition() {
        position.add(velocity);
    }

    private void calculateBraking() {
        fBraking.set(velocity);
        fBraking.normalize();
        fBraking.scale(-braking_cf);
    }

    private void checkCheckpoint() throws IOException {
        if (Vector.distanceBetween(position,checkpointPositions[checkpointCurrent]) < 100){
            checkpointCurrent++;
            if (checkpointCurrent == 27){
                double timerStop = System.currentTimeMillis();
                records.addRecord(timerStop - timerStart, car_name);
                timerStart = System.currentTimeMillis();
                checkpointCurrent = 0;
            }
        }
    }

    public void dumbAI(boolean[] keyboard){
        Vector checkpoint = new Vector(checkpointPositions[checkpointCurrent]);
        checkpoint.sub(position);
        double angle = direction.getRelativeAngleBetween(checkpoint);
        double velocityAngle = velocity.getRelativeAngleBetween(checkpoint);
        keyboard[87] = true;
        if (angle < -0.05) {
            keyboard[65] = true;
            keyboard[68] = false;
        } else if (angle > 0.05) {
            keyboard[65] = false;
            keyboard[68] = true;
        } else {
            keyboard[65] = false;
            keyboard[68] = false;
        }
        keyboard[32] = Math.abs(velocityAngle) * velocity.getSize() * (2500 - Vector.distanceBetween(position,checkpointPositions[checkpointCurrent])) > 15000;
    }

    public Vector getPosition(){
        return position;
    }

    public int getCar_id(){
        return car_id;
    }
}
