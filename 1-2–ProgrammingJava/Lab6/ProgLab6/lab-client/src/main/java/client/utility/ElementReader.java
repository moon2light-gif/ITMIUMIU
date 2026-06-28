package client.utility;

import common.exceptions.InvalidInputException;
import common.model.Address;
import common.model.OrganizationType;
import common.transport.Request;
import common.utility.Validator;

import java.util.Scanner;

/**
 * Класс для обработки ввода элементов типа Organization
 *
 * @author mikhailkramar
 */
public class ElementReader {
    private Scanner scanner;
    private boolean interactive = true;
    private RequestBuilder requestBuilder;

    public ElementReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setRequestBuilder(RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    //переключение сканера (для файла)
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    //метод для чтения объектов
    private <T> T ask(String message, String error, Parser<T> parser, Validatable<T> validator) {
        T value = null;
        boolean isValid = false;
        while(!isValid) {
            if (interactive) {
                System.out.println(message + ": ");
            }

            //проверка ctrl+d внутри пошагового ввода
            if (!scanner.hasNextLine()) {
                throw new RuntimeException("Ввод прерван!");
            }

            String input = scanner.nextLine().trim(); //считали ввод, убрали лишние пробелы
            try {
                //превращаем строку в нужный объект
                value = parser.parse(input);
                if (validator.validate(value)) {
                    isValid = true; //выход из цикла
                }
                else {
                    throw new InvalidInputException(error);
                }
            } catch (InvalidInputException ex) {
                if (!interactive) {
                    throw new RuntimeException("Ошибка валидации в скрипте: " + ex.getMessage());
                }
                System.out.println(ex.getMessage());
            } catch (Exception ex) {
                if (!interactive) {
                    throw new RuntimeException("Некорректный формат данных в скрипте.");
                }
                //сюда мы попадем если сломался парсинг, мы могли ввести буквы вместо цифр или что-нибудь ещё
                System.out.println("Ошибка: Некорректный формат данных! Попробуйте снова.");
            }
        }
        return value;
    }

    //методы для полей Organization
    public String askName() {
        return ask("Введите название организации", "Название не может быть пустым!",
                (input) -> input, Validator::validateName);
    };
    public float askX() {
        return ask("Введите поле X (> -694)", "X должен быть больше -694!",
                Float::parseFloat, Validator::validateX);
    };
    public Long askY() {
        return ask("Введите поле Y (<= 748)", "Y должен иметь значение не более 748!",
                (input) -> { if(input.isEmpty()) throw new Exception();// Y не может быть nullом!
                    return Long.parseLong(input);
                }, Validator::validateY);
    };
    public Integer askAnnualTurnover() {
        return ask("Введите годовой оборот", "Годовой оборот должен быть больше 0!",
                Integer::parseInt, Validator::validateAnnualTurnover);
    };
    public String askFullName() {
        return ask("Введите полное название организации", "Полное название должно быть уникальным и состоять из не более чем 1155 символов!",
                (input) -> input, (input) -> {
                    if (!Validator.validateFullName(input, null)) {
                        return false;
                    }

                    return requestBuilder.validateArgsOnServer("check_name", input).isSuccess();

                });
    }
    public OrganizationType askType() {
        if (interactive) {
            System.out.println("Доступные типы:");
            OrganizationType.printTypeValues();
        }
        return ask("Выберите тип (Enter для null)", "Такого типа нет!",
                (input) -> input.isEmpty() ? null : OrganizationType.valueOf(input.toUpperCase()),
                (input) -> true);
    }
    public Address askOfficialAddress() {
        String street = ask("Введите адрес", "Адрес не может быть пустым!",
                (input) -> input, Validator::validateStreet);
        return new Address(street);
    };


}
