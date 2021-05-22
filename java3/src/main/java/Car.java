import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EventListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class Car {
    private BufferedImage car_img= ImageIO.read(getClass().getResourceAsStream("suba.png"));

    public double mass_cf = 2800;
    public double braking_cf = 500;
    public double drag_cf = 0.4257;
    public double rolling_resistance_cf = 12.8;
    public double max_engine_cf = 500;
    public double engine_acceleration_cf = 40;
    public double tyre_cf = 0.4;
    public double drift_cf = 5;
    public double drift_angle_cf = 30;

    public Vector position = new Vector();
    public Vector direction = new Vector(0, 1);
    public Vector velocity = new Vector();
    public Vector acceleration = new Vector();

    public boolean isBraking = false;
    public Vector fBraking = new Vector();

    public double engineForce = 0;
    public Vector fTraction = new Vector();

    public Vector fDrag = new Vector();

    public Vector fRolingResistence = new Vector();

    public Vector fLongtitudinal = new Vector();

    public boolean isDrifting;

    public Car() throws IOException {
    }

    public void update(boolean[] keyboard) {

        double dif = tyre_cf * (velocity.getSize() / 60.0);

        if (keyboard[37]) {
            if (velocity.getSize() < 10) direction.rotate(Math.toRadians((-tyre_cf) * (velocity.getSize())));
            else direction.rotate(Math.toRadians((-drag_cf) * 200/(10 + velocity.getSize())));
        }
        else if (keyboard[39]) {
            if (velocity.getSize() < 10) direction.rotate(Math.toRadians((tyre_cf) * (velocity.getSize())));
            else direction.rotate(Math.toRadians((drag_cf) * 200/(10 + velocity.getSize())));
        }

        double difAngle = velocity.getRelativeAngleBetween(direction);
        if (!Double.isNaN(difAngle)){
            velocity.rotate(difAngle/((75)*(5*dif)));
            isDrifting = Math.abs(Math.toDegrees(difAngle)) > drift_angle_cf && velocity.getSize() > 1;
        }

        if (keyboard[38]) {
            engineForce += engine_acceleration_cf;
            if (engineForce > max_engine_cf) engineForce = max_engine_cf;
        } else {
            engineForce = 0;
        }

        if (isDrifting) {
            engineForce = engineForce / drift_cf;
        }

        isBraking = keyboard[40];

        calculateBraking();
        calculateTraction();
        calculateDrag();
        calculateRolingResistence();
        calculateLongtitudinalForce(isBraking);
        calculateAcceleration();
        calculateVelocity();
        calculatePosition();
    }

    private Vector driftLastPoint11;
    private Vector driftLastPoint12;
    private Vector driftLastPoint21;
    private Vector driftLastPoint22;

    public void draw(Graphics2D graphics_car, Graphics2D graphics_tires_traces) {
        double angle = -Math.atan2(direction.x, direction.y);
        int pox = (int) (position.x);
        int poy = (int) (position.y);
        graphics_car.translate(pox, poy);
        graphics_car.rotate(angle);
        graphics_car.drawImage(car_img,-65,-65,null);
        graphics_car.rotate(-angle);
        graphics_car.translate(-pox, -poy);
        if (isDrifting || isBraking) {
            Vector driftNewPoint11 = new Vector (10,-30);
            Vector driftNewPoint12 = new Vector (20,-30);
            Vector driftNewPoint21 = new Vector (-10,-30);
            Vector driftNewPoint22 = new Vector (-20,-30);
            driftNewPoint11.rotate(angle);
            driftNewPoint12.rotate(angle);
            driftNewPoint21.rotate(angle);
            driftNewPoint22.rotate(angle);
            driftNewPoint11.add(position);
            driftNewPoint12.add(position);
            driftNewPoint21.add(position);
            driftNewPoint22.add(position);
            if (driftLastPoint11 != null){
                graphics_tires_traces.setColor(new Color(0, 0, 0, 50));
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

    private void calculateRolingResistence() {
        fRolingResistence.set(velocity);
        fRolingResistence.scale(-rolling_resistance_cf);
    }

    private void calculateLongtitudinalForce(boolean isBraking) {
        if (isBraking) {
            fLongtitudinal.set(fBraking);
        }
        else {
            fLongtitudinal.set(fTraction);
        }
        fLongtitudinal.add(fDrag);
        fLongtitudinal.add(fRolingResistence);
    }

    private void calculateAcceleration() {
        acceleration.set(fLongtitudinal);
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

}
