package monsterbgone.patches;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import monsterbgone.screens.MonsterLibraryScreen;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "render"
)
@SpirePatch(
        clz = CustomMonster.class,
        method = "render"
)
public class MonsterRenderPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert(AbstractMonster __instance, SpriteBatch sb) {
        if (CardCrawlGame.mainMenuScreen.screen == MonsterLibraryScreen.Enum.MONSTERBGONE) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getCurrRoom");

            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
