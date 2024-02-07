package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.ButtonsMenu;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.server.ecs.system.SaveInfManager;
import ch.realmtech.server.level.worldGeneration.SeedGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CreateNewWorldScreen extends AbstractScreen {

    private final TextField seedField;

    public CreateNewWorldScreen(RealmTech context) {
        super(context);
        seedField = new TextField(null, skin);
    }

    @Override
    public void show() {
        super.show();
        ButtonsMenu.TextButtonMenu backButton = new ButtonsMenu.TextButtonMenu(context, "back", new OnClick((event, x, y) -> context.setScreen(ScreenType.SELECTION_DE_SAUVEGARDE)));
        List<File> listSauvegarde;
        try {
            listSauvegarde = SaveInfManager.listSauvegardeInfinie();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<File> finalListSauvegarde = listSauvegarde;

        uiTable.setSkin(skin);
        uiTable.setFillParent(true);
        uiTable.add("Création d'un nouveau monde");
        uiTable.row();

        Table contenue = new Table(skin);
        Table saveNameTable = new Table(skin);
        TextField saveNameTextField = new TextField(null, skin);
        saveNameTable.add(new Label("Nom:", skin)).left();
        saveNameTable.row();
        saveNameTable.add(saveNameTextField).width(400f).left();
        contenue.add(saveNameTable);
        contenue.row();

        Table seedTable = new Table(skin);
        seedTable.add(new Label("seed (optional): ", skin)).left();
        seedTable.row();
        seedTable.add(seedField).width(400f).left();
        contenue.add(seedTable);
        contenue.row();

        uiTable.add(contenue).expandY().fillY().top();
        uiTable.row();

        Table footer = new Table(skin);
        TextButton nouvelleSauvegardeButton = new ButtonsMenu.TextButtonMenu(context, "create new save", new OnClick((event, x, y) -> createNewSave(context, finalListSauvegarde, saveNameTextField)));
        footer.add(nouvelleSauvegardeButton).padRight(10f);
        footer.add(backButton);
        footer.row();
        uiTable.add(footer).padBottom(10f);

    }

    private void createNewSave(RealmTech context, List<File> listSauvegarde, TextField nouvelleSauvegardeNomTextField) {
        if (listSauvegarde.stream().map(File::getName).anyMatch((sauvegardeName) -> sauvegardeName.equalsIgnoreCase(nouvelleSauvegardeNomTextField.getText()))) {
            Popup.popupErreur(context, "Une sauvegarde au même nom existe déjà. Veilliez choisir un autre nom", uiStage);
            return;
        }
        try {
            long seed;
            if (!seedField.getText().isBlank()) {
                byte[] seedFieldBytes = seedField.getText().getBytes(StandardCharsets.UTF_8);
                long somme = 0;
                for (byte seedFieldByte : seedFieldBytes) {
                    somme += seedFieldByte;
                }
                seed = somme;
            } else {
                seed = SeedGenerator.randomSeed();
            }
            context.rejoindreSoloServeur(nouvelleSauvegardeNomTextField.getText(), seed);
        } catch (Exception e) {
            Popup.popupErreur(context, e.getMessage(), uiStage);
        }
    }
}
