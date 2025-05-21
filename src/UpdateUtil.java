import java.io.*;
import javax.swing.*;

public class UpdateUtil {
    // 숨김 업데이트 폴더 경로 지정
    public static String getUpdateDir() {
        String updateDir = System.getenv("LOCALAPPDATA") + "\\CampusSeat\\update";
        File updateFolder = new File(updateDir);
        if (!updateFolder.exists()) updateFolder.mkdirs();
        return updateDir;
    }

    // update.exe를 관리자 권한으로 실행 후 프로그램 종료
    public static void runUpdaterAndExit(String downloadUrl) {
        try {
            String updateDir = getUpdateDir();
            String updaterPath = updateDir + "\\CampusSeat_update.exe";
            // downloadUrl을 인자로 넘김
            String[] cmd = {
                "powershell",
                "Start-Process -FilePath '" + updaterPath + "' -ArgumentList '" + downloadUrl + "' -Verb runAs"
            };
            new ProcessBuilder(cmd).start();
            System.exit(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "업데이트 실행 오류: " + e, "업데이트 실패", JOptionPane.ERROR_MESSAGE);
        }
    }
}