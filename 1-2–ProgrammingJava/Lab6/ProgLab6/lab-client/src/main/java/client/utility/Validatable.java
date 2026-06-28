package client.utility;

//интерфейс для валидации
public interface Validatable<T> {
    boolean validate(T value);
}
