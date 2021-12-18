package pg.gipter.monitor.services;

import lombok.NoArgsConstructor;
import mslinks.ShellLink;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.gipter.monitor.utils.JarHelper;
import pg.gipter.monitor.utils.SystemUtils;

import java.io.IOException;
import java.nio.file.*;

@NoArgsConstructor
public class StartupService {

    private static final Logger logger = LoggerFactory.getLogger(StartupService.class);
    private static final Path shortcutLnkPath = Paths.get(
            "C:",
            "Users",
            SystemUtils.userName(),
            "AppData",
            "Roaming",
            "Microsoft",
            "Windows",
            "Start Menu",
            "Programs",
            "Startup",
            "Gipter-Monitor.lnk"
    );

    public void startOnStartup() {
        if (SystemUtils.isWindows()) {
            Path target = JarHelper.getJarPath().orElseGet(() -> Paths.get(""));
            if (!isStartOnStartupActive()) {
                logger.info("Creating shortcut to [{}] and placing it in Windows startup folder. [{}]",
                        target, shortcutLnkPath
                );
                try {
                    String workingDir = JarHelper.homeDirectoryPath().orElse("");

                    int iconNumber = 130;
                    ShellLink shellLink = ShellLink.createLink(target.toAbsolutePath().toString())
                            .setWorkingDir(workingDir)
                            .setIconLocation("%SystemRoot%\\system32\\SHELL32.dll");
                    shellLink.getHeader().setIconIndex(iconNumber);
                    shellLink.saveTo(shortcutLnkPath.toAbsolutePath().toString());
                    logger.info("Shortcut located in startup folder [{}].", shortcutLnkPath);
                    logger.info("Link working dir {}", shellLink.getWorkingDir());
                    logger.info("Link target {}", shellLink.resolveTarget());
                    logger.info("Link arguments [{}]", shellLink.getCMDArgs());
                    logger.info("Shortcut created and placed in Windows startup folder.");
                } catch (IOException e) {
                    logger.warn("Can not create shortcut to [{}] file and place it in Windows startup folder. [{}]", target, shortcutLnkPath, e);
                }
            } else {
                logger.info("Gipter-Monitor have already been set to start on startup. Shortcut already exists [{}]. ", shortcutLnkPath);
            }
        }
    }

    public void disableStartOnStartup() {
        if (SystemUtils.isWindows()) {
            if (isStartOnStartupActive()) {
                try {
                    FileUtils.forceDelete(shortcutLnkPath.toFile());
                    logger.info("Deletion of link done: [{}]", shortcutLnkPath);
                } catch (IOException e) {
                    logger.error("Can not delete link: [{}]", shortcutLnkPath, e);
                }
            }
        }
    }

    public boolean isStartOnStartupActive() {
        return Files.exists(shortcutLnkPath) && Files.isRegularFile(shortcutLnkPath);
    }
}
