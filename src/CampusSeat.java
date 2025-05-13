import javax.swing.*;
import java.awt.*;

public class CampusSeat extends JFrame {
    private String userPw;
    private String hint;
    private int remaining;
    private boolean unlocked = false;
    private Timer timer;
    private JPasswordField pwField;
    private JLabel hintLabel, timerLabel, datetimeLabel;

    public CampusSeat(String userPw, String hint, int timerMin) {
        this.userPw = userPw;
        this.hint = hint;
        this.remaining = timerMin * 60;

        setTitle("자리 지키미");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(null);

        datetimeLabel = new JLabel("", SwingConstants.CENTER);
        datetimeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 40));
        datetimeLabel.setForeground(Color.WHITE);
        datetimeLabel.setBounds(0, 60, 1920, 60);
        add(datetimeLabel);

        timerLabel = new JLabel("", SwingConstants.CENTER);
        timerLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBounds(0, 180, 1920, 40);
        add(timerLabel);

        pwField = new JPasswordField(18);
        pwField.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        pwField.setBounds(860, 300, 200, 40);
        add(pwField);

        hintLabel = new JLabel("", SwingConstants.CENTER);
        hintLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setBounds(0, 350, 1920, 30);
        add(hintLabel);

        JButton helpBtn = new JButton("도움말");
        helpBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        helpBtn.setBounds(1720, 980, 100, 40);
        helpBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "관리자 : 010-9903-5655"));
        add(helpBtn);

        pwField.addActionListener(e -> checkPassword());

        timer = new Timer(1000, e -> updateTimer());
        timer.start();

        new Thread(this::updateDatetime).start();
    }

    private void updateDatetime() {
        while (!unlocked) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String datetimeStr = now.toString().replace("T", " ").substring(0, 19);
            datetimeLabel.setText(datetimeStr);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }

    private void updateTimer() {
        if (!unlocked && remaining > 0) {
            remaining--;
            timerLabel.setText("남은 시간: " + (remaining/60) + "분 " + (remaining%60) + "초");
        } else if (!unlocked) {
            unlocked = true;
            unlockScreen("타이머 만료로 자동 해제되었습니다.");
        }
    }

    private void checkPassword() {
        String pw = new String(pwField.getPassword());
        if (pw.equals(userPw)) {
            unlocked = true;
            unlockScreen("비밀번호가 맞습니다. 잠금 해제!");
        } else if (pw.equals("020115")) {
            unlocked = true;
            unlockScreen("관리자 권한으로 잠금 해제!");
        } else {
            hintLabel.setText(hint);
            JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            pwField.setText("");
        }
    }

    private void unlockScreen(String msg) {
        JOptionPane.showMessageDialog(this, msg, "잠금 해제", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}