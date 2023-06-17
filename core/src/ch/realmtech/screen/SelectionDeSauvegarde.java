package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.SaveInfManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SelectionDeSauvegarde extends AbstractScreen {
    private final static String TAG = SelectionDeSauvegarde.class.getSimpleName();
    private VerticalGroup listeDesSauvegarde;
    private ScrollPane listeDesSauvegardeScrollPane;

    public SelectionDeSauvegarde(RealmTech context) throws IOException {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        uiTable.clear();
        uiTable.add(new Label("Sélectionner une sauvegarde", skin));
        uiTable.row();
        listeDesSauvegarde = new VerticalGroup();
//        ImmutableBag<File> tousLesSauvegarde = context.getEcsEngine().getSystem(SaveManager.class).getTousLesSauvegarde();
//        for (final File file : tousLesSauvegarde) {
//            Table table = new Table(skin);
//            TextButton loadSaveButton = new TextButton(file.getName().split("\\.")[0], skin);
//            String version = null;
//            try {
//                version = String.valueOf(ByteBuffer.wrap(Files.readAllBytes(file.toPath()), 9, Integer.BYTES).getInt());
//            } catch (Exception e) {
//                continue;
//            }
//            TextButton supprimerSave = new TextButton("supprimer", skin);
//            supprimerSave.addListener(supprimerSave(file));
//            loadSaveButton.row();
//            loadSaveButton.addListener(loadSaveButton(file));
//            table.add(loadSaveButton);
////            table.row();
////            loadSaveButton.add(new Label("rtsV : " + version + "",skin));
//            table.add(supprimerSave);
//            listeDesSauvegarde.addActor(table);
//        }
        List<File> files = null;
        try {
            files = SaveInfManager.listSauvegardeInfinie();
        } catch (IOException e) {
            Gdx.app.error(TAG, e.getMessage(), e);
        }
        for (int i = 0; i < files.size(); i++) {
            System.out.println(files.get(i));
        }
        listeDesSauvegardeScrollPane = new ScrollPane(listeDesSauvegarde);
        uiTable.add(listeDesSauvegardeScrollPane);
        uiTable.row();
        TextField nomNouvelleCarte = new TextField("", skin);
        uiTable.add(nomNouvelleCarte);
        TextButton nouvelleCarteButton = new TextButton("générer nouvelle carte", skin);
        nouvelleCarteButton.addListener(nouvelleCarte(nomNouvelleCarte));
        uiTable.add(nouvelleCarteButton);
    }

    private ClickListener loadSaveButton(final File file) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //context.loadInfFile(file);
                //context.newSaveInitWorld(file);
                //context.loadSaveOnWorkingSave();
            }
        };
    }

    private ClickListener supprimerSave(File file) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                file.delete();
                show();
            }
        };
    }

    private ClickListener nouvelleCarte(TextField nomNouvelleCarte) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    context.getEcsEngine().generateNewSave(nomNouvelleCarte.getText());
                    context.setScreen(ScreenType.GAME_SCREEN);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                if (nomNouvelleCarte.getText().isEmpty()) {
//                    uiStage.addActor(PopupHelper.popupErreur("Le nom de la carte ne doit pas être vide"));
//                    return;
//                }
//                try {
//                    context.newSaveInitWorld(nomNouvelleCarte.getText());
//                    context.generateNewWorld();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            }
        };
    }
}
