package com.floweytf.rebark;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DataReloadListener extends JsonReloadListener {
    private static DataReloadListener INSTANCE;
    public static DataReloadListener getInstance() {
        if(INSTANCE == null)
            INSTANCE = new DataReloadListener();
        return INSTANCE;
    }


    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation STRIP_LIST_LOCATION = new ResourceLocation(RebarkMain.MODID, "rebark_data/strip_list.json");
    private static final ResourceLocation REBARK_LIST_LOCATION = new ResourceLocation(RebarkMain.MODID, "rebark_data/strip_list.json");
    private static final Gson GSON = new Gson();

    public DataReloadListener() {
        super(GSON, "rebark_data");
    }

    boolean isStripListWhitelist = false;
    boolean isRebarkListWhitelist = false;
    private final Set<Block> stripList = new HashSet<>();
    private final Set<ResourceLocation> stripTagList = new HashSet<>();

    private final Set<Block> rebarkList = new HashSet<>();
    private final Set<ResourceLocation> rebarkTagList = new HashSet<>();

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, IResourceManager iResourceManager, IProfiler iProfiler) {
        try {
            // clear all entries
            stripList.clear();
            stripTagList.clear();
            rebarkList.clear();
            rebarkTagList.clear();

            parseJson(STRIP_LIST_LOCATION, stripList, stripTagList, (b) -> isStripListWhitelist = b, iResourceManager);
            parseJson(REBARK_LIST_LOCATION, rebarkList, rebarkTagList, (b) -> isRebarkListWhitelist = b, iResourceManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseJson(ResourceLocation rl, Set<Block> a, Set<ResourceLocation> b, Consumer<Boolean> flagSetter, IResourceManager iResourceManager) throws IOException {
        IResource resource = iResourceManager.getResource(rl);
        JsonElement json = GSON.fromJson(new InputStreamReader(resource.getInputStream()), JsonElement.class);
        String type = json.getAsJsonObject().getAsJsonPrimitive("type").getAsString();
        json.getAsJsonObject().getAsJsonArray("entries").forEach((e) -> parseEntry(
            e,
            a,
            b
        ));

        flagSetter.accept(type.equals("whitelist"));
    }

    private static void parseEntry(JsonElement entry, Set<Block> a, Set<ResourceLocation> b) {
        JsonObject object = entry.getAsJsonObject();
        boolean isTag = object.has("tag");
        boolean isBlock = object.has("block");
        if(isTag && isBlock) {
            LOGGER.error("Entry has both tag and block");
            return;
        }

        if(isTag) {
            // parse the tag
            ResourceLocation t = ResourceLocation.tryParse(object.getAsJsonPrimitive("tag").getAsString());
            b.add(t);
            return;
        }

        if(isBlock) {
            Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(object.getAsJsonPrimitive("tag").getAsString()));
            a.add(block);
        }

        LOGGER.error("Empty entry");
    }

    boolean testStrip(Block block) {
        // test if block is strippable 
        if(stripList.contains(block))
            return isStripListWhitelist;
        if(block.getTags().stream().anyMatch(stripTagList::contains))
            return isStripListWhitelist;
        return !isStripListWhitelist;
    }

    boolean testRebark(Block block) {
        // test if block is rebark-able
        // the block supplied should be the resulting block after the rebark
        if(rebarkList.contains(block))
            return isRebarkListWhitelist;
        if(block.getTags().stream().anyMatch(rebarkTagList::contains))
            return isRebarkListWhitelist;
        return !isRebarkListWhitelist;
    }
}
