package generique.metier.initialisation;

import java.util.function.Supplier;

import generique.metier.entite.Generation;
import generique.metier.entite.Individu;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public class Generateur<T extends Individu<?>> {
	
	@Setter
	@NonNull
	private Supplier<T> fabriqueIndividu;
	
	public Generation<T> getGeneration(int nbIndividu){
		Generation<T> generationInitiale = new Generation<T>(0);
		for(int i =0; i < nbIndividu; i++) {
			generationInitiale.ajouterIndividu(fabriqueIndividu.get());
		}
		return generationInitiale;
	}

}
