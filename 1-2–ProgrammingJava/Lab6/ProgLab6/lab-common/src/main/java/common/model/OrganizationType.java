package common.model;

/**
 * Перечисление всех доступных типов организации
 * @author mikhailkramar
 */
public enum OrganizationType {
    COMMERCIAL,
    GOVERNMENT,
    TRUST,
    PRIVATE_LIMITED_COMPANY,
    OPEN_JOINT_STOCK_COMPANY;

    /**
     * Вывод всех доступных типов в консоль
     */
    public static void printTypeValues() {
        for (OrganizationType ot : OrganizationType.values()) {
            System.out.println(ot);
        }
    }
}
