package common;

public class Utils {
    public static String getUserAgent() {
        String javaVersion = "Java/" + System.getProperty("java.version");
        String os = System.getProperty("os.name") + " " 
                + System.getProperty("os.arch") + " " + System.getProperty("os.version");
        String sdk = Config.USER_AGENT + Config.SDK_VERSION;
        return sdk + " (" + os + ") " + javaVersion;
    }

    /*
     * check the arg.
     * 1. arg == null, return false, treat as empty situation
     * 2. arg == "", return false, treat as empty situation
     * 3. arg == " " or arg == "   ", return false, treat as empty situation
     * 4. return true, only if the arg is a illegal string
     *
     * */
    public static boolean isArgNotEmpty(String arg) {
        return arg != null && !arg.trim().isEmpty();
    }

//    public static String getPath(String streamId) {
//        String[] res = streamId.split("\\.");
//        // res[1] -> hub, res[2] -> title
//        return String.format("/%s/%s", res[1], res[2]);
//    }
}
