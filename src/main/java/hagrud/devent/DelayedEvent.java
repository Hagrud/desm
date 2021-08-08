package hagrud.devent;

import cpw.mods.fml.common.eventhandler.Event;

public abstract class DelayedEvent extends Event implements IStorable{

    public abstract String getRegistryKey();

}
