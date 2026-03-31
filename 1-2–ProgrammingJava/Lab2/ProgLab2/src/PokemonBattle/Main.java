package PokemonBattle;
import PokemonBattle.pokemons.*;
import PokemonBattle.moves.*;
import ru.ifmo.se.pokemon.*;

public final class Main {
    public static void main(String[] args) {
        System.out.println("<<ВЫ НАБЛЮДАЕТЕ НЕВЕРОЯТНОЕ СОБЫТИЕ, КОТОРОЕ МОЖНО УВИДЕТЬ ТОЛЬКО РАЗ В 1000 ЛЕТ>>\n");
        Battle battle1 = new Battle();

        Diancie poke1 = new Diancie("Блестяшка", 4);
        Ponyta poke2 = new Ponyta("Поня", 1);
        Rapidash poke3 = new Rapidash("ПоняСигма", 2);
        Pichu poke4 = new Pichu("ПикачКитай", 1);
        Pikachu poke5 = new Pikachu("ПикачОригинал", 5);
        Raichu poke6 = new Raichu("ПикачИмба", 3);

        battle1.addAlly(poke5);
        battle1.addAlly(poke2);
        battle1.addAlly(poke6);
        battle1.addFoe(poke4);
        battle1.addFoe(poke1);
        battle1.addFoe(poke3);

        battle1.go();
    }
}