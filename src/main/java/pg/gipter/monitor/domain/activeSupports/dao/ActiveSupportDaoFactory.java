package pg.gipter.monitor.domain.activeSupports.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActiveSupportDaoFactory {

    public static ActiveSupportDao getInstance() {
        return new ActiveSupportRepository();
    }
}
