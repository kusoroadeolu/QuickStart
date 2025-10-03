import org.quickstart.processes.ProcessStarter;

void main()  {
//    RegistryHandler handler = RegistryHandler.getInstance();
//    handler.exportFromRegistryToYaml(Set.of("git_server", "uptime_monitor") ,Path.of(QuickStartConstants.USER_DIR.toString(), "docker-compose.yaml"));


//    ProcessStarter processStarter = new ProcessStarter();
//    String val = "file-path";
//    String[] arr = {"docker-compose", "-f", "\"C:\\Users\\eastw\\docker-compose.yaml\"", "up", "-d"};
    Path path = Paths.get("C:\\Users\\eastw\\Git Projects\\Personal\\QuickStart\\src\\main\\java\\org\\quickstart\\App.java");
    IO.println(path.getFileName());
}

