package environment;
import mainObjects.*;
import java.util.Objects;

import java.util.ArrayList;

public class Workbench {
    private final ArrayList<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    // метод поиска предмета на верстаке
    public Item findItem(String name) {
        for (Item item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        throw new NoItemException(name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return "Workbench{items=" + items + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Workbench other = (Workbench) obj;
        return Objects.equals(this.items, other.items);

    }
}
