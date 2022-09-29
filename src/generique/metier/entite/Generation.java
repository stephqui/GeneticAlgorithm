package generique.metier.entite;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;

//@Log

public class Generation <T extends Individu<?>> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final LogManager logManager = LogManager.getLogManager();
	private static Logger log = Logger.getGlobal();
	
	//chargement de la config des logs
	static {
		try {
			logManager.readConfiguration(new FileInputStream("src/geneticLogConfig.properties"));
		}catch (IOException exception) {
			log.log(Level.SEVERE, "Cannot read configuration file", exception);
		}
	}
	
	@Getter
	private List<T> lesIndividus = new ArrayList<T>();
	
	@Getter
	private Integer numero;
	
	public Generation(int numero) {
		this.numero = numero;
	}
	
	public void ajouterIndividu(@NonNull T nouvelIndividu) {
		this.lesIndividus.add(nouvelIndividu);
	}
	
	public void ajouterGroupeIndividu(@NonNull List<T> groupeIndividus) {
		if(! groupeIndividus.contains(null))
		this.lesIndividus.addAll(groupeIndividus);
	}
	
	public void enleverIndividu(@NonNull T individu) {
		this.lesIndividus.remove(individu);
	}
	
	public Integer nbIndividus() {
		return this.lesIndividus.size();
	}
	
	public void eliminerNonSelectionnes() {
		List<T> lesIndividusLocaux = new ArrayList<>();
		
		lesIndividusLocaux.addAll(lesIndividus);
		
		for(T individu : lesIndividusLocaux) {
			if(!individu.isSelectionne()) {
				this.lesIndividus.remove(individu);
			}
		}
	}
	
	public Integer getSommeScore() {
		int somme = 0;
		for(T individu : lesIndividus) {
			somme += individu.getScore();
		}
		return somme;
	}
	
	public Integer getScoreDeSelection() {
		int scoreMax = 0;
		for(T individu : lesIndividus) {
			if(individu.getScore() > scoreMax) scoreMax = individu.getScore();
		}
		
		int[] repartitionScores = new int[scoreMax + 1];
		
		for(T individu : lesIndividus) {
			repartitionScores[individu.getScore()] += 1;
		}
		
		int nbIndividus = 0;
		int sommePonderee = 0;
		
		if(log.isLoggable(Level.FINE)) log.log(Level.FINE, "Repartition");
		
		for(int i = 1; i< repartitionScores.length; i++) {
			if(log.isLoggable(Level.FINE)) log.log(Level.FINE,String.valueOf(repartitionScores[i]));
			
			nbIndividus += repartitionScores[i];
			sommePonderee += repartitionScores[i] * i + 1;
		}
		return (int) sommePonderee / nbIndividus;
	}
	
	public boolean contientSolution() {
		for(T individu : lesIndividus) {
			if(individu.isSolution())
				return true;
		}
		return false;
	}
	
	public T getMeilleurIndividu() {
		T meilleureSolution = null;
		for(T individu : lesIndividus) {
			if(meilleureSolution == null || individu.getScore() > meilleureSolution.getScore())
				meilleureSolution = individu;
		}
		return meilleureSolution;
	}
}
