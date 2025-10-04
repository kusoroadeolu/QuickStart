import org.quickstart.registry.RegistryHandler;

void main() throws IOException {
    //RegistryHandler handler = RegistryHandler.getInstance();
    //handler.exportFromRegistryToYaml(Set.of("git_server", "uptime_monitor") ,Path.of(QuickStartConstants.USER_DIR.toString(), "docker-compose.yaml"));
//    ProfileHandler profileHandler = ProfileHandler.getInstance();
//    profileHandler.runProfile("starter");

    RegistryHandler.getInstance().importToRegistryFromYaml("myyaml.yaml", false);
    HashMap<String, String> map = new HashMap<>();

}

