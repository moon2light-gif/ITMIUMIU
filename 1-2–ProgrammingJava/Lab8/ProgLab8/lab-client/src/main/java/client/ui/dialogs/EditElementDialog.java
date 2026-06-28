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
 * Класс {@code EditElementDialog} представляет собой типа диалог просмотра или редактирования организации
 *
 * @author mikhailkramar
 */
public class EditElementDialog extends Dialog<EditElementDialog.Result> {
    //все возможные действия, которые может выбрать пользователь в диалоговом окне
    public enum Action { SAVE, DELETE, NONE }

    //класс для результата работы этого диалогового окна
    public static final class Result {
        public final Action action;
        public final Organization organization;

        public Result(Action action, Organization organization) {
            this.action = action;
            this.organization = organization;
        }
    }

    private final LanguageManager lm = LanguageManager.getInstance();

    public EditElementDialog(Client client, String login, String password, Organization org, boolean owner) {
        OrganizationFormPane form = new OrganizationFormPane();
        form.setOrganization(org);
        form.setEditable(owner);

        setTitle(lm.get("dialog.edit.title") + " №" + org.getId());
        setHeaderText(null);
        try {
            getDialogPane().getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        } catch (Exception ignored) { }
        getDialogPane().setContent(form);

        final String originalFullName = org.getFullName();

        if (owner) {
            ButtonType saveType = new ButtonType(lm.get("button.save"), ButtonBar.ButtonData.OK_DONE);
            ButtonType deleteType = new ButtonType(lm.get("button.delete"), ButtonBar.ButtonData.OTHER);
            ButtonType cancelType = new ButtonType(lm.get("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            getDialogPane().getButtonTypes().addAll(saveType, deleteType, cancelType);

            Button saveButton = (Button) getDialogPane().lookupButton(saveType);
            saveButton.addEventFilter(ActionEvent.ACTION, e -> {
                String err = form.validateLocal();
                if (err != null) {
                    showError(err);
                    e.consume();
                    return;
                }

                //уникальность проверяю только если полное название изменилось
                if (!form.getFullNameValue().equals(originalFullName)) {
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
                }
            });

            Button deleteButton = (Button) getDialogPane().lookupButton(deleteType);
            deleteButton.addEventFilter(ActionEvent.ACTION, e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle(lm.get("alert.confirm.title"));
                confirm.setHeaderText(null);
                confirm.setContentText(lm.get("confirm.delete"));
                var result = confirm.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    e.consume(); //отмена удаления, остаемся все равно в диалоге
                }
            });

            setResultConverter(bt -> {
                if (bt == saveType) return new Result(Action.SAVE, form.buildOrganization());
                if (bt == deleteType) return new Result(Action.DELETE, org);
                return new Result(Action.NONE, null);
            });
        } else {
            ButtonType closeType = new ButtonType(lm.get("button.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
            getDialogPane().getButtonTypes().add(closeType);
            setResultConverter(bt -> new Result(Action.NONE, null));
        }
    }

    /**
     * Метод, предназначенный для вывода специфических ошибок в окошке
     * @param message сообщение нашей крутой ошибки
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(lm.get("alert.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
