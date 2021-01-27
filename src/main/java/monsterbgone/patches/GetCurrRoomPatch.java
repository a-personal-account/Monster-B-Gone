package monsterbgone.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import monsterbgone.screens.MonsterLibraryScreen;

@SpirePatch(
        clz= AbstractDungeon.class,
        method="getCurrRoom"
)
public class GetCurrRoomPatch {
    @SpirePrefixPatch
    public static SpireReturn<AbstractRoom> Insert() {
        if(CardCrawlGame.mainMenuScreen.screen == MonsterLibraryScreen.Enum.MONSTERBGONE) {
            AbstractRoom room = new MonsterRoom();
            room.phase = AbstractRoom.RoomPhase.COMPLETE;
            room.monsters = new MonsterGroup(new AbstractMonster[0]);
            return SpireReturn.Return(room);
        }
        return SpireReturn.Continue();
    }
}
