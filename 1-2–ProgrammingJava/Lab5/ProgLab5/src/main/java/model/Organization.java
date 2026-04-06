package model;
import java.time.LocalDateTime;

public class Organization implements Comparable<Organization> {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer annualTurnover; //Поле не может быть null, Значение поля должно быть больше 0
    private String fullName; //Длина строки не должна быть больше 1155, Значение этого поля должно быть уникальным, Поле не может быть null
    private OrganizationType type; //Поле может быть null
    private Address officialAddress; //Поле не может быть null

    public Organization(String name, Coordinates coordinates, Integer annualTurnover, String fullName, OrganizationType type, Address officialAddress) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.annualTurnover = annualTurnover;
        this.fullName = fullName;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    public Organization() {
        //пустой для Jackson
    }

    //функция для сортировки экземпляров
    @Override
    public int compareTo(Organization other) {
        if (other == null) return 1; //просто так, в качестве хорошего тона
        //String уже реализует метод compareTo, а так как name - String, то сравнение будет корректным
        return this.fullName.compareTo(other.getName()); //лексикографически
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public Integer getAnnualTurnover() {
        return annualTurnover;
    }
    public String getFullName() {
        return fullName;
    }
    public Address getOfficialAddress() {
        return officialAddress;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    //отдельно определим метод для установки id (он пригодится в других классах)
    public void setId(Long id) {
        this.id = id;
    };

    @Override
    public String toString() {
        return "Organization {ID: " + id + ",\n name: " + name + ",\n coordinates: " + coordinates +
                ",\n creationDate: " + creationDate + ",\n annualTurnover: " + annualTurnover +
                ",\n fullName: " + fullName + ",\n type: " + type +
                ",\n officialAddress: " + officialAddress + "}";
    }
}
