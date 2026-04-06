package utility;

//для превращения строки в нужный нам объект
public interface Parser<T> {
    T parse(String input) throws Exception;
}
