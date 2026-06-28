package client.ui;

import client.connection.Client;
import client.utility.LanguageManager;
import common.transport.Request;
import common.transport.Response;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Locale;

/**
 * Класс {@code LoginController} представляет собой контроллер авторизации.
 *
 * @author mikhailkramar
 */
public class LoginController {
    @FXML
    private TabPane tabPane; //контейнер с вкладками
    @FXML
    private Tab loginTab; //вкладка "Вход"
    @FXML
    private Tab registerTab; //вкладка "Регистрация"
    @FXML
    private Label loginLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button submitButton;
    @FXML
    private Label errorLabel;
    @FXML
    private MenuButton languageButton;
    @FXML
    private HBox forgotBox;
    @FXML
    private Hyperlink forgotLink; //кнопка, которая будет выглядеть как ссылка

    private Client client;

    //тут получаем единственный экземпляр менеджера языков
    private final LanguageManager languageManager = LanguageManager.getInstance();

    /**
     * Метод для установки UDP-клиента
     * @param client объект UDP-client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Метод для замены текста в разных вкладках
     */
    private void applyLocaleToTabs() {
        boolean isLogin = tabPane.getSelectionModel().getSelectedItem() == loginTab;

        if (isLogin) {
            submitButton.setText(languageManager.get("button.login"));
            forgotBox.setVisible(true);
            forgotBox.setManaged(true);
        } else {
            submitButton.setText(languageManager.get("button.register"));
            forgotBox.setVisible(false);
            forgotBox.setManaged(false);
        }
    }

    /**
     * Метод, позволяющий выставить все переводы к элементам окна "Вход"
     */
    private void applyBundle() {
        loginTab.setText(languageManager.get("tab.login"));
        registerTab.setText(languageManager.get("tab.register"));
        loginLabel.setText(languageManager.get("label.login"));
        passwordLabel.setText(languageManager.get("label.password"));

        loginField.setPromptText(languageManager.get("prompt.login")); //тут устанавливаем именно PromptText, чтобы была типа подсказка
        passwordField.setPromptText(languageManager.get("prompt.password")); //тут тоже

        errorLabel.setText(""); //при смене языка, не должна висеть ошибка на прошлом языке

        forgotLink.setText(languageManager.get("forgot.password"));
        applyLocaleToTabs(); //применяем к кнопке
    }

    /**
     * Метод, позволяющий добавить новый язык (в меню плашки выставляются)
     * @param label метка для языка
     * @param locale объект класса Locale для конкретного региона
     */
    private void addLanguageItem(String label, Locale locale) {
        MenuItem item = new MenuItem(label);

        item.setOnAction(e -> {
            languageButton.setText(label); //тут меняем текст в меню языка
            languageManager.setLocale(locale); //тут меняем язык в менеджере
            applyBundle(); //обновляем вообще все тексты
        });

        languageButton.getItems().add(item);
    }

    @FXML
    public void initialize() {
        //применяем язык по умолчанию
        applyBundle();

        submitButton.setDefaultButton(true);

        //добавляем языки
        addLanguageItem("RU", new Locale("ru"));
        addLanguageItem("EN(GB)", new Locale("en", "GB"));
        addLanguageItem("SR", new Locale("sr"));
        addLanguageItem("BG", new Locale("bg"));

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldTab, newTab) -> {
            loginField.clear(); //очищаем при смене вкладки
            passwordField.clear(); //тоже очищаем
            errorLabel.setText(""); //при смене вкладки ошибка исчезает типа

            applyLocaleToTabs();//применяем локализацию к вкладкам

        });
    }

    @FXML
    private void handleSubmit() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showError(languageManager.get("error.empty"));
            return;
        }

        boolean isLoginTab = tabPane.getSelectionModel().getSelectedItem() == loginTab;

        String command = isLoginTab ? "login" : "register";

        submitButton.setDisable(true); //тут заблокировал кнопку, когда уже нажал
        errorLabel.setText("");

        //для сетевой работы отдельный поток, потому что иначе пользователь заметит, что приложение зависло (мышкой нельзя будет подвигать)
        Thread thread = new Thread(() -> {
            Request request = new Request(login, password, command, null, null);
            Response response = client.sendAndReceive(request);

            Platform.runLater(() -> {
                submitButton.setDisable(false);
                if (response != null && response.isSuccess()) {
                    Long ownerId = extractOwnerId(response);
                    openMainWindow(login, password, ownerId); //открываем приложение
                } else {
                    showError(response != null ? response.getMessage() : languageManager.get("error.server"));
                }
            });

        });

        thread.setDaemon(true); //поток завершится, когда завершится приложение
        thread.start();

    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(languageManager.get("alert.forgot.title"));
        alert.setHeaderText(null);
        alert.setContentText(languageManager.get("forgot.text"));
        alert.showAndWait();
    }

    /**
     * Метод, позволяющий показать причину ошибки при авторизации или регистрации
     * @param message ошибка
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }

    /**
     * Достаёт ownerId пользователя из ответа сервера (первый элемент коллекции).
     * @param r ответ на login/register
     * @return идентификатор владельца или {@code null}, если его нет
     */
    private Long extractOwnerId(Response r) {
        if (r == null || r.getCollection() == null) return null;
        for (Object o : r.getCollection()) {
            if (o instanceof Long id) return id;
            if (o instanceof Number n) return n.longValue();
        }
        return null;
    }

    /**
     * Метод, предназначенный для загрузки главного окна
     * @param login логин пользователя
     * @param password пароль пользователя
     * @param ownerId идентификатор владельца (для точного определения «своих» объектов)
     */
    private void openMainWindow(String login, String password, Long ownerId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.initData(client, login, password, ownerId); //передаём данные контроллеру

            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setTitle("Коллекция объектов");
            stage.setScene(new Scene(root, 1100, 650));
            stage.setResizable(true); //можно растягивать туда-сюда

        } catch (Exception ex) {
            showError("Ошибка при открытии главного окна, увы.");
        }

        System.out.println("Успешный вход: " + login);
    }
}
