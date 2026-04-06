package utility;

import model.Address;
import model.Organization;
import service.CollectionManager;

import java.time.LocalDateTime;

//общий валидатор для DumpManager'а и ElementReader'а
public class Validator {
    //проверки для отдельных полей, которые используются в ElementReader
    public static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    //тут x может быть null в xml, если на его месте ничего не записано, значит пишем как объект
    public static boolean validateX(Float x) {
        return x != null && x > -694;
    }

    public static boolean validateY(Long y) {
        return y != null && y <= 748;
    }

    public static boolean validateAnnualTurnover(Integer turnover) {
        return turnover != null && turnover > 0;
    }

    public static boolean validateFullName(String fullName, CollectionManager cm) {
        if (fullName == null || fullName.isEmpty() || fullName.length() > 1155) return false;
        return cm == null || cm.fullNameIsUnique(fullName);
    }

    //для поля Type нет валидации, потому что он может быть null, а наличие типа прописано в ElementReader'е

    //проверка всего объекта адреса для xml
    public static boolean validateAddress(Address address) {
        if (address == null || address.getStreet() == null) return false;
        return !address.getStreet().isEmpty();
    }

    //проверка именно улицы (String) для ввода с клавиатуры
    public static boolean validateStreet(String street) {
        return street != null && !street.isEmpty();
    }

    //только для xml и DumpManager
    public static boolean validateCreationDate(LocalDateTime date) {
        return date != null;
    }

    //теперь общая проверка для нашего DumpManager
    public static boolean validateOrganization(Organization org, CollectionManager cm) {
        if (org == null) return false;
        return validateName(org.getName())
                && validateCreationDate(org.getCreationDate())
                && org.getCoordinates() != null
                && validateX(org.getCoordinates().getX())
                && validateY(org.getCoordinates().getY())
                && validateAnnualTurnover(org.getAnnualTurnover())
                && validateFullName(org.getFullName(), cm)
                && validateAddress(org.getOfficialAddress());
    }
}
