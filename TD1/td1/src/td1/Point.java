package td1;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void affiche(){
        System.out.println("P=(" + this.x + "," + this.y + ")");
    }


    public void translate(double dx, double dy){
        this.x = dx;
        this.y = dy;
    }

  
}


