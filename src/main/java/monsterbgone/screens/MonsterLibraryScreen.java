package monsterbgone.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import monsterbgone.MonsterBGone;
import monsterbgone.screens.labels.CollapsibleLabel;
import monsterbgone.screens.labels.ModCheckboxLabel;
import monsterbgone.screens.labels.MonsterCheckboxLabel;
import monsterbgone.screens.labels.TypeCheckboxLabel;

import java.util.*;

public class MonsterLibraryScreen implements MonsterTabBarListener, ScrollBarListener {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(MonsterBGone.makeID("menuscreen"));

    public static final String[] TEXT = uiStrings.TEXT;

    public static final float INDENT = 30F * Settings.scale;
    public static final float LINEHEIGHT = 30F * Settings.scale;

    private static float drawStartX;
    private static float drawStartY = Settings.HEIGHT * 0.66F;

    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F, currentDiffY = 0.0F;

    private float scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
    private float scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;

    public MonsterTabBar tabBar;

    public Map<String, Boolean> modfilters = new HashMap<>();
    private Map<String, Boolean> actfilters = new HashMap<>();

    public MenuCancelButton button = new MenuCancelButton();

    private ScrollBar scrollBar;
    private ModSidebar sidebar;

    private int selectionIndex = 0;
    private Color highlightBoxColor = new Color(1.0F, 0.95F, 0.5F, 0.0F);

    private List<CollapsibleLabel> visibleEncounters;
    public EncounterPreview encounterPreview = null;
    public Set<String> renderBlacklist = new HashSet<>();

    public static class Enum {
        @SpireEnum
        public static MainMenuScreen.CurScreen MONSTERBGONE;
    }

    public MonsterLibraryScreen() {
        drawStartX = Settings.WIDTH * 1F / 4F;

        visibleEncounters = new ArrayList<>();
        visibleEncounters.add(new CollapsibleLabel(Exordium.ID, Exordium.NAME));
        visibleEncounters.add(new CollapsibleLabel(TheCity.ID, TheCity.NAME));
        visibleEncounters.add(new CollapsibleLabel(TheBeyond.ID, TheBeyond.NAME));
        visibleEncounters.add(new CollapsibleLabel(TheEnding.ID, TheEnding.NAME));

        this.tabBar = new MonsterTabBar(this, this.visibleEncounters);
        this.scrollBar = new ScrollBar(this);

        for (Map.Entry<String, Map<String, Map<String, Boolean>[]>> entry0 : MonsterBGone.vanillaActCustomMonsterConfig.entrySet()) {
            for (String key : entry0.getValue().keySet()) {
                modfilters.putIfAbsent(key, true);
            }
            actfilters.put(entry0.getKey(), true);
        }
        this.sidebar = new ModSidebar(this, modfilters);
        applyFilter();
        calculateScrollBounds();
    }

    public void open() {
        this.button.show(CardLibraryScreen.TEXT[0]);
        this.currentDiffY = this.scrollLowerBound;
        CardCrawlGame.mainMenuScreen.screen = Enum.MONSTERBGONE;
        CardCrawlGame.mainMenuScreen.darken();
    }

    public void update() {
        this.tabBar.update();

        boolean isScrollBarScrolling = this.scrollBar.update();
        if (!isScrollBarScrolling)
            updateScrolling();

        this.button.update();
        if (this.button.hb.clicked || InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            this.button.hb.clicked = false;
            this.button.hide();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.mainMenuScreen.lighten();
        }

        int offset = 0;
        for (CollapsibleLabel cl : visibleEncounters) {
            if (!cl.source.isEmpty()) {
                offset = cl.update(this.currentDiffY + MonsterTabBar.BASELINE, offset, this);
            }
        }
        this.sidebar.update(this.currentDiffY + MonsterTabBar.BASELINE);

        if (encounterPreview != null) {
            try {
                if (encounterPreview.update()) {
                    encounterPreview = null;
                }
            } catch (Exception e) {
                encounterPreviewError();
            }
        }
    }

    private void updateScrolling() {
        int y = InputHelper.mY;
        if (!this.grabbedScreen) {
            if (InputHelper.scrolledDown) {
                this.currentDiffY += Settings.SCROLL_SPEED;
            } else if (InputHelper.scrolledUp) {
                this.currentDiffY -= Settings.SCROLL_SPEED;
            }
            if (InputHelper.justClickedLeft) {
                this.grabbedScreen = true;
                this.grabStartY = y - this.currentDiffY;
            }
        } else if (InputHelper.isMouseDown) {
            this.currentDiffY = y - this.grabStartY;
        } else {
            this.grabbedScreen = false;
        }
        resetScrolling();
        updateBarPosition();
    }

    private void calculateScrollBounds() {
        int lines = 0;

        for (CollapsibleLabel entry0 : visibleEncounters) {
            if (!entry0.source.isEmpty()) {
                lines += 2;
                for (ModCheckboxLabel entry1 : entry0.source) {
                    lines++;
                    for (TypeCheckboxLabel entry2 : entry1.source) {
                        if (entry2 != null) {
                            lines++;
                            lines += entry2.source.size();
                        }
                    }
                }
            }
        }
        if (lines > 20) {
            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + lines * LINEHEIGHT;
        } else {
            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
        }
    }

    private void resetScrolling() {
        if (this.currentDiffY < this.scrollLowerBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);
        } else if (this.currentDiffY > this.scrollUpperBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);
        }
    }

    public void render(SpriteBatch sb) {
        this.scrollBar.render(sb);

        renderGroup(sb);
        this.tabBar.render(sb);
        this.sidebar.render(sb);

        this.button.render(sb);

        if (encounterPreview != null) {
            try {
                encounterPreview.render(sb);
            } catch (Exception e) {
                encounterPreviewError();
            }
        }
    }

    private void renderGroup(SpriteBatch sb) {
        int offset = 0;
        for (CollapsibleLabel cl : visibleEncounters) {
            if (!cl.source.isEmpty()) {
                offset = cl.render(sb, drawStartX, drawStartY + this.currentDiffY, offset);
            }
        }
    }

    public void didChangeTab(MonsterTabBar tabBar, boolean[] newSelection) {
        for (int i = 1; i < newSelection.length; i++) {
            actfilters.put(actNumToID(i - 1), newSelection[i]);
        }

        applyFilter();
        calculateScrollBounds();
    }
    public void didChangeMod(List<ModSidebar.ModEntry> entries) {
        for(int i = 1; i < entries.size(); i++) {
            modfilters.put(entries.get(i).key, entries.get(i).selected);
        }

        applyFilter();
        calculateScrollBounds();
    }

    private void applyFilter() {
        for (Map.Entry<String, Boolean> filterentry : actfilters.entrySet()) {
            CollapsibleLabel cl = visibleEncounters.get(actIDtoNum(filterentry.getKey()));
            cl.clear();
            visibleEncounters.get(actIDtoNum(filterentry.getKey())).clear();
            if (filterentry.getValue()) {
                if (MonsterBGone.vanillaActCustomMonsterConfig.containsKey(filterentry.getKey())) {
                    List<ModCheckboxLabel> tmp = cl.source;
                    for (Map.Entry<String, Map<String, Boolean>[]> entry0 : MonsterBGone.vanillaActCustomMonsterConfig.get(filterentry.getKey()).entrySet()) {
                        if (modfilters.get(entry0.getKey())) {
                            tmp.add(new ModCheckboxLabel(entry0.getKey(), entry0.getValue()));
                        }
                    }
                }
            }
        }
    }

    private String actNumToID(int num) {
        switch (num) {
            case 0:
                return Exordium.ID;
            case 1:
                return TheCity.ID;
            case 2:
                return TheBeyond.ID;
            case 3:
                return TheEnding.ID;
        }
        throw new RuntimeException("invalid actnumber: " + num);
    }

    private int actIDtoNum(String id) {
        switch (id) {
            case Exordium.ID:
                return 0;
            case TheCity.ID:
                return 1;
            case TheBeyond.ID:
                return 2;
            case TheEnding.ID:
                return 3;
        }
        throw new RuntimeException("invalid act ID: " + id);
    }

    public void scrolledUsingBar(float newPercent) {
        this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);
        this.scrollBar.parentScrolledToPercent(percent);
    }

    public void previewEncounter(MonsterCheckboxLabel mcl) {
        float alpha = 0F;
        if (encounterPreview != null) {
            if (encounterPreview.parent == mcl) {
                encounterPreview.ending = false;
                return;
            }
            encounterPreview.dispose();
            alpha = encounterPreview.alpha;
        }
        encounterPreview = new EncounterPreview(this, mcl, alpha);
    }

    private void encounterPreviewError() {
        renderBlacklist.add(encounterPreview.parent.key);
        encounterPreview.dispose();
        encounterPreview = null;
    }
}