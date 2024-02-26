# RealmTech

## Introduction

Un petit jeu qui s'inspire de Factorio (dans la partie 2d de la carte)
et de Gregtech (pour la mécanique de progression des tiers). L'un
des buts premiers RealmTech est de changer la mécanique de progression
de Factorio pour qu'elle soit moins dans la production massive, 
mais dans l'évolution progressive des matériaux pour accéder
à de meilleures machines.L'un des buts est aussi de pouvoir avoir
une plus grande liberté de construction avec des blocs décoratifs
pour créer ses propres bâtiments.

## Jouer au jeu
### cmd
```shell
git clone https://github.com/FabienChatton/RealmTech
cd RealmTech
.\gradlew desktop:run
```
Attention, cette méthode stock le fichier RealmTechData dans le dossier assets, ce qui n'est pas le comportement attendu. Pour un lancement optimal, il est recommandé d'utiliser la méthode avec IntelliJ.
### IntelliJ
```shell
git clone https://github.com/FabienChatton/RealmTech
```
Il faut ensuite importer la configuration dans le dossier .run

![IntelliJ screenshot](data:image/png;base64,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)

### Docker (Server Only)

Pour avoir un serveur dedié. <s>Il existe une image sur docker hub qui peut être utilisé pour lancer un serveur.</s> Pour build l'image du serveur manuellement, il faut faire la commande de build qui va créer l'image realmtech-server, il est également possible de changer les build-arg pour spécifier le port, nom de sauvegarde et seed
du monde.

Pour lancer l'image docker, il suffit de faire la commande docker run ci-dessous. Vous pouvez également changer les arguments d'environnement pour le port, nom de sauvegarde et seed du monde (la seed 0 signifie aléatoire). Attention a bien spécifier l'emplacement de stockage du dossier RealmTechData, car c'est lui qui contient toute la sauvegarde du serveur.

```sh
docker build -t realmtech-server --build-arg port=25533 --build-arg savename=myworld --build-arg seed=0 -f Dockerfile .

docker run -d -p 25533:25533 -v ./RealmTechData/:/app/RealmTechData -e PORT=25533 -e SAVENAME=myworld -e SEED=0 --name realmtech-server realmtech-server
 ```

## Road Map

### indev

1. [x] Sauvegarde de la carte générée
2. [x] Génération de cartes procédurale
3. [x] Animation du joueur
4. [x] Inventaire du joueur
5. [x] Outils
6. [x] Récupération de ressource avec outils
7. [x] Système de craft
8. [x] Génération de minerais
9. [x] Machine basique

### infdev

1. [x] Carte infinie
2. [x] Serveur interne
3. [x] Console de debug

### alpha

1. [x] Cycle jour/nuit
2. [ ] Machine électrique
3. [ ] Infrastructure, convoyeur/pipe
4. [ ] Machine multi-cellules
5. [ ] Raccourci clavier

### beta 

1. [ ] Pétrole
2. [ ] Véhicule
3. [ ] Trains
4. [ ] Dimensions

## Documentation Architecture
RealmTech est la globalité du projet. La partie client est en soi, le jeu vu par le joueur. La partie serveur contient le serveur de jeu. Pour que le jeu fonctionne, il est nécessaire d'avoir une partie client et une partie serveur. Cependant, pas d'inquiétude : le client inclut également le serveur. De plus, le serveur peut être utilisé de manière "Standalone", c'est-à-dire sans le client. Ainsi, des clients peuvent se connecter à un serveur hébergé sur une machine dédiée.

```mermaid
graph LR
    subgraph RealmTech
        subgraph RealmTechClient
            subgraph Screen
                MenuScreen
                OptionScreen
                SavesScreen
            end
            ContextOption
            subgraph InGame
                subgraph Client
                    contextClient
                    subgraph EcsClient [ECS]
                        ecsClient
                        SystemsClient
                        EcsWorldClient
                        PhysiqueWorldClient
                        ConnexionClientIn
                        ConnexionClientOut
                        CommandExecuteClient
                    end
                    subgraph GameScreenClient [GameScreen]
                        GameScreen
                        PauseScreen
                    end
                    assets
                    GameOptionClient
                end
                subgraph RealmTechServer
                    subgraph Server
                        contextServer
                        subgraph EcsServer [ECS]
                            ecsServer
                            Components
                            SystemsServer
                            EcsWorldServer
                            PhysiqueWorldServer
                            ConnexionServerIn
                            ConnexionServerOut
                            CommandExecuteServer
                        end
                        GameOptionServer
                    end
                end
            end
        end
        contextClient --- ecsClient
        contextClient --- GameScreenClient
        contextClient --- assets

        ecsClient --- SystemsClient 
        ecsClient --- EcsWorldClient
        ecsClient --- PhysiqueWorldClient
        ecsClient --- ConnexionClientIn
        ecsClient --- ConnexionClientOut
        ecsClient --- CommandExecuteClient

        contextServer --- ecsServer

        ecsServer --- Components
        ecsServer --- SystemsServer
        ecsServer --- EcsWorldServer
        ecsServer --- PhysiqueWorldServer
        ecsServer --- ConnexionServerIn
        ecsServer --- ConnexionServerOut
        ecsServer --- CommandExecuteServer
    end
```
L'ECS est la partie centrale du jeu. C'est lui qui contient tout
le nécessaire au bon fonctionnement. Il est créé lors du lancement et s'éteint lors de la fermeture lorsque le joueur quitte la partie.
L'ECS contient :
- Système entités composant
- Monde physique
- Joueur
- Sauvegarde

### Organisation des projets
RealmTech est composé de 3 sous projets
- core
- server
- desktop

Le core est la partie comprenant le code pour la partie client, c'est ici
qu'on trouve tout ce qui est texture ou interface graphique.

Le server est la partie... serveur, il contient tout le code qui permet l'interaction entre le client et le serveur.
C'est ce sous-projet qui est le plus gros du code.

Le desktop ne comporte que la class main pour lancer le jeu sur pc.

Il y a le sous-projet test, mais il n'est utilisé que pour faire des tests unitaires.

```mermaid
flowchart
subgraph source
    core
    server
    desktop
end

subgraph dependencies
    LibGdx
    LibGdxLwjgl
    Netty
    Artemis
    Box2d
    Picocli
    DiscordRPC
end

core --> server
desktop --> core

server --> LibGdx
server --> Netty
server --> Artemis
server --> Box2d
server --> Picocli

core --> DiscordRPC

desktop --> LibGdxLwjgl
```


### Cellules
Les cellules composent le monde. Elles sont divisées en plusieurs catégories, en fonction de leur layer. Le layer représente le niveau où la cellule se trouve.

0. ground
1. ground deco
2. build
3. build déco

Ce système de layer, peut varier dans les futures versions du protocole
de sauvegarde du monde, mais pour le moment, il fonctionne comme sa.
Le layer est sauvegardé dans le CelleBehavior.
Le layer est utilisé pour poser la cellule sur le plateau


### Registre
Les registres permettent de stocker tout le contenu que le jeu va utiliser.
Ainsi, il est plus facile d'ajouter du contenu.
```mermaid
classDiagram
    direction BT
    class InfRegistry~T extends InfEntry~ {
        -InfRegistry~T~ parent
        -List~InfRegistry~T~~ enfants
        -String name
        +getId()
        +getEnfants()
        +add(T entry)
    }
    
    class InfRegistryEntry~T extends InfEntry~ {
        T entry
    }
    
    class InfEntry {
        <<interface>>
    }
    class InfCellEntry {
        
    }
    
    InfCellEntry --|> InfEntry
    InfRegistryEntry --|> InfRegistry
    InfRegistry --* InfRegistry
    InfRegistryEntry --* InfEntry
```

## Inventaire
Les inventaires permettent de stocker des items.
Les inventaires sont un tableau de deux dimensions, représentant dans la
première dimension, l'index de l'inventaire, et dans la deuxième,
le nombre d'item. L'inventaire peut être lié
à un system de craft.
### Protocole de sauvegarde inventaire
La version du protocole de sauvegarde sont les premiers 4 octets de l'inventaire
sérialisé.
```text
Métadonnées
    - version du protocole, int
    - nombre de row, int
    - nombre de slot par row, int
    - backgoundTextureName, String (US_ASCII)
 body
    pour chaque slot:
        - hash du registre de l'item, int
        - le nombre d'item, byte
```
Les rows sont les lignes, c'est-à-dire qu'elles vont de gauche à droite, alors
que les slot par row sont les colonnes, c'est-à-dire de haut en bas
```text
+-+-+ 
| | | <- row
+-+-+
| | |
+-+-+
 /\ 
 || slot par row
```
Un composant d'inventaire seul, n'est pas utile, il doit être utilisé avec un autre composant, comme un composant de coffre, ou un composant de table de craft

```mermaid
classDiagram
    class Player
    class Inventory
    class InventoryUi
    class Chest {
        -inventoryId
    }
    class CraftingTable {
        -inventoryCraftId
        -inventoryResultId
    }
    class Crafting

    Player *-- Chest
    Chest *-- Inventory
    Chest *-- InventoryUi
    
    Player *-- CraftingTable
    CraftingTable "2" *-- Inventory
    CraftingTable *-- InventoryUi
    CraftingTable *-- Crafting
```


## RealmTechData
Le dossier RealmTechData contient les informations nécessaires à
l'exécution du jeu. Le dossier est créé lors de la première execution
du jeu. Le dossier est sauvegardé au meme emplacement que le jar ou a la racine du repo.
Le dossier contient les fichiers de configurations et les sauvegardes des mondes.

### Hiérarchie du dossier
```text
|-- RealmTechData
    |-- saves
    |   |-- $sauvegarde 1
    |   |-- $sauvegarde 2
    |   |-- $sauvegarde 3
    |   ...
    |-- properties
        |-- options.cfg
```

## Protocole de sauvegarde de RealmTech
### Version 9

Le layer d'une cellule représente sa position dans la hauteur. C'est la
cellule avec le plus grand layer qui sera affiché. Le layer ne peut
pas être négatif.

0. ground
1. ground deco
2. build
3. build deco

### Hiérarchie dossier
```text
|-- $nomDeLaSauvegarde
    |-- playerInventory.psi
    |-- level
    |   |-- header.rsh
    |   |-- chunks
    |       |-- 0,0.rsc
    |       |-- 0,1.rsc
    |       |-- 1,0.rsc
    |       |-- ...  
    |-- players
        |-- [uuid]
            |-- inventory.pis
```
#### Fichier header.rsh
Ce fichier contient des métadonnées sur le monde.
```text
nomSauvegade, bytes len n + '\n'
seed, long
```
#### Fichier .rsc
Un fichier .rsc contient les données d'un chunk. Le fichier est nommée en
fonction du <code>chunk pos</code> du chunk. Les deux coordonnées sont
séparés par une virgule, par exemple : 12,-34.rsc.
```text
- nombre de cellule que contient le chunk, short
pour chaque cellule :
    - hash du cellRegisterEntry, int
    - position dans le chunk, byte
```
Un chunk fait <code>version protocole (int) + nombre de cells (short) * taille cell (5 bytes) + chunkPosX (int) + chunkPosY (int)</code>.

Une cellule fait <code>5 bytes</code>.
#### Fichier .psi

Le fichier de sauvegarde de l’inventaire d'un joueur.
Il utilise le serializer du coffre pour sauvegarder l'inventaire
du joueur.
