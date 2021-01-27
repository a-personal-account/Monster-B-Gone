package monsterbgone.screens.labels;

import basemod.BaseMod;
import monsterbgone.screens.MonsterLibraryScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonsterCheckboxLabel extends CheckboxLabel {
    private Map<String, Boolean> source;
    public String key;

    public MonsterCheckboxLabel(Map<String, Boolean> source, String key, boolean checked) {
        super(BaseMod.getMonsterName(key));
        this.key = key;
        this.checked = checked;
        this.source = source;
    }

    public static List<MonsterCheckboxLabel> fromMap(Map<String, Boolean> source) {
        List<MonsterCheckboxLabel> result = new ArrayList<>();
        for(Map.Entry<String, Boolean> entry : source.entrySet()) {
            result.add(new MonsterCheckboxLabel(source, entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public int update(float y, int offset, MonsterLibraryScreen parent) {
        offset = super.update(y, offset);

        if(!this.hb.hovered) {
            if (parent.encounterPreview != null && parent.encounterPreview.parent == this) {
                parent.encounterPreview.ending = true;
            }
        } else if(parent.tabBar.viewPreviewChecked && (parent.encounterPreview == null || parent.encounterPreview.parent != this) && !parent.renderBlacklist.contains(this.key)) {
            parent.previewEncounter(this);
        }

        return offset;
    }

    @Override
    protected boolean click() {
        this.checked = !this.checked;
        this.updateEntry();
        return true;
    }

    public void updateEntry() {
        source.put(this.key, this.checked);
    }
}
