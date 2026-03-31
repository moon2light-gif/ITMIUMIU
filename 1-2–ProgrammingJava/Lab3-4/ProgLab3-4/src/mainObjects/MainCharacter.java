package mainObjects;
import environment.*;

public abstract class MainCharacter {
    protected String name;
    protected String hairColor;
    protected int age;
    protected Position pos;

    public MainCharacter(String name, String hairColor, int age, Position pos) {
        this.name = name;
        this.hairColor = hairColor;
        this.age = age;
        this.pos = pos;
    }

    public abstract void Voice(String text);
    public abstract void Move(Room room);
    public abstract void lightLamp(Lamp lamp);
}
