package pg.gipter.monitor.utils;

import javafx.concurrent.Task;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtils {

    private static ExecutorService executorService;

    private static ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        return executorService;
    }

    public static void submit(Task<?> callable) throws ExecutionException, InterruptedException {
        getExecutorService().submit(callable);
    }
}
