package monsterbgone.screens.labels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import monsterbgone.MonsterBGone;
import monsterbgone.screens.MonsterLibraryScreen;
import monsterbgone.screens.MonsterTabBar;

public abstract class ClickableLabel {
    public Hitbox hb;
    public String text;
    protected boolean active;

    public ClickableLabel(String text) {
        this.hb = new Hitbox(MonsterTabBar.LEFT_EDGE, 0, MonsterTabBar.BAR_W * Settings.scale, MonsterLibraryScreen.LINEHEIGHT);
        this.text = text;
    }

    public int update(float y, int offset) {
        y = this.calcY(y, offset);
        active = (y < MonsterTabBar.BASELINE && y > -Settings.HEIGHT - MonsterLibraryScreen.LINEHEIGHT);
        this.hb.move(this.hb.cX, y);
        if (active) {
            this.hb.update();
            if (this.hb.hovered && InputHelper.justClickedLeft && this.click()) {
                MonsterBGone.saveConfigData();
            }
        }
        return offset;
    }

    protected int render(SpriteBatch sb, float x, float y, int offset) {
        if(active) {
            FontHelper.renderFontLeft(sb, FontHelper.topPanelInfoFont, text, x, this.hb.cY, this.hb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR);
            this.hb.render(sb);
        }
        return offset;
    }

    protected float calcY(float y, int offset) {
        return -offset * MonsterLibraryScreen.LINEHEIGHT + y;
    }

    protected abstract boolean click();
}
