package PokemonBattle.pokemons;
import ru.ifmo.se.pokemon.*;
import PokemonBattle.moves.*;

public class Ponyta extends Pokemon{

    public Ponyta(String name, int level) {
        super(name, level);
        setStats(50, 85, 55, 65, 65, 90);
        setType(Type.FIRE);
        setMove(new Will_O_Wisp(), new Swagger(), new Growl());
    }

}
