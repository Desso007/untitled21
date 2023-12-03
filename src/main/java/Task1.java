import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Task1 {
    private static final int PORT = 8888;
    private static final int MAX_CONNECTIONS = 5;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONNECTIONS);

        // Запуск сервера
        executorService.execute(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Сервер запущен. Ожидание подключений...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executorService.execute(new ClientHandler(clientSocket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Запуск клиента
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in);
             BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                System.out.print("Введите запрос: ");
                String request = scanner.nextLine();

                writer.println(request);

                String response = serverReader.readLine();
                System.out.println("Ответ сервера: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = reader.readLine();
            String response = getResponse(request);

            writer.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponse(String request) {
        switch (request.toLowerCase()) {
            case "личности":
                return "Не переходи на личности там, где их нет";
            case "оскорбления":
                return "Если твои противники перешли на личные оскорбления, будь уверен — твоя победа не за горами";
            case "глупый":
                return "А я тебе говорил, что ты глупый? Так вот, я забираю свои слова обратно... Ты просто бог идиотизма.";
            case "интеллект":
                return "Чем ниже интеллект, тем громче оскорбления";
            default:
                return "Неизвестный запрос";
        }
    }
}



