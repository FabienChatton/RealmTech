package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.game.io.Save;
import ch.realmtech.helper.PopupHelper;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class SelectionDeSauvegarde extends AbstractScreen{
    private VerticalGroup listeDesSauvegarde;
    private ScrollPane listeDesSauvegardeScrollPane;

    public SelectionDeSauvegarde(RealmTech context) throws IOException {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        uiTable.clear();
        uiTable.add(new Label("Sélectionner une sauvegarde",skin));
        uiTable.row();
        listeDesSauvegarde = new VerticalGroup();
        Array<File> saveFile = Save.getTousLesSauvegarde();
        for (final File file : saveFile) {
            Table table = new Table(skin);
            TextButton loadSaveButton = new TextButton(file.getName().split("\\.")[0],skin);
            String version = null;
            try {
                version = String.valueOf(ByteBuffer.wrap(Files.readAllBytes(file.toPath()),9,Integer.BYTES).getInt());
            } catch (Exception e) {
                continue;
            }
            TextButton supprimerSave = new TextButton("supprimer", skin);
            supprimerSave.addListener(supprimerSave(file));
            loadSaveButton.row();
            loadSaveButton.addListener(loadSaveButton(file));
            table.add(loadSaveButton);
//            table.row();
//            loadSaveButton.add(new Label("rtsV : " + version + "",skin));
            table.add(supprimerSave);
            listeDesSauvegarde.addActor(table);
        }
        listeDesSauvegardeScrollPane = new ScrollPane(listeDesSauvegarde);
        uiTable.add(listeDesSauvegardeScrollPane);
        uiTable.row();
        TextField nomNouvelleCarte = new TextField("",skin);
        uiTable.add(nomNouvelleCarte);
        TextButton nouvelleCarteButton = new TextButton("générer nouvelle carte",skin);
        nouvelleCarteButton.addListener(nouvelleCarte(nomNouvelleCarte));
        uiTable.add(nouvelleCarteButton);
    }

    private ClickListener loadSaveButton(File file) {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    context.loadSave(file);
                    context.setScreen(ScreenType.GAME_SCREEN);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    private ClickListener supprimerSave(File file) {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                file.delete();
                show();
            }
        };
    }

    private ClickListener nouvelleCarte(TextField nomNouvelleCarte) {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nomNouvelleCarte.getText().isEmpty()) {
                    uiStage.addActor(PopupHelper.popupErreur("Le nom de la carte ne doit pas être vide"));
                    return;
                }
                try {
                    context.newSave(nomNouvelleCarte.getText());
                    context.setScreen(ScreenType.GAME_SCREEN);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
