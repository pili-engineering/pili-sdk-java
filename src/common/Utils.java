package common;

public class Utils {
    public static String getUserAgent() {
        String javaVersion = "Java/" + System.getProperty("java.version");
        String os = System.getProperty("os.name") + " " 
                + System.getProperty("os.arch") + " " + System.getProperty("os.version");
        String sdk = Config.USER_AGENT + Config.SDK_VERSION;
        return sdk + " (" + os + ") " + javaVersion;
    }
}
