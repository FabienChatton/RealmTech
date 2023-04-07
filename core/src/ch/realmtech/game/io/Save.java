package ch.realmtech.game.io;

import ch.realmtech.RealmTech;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.cell.GameCellFactory;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.level.chunk.GameChunkFactory;
import ch.realmtech.game.level.map.RealmTechTiledMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class Save implements Closeable, Flushable{
    private final RealmTechTiledMap map;
    private final RealmTech context;
    private final File file;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public Save(File file, RealmTech context) {
        map = new RealmTechTiledMap(this);
        this.file = file;
        this.context = context;
    }

    public void genereteNewMap() {
        map.creerMapAleatoire();
    }

    public void loadMap() throws IOException {
        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        byte[] rawFile = inputStream.readAllBytes();
        String magicGameName = new String(rawFile,0,9);
        int version = ByteBuffer.wrap(rawFile, 9,Integer.BYTES).getInt();
        switch (version) {
            case 2 -> loadMapV2(rawFile);
            default -> loadLast(rawFile);
        }
        inputStream.close();
    }


    private void loadMapV2(byte[] rawFile) {
        Date date = new Date(ByteBuffer.wrap(rawFile, 13, Long.BYTES).getLong());
        int i = 21;
        int worldWith = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
        i += Integer.BYTES;
        int worldHigh = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
        i += Integer.BYTES;
        GameChunk[][] chunks = new GameChunk[worldWith / GameChunk.CHUNK_SIZE][worldHigh / GameChunk.CHUNK_SIZE];
        while (i < rawFile.length) {
            int chunkPossX = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
            i += Integer.BYTES;
            int chunkPossY = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
            i += Integer.BYTES;
            chunks[chunkPossX][chunkPossY] = GameChunkFactory.createEmptyChunk(map, chunkPossX, chunkPossY);
            GameChunk chunk = chunks[chunkPossX][chunkPossY];
            GameCell[][] gameCells = new GameCell[GameChunk.CHUNK_SIZE][GameChunk.CHUNK_SIZE];
            for (byte x = 0; x < GameChunk.CHUNK_SIZE; x++) {
                for (byte y = 0; y < GameChunk.CHUNK_SIZE; y++) {
                    CellType cellType = CellType.getCellTypeByID(ByteBuffer.wrap(rawFile, i, Byte.BYTES).get());
                    i += Byte.BYTES;
                    byte innerPoss = ByteBuffer.wrap(rawFile, i, Byte.BYTES).get();
                    i += Byte.BYTES;
                    gameCells[x][y] = GameCellFactory.createByteType(chunk,innerPoss,cellType);
                }
            }
            chunk.setCells(gameCells);
        }
        map.initAndPlace(worldWith, worldHigh, 32, 32, chunks);
    }

    private void loadLast(byte[] rawFile) {
        loadMapV2(rawFile);
    }

    @Deprecated
    public void saveV1() throws IOException {
        if (file.exists()) {
            Files.newBufferedWriter(file.toPath(), StandardOpenOption.TRUNCATE_EXISTING).close();
        }
        outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        outputStream.write("RealmTech".getBytes());
        outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(1).array());
        outputStream.write(ByteBuffer.allocate(Long.BYTES).putLong(System.currentTimeMillis()).array());
        map.save(this);
        outputStream.flush();
        outputStream.close();
    }

    public void saveV2() throws IOException {
        final int version = 2;
        if (file.exists()) {
            Files.newBufferedWriter(file.toPath(), StandardOpenOption.TRUNCATE_EXISTING).close();
        }
        outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        outputStream.write("RealmTech".getBytes());
        outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(version).array());
        outputStream.write(ByteBuffer.allocate(Long.BYTES).putLong(System.currentTimeMillis()).array());
        outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(RealmTechTiledMap.WORLD_WITH).array());
        outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(RealmTechTiledMap.WORLD_HIGH).array());
        map.save(this);
        outputStream.flush();
        outputStream.close();
    }

    public void saveLast() throws IOException {
        saveV2();
    }

    public void write (int aInt) throws IOException{
        outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(aInt).array());
    }

    public void write(byte aByte) throws IOException {
        outputStream.write(ByteBuffer.allocate(Byte.BYTES).put(aByte).array());
    }

    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }

    public static Array<File> getTousLesSauvegarde() {
        Array<File> files = new Array<>();
        FileHandle[] list = Gdx.files.local("").list(pathname -> pathname.getName().matches(".*\\.rts"));
        for (FileHandle fileHandle : list) {
            files.add(fileHandle.file());
        }
        return files;
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    public RealmTech getContext() {
        return context;
    }

    public RealmTechTiledMap getMap() {
        return map;
    }
}
