import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientHandlerTest {

    private static class MockSocket extends Socket {
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public MockSocket(String input) {
            this.inputStream = new ByteArrayInputStream(input.getBytes());
            this.outputStream = new ByteArrayOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public OutputStream getOutputStream() {
            return outputStream;
        }
    }

    @Test
    void testClientHandler() throws IOException {
        String input = "оскорбления\n";
        MockSocket mockClientSocket = new MockSocket(input);

        ClientHandler clientHandler = new ClientHandler(mockClientSocket);
        clientHandler.run();

        String expectedResponse = "Если твои противники перешли на личные оскорбления, будь уверен — твоя победа не за горами";
        assertEquals(expectedResponse, mockClientSocket.getOutputStream().toString().trim());
    }
}
