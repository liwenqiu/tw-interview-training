package me.liwenqiu.tw.trains.domain;

import me.liwenqiu.tw.trains.exception.TrainRuntimeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author liwenqiu@gmail.com
 */
public class TownTest {

    @Test
    public void testGetName() {
        Town town = new Town("A");
        assertEquals("A", town.getName());
    }

    @Test
    public void testCreateRouteToTown() {
        Town townA = new Town("A");
        Town townB = new Town("B");

        townA.createRouteToTown(townB, 100);

        assertTrue(townA.getAdjacentTowns().contains(townB));
    }

    @Test
    public void testGetDistanceTo() {
        Town townA = new Town("A");
        Town townB = new Town("B");

        townA.createRouteToTown(townB, 100);

        assertEquals(100, townA.getDistanceTo(townB));
    }

    @Test
    public void testGetDistanceToNotFound() {
        try {
            Town townA = new Town("A");
            Town townB = new Town("B");

            townA.getDistanceTo(townB);
            fail("This should fail");
        } catch (TrainRuntimeException e) {
            assertEquals("NO SUCH ROUTE", e.getMessage());
        }
    }
}
