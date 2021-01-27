package monsterbgone.screens;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import monsterbgone.MonsterBGone;
import monsterbgone.screens.labels.MonsterCheckboxLabel;

public class EncounterPreview {
    public MonsterCheckboxLabel parent;
    private MonsterGroup group;
    public float alpha;
    public boolean ending;

    public EncounterPreview(MonsterLibraryScreen mls, MonsterCheckboxLabel mcl, float alpha) {
        MonsterBGone.logger.info("Loading encounter: " + mcl.key);
        this.parent = mcl;

        try {
            MonsterBGone.primeRNG();
            this.group = BaseMod.getMonster(mcl.key);
        } catch(Exception e) {
            e.printStackTrace();
            mls.tabBar.viewPreviewChecked = false;
        }

        this.alpha = alpha;
        this.ending = false;
    }

    public boolean update() {
        if(group == null) {
            return true;
        }
        for(AbstractMonster mo : group.monsters) {
            mo.update();
        }
        if(ending) {
            if(alpha > 0F) {
                alpha -= Gdx.graphics.getDeltaTime() / 5F;
                if(alpha < 0F) {
                    dispose();
                    return true;
                }
            }
        } else if(alpha < 1F) {
            alpha += Gdx.graphics.getDeltaTime();
            if(alpha > 1F) {
                alpha = 1F;
            }
        }
        return false;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(new Color(1F, 1F, 1F, alpha));
        group.render(sb);
        sb.setColor(Color.WHITE);
    }

    public void dispose() {
        for(AbstractMonster mo : group.monsters) {
            mo.dispose();
        }
    }
}
