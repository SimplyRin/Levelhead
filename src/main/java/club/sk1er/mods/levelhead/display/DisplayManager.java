package club.sk1er.mods.levelhead.display;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import club.sk1er.mods.levelhead.Levelhead;
import club.sk1er.mods.levelhead.config.MasterConfig;
import club.sk1er.mods.levelhead.utils.JsonHolder;

public class DisplayManager {

	private Levelhead levelhead;

    private Gson GSON = new Gson();
    private List<AboveHeadDisplay> aboveHead = new ArrayList<>();
    private LevelheadDisplay chat = null;
    private TabDisplay tab = null;
    private MasterConfig config = new MasterConfig();
    private File file;

    public DisplayManager(Levelhead levelhead, JsonHolder source, File file) {
    	this.levelhead = levelhead;
        this.file = file;
        if (source.has("master")) {
            this.config = GSON.fromJson(source.optJsonObject("master").getObject(), MasterConfig.class);
        } else {
            this.config = new MasterConfig();
        }
        for (JsonElement head : source.optJSONArray("head")) {
            aboveHead.add(new AboveHeadDisplay(levelhead, GSON.fromJson(head.getAsJsonObject(), DisplayConfig.class)));
        }
        if (source.has("chat")) {
            this.chat = new ChatDisplay(levelhead, GSON.fromJson(source.optJsonObject("chat").getObject(), DisplayConfig.class));
        }
        if (source.has("tab")) {
            this.tab = new TabDisplay(levelhead, GSON.fromJson(source.optJsonObject("tab").getObject(), DisplayConfig.class));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));

        if (aboveHead.isEmpty()) {
            aboveHead.add(new AboveHeadDisplay(levelhead, new DisplayConfig()));
        }

        if (tab == null) {
            DisplayConfig config = new DisplayConfig();
            config.setType("QUESTS");
            tab = new TabDisplay(levelhead, config);
        }

        adjustIndexes();

        if (chat == null) {
            DisplayConfig config = new DisplayConfig();
            config.setType("GUILD_NAME");
            chat = new ChatDisplay(levelhead, config);
        }


    }

    public void adjustIndexes() {
        for (int i = 0; i < aboveHead.size(); i++) {
            aboveHead.get(i).setBottomValue(i == 0);
            aboveHead.get(i).setIndex(i);
        }
    }

    public List<AboveHeadDisplay> getAboveHead() {
        return aboveHead;
    }

    public LevelheadDisplay getChat() {
        return chat;
    }

    public LevelheadDisplay getTab() {
        return tab;
    }

    public MasterConfig getMasterConfig() {
        return config;
    }

    public void tick() {
        if (!levelhead.getDisplayManager().getMasterConfig().isEnabled()) {
            return;
        }

        aboveHead.forEach(LevelheadDisplay::tick);
        if (tab != null)
            tab.tick();
        if (chat != null)
            chat.tick();
    }

    public void checkCacheSizes() {
        aboveHead.forEach(LevelheadDisplay::checkCacheSize);
        if (tab != null) {
            tab.checkCacheSize();
        }
        if (chat != null) {
            chat.checkCacheSize();
        }
    }

    public void save() {
        JsonHolder jsonHolder = new JsonHolder();
        jsonHolder.put("master", new JsonHolder(GSON.toJson(getMasterConfig())));
        if (tab != null) {
            jsonHolder.put("tab", new JsonHolder(GSON.toJson(tab.getConfig())));
        }
        if (chat != null) {
            jsonHolder.put("chat", new JsonHolder(GSON.toJson(chat.getConfig())));
        }
        JsonArray head = new JsonArray();
        for (AboveHeadDisplay aboveHeadDisplay : this.aboveHead) {
            head.add(new JsonHolder(GSON.toJson(aboveHeadDisplay.getConfig())).getObject());
        }
        jsonHolder.put("head", head);
        try {
            FileUtils.writeStringToFile(this.file, jsonHolder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clearCache() {
        for (AboveHeadDisplay aboveHeadDisplay : this.aboveHead) {
            aboveHeadDisplay.cache.clear();
            aboveHeadDisplay.trueValueCache.clear();
        }
        if (tab != null) {
            tab.cache.clear();
            tab.trueValueCache.clear();
        }
        if (chat != null) {
            chat.cache.clear();
            chat.cache.clear();
        }
    }
}
