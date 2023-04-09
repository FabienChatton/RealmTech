# format : .rts (RealmTrechFileSave)

# version 1
## Header
- "RealmTech" (offset 0, len 9 bytes)
- Version protocole file save : int (offset 9, len 4 bytes)
- save date long (offset 13, len 8 bytes)

### corps
C'est ici que contient tous les chunks, tous les blocks.
Les exemples d'offset sont pour les premiers du fichier. 

- chunkPossX int (offset 21, len 4 bytes)
- chunkPossY int (offset 25, len 4 bytes)
  - type block byte (offset 29, len 1 byte)
  - block innerPoss (offset 30, len 1 byte)
  - ...
- ...

# version 2
## Header
- "RealmTech" (offset 0, len 9 bytes)
- Version protocole file save : int (offset 9, len 4 bytes)
- save date long (offset 13, len 8 bytes)
- world with int (offset 21, len 4 bytes)
- world high int (offset 25, len 4 bytes)

### corps
C'est ici que contient tous les chunks, tous les blocks.
Les exemples d'offset sont pour les premiers du fichier.

- chunkPossX int (offset 29, len 4 bytes)
- chunkPossY int (offset 31, len 4 bytes)
  - type block byte (offset 35, len 1 byte)
  - block innerPoss (offset 36, len 1 byte)
  - ...
- ...

# version 3

Le layer d'une cellule représente sa position dans la hauteur. C'est la
cellule avec le plus grand layer qui sera affiché. Le layer ne peut
pas être négatif. 
0. ground 
1. ground deco 
2. build 
3. build deco

## Header
- "RealmTech" (offset 0, len 9 bytes)
- Version protocole file save : int (offset 9, len 4 bytes)
- save date long (offset 13, len 8 bytes)
- world with int (offset 21, len 4 bytes)
- world high int (offset 25, len 4 bytes)
- number layer byte (offset 29, len 1 byte)
- seed long (offset 30, len 8)
- player position x float (offset 38, len 4)
- player position y float (offset 39, len 4)

### corps

- chunkPossX int (offset 46, len 4 bytes)
- chunkPossY int (offset 50, len 4 bytes)
  - type cell byte (offset 54, len 1 byte)
  - block innerPoss byte (offset 55, len 1 byte)
  - layer byte (offset 56, len 1 byte)
  - ...
- ...

# version 4

Le layer d'une cellule représente sa position dans la hauteur. C'est la
cellule avec le plus grand layer qui sera affiché. Le layer ne peut
pas être négatif.
0. ground
1. ground deco
2. build
3. build deco

## Header
- "RealmTech" (offset 0, len 9 bytes)
- Version protocole file save : int (offset 9, len 4 bytes)
- save date long (offset 13, len 8 bytes)
- world with int (offset 21, len 4 bytes)
- world high int (offset 25, len 4 bytes)
- number layer byte (offset 29, len 1 byte)
- seed long (offset 30, len 8)
- player position x float (offset 38, len 4)
- player position y float (offset 39, len 4)

### corps

- chunkPossX int (offset 46, len 4 bytes)
- chunkPossY int (offset 50, len 4 bytes)
  - nomDeCells short (offset 54, len 2 bytes)
  - type cell byte (offset 56, len 1 byte)
  - block innerPoss byte (offset 57, len 1 byte)
  - layer byte (offset 58, len 1 byte)
  - ...
- ...