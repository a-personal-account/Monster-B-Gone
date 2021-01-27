package monsterbgone;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@SpireInitializer
public class MonsterBGone implements
        PostInitializeSubscriber,
        EditStringsSubscriber {
    public static final Logger logger = LogManager.getLogger(MonsterBGone.class.getSimpleName());

    public static void initialize() {
        BaseMod.subscribe(new MonsterBGone());
    }

    public static String MOD_ID = "monsterbgone";

    public static String makeID(String id) {
        return MOD_ID + ":" + id;
    }

    public static final int ENCOUNTER_TYPES = 4;

    private static Properties properties = new Properties();

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture(MOD_ID + "/images/Badge.png");
        ModPanel modPanel = new ModPanel();
        BaseMod.registerModBadge(
                badgeTexture, "Boss B Gone", "Razash",
                "Tool for removing any modded encounter in the vanilla acts.", modPanel);

        loadConfigData();
    }

    public static Map<String, Map<String, Map<String, Boolean>[]>> vanillaActCustomMonsterConfig = new HashMap<>();

    public static boolean isVanillaAct(String dungeonID) {
        return dungeonID.equals(Exordium.ID) || dungeonID.equals(TheCity.ID) || dungeonID.equals(TheBeyond.ID) || dungeonID.equals(TheEnding.ID);
    }

    public static void addConfigThing(String dungeonID, String encounterID, int mapindex) {
        addConfigThing(dungeonID, encounterID, mapindex, true);
    }

    public static Map<String, String> unknownEncounters = new HashMap<>();
    public static void addConfigThing(String dungeonID, String encounterID, int mapindex, boolean config) {
        String modID;
        if(!unknownEncounters.containsKey(encounterID)) {
            primeRNG();
            try {
                MonsterGroup group = BaseMod.getMonster(encounterID);

                modID = WhatMod.findModName(group.monsters.get(0).getClass());
                for(int i = 1; i < group.monsters.size(); i++) {
                    String tmp = WhatMod.findModName(group.monsters.get(i).getClass());
                    if(tmp != null && !tmp.equals(modID)) {
                        modID = "???";
                        break;
                    }
                }
            } catch (Exception ignore) {
                modID = "???";
            }
            unknownEncounters.put(encounterID, modID);
        } else {
            modID = unknownEncounters.get(encounterID);
        }
        addConfigThing(dungeonID, encounterID, mapindex, config, modID);
    }
    public static void addConfigThing(String dungeonID, String encounterID, int mapindex, boolean config, String modID) {
        //the dungeon ID
        if (!vanillaActCustomMonsterConfig.containsKey(dungeonID)) {
            vanillaActCustomMonsterConfig.put(dungeonID, new HashMap<>());
        }
        //the mod it comes from
        if (!vanillaActCustomMonsterConfig.get(dungeonID).containsKey(modID)) {
            vanillaActCustomMonsterConfig.get(dungeonID).put(modID, new Map[MonsterBGone.ENCOUNTER_TYPES]);
        }
        Map<String, Boolean>[] mapArray = vanillaActCustomMonsterConfig.get(dungeonID).get(modID);

        //what kind of enemy (weak, strong, elite, boss)
        if (mapArray[mapindex] == null) {
            mapArray[mapindex] = new HashMap<>();
        }

        //the encounter itself with its corresponding config
        if (!mapArray[mapindex].containsKey(encounterID)) {
            mapArray[mapindex].put(encounterID, config);
        }
    }

    public static void primeRNG() {
        if (AbstractDungeon.monsterRng == null) {
            Settings.seed = 0L;
            AbstractDungeon.generateSeeds();
        }
    }

    public static List<MonsterInfo> getEncounters(String dungeonID, List<MonsterInfo> input, int mapindex) {
        Set<String> exclusions = getExclusions(dungeonID, mapindex);

        List<MonsterInfo> output = new ArrayList<>();
        for (MonsterInfo entry : input) {
            if (!exclusions.contains(entry.name)) {
                output.add(entry);
            }
        }

        return output;
    }
    public static List<String> getBossEncounters(String dungeonID, List<BaseMod.BossInfo> input, int mapindex) {
        Set<String> exclusions = getExclusions(dungeonID, mapindex);

        List<String> output = new ArrayList<>();
        for (BaseMod.BossInfo entry : input) {
            if (!exclusions.contains(entry.id)) {
                output.add(entry.id);
            }
        }

        return output;
    }

    private static Set<String> getExclusions(String dungeonID, int mapindex) {
        Set<String> exclusions = new HashSet<>();

        if(vanillaActCustomMonsterConfig.containsKey(dungeonID)) {
            for (Map<String, Boolean>[] mapmap : vanillaActCustomMonsterConfig.get(dungeonID).values()) {
                if (mapmap[mapindex] != null) {
                    for (Map.Entry<String, Boolean> entry : mapmap[mapindex].entrySet()) {
                        if (!entry.getValue()) {
                            exclusions.add(entry.getKey());
                        }
                    }
                }
            }
        }
        return exclusions;
    }

    private void loadLocStrings(Settings.GameLanguage language) {
        BaseMod.loadCustomStringsFile(UIStrings.class, MonsterBGone.MOD_ID + "/localization/" + language.name().toLowerCase() + "/UIStrings.json");
    }

    private Settings.GameLanguage languageSupport() {
        switch (Settings.language) {
            default:
                return Settings.GameLanguage.ENG;
        }
    }

    @Override
    public void receiveEditStrings() {
        Settings.GameLanguage language = languageSupport();

        // Load english first to avoid crashing if translation doesn't exist for something. Blatantly stolen from Vex.
        loadLocStrings(Settings.GameLanguage.ENG);
        if (!Settings.language.equals(Settings.GameLanguage.ENG)) {
            try {
                loadLocStrings(language);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void clearConfigData() {
        try {
            SpireConfig config = new SpireConfig(MOD_ID, "config", properties);
            String base = makeID("");

            for (Map.Entry<Object, Object> tmp : ((Properties) ReflectionHacks.getPrivate(config, SpireConfig.class, "properties")).entrySet()) {
                if (tmp.getKey() instanceof String && ((String) tmp.getKey()).startsWith(base)) {
                    config.remove((String)tmp.getKey());
                }
            }
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadConfigData() {
        try {
            SpireConfig config = new SpireConfig(MOD_ID, "config", properties);
            config.load();

            String base = makeID("");
            for (Map.Entry<Object, Object> tmp : ((Properties) ReflectionHacks.getPrivate(config, SpireConfig.class, "properties")).entrySet()) {
                if (tmp.getKey() instanceof String && ((String) tmp.getKey()).startsWith(base)) {
                    String[] keys = ((String) tmp.getKey()).split(":");
                    String encounterID = keys[4];
                    for(int i = 5; i < keys.length; i++) {
                        encounterID += ":" + keys[i];
                    }
                    System.out.println(tmp.getKey());
                    System.out.println(keys[1]);
                    System.out.println(keys[2]);
                    System.out.println(keys[3]);
                    System.out.println(encounterID);
                    addConfigThing(keys[1], encounterID, Integer.parseInt(keys[3]), false, keys[2]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveConfigData();
        }
    }

    public static void saveConfigData() {
        try {
            SpireConfig config = new SpireConfig(MOD_ID, "config", properties);

            for(Map.Entry<String, Map<String, Map<String, Boolean>[]>> entry0 : vanillaActCustomMonsterConfig.entrySet()) {
                for(Map.Entry<String, Map<String, Boolean>[]> entry1 : entry0.getValue().entrySet()) {
                    for(int i = 0; i < entry1.getValue().length; i++) {
                        if(entry1.getValue()[i] != null) {
                            for (Map.Entry<String, Boolean> entry2 : entry1.getValue()[i].entrySet()) {
                                String key = makeID(entry0.getKey() + ":" + entry1.getKey() + ":" + i + ":" + entry2.getKey());
                                if (!entry2.getValue()) {
                                    config.setBool(key, false);
                                } else if (config.has(key)) {
                                    config.remove(key);
                                }
                            }
                        }
                    }
                }
            }

            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
