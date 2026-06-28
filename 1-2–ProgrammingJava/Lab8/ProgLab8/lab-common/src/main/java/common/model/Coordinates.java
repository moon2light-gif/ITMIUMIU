package common.model;

import java.io.Serializable;

/**
 * Класс координат, которые используются в Organization
 * @author mikhailkramar
 */
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    private float x; //Значение поля должно быть больше -694
    private Long y; //Максимальное значение поля: 748, Поле не может быть null

    public Coordinates(float x, Long y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {
        //опять для Jackson
    }

    /**
     * Метод получения поля x
     * @return x координата
     */
    public float getX() {return x;}

    /**
     * Метод для получения поля y
     * @return y координата
     */
    public Long getY() {return y;}

    @Override
    public String toString() {
        return "X = " + x + ", Y = " + y;
    }
}

