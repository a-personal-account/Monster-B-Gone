package monsterbgone.screens.labels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import monsterbgone.MonsterBGone;
import monsterbgone.screens.MonsterLibraryScreen;

import java.util.Map;

public class ModCheckboxLabel extends ClickableLabel {
    public TypeCheckboxLabel[] source;

    public ModCheckboxLabel(String key, Map<String, Boolean>[] source) {
        super(key);

        this.source = new TypeCheckboxLabel[MonsterBGone.ENCOUNTER_TYPES];
        for (int i = 0; i < this.source.length; i++) {
            if (source[i] != null) {
                this.source[i] = new TypeCheckboxLabel(i, source[i]);
            }
        }
    }

    public int update(float y, int offset, MonsterLibraryScreen parent) {
        offset++;
        super.update(y, offset);

        for (TypeCheckboxLabel list : source) {
            if (list != null) {
                offset++;
                offset = list.update(y, offset, parent);
            }
        }
        return offset;
    }

    public int render(SpriteBatch sb, float x, float y, int offset) {
        offset++;
        super.render(sb, x, y, offset);

        for (int i = 0; i < source.length; i++) {
            if (source[i] != null) {
                offset++;
                offset = source[i].render(sb, x + MonsterLibraryScreen.INDENT, y, offset);
            }
        }
        return offset;
    }

    @Override
    protected boolean click() {
        boolean newVal = false;
        for (TypeCheckboxLabel list : source) {
            if (list != null) {
                for (MonsterCheckboxLabel mcl : list.source) {
                    if (newVal |= mcl.checked) {
                        break;
                    }
                }
                if (newVal) {
                    break;
                }
            }
        }
        for (TypeCheckboxLabel list : source) {
            if (list != null) {
                for (MonsterCheckboxLabel mcl : list.source) {
                    mcl.checked = newVal;
                    mcl.updateEntry();
                }
            }
        }
        return true;
    }
}
