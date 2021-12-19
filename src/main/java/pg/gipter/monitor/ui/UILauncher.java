package pg.gipter.monitor.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.gipter.monitor.launchers.Launcher;
import pg.gipter.monitor.services.StartupService;
import pg.gipter.monitor.services.VersionService;
import pg.gipter.monitor.ui.fxproperties.ActiveSupportDetails;
import pg.gipter.monitor.utils.BundleUtils;
import pg.gipter.monitor.utils.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Created by Gawa 2017-10-04 */
public class UILauncher implements Launcher {

    private static final Logger logger = LoggerFactory.getLogger(UILauncher.class);

    private final Stage mainWindow;
    private Stage detailsWindow;
    private TrayHandler trayHandler;
    @Getter
    private final SimpleObjectProperty<ActiveSupportDetails> currentActiveSupport;

    public UILauncher(Stage mainWindow) {
        this.mainWindow = mainWindow;
        currentActiveSupport = new SimpleObjectProperty<>();
    }

    public void initTrayHandler() {
        trayHandler = new TrayHandler(this);
        if (trayHandler.tryIconExists()) {
            logger.info("Updating tray icon.");
            trayHandler.updateTrayLabels();
        } else {
            logger.info("Initializing tray icon.");
            trayHandler.createTrayIcon();
        }
    }

    @Override
    public void execute() {
        if (!isTraySupported()) {
            logger.info("Tray icon is not supported. Can't launch in silent mode. Program is terminated");
            Platform.exit();
        }
        setStartOnStartup();
        initTray();
        buildAndShowMainWindow();
    }

    private void setStartOnStartup() {
        logger.info("Checking if Gipter can be started on system startup.");
        if (!isTraySupported()) {
            logger.info("Tray not supported. Can not set start on startup.");
            return;
        }
        new StartupService().startOnStartup();
    }

    public void buildAndShowMainWindow() {
        buildScene(
                mainWindow,
                WindowFactory.MAIN.createWindow(this)
        );
        Platform.runLater(mainWindow::show);
    }

    private void buildScene(Stage stage, AbstractWindow window) {
        try {
            Image icon = readImage(window.windowImgFilePath());
            stage.getIcons().add(icon);
        } catch (IOException ex) {
            logger.warn("Problem with loading window icon: {}.", ex.getMessage());
        }
        try {
            stage.setTitle(BundleUtils.getMsg(
                    window.windowTitleBundle(), VersionService.getInstance().getVersion()
            ));
            stage.setResizable(window.resizable());
            Scene scene = new Scene(window.root());
            if (!StringUtils.nullOrEmpty(window.css())) {
                scene.getStylesheets().add(window.css());
            }
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException ex) {
            logger.error("Building scene error.", ex);
        }
    }

    private Image readImage(String imgPath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(imgPath)) {
            return new Image(is);
        }
    }

    public static void platformExit() {
        Platform.exit();
        System.exit(0);
    }

    public void updateTray() {
        if (trayHandler != null && trayHandler.canCreateTrayIcon()) {
            trayHandler.updateTrayLabels();
        }
    }

    public void removeTray() {
        trayHandler.removeTrayIcon();
    }

    public EventHandler<WindowEvent> trayOnCloseEventHandler() {
        return trayHandler.trayOnCloseEventHandler();
    }

    public boolean isTrayActivated() {
        return isTraySupported();
    }

    public boolean isTraySupported() {
        return (trayHandler != null || SystemTray.isSupported());
    }

    private void initTray() {
        if (isTrayActivated()) {
            initTrayHandler();
        } else {
            setMainOnCloseRequest(AbstractController.regularOnCloseEventHandler());
        }
    }

    void setMainOnCloseRequest(EventHandler<WindowEvent> onCloseEventHandler) {
        mainWindow.setOnCloseRequest(onCloseEventHandler);
    }

    public void hideMainWindow() {
        mainWindow.hide();
    }

    public void bind(SimpleObjectProperty<ActiveSupportDetails> selectedValue) {
        currentActiveSupport.unbindBidirectional(selectedValue);
        currentActiveSupport.bindBidirectional(selectedValue);
    }

    public void openDetailsWindow() {
        Platform.runLater(() -> {
            detailsWindow = new Stage();
            detailsWindow.initModality(Modality.APPLICATION_MODAL);
            buildScene(detailsWindow, WindowFactory.DETAILS.createWindow(this));
            detailsWindow.setOnCloseRequest(event -> detailsWindow.close());
            detailsWindow.showAndWait();
        });
    }

    public void closeDetailsWindow() {
        detailsWindow.close();
    }

    public void displayNotificationMessage(String message) {
        trayHandler.displayMessage(message);
    }

    public void updateTables(List<ActiveSupportDetails> failedTries) {

    }
}
