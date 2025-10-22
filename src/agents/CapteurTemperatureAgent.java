package agents;

import behaviours.MesureTemperatureBehaviour;
import jade.core.Agent;
import lombok.Getter;

@Getter
public class CapteurTemperatureAgent extends Agent {
    protected int nbMesures = 0;
    protected final int MAX_MESURES = 20;
    protected final double SEUIL_ALERTE = 28.0;

    @Override
    protected void setup() {
        System.out.println("\n[" + getLocalName() + "] 🚀 Démarrage du capteur de température\n");

        // Un seul comportement qui contient tous les autres
        addBehaviour(new MesureTemperatureBehaviour(this));
    }

    public void incrementNbMesures() {
        this.nbMesures++;
    }

    @Override
    protected void takeDown() {
        System.out.println("\n[" + getLocalName() + "] 👋 Agent terminé avec succès\n");
    }
}