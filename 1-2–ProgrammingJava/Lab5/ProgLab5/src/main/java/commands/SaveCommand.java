package commands;

import service.CollectionManager;
import transport.Request;

public class SaveCommand extends AbstractCommand {
    public SaveCommand(CollectionManager cm) {
        super("save", "сохранить коллекцию в файл", cm);
    }

    @Override
    public void execute(Request request) {
        //тут вызываем метод сохранения, который пропишем в collectionManager
        collectionManager.save();
    }
}
