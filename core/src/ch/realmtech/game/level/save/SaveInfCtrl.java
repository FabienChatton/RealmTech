package ch.realmtech.game.level.save;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;

import java.io.File;

import static ch.realmtech.game.level.save.SaveInfWrk.InfChunk;

public class SaveInfCtrl {
    private final RealmTech context;
    private final SaveInfWrk saveInfWrk;
    private final File rootSaveDir;

    private SaveInfCtrl(RealmTech context, File rootSaveDir) {
        this.context = context;
        saveInfWrk = new SaveInfWrk();
        this.rootSaveDir = rootSaveDir;
    }

    public static SaveInfCtrl fromFileName(RealmTech context, String rootSaveDirName) {
        File rootSaveDir = Gdx.files.local(rootSaveDirName).file();
        return new SaveInfCtrl(context, rootSaveDir);
    }

    public void readChunk(int chunkPosX, int chunkPosY, File rootSaveDir) {
        InfChunk infChunk = saveInfWrk.readSavedInfChunk(chunkPosX, chunkPosY, rootSaveDir);

    }
}
