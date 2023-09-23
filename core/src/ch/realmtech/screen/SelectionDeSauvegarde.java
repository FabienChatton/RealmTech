package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtechServer.ecs.system.SaveInfManager;
import ch.realmtech.helper.ButtonsMenu.ScrollPaneMenu;
import ch.realmtech.helper.OnClick;
import ch.realmtech.helper.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static ch.realmtech.helper.ButtonsMenu.TextButtonMenu;

public class SelectionDeSauvegarde extends AbstractScreen {
    private final static String TAG = SelectionDeSauvegarde.class.getSimpleName();
    private Table listeDesSauvegarde;
    private ScrollPaneMenu listeDesSauvegardeScrollPane;
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
                TextButton buttonFichier = new TextButtonMenu(context, file.getName());
                buttonFichier.addListener(loadSaveButton(file));
                fichierTable.add(buttonFichier).expand();

                // button supprimer la sauvegarde
                TextButton buttonSupprimer = new TextButtonMenu(context, "X", new OnClick((event, x, y) -> Popup.popupConfirmation(context, "voulez vous supprimer la sauvegarde \"" + file.getName() + "\" ?", uiStage, () -> {
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
            Gdx.app.error(TAG, e.getMessage(), e);
            Popup.popupErreur(context, e.getMessage(), uiStage);
        }
        listeDesSauvegardeScrollPane = new ScrollPaneMenu(context, listeDesSauvegarde);
        uiTable.add(listeDesSauvegardeScrollPane).expand().fillX().top().row();
        listeDesSauvegardeScrollPane.focus();

        Table nouvelleCarteTable = new Table(skin);
        TextField nouvelleCarteTextField = new TextField("", skin);
        nouvelleCarteTable.add(nouvelleCarteTextField).width(200f).padRight(10f);
        TextButton nouvelleCarteButton = new TextButtonMenu(context, "générer nouvelle carte", new OnClick((event, x, y) -> {
            if (listSauvegarde.stream().map(File::getName).anyMatch(sauvegardeName -> sauvegardeName.equalsIgnoreCase(nouvelleCarteTextField.getText()))) {
                Popup.popupErreur(context, "Une sauvegarde au même nom existe déjà. Veilliez choisir un autre nom", uiStage);
                return;
            }
            try {
                context.generateNewSave(nouvelleCarteTextField.getText());
                context.setScreen(ScreenType.GAME_SCREEN);
            } catch (IOException | IllegalArgumentException e) {
                Popup.popupErreur(context, e.getMessage(), uiStage);
            }
        }));
        nouvelleCarteTable.add(nouvelleCarteButton);
        uiTable.add(nouvelleCarteTable).row();

        TextButtonMenu backButton = new TextButtonMenu(context, "back", new OnClick((event, x, y) -> context.setScreen(ScreenType.MENU)));
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
                context.loadInfFile(file.getName());
                context.setScreen(ScreenType.GAME_SCREEN);
            } catch (Exception e) {
                Gdx.app.error(TAG, e.getMessage(), e);
                Popup.popupErreur(context, e.getMessage(), uiStage);
                context.supprimeECS();
            }
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
}
