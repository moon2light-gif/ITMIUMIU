package common.utility;

import common.model.Address;
import common.model.Organization;
import java.time.LocalDateTime;

/**
 * Общий класс для валидации полей организации для DumpManager и ElementReader
 *
 * @author mikhailkramar
 */
public class Validator {
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

    public static boolean validateFullName(String fullName, UniqueNameChecker checker) {
        return fullName != null && !fullName.isEmpty() && fullName.length() <= 1155 &&
                (checker == null || checker.isUnique(fullName));
    }

    public static boolean validateAddress(Address address) {
        if (address == null || address.getStreet() == null) return false;
        return !address.getStreet().isEmpty();
    }

    public static boolean validateStreet(String street) {
        return street != null && !street.isEmpty();
    }

    public static boolean validateCreationDate(LocalDateTime date) {
        return date != null;
    }

    public static boolean validateOrganization(Organization org, UniqueNameChecker checker) {
        if (org == null) return false;
        return validateName(org.getName())
                && validateCreationDate(org.getCreationDate())
                && org.getCoordinates() != null
                && validateX(org.getCoordinates().getX())
                && validateY(org.getCoordinates().getY())
                && validateAnnualTurnover(org.getAnnualTurnover())
                && validateFullName(org.getFullName(), checker)
                && validateAddress(org.getOfficialAddress());
    }
}
