package PokemonBattle.pokemons;
import ru.ifmo.se.pokemon.*;
import PokemonBattle.moves.*;

public class Pikachu extends Pichu {

    public Pikachu(String name, int level) {
        super(name, level);
        setStats(35, 55, 40, 50, 50, 90);
        setMove(new Nuzzle());
    }
}
