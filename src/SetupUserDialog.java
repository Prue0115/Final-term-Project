import javax.swing.*;

public class SetupUserDialog {
    public static String[] showDialog() {
        JTextField idField = new JTextField();
        JPasswordField pwField = new JPasswordField();
        JTextField hintField = new JTextField();
        JTextField timerField = new JTextField();

        Object[] fields = {
                "학번:", idField,
                "비밀번호:", pwField,
                "비밀번호 힌트(선택):", hintField,
                "타이머(분, 1~60):", timerField
        };

        int result = JOptionPane.showConfirmDialog(null, fields, "자리 지키미", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String studentId = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();
            String hint = hintField.getText().trim();
            String timerStr = timerField.getText().trim();
            int timerMin = 0;
            try { timerMin = Integer.parseInt(timerStr); } catch (Exception ignored) {}
            if (studentId.isEmpty() || pw.isEmpty() || timerMin < 1 || timerMin > 60) {
                JOptionPane.showMessageDialog(null, "입력값을 확인하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return showDialog();
            }
            ApiUtil.saveUserInfo(studentId, pw, hint, timerMin);
            JOptionPane.showMessageDialog(null, "비밀번호와 타이머가 저장되었습니다.", "설정 완료", JOptionPane.INFORMATION_MESSAGE);
            return new String[] { studentId, pw, hint, String.valueOf(timerMin) };
        }
        return null;
    }
}