package behaviours;

import agents.CapteurTemperatureAgent;
import jade.core.Agent;
import jade.core.behaviours.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MesureTemperatureBehaviour extends SequentialBehaviour {

    // Variable partagée entre les sous-comportements
    private double derniereTemperature = 0.0;

    public MesureTemperatureBehaviour(Agent a) {
        super(a);

        // 1. Comportement d'initialisation (OneShotBehaviour)
        addSubBehaviour(new InitialisationBehaviour());

        // 2. Comportement de mesure périodique (TickerBehaviour)
        addSubBehaviour(new MesurePeriodiqueSubBehaviour(a, 5000));

        // 3. Comportement de vérification d'alerte (SimpleBehaviour)
        addSubBehaviour(new VerificationAlerteSubBehaviour());

        // 4. Comportement de shutdown (OneShotBehaviour)
        addSubBehaviour(new ShutdownSubBehaviour());
    }

    // ========== SOUS-COMPORTEMENT 1: INITIALISATION ==========
    private class InitialisationBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║ [" + myAgent.getLocalName() + "] Initialisation du capteur");
            System.out.println("║ Configuration:");
            CapteurTemperatureAgent agent = (CapteurTemperatureAgent) myAgent;
            System.out.println("║   - Max mesures: " + agent.getMAX_MESURES());
            System.out.println("║   - Seuil alerte: " + agent.getSEUIL_ALERTE() + "°C");
            System.out.println("║   - Période: 5 secondes");
            System.out.println("╚════════════════════════════════════════╝");
        }
    }

    // ========== SOUS-COMPORTEMENT 2: MESURE PÉRIODIQUE ==========
    private class MesurePeriodiqueSubBehaviour extends TickerBehaviour {
        private Random random = new Random();

        public MesurePeriodiqueSubBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            CapteurTemperatureAgent agent = (CapteurTemperatureAgent) myAgent;

            // Vérifier si on a atteint la limite
            if (agent.getNbMesures() >= agent.getMAX_MESURES()) {
                System.out.println("\n[" + agent.getLocalName() + "] ✓ Limite de " + agent.getMAX_MESURES() + " mesures atteinte");

                // Arrêter ce comportement pour passer au suivant
                stop();
                return;
            }

            // Générer température aléatoire entre 15 et 30°C
            double temperature = 15 + (30 - 15) * random.nextDouble();
            derniereTemperature = temperature;

            // Incrémenter le compteur
            agent.incrementNbMesures();

            // Formater l'heure
            String heure = new SimpleDateFormat("HH:mm:ss").format(new Date());

            // Afficher la mesure
            System.out.printf("[%s] %s - Température: %.1f°C (Mesure %d/%d)",
                    agent.getLocalName(), heure, temperature,
                    agent.getNbMesures(), agent.getMAX_MESURES());

            // Alerte intermédiaire si température élevée
            if (temperature > agent.getSEUIL_ALERTE()) {
                System.out.print(" ⚠️  ALERTE!");
            }
            System.out.println();
        }
    }

    // ========== SOUS-COMPORTEMENT 3: VÉRIFICATION ALERTE ==========
    private class VerificationAlerteSubBehaviour extends SimpleBehaviour {
        private boolean done = false;

        @Override
        public void action() {
            CapteurTemperatureAgent agent = (CapteurTemperatureAgent) myAgent;

            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║ [" + agent.getLocalName() + "] VÉRIFICATION FINALE");
            System.out.println("║ Nombre de mesures: " + agent.getNbMesures());
            System.out.println("║ Dernière température: " + String.format("%.1f°C", derniereTemperature));
            System.out.println("║ Seuil d'alerte: " + agent.getSEUIL_ALERTE() + "°C");

            // Vérifier le seuil d'alerte
            if (derniereTemperature > agent.getSEUIL_ALERTE()) {
                System.out.println("║ Statut: 🚨 ALERTE - Température élevée!");
            } else {
                System.out.println("║ Statut: ✅ Normal");
            }
            System.out.println("╚════════════════════════════════════════╝\n");

            done = true;
        }

        @Override
        public boolean done() {
            return done;
        }
    }

    // ========== SOUS-COMPORTEMENT 4: SHUTDOWN ==========
    private class ShutdownSubBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            CapteurTemperatureAgent agent = (CapteurTemperatureAgent) myAgent;
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║ [" + agent.getLocalName() + "] ARRÊT DU CAPTEUR");
            System.out.println("║ Total mesures effectuées: " + agent.getNbMesures());
            System.out.println("║ Nettoyage des ressources...");
            System.out.println("╚════════════════════════════════════════╝");

            // Arrêter l'agent
            myAgent.doDelete();
        }
    }
}