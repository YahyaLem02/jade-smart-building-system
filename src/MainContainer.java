import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class MainContainer {

    public static void main(String[] args) {
        try {
            // Obtenir le runtime JADE
            Runtime runtime = Runtime.instance();

            // Créer un profil par défaut
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.MAIN_PORT, "1099");
            profile.setParameter(Profile.GUI, "true"); // Interface graphique JADE

            // Créer le conteneur principal
            AgentContainer mainContainer = runtime.createMainContainer(profile);

            System.out.println("╔════════════════════════════════════════════════════╗");
            System.out.println("║    SYSTÈME DE CAPTEURS INTELLIGENTS - JADE        ║");
            System.out.println("║           Bâtiment Intelligent v1.0               ║");
            System.out.println("╚════════════════════════════════════════════════════╝\n");

            // Créer et démarrer les agents
            System.out.println("🚀 Démarrage des agents capteurs...\n");

            // Agent 1: Capteur de Température
            AgentController agentTemp = mainContainer.createNewAgent(
                    "CapteurTemp",
                    "agents.CapteurTemperatureAgent",
                    new Object[0]
            );
            agentTemp.start();
            Thread.sleep(500);

            // Agent 2: Capteur de Mouvement
            AgentController agentMouv = mainContainer.createNewAgent(
                    "CapteurMouv",
                    "agents.CapteurMouvementAgent",
                    new Object[0]
            );
            agentMouv.start();
            Thread.sleep(500);

            // Agent 3: Compteur de Personnes
            AgentController agentCompteur = mainContainer.createNewAgent(
                    "CompteurPersonnes",
                    "agents.CompteurPersonnesAgent",
                    new Object[0]
            );
            agentCompteur.start();
            Thread.sleep(500);

            // Agent 4: Analyseur d'Énergie
            AgentController agentEnergie = mainContainer.createNewAgent(
                    "AnalyseurEnergie",
                    "agents.AnalyseurEnergieAgent",
                    new Object[0]
            );
            agentEnergie.start();

            System.out.println("\n✅ Tous les agents sont démarrés!");
            System.out.println("📊 Observation en cours...\n");
            System.out.println("═══════════════════════════════════════════════════\n");

        } catch (StaleProxyException e) {
            System.err.println("❌ Erreur lors de la création des agents: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("❌ Erreur d'interruption: " + e.getMessage());
            e.printStackTrace();
        }
    }
}