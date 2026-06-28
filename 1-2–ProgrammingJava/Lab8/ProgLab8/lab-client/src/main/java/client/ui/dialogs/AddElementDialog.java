package client.ui.dialogs;

import client.connection.Client;
import client.utility.LanguageManager;
import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Класс {@code AddElementDialog} представляет собой диалоговое окошко добавления новой организации
 *
 * @author mikhailkramar
 */
public class AddElementDialog extends Dialog<Organization> {

    private final LanguageManager lm = LanguageManager.getInstance();

    public AddElementDialog(Client client, String login, String password, boolean addIfMin) {
        OrganizationFormPane form = new OrganizationFormPane();

        setTitle(lm.get(addIfMin ? "dialog.addIfMin.title" : "dialog.add.title"));
        setHeaderText(null);

        ButtonType okType = new ButtonType(lm.get("button.add.confirm"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType(lm.get("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okType, cancelType);
        getDialogPane().setContent(form);
        try {
            getDialogPane().getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        } catch (Exception ignored) { }

        Button okButton = (Button) getDialogPane().lookupButton(okType);
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            String err = form.validateLocal();
            if (err != null) {
                showError(err);
                e.consume();
                return;
            }

            //тут проверяем уникальность полного названия на сервере

            Response resp = client.sendAndReceive(new Request(login, password, "check_name", form.getFullNameValue(), null));
            if (resp == null) {
                showError(lm.get("msg.serverUnavailable"));
                e.consume();
                return;
            }
            if (!resp.isSuccess()) {
                showError(lm.get("valid.fullNameTaken"));
                e.consume();
            }
        });

        setResultConverter(bt -> bt == okType ? form.buildOrganization() : null);
    }

    /**
     * Метод для вывода ошибочек
     * @param message само сообщение об ошибке
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(lm.get("alert.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
