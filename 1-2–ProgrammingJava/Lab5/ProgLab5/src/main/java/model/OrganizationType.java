package model;

public enum OrganizationType {
    COMMERCIAL,
    GOVERNMENT,
    TRUST,
    PRIVATE_LIMITED_COMPANY,
    OPEN_JOINT_STOCK_COMPANY;

    public static void printTypeValues() {
        for (OrganizationType ot : OrganizationType.values()) {
            System.out.println(ot);
        }
    }
}
