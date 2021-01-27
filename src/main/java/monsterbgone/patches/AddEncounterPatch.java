package monsterbgone.patches;

import basemod.BaseMod;
import monsterbgone.MonsterBGone;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.monsters.MonsterInfo;

public class AddEncounterPatch {
    @SpirePatch(
            clz = BaseMod.class,
            method = "addMonsterEncounter"
    )
    public static class WeakEncounter {
        @SpirePostfixPatch
        public static void Postfix(String dungeonID, MonsterInfo encounter) {
            if(MonsterBGone.isVanillaAct(dungeonID)) {
                MonsterBGone.addConfigThing(dungeonID, encounter.name, 0);
            }
        }
    }

    @SpirePatch(
            clz = BaseMod.class,
            method = "addStrongMonsterEncounter"
    )
    public static class StronkEncounter {
        @SpirePostfixPatch
        public static void Postfix(String dungeonID, MonsterInfo encounter) {
            if(MonsterBGone.isVanillaAct(dungeonID)) {
                MonsterBGone.addConfigThing(dungeonID, encounter.name, 1);
            }
        }
    }

    @SpirePatch(
            clz = BaseMod.class,
            method = "addEliteEncounter"
    )
    public static class EliteEncounter {
        @SpirePostfixPatch
        public static void Postfix(String dungeonID, MonsterInfo encounter) {
            if(MonsterBGone.isVanillaAct(dungeonID)) {
                MonsterBGone.addConfigThing(dungeonID, encounter.name, 2);
            }
        }
    }

    @SpirePatch(
            clz = BaseMod.class,
            method = "addBoss"
    )
    public static class BossEncounter {
        @SpirePostfixPatch
        public static void Postfix(String dungeon, String bossID) {
            if(MonsterBGone.isVanillaAct(dungeon)) {
                MonsterBGone.addConfigThing(dungeon, bossID, 3);
            }
        }
    }
}
