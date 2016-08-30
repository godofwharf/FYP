package com.github.godofwharf.streaming.routing;

public class RouterSingleton {

    private static Router ref;

    public static Router getRouter() {
        if (ref == null) {
            System.out.println("Error - router not initialized");
            System.exit(0);
        }
        return ref;
    }

    public static void setRouter(Router r) {
        ref = r;
    }
}
