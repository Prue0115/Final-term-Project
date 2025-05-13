public class Main {
    public static void main(String[] args) {
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