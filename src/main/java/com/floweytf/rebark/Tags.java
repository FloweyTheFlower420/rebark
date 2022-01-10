package com.floweytf.rebark;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;

public class Tags {
    private static final ITag.INamedTag<Block> REBARK_BLACKLIST_TAG = BlockTags.bind(RebarkMain.MODID + ":rebark_blacklist");
    private static final ITag.INamedTag<Block> STRIP_BLACKLIST_TAG = BlockTags.bind(RebarkMain.MODID + ":strip_blacklist");
    private static final ITag.INamedTag<Block> REBARK_WHITELIST_TAG = BlockTags.bind(RebarkMain.MODID + ":rebark_blacklist");
    private static final ITag.INamedTag<Block> STRIP_WHITELIST_TAG = BlockTags.bind(RebarkMain.MODID + ":strip_blacklist");

    public static boolean validateRebark(Block block) {
        boolean passWhitelist;
        if(REBARK_WHITELIST_TAG.getValues().size() == 0) // whitelist isn't used
            passWhitelist = true;
        else
            passWhitelist = REBARK_WHITELIST_TAG.contains(block);

        return passWhitelist && !REBARK_BLACKLIST_TAG.contains(block);
    }

    public static boolean validateStrip(Block block) {
        boolean passWhitelist;
        if(STRIP_WHITELIST_TAG.getValues().size() == 0) // whitelist isn't used
            passWhitelist = true;
        else
            passWhitelist = STRIP_WHITELIST_TAG.contains(block);

        return passWhitelist && !STRIP_BLACKLIST_TAG.contains(block);
    }
}
