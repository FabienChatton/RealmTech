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