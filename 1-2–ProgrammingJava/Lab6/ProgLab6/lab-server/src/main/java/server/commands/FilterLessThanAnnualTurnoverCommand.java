package server.commands;

import common.model.Organization;
import common.transport.Response;
import server.service.CollectionManager;
import common.transport.Request;
import common.utility.ArgumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Класс {@code FilterLessThanAnnualTurnover} описывает команду "filter_less_than_annual_turnover",
 * отвечающая за вывод элементов коллекции, значение поля annualTurnover которых меньше заданного
 *
 * @author mikhailkramar
 */
public class FilterLessThanAnnualTurnoverCommand extends AbstractCommand {

    public FilterLessThanAnnualTurnoverCommand(CollectionManager cm) {
        super("filter_less_than_annual_turnover", "вывести элементы, значение поля annualTurnover которых меньше заданного", cm);
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.INT;
    }

    @Override
    public Response execute(Request request) {
        //заданное значение annualTurnover
        Integer turnoverFilter = Integer.parseInt(request.getArgument());
        //собираем коллекцию отсортированных нами организаций
        List<Organization> filteredOrgs = new ArrayList<Organization>();

        for (Organization org : collectionManager.getCollection()) {
            if (org.getAnnualTurnover() < turnoverFilter) {
                filteredOrgs.add(org);
            }
        }

        if (filteredOrgs.isEmpty()) {
            return new Response(true, null,"Организаций с оборотом меньше " + turnoverFilter + " не найдено!");
        }
        else {
            return new Response(true, filteredOrgs, "Организации с оборотом меньше:");
        }
    }
}
