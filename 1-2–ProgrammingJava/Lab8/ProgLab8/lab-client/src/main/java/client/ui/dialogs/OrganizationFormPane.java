package client.ui.dialogs;

import client.utility.LanguageManager;
import common.model.Address;
import common.model.Coordinates;
import common.model.Organization;
import common.model.OrganizationType;
import common.utility.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code OrganizationFormPane} представляет собой переиспользуемую форму с полями организации,
 * используется в окошках редактирования и добавления
 *
 * @author mikhailkramar
 */
public class OrganizationFormPane extends GridPane {

    private final LanguageManager lm = LanguageManager.getInstance();

    private final TextField nameField = new TextField();
    private final TextField xField = new TextField();
    private final TextField yField = new TextField();
    private final TextField turnoverField = new TextField();
    private final TextField fullNameField = new TextField();
    private final ComboBox<OrganizationType> typeBox = new ComboBox<>();
    private final TextField addressField = new TextField();

    public OrganizationFormPane() {
        setHgap(12);
        setVgap(10);
        setPadding(new Insets(18));
        getStyleClass().add("form-pane");

        List<OrganizationType> types = new ArrayList<>();
        types.add(null);
        for (OrganizationType t : OrganizationType.values()) types.add(t);
        typeBox.setItems(FXCollections.observableArrayList(types));
        typeBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(OrganizationType t) {
                return (t == null) ? lm.get("form.typeNone") : t.name();
            }
            @Override
            public OrganizationType fromString(String s) {
                return null;
            }
        });
        typeBox.getSelectionModel().selectFirst();
        typeBox.setMaxWidth(Double.MAX_VALUE);

        int r = 0;
        addRow(r++, "label.name", nameField);
        addRow(r++, "label.xCoordinate", xField);
        addRow(r++, "label.yCoordinate", yField);
        addRow(r++, "label.turnover", turnoverField);
        addRow(r++, "label.fullName", fullNameField);
        addRow(r++, "label.type", typeBox);
        addRow(r++, "label.address", addressField);
    }

    private void addRow(int row, String labelKey, javafx.scene.Node field) {
        Label label = new Label(lm.get(labelKey) + ":");
        label.getStyleClass().add("form-label");
        add(label, 0, row);
        add(field, 1, row);
        if (field instanceof TextField tf) {
            tf.setPrefWidth(240);
        }
    }

    /**
     * Метод для заполнения формы организации данными
     * @param org объект какой-то организации
     */
    public void setOrganization(Organization org) {
        if (org == null) return;
        nameField.setText(org.getName());
        xField.setText(String.valueOf(org.getCoordinates().getX()));
        yField.setText(String.valueOf(org.getCoordinates().getY()));
        turnoverField.setText(String.valueOf(org.getAnnualTurnover()));
        fullNameField.setText(org.getFullName());
        typeBox.getSelectionModel().select(org.getType());
        addressField.setText(org.getOfficialAddress().getStreet());
    }

    /** Делает форму редактируемой или режимом «только чтение». */
    public void setEditable(boolean editable) {
        nameField.setEditable(editable);
        xField.setEditable(editable);
        yField.setEditable(editable);
        turnoverField.setEditable(editable);
        fullNameField.setEditable(editable);
        addressField.setEditable(editable);
        typeBox.setDisable(!editable);
    }

    public String getFullNameValue() {
        return fullNameField.getText() == null ? "" : fullNameField.getText().trim();
    }

    /**
     * Проверяет поля по правилам {@link Validator}.
     * @return текст ошибки (локализованный) или {@code null}, если всё корректно
     */
    public String validateLocal() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (!Validator.validateName(name)) return lm.get("valid.name");

        Float x;
        try {
            x = Float.parseFloat(xField.getText().trim());
        } catch (Exception e) {
            return lm.get("valid.number");
        }
        if (!Validator.validateX(x)) return lm.get("valid.x");

        Long y;
        try {
            y = Long.parseLong(yField.getText().trim());
        } catch (Exception e) {
            return lm.get("valid.number");
        }
        if (!Validator.validateY(y)) return lm.get("valid.y");

        Integer turnover;
        try {
            turnover = Integer.parseInt(turnoverField.getText().trim());
        } catch (Exception e) {
            return lm.get("valid.number");
        }
        if (!Validator.validateAnnualTurnover(turnover)) return lm.get("valid.turnover");

        String fullName = getFullNameValue();
        if (!Validator.validateFullName(fullName, null)) return lm.get("valid.fullName");

        String street = addressField.getText() == null ? "" : addressField.getText().trim();
        if (!Validator.validateStreet(street)) return lm.get("valid.address");

        return null;
    }

    /** Собирает организацию из полей (предполагается, что валидация уже пройдена). */
    public Organization buildOrganization() {
        String name = nameField.getText().trim();
        float x = Float.parseFloat(xField.getText().trim());
        long y = Long.parseLong(yField.getText().trim());
        int turnover = Integer.parseInt(turnoverField.getText().trim());
        String fullName = getFullNameValue();
        OrganizationType type = typeBox.getValue();
        String street = addressField.getText().trim();

        return new Organization(name, new Coordinates(x, y), turnover, fullName, type, new Address(street));
    }
}
