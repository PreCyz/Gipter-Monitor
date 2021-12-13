package pg.gipter.monitor.domain.activeSupports.services;

import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;
import pg.gipter.monitor.domain.activeSupports.dao.ActiveSupportDao;
import pg.gipter.monitor.domain.activeSupports.dao.ActiveSupportDaoFactory;

import java.util.List;

public class ActiveSupportService {
    private final ActiveSupportDao activeSupportDao;

    public ActiveSupportService() {
        this.activeSupportDao = ActiveSupportDaoFactory.getInstance();
    }

    public ActiveSupport save(ActiveSupport activeSupport) {
        activeSupport = activeSupportDao.upsert(activeSupport);
        return activeSupport;
    }

    public List<ActiveSupport> saveAll(List<ActiveSupport> activeSupports) {
        activeSupports = activeSupportDao.saveAll(activeSupports);
        return activeSupports;
    }
}
