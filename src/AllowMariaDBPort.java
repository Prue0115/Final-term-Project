public class AllowMariaDBPort {
    public static void allow() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "netsh", "advfirewall", "firewall", "add", "rule",
                        "name=campusseat", "dir=in", "action=allow",
                        "protocol=TCP", "localport=3306"
                );
                pb.redirectErrorStream(true);
                pb.start();
            } catch (Exception e) {
                // 무시
            }
        }
    }
}