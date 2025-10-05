package org.quickstart;

import org.quickstart.registry.RegistryHandler;

import java.util.Set;

public class App {
    public static void main(String[] args) {
        RegistryHandler.getInstance().importToRegistryFromYaml("myyaml.yaml", Set.of("redis"), true);
    }
}
