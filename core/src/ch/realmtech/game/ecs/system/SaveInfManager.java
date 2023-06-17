package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.level.cell.Cells;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import ch.realmtech.game.level.worldGeneration.PerlineNoise2;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SaveInfManager extends Manager {
    private final static int SAVE_PROTOCOLE_VERSION = 6;
    private final static String TAG = SaveInfManager.class.getSimpleName();
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<InfLayerComponent> mLayer;
    public void saveInfMap(int mapId, Path rootSaveDirPath) throws IOException {
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        if (infMapComponent != null) {
            saveInfHeaderFile(infMapComponent.infMetaDonnees, rootSaveDirPath);
            for (int infChunkId : infMapComponent.infChunks) {
                saveInfChunk(infChunkId, rootSaveDirPath);
            }
        }
    }

    public void saveInfMap(int mapdId, String nameRootSaveDir) throws IOException {
        saveInfMap(mapdId, Gdx.files.internal(nameRootSaveDir).file().toPath());
    }

    /**
     *
     * @param name nom de la nouvelle sauvegarde
     * @return l'id un infMap
     * @throws IOException
     */
    public int generateNewSave(String name) throws IOException {
        File rootSaveDir = new File(Gdx.files.internal(".").file(), name);
        try {
            Files.createDirectory(rootSaveDir.toPath());
        } catch (FileAlreadyExistsException ignored) {

        }
        int mapId = world.create();
        int metaDonneesId = world.create();
        InfMapComponent infMapComponent = world.edit(mapId).create(InfMapComponent.class);
        InfMetaDonneesComponent infMetaDonneesComponent = world.edit(metaDonneesId).create(InfMetaDonneesComponent.class);
        infMetaDonneesComponent.seed = MathUtils.random(Long.MIN_VALUE, Long.MAX_VALUE - 1);
        infMetaDonneesComponent.perlinNoise = new PerlinNoise(new Random(infMetaDonneesComponent.seed), WorldMap.WORLD_WITH, WorldMap.WORLD_HIGH, new PerlineNoise2(7, 0.6f, 0.005f));;
        infMapComponent.infMetaDonnees = metaDonneesId;
        return mapId;
    }

    public void saveInfHeaderFile(int infMetaDonneesId, Path rootSaveDirPath) throws IOException {
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(infMetaDonneesId);
        File metaDonneesFile = getMetaDonneesFile(rootSaveDirPath, true);
        try {
            metaDonneesFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(metaDonneesFile)))) {
            outputStream.writeUTF("RealmTech");
            outputStream.writeInt(SAVE_PROTOCOLE_VERSION);
            outputStream.writeLong(System.currentTimeMillis());
            outputStream.writeLong(infMetaDonneesComponent.seed);
            outputStream.writeFloat(infMetaDonneesComponent.playerPositionX);
            outputStream.writeFloat(infMetaDonneesComponent.playerPositionY);
        } catch (FileNotFoundException e) {
            Gdx.app.error(TAG, "Le fichier header n'a pas été trouve", e);
        } catch (IOException e) {
            Gdx.app.error(TAG, "Une erreur concernant le fichier header inattendue est survenue" + metaDonneesFile.toPath(), e);
        }
    }

    public void saveInfChunk(int infChunkId, Path rootSaveDirPath) throws IOException {
        InfChunkComponent infChunkComponent = mChunk.get(infChunkId);
        File chunksFile = getChunkFile(infChunkComponent.chunkPossX, infChunkComponent.chunkPossY, rootSaveDirPath);
        if (!chunksFile.exists()) {
            try {
                Files.createDirectories(chunksFile.toPath().getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(chunksFile)))) {
            // Métadonnées
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(SAVE_PROTOCOLE_VERSION).array());
            // Header
            outputStream.write(infChunkComponent.infLayers.length); // nombre de layer
            for (int i = 0; i < infChunkComponent.infLayers.length; i++) { // pour chaque layer
                int infLayerId = infChunkComponent.infLayers[i];
                InfLayerComponent infLayerComponent = mLayer.get(infLayerId);
                outputStream.write(ByteBuffer.allocate(Short.BYTES).putShort((short) infLayerComponent.infCells.length).array()); // nombre de cellules pour de ce layer
            }
            // body
            for (byte i = 0; i < infChunkComponent.infLayers.length; i++) {
                InfLayerComponent infLayerComponent = mLayer.get(infChunkComponent.infLayers[i]);
                for (short k = 0; k < infLayerComponent.infCells.length; k++) {
                    InfCellComponent infCellComponent = mCell.get(infLayerComponent.infCells[k]);
                    outputStream.writeLong(infCellComponent.hashCellRegistry);
                    outputStream.write(Cells.getInnerChunkPos(infCellComponent.posX, infCellComponent.posY));
                }
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            Gdx.app.error(TAG, "Le fichier du chunk n'a pas été trouve", e);
        } catch (IOException e) {
            Gdx.app.error(TAG, "Une erreur inattendue est survenue" + chunksFile.toPath(), e);
        }
    }

    /**
     * @return l'id du monde
     */
    public int readInfMap(Path rootSaveDirPath) throws IOException {
        int worldId = world.create();
        InfMapComponent infMapComponent = world.edit(worldId).create(InfMapComponent.class);
        int infMetaDonneesId = readInfMetaDonnees(rootSaveDirPath);
        InfMetaDonneesComponent metaDonneesComponent = mMetaDonnees.get(infMetaDonneesId);
        int chunkPosX = (int) (metaDonneesComponent.playerPositionX / WorldMap.CHUNK_SIZE);
        int chunkPosY = (int) (metaDonneesComponent.playerPositionY / WorldMap.CHUNK_SIZE);
        int chunkId = readSavedInfChunk(chunkPosX, chunkPosY, rootSaveDirPath);
        infMapComponent.set(new int[]{chunkId}, infMetaDonneesId);
        return worldId;
    }

    public int readInfMetaDonnees(Path rootSaveDirPath) throws IOException {
        File metaDonneesFile = getMetaDonneesFile(rootSaveDirPath, false);
        try (final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(metaDonneesFile)))) {
            ByteBuffer buffer = ByteBuffer.wrap(inputStream.readAllBytes());
            byte[] realmTechMagic = new byte[9];
            buffer.get(9, realmTechMagic);
            if (!Arrays.equals(realmTechMagic, "RealmTech".getBytes())) {
                throw new IOException("ce n'est pas un fichier header de RealmTech");
            }
            int version = buffer.getInt();
            if (version != SAVE_PROTOCOLE_VERSION) {
                throw new IOException("Ce n'est pas la bonne version. Fichier : " + version + " jeu : " + SAVE_PROTOCOLE_VERSION);
            }
            long dateSauvegarde = buffer.getLong();
            long seed = buffer.getLong();
            float playerPosX = buffer.getFloat();
            float playerPosY = buffer.getFloat();
            int metaDonnesId = world.create();
            world.edit(metaDonnesId).create(InfMetaDonneesComponent.class).set(seed, playerPosX, playerPosY);
            return metaDonnesId;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param chunkPosX
     * @param chunkPosY
     * @param rootSaveDirPath
     * @return donne l'id du nouveau chunk
     */
    public int readSavedInfChunk(int chunkPosX, int chunkPosY, Path rootSaveDirPath) throws IOException {
        File chunksFile = getChunkFile(chunkPosX, chunkPosY, rootSaveDirPath);
        try (final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(chunksFile)))) {
            // Métadonnées
            ByteBuffer inputWrap = ByteBuffer.wrap(inputStream.readAllBytes());
            int versionProtocole = inputWrap.getInt();
            if (versionProtocole != SAVE_PROTOCOLE_VERSION) {
                Gdx.app.error(TAG, "La version de la sauvegarde ne correspond pas. Fichier :" + versionProtocole + ". Jeu :" + SAVE_PROTOCOLE_VERSION);
            }
            // Header
            byte nombreDeLayer = inputWrap.get();
            short[] nombreDeCelluleParLayer = new short[nombreDeLayer];
            for (int i = 0; i < nombreDeLayer; i++) {
                nombreDeCelluleParLayer[i] = inputWrap.getShort();
            }
            // Body
            int[] infLayerId = new int[nombreDeLayer];
            int chunkId = world.create();
            world.edit(chunkId).create(InfChunkComponent.class).set(chunkPosX, chunkPosY, infLayerId);
            for (byte i = 0; i < infLayerId.length; i++) {
                infLayerId[i] = world.create();
                int[] infCellsId = new int[nombreDeCelluleParLayer[i]];
                world.edit(infLayerId[i]).create(InfLayerComponent.class).set(i,infCellsId);
                for (byte j = 0; j < infCellsId.length; j++) {
                    int hashRegistry = inputWrap.getInt();
                    byte pos = inputWrap.get();
                    byte posX = Cells.getInnerChunkPosX(pos);
                    byte posY = Cells.getInnerChunkPosY(pos);
                    infCellsId[j] = world.create();
                    world.edit(infCellsId[j]).create(InfCellComponent.class).set(posX, posY, hashRegistry);
                }
            }
            return chunkId;
        } catch (FileNotFoundException e) {
            Gdx.app.error(TAG, "Le fichier n'a pas été trouve", e);
        } catch (IOException e) {
            Gdx.app.error(TAG, "Une erreur inattendue est survenue", e);
        }
        return -1;
    }

    public static List<File> listSauvegardeInfinie() throws IOException {
        File rootFile = Gdx.files.internal(".").file();
        List<File> ret = new ArrayList<>();
        for (File file : rootFile.listFiles()) {
            if (getMetaDonneesFile(file.toPath(), false).exists()) {
                ret.add(file);
            }
        }
        return ret;
    }

    private static File getMetaDonneesFile(Path rootSaveDirPath, boolean creerSiInexistant) throws IOException {
        if (creerSiInexistant) verifiePathLevelExiste(rootSaveDirPath);
        String headerPath = ("level/header.rsh");
        return new File(rootSaveDirPath + "/" + headerPath);
    }

    private static File getChunkFile(int chunkPossX, int chunkPossY, Path rootSaveDirPath) throws IOException {
        verifiePathLevelExiste(rootSaveDirPath);
        verifiePathChunkExiste(rootSaveDirPath);
        String chunkFileName = String.format("%s-%s", chunkPossX, chunkPossY);
        return new File(rootSaveDirPath.toString() + "/" + String.format("level/chunks/%s.rcs", chunkFileName));
    }

    private static void verifiePathLevelExiste(Path rootSaveDirPath) throws IOException {
        Path levelPath = Path.of(rootSaveDirPath.toString() + "/level");
        if (!Files.exists(levelPath)) {
            Files.createDirectory(levelPath);
        }
    }

    private static void verifiePathChunkExiste(Path rootSaveDirPath) throws IOException {
        Path levelChunkPath = Path.of(rootSaveDirPath.toString() + "/level/chunks");
        if (!Files.exists(levelChunkPath)) {
            Files.createDirectory(levelChunkPath);
        }
    }
}
