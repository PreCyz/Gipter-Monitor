package pg.gipter.monitor.ui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.gipter.monitor.services.VersionService;
import pg.gipter.monitor.utils.BundleUtils;
import pg.gipter.monitor.utils.ResourceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.Executor;

public class TrayHandler {

    private static final Logger logger = LoggerFactory.getLogger(TrayHandler.class);

    private final UILauncher uiLauncher;
    private static TrayIcon trayIcon;
    private static PopupMenu trayPopupMenu;
    private final Executor executor;

    TrayHandler(UILauncher uiLauncher, Executor executor) {
        this.uiLauncher = uiLauncher;
        this.executor = executor;
    }

    void createTrayIcon() {
        uiLauncher.setMainOnCloseRequest(trayOnCloseEventHandler());

        Platform.setImplicitExit(false);
        SystemTray tray = SystemTray.getSystemTray();

        trayPopupMenu = new PopupMenu();
        addMenuItemsToMenu(trayPopupMenu);

        trayIcon = new TrayIcon(
                createTrayImage(),
                BundleUtils.getMsg("main.title", VersionService.getInstance().getVersion()),
                trayPopupMenu
        );
        trayIcon.addActionListener(showActionListener());
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.error("Error when creating tray.", e);
        }
    }

    boolean canCreateTrayIcon() {
        return SystemTray.isSupported();
    }

    private void addMenuItemsToMenu(PopupMenu popupMenu) {
        executor.execute(() -> {
            MenuItem showItem = new MenuItem(BundleUtils.getMsg("tray.item.show"));
            showItem.addActionListener(showActionListener());
            popupMenu.add(showItem);

            popupMenu.addSeparator();

            MenuItem closeItem = new MenuItem(BundleUtils.getMsg("tray.item.close"));
            closeItem.addActionListener(closeActionListener());
            popupMenu.add(closeItem);
        });
    }

    private Image createTrayImage() {
        Optional<URL> imageURL = ResourceUtils.getImgResource(ImageFile.MINION_TRAY.fileUrl());
        String description = "Minion-Monitor";
        return new ImageIcon(imageURL.get(), description).getImage();
    }

    EventHandler<WindowEvent> trayOnCloseEventHandler() {
        return windowEvent -> hide();
    }

    private ActionListener showActionListener() {
        return e -> Platform.runLater(uiLauncher::buildAndShowMainWindow);
    }

    private ActionListener closeActionListener() {
        return e -> UILauncher.platformExit();
    }

    private void hide() {
        Platform.runLater(() -> {
            if (SystemTray.isSupported()) {
                uiLauncher.hideMainWindow();
            } else {
                UILauncher.platformExit();
            }
        });
    }

    void removeTrayIcon() {
        if (canCreateTrayIcon()) {
            SystemTray tray = SystemTray.getSystemTray();
            boolean exist = false;
            for (TrayIcon icon : tray.getTrayIcons()) {
                if (icon.getImage().equals(trayIcon.getImage()) && icon.getToolTip().equals(trayIcon.getToolTip())) {
                    exist = true;
                    break;
                }
            }
            if (exist) {
                tray.remove(trayIcon);
                trayIcon = null;
            }
        }
    }

    void updateTrayLabels() {
        if (canCreateTrayIcon()) {
            trayPopupMenu.removeAll();
            addMenuItemsToMenu(trayPopupMenu);
        }
    }

    boolean tryIconExists() {
        return trayIcon != null;
    }
}
