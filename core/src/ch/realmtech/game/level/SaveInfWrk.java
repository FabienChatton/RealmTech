package ch.realmtech.game.level;

import ch.realmtech.game.Cells;
import com.badlogic.gdx.Gdx;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class SaveInfWrk {
    private final static String TAG = SaveInfWrk.class.getSimpleName();
    public final static int SAVE_PROTOCOLE_VERSION = 6;
//    public void saveMapInf(String saveName) throws IOException {
//        File saveDir = Gdx.files.local(saveName).file();
//        if (saveDir.exists()) {
//            if (!saveDir.isDirectory()) {
//                throw new NotDirectoryException("Le dossier racine " + saveDir.toPath() + " de la sauvegarde n'est pas un dossier");
//            }
//            Gdx.app.debug(TAG, "Un dossier " + saveDir + " au même nom existe déjà. Commencement de la suppression du dossier");
//
//        }
//        Files.createDirectory(Gdx.files.local(saveName).file().toPath());
//    }

    public record InfChunk(int chunkPossX, int chunkPossY, InfLayer[] infLayers) {

    }

    /**
     * @param layer Le niveau du layer.
     *<ol start="0">
     *  <li>ground</li>
     *  <li>ground deco</li>
     *  <li>build</li>
     *  <li>build deco</li>
     *</ol>
     * @param infCells
     */
    public record InfLayer(byte layer, InfCell[] infCells) {

    }

    public record InfCell(byte possX, byte possY, byte cellEntryId){

    }

    public void saveInfChunk(InfChunk infChunk, File rootSaveDir) {
        File chunksFile = getChunksFile(infChunk.chunkPossX, infChunk.chunkPossY, rootSaveDir);
        try {
            Files.createDirectories(chunksFile.toPath().getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (final DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(chunksFile)))) {
            // Métadonnées
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(SAVE_PROTOCOLE_VERSION).array());
            // Header
            outputStream.write(infChunk.infLayers.length); // nombre de layer
            for (int i = 0; i < infChunk.infLayers.length; i++) { // pour chaque layer
                outputStream.write(ByteBuffer.allocate(Short.BYTES).putShort((short) infChunk.infLayers[i].infCells.length).array()); // nombre de cellules pour de ce layer
            }
            // body
            for (byte i = 0; i < infChunk.infLayers.length; i++) {
                for (byte k = 0; k < infChunk.infLayers[i].infCells.length; k++) {
                    InfCell infCell = infChunk.infLayers[i].infCells[k];
                    outputStream.write(infCell.cellEntryId);
                    outputStream.write(Cells.getInnerChunkPoss(infCell.possX, infCell.possY));
                }
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            Gdx.app.error(TAG, "Le fichier n'a pas été trouve",e);
        } catch (IOException e) {
            Gdx.app.error(TAG, "Une erreur inattendue est survenue",e);
        }
    }

    public InfChunk readSavedInfChunk(int chunkPossX, int chunkPossY, File rootSaveDir) {
        File chunksFile = getChunksFile(chunkPossX, chunkPossY, rootSaveDir);
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
            InfLayer[] infLayers = new InfLayer[nombreDeLayer];
            for (byte i = 0; i < nombreDeLayer; i++) {
                InfCell[] infCells = new InfCell[nombreDeCelluleParLayer[i]];
                for (byte j = 0; j < nombreDeCelluleParLayer[i]; j++) {
                    byte idCell = inputWrap.get();
                    byte poss = inputWrap.get();
                    byte possX = Cells.getInnerChunkPossX(poss);
                    byte possY = Cells.getInnerChunkPossY(poss);
                    infCells[j] = new InfCell(possX, possY, idCell);
                }
                infLayers[i] = new InfLayer(i, infCells);
            }
            return new InfChunk(chunkPossX, chunkPossY, infLayers);
        } catch (FileNotFoundException e) {
            Gdx.app.error(TAG, "Le fichier n'a pas été trouve",e);
        } catch (IOException e) {
            Gdx.app.error(TAG, "Une erreur inattendue est survenue",e);
        }
        return null;
    }

    private static File getChunksFile(int chunkPossX, int chunkPossY, File rootSaveDir) {
        String chunkFileName = String.format("%s-%s", chunkPossX, chunkPossY);
        return new File(rootSaveDir,String.format("level/chunks/%s.rcs", chunkFileName));
    }
}
