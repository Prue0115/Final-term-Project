import java.net.*;
import java.io.*;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

public class Config {
    public static final String API_SERVER = "http://campusseat.kro.kr:8888"; // API 서버 주소
    public static final String CURRENT_VERSION = "1.0.0"; // 현재 버전

    // 서버에서 최신 버전 정보 받아오기
    public static JSONObject getLatestVersionInfo() {
        try {
            URI uri = new URI(API_SERVER + "/api/version/latest");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    return new JSONObject(sb.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveUserInfo(String studentId, String pw, String hint, int timerMin) {
        try {
            JSONObject data = new JSONObject();
            data.put("학번", studentId);
            data.put("암호", pw);
            data.put("암호힌트", hint);
            data.put("타이머", timerMin);

            // 한국 시간(Asia/Seoul)으로 실행 시각 추가
            String now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            data.put("날짜-시간", now);

            URL url = new java.net.URI(API_SERVER + "/user").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = data.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("API 서버에 저장 요청 성공!");
            } else {
                System.out.println("API 서버 응답 코드: " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getInstallPathFromRegistry() {
        try {
            String[] cmd = {
                "powershell",
                "-Command",
                "(Get-ItemProperty -Path 'HKCU:\\Software\\CampusSeat' -Name 'InstallPath').InstallPath"
            };
            Process proc = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF-8"));
            String path = br.readLine();
            if (path != null && !path.trim().isEmpty()) return path.trim();
        } catch (Exception ignored) {}
        return "C:\\Program Files\\CampusSeat";
    }

    /**
     * 최신 버전이 있으면 업데이트 안내 후 CampusSeat_update.exe 실행 (카카오톡 방식)
     * @param downloadUrl 서버에서 받은 최신 CampusSeat.exe 다운로드 URL
     */
    public static void runUpdateProcess(String downloadUrl) {
        int result = JOptionPane.showConfirmDialog(
            null,
            "새 버전이 있습니다. 지금 업데이트하시겠습니까?",
            "업데이트 안내",
            JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            try {
                // 업데이트 폴더 경로
                String updateDir = System.getenv("LOCALAPPDATA") + "\\CampusSeat\\update";
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

    public static void main(String[] args) {
        // 1. 최신 버전 정보 조회
        JSONObject latest = getLatestVersionInfo();
        if (latest != null) {
            String serverVersion = latest.optString("버전");
            String downloadUrl = latest.optString("다운로드URL");
            if (!CURRENT_VERSION.equals(serverVersion)) {
                runUpdateProcess(downloadUrl);
                return;
            }
        }

        // 2. 사용자 정보 입력 다이얼로그 호출
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
