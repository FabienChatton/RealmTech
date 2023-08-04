package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.SaveInfManager;
import ch.realmtech.helper.ButtonsMenu;
import ch.realmtech.helper.OnClick;
import ch.realmtech.helper.Popup;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SelectionDeSauvegarde extends AbstractScreen {
    private final static String TAG = SelectionDeSauvegarde.class.getSimpleName();
    private Table listeDesSauvegarde;
    private ScrollPane listeDesSauvegardeScrollPane;
    private List<File> listSauvegarde;

    public SelectionDeSauvegarde(RealmTech context) {
        super(context);
        listSauvegarde = new ArrayList<>();
    }

    @Override
    public void show() {
        super.show();
        uiTable.setFillParent(true);
        uiTable.clear();
        uiTable.add(new Label("Sélectionner une sauvegarde", skin)).top();
        uiTable.row();
        listeDesSauvegarde = new Table(context.getSkin());
        try {
            listSauvegarde = SaveInfManager.listSauvegardeInfinie();
            for (File file : listSauvegarde) {
                Table fichierTable = new Table(skin);
                // button lancer la sauvegarde
                TextButton buttonFichier = ButtonsMenu.textButton(context, file.getName());
                buttonFichier.addListener(loadSaveButton(file));
                fichierTable.add(buttonFichier).expand();

                // button supprimer la sauvegarde
                TextButton buttonSupprimer = ButtonsMenu.textButton(context, "X", new OnClick((event, x, y) -> Popup.popupConfirmation(context, "voulez vous supprimer la sauvegarde \"" + file.getName() + "\" ?", uiStage, () -> {
                    try {
                        supprimerDossier(file);
                    } catch (IOException e) {
                        Popup.popupErreur(context, e.getMessage(), uiStage);
                    }
                    show();
                })));
                buttonSupprimer.setColor(Color.RED);
                fichierTable.add(buttonSupprimer);
                listeDesSauvegarde.add(fichierTable).width(200f).row();

            }
        } catch (IOException e) {
            Popup.popupErreur(context, e.getMessage(), uiStage);
        }
        listeDesSauvegardeScrollPane = new ScrollPane(listeDesSauvegarde);
        uiTable.add(listeDesSauvegardeScrollPane).expand().top();
        uiTable.row();

        Table nouvelleCarteTable = new Table(skin);
        TextField nouvelleCarteTextField = new TextField("", skin);
        nouvelleCarteTable.add(nouvelleCarteTextField).width(200f).padRight(10f);
        TextButton nouvelleCarteButton = ButtonsMenu.textButton(context, "générer nouvelle carte", new OnClick((event, x, y) -> {
            if (listSauvegarde.stream().map(File::getName).anyMatch(sauvegardeName -> sauvegardeName.equalsIgnoreCase(nouvelleCarteTextField.getText()))) {
                Popup.popupErreur(context, "Une sauvegarde au même nom existe déjà. Veilliez choisir un autre nom", uiStage);
                return;
            }
            try {
                context.getEcsEngine().generateNewSave(nouvelleCarteTextField.getText());
                context.setScreen(ScreenType.GAME_SCREEN);
            } catch (IOException | IllegalArgumentException e) {
                Popup.popupErreur(context, e.getMessage(), uiStage);
            }
        }));
        nouvelleCarteTable.add(nouvelleCarteButton);
        uiTable.add(nouvelleCarteTable).row();

        TextButton backButton = ButtonsMenu.textButton(context, "back", new OnClick((event, x, y) -> context.setScreen(ScreenType.MENU)));
        uiTable.add(backButton);
        InputEvent defaultClick = new InputEvent();
        defaultClick.setStage(listeDesSauvegardeScrollPane.getStage());
        defaultClick.setStageX(listeDesSauvegardeScrollPane.getMaxX());
        defaultClick.setStageY(listeDesSauvegardeScrollPane.getMaxY());
        defaultClick.setType(InputEvent.Type.touchDown);
        defaultClick.setButton(Input.Buttons.LEFT);
        listeDesSauvegardeScrollPane.fire(defaultClick);
    }

    private ClickListener loadSaveButton(final File file) {
        return new OnClick((event, x, y) -> {
            try {
                context.loadInfFile(file.toPath());
                context.setScreen(ScreenType.GAME_SCREEN);
            } catch (Exception e) {
                Popup.popupErreur(context, e.getMessage(), uiStage);
                context.getEcsEngine().clearAllEntity();
            }
        });
    }

    private ClickListener supprimerSave(File file) {
        return new OnClick((event, x, y) -> {
            Popup.popupConfirmation(context, "voulez vous supprimer la sauvegarde \"" + file.getName() + "\" ?", uiStage, () -> {
                try {
                    supprimerDossier(file);
                } catch (IOException e) {
                    Popup.popupErreur(context, e.getMessage(), uiStage);
                }
                show();
            });
        });
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
                if (listSauvegarde.stream().map(File::getName).anyMatch(sauvegardeName -> sauvegardeName.equalsIgnoreCase(nomNouvelleCarte.getText()))) {
                    Popup.popupErreur(context, "Une sauvegarde au même nom existe déjà. Veilliez choisir un autre nom", uiStage);
                    return;
                }
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
