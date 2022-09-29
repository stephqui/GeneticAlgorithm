package alphabetic.lanceur;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;

import alphabetic.metier.entite.Mot;
import generique.metier.evaluation.Evaluateur;
import generique.metier.mediation.Mediateur;
import lombok.extern.java.Log;


@Log
public class App {
	
	private static final LogManager logManager = LogManager.getLogManager();
	
	static {
		try {
			logManager.readConfiguration(new FileInputStream("src/geneticLogConfig.properties"));
		}catch (IOException exception) {
			log.log(Level.SEVERE, "Impossible de charger le fichier de configuration", exception);
		}
	}
	
	public static void main(String[] args) {
		
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream("src/geneticConfig.properties"));		
			
			Properties dictionnaire = new Properties();
			dictionnaire.load(new FileInputStream("src/dictionnaire.properties"));
			
			int longueurDeMot = Integer.valueOf(properties.getProperty("longueurDeMot"));
			
			Supplier<String> fonctionGeneratriceMotMystereAleatoire;	
			
			if (longueurDeMot > 0 && longueurDeMot < 26) {
				fonctionGeneratriceMotMystereAleatoire = () -> dictionnaire.getProperty(String.valueOf(longueurDeMot));
			}else
				fonctionGeneratriceMotMystereAleatoire = () -> RandomStringUtils.randomAlphabetic(longueurDeMot).toLowerCase();
			
			Supplier<Mot> fonctionGeneratriceIndividuAleatoire = () -> new Mot(RandomStringUtils.randomAlphabetic(longueurDeMot).toLowerCase());
			
			BiConsumer<Mot, String> fonctionEvaluatriceIndividu = (x,y) -> {
				char[] elementMotMystere = y.toCharArray();
				char[] elementIndividu = x.getValeur().toCharArray();
				int scoreIndividu = 0;
				
				for (int i = 0; i < elementIndividu.length; i++) {
					if (elementIndividu[i] == elementMotMystere[i]) scoreIndividu++;
				}
				x.setScore(scoreIndividu);
				x.setSolution(x.getScore() == elementMotMystere.length);
			};
			
			Evaluateur<Mot, String> evaluateurIndividu = new Evaluateur<Mot, String>(fonctionGeneratriceMotMystereAleatoire, fonctionEvaluatriceIndividu);
			
			BiConsumer<Mot, Integer> fonctionSelectriceIndividu = (x, y) -> {
				x.setSelectionne(x.getScore() >= y);
			};

			BiFunction<Mot, Mot, List<Mot>> fonctionFabriqueDescendants = (x, y) -> {
				List<Mot> leGroupeEnfants = new ArrayList<Mot>();
				
				char[] elementParentUn = x.getValeur().toCharArray();
				char[] elementParentDeux = y.getValeur().toCharArray();
				
				
				char[] elementEnfantUn = new char[elementParentUn.length];
				char[] elementEnfantDeux = new char[elementParentDeux.length];
				
				for (int i = 0; i < elementParentUn.length; i++) {
					int choix = (int) ((Math.random() * 10) %2);
					if (choix == 1) {
						elementEnfantUn[i] = elementParentUn[i];
						elementEnfantDeux[i] = elementParentDeux[i];
					}else {
						elementEnfantUn[i] = elementParentDeux[i];
						elementEnfantDeux[i] = elementParentUn[i];
						
					}
				}
				
				Mot enfantUn = new Mot(new String(elementEnfantUn));
				Mot enfantDeux = new Mot(new String(elementEnfantDeux));
				
				enfantUn.setEnfant(true);
				enfantDeux.setEnfant(true);
				
				leGroupeEnfants.add(enfantUn);
				leGroupeEnfants.add(enfantDeux);
				
				return leGroupeEnfants;
				
			};
			
			Mediateur<Mot, String> mediateur = new Mediateur<>(fonctionGeneratriceMotMystereAleatoire,
					fonctionGeneratriceIndividuAleatoire,
					fonctionEvaluatriceIndividu,
					evaluateurIndividu,
					fonctionSelectriceIndividu,
					fonctionFabriqueDescendants
					);
			
			mediateur.run();
				
			//*************************************************
	}catch(IOException e) {
		log.log(Level.SEVERE, "Erreur à l'éxécution", e.getMessage());
		log.log(Level.SEVERE, "Cause", e.getCause());
		
		}
	}

}
