package common.utility;

import java.io.*;

/** Класс {@code Serializer} служит для сериализации и десериализации объектов,
 * отправляемых между сервером и клиентами по сети
 *
 * @author mikhailkramar
 */
public class Serializer {
    /**
     *
     * @param object объект, передаваемый с клиента на сервер, который мы сериализуем в массив байтов
     * @return
     * @throws IOException исключение, которое возникает при передаче, пакет мог побиться
     */
    public static byte[] serialize(Object object) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     *
     * @param data массив байтов, которые будут восстановлены в виде объекта на стороне сервера
     * @return объект Object
     * @throws IOException
     * @throws ClassNotFoundException исключение, возникающее, если у сервера нет такого класса
     */
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayOutputStream)) {
            return objectInputStream.readObject(); //вернули Object, поэтому потом приведу тип
        } catch (ClassNotFoundException ex) {
            throw new IOException("Ошибка десериализации: класс не найден на сервере или клиенте", ex);
        }
    }
}
