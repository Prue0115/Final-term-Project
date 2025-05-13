import java.net.*;
import java.io.*;
import org.json.JSONObject;

public class ApiUtil {
    public static void saveUserInfo(String studentId, String pw, String hint, int timerMin) {
        try {
            JSONObject data = new JSONObject();
            data.put("student_id", studentId);
            data.put("password", pw);
            data.put("hint", hint);
            data.put("timer_min", timerMin);

            // Main 클래스의 상수 사용
            URL url = new URI(Main.API_SERVER + "/save_user").toURL();
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