package ts.logix.py;

/**
 * The matching Python file is named Conversion.py.
 * 
 * @author Kenzie Togami
 */
public class Conversion {

    /**
     * Converts the given state boolean to a string.
     * 
     * @param state
     *            - the state to convert
     * @return "On" for <tt>true</tt>, "Off" for <tt>false</tt>.
     */
    public static String getStateAsStr(boolean state) {
        return state ? "On" : "Off";
    }

}
