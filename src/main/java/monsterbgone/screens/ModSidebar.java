package monsterbgone.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModSidebar {
    private static final float SPACING = 64.0f;
    private List<ModEntry> entries = new ArrayList<>();

    private MonsterLibraryScreen parent;

    public ModSidebar(MonsterLibraryScreen parent, Map<String, Boolean> modfilters) {
        this.parent = parent;
        entries.add(new ModEntry(MonsterLibraryScreen.TEXT[1], true, Color.LIGHT_GRAY));
        for (Map.Entry<String, Boolean> mf : modfilters.entrySet()) {
            entries.add(new ModEntry(mf.getKey(), mf.getValue()));
        }
    }

    public void update(float y) {
        int offset = 0;
        for (ModEntry entry : entries) {
            entry.hb.move(157.0f * Settings.scale, y - SPACING * offset * Settings.scale - 14.0f * Settings.scale);
            entry.active = entry.hb.y < Settings.HEIGHT && entry.hb.y + entry.hb.height > 0;
            if (entry.active) {
                entry.hb.update();
                if (entry.hb.hovered && InputHelper.justClickedLeft) {
                    entry.selected = !entry.selected;
                    if (offset == 0) {
                        for (ModEntry entry2 : entries) {
                            entry2.selected = entry.selected;
                        }
                    } else {
                        boolean newValue = false;
                        for (int j = 1; j < entries.size(); j++) {
                            newValue |= entries.get(j).selected;
                        }
                        entries.get(0).selected = newValue;
                    }
                    parent.didChangeMod(entries);
                }
            }
            offset++;
        }
    }

    public void render(SpriteBatch sb) {
        for (ModEntry entry : entries) {
            if(entry.active) {
                Color color = entry.color.cpy();
                if (entry.selected) {
                    color = color.lerp(Color.GRAY, 0.5f);
                }
                sb.setColor(color);
                sb.draw(ImageMaster.COLOR_TAB_BAR, 40.0f * Settings.scale, entry.hb.cY - 51F, 0, 0, 235.0F, 102.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);

                Color textcolor = Settings.GOLD_COLOR;
                if (!entry.selected) {
                    textcolor = Color.GRAY;
                    sb.setColor(Color.GRAY);
                } else {
                    sb.setColor(Color.WHITE);
                }
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, entry.key, 157.0f * Settings.scale, entry.hb.cY, textcolor, 0.85f);
            }
        }

        for (ModEntry entry : entries) {
            entry.hb.render(sb);
        }
    }

    public static class ModEntry {
        public String key;
        public boolean selected;
        public Color color;
        public Hitbox hb;
        public boolean active;

        public ModEntry(String key, boolean selected) {
            int n = 0;
            for (byte b : key.getBytes()) {
                n += b;
            }
            Random random = new Random(n);
            this.color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1F);

            this.key = key;
            this.selected = selected;
            this.hb = new Hitbox(235.0f * Settings.scale, 51.0f * Settings.scale);
        }
        public ModEntry(String key, boolean selected, Color color) {
            this.color = color;

            this.key = key;
            this.selected = selected;
            this.hb = new Hitbox(235.0f * Settings.scale, 51.0f * Settings.scale);
        }
    }
}
