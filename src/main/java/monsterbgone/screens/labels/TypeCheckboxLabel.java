package monsterbgone.screens.labels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import monsterbgone.MonsterBGone;
import monsterbgone.screens.MonsterLibraryScreen;

import java.util.List;
import java.util.Map;

public class TypeCheckboxLabel extends ClickableLabel {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(MonsterBGone.makeID("encountertypes"));

    public List<MonsterCheckboxLabel> source;

    public TypeCheckboxLabel(int key, Map<String, Boolean> source) {
        super(uiStrings.TEXT[key]);

        this.source = MonsterCheckboxLabel.fromMap(source);
    }

    public int update(float y, int offset, MonsterLibraryScreen parent) {
        offset++;
        super.update(y, offset);

        for(MonsterCheckboxLabel mcl : source) {
            offset++;
            offset = mcl.update(y, offset, parent);
        }
        return offset;
    }

    public int render(SpriteBatch sb, float x, float y, int offset) {
        offset++;
        super.render(sb, x, y, offset);

        for(MonsterCheckboxLabel mcl : source) {
            offset++;
            mcl.render(sb, x + MonsterLibraryScreen.INDENT, y, offset);
        }
        return offset;
    }

    @Override
    protected boolean click() {
        boolean newVal = false;
        for (MonsterCheckboxLabel mcl : source) {
            if (newVal |= mcl.checked) {
                break;
            }
        }
        for (MonsterCheckboxLabel mcl : source) {
            mcl.checked = newVal;
            mcl.updateEntry();
        }
        return true;
    }
}
