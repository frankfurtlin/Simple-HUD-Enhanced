package com.frankfurtlin.simpleHudEnhanced.debugStatus;

public class DebugStatus {

    private static boolean debugStatus = false;

    public static void setDebugStatus(boolean status) {
        debugStatus = status;
    }

    public static boolean getDebugStatus() {
        return debugStatus;
    }
}
