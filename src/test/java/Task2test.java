import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Task2test {

    private Task2 threadPool;

    @BeforeEach
    void setUp() {
        threadPool = FixedThreadPool.create(2);
        threadPool.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        threadPool.close();
    }

    @Test
    void testFibonacciCalculation() {
        long result = fibonacci(5);
        assertEquals(5, result);
    }

    @Test
    void testThreadPoolExecution() {
        // Добавляем задачу в пул
        threadPool.execute(() -> {
            long result = fibonacci(3);
            System.out.println("Test Result: " + result);
        });

        // Даем некоторое время для выполнения задачи в пуле
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private static long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
