import characters.Carlson;
import environment.*;
import mainObjects.*;

class Main {
    public static void main(String[] args) {
        System.out.println("НЕВЕРОЯТНЫЕ ПРИКЛЮЧЕНИЯ КАРЛСОНА");

        // создаем верстачок
        Workbench wrkbnch = new Workbench();
        // создаём предметы и кладём на него
        wrkbnch.addItem(new Item("Наждачка", new Position(12.0, 2.2, 95.0)));
        wrkbnch.addItem(new Item("Молоток", new Position(4.0, 5.6, 95.0)));
        wrkbnch.addItem(new Item("Пила", new Position(17.8, 21.0, 95.0)));

        // комната действия
        Room room = new Room(wrkbnch);

        // камин
        Fireplace fireplace = new Fireplace(new Position(45.0, 10.0, 55.6));
        // лампа, висящая над столом засчёт позиции z > позиции z камина
        Lamp lamp = new Lamp(Math.random() > 0.3 ? 12.0 : 0.0, new Position(35.76, 12.321, 165.0));
        //добавляем источники освещения для нашей комнатки
        room.addLight(fireplace);
        room.addLight(lamp);

        // персонаж - Карлсон
        Carlson carlson = new Carlson("Карслон", "оранжевый", 39, new Position(0.0, 0.0, 76.9));

        // получаем начальный уровень освещённости
        room.updateLight();

        // непроверяемое исключение могу показать
        //wrkbnch.findItem("Бензопила");

        // карлсон встал и подошёл к камину
        carlson.Move(room);

        // карлсон добавляет в камин два бревна
        carlson.addTwoLogs(fireplace);

        // зажигаем камин
        fireplace.turnOn();
        room.updateLight();
        room.illuminateItems();


        // сжигаем поленья, два полена - два шага
        fireplace.burnStep();
        fireplace.burnStep();

        // камин потух, новая освещённость
        room.updateLight();
        room.hideItems();

        // карлсон зажигает керосиновую лампу после прогорания камина
        carlson.lightLamp(lamp);

        // что происходит после попытки зажечь лампу
        room.updateLight();

        if (lamp.isOn) {
            room.illuminateItems();
        }
        else {
            room.hideItems();
        }

        System.out.println("НУ ВОТ И ЗАКОНЧИЛИСЬ ПРИКЛЮЧЕНИЯ НАШЕГО ГЕРОЯ");
    }
}