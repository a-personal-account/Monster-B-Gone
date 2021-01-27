package monsterbgone.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import monsterbgone.screens.MonsterLibraryScreen;

import java.lang.reflect.Field;

public class MonsterMenuButton {
    @SpireEnum
    static MenuButton.ClickResult MONSTERBGONE;

    static MonsterLibraryScreen monsterScreen = null;

    @SpirePatch(
            clz=MenuButton.class,
            method="setLabel"
    )
    public static class SetLabel
    {
        public static void Postfix(MenuButton __instance)
        {
            try {
                if (__instance.result == MONSTERBGONE) {
                    Field f_label = MenuButton.class.getDeclaredField("label");
                    f_label.setAccessible(true);
                    f_label.set(__instance, "Modded Encounters");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(
            clz=MenuButton.class,
            method="buttonEffect"
    )
    public static class ButtonEffect
    {
        public static void Postfix(MenuButton __instance)
        {
            if (__instance.result == MONSTERBGONE) {
                if (monsterScreen == null) {
                    monsterScreen = new MonsterLibraryScreen();
                }
                monsterScreen.open();
            }
        }
    }
}
