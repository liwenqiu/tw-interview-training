package me.liwenqiu.tw.trains.domain;

import com.sun.glass.ui.TouchInputSupport;
import me.liwenqiu.tw.trains.exception.TrainRuntimeException;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author liwenqiu@gmail.com
 */
public class RouteMapTest {

    @Test
    public void testParseTokenToStartTown() {
        String token = "AB5";

        RouteMap m = new RouteMap();

        assertEquals("A", m.parseStartTown(token));
    }

    @Test
    public void testParseTokenToEndTown() {
        String token = "AB5";

        RouteMap m = new RouteMap();

        assertEquals("B", m.parseEndTown(token));
    }

    @Test
    public void testParseTokenToDistance() {
        String token = "AB5";

        RouteMap m = new RouteMap();

        assertEquals(5, m.parseTokenToDistance(token));
    }


    @Test
    public void testAllFunctions() {
        List<String> elements = new ArrayList<String>();
        String[] token = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7".split(",");
        for (String t : token) {
            elements.add(t);
        }

        RouteMap map = RouteMap.build(elements);

        // #1
        assertEquals(9, map.calculateDistanceOfTowns("A", "B", "C"));
        // #2
        assertEquals(5, map.calculateDistanceOfTowns("A", "D"));
        // #3
        assertEquals(13, map.calculateDistanceOfTowns("A", "D", "C"));
        // #4
        assertEquals(22, map.calculateDistanceOfTowns("A", "E", "B", "C", "D"));
        // #5
        try {
            map.calculateDistanceOfTowns("A", "E", "D");
            fail("This should fail");
        } catch (TrainRuntimeException e) {
            assertEquals("NO SUCH ROUTE", e.getMessage());
        }
        // #6
        assertEquals(2, map.findNumberOfPathsToTownWithMaxStopLimit("C", "C", 3));
        // #7
        assertEquals(3, map.findNumberOfPathsToTownAtSpecifiedStop("A", "C", 4));
        // #8
        assertEquals(9, map.findShortestPathBetweenTowns("A", "C"));
        // #9
        assertEquals(9, map.findShortestPathBetweenTowns("B", "B"));
        // #10
        assertEquals(7, map.findNumberOfPathBetweenTownsWithMaxDistanceLimit("C", "C", 30));
    }

    @Test
    public void testGetTown() {
        RouteMap m = RouteMap.build(Arrays.asList("AB5"));
        Town town = m.getTown("A");

        assertNotNull(town);
        assertEquals("A", town.getName());
    }

    @Test
    public void testGetTownNotFound() {
        RouteMap m = RouteMap.build(Arrays.asList("AB5"));
        try {
            m.getTown("C");
            fail("This should fail");
        } catch (TrainRuntimeException e) {
            assertEquals("NO SUCH TOWN: C", e.getMessage());
        }
    }

    @Test
    public void testAddRoute() {
        RouteMap m = RouteMap.build(Arrays.asList("AB5"));
        m.addRoute("A", "C", 6);

        assertNotNull(m.getTown("C"));
        assertTrue(m.getTown("A").getAdjacentTowns().contains(new Town("C")));
    }

    @Test
    public void testCalculateDistanceOfTowns() {
        RouteMap m = RouteMap.build(Arrays.asList("AB5", "AC8", "CD9", "DE10"));
        assertEquals(27, m.calculateDistanceOfTowns("A", "C", "D", "E"));
    }

    @Test
    public void testcalculateDurationOfTowns() {
        RouteMap m = RouteMap.build(Arrays.asList("AB5", "BC4", "CD8", "DC8"));
        assertEquals(11, m.calculateDurationOfTowns("A", "B", "C"));
    }

    @Test
    public void testConstructPathFromParentTable() {
        RouteMap map = RouteMap.build(Arrays.asList("SA6", "SB2", "BA3", "AF1", "BF5"));
        Town a = map.getTown("A");
        Town b = map.getTown("B");
        Town s = map.getTown("S");
        Town f = map.getTown("F");

        Map<Town, Town> parents = new HashMap<Town, Town>();
        parents.put(f, a);
        parents.put(a, b);
        parents.put(b, s);

        Path path = map.constructPathFromParentTable(parents, s, f);
        assertNotNull(path);
        assertEquals("S->B->A->F", path.toString());
    }

    @Test
    public void testFindShortestPathBetweenTowns() {
        RouteMap map = RouteMap.build(Arrays.asList("SA6", "SB2", "BA3", "AF1", "BF5"));

        assertEquals(6, map.findShortestPathBetweenTowns("S", "F"));
    }
}
