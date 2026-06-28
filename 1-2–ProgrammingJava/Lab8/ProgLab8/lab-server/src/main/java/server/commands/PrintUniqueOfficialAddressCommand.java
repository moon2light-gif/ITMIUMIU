package server.commands;

import common.model.Address;
import common.model.Organization;
import common.transport.Request;
import common.transport.Response;
import server.service.CollectionManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс {@code PrintUniqueOfficialAddressCommand} описывает команду "print_unique_official_address",
 * которая отвечает за вывод уникальных значений поля officialAddress всех элементов в коллекции
 *
 * @author mikhailkramar
 */
public class    PrintUniqueOfficialAddressCommand extends AbstractCommand {

    public PrintUniqueOfficialAddressCommand(CollectionManager cm) {
        super("print_unique_official_address", "вывести уникальные значения поля officialAddress всех элементов в коллекции", cm);
    }

    @Override
    public Response execute(Request request) {

        //создадим HashSet, который по своей структуре обеспечит уникальность адресов
        Set<Address> addresses = new HashSet<Address>();

        for (Organization org : collectionManager.getCollection()) {
            if (org.getOfficialAddress() != null) {
                addresses.add(org.getOfficialAddress());
            }
        }

        if (addresses.isEmpty()) {
            return new Response(true, null, "К сожалению, список официальных адресов пуст(");
        }
        else {
            return new Response(true, addresses, "Вот уникальные адреса организаций:");
        }
    }
}
