import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class UserSettingDialog {
    public static String[] showDialog() {
        return showDialog("", "", "", "");
    }

    // Placeholder가 보이는 JTextField
    static class HintTextField extends JTextField {
        private final String hint;
        public HintTextField(String hint, String text, int columns) {
            super(text, columns);
            this.hint = hint;
            setUI(new BasicTextFieldUI());
            setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230))); // 연한 밑줄
            setOpaque(false);

            getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { repaint(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { repaint(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { repaint(); }
            });
            addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent e) { repaint(); }
                public void focusLost(java.awt.event.FocusEvent e) { repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(180, 180, 180)); // 연한 placeholder 색
                g2.setFont(getFont());
                Insets insets = getInsets();
                g2.drawString(hint, insets.left + 2, getHeight() / 2 + getFont().getSize() / 2 - 2);
                g2.dispose();
            }
        }
    }

    private static String[] showDialog(String prevId, String prevPw, String prevHint, String prevTimer) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        Font labelFont = new Font("Apple SD Gothic Neo, SF Pro, 맑은 고딕, Malgun Gothic, SansSerif", Font.BOLD, 15);
        Font fieldFont = new Font("Apple SD Gothic Neo, SF Pro, 맑은 고딕, Malgun Gothic, SansSerif", Font.PLAIN, 15);

        // 학번
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel idLabel = new JLabel("학번");
        idLabel.setFont(labelFont);
        panel.add(idLabel, gbc);
        gbc.gridy = 1;
        HintTextField idField = new HintTextField("학번 입력", prevId, 15);
        idField.setFont(fieldFont);
        panel.add(idField, gbc);

        // 비밀번호
        gbc.gridy = 2;
        JLabel pwLabel = new JLabel("비밀번호");
        pwLabel.setFont(labelFont);
        panel.add(pwLabel, gbc);
        gbc.gridy = 3;
        HintTextField pwField = new HintTextField("비밀번호 입력", prevPw, 15);
        pwField.setFont(fieldFont);
        panel.add(pwField, gbc);

        // 비밀번호 힌트
        gbc.gridy = 4;
        JLabel hintLabel = new JLabel("비밀번호 힌트(선택)");
        hintLabel.setFont(labelFont);
        panel.add(hintLabel, gbc);
        gbc.gridy = 5;
        HintTextField hintField = new HintTextField("비밀번호 힌트 입력", prevHint, 15);
        hintField.setFont(fieldFont);
        panel.add(hintField, gbc);

        // 타이머
        gbc.gridy = 6;
        JLabel timerLabel = new JLabel("타이머(분, 1~60)");
        timerLabel.setFont(labelFont);
        panel.add(timerLabel, gbc);
        gbc.gridy = 7;
        HintTextField timerField = new HintTextField("타이머 시간 입력", prevTimer, 5);
        timerField.setFont(fieldFont);
        panel.add(timerField, gbc);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton okButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");

        Font buttonFont = new Font("Apple SD Gothic Neo, SF Pro, 맑은 고딕, Malgun Gothic, SansSerif", Font.BOLD, 14);
        okButton.setFont(buttonFont);
        cancelButton.setFont(buttonFont);

        // macOS 스타일 버튼 색상 및 둥근 테두리
        okButton.setBackground(new Color(240, 240, 240));
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cancelButton.setBackground(new Color(240, 240, 240));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        // 전체 패널에 버튼 추가
        gbc.gridy++;
        panel.add(Box.createVerticalStrut(10), gbc); // 버튼과 입력란 사이 여백
        gbc.gridy++;
        panel.add(buttonPanel, gbc);

        // 커스텀 다이얼로그 생성
        JDialog dialog = new JDialog((Frame) null, "자리 지키미", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        final String[] result = new String[4];
        okButton.addActionListener(e -> {
            String studentId = idField.getText().trim();
            String pw = pwField.getText().trim();
            String hint = hintField.getText().trim();
            String timerStr = timerField.getText().trim();
            int timerMin = 0;
            try { timerMin = Integer.parseInt(timerStr); } catch (Exception ignored) {}
            if (studentId.isEmpty() || pw.isEmpty() || timerMin < 1 || timerMin > 60) {
                JOptionPane.showMessageDialog(dialog, "입력값을 확인하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Config.saveUserInfo(studentId, pw, hint, timerMin);

            // macOS 스타일 설정 완료 다이얼로그
            JDialog doneDialog = new JDialog(dialog, "설정 완료", true);
            doneDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            JPanel donePanel = new JPanel(new BorderLayout(0, 20));
            donePanel.setBackground(Color.WHITE);
            JLabel msg = new JLabel("비밀번호와 타이머가 저장되었습니다.", SwingConstants.CENTER);
            msg.setFont(new Font("Apple SD Gothic Neo, SF Pro, 맑은 고딕, Malgun Gothic, SansSerif", Font.PLAIN, 15));
            donePanel.add(msg, BorderLayout.CENTER);

            JButton doneBtn = new JButton("확인");
            doneBtn.setFont(new Font("Apple SD Gothic Neo, SF Pro, 맑은 고딕, Malgun Gothic, SansSerif", Font.BOLD, 14));
            doneBtn.setBackground(new Color(240, 240, 240));
            doneBtn.setFocusPainted(false);
            doneBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
            doneBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(Color.WHITE);
            btnPanel.add(doneBtn);
            donePanel.add(btnPanel, BorderLayout.SOUTH);

            doneDialog.getContentPane().add(donePanel);
            doneDialog.setSize(320, 140);
            doneDialog.setLocationRelativeTo(dialog);

            doneBtn.addActionListener(ev -> doneDialog.dispose());
            doneDialog.setVisible(true);

            result[0] = studentId;
            result[1] = pw;
            result[2] = hint;
            result[3] = String.valueOf(timerMin);
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });

        dialog.setVisible(true);

        if (result[0] == null) return null;
        return result;
    }
}