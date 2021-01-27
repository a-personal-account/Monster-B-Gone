package monsterbgone.patches;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import monsterbgone.MonsterBGone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetEncounterPatch {

    @SpirePatch(
            clz = BaseMod.class,
            method = "getMonsterEncounters"
    )
    public static class WeakEncounter {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<List<MonsterInfo>> Insert(String dungeonID) {
            if (MonsterBGone.isVanillaAct(dungeonID)) {
                return SpireReturn.Return(MonsterBGone.getEncounters(dungeonID, ((Map<String, List<MonsterInfo>>)ReflectionHacks.getPrivateStatic(BaseMod.class, "customMonsterEncounters")).get(dungeonID), 0));
            }
            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(HashMap.class, "get");

                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = BaseMod.class,
            method = "getStrongMonsterEncounters"
    )
    public static class StronkEncounter {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<List<MonsterInfo>> Insert(String dungeonID) {
            if (MonsterBGone.isVanillaAct(dungeonID)) {
                return SpireReturn.Return(MonsterBGone.getEncounters(dungeonID, ((Map<String, List<MonsterInfo>>)ReflectionHacks.getPrivateStatic(BaseMod.class, "customStrongMonsterEncounters")).get(dungeonID), 1));
            }
            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(HashMap.class, "get");

                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = BaseMod.class,
            method = "getEliteEncounters"
    )
    public static class EliteEncounter {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<List<MonsterInfo>> Insert(String dungeonID) {
            if (MonsterBGone.isVanillaAct(dungeonID)) {
                return SpireReturn.Return(MonsterBGone.getEncounters(dungeonID, ((Map<String, List<MonsterInfo>>)ReflectionHacks.getPrivateStatic(BaseMod.class, "customEliteEncounters")).get(dungeonID), 2));
            }
            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(HashMap.class, "get");

                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = BaseMod.class,
            method = "getBossIDs"
    )
    public static class BossEncounter {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<List<String>> Insert(String dungeonID) {
            if (MonsterBGone.isVanillaAct(dungeonID)) {
                return SpireReturn.Return(MonsterBGone.getBossEncounters(dungeonID, ((Map<String, List<BaseMod.BossInfo>>)ReflectionHacks.getPrivateStatic(BaseMod.class, "customBosses")).get(dungeonID), 3));
            }
            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(HashMap.class, "get");

                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }
}
