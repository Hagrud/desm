package hagrud.enginering.Logistic;

import hagrud.devent.EventSchedulerMod;
import javafx.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class TubeManager {

    // Key : X,Z position, Value : Pos, Length
    private Map<Pair<Integer, Integer>, Map<Integer, Integer>> pipes;

    public TubeManager(){
        pipes = new HashMap<>();
    }

    public int isTop( Map<Integer, Integer> localPipes, int y )
    {
        if (localPipes.containsKey(y) )
            return y;
        return -1;
    }

    public int isBot( Map<Integer, Integer> localPipes, int y )
    {
        for( int key : localPipes.keySet() )
        {
            if ( y == key - localPipes.get(key) + 1 )
                return key;
        }
        return -1;
    }

    public int isIn( Map<Integer, Integer> localPipes, int y )
    {
        for( int key : localPipes.keySet() )
        {
            if( y <= key && y + localPipes.get(key) > key )
                return key;
        }
        return -1;
    }

    public void addTube(int x, int y, int z ) {
        Pair<Integer,Integer> xz = new Pair<>(x,z);

        if( pipes.containsKey( xz ) )
        {
            Map<Integer, Integer> localPipes = pipes.get( xz );

            int ktop = isTop( localPipes, y-1 );
            int kbot = isBot( localPipes, y+1 );

            if( ktop > -1 ){
                int length = localPipes.remove( ktop );
                if( kbot > -1 )
                {
                    localPipes.put( kbot, localPipes.get(kbot)+1+length);
                } else {
                    localPipes.put( y, length+1 );
                }

            } else if( kbot > -1)
            {
                localPipes.put( kbot, localPipes.get(kbot)+1 );
            } else {
                localPipes.put( y, 1 );
            }
        }
        else
        {
            Map<Integer,Integer> localPipes = new HashMap<>();
            localPipes.put( y, 1 );
            pipes.put( xz, localPipes );
        }

    }

    public void removeTube(int x, int y, int z){
        Pair<Integer,Integer> xz = new Pair<>(x,z);

        if ( pipes.containsKey( xz ) ){
            Map<Integer,Integer> localPipes = pipes.get(xz);
            int pipeY = isIn( localPipes, y );

            if( pipeY == -1 ) {
                System.out.println("Error remove pipe that does not exist");
                return;
            }

            int length = localPipes.remove( pipeY );

            int l0 = pipeY - y;
            int l1 = length - 1 - l0;

            int ny = y-1;

            if ( l0 > 0 )
                localPipes.put( pipeY, l0 );

            if ( l1 > 0 )
                localPipes.put( ny, l1 );

            if( localPipes.isEmpty() )
                pipes.remove( xz );
        }

    }

    public boolean tryPlacingStackInTube(World world, ItemStack stack, int x, int y, int z)
    {
        Pair<Integer,Integer> xz = new Pair<>(x,z);
        if( pipes.containsKey(xz) && pipes.get(xz).containsKey(y) )
        {
            int height = pipes.get(xz).get(y);
            DelayedTubeEvent event = new DelayedTubeEvent( world, x, y, z, height, stack );

            float a = 0.1f;
            float t = (float) Math.sqrt( 2 * height / a );

            long delay = (long) (t * 20);

            EventSchedulerMod.instance.scheduleEventIn( event, delay);
            return true;
        }

        return false;
    }

}
