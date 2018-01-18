package me.liwenqiu.tw.trains.domain;

/**
 * @author liwenqiu@gmail.com
 */
public class Path implements Cloneable {

    private String startTownName;
    private String endTownName;
    private int distance;

    private Path nextPath;

    public Path(String startTownName, String endTownName, int distance) {
        this.startTownName = startTownName;
        this.endTownName = endTownName;
        this.distance = distance;
    }

    public Path(Town startTown, Town endTown, int distance) {
        this(startTown.getName(), endTown.getName(), distance);
    }

    public String getStartTownName() {
        return startTownName;
    }

    public String getEndTownName() {
        return endTownName;
    }

    public int getDistance() {
        return distance;
    }

    public Path extendPath(String endTownName, int distance) {
        Path tail = this.getTailPath();
        tail.nextPath = new Path(tail.endTownName, endTownName, distance);
        return this;
    }

    public int calculateDistanceToTail() {
        Integer totalDistance = this.distance;
        Path current = this;
        while (current.hasNext()) {
            current = current.getNextPath();
            totalDistance += current.distance;
        }
        return totalDistance;
    }


    public Path copy() {
        try {
            return (Path)this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Path newPath = (Path)super.clone();

        Path index = newPath;
        Path current = this;
        while (current.hasNext()) {
            current = current.getNextPath();
            index.nextPath = (Path)current.clone();
            index = index.nextPath;
        }

        return newPath;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(startTownName);
        builder.append("->");
        builder.append(endTownName);

        Path current = this;
        while (current.hasNext()) {
            current = current.getNextPath();
            builder.append("->");
            builder.append(current.endTownName);
        }


        return builder.toString();
    }

    protected boolean hasNext() {
        return nextPath != null ? true : false;
    }

    protected Path getNextPath() {
        return this.nextPath;
    }

    protected Path getTailPath() {
        if (! this.hasNext()) {
            return this;
        }

        Path current = this;
        while (current.hasNext()) {
            current = current.getNextPath();
        }
        return current;
    }
}
