package server.commands;

import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import server.service.CollectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс {@code PrintFieldAscendingAnnualTurnoverCommand} описывает команду "print_field_ascending_annual_turnover",
 * которая отвечает за вывод значения поля annualTurnover всех элементов в порядке возрастания
 *
 * @author mikhailkramar
 */
public class PrintFieldAscendingAnnualTurnoverCommand extends AbstractCommand {

    public PrintFieldAscendingAnnualTurnoverCommand(CollectionManager cm) {
        super("print_field_ascending_annual_turnover", "вывести значения поля annualTurnover всех элементов в порядке возрастания", cm);
    }

    @Override
    public Response execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            return new Response(true, collectionManager.getCollection(), "Коллекция пуста!");
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

        return new Response(true, turnovers, "Годовые обороты организаций по возрастанию:");
    }
}
