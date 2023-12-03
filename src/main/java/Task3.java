import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public interface Task3 {
    void crackPasswords();
}

class SingleThreadPasswordCracker implements Task3 {
    private static final char[] CHARSET = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int PASSWORD_LENGTH = 4;

    private Map<String, String> passwordDictionary = new HashMap<>();

    public SingleThreadPasswordCracker() {
        loadPasswordsIntoDictionary();
    }

    private void loadPasswordsIntoDictionary() {
        String[] usernames = {"a.v.petrov", "v.v.belov", "a.s.ivanov", "k.p.maslov"};
        String[] hashes = {"e10adc3949ba59abbe56e057f20f883e", "d8578edf8458ce06fbc5bb76a58c5ca4",
                "482c811da5d5b4bc6d497ffa98491e38", "5f4dcc3b5aa765d61d8327deb882cf99"};

        for (int i = 0; i < usernames.length; i++) {
            passwordDictionary.put(hashes[i], usernames[i]);
        }
    }

    @Override
    public void crackPasswords() {
        String currentPassword = generateInitialPassword();

        while (!passwordDictionary.isEmpty()) {
            String hash = md5(currentPassword);
            if (passwordDictionary.containsKey(hash)) {
                String username = passwordDictionary.get(hash);
                System.out.println("Username: " + username + ", Password: " + currentPassword);
                passwordDictionary.remove(hash);
            }
            currentPassword = nextPassword(currentPassword);
        }
    }

    private String generateInitialPassword() {
        StringBuilder initialPassword = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            initialPassword.append(CHARSET[0]);
        }
        return initialPassword.toString();
    }

    private String nextPassword(String currentPassword) {
        char[] passwordChars = currentPassword.toCharArray();
        for (int i = passwordChars.length - 1; i >= 0; i--) {
            int index = (indexOfChar(passwordChars[i]) + 1) % CHARSET.length;
            passwordChars[i] = CHARSET[index];
            if (index != 0) {
                break;
            }
        }
        return new String(passwordChars);
    }

    private int indexOfChar(char c) {
        for (int i = 0; i < CHARSET.length; i++) {
            if (CHARSET[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

class MultiThreadPasswordCracker implements Task3 {
    private static final char[] CHARSET = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int PASSWORD_LENGTH = 4;

    private Map<String, String> passwordDictionary = new HashMap<>();
    private ExecutorService executorService;

    public MultiThreadPasswordCracker(int threadCount) {
        loadPasswordsIntoDictionary();
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    private void loadPasswordsIntoDictionary() {
        String[] usernames = {"a.v.petrov", "v.v.belov", "a.s.ivanov", "k.p.maslov"};
        String[] hashes = {"e10adc3949ba59abbe56e057f20f883e", "d8578edf8458ce06fbc5bb76a58c5ca4",
                "482c811da5d5b4bc6d497ffa98491e38", "5f4dcc3b5aa765d61d8327deb882cf99"};

        for (int i = 0; i < usernames.length; i++) {
            passwordDictionary.put(hashes[i], usernames[i]);
        }
    }

    @Override
    public void crackPasswords() {
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            executorService.execute(new PasswordCrackTask(i));
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class PasswordCrackTask implements Runnable {
        private int position;
        private String currentPassword;

        public PasswordCrackTask(int position) {
            this.position = position;
            this.currentPassword = String.valueOf(CHARSET[0]);
        }

        @Override
        public void run() {
            while (!passwordDictionary.isEmpty()) {
                generateAndCheckPasswords();
                currentPassword = nextPassword(currentPassword, position);
            }
        }

        private void generateAndCheckPasswords() {
            char[] passwordChars = currentPassword.toCharArray();
            for (int i = passwordChars.length - 1; i >= 0; i--) {
                int index = (indexOfChar(passwordChars[i]) + 1) % CHARSET.length;
                passwordChars[i] = CHARSET[index];
                if (index != 0) {
                    break;
                }
            }

            String hash = md5(new String(passwordChars));
            if (passwordDictionary.containsKey(hash)) {
                String username = passwordDictionary.get(hash);
                System.out.println("Username: " + username + ", Password: " + new String(passwordChars));
                passwordDictionary.remove(hash);
            }
        }
    }

    private String nextPassword(String currentPassword, int position) {
        char[] passwordChars = currentPassword.toCharArray();
        int index = (indexOfChar(passwordChars[position]) + 1) % CHARSET.length;
        passwordChars[position] = CHARSET[index];

        return new String(passwordChars);
    }

    private int indexOfChar(char c) {
        for (int i = 0; i < CHARSET.length; i++) {
            if (CHARSET[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

