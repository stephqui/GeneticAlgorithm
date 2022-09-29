package generique.metier.entite;

import java.io.Serializable;

public interface Individu <T> extends Serializable {
	
	T getValeur();
	
	void setValeur(T valeur);
	
	int getScore();
	
	void setScore(int score);
	
	boolean isSolution();
	
	boolean isSelectionne();
	
	boolean isEnfant();

}
 