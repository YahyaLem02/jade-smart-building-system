package behaviours;

import agents.CapteurMouvementAgent;
import jade.core.Agent;
import jade.core.behaviours.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetectionMouvementBehaviour extends SequentialBehaviour {

    public DetectionMouvementBehaviour(Agent a) {
        super(a);

        // 1. Comportement d'initialisation (OneShotBehaviour)
        addSubBehaviour(new InitMovementBehaviour());

        // 2. Comportement de détection cyclique (CyclicBehaviour)
        addSubBehaviour(new DetectionMouvementSubBehaviour());

        // 3. Comportement de comptage final (SimpleBehaviour)
        addSubBehaviour(new ComptageDetectionsSubBehaviour());
    }

    // ========== SOUS-COMPORTEMENT 1: INITIALISATION ==========
    private class InitMovementBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            CapteurMouvementAgent agent = (CapteurMouvementAgent) myAgent;
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║ [" + myAgent.getLocalName() + "] Initialisation du capteur");
            System.out.println("║ Configuration:");
            System.out.println("║   - Probabilité mouvement: " + (agent.getPROBABILITE_MOUVEMENT() * 100) + "%");
            System.out.println("║   - Pause entre détections: 3 secondes");
            System.out.println("║   - Mode: Détection continue");
            System.out.println("╚════════════════════════════════════════╝");
        }
    }

    // ========== SOUS-COMPORTEMENT 2: DÉTECTION CYCLIQUE ==========
    private class DetectionMouvementSubBehaviour extends CyclicBehaviour {
        private static final int MAX_VERIFICATIONS = 30;

        @Override
        public void action() {
            CapteurMouvementAgent agent = (CapteurMouvementAgent) myAgent;

            // Vérifier si on atteint la limite
            if (agent.getNbVerifications() >= MAX_VERIFICATIONS) {
                System.out.println("\n[" + agent.getLocalName() + "] ✓ Limite de vérifications atteinte");

                // Passer au comportement suivant
                myAgent.removeBehaviour(this);
                return;
            }

            // Pause de 3 secondes
            block(3000);

            agent.incrementNbVerifications();
            boolean mouvementDetecte = agent.getRandom().nextDouble() < agent.getPROBABILITE_MOUVEMENT();

            // Formater l'heure
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String heure = sdf.format(new Date());

            if (mouvementDetecte) {
                agent.incrementNbDetections();
                System.out.println("[" + agent.getLocalName() + "] " + heure +
                        " - 🔴 MOUVEMENT DÉTECTÉ ! (Total: " + agent.getNbDetections() +
                        "/" + agent.getNbVerifications() + ")");
            } else {
                System.out.println("[" + agent.getLocalName() + "] " + heure +
                        " - ⚪ Aucun mouvement (" + agent.getNbVerifications() + ")");
            }
        }
    }

    // ========== SOUS-COMPORTEMENT 3: COMPTAGE FINAL ==========
    private class ComptageDetectionsSubBehaviour extends SimpleBehaviour {
        private boolean done = false;

        @Override
        public void action() {
            CapteurMouvementAgent agent = (CapteurMouvementAgent) myAgent;

            // Calculer le taux de détection
            double tauxDetection = agent.getNbVerifications() > 0 ?
                    (agent.getNbDetections() * 100.0 / agent.getNbVerifications()) : 0;

            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║ [" + agent.getLocalName() + "] COMPTAGE FINAL");
            System.out.println("║ Total vérifications: " + agent.getNbVerifications());
            System.out.println("║ Total détections: " + agent.getNbDetections());
            System.out.println("║ Taux de détection: " + String.format("%.1f%%", tauxDetection));

            if (tauxDetection > 50) {
                System.out.println("║ Statut: 🔴 Activité élevée");
            } else if (tauxDetection > 20) {
                System.out.println("║ Statut: 🟡 Activité normale");
            } else {
                System.out.println("║ Statut: 🟢 Activité faible");
            }
            System.out.println("╚════════════════════════════════════════╝\n");

            // Arrêter l'agent après le comptage
            myAgent.doDelete();
            done = true;
        }

        @Override
        public boolean done() {
            return done;
        }
    }
}