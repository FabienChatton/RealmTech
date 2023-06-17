# format : .rts (RealmTechFileSave)
# version 6

Le layer d'une cellule représente sa position dans la hauteur. C'est la
cellule avec le plus grand layer qui sera affiché. Le layer ne peut
pas être négatif.

0. ground
1. ground deco
2. build
3. build deco

## Hiérarchie dossier
````
|-- $nomDeLaSauvegarde
    |-- level
        |-- header.rsh
        |-- chunks
            |-- 0-0.rsc
            |-- 0-1.rsc
            |-- 1-0.rsc
            |-- ...  
````
### fichier header.rsh
Ce fichier contient des métadonnées sur le monde.
````
"RealmTech", String (offset 0, len 9 bytes)
Version protocole file save, int (offset 9, len 4 bytes)
save date, long (offset 13, len 8 bytes)
seed, long (offset 22, len 8)
player position x, float (offset 30, len 4)
player position y, float (offset 34, len 4)
````
### fichier .rsc
Le nom du fichier qui correspond à un fichier .rsc, correspond à la position
du chunk en jeu. Le nom du fichier contient la position X du chunk, un "-" et
La position Y du chunk. 12-23.rsc. Chaque fichier contient un petit header pour
specifier la version du protocole de sauvegarde. Le header contient aussi
un "tableau associatif" entre hash (int) et id (byte) des entrées des registres
des celles pour éviter de les hash soit pour chaque cellule dans le fichier.
````
Métadonnées
    - version du protocole, int (offset 0, len 4 bytes)
Header
    - nombre de layer, byte (offset 5, len 1 byte)
    pour chaque layer :
        - nombre de cellule sur ce layer, short (offset 6, len 2 bytes)
Body
    pour chaque cellule :
        - hash du cellRegisterEntry, byte (4 bytes)
        - position dans le chunk, byte (1 byte)
````

