package generique.metier.evaluation;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


public class Evaluateur<Individu, V> {
	
	@Setter
	@NonNull
	private Supplier<V> fabriqueMotMystere;
	
	@Getter
	private V motMystere;
	
	@Setter
	@NonNull
	private BiConsumer<Individu, V> evaluateurIndividu;
	
	public Evaluateur(Supplier<V> fabriqueMotMystere, BiConsumer<Individu, V> evaluateurIndividu){
		this.motMystere = fabriqueMotMystere.get();
		setFabriqueMotMystere(fabriqueMotMystere);
		setEvaluateurIndividu(evaluateurIndividu);
	}

	public void evaluer(Individu individu) {
		this.evaluateurIndividu.accept(individu, motMystere);
	}
	
}
