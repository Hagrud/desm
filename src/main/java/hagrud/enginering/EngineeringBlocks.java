package hagrud.enginering;

import cpw.mods.fml.common.registry.GameRegistry;
import hagrud.enginering.blocks.TubeBlock;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class EngineeringBlocks {

    public static Block tubeBlock;

    protected static void preInitBlocks()
    {
        tubeBlock = new TubeBlock().setBlockName("tube")
                .setBlockTextureName(EngineringMod.MODID + ":block_tube")
                .setCreativeTab(CreativeTabs.tabFood);
    }

    protected static void registerBlocks()
    {
        GameRegistry.registerBlock( tubeBlock, "block_tube" );
    }


}
