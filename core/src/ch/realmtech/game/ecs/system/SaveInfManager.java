package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfChunkComponent;
import ch.realmtech.game.ecs.component.InfMapComponent;
import ch.realmtech.game.ecs.component.InfMetaDonneesComponent;
import ch.realmtech.game.level.cell.Cells;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveInfManager extends Manager {
    private final static int SAVE_PROTOCOLE_VERSION = 7;
    private final static String ROOT_PATH = "RealmTechData";
    private final static String ROOT_PATH_SAVES = "saves";
    private final static String TAG = SaveInfManager.class.getSimpleName();
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;

    public void saveInfMap(int mapId) throws IOException {
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(infMapComponent.infMetaDonnees);
        saveInfMap(mapId, infMetaDonneesComponent.saveName);
    }

    public void saveInfMap(int mapId, Path saveName) throws IOException {
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        if (infMapComponent != null) {
            saveInfHeaderFile(infMapComponent.infMetaDonnees, saveName);
            for (int infChunkId : infMapComponent.infChunks) {
                saveInfChunk(infChunkId, saveName);
            }
        }
    }

    /**
     *
     * @param saveName nom de la nouvelle sauvegarde
     * @return l'id un infMap
     * @throws IOException
     */
    public int generateNewSave(String saveName) throws IOException{
        creerHiearchieDUneSave(saveName);
        int mapId = world.create();
        int metaDonneesId = world.create();
        InfMapComponent infMapComponent = world.edit(mapId).create(InfMapComponent.class);
        InfMetaDonneesComponent infMetaDonneesComponent = world.edit(metaDonneesId).create(InfMetaDonneesComponent.class);
        infMetaDonneesComponent.set(MathUtils.random(Long.MIN_VALUE, Long.MAX_VALUE - 1), 0, 0, saveName, world);
        infMapComponent.infMetaDonnees = metaDonneesId;
        return mapId;
    }

    public void saveInfHeaderFile(int infMetaDonneesId, Path rootSaveDirPath) throws IOException {
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(infMetaDonneesId);
        File metaDonneesFile = getMetaDonneesFile(rootSaveDirPath, true);
        metaDonneesFile.createNewFile();

        try (final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(metaDonneesFile)))) {
            outputStream.write("RealmTech".getBytes());
            outputStream.write(rootSaveDirPath.getFileName().toString().getBytes().length);
            outputStream.write(rootSaveDirPath.getFileName().toString().getBytes());
            outputStream.writeInt(SAVE_PROTOCOLE_VERSION);
            outputStream.writeLong(System.currentTimeMillis());
            outputStream.writeLong(infMetaDonneesComponent.seed);
            outputStream.writeFloat(infMetaDonneesComponent.playerPositionX);
            outputStream.writeFloat(infMetaDonneesComponent.playerPositionY);
            outputStream.flush();
        }
    }

    public void saveInfChunk(int infChunkId, Path rootSaveDirPath) throws IOException {
        InfChunkComponent infChunkComponent = mChunk.get(infChunkId);
        File chunksFile = getChunkFile(infChunkComponent.chunkPosX, infChunkComponent.chunkPosY, rootSaveDirPath);
        if (!chunksFile.exists()) {
            Files.createDirectories(chunksFile.toPath().getParent());
        }

        try (final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(chunksFile)))) {
            // Métadonnées
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(SAVE_PROTOCOLE_VERSION).array());
            // Header
            outputStream.writeShort(infChunkComponent.infCellsId.length); // nombre de cellules dans ce chunk
            // body
            for (int i = 0; i < infChunkComponent.infCellsId.length; i++) {
                InfCellComponent infCellComponent = mCell.get(infChunkComponent.infCellsId[i]);
                if (infCellComponent != null) {
                    outputStream.write((byte) CellRegisterEntry.getHash(infCellComponent.cellRegisterEntry));
                    outputStream.write(Cells.getInnerChunkPos(infCellComponent.innerPosX, infCellComponent.innerPosY));
                }
            }
            outputStream.flush();
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
        infMapComponent.set(new int[0], infMetaDonneesId);
        return worldId;
    }

    public int readInfMetaDonnees(Path rootSaveDirPath) throws IOException {
        File metaDonneesFile = getMetaDonneesFile(rootSaveDirPath, false);
        try (final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(metaDonneesFile)))) {
            ByteBuffer buffer = ByteBuffer.wrap(inputStream.readAllBytes());
            byte[] realmTechMagic = new byte[9];
            buffer.get(realmTechMagic, 0, realmTechMagic.length);
            byte[] bytesExpected = "RealmTech".getBytes();
            if (!Arrays.equals(realmTechMagic, bytesExpected)) {
                throw new IOException("ce n'est pas un fichier header de RealmTech");
            }
            byte tailleSaveName = buffer.get();
            byte[] saveNameChar = new byte[tailleSaveName];
            for (int i = 0; i < saveNameChar.length; i++) {
                saveNameChar[i] = buffer.get();
            }
            String saveName = new String(saveNameChar);
            int version = buffer.getInt();
            if (version != SAVE_PROTOCOLE_VERSION) {
                throw new IOException("Ce n'est pas la bonne version. Fichier : " + version + " jeu : " + SAVE_PROTOCOLE_VERSION);
            }
            long dateSauvegarde = buffer.getLong();
            long seed = buffer.getLong();
            float playerPosX = buffer.getFloat();
            float playerPosY = buffer.getFloat();
            int metaDonnesId = world.create();
            world.edit(metaDonnesId).create(InfMetaDonneesComponent.class).set(seed, playerPosX, playerPosY, saveName, world);
            return metaDonnesId;
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
            short nombreDeCellule = inputWrap.getShort();

            // Body
            int[] cellulesId = new int[nombreDeCellule];
            int chunkId = world.create();
            world.edit(chunkId).create(InfChunkComponent.class).set(chunkPosX, chunkPosY, cellulesId);
            for (int i = 0; i < cellulesId.length; i++) {
                cellulesId[i] = world.create();
                byte hashRegistry = inputWrap.get();
                byte pos = inputWrap.get();
                byte posX = Cells.getInnerChunkPosX(pos);
                byte posY = Cells.getInnerChunkPosY(pos);
                world.edit(cellulesId[i]).create(InfCellComponent.class).set(posX, posY, CellRegisterEntry.getCellModAndCellHash(hashRegistry));
            }
            return chunkId;
        }
    }

    public static List<File> listSauvegardeInfinie() throws IOException {
        creerHiearchieRealmTechData();
        File rootFile = Gdx.files.local(String.format("%s/%s", ROOT_PATH, ROOT_PATH_SAVES)).file();
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
        return new File(getLocalPathSaveRoot().toFile() + "/" + rootSaveDirPath + "/" + headerPath);
    }

    private static File getChunkFile(int chunkPosX, int chunkPosY, Path rootSaveDirPath) throws IOException {
        verifiePathLevelExiste(rootSaveDirPath);
        verifiePathChunkExiste(rootSaveDirPath);
        String chunkFileName = String.format("%s,%s", chunkPosX, chunkPosY);
        return new File(getLocalPathSaveRoot().toFile() + "/" + rootSaveDirPath + "/" + String.format("level/chunks/%s.rcs", chunkFileName));
    }

    private static void verifiePathLevelExiste(Path rootSaveDirPath) throws IOException {
        Path levelPath = Path.of(getLocalPathSaveRoot() + "/" + rootSaveDirPath.toString() + "/level");
        if (!Files.exists(levelPath)) {
            Files.createDirectory(levelPath);
        }
    }

    private static void verifiePathChunkExiste(Path rootSaveDirPath) throws IOException {
        Path levelChunkPath = Path.of(getLocalPathSaveRoot().toFile() + "/" + rootSaveDirPath.toString() + "/level/chunks");
        if (!Files.exists(levelChunkPath)) {
            Files.createDirectory(levelChunkPath);
        }
    }
    private static void creerHiearchieRealmTechData() throws IOException {
        File root = Gdx.files.local(ROOT_PATH).file();
        if (!root.exists()) {
            Files.createDirectories(root.toPath());
        }
        File rootSave = Gdx.files.local(String.format("%s/%s", ROOT_PATH, ROOT_PATH_SAVES)).file();
        if (!rootSave.exists()) {
            Files.createDirectories(rootSave.toPath());
        }
    }

    private static void creerHiearchieDUneSave(String saveName) throws IOException {
        Path savePath = getSavePath(saveName);
        creerHiearchieLevel(savePath);
        creerHiearchieChunk(savePath);
    }

    private static void creerHiearchieLevel(Path pathSave) throws IOException {
        Path pathLevel = Path.of(pathSave.toFile().toString(), "level");
        if (pathLevel.toFile().exists()) {
            Files.createDirectories(pathLevel);
        }
    }

    private static Path getSavePath(String saveName) throws IOException{
        return Path.of(getLocalPathSaveRoot().toFile().toString(), saveName);
    }

    private static Path getLevelPath(Path savePath) throws IOException {
        return Path.of(savePath.toFile().toString(), "level");
    }

    private static void creerHiearchieChunk(Path savePath) throws IOException {
        Path chunkPath = Path.of(getLevelPath(savePath).toFile().toString(), "chunks");
        if (!chunkPath.toFile().exists()) {
            Files.createDirectories(chunkPath);
        }
    }

    private static Path getLocalPathSaveRoot() throws IOException {
        creerHiearchieRealmTechData();
        return Gdx.files.local(String.format("%s/%s", ROOT_PATH, ROOT_PATH_SAVES)).file().toPath();
    }
}
