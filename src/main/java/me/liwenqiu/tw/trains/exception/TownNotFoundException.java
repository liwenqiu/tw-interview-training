package me.liwenqiu.tw.trains.exception;

/**
 * @author liwenqiu@gmail.com
 */
public class TownNotFoundException extends TrainRuntimeException {

    public TownNotFoundException(String townName) {
        super("NO SUCH TOWN: " + townName);
    }
}
