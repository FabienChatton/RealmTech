package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.options.DataCtrl;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.exception.IllegalMagicNumbers;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SaveInfManager extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(SaveInfManager.class);
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    @Wire
    private SerializerController serializerController;
    public final static int SAVE_PROTOCOLE_VERSION = 8;
    public final static String ROOT_PATH_SAVES = "saves";
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<SaveMetadataComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PositionComponent> mPosition;

    public void saveInfMap(int mapId) throws IOException {
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        SaveMetadataComponent saveMetadataComponent = mMetaDonnees.get(infMapComponent.infMetaDonnees);
        saveInfMap(mapId, saveMetadataComponent.saveName);
    }

    public void saveInfMap(int mapId, String saveName) throws IOException {
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        if (infMapComponent != null) {
            Path savePath = getSavePath(saveName);
            saveSaveMetadata(infMapComponent.infMetaDonnees, savePath);
            for (int infChunkId : infMapComponent.infChunks) {
                saveInfChunk(infChunkId, savePath);
            }
        }
    }

    /**
     * Prépare la sauvegarde.
     * Créer une sauvegarde si le nom de la sauvegarde n'existe pas ou charge la sauvegarde si elle existe.
     * @param saveName Le nom de la sauvegarde
     * @return l'id de la infMap
     * @throws IOException
     */
    public int generateOrLoadSave(String saveName) throws IOException {
        int mapId;
        List<File> listSauvegarde = listSauvegardeInfinie();
        // la sauvegarde existe déjà
        if (listSauvegarde.stream().map(File::getName).anyMatch(s -> s.equals(saveName))) {
            mapId = readInfMap(saveName);
        } else {
            mapId = generateNewSave(saveName);
        }
        return mapId;
    }

    /**
     *
     * @param saveName nom de la nouvelle sauvegarde
     * @return l'id un infMap
     * @throws IOException
     */
    public int generateNewSave(String saveName) throws IOException{
        if (saveName == null || saveName.isBlank()) throw new IllegalArgumentException("le nom de la sauvegarde ne peut pas être null ou vide");
        if (saveName.contains("/")) throw new IllegalArgumentException("le nom du la sauvegarde ne peut pas contenir de \"/\"");
        creerHiearchieDUneSave(saveName);
        int mapId = world.create();
        int metaDonneesId = createSaveMetadata(saveName);;
        InfMapComponent infMapComponent = world.edit(mapId).create(InfMapComponent.class);
        infMapComponent.set(new int[0], metaDonneesId);
        return mapId;
    }

    public void saveSaveMetadata(int infMetaDonneesId, Path rootSaveDirPath) throws IOException {
        SaveMetadataComponent saveMetadataComponent = mMetaDonnees.get(infMetaDonneesId);
        //PositionComponent positionComponentPlayer = world.getSystem(TagManager.class).getEntity(PlayerComponent.TAG).getComponent(PositionComponent.class);
        File metaDonneesFile = getMetaDonneesFile(rootSaveDirPath);
        metaDonneesFile.createNewFile();

        try (final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(metaDonneesFile)))) {
            SerializedApplicationBytes encodeSaveMetadata = serializerController.getSaveMetadataSerializerController().encode(saveMetadataComponent);
            outputStream.write(encodeSaveMetadata.applicationBytes());
            outputStream.flush();
        }
    }

    public void saveInfChunk(int infChunkId, Path rootSaveDirPath) throws IOException {
        InfChunkComponent infChunkComponent = mChunk.get(infChunkId);
        File chunksFile = getChunkFile(infChunkComponent.chunkPosX, infChunkComponent.chunkPosY, rootSaveDirPath);
        if (!chunksFile.exists()) {
            Files.createDirectories(chunksFile.toPath().getParent());
        }

        try (final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(chunksFile))))) {
            SerializedApplicationBytes encodeChunk = serializerController.getChunkSerializerController().encode(infChunkComponent);
            outputStream.write(encodeChunk.applicationBytes());
            outputStream.flush();
        }
    }

    /**
     * @return l'id du monde
     */
    public int readInfMap(String saveName) throws IOException {
        int worldId = world.create();
        Path rootSaveDirPath = Path.of(DataCtrl.ROOT_PATH + "/" + ROOT_PATH_SAVES + "/" + saveName);
        logger.info("Lecture de la carte \"{}\"", rootSaveDirPath.toAbsolutePath());
        InfMapComponent infMapComponent = world.edit(worldId).create(InfMapComponent.class);
        int infMetaDonneesId = readInfMetaDonnees(rootSaveDirPath, saveName);
        SaveMetadataComponent metaDonneesComponent = mMetaDonnees.get(infMetaDonneesId);
        infMapComponent.set(new int[0], infMetaDonneesId);
        return worldId;
    }

    public int readInfMetaDonnees(Path rootSaveDirPath, String saveName) throws IOException {
        File metaDonneesFile = getMetaDonneesFile(rootSaveDirPath);
        try (final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(metaDonneesFile)))) {
            return serializerController.getSaveMetadataSerializerController().decode(new SerializedApplicationBytes(inputStream.readAllBytes()));
        } catch (IllegalMagicNumbers e) {
            logger.error("The saveMetadata file was not correctly read. Recreating a new one");
            return createSaveMetadata(saveName);
        }
    }

    private int createSaveMetadata(String saveName) {
        int saveMetadataId = world.create();
        SaveMetadataComponent saveMetadataComponent = world.edit(saveMetadataId).create(SaveMetadataComponent.class);
        saveMetadataComponent.set(MathUtils.random(Long.MIN_VALUE, Long.MAX_VALUE - 1), saveName, world);
        return saveMetadataId;
    }

    public int readSavedInfChunk(int chunkPosX, int chunkPosY, String saveName) throws IOException {
        File chunksFile = getChunkFile(chunkPosX, chunkPosY, getSavePath(saveName));
        byte[] chunkBytes;
        // test with gzip format first
        try (final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(chunksFile))))) {
            chunkBytes = inputStream.readAllBytes();
        }
        return serializerController.getChunkSerializerController().decode(new SerializedApplicationBytes(chunkBytes));
    }

    public static List<File> listSauvegardeInfinie() throws IOException {
        DataCtrl.creerHiearchieRealmTechData();
        File rootFile = getLocalPathSaveRoot().toFile();
        List<File> ret = new ArrayList<>();
        for (File file : rootFile.listFiles()) {
            if (getMetaDonneesFile(file.toPath()).exists()) {
                ret.add(file);
            }
        }
        return ret;
    }

    public void savePlayerInventory(InventoryComponent playerInventory, int mapId) throws IOException {
        File playerSaveInventoryFile = getPlayerSaveInventoryFile(mMetaDonnees.get(mInfMap.get(mapId).infMetaDonnees).saveName);
        try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(playerSaveInventoryFile)))) {
            // métadonnées
            outputStream.writeInt(SAVE_PROTOCOLE_VERSION);
            // header
            outputStream.writeByte((byte) Arrays.stream(playerInventory.inventory).filter(stack -> stack[0] != 0).count());
            // body
            for (int i = 0; i < playerInventory.inventory.length; i++) {
                if (playerInventory.inventory[i][0] != 0) {
                    int[] stack = playerInventory.inventory[i];
                    outputStream.writeInt(mItem.get(stack[0]).itemRegisterEntry.getHash());
                    outputStream.writeByte(InventoryManager.tailleStack(stack));
                    outputStream.writeByte(i);
                }
            }
        }
    }

//    public int[][] getPlayerSaveInventory(int mapId) throws IOException {
//        int[][] inventory = new int[InventoryComponent.DEFAULT_NUMBER_OF_ROW * InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW][InventoryComponent.DEFAULT_STACK_LIMITE];
//        File playerSaveInventoryFile = getPlayerSaveInventoryFile(mMetaDonnees.get(mInfMap.get(mapId).infMetaDonnees).saveName);
//        try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(playerSaveInventoryFile)))) {
//            ByteBuffer byteBuffer = ByteBuffer.wrap(inputStream.readAllBytes());
//            int version = byteBuffer.getInt();
//            byte nombreSlot = byteBuffer.get();
//            for (int i = 0; i < nombreSlot; i++) {
//                int hashModIdItem = byteBuffer.getInt();
//                byte nombre = byteBuffer.get();
//                byte index = byteBuffer.get();
//                ItemRegisterEntry itemRegisterEntry = ItemRegisterEntry.getItemByHash(hashModIdItem);
//                int[] stack = inventory[index];
//                for (int j = 0; j < nombre; j++) {
//                    int nouvelItemId = systemsAdminCommun.itemManagerServer.newItemInventory(itemRegisterEntry);
//                    stack[j] = nouvelItemId;
//                }
//            }
//        }
//
//        return inventory;
//    }

    private static File getMetaDonneesFile(Path rootSaveDirPath) {
        String headerPath = ("level/header.rsh");
        return Path.of(rootSaveDirPath.toFile().toString(), headerPath).toFile();
    }

    private static File getChunkFile(int chunkPosX, int chunkPosY, Path rootSaveDirPath) throws IOException {
        String chunkFileName = String.format("%s,%s", chunkPosX, chunkPosY);
        return new File(rootSaveDirPath + "/" + String.format("level/chunks/%s.rcs", chunkFileName));
    }

    private static void creerHiearchieDUneSave(String saveName) throws IOException {
        Path savePath = getSavePath(saveName);
        logger.info("création de la sauvegarde \"{}\"", savePath.toAbsolutePath());
        creerHiearchieLevel(savePath);
        creerHiearchieChunk(savePath);
    }

    private static void creerHiearchieLevel(Path pathSave) throws IOException {
        Path pathLevel = Path.of(pathSave.toFile().toString(), "level");
        if (pathLevel.toFile().exists()) {
            Files.createDirectories(pathLevel);
        }
    }

    public static Path getSavePath(String saveName) throws IOException{
        return new File(getLocalPathSaveRoot().toFile(), saveName).toPath();
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
        DataCtrl.creerHiearchieRealmTechData();
        return Path.of(String.format("%s/%s", DataCtrl.ROOT_PATH, ROOT_PATH_SAVES));
    }

    private static File getPlayerSaveInventoryFile(String saveName) throws IOException {
        DataCtrl.creerHiearchieRealmTechData();
        // psi -> playerSaveInventory
        return Path.of(getSavePath(saveName).toFile().toString(), "playerInventory.pis").toFile();
    }
}
