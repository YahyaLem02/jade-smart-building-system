package behaviours;

import agents.CompteurPersonnesAgent;
import jade.core.Agent;
import jade.core.behaviours.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ComptagePersonneBehaviour extends ParallelBehaviour {

    public ComptagePersonneBehaviour(Agent a) {
        super(a, ParallelBehaviour.WHEN_ALL);

        // 1. Comportement d'initialisation (OneShotBehaviour)
        addSubBehaviour(new InitCompteurBehaviour());

        // 2. Comportement de simulation flux (TickerBehaviour) + MàJ état (SimpleBehaviour)
        addSubBehaviour(new SimulationFluxEtatSequence(a));

        // 3. Comportement d'affichage statistiques
        addSubBehaviour(new StatistiquesMonitorBehaviour());
    }

    // ========== SOUS-COMPORTEMENT 1: INITIALISATION ==========
    private class InitCompteurBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            CompteurPersonnesAgent agent = (CompteurPersonnesAgent) myAgent;
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║ [" + myAgent.getLocalName() + "] Initialisation du compteur");
            System.out.println("║ Configuration:");
            System.out.println("║   - Intervalle simulation: 4000ms");
            System.out.println("║   - Intervalle stats: " + agent.getSTATS_INTERVAL() + " opérations");
            System.out.println("║   - Probabilité entrée: 60%");
            System.out.println("║   - Probabilité sortie: 40%");
            System.out.println("╚════════════════════════════════════════╝\n");
        }
    }

    // ========== SÉQUENCE: SIMULATION + MÀJ ÉTAT ==========
    private class SimulationFluxEtatSequence extends SequentialBehaviour {

        public SimulationFluxEtatSequence(Agent a) {
            super(a);

            // 2a. Simulation des flux (TickerBehaviour)
            addSubBehaviour(new SimulationFluxBehaviour(a, 4000));

            // 2b. Maintien de l'état interne (SimpleBehaviour)
            addSubBehaviour(new MiseAJourEtatBehaviour());
        }
    }

    // ========== SOUS-COMPORTEMENT 2a: SIMULATION FLUX ==========
    private class SimulationFluxBehaviour extends TickerBehaviour {
        private static final int MAX_OPERATIONS = 30;

        public SimulationFluxBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            CompteurPersonnesAgent agent = (CompteurPersonnesAgent) myAgent;

            // Vérifier si on atteint la limite
            if (agent.getNbOperations() >= MAX_OPERATIONS) {
                System.out.println("\n[" + agent.getLocalName() + "] ✓ Limite de " + MAX_OPERATIONS + " opérations atteinte\n");
                stop();
                return;
            }

            agent.incrementNbOperations();

            // Décider entrée ou sortie (60% entrée, 40% sortie)
            boolean entree = agent.getRandom().nextDouble() < 0.6 || agent.getPersonnesPresentes() == 0;

            // Formater l'heure
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String heure = sdf.format(new Date());

            if (entree) {
                agent.incrementPersonnesPresentes();
                agent.incrementTotalEntrees();
                agent.updateMaxPersonnes(agent.getPersonnesPresentes());

                System.out.printf("[%s] %s - 🟢 ENTRÉE | Présents: %d | Total entrées: %d%n",
                        agent.getLocalName(), heure, agent.getPersonnesPresentes(), agent.getTotalEntrees());
            } else {
                if (agent.getPersonnesPresentes() > 0) {
                    agent.decrementPersonnesPresentes();
                    agent.incrementTotalSorties();

                    System.out.printf("[%s] %s - 🔴 SORTIE | Présents: %d | Total sorties: %d%n",
                            agent.getLocalName(), heure, agent.getPersonnesPresentes(), agent.getTotalSorties());
                }
            }
        }
    }

    // ========== SOUS-COMPORTEMENT 2b: MISE À JOUR ÉTAT ==========
    private class MiseAJourEtatBehaviour extends SimpleBehaviour {
        private boolean done = false;

        @Override
        public void action() {
            CompteurPersonnesAgent agent = (CompteurPersonnesAgent) myAgent;

            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║ [" + agent.getLocalName() + "] ÉTAT FINAL DU SYSTÈME");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.printf("║ Personnes présentes: %-17d║%n", agent.getPersonnesPresentes());
            System.out.printf("║ Maximum atteint: %-21d║%n", agent.getMaxPersonnes());
            System.out.printf("║ Flux net: %-29d║%n", (agent.getTotalEntrees() - agent.getTotalSorties()));
            System.out.println("╚════════════════════════════════════════╝\n");

            done = true;
        }

        @Override
        public boolean done() {
            return done;
        }
    }

    // ========== SOUS-COMPORTEMENT 3: AFFICHAGE STATISTIQUES ==========
    private class StatistiquesMonitorBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            CompteurPersonnesAgent agent = (CompteurPersonnesAgent) myAgent;

            // Attendre un peu avant de vérifier
            block(4000);

            // Afficher statistiques tous les 10 opérations
            if (agent.getNbOperations() > 0 && agent.getNbOperations() % agent.getSTATS_INTERVAL() == 0) {
                afficherStatistiques(agent);
            }

            // Arrêter ce comportement quand la simulation est terminée
            if (agent.getNbOperations() >= 30) {
                myAgent.removeBehaviour(this);
            }
        }

        private void afficherStatistiques(CompteurPersonnesAgent agent) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║   📊 STATISTIQUES INTERMÉDIAIRES       ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.printf("║ Opérations effectuées: %-15d║%n", agent.getNbOperations());
            System.out.printf("║ Présents actuellement: %-15d║%n", agent.getPersonnesPresentes());
            System.out.printf("║ Total entrées: %-23d║%n", agent.getTotalEntrees());
            System.out.printf("║ Total sorties: %-23d║%n", agent.getTotalSorties());
            System.out.printf("║ Maximum atteint: %-21d║%n", agent.getMaxPersonnes());
            System.out.printf("║ Flux net: %-29d║%n", (agent.getTotalEntrees() - agent.getTotalSorties()));

            // Indicateur de capacité
            double tauxOccupation = (agent.getPersonnesPresentes() * 100.0) / Math.max(1, agent.getMaxPersonnes());
            if (tauxOccupation >= 80) {
                System.out.println("║ Statut: 🔴 Capacité élevée              ║");
            } else if (tauxOccupation >= 50) {
                System.out.println("║ Statut: 🟡 Capacité moyenne             ║");
            } else {
                System.out.println("║ Statut: 🟢 Capacité normale             ║");
            }
            System.out.println("╚════════════════════════════════════════╝\n");
        }
    }
}