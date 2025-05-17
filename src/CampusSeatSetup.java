import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.*;

public class CampusSeatSetup {
    // 선택된 설치 경로를 저장하는 static 필드
    private static String installPath = System.getenv().getOrDefault("MYAPP_PATH", "C:\\Program Files\\CampusSeat");

    /**
     * 카카오톡 설치처럼 폴더 선택 다이얼로그를 표시하고, 선택된 경로를 저장합니다.
     * 필요한 디스크 공간과 남은 디스크 공간을 실제로 계산해서 표시합니다.
     * 설치 버튼 클릭 시 CampusSeat 폴더 생성, CampusSeat.exe 복사, 바탕화면 바로가기 생성
     */
    public static void init() {
        // 색상 정의
        Color bgColor = new Color(250, 250, 250);
        Color btnBlue = new Color(0, 120, 215);
        Color btnText = Color.WHITE;
        Color borderGray = new Color(200, 200, 200);

        long requiredBytes = 120L * 1024 * 1024; // 120MB

        JDialog dialog = new JDialog((Frame) null, "자리 지키미 설치", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 320);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(bgColor);

        // 상단 아이콘 및 제목
        JLabel iconLabel = new JLabel(new ImageIcon("images/campusseat_icon.png"));
        iconLabel.setBounds(20, 18, 32, 32);
        dialog.add(iconLabel);

        JLabel titleLabel = new JLabel("자리 지키미를 설치할 폴더를 선택해 주세요.");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        titleLabel.setBounds(15, 20, 300, 30);
        dialog.add(titleLabel);

        // 안내 메시지
        JLabel msg1 = new JLabel("자리 지키미을 다음 폴더에 설치합니다.");
        msg1.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        msg1.setBounds(15, 60, 400, 20);
        dialog.add(msg1);

        JLabel msg2 = new JLabel("다른 폴더에 설치하시려면 '찾아보기' 버튼을 눌러서 다른 폴더를 선택해 주세요.");
        msg2.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        msg2.setBounds(15, 78, 500, 20);
        dialog.add(msg2);

        // 설치 폴더 입력
        JLabel folderLabel = new JLabel("설치 폴더");
        folderLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        folderLabel.setBounds(20, 140, 80, 25);
        dialog.add(folderLabel);

        JTextField pathField = new JTextField(installPath); // 반드시 위쪽에 선언
        pathField.setBounds(100, 140, 340, 25);
        pathField.setBackground(Color.WHITE);
        pathField.setBorder(BorderFactory.createLineBorder(borderGray));
        dialog.add(pathField);

        JButton browseBtn = new JButton("찾아보기...");
        browseBtn.setBounds(450, 140, 110, 25);
        browseBtn.setBackground(Color.WHITE);
        browseBtn.setFocusPainted(false);
        dialog.add(browseBtn);

        // 디스크 공간 안내
        JLabel diskLabel = new JLabel();
        diskLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        diskLabel.setBounds(20, 180, 250, 20);
        dialog.add(diskLabel);

        JLabel freeLabel = new JLabel();
        freeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        freeLabel.setBounds(270, 180, 300, 20);
        dialog.add(freeLabel);

        // 디스크 공간 계산 함수
        Runnable updateDiskInfo = () -> {
            File disk = new File(pathField.getText());
            while (!disk.exists()) {
                disk = disk.getParentFile();
                if (disk == null) break;
            }
            long freeBytes = (disk != null) ? disk.getFreeSpace() : 0;
            diskLabel.setText("필요한 디스크 공간: " + String.format("%.1f MB", requiredBytes / 1024.0 / 1024.0));
            freeLabel.setText("남은 디스크 공간: " + (freeBytes > 0 ? String.format("%.1f GB", freeBytes / 1024.0 / 1024.0 / 1024.0) : "알 수 없음"));
        };
        updateDiskInfo.run();

        // 폴더 변경 시 디스크 공간 정보 갱신
        pathField.addActionListener(e -> updateDiskInfo.run());
        pathField.addCaretListener(e -> updateDiskInfo.run());

        // 폴더 찾아보기 버튼
        browseBtn.addActionListener((ActionEvent e) -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception ignore) {}

            JDialog chooserDialog = new JDialog(dialog, "폴더 찾아보기", true);
            chooserDialog.setLayout(new BorderLayout());

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setBackground(Color.WHITE);

            JLabel msg = new JLabel("<html><b>CampusSeat를 다음 폴더에 설치합니다:</b></html>");
            msg.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            msg.setAlignmentX(Component.LEFT_ALIGNMENT);

            topPanel.add(Box.createVerticalStrut(10));
            topPanel.add(msg);
            topPanel.add(Box.createVerticalStrut(10));

            JFileChooser chooser = new JFileChooser(pathField.getText());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setApproveButtonText("확인");
            chooser.setApproveButtonToolTipText("이 폴더에 설치");

            chooserDialog.add(topPanel, BorderLayout.NORTH);
            chooserDialog.add(chooser, BorderLayout.CENTER);
            chooserDialog.setSize(420, 500);
            chooserDialog.setLocationRelativeTo(dialog);

            chooser.addActionListener(ev -> {
                if (ev.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File selectedDir = chooser.getSelectedFile();
                    if (selectedDir != null) {
                        pathField.setText(selectedDir.getAbsolutePath());
                        updateDiskInfo.run();
                    }
                    chooserDialog.dispose();
                } else if (ev.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                    chooserDialog.dispose();
                }
            });

            chooserDialog.setVisible(true);
        });

        // 설치 버튼
        JButton installBtn = new JButton("설치");
        installBtn.setBounds(360, 230, 100, 30);
        installBtn.setBackground(btnBlue);
        installBtn.setForeground(btnText);
        installBtn.setFocusPainted(false);
        installBtn.addActionListener(e -> {
            String basePath = pathField.getText().trim();

            // 항상 하위 폴더 이름을 CampusSeat로 고정
            File dir = new File(basePath, "CampusSeat");
            installPath = dir.getAbsolutePath();

            // 폴더가 없으면 생성
            if (!dir.exists()) dir.mkdirs();

            // CampusSeat.exe 복사 (설치 폴더)
            try {
                Path exeSource = Paths.get("resources/CampusSeat.exe"); // 실제 소스 경로로 수정
                Path exeTarget = Paths.get(installPath, "CampusSeat.exe");
                Files.copy(exeSource, exeTarget, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "CampusSeat.exe 복사 실패: " + ex.getMessage());
            }

            // 숨김 폴더(AppData\CampusSeat\ update) 생성 및 update.exe 복사
            try {
                String updateDir = System.getenv("LOCALAPPDATA") + "\\CampusSeat\\update";
                File updateFolder = new File(updateDir);
                if (!updateFolder.exists()) updateFolder.mkdirs();

                Path updaterSource = Paths.get("resources/CampusSeat_updata.exe"); // 실제 소스 경로
                Path updaterTarget = Paths.get(updateDir, "CampusSeat_updata.exe");
                Files.copy(updaterSource, updaterTarget, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "CampusSeat_updata.exe 복사 실패: " + ex.getMessage());
            }

            // 바탕화면에 바로가기 생성 (powershell 사용)
            try {
                String desktop = System.getProperty("user.home") + "\\Desktop";
                String shortcut = desktop + "\\CampusSeat.lnk";
                String exePath = installPath + "\\CampusSeat.exe";
                String[] cmd = {
                    "powershell",
                    "$s=(New-Object -COM WScript.Shell).CreateShortcut('" + shortcut + "');" +
                    "$s.TargetPath='" + exePath + "';$s.Save()"
                };
                new ProcessBuilder(cmd).start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "바로가기 생성 실패: " + ex.getMessage());
            }

            // 텍스트필드에도 실제 경로 반영
            pathField.setText(installPath);

            dialog.dispose();

            // 설치 후 사용자 정보 입력 다이얼로그 호출 (필요시)
            // SetupUserDialog.showDialog();
        });
        dialog.add(installBtn);

        // 취소 버튼
        JButton cancelBtn = new JButton("취소");
        cancelBtn.setBounds(470, 230, 90, 30);
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> {
            installPath = null;
            dialog.dispose();
        });
        dialog.add(cancelBtn);

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // 현재 선택된 설치 경로를 반환합니다.
    public static String getInstallPath() {
        return installPath;
    }
}