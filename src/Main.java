public class Main {
    public static final String API_SERVER = "http://172.30.1.41:8888"; //API 서버 주소
    
    public static final String CURRENT_VERSION = "1.0.0"; //현재 버전

    // 테스트 실행 할 땐 내부망 : http://내부IP:#### / 배포할 땐 외부망 : http://외부IP:####
    public static void main(String[] args) {
        // API 서버 주소와 버전 정보 출력 (확인용)
        System.out.println("API 서버 주소: " + API_SERVER);
        System.out.println("현재 버전: " + CURRENT_VERSION);

        AllowMariaDBPort.allow();
        UpdateUtil.checkUpdate();

        String[] userInfo = SetupUserDialog.showDialog();
        if (userInfo == null) return;

        String studentId = userInfo[0];
        String pw = userInfo[1];
        String hint = userInfo[2];
        int timerMin = Integer.parseInt(userInfo[3]);

        ApiUtil.saveUserInfo(studentId, pw, hint, timerMin);

        javax.swing.SwingUtilities.invokeLater(() -> {
            new CampusSeat(pw, hint, timerMin).setVisible(true);
        });
    }
}
