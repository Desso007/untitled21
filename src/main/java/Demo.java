public class Demo {
    public static void main(String[] args) {
        try (Task2 threadPool = FixedThreadPool.create(4)) {
            threadPool.start();

            for (int i = 0; i < 10; i++) {
                int finalI = i;
                threadPool.execute(() -> {
                    long result = fibonacci(finalI);
                    System.out.println("Fibonacci(" + finalI + ") = " + result);
                });
            }

            // Даем некоторое время для выполнения задач
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
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
