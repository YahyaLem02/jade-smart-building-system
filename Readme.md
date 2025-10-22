# 🏢 JADE Smart Building System

[![JADE](https://img.shields.io/badge/JADE-4.5.0-green.svg)](https://jade.tilab.com/)
[![Java](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Active-success.svg)]()

Un système de gestion intelligente de bâtiment utilisant la plateforme multi-agents JADE (Java Agent DEvelopment Framework). Ce projet implémente quatre agents autonomes pour surveiller et optimiser différents aspects d'un bâtiment intelligent.

## 📋 Table des Matières

- [À Propos](#à-propos)
- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Structure du Projet](#structure-du-projet)
- [Agents Implémentés](#agents-implémentés)
- [Technologies](#technologies)
- [Captures d'Écran](#captures-décran)
- [Auteur](#auteur)
- [Licence](#licence)

## 🎯 À Propos

Ce projet propose une solution multi-agents pour la gestion automatisée d'un bâtiment intelligent. Chaque agent fonctionne de manière autonome et gère un aspect spécifique :
- Surveillance de la température
- Détection de mouvements
- Comptage des personnes
- Analyse de la consommation énergétique

Le système utilise différents types de comportements JADE (OneShotBehaviour, TickerBehaviour, CyclicBehaviour, SimpleBehaviour) pour simuler un environnement réel de gestion de bâtiment.

## ✨ Fonctionnalités

### 🌡️ Capteur de Température
- Mesures périodiques toutes les 5 secondes
- Génération de températures entre 15°C et 30°C
- Système d'alerte au-dessus de 28°C
- Arrêt automatique après 20 mesures

### 🚶 Capteur de Mouvement
- Détection continue avec probabilité de 40%
- Vérifications toutes les 3 secondes
- Statistiques de détection en temps réel
- Limite de 30 vérifications

### 👥 Compteur de Personnes
- Simulation d'entrées (60%) et sorties (40%)
- Suivi du nombre de personnes présentes
- Statistiques détaillées toutes les 10 opérations
- Détection du pic de fréquentation

### ⚡ Analyseur d'Énergie
- Analyse de consommation toutes les 7 secondes
- Moyenne mobile sur 5 mesures
- Suggestions d'économie au-dessus de 150 kW
- Consommation entre 50 et 200 kW

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│           Plateforme JADE Container             │
├─────────────────────────────────────────────────┤
│  ┌───────────────┐  ┌───────────────────────┐  │
│  │   Capteur     │  │   Capteur Mouvement   │  │
│  │  Température  │  │   (CyclicBehaviour)   │  │
│  │ (TickerBeh.)  │  │                       │  │
│  └───────────────┘  └───────────────────────┘  │
│  ┌───────────────┐  ┌───────────────────────┐  │
│  │   Compteur    │  │   Analyseur Énergie   │  │
│  │   Personnes   │  │ (ParallelBehaviour)   │  │
│  │ (ParallelBeh.)│  │                       │  │
│  └───────────────┘  └───────────────────────┘  │
└─────────────────────────────────────────────────┘
```

### Types de Comportements Utilisés

| Agent | Comportement Principal | Sous-Comportements |
|-------|----------------------|-------------------|
| **CapteurTemperature** | SequentialBehaviour | OneShotBehaviour, TickerBehaviour, SimpleBehaviour |
| **CapteurMouvement** | SequentialBehaviour | OneShotBehaviour, CyclicBehaviour, SimpleBehaviour |
| **CompteurPersonnes** | ParallelBehaviour | OneShotBehaviour, TickerBehaviour, SimpleBehaviour |
| **AnalyseurEnergie** | SequentialBehaviour | OneShotBehaviour, SimpleBehaviour (×3) |

## 📦 Prérequis

- **Java JDK 8** ou supérieur
- **JADE 4.5.0** ou supérieur
- **Maven** (optionnel)
- **Lombok** (pour les annotations)
- **IDE** : IntelliJ IDEA, Eclipse, ou VS Code

## 🚀 Installation

### 1. Cloner le Repository

```bash
git clone https://github.com/YahyaLem02/jade-smart-building-system.git
cd jade-smart-building-system
```

### 2. Télécharger JADE

```bash
# Télécharger JADE depuis le site officiel
wget https://jade.tilab.com/download/JADE-bin-4.5.0.zip
unzip JADE-bin-4.5.0.zip -d lib/
```

Ou téléchargez manuellement depuis [jade.tilab.com](https://jade.tilab.com/)

### 3. Configuration Maven (Optionnel)

Si vous utilisez Maven, ajoutez dans votre `pom.xml` :

```xml
<dependencies>
    <dependency>
        <groupId>com.tilab.jade</groupId>
        <artifactId>jade</artifactId>
        <version>4.5.0</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### 4. Compilation

```bash
# Avec Maven
mvn clean compile

# Sans Maven (avec javac)
javac -cp "lib/jade.jar:lib/lombok.jar" -d bin src/**/*.java
```

## 💻 Utilisation

### Démarrage du Système

```bash
# Avec Maven
mvn exec:java -Dexec.mainClass="main.MainContainer"

# Sans Maven
java -cp "bin:lib/jade.jar:lib/lombok.jar" main.MainContainer
```

### Démarrage avec Interface Graphique JADE

```bash
java -cp "bin:lib/jade.jar" jade.Boot -gui
```

Puis créer les agents manuellement via l'interface JADE :
- `CapteurTemperature:agents.CapteurTemperatureAgent`
- `CapteurMouvement:agents.CapteurMouvementAgent`
- `CompteurPersonnes:agents.CompteurPersonnesAgent`
- `AnalyseurEnergie:agents.AnalyseurEnergieAgent`

### Configuration des Paramètres

Modifiez les constantes dans chaque classe agent :

```java
// CapteurTemperatureAgent.java
protected final int MAX_MESURES = 20;
protected final double SEUIL_ALERTE = 28.0;

// CapteurMouvementAgent.java
private final double PROBABILITE_MOUVEMENT = 0.4;

// CompteurPersonnesAgent.java
private final int STATS_INTERVAL = 10;

// AnalyseurEnergieAgent.java
private final double SEUIL_ECONOMIE = 150.0;
private final int TAILLE_HISTORIQUE = 5;
```

## 📁 Structure du Projet

```
jade-smart-building-system/
│
├── src/
│   ├── agents/
│   │   ├── CapteurTemperatureAgent.java
│   │   ├── CapteurMouvementAgent.java
│   │   ├── CompteurPersonnesAgent.java
│   │   └── AnalyseurEnergieAgent.java
│   │
│   ├── behaviours/
│   │   ├── MesureTemperatureBehaviour.java
│   │   ├── DetectionMouvementBehaviour.java
│   │   ├── ComptagePersonneBehaviour.java
│   │   └── AnalyseEnergieBehaviour.java
│   │
│   └── main/
│       └── MainContainer.java
│
├── lib/
│   └── jade.jar
│
├── docs/
│   └── rapport.pdf
│
├── images/
│   ├── capteur_temperature.png
│   ├── capteur_mouvement.png
│   ├── compteur_personnes.png
│   └── analyseur_energie.png
│
├── pom.xml
├── README.md
└── LICENSE
```

## 🤖 Agents Implémentés

### 1. CapteurTemperatureAgent 🌡️

**Type** : Agent Réactif  
**Comportement** : SequentialBehaviour

- **InitialisationBehaviour** : Affiche la configuration
- **MesurePeriodiqueSubBehaviour** : Mesures toutes les 5s
- **VerificationAlerteSubBehaviour** : Vérification finale
- **ShutdownSubBehaviour** : Arrêt propre de l'agent

**Paramètres** :
- Période : 5000ms
- Plage température : 15-30°C
- Seuil alerte : 28°C
- Max mesures : 20

### 2. CapteurMouvementAgent 🚶

**Type** : Agent Réactif  
**Comportement** : SequentialBehaviour

- **InitMovementBehaviour** : Configuration initiale
- **DetectionMouvementSubBehaviour** : Détection cyclique
- **ComptageDetectionsSubBehaviour** : Statistiques finales

**Paramètres** :
- Pause : 3000ms
- Probabilité détection : 40%
- Max vérifications : 30

### 3. CompteurPersonnesAgent 👥

**Type** : Agent Basé sur Modèle  
**Comportement** : ParallelBehaviour

- **InitCompteurBehaviour** : Initialisation compteur
- **SimulationFluxBehaviour** : Simulation entrées/sorties
- **MiseAJourEtatBehaviour** : État final
- **StatistiquesMonitorBehaviour** : Stats périodiques

**Paramètres** :
- Période : 4000ms
- Probabilité entrée : 60%
- Stats interval : 10 opérations
- Max opérations : 30

### 4. AnalyseurEnergieAgent ⚡

**Type** : Agent Basé sur Buts  
**Comportement** : ParallelBehaviour

- **InitEnergieBehaviour** : Initialisation analyseur
- **CalculConsommationBehaviour** : Calcul consommation
- **SuggestionEconomieBehaviour** : Suggestions intelligentes
- **MoyenneMobileBehaviour** : Moyenne glissante

**Paramètres** :
- Période : 7000ms
- Plage consommation : 50-200 kW
- Seuil économie : 150 kW
- Fenêtre historique : 5 mesures
- Max analyses : 15

## 🛠️ Technologies

- **Framework** : JADE 4.5.0
- **Langage** : Java 8+
- **Build Tool** : Maven (optionnel)
- **Annotations** : Lombok
- **Paradigme** : Programmation orientée agents
- **Architecture** : Multi-agents autonomes

## 📸 Captures d'Écran

### Interface JADE
![JADE Interface](images/jade_interface.png)

### Capteur de Température
![Capteur Température](images/capteur_temperature.png)

### Capteur de Mouvement
![Capteur Mouvement](images/capteur_mouvement.png)

### Compteur de Personnes
![Compteur Personnes](images/compteur_personnes.png)

### Analyseur d'Énergie
![Analyseur Énergie](images/analyseur_energie.png)

## 📊 Résultats Attendus

Le système affiche en temps réel :
- ✅ Mesures de température avec alertes
- ✅ Détections de mouvements avec statistiques
- ✅ Flux d'entrées/sorties de personnes
- ✅ Consommation énergétique avec suggestions
- ✅ Rapports finaux détaillés pour chaque agent

## 🔧 Développement

### Ajouter un Nouvel Agent

1. Créer la classe agent dans `src/agents/`
2. Créer le behaviour dans `src/behaviours/`
3. Ajouter l'agent dans `MainContainer.java`

```java
AgentController nouveauAgent = container.createNewAgent(
    "NomAgent",
    "agents.NouveauAgent",
    new Object[]{}
);
nouveauAgent.start();
```

### Personnaliser un Comportement

Modifiez les paramètres dans les classes agents ou créez de nouveaux sous-comportements dans les classes behaviour.

## 📝 Documentation

- [JADE Documentation](https://jade.tilab.com/documentation/)
- [JADE Tutorial](https://jade.tilab.com/doc/tutorials/JADEProgramming-Tutorial-for-beginners.pdf)
- [Rapport du Projet](docs/rapport.pdf)

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créez une branche (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📧 Contact

**Yahya Lemjid** - [@YahyaLem02](https://github.com/YahyaLem02)

Project Link: [https://github.com/YahyaLem02/jade-smart-building-system](https://github.com/YahyaLem02/jade-smart-building-system)

## 🎓 Contexte Académique

Ce projet a été réalisé dans le cadre d'un travail pratique sur les systèmes multi-agents et la plateforme JADE. Il illustre les concepts suivants :
- Architecture multi-agents
- Autonomie des agents
- Différents types de comportements JADE
- Programmation orientée agents
- Systèmes intelligents distribués

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

<div align="center">

**⭐ Si ce projet vous a été utile, n'hésitez pas à lui donner une étoile ! ⭐**

Made with ❤️ by [Yahya Lemjid](https://github.com/YahyaLem02)

</div>