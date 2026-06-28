package common.model;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Класс, описывающий элемент коллекции Organization
 * @author mikhailkramar
 */
public class Organization implements Comparable<Organization>, Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer annualTurnover; //Поле не может быть null, Значение поля должно быть больше 0
    private String fullName; //Длина строки не должна быть больше 1155, Значение этого поля должно быть уникальным, Поле не может быть null
    private OrganizationType type; //Поле может быть null
    private Address officialAddress; //Поле не может быть null

    private Long ownerId; //ID пользователя, который добавил этот объект в коллекцию

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
        //пустой конструктор :O
    }

    /**
     * Реализация сортировки по умолчанию
     * @param other объект, с которым сравниваем
     * @return int == 0, если объект равен передаваемому, int > 0, если текущий объект "больше" передаваемого, иначе int < 0
     */
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
    public OrganizationType getType() {
        return type;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Метод для получения идентификационного номера пользователя, который добавил конкретную организацию
     * @return ID пользователя
     */
    public Long getOwnerId() {
        return ownerId;
    }

    /**
     * Метод установки ID конкретной организации
     * @param id номер, который хотим установить
     */
    public void setId(Long id) {
        this.id = id;
    };

    /**
     * Метод установки идентификационного номера пользователя, который добавил объект Organization
     * @param ownerId ID пользователя
     */
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Organization {ID: " + id + ",\n name: " + name + ",\n coordinates: " + coordinates +
                ",\n creationDate: " + creationDate + ",\n annualTurnover: " + annualTurnover +
                ",\n fullName: " + fullName + ",\n type: " + type +
                ",\n officialAddress: " + officialAddress + ",\n ownerId: " + ownerId + "}";
    }
}
