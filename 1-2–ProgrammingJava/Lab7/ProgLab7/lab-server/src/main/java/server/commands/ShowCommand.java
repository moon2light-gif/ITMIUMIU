    package server.commands;

    import common.transport.Request;
    import common.transport.Response;
    import server.service.CollectionManager;

    /**
     * Класс команды "show", позволяющей посмотреть все элементы коллекции
     *
     * @author mikhailkramar
     */
    public class ShowCommand extends AbstractCommand {
        public ShowCommand(CollectionManager cm) {
            super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", cm);
        }

        @Override
        public Response execute(Request request) {
            if (collectionManager.getCollection().isEmpty()) {
                return new Response(true, null, "Коллекция пуста, выводить нечего!");
            }
            return new Response(true, collectionManager.getCollectionCopy(), "Элементы коллекции:");
        }
    }
