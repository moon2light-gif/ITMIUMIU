package environment;
import mainObjects.*;
import java.util.Objects;

public class Item implements Illuminable {
    private final String name;
    private final Position pos;
    private boolean visible;
    //конструктор для предмета
    public Item(String name, Position pos) {
        this.name = name;
        this.pos = pos;
    }
    //getter названия предмета
    public String getName() {
        return name;
    }

    public Position getPos() {
        return pos;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void illuminate() {
        visible = true;
        System.out.print("освещён");
    }
    @Override
    public void hide() {
        visible = false;
        System.out.print("не видим");
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pos);
    }

    @Override
    public String toString() {
        return "Item{name=" + name + ", pos=" + pos + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        Item other = (Item) object;
        return Objects.equals(this.name, other.name) && Objects.equals(this.pos, other.pos);
    }

}
