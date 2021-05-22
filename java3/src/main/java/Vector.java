
public class Vector {

    public double x;
    public double y;

    public Vector() {
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void rotate(double angle) {
        double nx = x * Math.cos(angle) - y * Math.sin(angle);
        double ny = x * Math.sin(angle) + y * Math.cos(angle);
        set(nx, ny);
    }

    public void translate(double x, double y, double z) {
        set(this.x + x, this.y + y);
    }
    
    public double getSize() {
        return Math.sqrt(x * x + y * y);
    }
    
    public void normalize() {
        double size = getSize();
        x /= size;
        y /= size;
    }
    
    public void scale(double f) {
        x *= f;
        y *= f;
    }
    
    public void add(Vector v) {
        x += v.x;
        y += v.y;
    }
    
    public void sub(Vector v) {
        x -= v.x;
        y -= v.y;
    }

    public double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    public static double angleBetween(Vector a, Vector b) {
	double am = a.getSize();
	double bm = b.getSize();
	return Math.acos(a.dot(b) / (am * bm));
    }
    
    public static void sub(Vector r, Vector a, Vector b) {
        r.x = a.x - b.x;
        r.y = a.y - b.y;
    }
    
    public double getRelativeAngleBetween(Vector v) {
        return getSign(v) * Math.acos(dot(v) / (getSize() * v.getSize()));
    }

    public int getSign(Vector v) {
        return (y*v.x > x*v.y)?-1:1;
    }
}
