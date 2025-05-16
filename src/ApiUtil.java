import java.net.*;
import java.io.*;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ApiUtil {
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

            URL url = new URI(Main.API_SERVER + "/user").toURL();
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

    public void printApiInfo() {
        System.out.println(Main.API_SERVER);
        System.out.println(Main.CURRENT_VERSION);
    }
}