package me.liwenqiu.tw.trains.domain;

import me.liwenqiu.tw.trains.exception.NoSuchRouteException;

import java.util.*;

/**
 * @author liwenqiu@gmail.com
 */
public class Town {

    private final String name;

    //  Neighbor Town => Distance
    private final Map<Town, Integer> neighbors;

    // Use RouteMap.newTown(name) to create town
    protected Town(String name) {
        this.name = name;
        this.neighbors = new HashMap<Town, Integer>();
    }

    public String getName() {
        return this.name;
    }

    public Set<Town> getAdjacentTowns() {
        return this.neighbors.keySet();
    }

    public int getDistanceTo(Town target) {
        Integer distance = this.neighbors.get(target);
        if (distance == null) {
            throw new NoSuchRouteException();
        }
        return distance;
    }

    protected void createRouteToTown(Town target, Integer distance) {
        this.neighbors.put(target, distance);
    }


    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Town) {
            return this.name.equals(((Town) obj).name);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
