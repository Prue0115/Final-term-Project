import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.plaf.basic.BasicPasswordFieldUI;

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

        // 전체 레이아웃: BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            private ImageIcon bgIcon = new ImageIcon("images/background.jpg"); // 또는 png
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = bgIcon.getImage();
                if (img != null && bgIcon.getIconWidth() > 0 && bgIcon.getIconHeight() > 0) {
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.WHITE);
                    g.drawString("배경 이미지를 불러올 수 없습니다.", 50, 50);
                }
            }
        };

        // 우측 상단 전원 버튼 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // 전원 아이콘 버튼 (클릭 시 팝업 메뉴)
        ImageIcon rawIcon = new ImageIcon("images/power-button.png");
        // 원하는 크기로 조절 (예: 32x32)
        Image scaledImg = rawIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        JButton powerBtn = new JButton();
        powerBtn.setToolTipText("전원 메뉴");
        powerBtn.setBackground(new Color(0,0,0,0));
        powerBtn.setBorderPainted(false);
        powerBtn.setFocusPainted(false);
        powerBtn.setContentAreaFilled(false);
        powerBtn.setIcon(scaledIcon); // 크기 조절된 아이콘 사용

        // 팝업 메뉴 생성
        JPopupMenu powerMenu = new JPopupMenu();
        JMenuItem shutdownItem = new JMenuItem("시스템 종료");
        shutdownItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "정말 시스템을 종료하시겠습니까?", "종료 확인", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    Runtime.getRuntime().exec("shutdown -s -t 0");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "시스템 종료 명령 실행 실패", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JMenuItem rebootItem = new JMenuItem("재시동");
        rebootItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "정말 시스템을 재시동하시겠습니까?", "재시동 확인", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    Runtime.getRuntime().exec("shutdown -r -t 0");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "시스템 재시동 명령 실행 실패", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        powerMenu.add(shutdownItem);
        powerMenu.add(rebootItem);

        powerBtn.addActionListener(e -> powerMenu.show(powerBtn, 0, powerBtn.getHeight()));

        // 오른쪽 끝에 버튼 배치
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);
        rightPanel.add(powerBtn);
        topPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 중앙 배치용 패널
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // 배경 투명
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        datetimeLabel = new JLabel("", SwingConstants.CENTER);
        datetimeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 40));
        datetimeLabel.setForeground(Color.WHITE);
        datetimeLabel.setPreferredSize(new Dimension(400, 50));
        centerPanel.add(datetimeLabel, gbc);

        gbc.gridy++;
        timerLabel = new JLabel("", SwingConstants.CENTER);
        timerLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 24));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setPreferredSize(new Dimension(400, 30));
        centerPanel.add(timerLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;

        // ====== 화면 크기에 따라 비밀번호 입력 필드와 도움말 버튼을 동적으로 아래쪽에 배치 ======
        // 화면 높이의 60% 위치에 배치
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int topMargin = (int)(screenHeight * 0.6);

        gbc.insets = new Insets(topMargin, 20, 20, 20);

        JPanel pwAndHelpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pwAndHelpPanel.setOpaque(false);

        // 비밀번호 입력 필드 생성
        pwField = new JPasswordField();
        pwField.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        pwField.setBackground(new Color(220, 220, 220, 220));
        pwField.setOpaque(false);

        // 둥근 테두리와 배경을 위한 커스텀 UI 적용
        pwField.setUI(new BasicPasswordFieldUI() {
            @Override
            protected void paintSafely(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 40;
                Shape round = new RoundRectangle2D.Float(0, 0, pwField.getWidth()-1, pwField.getHeight()-1, arc, arc);
                g2.setColor(new Color(220, 220, 220, 220));
                g2.fill(round);
                g2.setColor(new Color(180, 180, 180));
                g2.setStroke(new BasicStroke(2));
                g2.draw(round);

                // 플레이스홀더(암호 입력) 표시
                if (pwField.getPassword().length == 0 && !pwField.isFocusOwner()) {
                    g2.setFont(new Font("맑은 고딕", Font.BOLD, 18));
                    g2.setColor(new Color(255, 255, 255, 140)); // 반투명 흰색
                    FontMetrics fm = g2.getFontMetrics();
                    String placeholder = "암호 입력";
                    int x = 15;
                    int y = (pwField.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(placeholder, x, y);
                }

                g2.dispose();
                super.paintSafely(g);
            }
        });
        pwField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 크기 조정
        Dimension pwSize = new Dimension(155, 35);
        pwField.setPreferredSize(pwSize);
        pwField.setMinimumSize(pwSize);
        pwField.setMaximumSize(pwSize);

        // 플레이스홀더 갱신을 위한 리스너 추가
        pwField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { pwField.repaint(); }
            public void focusLost(java.awt.event.FocusEvent e) { pwField.repaint(); }
        });
        pwField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { pwField.repaint(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { pwField.repaint(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { pwField.repaint(); }
        });

        // 도움말 버튼 생성
        int helpIconSize = 25; // 원하는 크기로 조절
        ImageIcon helpIcon = new ImageIcon(
            new ImageIcon("images/help.png").getImage().getScaledInstance(helpIconSize, helpIconSize, Image.SCALE_SMOOTH)
        );
        JButton helpBtn = new JButton(helpIcon);
        helpBtn.setBackground(new Color(0,0,0,0)); // 배경 투명
        helpBtn.setFocusPainted(false);
        helpBtn.setBorderPainted(false);
        helpBtn.setContentAreaFilled(false); // 배경 제거
        helpBtn.setToolTipText("도움말");
        helpBtn.setPreferredSize(new Dimension(helpIconSize, helpIconSize)); // 버튼 크기를 아이콘과 동일하게
        helpBtn.setMaximumSize(new Dimension(helpIconSize, helpIconSize));
        helpBtn.setMinimumSize(new Dimension(helpIconSize, helpIconSize));
        helpBtn.setSize(new Dimension(helpIconSize, helpIconSize));
        helpBtn.addActionListener(e -> JOptionPane.showMessageDialog(
            this,
            "관리자 : 010-9903-5655",
            "도움말",
            JOptionPane.INFORMATION_MESSAGE,
            helpIcon // 다이얼로그에도 같은 아이콘 사용
        ));

        // ====== 비밀번호 입력 필드와 도움말 버튼을 나란히 배치 (이전 위치로) ======
        gbc.gridy++;
        Dimension screenSize2 = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight2 = screenSize2.height;
        int pwMargin = (int)(screenHeight2 * 0.6); // 화면 높이의 60% 위치
        gbc.insets = new Insets(pwMargin, 0, 0, 0);
        pwAndHelpPanel.add(pwField);
        pwAndHelpPanel.add(helpBtn);
        centerPanel.add(pwAndHelpPanel, gbc);

        // ====== 힌트 라벨 위치 ======
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0); // 여백 없음
        hintLabel = new JLabel(" ", SwingConstants.CENTER); // 공백으로 초기화
        hintLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        hintLabel.setForeground(Color.LIGHT_GRAY);
        hintLabel.setPreferredSize(new Dimension(280, 24)); // 최소 높이 지정(원하는 값으로)
        centerPanel.add(hintLabel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

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
            unlockScreen("비밀번호가 맞습니다. 잠금 해제");
        } else if (pw.equals("020115")) {
            unlocked = true;
            unlockScreen("관리자 권한으로 잠금 해제");
        } else {
            hintLabel.setText(hint); // "힌트: " 없이 힌트만 표시
            JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            pwField.setText("");
        }
    }

    private void unlockScreen(String msg) {
        JOptionPane.showMessageDialog(this, msg, "잠금 해제", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}