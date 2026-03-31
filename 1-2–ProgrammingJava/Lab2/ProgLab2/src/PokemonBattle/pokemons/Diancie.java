package PokemonBattle.pokemons;
import ru.ifmo.se.pokemon.*;
import PokemonBattle.moves.*;

public final class Diancie extends Pokemon {

    public Diancie(String name, int level) {
        super(name, level);
        setStats(50, 100, 150, 100, 150, 50);
        setType(Type.ROCK, Type.FAIRY);
        setMove(new StoneEdge(), new Facade(), new Sharpen(), new Rest());
    }

}
