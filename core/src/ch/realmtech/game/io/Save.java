package ch.realmtech.game.io;

import ch.realmtech.RealmTech;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.level.map.RealmTechTiledMap;
import com.badlogic.gdx.files.FileHandle;

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

    public Save(RealmTechTiledMap map, FileHandle fileHandler, RealmTech context) throws IOException {
        this.map = map;
        this.file = fileHandler.file();
        this.context = context;
    }

    public void loadMap() throws IOException {
        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        byte[] rawFile = inputStream.readAllBytes();
        String magicGameName = new String(rawFile,0,9);
        int version = ByteBuffer.wrap(rawFile, 9,Integer.BYTES).getInt();
        if (version == 1) loadMapV1(rawFile);
        inputStream.close();
    }

    private void loadMapV1(byte[] rawFile) {
        Date date = new Date(ByteBuffer.wrap(rawFile, 13, Long.BYTES).getLong());
        int i = 21;
        while (i < rawFile.length) {
            int chunkPossX = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
            i += Integer.BYTES;
            int chunkPossY = ByteBuffer.wrap(rawFile, i, Integer.BYTES).getInt();
            i += Integer.BYTES;
            GameChunk gameChunk = new GameChunk(map, context, chunkPossX, chunkPossY);
            map.setGameChunk(chunkPossX, chunkPossY, gameChunk);
            for (int x = 0; x < GameChunk.CHUNK_SIZE; x++) {
                for (int y = 0; y < GameChunk.CHUNK_SIZE; y++) {
                    CellType cellType = CellType.getCellTypeByID(ByteBuffer.wrap(rawFile, i, Byte.BYTES).get());
                    i += Byte.BYTES;
                    byte innerPoss = ByteBuffer.wrap(rawFile, i, Byte.BYTES).get();
                    i += Byte.BYTES;
                    if (cellType != null) {
                        GameCell gameCell = new GameCell(gameChunk, innerPoss, cellType);
                        gameCell.placeCellOnMap(0);
                    } else {
                        gameChunk.setCell(GameCell.getInnerChunkPossX(innerPoss), GameCell.getInnerChunkPossY(innerPoss),null);
                    }
                }
            }
        }
    }

    public void save() throws IOException {
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

    public void write (int aInt) throws IOException{
        outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(aInt).array());
    }

    public void write(byte aByte) throws IOException {
        outputStream.write(ByteBuffer.allocate(Byte.BYTES).put(aByte).array());
    }

    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
