package pg.gipter.monitor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.gipter.monitor.launchers.Launcher;
import pg.gipter.monitor.launchers.LauncherFactory;
import pg.gipter.monitor.services.VersionService;
import pg.gipter.monitor.utils.StringUtils;
import pg.gipter.monitor.utils.SystemUtils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Gipter monitor started.");
        Main mObj = new Main(args);
        mObj.setLoggerLevel("INFO");
        logger.info("Java version '{}'.", SystemUtils.javaVersion());
        logger.info("Version of application '{}'.", VersionService.getInstance().getVersion());
        logger.info("Gipter-Monitor can use '{}' threads.", Runtime.getRuntime().availableProcessors());
        launch(args);
    }

    public Main() {
    }

    Main(String[] args) {
        Optional<String> javaHome = Stream.of(args).filter(arg -> arg.startsWith("java.home")).findFirst();
        if (javaHome.isPresent()) {
            System.setProperty("java.home", javaHome.get().split("=")[1]);
            logger.info("New JAVA_HOME {}.", SystemUtils.javaHome());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Launcher launcher = LauncherFactory.getLauncher(primaryStage);
        launcher.execute();
    }

    private void setLoggerLevel(String loggerLevel) {
        if (!StringUtils.nullOrEmpty(loggerLevel)) {
            Set<String> loggers = Stream.of(
                    "pg.gipter",
                    "org.springframework",
                    "org.mongodb",
                    "org.quartz"
            ).collect(toSet());

            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

            for (String loggerName : loggers) {
                ch.qos.logback.classic.Logger logger = loggerContext.getLogger(loggerName);
                logger.setLevel(Level.toLevel(loggerLevel));
                Main.logger.info("Level of the logger [{}] is set to [{}]", loggerName, Level.toLevel(loggerLevel));
            }
        }
    }
}
