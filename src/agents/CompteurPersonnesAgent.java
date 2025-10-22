package agents;

import behaviours.ComptagePersonneBehaviour;
import jade.core.Agent;
import lombok.Getter;
import java.util.Random;

@Getter
public class CompteurPersonnesAgent extends Agent {
    private Random random;
    private int personnesPresentes = 0;
    private int totalEntrees = 0;
    private int totalSorties = 0;
    private int nbOperations = 0;
    private int maxPersonnes = 0;
    private final int STATS_INTERVAL = 10;

    @Override
    protected void setup() {
        random = new Random();
        System.out.println("\n[" + getLocalName() + "] 🚀 Démarrage du compteur de personnes\n");

        // Un seul comportement qui contient tous les autres
        addBehaviour(new ComptagePersonneBehaviour(this));
    }

    @Override
    protected void takeDown() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     RAPPORT FINAL - COMPTEUR PERSONNES   ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf("║ Personnes actuellement présentes: %-6d║%n", personnesPresentes);
        System.out.printf("║ Total entrées: %-26d║%n", totalEntrees);
        System.out.printf("║ Total sorties: %-26d║%n", totalSorties);
        System.out.printf("║ Maximum atteint: %-22d║%n", maxPersonnes);
        System.out.printf("║ Nombre d'opérations: %-19d║%n", nbOperations);
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("\n[" + getLocalName() + "] 👋 Agent terminé avec succès\n");
    }

    // Méthodes pour modifier les attributs
    public void incrementPersonnesPresentes() {
        this.personnesPresentes++;
    }

    public void decrementPersonnesPresentes() {
        if (this.personnesPresentes > 0) {
            this.personnesPresentes--;
        }
    }

    public void incrementTotalEntrees() {
        this.totalEntrees++;
    }

    public void incrementTotalSorties() {
        this.totalSorties++;
    }

    public void incrementNbOperations() {
        this.nbOperations++;
    }

    public void updateMaxPersonnes(int valeur) {
        if (valeur > this.maxPersonnes) {
            this.maxPersonnes = valeur;
        }
    }
}