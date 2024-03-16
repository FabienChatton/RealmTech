package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.ButtonsMenu.ScrollPaneMenu;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.server.ecs.system.MapSystemServer;
import ch.realmtech.server.ecs.system.SaveInfManager;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static ch.realmtech.core.helper.ButtonsMenu.TextButtonMenu;

public class SelectASaveScreen extends AbstractScreen {
    private final static Logger logger = LoggerFactory.getLogger(MapSystemServer.class);
    private List<File> listSauvegarde;

    public SelectASaveScreen(RealmTech context) {
        super(context);
        listSauvegarde = new ArrayList<>();
    }

    @Override
    public void show() {
        super.show();
        TextButtonMenu createNewSave = new TextButtonMenu(context, "create new save", new OnClick((event, x, y) -> context.setScreen(ScreenType.CREATE_NEW_WORLD)));
        TextButtonMenu backButton = new TextButtonMenu(context, "back", new OnClick((event, x, y) -> context.setScreen(ScreenType.MENU)));

        uiTable.clear();
        uiTable.setFillParent(true);
        uiTable.add(new Label("Select a save", skin)).top();
        uiTable.row();
        Table listeDesSauvegarde = new Table(context.getSkin());
        try {
            listSauvegarde = SaveInfManager.listSauvegardeInfinie();
            for (File file : listSauvegarde) {
                Table fichierTable = new Table(skin);
                // button lancer la sauvegarde
                TextButton buttonFichier = new TextButtonMenu(context, file.getName());
                buttonFichier.addListener(loadSaveButton(file));
                fichierTable.add(buttonFichier).expand();

                // button supprimer la sauvegarde
                TextButton buttonSupprimer = getButtonSupprimer(file);
                fichierTable.add(buttonSupprimer);
                listeDesSauvegarde.add(fichierTable).width(200f).row();

            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            Popup.popupErreur(context, e.getMessage(), uiStage);
        }
        ScrollPaneMenu listeDesSauvegardeScrollPane = new ScrollPaneMenu(context, listeDesSauvegarde);
        uiTable.add(listeDesSauvegardeScrollPane).expand().fillX().top().row();
        listeDesSauvegardeScrollPane.focus();

        uiTable.row();
        Table buttonsDuBas = new Table(skin);
        buttonsDuBas.add(createNewSave).padRight(10f);
        buttonsDuBas.add(backButton);
        uiTable.add(buttonsDuBas).padBottom(10f);

        InputEvent defaultClick = new InputEvent();
        defaultClick.setStage(listeDesSauvegardeScrollPane.getStage());
        defaultClick.setStageX(listeDesSauvegardeScrollPane.getMaxX());
        defaultClick.setStageY(listeDesSauvegardeScrollPane.getMaxY());
        defaultClick.setType(InputEvent.Type.touchDown);
        defaultClick.setButton(Input.Buttons.LEFT);
        listeDesSauvegardeScrollPane.fire(defaultClick);
    }

    private TextButton getButtonSupprimer(File file) {
        TextButton buttonSupprimer = new TextButtonMenu(context, "X", new OnClick((event, x, y) -> Popup.popupConfirmation(context, "voulez vous supprimer la sauvegarde \"" + file.getName() + "\" ?", uiStage, () -> {
            try {
                deleteFolder(file);
            } catch (IOException e) {
                Popup.popupErreur(context, e.getMessage(), uiStage);
            }
            show();
        })));
        buttonSupprimer.setColor(Color.RED);
        return buttonSupprimer;
    }

    private ClickListener loadSaveButton(final File file) {
        return new OnClick((event, x, y) -> {
            try {
                context.rejoindreSoloServeur(file.getName(), null);
            } catch (Exception e) {
                Popup.popupErreur(context, e.getMessage(), uiStage);
                logger.error(e.getMessage(), e);
                context.closeEcs();
            }
        });
    }

    private void deleteFolder(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;
            for (File listFile : files) {
                deleteFolder(listFile);
            }
        }
        Files.delete(file.toPath());
    }
}
