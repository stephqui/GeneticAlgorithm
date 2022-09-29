package generique.metier.entite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import lombok.Getter;

public class Population<T extends Generation<?>> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	private List<T> lesGenerations = new ArrayList<>();
	
	private NavigableMap<Integer, Integer> recensement = new TreeMap<Integer, Integer>();
	
	public void ajouterGeneration(T generation) {
		this.lesGenerations.add(generation);
	}
	
	public int nbGenerations() {
		return this.lesGenerations.size();
	}
	
	public void recenser(T generation) {
		this.recensement.put(generation.getNumero(), generation.getSommeScore());
	}
	
	public boolean isPopulationDecroissante() {
		int nbDeGenerationDecroissante = 0;
		if(recensement.size() > 10)
			for(int i =0; i < 5; i++)
				if(recensement.descendingMap().get(1) < recensement.descendingMap().get(i + 1))
					nbDeGenerationDecroissante++;
		return (nbDeGenerationDecroissante == 5);
	}
}
