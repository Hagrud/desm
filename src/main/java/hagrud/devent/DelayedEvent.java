package hagrud.devent;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.nbt.NBTTagCompound;

public abstract class DelayedEvent extends Event {

    public abstract String getRegistryKey();

    public void postInit() {}

    public abstract void readFromNBT( NBTTagCompound nbt );

    public abstract NBTTagCompound writeToNBT(NBTTagCompound nbt);

}
