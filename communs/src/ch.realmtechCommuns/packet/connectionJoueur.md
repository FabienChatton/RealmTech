```mermaid
sequenceDiagram
    participant autreClient
    participant client
    participant serveur

    client ->>+ serveur: DemandDeConnectionJoueur
    serveur -->>- client: ConnectionJoueurRéussit(pos, uuid)
    client ->> client: createPlayerClient(pos)
    client ->> client: setScreen(gameScreen)
```