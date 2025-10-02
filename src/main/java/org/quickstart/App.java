import org.quickstart.ProcessStarter;
import org.quickstart.constants.QuickStartConstants;
import org.quickstart.registry.RegistryHandler;

void main()  {
    RegistryHandler handler = RegistryHandler.getInstance();
    handler.exportFromRegistryToYaml(Set.of("git_server", "uptime_monitor") ,Path.of(QuickStartConstants.USER_DIR.toString(), "docker-compose.yaml"));
}

