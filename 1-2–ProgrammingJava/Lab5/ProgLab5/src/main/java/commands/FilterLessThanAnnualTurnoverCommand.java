package commands;

import model.Organization;
import service.CollectionManager;
import transport.Request;
import utility.ArgumentType;

import java.util.ArrayList;
import java.util.List;

public class FilterLessThanAnnualTurnoverCommand extends AbstractCommand {
    public FilterLessThanAnnualTurnoverCommand(CollectionManager cm) {
        super("filter_less_than_annual_turnover", "вывести элементы, значение поля annualTurnover которых меньше заданного", cm);
    }

    @Override
    public ArgumentType getArgumentType() {
        return ArgumentType.INT;
    }

    //requiresObjectArgument - false

    @Override
    public void execute(Request request) {
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
            System.out.println("Организаций с оборотом меньше " + turnoverFilter + " не найдено!");
        }
        else {
            System.out.println("Организации с оборотом меньше " + turnoverFilter + ":");
            for (Organization org : filteredOrgs) {
                System.out.println(org);
            }
        }

    }

}
