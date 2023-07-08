package ch.realmtech.game.clickAndDrop;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ImageItemTable extends Table {
    public Image image;
    public Label countLabel;
    private final int[] stack;
    public ImageItemTable(Skin skin, int[] stack) {
        super(skin);
        this.stack = stack;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        addActor(image);
        image.moveBy(-image.getWidth() / 2, -image.getHeight() / 2);
        this.image = image;
    }

    public Label getCountLabel() {
        return countLabel;
    }

    public void setCountLabel(Label countLabel) {
        addActor(countLabel);
        countLabel.setFontScale(0.5f);
        countLabel.moveBy(-getImage().getWidth() / 2, 7);
        countLabel.setWidth(0);
        countLabel.setHeight(0);
        this.countLabel = countLabel;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public int[] getStack() {
        return stack;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }
}
