package pg.gipter.monitor.services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Scanner;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionService {

    private static class InstanceHolder {
        public static final VersionService INSTANCE_HOLDER = new VersionService();
    }

    public static VersionService getInstance() {
        return InstanceHolder.INSTANCE_HOLDER;
    }

    public String getVersion() {
        String version = "";

        InputStream is = getClass().getClassLoader().getResourceAsStream("version.txt");
        if (is == null) {
            log.warn("Can not read version.");
        } else {
            Scanner scan = new Scanner(is);
            if (scan.hasNextLine()) {
                version = scan.nextLine();
            }
        }
        return SemanticVersioning.getSemanticVersioning(version).getVersion();
    }
}
