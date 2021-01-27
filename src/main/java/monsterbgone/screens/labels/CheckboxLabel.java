package monsterbgone.screens.labels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import monsterbgone.screens.MonsterTabBar;

public abstract class CheckboxLabel extends ClickableLabel {
    public boolean checked;

    public CheckboxLabel(String key) {
        super(key);
    }

    @Override
    public int render(SpriteBatch sb, float x, float y, int offset) {
        sb.setColor(this.hb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR);
        sb.draw(this.checked ? ImageMaster.COLOR_TAB_BOX_TICKED : ImageMaster.COLOR_TAB_BOX_UNTICKED, x + MonsterTabBar.TICKBOX_W / 6F, hb.y - MonsterTabBar.TICKBOX_W / 6F, MonsterTabBar.TICKBOX_W / 2F, MonsterTabBar.TICKBOX_W / 2F, MonsterTabBar.TICKBOX_W, MonsterTabBar.TICKBOX_W, Settings.scale, Settings.scale, 0.0F, 0, 0, MonsterTabBar.TICKBOX_W, MonsterTabBar.TICKBOX_W, false, false);
        super.render(sb, x + (MonsterTabBar.TICKBOX_W) * Settings.scale, y, offset);
        return offset;
    }
}
