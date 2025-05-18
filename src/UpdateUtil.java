import java.net.*;
import java.io.*;
import javax.swing.*;

public class UpdateUtil {
    // 새 버전 다운로드(AppData\CampusSeat\ update\CampusSeat.new)
    public static void downloadAndReplace(String downloadUrl) {
        try {
            String updateDir = getUpdateDir();
            File tmpFile = new File(updateDir, "CampusSeat.new");

            URL url = new URI(downloadUrl).toURL();
            InputStream in = url.openStream();
            FileOutputStream fos = new FileOutputStream(tmpFile);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            in.close();

            JOptionPane.showMessageDialog(null, "새 버전이 다운로드되었습니다.\n프로그램을 재시작해 주세요.", "업데이트 완료", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "다운로드 오류: " + e, "업데이트 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 숨김 업데이트 폴더 경로 지정
    public static String getUpdateDir() {
        String updateDir = System.getenv("LOCALAPPDATA") + "\\CampusSeat\\update";
        File updateFolder = new File(updateDir);
        if (!updateFolder.exists()) updateFolder.mkdirs();
        return updateDir;
    }

    // update.exe를 관리자 권한으로 실행 후 프로그램 종료
    public static void runUpdaterAndExit() {
        try {
            String updateDir = getUpdateDir();
            String updaterPath = updateDir + "\\CampusSeat_update.exe";
            String[] cmd = {
                "powershell",
                "Start-Process -FilePath '" + updaterPath + "' -Verb runAs"
            };
            new ProcessBuilder(cmd).start();
            System.exit(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "업데이트 실행 오류: " + e, "업데이트 실패", JOptionPane.ERROR_MESSAGE);
        }
    }
}