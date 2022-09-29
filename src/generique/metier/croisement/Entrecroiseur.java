package generique.metier.croisement;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import generique.metier.entite.Generation;
import generique.metier.entite.Individu;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public class Entrecroiseur<T extends Individu<?>> {
	
	@Setter
	@NonNull
	private BiFunction<T, T, List<T>> fabriqueDescendant;
	
	public Generation<T> getGeneration(Generation<T> generationParent){
		Generation<T> nouvelleGeneration = new Generation<T>(generationParent.getNumero()+1);
		
		generationParent.getLesIndividus().sort(new Comparator<T>(){
			
			@Override
			public int compare(T motA, T motB) {
				return motB.getScore() - motA.getScore();
			}
		});
		int index = 0;
		
		while(nouvelleGeneration.nbIndividus() < 50 && index < generationParent.nbIndividus()-1) {
			nouvelleGeneration.ajouterGroupeIndividu(fabriqueDescendant.apply(generationParent.getLesIndividus().get(index), 
																			  generationParent.getLesIndividus().get(index+1)
																				)
														);
			index+=2;
		}
		return nouvelleGeneration;
	}
	
}
