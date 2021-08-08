package hagrud.enginering;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hagrud.devent.EventSchedulerMod;
import hagrud.enginering.Logistic.DelayedTubeEvent;
import hagrud.enginering.Logistic.TubeManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Mod( modid = EngineringMod.MODID, name = "Engineering mod" )
public class EngineringMod {

    public static final String MODID = "engineering";
    public TubeManager manager;

    @Mod.Instance(MODID)
    public static EngineringMod instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        EngineeringBlocks.preInitBlocks();
        EngineeringBlocks.registerBlocks();

        manager = (TubeManager) EventSchedulerMod.registerAndInstanciateManager( MODID + "_tube_manager", TubeManager.class );
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        EventSchedulerMod.registerDelayedEventClass(DelayedTubeEvent.REGISTRATION_KEY, DelayedTubeEvent.class);
    }

    @SubscribeEvent
    public void itemUse(PlayerInteractEvent event){
        if( !event.world.isRemote &&
                !event.entityPlayer.isSneaking() &&
                event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK &&
                event.entityPlayer.getHeldItem() != null ){

            if( event.world.getBlock( event.x, event.y, event.z ) == EngineeringBlocks.tubeBlock ){
                if ( manager.tryPlacingStackInTube( event.world, event.entityPlayer.getHeldItem(), event.x, event.y, event.z ) )
                {
                    event.entityPlayer.getHeldItem().stackSize = 0;
                    event.setCanceled(true);
                }
            }

        }
    }

    @SubscribeEvent
    public void tubeEvent( DelayedTubeEvent event ){
        World world = event.getWorld();
        EntityItem entity = new EntityItem( world, event.getX(), event.getY()-event.getHeight()+0.5, event.getZ(), event.getStack() );
        boolean res = world.spawnEntityInWorld( entity );
    }

}