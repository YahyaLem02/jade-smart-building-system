package behaviours;

import agents.AnalyseurEnergieAgent;
import jade.core.Agent;
import jade.core.behaviours.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnalyseEnergieBehaviour extends SequentialBehaviour {

    public AnalyseEnergieBehaviour(Agent a) {
        super(a);

        // 1. Comportement d'initialisation (OneShotBehaviour)
        addSubBehaviour(new InitEnergieBehaviour());

        // 2. Comportements parallèles pour analyse, suggestion et moyenne mobile
        addSubBehaviour(new AnalyseParalleleBehaviour(a));
    }

    // ========== SOUS-COMPORTEMENT 1: INITIALISATION ==========
    private class InitEnergieBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            AnalyseurEnergieAgent agent = (AnalyseurEnergieAgent) myAgent;
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║ [" + myAgent.getLocalName() + "] Initialisation de l'analyseur");
            System.out.println("║ Configuration:");
            System.out.println("║   - Période analyse: 7000ms (7s)");
            System.out.println("║   - Max analyses: " + agent.getMAX_ANALYSES());
            System.out.println("║   - Seuil économie: " + agent.getSEUIL_ECONOMIE() + " kW");
            System.out.println("║   - Taille historique: " + agent.getTAILLE_HISTORIQUE());
            System.out.println("║   - Liste des mesures initialisée");
            System.out.println("╚════════════════════════════════════════╝\n");
        }
    }

    // ========== COMPORTEMENTS PARALLÈLES ==========
    private class AnalyseParalleleBehaviour extends ParallelBehaviour {

        public AnalyseParalleleBehaviour(Agent a) {
            super(a, ParallelBehaviour.WHEN_ALL);

            // 2. Calcul de consommation (SimpleBehaviour avec période)
            addSubBehaviour(new CalculConsommationBehaviour());

            // 3. Suggestion d'économie (SimpleBehaviour avec condition)
            addSubBehaviour(new SuggestionEconomieBehaviour());

            // 4. Maintien moyenne mobile (SimpleBehaviour)
            addSubBehaviour(new MoyenneMobileBehaviour());
        }
    }

    // ========== SOUS-COMPORTEMENT 2: CALCUL CONSOMMATION ==========
    private class CalculConsommationBehaviour extends SimpleBehaviour {
        private long derniereTick = 0;
        private final long PERIODE = 7000;
        private boolean done = false;

        @Override
        public void action() {
            AnalyseurEnergieAgent agent = (AnalyseurEnergieAgent) myAgent;
            long maintenant = System.currentTimeMillis();

            // Vérifier si c'est le moment d'analyser
            if (derniereTick == 0 || (maintenant - derniereTick) >= PERIODE) {
                derniereTick = maintenant;

                // Vérifier si on a atteint la limite
                if (agent.getNbAnalyses() >= agent.getMAX_ANALYSES()) {
                    System.out.println("\n[" + agent.getLocalName() + "] ✓ Limite de " + agent.getMAX_ANALYSES() + " analyses atteinte");
                    done = true;
                    myAgent.doDelete();
                    return;
                }

                // Générer consommation entre 50 et 200 kW
                double consommation = 50 + (200 - 50) * agent.getRandom().nextDouble();
                agent.incrementNbAnalyses();
                agent.addConsommationTotale(consommation);

                // Ajouter à l'historique
                agent.addToHistorique(consommation);

                // Calculer moyenne mobile
                double moyenneMobile = agent.getHistorique().stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);

                // Formater l'heure
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String heure = sdf.format(new Date());

                // Afficher l'analyse
                System.out.printf("[%s] %s - 📊 Consommation: %.1f kW | Moyenne mobile: %.1f kW | Analyse %d/%d%n",
                        agent.getLocalName(), heure, consommation, moyenneMobile,
                        agent.getNbAnalyses(), agent.getMAX_ANALYSES());

            } else {
                // Attendre un peu avant de vérifier à nouveau
                block(500);
            }
        }

        @Override
        public boolean done() {
            return done;
        }
    }

    // ========== SOUS-COMPORTEMENT 3: SUGGESTION ÉCONOMIE ==========
    private class SuggestionEconomieBehaviour extends SimpleBehaviour {
        private boolean done = false;

        @Override
        public void action() {
            AnalyseurEnergieAgent agent = (AnalyseurEnergieAgent) myAgent;

            // Vérifier si on a terminé
            if (agent.getNbAnalyses() >= agent.getMAX_ANALYSES()) {
                done = true;
                return;
            }

            // Attendre qu'il y ait des données
            if (agent.getHistorique().isEmpty()) {
                block(1000);
                return;
            }

            // Calculer moyenne mobile
            double moyenneMobile = agent.getHistorique().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            // Condition: suggérer économies si moyenne > 150 kW
            if (moyenneMobile > agent.getSEUIL_ECONOMIE()) {
                suggererEconomies(agent, moyenneMobile);
            }

            // Attendre avant la prochaine vérification
            block(7000);
        }

        @Override
        public boolean done() {
            return done;
        }

        private void suggererEconomies(AnalyseurEnergieAgent agent, double moyenne) {
            double depassement = moyenne - agent.getSEUIL_ECONOMIE();
            System.out.println("\n  ╔═══════════════════════════════════════╗");
            System.out.println("  ║  💡 SUGGESTION D'ÉCONOMIE ACTIVÉE     ║");
            System.out.println("  ╠═══════════════════════════════════════╣");
            System.out.printf("  ║  Consommation: %.1f kW > %.1f kW     ║%n", moyenne, agent.getSEUIL_ECONOMIE());
            System.out.printf("  ║  Dépassement: %.1f kW                 ║%n", depassement);
            System.out.printf("  ║  Économie potentielle: %.1f kW        ║%n", depassement);

            // Suggestions aléatoires
            String[] suggestions = {
                    "Réduire l'éclairage zones inoccupées",
                    "Ajuster climatisation (±2°C)      ",
                    "Éteindre équipements en veille    ",
                    "Optimiser heures machines         "
            };
            String suggestion = suggestions[agent.getRandom().nextInt(suggestions.length)];
            System.out.println("  ║  Action: " + suggestion + " ║");
            System.out.println("  ╚═══════════════════════════════════════╝\n");
        }
    }

    // ========== SOUS-COMPORTEMENT 4: MOYENNE MOBILE ==========
    private class MoyenneMobileBehaviour extends SimpleBehaviour {
        private boolean done = false;
        private int dernierAffichage = 0;

        @Override
        public void action() {
            AnalyseurEnergieAgent agent = (AnalyseurEnergieAgent) myAgent;

            // Vérifier si on a terminé
            if (agent.getNbAnalyses() >= agent.getMAX_ANALYSES()) {
                afficherStatistiqueFinale(agent);
                done = true;
                return;
            }

            // Afficher statistiques toutes les 5 analyses
            if (agent.getNbAnalyses() > 0 &&
                    agent.getNbAnalyses() % 5 == 0 &&
                    agent.getNbAnalyses() != dernierAffichage) {

                dernierAffichage = agent.getNbAnalyses();
                afficherStatistiques(agent);
            }

            // Attendre avant la prochaine vérification
            block(7000);
        }

        @Override
        public boolean done() {
            return done;
        }

        private void afficherStatistiques(AnalyseurEnergieAgent agent) {
            double moyenneMobile = agent.getHistorique().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            double moyenneGlobale = agent.getNbAnalyses() > 0 ?
                    agent.getConsommationTotale() / agent.getNbAnalyses() : 0;

            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║  📈 STATISTIQUES INTERMÉDIAIRES        ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.printf("║  Analyses effectuées: %-16d║%n", agent.getNbAnalyses());
            System.out.printf("║  Moyenne mobile (5 dernières): %.1f kW ║%n", moyenneMobile);
            System.out.printf("║  Moyenne globale: %.1f kW              ║%n", moyenneGlobale);
            System.out.printf("║  Taille fenêtre: %-21d║%n", agent.getHistorique().size());

            if (moyenneMobile < agent.getSEUIL_ECONOMIE()) {
                System.out.println("║  Statut: 🟢 Consommation optimale      ║");
            } else {
                System.out.println("║  Statut: 🔴 Consommation élevée        ║");
            }
            System.out.println("╚════════════════════════════════════════╝\n");
        }

        private void afficherStatistiqueFinale(AnalyseurEnergieAgent agent) {
            double moyenneMobile = agent.getHistorique().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║  📊 MOYENNE MOBILE FINALE              ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.printf("║  Moyenne des 5 dernières: %.1f kW      ║%n", moyenneMobile);
            System.out.println("║  Historique:");
            for (int i = 0; i < agent.getHistorique().size(); i++) {
                System.out.printf("║    %d. %.1f kW                          ║%n",
                        i + 1, agent.getHistorique().get(i));
            }
            System.out.println("╚════════════════════════════════════════╝\n");
        }
    }
}