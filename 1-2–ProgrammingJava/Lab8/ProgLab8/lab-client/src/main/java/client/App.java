package client;

import client.connection.Client;
import client.connection.RetryClientDecorator;
import client.connection.UDPClient;

import client.ui.LoginController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

/**
 * Класс {@code App} объединяет все нужные нам классы в одно приложение
 * <p>
 * author mikhailkramar
 */
public class App extends Application {

    private Client wrappedClient;

    @Override
    public void init() {
        //Выставляем UDP-клиента
        Client udpClient = new UDPClient("localhost", 19614);
        this.wrappedClient = new RetryClientDecorator(udpClient);
    }
    
    @Override
    public void start(Stage mainStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        //установка контроллера первой сцены
        LoginController loginController = loader.getController();
        loginController.setClient(wrappedClient);

        //установка параметров для первой сцены
        mainStage.setTitle("Вход");
        mainStage.setScene(new Scene(root, 500, 520));
        mainStage.setResizable(false);
        mainStage.show();

    }

    @Override
    public void stop() {
        System.out.println("Приложение закрыто!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
