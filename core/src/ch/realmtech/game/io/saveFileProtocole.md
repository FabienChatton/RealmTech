# format : .rts (RealmTrechFileSave)

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