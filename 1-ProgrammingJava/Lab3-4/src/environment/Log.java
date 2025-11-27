package environment;
import mainObjects.*;
import java.util.Objects;

public class Log {
    private final double size;
    private final WoodType wood;

    public Log(double size, WoodType wood) {
        this.size = size;
        this.wood = wood;
    }

    public double getSize() {
        return size;
    }
    public WoodType getWood() {
        return wood;
    }

    @Override
    public String toString() {
        return "Log{size=" + size + ", wood=" + wood + "}";
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Log other = (Log) obj;
        //первое условие означает, что эти значения равны (compare возвращает 0)
        return (Double.compare(this.size, other.size) == 0) && Objects.equals(this.wood, other.wood);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Double.valueOf(size), wood);
    }
}
