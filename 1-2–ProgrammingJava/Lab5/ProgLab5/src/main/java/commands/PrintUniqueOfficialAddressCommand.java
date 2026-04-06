package commands;

import model.Address;
import model.Organization;
import service.CollectionManager;
import transport.Request;

import java.util.HashSet;
import java.util.Set;

public class PrintUniqueOfficialAddressCommand extends AbstractCommand {
    public PrintUniqueOfficialAddressCommand(CollectionManager cm) {
        super("print_unique_official_address", "вывести уникальные значения поля officialAddress всех элементов в коллекции", cm);
    }

    //NONE и false

    @Override
    public void execute(Request request) {
        //создадим HashSet, который по своей структуре обеспечит уникальность адресов
        Set<Address> addresses = new HashSet<Address>();

        for (Organization org : collectionManager.getCollection()) {
            if (org.getOfficialAddress() != null) {
                addresses.add(org.getOfficialAddress());
            }
        }

        if (addresses.isEmpty()) {
            System.out.println("К сожалению, список официальных адресов пуст(");
        }
        else {
            System.out.println("Уникальные официальные адреса организаций:");
            for (Address ad : addresses) {
                System.out.println(ad);
            }
        }
    }
}
