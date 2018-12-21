package club.sk1er.mods.levelhead.display;

import java.util.ArrayList;
import java.util.UUID;

import club.sk1er.mods.levelhead.Levelhead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

public class TabDisplay extends LevelheadDisplay {

	private Levelhead levelhead;

    public TabDisplay(Levelhead levelhead, DisplayConfig config) {
        super(DisplayPosition.TAB, config);
        this.levelhead = levelhead;
    }

    @Override
    public void tick() {

        for (NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            UUID id = networkPlayerInfo.getGameProfile().getId();
            if (id != null)
                if (!cache.containsKey(id))
                    levelhead.fetch(id, this, false);
        }
    }

    @Override
    public void checkCacheSize() {
        if (cache.size() > Math.max(levelhead.getDisplayManager().getMasterConfig().getPurgeSize(), 150)) {
            ArrayList<UUID> safePlayers = new ArrayList<>();
            for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                UUID id = info.getGameProfile().getId();
                if (existedMorethan5Seconds.contains(id)) {
                    safePlayers.add(id);
                }
            }
            existedMorethan5Seconds.clear();
            existedMorethan5Seconds.addAll(safePlayers);


            for (UUID uuid : cache.keySet()) {
                if (!safePlayers.contains(uuid)) {
                    cache.remove(uuid);
                    trueValueCache.remove(uuid);
                }
            }
        }
    }

    @Override
    public void onDelete() {
        cache.clear();
        trueValueCache.clear();
        existedMorethan5Seconds.clear();
    }
}
