package model;

public class Coordinates {
    private float x; //Значение поля должно быть больше -694
    private Long y; //Максимальное значение поля: 748, Поле не может быть null

    public Coordinates(float x, Long y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {
        //опять для Jackson
    }

    public float getX() {return x;}
    public Long getY() {return y;}

    @Override
    public String toString() {
        return "X = " + x + ", Y = " + y;
    }
}

