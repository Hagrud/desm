package hagrud.enginering.blocks;

import hagrud.enginering.EngineringMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class TubeBlock extends Block {

    public TubeBlock() {
        super( Material.iron );
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if ( !world.isRemote )
            EngineringMod.instance.manager.addTube( x, y, z );

        return metadata;
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int metadata) {
        EngineringMod.instance.manager.removeTube( x, y, z );
    }

}
