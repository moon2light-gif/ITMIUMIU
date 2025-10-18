package PokemonBattle.pokemons;
import ru.ifmo.se.pokemon.*;
import PokemonBattle.moves.*;

public final class Rapidash extends Ponyta {

    public Rapidash(String name, int level) {
        super(name, level);
        setStats(65, 100, 70, 80, 80, 105);
        setMove(new QuickAttack());
    }

}
