package hagrud.devent;

import net.minecraft.nbt.NBTTagCompound;

public class CyclicTickEvent extends DelayedEvent{

    private long delta = 0;
    public static final String REGISTRATION_KEY = "cyclictickevent";

    public CyclicTickEvent() { super(); }

    public CyclicTickEvent( long delay )
    {
        super();
        delta = delay;
    }

    public long getDelta() { return delta; }

    @Override
    public String getRegistryKey() {
        return REGISTRATION_KEY;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        delta = nbt.getInteger( "t" );
    }

    @Override
    public void postLoad() {}

    @Override
    public NBTTagCompound writeToNBT( NBTTagCompound nbt )
    {
        nbt.setLong( "t", delta );
        return nbt;
    }
}
