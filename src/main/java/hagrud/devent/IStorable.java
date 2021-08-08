package hagrud.devent;

import net.minecraft.nbt.NBTTagCompound;

public interface IStorable {

    void readFromNBT( NBTTagCompound nbt );

    NBTTagCompound writeToNBT(NBTTagCompound nbt);

    void postLoad();

}
