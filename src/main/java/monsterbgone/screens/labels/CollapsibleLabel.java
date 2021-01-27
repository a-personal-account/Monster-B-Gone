package monsterbgone.screens.labels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import monsterbgone.screens.MonsterLibraryScreen;

import java.util.ArrayList;
import java.util.List;

public class CollapsibleLabel extends ClickableLabel {
    private static final int ARROW_SIZE = 32;

    public List<ModCheckboxLabel> source;
    private boolean checked;
    private String ID;

    public CollapsibleLabel(String ID, String name) {
        super(name);

        this.checked = true;
        this.ID = ID;
        source = new ArrayList<>();
    }

    public int update(float y, int offset, MonsterLibraryScreen parent) {
        offset += 2;
        super.update(y, offset);

        if(checked) {
            for (ModCheckboxLabel list : source) {
                offset++;
                offset = list.update(y, offset, parent);
            }
        }
        return offset;
    }

    public int render(SpriteBatch sb, float x, float y, int offset) {
        offset += 2;
        if(active) {
            sb.draw(ImageMaster.FILTER_ARROW, x - ARROW_SIZE / 2F, this.hb.cY - ARROW_SIZE / 2F, ARROW_SIZE / 2F, ARROW_SIZE / 2F, ARROW_SIZE, ARROW_SIZE, Settings.scale, Settings.scale, checked ? 0 : 90, 0, 0, ARROW_SIZE, ARROW_SIZE, false, false);
        }
        super.render(sb, x + ARROW_SIZE * Settings.scale / 2F, y, offset);

        if(checked) {
            for (ModCheckboxLabel list : source) {
                offset++;
                offset = list.render(sb, x + MonsterLibraryScreen.INDENT * 2, y, offset);
            }
        }
        return offset;
    }

    @Override
    protected boolean click() {
        checked = !checked;
        return false;
    }

    public void clear() {
        source.clear();
    }
}
