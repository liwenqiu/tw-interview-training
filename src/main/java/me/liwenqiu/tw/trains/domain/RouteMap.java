package me.liwenqiu.tw.trains.domain;

import me.liwenqiu.tw.trains.exception.NoSuchRouteException;
import me.liwenqiu.tw.trains.exception.TownNotFoundException;

import java.util.*;

/**
 * @author liwenqiu@gmail.com
 */
public class RouteMap {

    protected final static int STOP_TIME_PER_STATION = 2;

    // Town Name => Town
    private final Map<String, Town> towns;

    public RouteMap() {
        this.towns = new HashMap<String, Town>();
    }

    public static RouteMap build(List<String> routes) {

        RouteMap map = new RouteMap();
        for (String route : routes) {
            route = route.trim();
            if (route.length() < 3) {
                throw new IllegalArgumentException("Invalid Route Format: " + route);
            }
            // [a-zA-Z][a-zA-Z]\d
            map.addRoute(map.parseStartTown(route), map.parseEndTown(route), map.parseTokenToDistance(route));
        }

        return map;
    }

    public String parseStartTown(String token) {
        return token.substring(0, 1);
    }

    public String parseEndTown(String token) {
        return token.substring(1, 2);
    }

    public int parseTokenToDistance(String token) {
        return Integer.valueOf(token.substring(2));
    }

    public Town getTown(String name) {

        Town town = towns.get(name);
        if (town == null) {
            throw new TownNotFoundException(name);
        }
        return town;
    }

    public Town newTown(String name) {
        Town town = towns.get(name);
        if (town == null) {
            town = new Town(name);
            towns.put(name, town);
        }
        return town;
    }

    public void addRoute(String srcTownName, String destTownName, Integer distance) {

        Town srcTown = towns.get(srcTownName);
        if (srcTown == null) {
            srcTown = new Town(srcTownName);
            towns.put(srcTownName, srcTown);
        }

        Town destTown = towns.get(destTownName);
        if (destTown == null) {
            destTown = new Town(destTownName);
            towns.put(destTownName, destTown);
        }

        srcTown.createRouteToTown(destTown, distance);
    }


    /**
     * Algorithm for #1 ~ #5
     */
    public int calculateDistanceOfTowns(String... townNames) {
        if (townNames.length < 2) {
            throw new IllegalArgumentException();
        }

        int total = 0;
        Town start = getTown(townNames[0]);
        for (int i = 1; i < townNames.length; i++) {
            Town next = getTown(townNames[i]);
            total += start.getDistanceTo(next);
            start = next;
        }
        return total;
    }

    public int calculateDurationOfTowns(String... townNames) {
        int distance = this.calculateDistanceOfTowns(townNames);
        return distance + (townNames.length - 2) * STOP_TIME_PER_STATION;
    }

    /**
     * Algorithm for #6
     */
    public int findNumberOfPathsToTownWithMaxStopLimit(String startTownName, String endTownName, int maxDegree) {
        List<Path> paths = findPathsToTownWithMaxStopLimit(null, getTown(startTownName), getTown(endTownName), maxDegree);
        return paths != null ? paths.size() : 0;
    }

    protected List<Path> findPathsToTownWithMaxStopLimit(Path parent, Town startTown, Town endTown, int maxDegree) {
        List<Path> results = new ArrayList<Path>();
        for (Town neighbor : startTown.getAdjacentTowns()) {

            int distance = startTown.getDistanceTo(neighbor);
            Path path = (parent != null) ? parent.copy().extendPath(neighbor.getName(), distance)
                                         : new Path(startTown.getName(), neighbor.getName(), distance);

            if (neighbor.equals(endTown)) {
                results.add(path);
                continue;
            }

            if (maxDegree - 1 > 0) {
                results.addAll(findPathsToTownWithMaxStopLimit(path, neighbor, endTown, maxDegree - 1));
            }
        }
        return results;
    }

    /**
     * Algorithm for #7
     */
    public int findNumberOfPathsToTownAtSpecifiedStop(String startTownName, String endTownName, int stop) {
        List<Path> paths = findPathsToTownAtSpecifiedStop(null, getTown(startTownName), getTown(endTownName), stop);
        return paths != null ? paths.size() : 0;
    }

    protected List<Path> findPathsToTownAtSpecifiedStop(Path parent, Town startTown, Town endTown, int stop) {
        List<Path> results = new ArrayList<Path>();
        for (Town neighbor : startTown.getAdjacentTowns()) {
            int distance = startTown.getDistanceTo(neighbor);
            Path path = (parent != null) ? parent.copy().extendPath(neighbor.getName(), distance)
                                         : new Path(startTown.getName(), neighbor.getName(), distance);

            if (stop - 1 > 0) {
                results.addAll(findPathsToTownAtSpecifiedStop(path, neighbor, endTown, stop - 1));
            } else {
                if (endTown.equals(neighbor)) {
                    results.add(path);
                }
            }
        }

        return results;
    }

    /**
     * Algorithm for #8 ~ #9
     */
    public int findShortestPathBetweenTowns(String startTownName, String endTownName) {
        Town startTown = getTown(startTownName);
        Town endTown = getTown(endTownName);

        Map<Town, Integer> costs = new HashMap<Town, Integer>();
        costs.put(endTown, Integer.MAX_VALUE);

        Map<Town, Town> parents = new HashMap<Town, Town>();
        parents.put(endTown, null);

        Set<Town> processed = new HashSet<Town>();

        for (Town neighbor : startTown.getAdjacentTowns()) {
            costs.put(neighbor, startTown.getDistanceTo(neighbor));
            parents.put(neighbor, startTown);
        }

        Town lowestTown = findTheLowestCostTown(costs, processed);
        while (lowestTown != null) {
            Integer cost = costs.get(lowestTown);

            for (Town neighbor : lowestTown.getAdjacentTowns()) {
                Integer newCost = cost + lowestTown.getDistanceTo(neighbor);

                Integer oldCost = costs.get(neighbor);
                if (oldCost == null) {
                    costs.put(neighbor, newCost);
                    parents.put(neighbor, lowestTown);
                    continue;
                }

                if (newCost < oldCost) {
                    costs.put(neighbor, newCost);
                    parents.put(neighbor, lowestTown);
                }
            }
            processed.add(lowestTown);
            lowestTown = findTheLowestCostTown(costs, processed);
        }

        Path path = constructPathFromParentTable(parents, startTown, endTown);
        return path.calculateDistanceToTail();
    }


    // ----------
    // F => A
    // A => B
    // B => S
    // ----------
    // Town Map: SA6, SB2, BA3, AF1, BF5
    // Return Path: S->B->A->F
    protected Path constructPathFromParentTable(Map<Town, Town> parents, Town startTown, Town endTown) {
        Stack<Town> stack = new Stack<Town>();
        stack.push(endTown);

        Town parent = parents.get(endTown);

        stack.push(parent);
        while (!parent.equals(startTown)) {
            parent = parents.get(parent);
            stack.push(parent);
        }

        Town src = stack.pop();
        Town next = stack.pop();
        Path path = new Path(src, next, src.getDistanceTo(next));
        src = next;
        while (!stack.isEmpty()) {
            next = stack.pop();
            path.extendPath(next.getName(), src.getDistanceTo(next));
            src = next;
        }
        return path;
    }

    // ----------
    //  A => 6
    //  B => 2
    //  F => Max
    // ----------
    // The above example return B, and B does not exist in processed
    protected Town findTheLowestCostTown(Map<Town, Integer> costs, Set<Town> processed) {

        Integer lowest = Integer.MAX_VALUE;
        Town town = null;
        for (Map.Entry<Town, Integer> cost : costs.entrySet()) {
            if (cost.getValue() < lowest && (!processed.contains(cost.getKey()))) {
                town = cost.getKey();
                lowest = cost.getValue();
            }
        }

        return town;
    }


    /**
     * Algorithm for #10
     */
    public int findNumberOfPathBetweenTownsWithMaxDistanceLimit(String startTownName, String endTownName, int maxDistance) {
        Town startTown = getTown(startTownName);
        Town endTown = getTown(endTownName);

        List<Path> paths = findPathsBetweenTownsWithMaxDistanceLimit(null, startTown, endTown, maxDistance);
        if (paths.size() == 0) {
            throw new NoSuchRouteException();
        }

        return calculateCompositePathsWithMaxDistanceLimit(paths, maxDistance);
    }

    // find all paths from startTown to endTown and their distance less than maxDistance
    protected List<Path> findPathsBetweenTownsWithMaxDistanceLimit(Path parent, Town startTown, Town endTown, int maxDistance) {
        List<Path> results = new ArrayList<Path>();
        for (Town neighbor : startTown.getAdjacentTowns()) {
            int distance = startTown.getDistanceTo(neighbor);
            Path path = (parent != null) ? parent.copy().extendPath(neighbor.getName(), distance)
                                         : new Path(startTown.getName(), neighbor.getName(), distance);

            if (path.calculateDistanceToTail() > maxDistance) {
                return results;
            }
            if (neighbor.equals(endTown)) {
                results.add(path);
                continue;
            }

            results.addAll(findPathsBetweenTownsWithMaxDistanceLimit(path, neighbor, endTown, maxDistance));
        }
        return results;
    }

    protected int calculateCompositePathsWithMaxDistanceLimit(List<Path> paths, int maxDistance) {
        List<Integer> distance = new ArrayList<Integer>(paths.size());
        for (Path path : paths) {
            distance.add(path.calculateDistanceToTail());
        }

        int result = distance.size();
        Integer[] distanceArray = distance.toArray(new Integer[distance.size()]);
        List<Integer> numbers = new ArrayList<Integer>(distance);
        int i = 0;
        while (i < numbers.size()) {
            List<Integer> newNumbers = filterArrayElementSumWithNumberAndLessThanLimit(distanceArray, numbers.get(i), maxDistance);
            result += newNumbers.size();
            numbers.addAll(newNumbers);
            i++;
        }
        return result;
    }


    // array: {9, 16, 21}, number: 9,  limit: 30  return: {18, 25}
    // array: {9, 16, 21}, number: 16, limit: 30  return: {25}
    // array: {9, 16, 21}, number: 21, limit: 30  return: {}
    protected List<Integer> filterArrayElementSumWithNumberAndLessThanLimit(Integer[] array, int number, int limit) {
        List<Integer> results = new ArrayList<Integer>(array.length);
        for (int i : array) {
            if (number + i < limit) {
                results.add(number + i);
            }
        }
        return results;
    }
}
