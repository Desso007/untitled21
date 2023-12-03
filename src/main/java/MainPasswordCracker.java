public class MainPasswordCracker {
    public static void main(String[] args) {
        Task3 passwordCracker;

        if (args.length > 0 && args[0].equalsIgnoreCase("multi")) {
            passwordCracker = new MultiThreadPasswordCracker(4);
        } else {
            passwordCracker = new SingleThreadPasswordCracker();
        }

        long startTime = System.currentTimeMillis();
        passwordCracker.crackPasswords();
        long endTime = System.currentTimeMillis();

        System.out.println("Time: " + (endTime - startTime) + " ms");
    }
}
