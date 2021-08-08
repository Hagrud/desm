package hagrud.devent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import java.io.*;
import java.util.*;

@Mod( modid = EventSchedulerMod.MODID, name = "Event Scheduler mod")
public class EventSchedulerMod {

    public static final String MODID = "eventscheduler";
    public static final String SAVEFOLDER = MODID + "/datas";

    @Mod.Instance(MODID)
    public static EventSchedulerMod instance;

        // Registry
    private final Map<String, Class<? extends DelayedEvent>> registry = new HashMap<>();

        // Events
    private SortedMap< Long, List<DelayedEvent> > events = new TreeMap<>();
    private long tick = 0;
    private boolean isLoaded = false;

    public static void registerDelayedEventClass( String key, Class<? extends DelayedEvent> type ){
        // TODO add duplicate check
        instance.registry.put( key, type );
    }

    public long getTick() { return tick; }

    private void scheduleEventAt( DelayedEvent event, Long target )
    {
        if ( target > tick ) {
            if ( events.containsKey( target ) ){
                events.get( target ).add( event );
            }else{
                List<DelayedEvent> list = new ArrayList<>();
                list.add( event );
                events.put( target, list );
            }
        } else {
            System.out.println( "... Invalid tick target" );
        }
    }

    public void scheduleEventIn( DelayedEvent event, Long delay ) {
        scheduleEventAt( event, tick + delay );
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register( this );
        MinecraftForge.EVENT_BUS.register( this );
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        registerDelayedEventClass( CyclicTickEvent.REGISTRATION_KEY, CyclicTickEvent.class );
    }

    @SubscribeEvent
    public void tickEvent( TickEvent.WorldTickEvent event){
        if( event.side.isClient() )
            return;

        tick++;
        if ( !events.isEmpty() && tick == events.firstKey() )
        {
            List<DelayedEvent> es = events.remove(tick);

            for(DelayedEvent e : es){
                if ( !e.isCanceled() ){
                    FMLCommonHandler.instance().bus().post( e );
                }
            }

        }
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        events = new TreeMap<>();
        tick = 0;

        File folder = new File(DimensionManager.getCurrentSaveRootDirectory(), SAVEFOLDER);
        File dataFile = new File( folder, "events.nbt" );

        NBTTagCompound dataScheduler = null;
        try {
            FileInputStream is = new FileInputStream( dataFile );
            dataScheduler = CompressedStreamTools.readCompressed( is );
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (dataScheduler != null)
        {
            tick = dataScheduler.getLong( "t" );
            int n = dataScheduler.getInteger( "n" );

            for( int i = 0; i < n; i++)
            {
                NBTTagCompound dataEvent = dataScheduler.getCompoundTag( "e" + i );
                long tickEvent = dataScheduler.getLong( "t" + i );
                String registryKeyEvent = dataScheduler.getString( "r" + i );

                try {
                    System.out.println( "Build event ! ");
                    DelayedEvent savedEvent = registry.get(registryKeyEvent).newInstance();
                    savedEvent.readFromNBT( dataEvent );
                    scheduleEventAt( savedEvent, tickEvent );
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Mod.EventHandler
    public void serverUnload(FMLServerStoppingEvent event) {
        // Get all data
        NBTTagCompound dataScheduler = new NBTTagCompound();
        dataScheduler.setLong( "t", tick );

        int i = 0;
        for ( Long key : events.keySet() )
        {
            List<DelayedEvent> rawList = events.get( key );
            List<DelayedEvent> activeList = new ArrayList<>();

            for ( DelayedEvent e : events.get( key ))
            {
                if(!e.isCanceled())
                {
                    NBTTagCompound dataEvent = new NBTTagCompound();
                    dataEvent = e.writeToNBT( dataEvent );

                    dataScheduler.setLong( "t" + i, key );
                    dataScheduler.setString( "r" + i, e.getRegistryKey() );
                    dataScheduler.setTag( "e" + i, dataEvent );

                    i++;
                }
            }
        }
        dataScheduler.setInteger( "n", i );

        // Save to file
        File folder = new File(DimensionManager.getCurrentSaveRootDirectory(), SAVEFOLDER);
        File dataFile = new File( folder, "events.nbt" );

        try {
            folder.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println( "save : " + dataScheduler );
        try {
            FileOutputStream os = new FileOutputStream(dataFile);
            CompressedStreamTools.writeCompressed( dataScheduler, os );
            os.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void serverLoadFinish(FMLServerStartedEvent event) {
        for( Long key : events.keySet() )
        {
            for( DelayedEvent ev : events.get( key ) )
            {
                ev.postInit();
            }
        }
    }

    @SubscribeEvent
    public void eventTest( CyclicTickEvent event )
    {
        System.out.println( "Print tick : " + tick );
        scheduleEventIn( new CyclicTickEvent( event.getDelta() ), event.getDelta() );
    }
}
