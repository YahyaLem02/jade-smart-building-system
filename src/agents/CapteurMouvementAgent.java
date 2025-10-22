package agents;

import behaviours.DetectionMouvementBehaviour;
import jade.core.Agent;
import lombok.Getter;
import java.util.Random;

@Getter
public class CapteurMouvementAgent extends Agent {
    private Random random;
    private int nbDetections = 0;
    private int nbVerifications = 0;
    private final double PROBABILITE_MOUVEMENT = 0.4;

    @Override
    protected void setup() {
        random = new Random();
        System.out.println("\n[" + getLocalName() + "] 🚀 Démarrage du capteur de mouvement\n");

        // Un seul comportement qui contient tous les autres
        addBehaviour(new DetectionMouvementBehaviour(this));
    }

    @Override
    protected void takeDown() {
        System.out.println("\n[" + getLocalName() + "] 👋 Agent terminé avec succès\n");
    }

    // Méthodes pour modifier les compteurs
    public void incrementNbDetections() {
        this.nbDetections++;
    }

    public void incrementNbVerifications() {
        this.nbVerifications++;
    }
}