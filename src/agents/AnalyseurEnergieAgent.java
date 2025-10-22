package agents;

import behaviours.AnalyseEnergieBehaviour;
import jade.core.Agent;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class AnalyseurEnergieAgent extends Agent {
    private Random random;
    private List<Double> historique;
    private final int TAILLE_HISTORIQUE = 5;
    private final double SEUIL_ECONOMIE = 150.0;
    private int nbAnalyses = 0;
    private final int MAX_ANALYSES = 15;
    private double consommationTotale = 0;

    @Override
    protected void setup() {
        random = new Random();
        historique = new ArrayList<>();
        System.out.println("\n[" + getLocalName() + "] 🚀 Démarrage de l'analyseur d'énergie\n");

        // Un seul comportement qui contient tous les autres
        addBehaviour(new AnalyseEnergieBehaviour(this));
    }

    @Override
    protected void takeDown() {
        double moyenne = nbAnalyses > 0 ? consommationTotale / nbAnalyses : 0;
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     RAPPORT FINAL - ANALYSEUR ÉNERGIE    ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf("║ Nombre d'analyses: %-21d║%n", nbAnalyses);
        System.out.printf("║ Consommation moyenne: %.1f kW          ║%n", moyenne);
        System.out.printf("║ Consommation totale: %.1f kW         ║%n", consommationTotale);

        if (!historique.isEmpty()) {
            double derniere = historique.get(historique.size() - 1);
            System.out.printf("║ Dernière mesure: %.1f kW               ║%n", derniere);
        }

        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("\n[" + getLocalName() + "] 👋 Agent terminé avec succès\n");
    }

    // Méthodes pour modifier les attributs
    public void incrementNbAnalyses() {
        this.nbAnalyses++;
    }

    public void addConsommationTotale(double consommation) {
        this.consommationTotale += consommation;
    }

    public void addToHistorique(double consommation) {
        this.historique.add(consommation);
        if (this.historique.size() > TAILLE_HISTORIQUE) {
            this.historique.remove(0);
        }
    }
}