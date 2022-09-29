package alphabetic.metier.entite;

import generique.metier.entite.Individu;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Mot implements Individu<String> {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@NonNull
	private String valeur;
	
	@Getter
	@Setter
	private int score;
	
	@Getter
	@Setter
	private boolean selectionne;
	
	@Getter
	@Setter
	private boolean enfant;
	
	@Getter
	@Setter
	private boolean solution;
	
	public Mot(String valeur) {
		setValeur(valeur);
		score = -1;
	}
	
}
