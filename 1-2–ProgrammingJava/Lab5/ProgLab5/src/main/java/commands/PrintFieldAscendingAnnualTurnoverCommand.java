package commands;

import model.Organization;
import service.CollectionManager;
import transport.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrintFieldAscendingAnnualTurnoverCommand extends AbstractCommand {
    public PrintFieldAscendingAnnualTurnoverCommand(CollectionManager cm) {
        super("print_field_ascending_annual_turnover", "вывести значения поля annualTurnover всех элементов в порядке возрастания", cm);
    }

    //NONE и false

    @Override
    public void execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            System.out.println("Коллекция пуста!");
            return;
        }
        //создадим список для годовых оборотов
        List<Integer> turnovers = new ArrayList<Integer>();

        for (Organization org : collectionManager.getCollection()) {
            if (org.getAnnualTurnover() != null) {
                turnovers.add(org.getAnnualTurnover());
            }
        }
        //сортируем по умолчанию
        Collections.sort(turnovers);

        System.out.println("Годовые обороты организаций по возрастанию:");
        for (Integer elem : turnovers) {
            System.out.println(elem);
        }
    }
}
