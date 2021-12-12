package pg.gipter.monitor.domain.activeSupports.dao;

import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;

public interface ActiveSupportDao {
    ActiveSupport upsert(ActiveSupport activeSupport);
}
