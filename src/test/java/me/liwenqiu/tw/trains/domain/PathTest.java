package me.liwenqiu.tw.trains.domain;

import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author liwenqiu@gmail.com
 */
public class PathTest {

    @Test
    public void testExtendPath() {
        Path head = new Path("A", "B", 500).extendPath("C", 100).extendPath("D", 50);
        assertEquals("A->B->C->D", head.toString());
    }

    @Test
    public void testCalculateDistanceToTail() {
        Path p = new Path("A", "B", 1000);
        p.extendPath("C", 500);
        assertEquals(1500, p.calculateDistanceToTail());

        p.extendPath("D", 200);
        assertEquals(1700, p.calculateDistanceToTail());
    }

    @Test
    public void testGetTailPath() {
        Path p = new Path("A", "B", 1000);
        p.extendPath("C", 200);
        p.extendPath("D", 100);

        Path tail = p.getTailPath();
        assertNotNull(tail);
        assertEquals("C", tail.getStartTownName());
        assertEquals("D", tail.getEndTownName());
        assertEquals(100, tail.getDistance());
    }
}
