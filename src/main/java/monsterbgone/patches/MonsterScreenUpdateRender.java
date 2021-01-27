package monsterbgone.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import monsterbgone.screens.MonsterLibraryScreen;

public class MonsterScreenUpdateRender {
    @SpirePatch(
            clz=MainMenuScreen.class,
            method="update"
    )
    public static class Update {
        public static void Postfix(MainMenuScreen __instance) {
            if (__instance.screen == MonsterLibraryScreen.Enum.MONSTERBGONE) {
                MonsterMenuButton.monsterScreen.update();
            }
        }
    }

    @SpirePatch(
            clz=MainMenuScreen.class,
            method="render"
    )
    public static class Render {
        public static void Postfix(MainMenuScreen __instance, SpriteBatch sb) {
            if (__instance.screen == MonsterLibraryScreen.Enum.MONSTERBGONE) {
                MonsterMenuButton.monsterScreen.render(sb);
            }
        }
    }
}
