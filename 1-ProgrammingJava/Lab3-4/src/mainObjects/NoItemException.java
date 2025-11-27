package mainObjects;

public class NoItemException extends RuntimeException {
    private final String item;

    public NoItemException(String item) {
        this.item = item;
    }

    @Override
    public String getMessage() {
        return "Предмета " + item + " нет";
    }

}
