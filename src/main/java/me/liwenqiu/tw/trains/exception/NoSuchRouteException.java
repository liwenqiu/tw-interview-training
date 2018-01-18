package me.liwenqiu.tw.trains.exception;

/**
 * @author liwenqiu@gmail.com
 */
public class NoSuchRouteException extends TrainRuntimeException {

    public NoSuchRouteException() {
        super("NO SUCH ROUTE");
    }
}
