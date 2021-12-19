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
            executorService = Executors.newFixedThreadPool(SystemUtils.availableProcessors());
        }
        return executorService;
    }

    public static void submit(Task<?> callable) throws ExecutionException, InterruptedException {
        getExecutorService().submit(callable);
    }

    public static <V> Future<V> submit(Callable<V> callable) {
        return getExecutorService().submit(callable);
    }

    public static void submit(Runnable runnable) {
        getExecutorService().submit(runnable, null);
    }

    private static <T> Future<T> submit(Runnable runnable, T result) {
        return getExecutorService().submit(runnable, result);
    }
}
