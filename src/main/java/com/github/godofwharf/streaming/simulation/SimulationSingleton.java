package com.github.godofwharf.streaming.simulation;


public class SimulationSingleton {

    private static Simulation ref;

    public static void init(Simulation sim) {
        ref = sim;
    }

    public static Simulation getSim() {
        if (ref == null) {
            System.out.println("Error - Simulation object not set");
            System.exit(0);
        }
        return ref;
    }

}
