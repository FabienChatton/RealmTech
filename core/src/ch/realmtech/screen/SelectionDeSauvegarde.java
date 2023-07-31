package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.SaveInfManager;
import ch.realmtech.helper.Popup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SelectionDeSauvegarde extends AbstractScreen {
    private final static String TAG = SelectionDeSauvegarde.class.getSimpleName();
    private Table listeDesSauvegarde;
    private ScrollPane listeDesSauvegardeScrollPane;

    public SelectionDeSauvegarde(RealmTech context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        uiTable.clear();
        uiTable.add(new Label("Sélectionner une sauvegarde", skin));
        uiTable.row();
        listeDesSauvegarde = new Table(context.getSkin());
        try {
            List<File> files = SaveInfManager.listSauvegardeInfinie();
            for (File file : files) {
                Table fichierTable = new Table(skin);
                // button lancer la sauvegarde
                TextButton buttonFichier = new TextButton(file.getName(), skin);
                buttonFichier.addListener(loadSaveButton(file));
                fichierTable.add(buttonFichier).left();

                // button supprimer la sauvegarde
                TextButton buttonSupprimer = new TextButton("X", skin);
                buttonSupprimer.addListener(supprimerSave(file));
                fichierTable.add(buttonSupprimer).right();
                listeDesSauvegarde.add(fichierTable).expand().fill();
                listeDesSauvegarde.row();

            }
        } catch (IOException e) {
            Popup.popupErreur(context, e.getMessage(), uiStage);
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
                try {
                    context.loadInfFile(file.toPath());
                    context.setScreen(ScreenType.GAME_SCREEN);
                } catch (Exception e) {
                    Popup.popupErreur(context, e.getMessage(), uiStage);
                    context.getEcsEngine().clearAllEntity();
                }
            }
        };
    }

    private ClickListener supprimerSave(File file) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Popup.popupConfirmation(context, "voulez vous supprimer la sauvegarde \"" + file.getName() + "\" ?", uiStage, () -> {
                    try {
                        supprimerDossier(file);
                    } catch (IOException e) {
                        Popup.popupErreur(context, e.getMessage(), uiStage);
                    }
                    show();
                });
            }
        };
    }

    private void supprimerDossier(File file) throws IOException {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                supprimerDossier(listFile);
            }
        }
        Files.delete(file.toPath());
    }

    private ClickListener nouvelleCarte(TextField nomNouvelleCarte) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    context.getEcsEngine().generateNewSave(nomNouvelleCarte.getText());
                    context.setScreen(ScreenType.GAME_SCREEN);
                } catch (IOException | IllegalArgumentException e) {
                    Popup.popupErreur(context, e.getMessage(), uiStage);
                }
            }
        };
    }
}
