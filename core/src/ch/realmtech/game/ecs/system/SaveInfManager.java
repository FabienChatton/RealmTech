package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.level.cell.Cells;
import ch.realmtech.game.registery.CellRegisterEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.options.RealmTechDataCtrl;
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
    private final static int SAVE_PROTOCOLE_VERSION = 8;
    public final static String ROOT_PATH_SAVES = "saves";
    private final static String TAG = SaveInfManager.class.getSimpleName();
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<ItemComponent> mItem;

    public void saveInfMap(int mapId) throws IOException {
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(infMapComponent.infMetaDonnees);
        saveInfMap(mapId, infMetaDonneesComponent.saveName);
    }

    public void saveInfMap(int mapId, String saveName) throws IOException {
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        if (infMapComponent != null) {
            Path savePath = getSavePath(saveName);
            saveInfHeaderFile(infMapComponent.infMetaDonnees, savePath);
            creerHiearchieDUneSave(saveName);
            for (int infChunkId : infMapComponent.infChunks) {
                saveInfChunk(infChunkId, savePath);
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
        if (saveName == null || saveName.isBlank()) throw new IllegalArgumentException("le nom de la sauvegarde ne peut pas être null ou vide");
        if (saveName.contains("/")) throw new IllegalArgumentException("le nom du la sauvegarde ne peut pas contenir de \"/\"");
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
        File metaDonneesFile = getMetaDonneesFile(rootSaveDirPath);
        metaDonneesFile.createNewFile();

        try (final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(metaDonneesFile)))) {
            outputStream.write("RealmTech".getBytes());
            outputStream.write(infMetaDonneesComponent.saveName.getBytes().length);
            outputStream.write(infMetaDonneesComponent.saveName.getBytes());
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
                    outputStream.writeInt(CellRegisterEntry.getHash(infCellComponent.cellRegisterEntry));
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
        File metaDonneesFile = getMetaDonneesFile(rootSaveDirPath);
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
     * @param saveName
     * @return donne l'id du nouveau chunk
     */
    public int readSavedInfChunk(int chunkPosX, int chunkPosY, String saveName) throws IOException {
        File chunksFile = getChunkFile(chunkPosX, chunkPosY, getSavePath(saveName));
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
                final int cellId = world.create();
                cellulesId[i] = cellId;
                int hashRegistry = inputWrap.getInt();
                byte pos = inputWrap.get();
                byte posX = Cells.getInnerChunkPosX(pos);
                byte posY = Cells.getInnerChunkPosY(pos);
                final CellRegisterEntry cellRegisterEntry = CellRegisterEntry.getCellModAndCellHash(hashRegistry);
                world.edit(cellulesId[i]).create(InfCellComponent.class).set(posX, posY, cellRegisterEntry);
                if (cellRegisterEntry.getEditEntity() != null)
                    cellRegisterEntry.getEditEntity().accept(world, cellId);
            }
            return chunkId;
        }
    }

    public static List<File> listSauvegardeInfinie() throws IOException {
        RealmTechDataCtrl.creerHiearchieRealmTechData();
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

    public int[][] getPlayerSaveInventory(int mapId) throws IOException {
        int[][] inventory = new int[InventoryComponent.DEFAULT_NUMBER_OF_ROW * InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW][InventoryComponent.DEFAULT_STACK_LIMITE];
        File playerSaveInventoryFile = getPlayerSaveInventoryFile(mMetaDonnees.get(mInfMap.get(mapId).infMetaDonnees).saveName);
        try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(playerSaveInventoryFile)))) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(inputStream.readAllBytes());
            int version = byteBuffer.getInt();
            byte nombreSlot = byteBuffer.get();
            for (int i = 0; i < nombreSlot; i++) {
                int hashModIdItem = byteBuffer.getInt();
                byte nombre = byteBuffer.get();
                byte index = byteBuffer.get();
                ItemRegisterEntry itemRegisterEntry = ItemRegisterEntry.getItemByHash(hashModIdItem);
                int[] stack = inventory[index];
                for (int j = 0; j < nombre; j++) {
                    int nouvelItemId = world.getSystem(ItemManager.class).newItemInventory(itemRegisterEntry);
                    stack[j] = nouvelItemId;
                }
            }
        }

        return inventory;
    }

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
        RealmTechDataCtrl.creerHiearchieRealmTechData();
        return Gdx.files.local(String.format("%s/%s", RealmTechDataCtrl.ROOT_PATH, ROOT_PATH_SAVES)).file().toPath();
    }

    private static File getPlayerSaveInventoryFile(String saveName) throws IOException {
        RealmTechDataCtrl.creerHiearchieRealmTechData();
        // psi -> playerSaveInventory
        return Path.of(getSavePath(saveName).toFile().toString(), "playerInventory.pis").toFile();
    }
}
