public class Main {
    public static final String API_SERVER = "http://172.30.1.41:8888"; // API 서버 주소
    public static final String CURRENT_VERSION = "1.0.0"; // 현재 버전

    public static void main(String[] args) {
        // API 서버 주소, 버전 정보 출력 (확인용)
        System.out.println("API 서버 주소: " + API_SERVER);
        System.out.println("현재 버전: " + CURRENT_VERSION);

        UpdateUtil.checkUpdate();

        // 사용자 정보 입력 다이얼로그 호출
        String[] userInfo = UserSettingDialog.showDialog();
        if (userInfo == null) return;

        String userPw = userInfo[1];
        String hint = userInfo[2];
        int timerMin = Integer.parseInt(userInfo[3]);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new CampusSeat(userPw, hint, timerMin).setVisible(true);
        });
    }
}
