package monsterbgone.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import monsterbgone.MonsterBGone;
import monsterbgone.screens.labels.CollapsibleLabel;

import java.util.Arrays;
import java.util.List;

public class MonsterTabBar {
    private static final float TAB_SPACING = (198.0F + 20) * Settings.xScale;
    public static final float BASELINE = Settings.HEIGHT * 9F / 11F;

    public static final int BAR_W = 1334;

    private static final int BAR_H = 102;

    private static final int TAB_W = 274;

    private static final int TAB_H = 68;

    public static final int TICKBOX_W = 48;

    public static float LEFT_EDGE = Settings.WIDTH / 2.0F - BAR_W / 2F * Settings.scale;

    public Hitbox[] vanillaActs;
    public Hitbox viewPreview;
    private final String[] actNames;
    private boolean[] selected;
    public boolean viewPreviewChecked;

    private MonsterTabBarListener delegate;

    public MonsterTabBar(MonsterTabBarListener delegate, List<CollapsibleLabel> actNames) {
        float w = 200.0F * Settings.scale;
        float h = 50.0F * Settings.scale;
        this.vanillaActs = new Hitbox[MonsterBGone.ENCOUNTER_TYPES + 1];
        this.viewPreview = new Hitbox(360.0F * Settings.scale, 48.0F * Settings.scale);
        this.viewPreview.move(1410.0F * Settings.xScale, BASELINE);
        this.selected = new boolean[this.vanillaActs.length];
        float x = 470.0F * Settings.xScale - TAB_SPACING;
        for(int i = 0; i < this.vanillaActs.length; i++) {
            this.vanillaActs[i] = new Hitbox(w, h);
            this.vanillaActs[i].move(x += TAB_SPACING, BASELINE + 50.0F * Settings.scale);
            this.selected[i] = true;
        }
        this.delegate = delegate;

        this.actNames = new String[this.vanillaActs.length];
        this.actNames[0] = MonsterLibraryScreen.TEXT[1];
        for(int i = 0; i < actNames.size(); i++) {
            this.actNames[i + 1] = actNames.get(i).text;
        }
        this.viewPreviewChecked = false;
    }

    public void update() {
        for(int i = 0; i < this.vanillaActs.length; i++) {
            this.vanillaActs[i].update();
            if(this.vanillaActs[i].justHovered) {
                CardCrawlGame.sound.playA("UI_HOVER", -0.4F);
            }
            if(InputHelper.justClickedLeft && this.vanillaActs[i].hovered) {
                this.selected[i] = !this.selected[i];
                if(i == 0) {
                    Arrays.fill(selected, this.selected[i]);
                } else {
                    boolean newValue = false;
                    for (int j = 1; j < selected.length; j++) {
                        newValue |= selected[j];
                    }
                    selected[0] = newValue;
                }
                this.delegate.didChangeTab(this, selected);
            }
        }
        this.viewPreview.update();
        if(this.viewPreview.hovered && InputHelper.justClickedLeft) {
            this.viewPreviewChecked = !this.viewPreviewChecked;
        }
    }

    public Texture getBarColor(int num) {
        switch (num) {
            case 0:
                return ImageMaster.COLOR_TAB_COLORLESS;
            case 1:
                return ImageMaster.COLOR_TAB_RED;
            case 2:
                return ImageMaster.COLOR_TAB_GREEN;
            case 3:
                return ImageMaster.COLOR_TAB_BLUE;
            case 4:
                return ImageMaster.COLOR_TAB_CURSE;
        }
        throw new RuntimeException("invalid actnumber: " + num);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.GRAY);
        for(int i = 0; i < vanillaActs.length; i++) {
            if(!selected[i]) {
                renderTab(sb, getBarColor(i), this.vanillaActs[i].cX, BASELINE, this.actNames[i], selected[i]);
            }
        }
        sb.setColor(Color.WHITE);
        for(int i = 0; i < vanillaActs.length; i++) {
            if(selected[i]) {
                renderTab(sb, getBarColor(i), this.vanillaActs[i].cX, BASELINE, this.actNames[i], selected[i]);
            }
        }
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.COLOR_TAB_BAR, Settings.WIDTH / 2.0F - BAR_W / 2F, BASELINE - BAR_H / 2F, BAR_W / 2F, BAR_H / 2F, BAR_W, BAR_H, Settings.xScale, Settings.scale, 0.0F, 0, 0, BAR_W, BAR_H, false, false);

        sb.setColor(Color.WHITE);
        renderViewUpgrade(sb);

        for(int i = 0; i < vanillaActs.length; i++) {
            vanillaActs[i].render(sb);
        }
    }

    private void renderTab(SpriteBatch sb, Texture img, float x, float y, String label, boolean selected) {
        sb.draw(img, x - TAB_W / 2F, y - TAB_H / 2F + 53.0F * Settings.scale, TAB_W / 2F, TAB_H, TAB_W, TAB_H, Settings.xScale, Settings.scale, 0.0F, 0, 0, TAB_W, TAB_H, false, false);
        Color c = Settings.GOLD_COLOR;
        if (!selected)
            c = Color.GRAY;
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, label, x, y + 50.0F * Settings.scale, c, 0.9F);
    }

    private void renderViewUpgrade(SpriteBatch sb) {
        Color c = Settings.CREAM_COLOR;
        if (this.viewPreview.hovered)
            c = Settings.GOLD_COLOR;
        FontHelper.renderFontRightAligned(sb, FontHelper.topPanelInfoFont, MonsterLibraryScreen.TEXT[0], 1546.0F * Settings.xScale, BASELINE, c);
        sb.setColor(c);
        sb.draw(this.viewPreviewChecked ? ImageMaster.COLOR_TAB_BOX_TICKED : ImageMaster.COLOR_TAB_BOX_UNTICKED, 1532.0F * Settings.xScale -
                FontHelper.getSmartWidth(FontHelper.topPanelInfoFont, MonsterLibraryScreen.TEXT[0], 9999.0F, 0.0F) - TICKBOX_W / 2F, BASELINE - TICKBOX_W / 2F, TICKBOX_W / 2F, TICKBOX_W / 2F, TICKBOX_W, TICKBOX_W, Settings.scale, Settings.scale, 0.0F, 0, 0, TICKBOX_W, TICKBOX_W, false, false);

        this.viewPreview.render(sb);
    }
}
