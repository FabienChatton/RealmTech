package ch.realmtech.game.level.save;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class SaveInfWrkTest {

    @Test
    void saveInfChunk() throws IOException {
        SaveInfWrk saveInfWrk = new SaveInfWrk();

        SaveInfWrk.InfCell[] infCell = new SaveInfWrk.InfCell[1];
        infCell[0] = new SaveInfWrk.InfCell((byte) 0, (byte) 0, (byte) 0);
        SaveInfWrk.InfLayer[] infLayer = new SaveInfWrk.InfLayer[1];
        infLayer[0] = new SaveInfWrk.InfLayer((byte) 0, infCell);
        SaveInfWrk.InfChunk infChunk = new SaveInfWrk.InfChunk( 0, 0, infLayer);
        saveInfWrk.saveInfChunk(infChunk, Files.createTempDirectory("testInf").toFile());
    }

    @Test
    void saveAndRead() throws IOException {
        SaveInfWrk saveInfWrk = new SaveInfWrk();

        SaveInfWrk.InfCell[] infCell = new SaveInfWrk.InfCell[1];
        infCell[0] = new SaveInfWrk.InfCell((byte) 0, (byte) 0, (byte) 0);
        SaveInfWrk.InfLayer[] infLayer = new SaveInfWrk.InfLayer[1];
        infLayer[0] = new SaveInfWrk.InfLayer((byte) 0, infCell);
        SaveInfWrk.InfChunk infChunk = new SaveInfWrk.InfChunk( 0, 0, infLayer);
        File testInf = Files.createTempDirectory("testInf").toFile();
        saveInfWrk.saveInfChunk(infChunk, testInf);


        SaveInfWrk.InfChunk savedInfChunk = saveInfWrk.readSavedInfChunk(0, 0, testInf);

        Assert.assertEquals(infChunk.infLayers()[0].infCells()[0], savedInfChunk.infLayers()[0].infCells()[0]);
    }
}