package pg.gipter.monitor.domain.activeSupports.dao;

import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;

import java.util.List;

public interface ActiveSupportDao {
    ActiveSupport upsert(ActiveSupport activeSupport);
    List<ActiveSupport> saveAll(List<ActiveSupport> activeSupports);
}
