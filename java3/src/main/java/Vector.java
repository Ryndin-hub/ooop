public class Vector {

    public double x;
    public double y;

    public Vector() {
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Vector v) {
        this.x = v.x;
        this.y = v.y;
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

    public void sub(Vector a, Vector b) {
        x = a.x - b.x;
        y = a.y - b.y;
    }


    public double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    public static double angleBetween(Vector a, Vector b) {
	    double am = a.getSize();
	    double bm = b.getSize();
	    return Math.acos(a.dot(b) / (am * bm));
    }

    public static double distanceBetween(Vector a, Vector b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public double getRelativeAngleBetween(Vector v) {
        return getSign(v) * Math.acos(dot(v) / (getSize() * v.getSize()));
    }

    public int getSign(Vector v) {
        return (y*v.x > x*v.y)?-1:1;
    }
}
