public class Main {
    public static final String API_SERVER = "http://172.30.1.41:8888"; // API 서버 주소
    public static final String CURRENT_VERSION = "1.0.0"; // 현재 버전

    public static void main(String[] args) {
        // 설치 경로 먼저 지정
        CampusSeatSetup.init();
        String installPath = CampusSeatSetup.getInstallPath();

        // API 서버 주소, 버전 정보, 설치 경로 출력 (확인용)
        System.out.println("API 서버 주소: " + API_SERVER);
        System.out.println("현재 버전: " + CURRENT_VERSION);
        System.out.println("설치 경로: " + installPath);

        UpdateUtil.checkUpdate();

        String[] userInfo = SetupUserDialog.showDialog();
        if (userInfo == null) return;

        javax.swing.SwingUtilities.invokeLater(() -> {
            new CampusSeat(userInfo[1], userInfo[2], Integer.parseInt(userInfo[3])).setVisible(true);
        });
    }
}
