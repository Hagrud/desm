package hagrud.enginering.Logistic;

import hagrud.devent.DelayedEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class DelayedTubeEvent extends DelayedEvent {

    public static final String REGISTRATION_KEY = "tubeevent";
    private int startZ;
    private int startY;
    private int startX;
    private int totalHeight;
    private ItemStack stack;
    private World world;

    public DelayedTubeEvent(){super();}

    public DelayedTubeEvent( World world, int x, int y, int z, int height, ItemStack s ){
        startX = x;
        startY = y;
        startZ = z;
        totalHeight = height;
        stack = s.copy();
        this.world = world;
    }

    public int getX(){ return startX; }
    public int getY(){ return startY; }
    public int getZ(){ return startZ; }
    public int getHeight(){ return totalHeight; }

    public World getWorld(){ return world; }
    public ItemStack getStack(){ return stack; }

    @Override
    public String getRegistryKey() { return REGISTRATION_KEY; }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        startX = nbt.getInteger( "x" );
        startY = nbt.getInteger( "y" );
        startZ = nbt.getInteger( "z" );
        totalHeight = nbt.getInteger( "h" );
        stack = ItemStack.loadItemStackFromNBT( nbt.getCompoundTag( "s" ) );
        world = DimensionManager.getWorld( nbt.getInteger( "w" ) );
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger( "x", startX );
        nbt.setInteger( "y", startY );
        nbt.setInteger( "z", startZ );
        nbt.setInteger( "h", totalHeight );
        nbt.setInteger( "w", world.provider.dimensionId );

        nbt.setTag( "s", stack.writeToNBT( new NBTTagCompound() ) );

        return nbt;
    }
}
