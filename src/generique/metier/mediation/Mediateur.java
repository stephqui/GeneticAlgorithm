package generique.metier.mediation;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import alphabetic.lanceur.App;
import generique.metier.croisement.Entrecroiseur;
import generique.metier.entite.Generation;
import generique.metier.entite.Individu;
import generique.metier.entite.Population;
import generique.metier.evaluation.Evaluateur;
import generique.metier.initialisation.Generateur;
import generique.metier.selection.Selecteur;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class Mediateur<T extends Individu<?>, R> implements Runnable {
	
	private static final LogManager logManager = LogManager.getLogManager();

	static {
		try {
			logManager.readConfiguration(new FileInputStream("geneticLogConfig.properties"));
		}catch (IOException exception) {
			log.log(Level.SEVERE, "Cannot read configuration file", exception);
		}
	}
	
	//Internes
	private int longueurDeMot;
	private int nbIndividuParGeneration;
	private long dureeTraitement;
	
	private Generateur<T> gs;
	private Generation<T> g;
	private Selecteur<T> selecteurIndividu;
	private Entrecroiseur<T> entrecroiseurIndividus;
	private Population<Generation<T>> population = new Population<Generation<T>>();
	private LocalDateTime startAt;
	
	//parametres
	@NonNull
	@Setter
	private Supplier<R> fonctionGeneratriceMotMystereAleatoire;
	
	@NonNull
	@Setter
	private Supplier<T> fonctionGeneratriceIndividuAleatoire;
	
	@NonNull
	@Setter
	private BiConsumer<T, R> fonctionEvaluatriceIndividu;
	
	@NonNull
	@Setter
	private Evaluateur<T, R> evaluateurIndividu;
	
	@NonNull
	@Setter
	private BiConsumer<T, Integer> fonctionSelectriceIndividu;
	
	@NonNull
	@Setter
	private BiFunction<T, T, List<T>> fonctionFabriqueDeDescendants;

	
	private void init() {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream("src/geneticConfig.properties"));
			this.longueurDeMot = Integer.valueOf(properties.getProperty("longueurDeMot"));
			this.nbIndividuParGeneration = Integer.valueOf(properties.getProperty("nbIndividusParGeneration"));
			this.dureeTraitement = Integer.valueOf(properties.getProperty("dureeExecution"));

		}catch (IOException e) {
			if (log.isLoggable(Level.SEVERE)) {
				log.log(Level.SEVERE, "Erreur à l'éxécution", e.getMessage());
				log.log(Level.SEVERE, "Cause", e.getMessage());
			}
			throw new RuntimeException(e);
		}
		
		this.selecteurIndividu = new Selecteur<T>(fonctionSelectriceIndividu, 0);
		this.entrecroiseurIndividus = new Entrecroiseur<T>(fonctionFabriqueDeDescendants);
		this.gs = new Generateur<>(fonctionGeneratriceIndividuAleatoire);
		this.startAt = LocalDateTime.now();
	}

	public Mediateur(Supplier<R> fonctionGeneratriceMotMystereAleatoire,
			 Supplier<T> fonctionGeneratriceIndividuAleatoire,
			 BiConsumer<T, R> fonctionEvaluatriceIndividu,
			 Evaluateur<T, R> evaluateurIndividu,
			 BiConsumer<T, Integer> fonctionSelectriceIndividu,
			 BiFunction<T, T, List<T>> fonctionFabriqueDeDescendants) {
		setFonctionGeneratriceMotMystereAleatoire(fonctionGeneratriceMotMystereAleatoire);
		setFonctionGeneratriceIndividuAleatoire(fonctionGeneratriceIndividuAleatoire);
		setFonctionEvaluatriceIndividu(fonctionEvaluatriceIndividu);
		setEvaluateurIndividu(evaluateurIndividu);
		setFonctionSelectriceIndividu(fonctionSelectriceIndividu);
		setFonctionFabriqueDeDescendants(fonctionFabriqueDeDescendants);
	}
	

	@Override
	public void run() {

		init();
		g = gs.getGeneration(nbIndividuParGeneration);

		for (T individu : g.getLesIndividus()) {
			evaluateurIndividu.evaluer(individu);
		}
		
		if (log.isLoggable(Level.FINE)) {
			log.log(Level.FINE, "Génération initiale évaluée\n");
			g.getLesIndividus().forEach(System.out::println);
		}
		
		while (Duration.between(startAt, LocalDateTime.now()).toMinutes() < dureeTraitement && !g.contientSolution() && !population.isPopulationDecroissante()) {
			if (log.isLoggable(Level.INFO)) {
				log.log(Level.INFO, "Génération {0} en cours de traitement", g.getNumero());
				System.out.println("Génération " + g.getNumero() + " en cours de traitement");

				if (log.isLoggable(Level.FINE)) {
					log.log(Level.FINE, "Durée de traitement : {0} minutes", Duration.between(startAt, LocalDateTime.now()).toMinutes());
					log.log(Level.FINE, "Mot mystere : {0}", evaluateurIndividu.getMotMystere());
					log.log(Level.FINE, "Score cumulé : {0}", g.getSommeScore());
					log.log(Level.FINE, "Score de selection : {0}", g.getScoreDeSelection());
				}
				log.log(Level.INFO, "Meilleure solution {0}", g.getMeilleurIndividu());
				System.out.println("Meilleure solution " + g.getMeilleurIndividu());
			}
			
			selecteurIndividu.setSeuil(g.getScoreDeSelection());
			for (T individu : g.getLesIndividus()) {
				selecteurIndividu.selectionner(individu);
			}
			
			g.eliminerNonSelectionnes();
			g = entrecroiseurIndividus.getGeneration(g);
			if (g.nbIndividus() < nbIndividuParGeneration) {
				g.ajouterGroupeIndividu(gs.getGeneration(50-g.nbIndividus()).getLesIndividus());
			}
			
			for (T individu : g.getLesIndividus()) {
				evaluateurIndividu.evaluer(individu);
			}
			population.recenser(g);	
		}

		
		if (log.isLoggable(Level.INFO)) {
			log.log(Level.INFO, "END !");
			System.out.println("END !");
			log.log(Level.INFO, "Le mot mystere est ? : {0}", evaluateurIndividu.getMotMystere());
			System.out.println("Le mot mystere est : " + evaluateurIndividu.getMotMystere());
			log.log(Level.INFO, "La meilleure solution trouvée est : {0}", g.getMeilleurIndividu());
			System.out.println("La meilleure solution trouvée est : " + g.getMeilleurIndividu());
			log.log(Level.INFO, "Population décroissante ? : {0}", population.isPopulationDecroissante());
			System.out.println("Population decroissante ? " + population.isPopulationDecroissante());
			log.log(Level.INFO, "temps écoulé : {0} minutes", Duration.between(startAt, LocalDateTime.now()).toSeconds());
			System.out.println("Temps écoulé ? " + Duration.between(startAt, LocalDateTime.now()).toMillis() + " ms");
		}
	}
	
}
