package com.ams.airManagement.service.implement;

import com.ams.airManagement.entity.Flights;
import com.ams.airManagement.repository.FlightsRepository;
import com.ams.airManagement.service.interfac.ChatServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;

@Service
public class ChatServiceImpl implements ChatServiceInterface {

    @Autowired
    private FlightsRepository flightsRepository;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final String systemPrompt = """
            Bạn là một tiếp viên hàng không thân thiện. Hãy trả lời ngắn gọn, lịch sự, dễ hiểu và sử dụng định dạng HTML với:<br/>
        - Xuống dòng bằng thẻ &lt;br/&gt;<br/>
        - Đánh dấu đầu dòng bằng dấu ➤<br/>
        - In đậm tiêu đề bằng thẻ &lt;b&gt;<br/>
        Nếu câu hỏi liên quan đến chính sách, hãy trả lời theo các chính sách được quy định sẵn.<br/>
        Nếu không tìm thấy chuyến bay, hãy gợi ý khách thử lại với điều kiện khác.<br/>
        Luôn sử dụng thẻ &lt;br/&gt; để xuống dòng thay vì ký tự \\n.
        """;

    @Override
    public String getChatResponse(String message) {
        message = message.toLowerCase(Locale.ROOT);

        if (message.contains("chuyến bay từ")) {
            return handleFlightRouteQuery(message);
        }

        if (message.contains("hãng") || message.contains("bamboo") || message.contains("vietjet") ||
                message.contains("vietnam airline") || message.contains("pacific")) {
            return handleAirlineQuery(message);
        }

        if (message.contains("hoàn") || message.contains("trả vé") || message.contains("chính sách") || message.contains("giá vé")) {
            return getPolicyResponse(message);
        }

        return callGeminiApi(message);
    }

    private String handleFlightRouteQuery(String message) {
        try {
            String[] parts = message.split("từ|đến|vào ngày|lúc|vào lúc");

            if (parts.length >= 3) {
                String from = normalizeProvince(parts[1].trim());
                String to = normalizeProvince(parts[2].trim());
                String dateStr = null;
                String timeStr = null;

                for (int i = 3; i < parts.length; i++) {
                    String part = parts[i].trim();
                    if (part.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*")) {
                        dateStr = part.split(" ")[0]; // Get date part
                    } else if (part.matches(".*\\d{1,2}:\\d{2}.*")) {
                        timeStr = part.split(" ")[0]; // Get time part
                    }
                }

                List<Flights> flights;

                if (dateStr != null && timeStr != null) {
                    flights = flightsRepository.findByRouteAndDateAndTime(from, to, dateStr, timeStr);
                } else if (dateStr != null) {
                    flights = flightsRepository.findByRouteAndDate(from, to, dateStr);
                } else if (timeStr != null) {
                    flights = flightsRepository.findByRouteAndTime(from, to, timeStr);
                } else {
                    flights = flightsRepository.findByRoute(from, to);
                }

                if (flights.isEmpty()) {
                    return "Hiện tại không tìm thấy chuyến bay phù hợp. <br/> ➤ Vui lòng thử ngày khác hoặc địa điểm khác.";
                }

                StringBuilder sb = new StringBuilder("➤ Các chuyến bay tìm được (tối đa 10 chuyến):<br/><br/>");
                int count = 0;
                for (Flights f : flights) {
                    if (count >= 10) break;

                    sb.append("<b>➤ Mã chuyến bay:</b> ").append(f.getFlightId()).append("<br/>")
                            .append("<b>   Hãng:</b> ").append(f.getAirline()).append("<br/>")
                            .append("<b>   Ngày giờ bay:</b> ").append(f.getTakeoffDate()).append(" ").append(f.getTakeoffTime()).append("<br/>")
                            .append("<b>   Điểm đi:</b> ").append(f.getDepartureProvince().getProvinceId()).append("<br/>")
                            .append("<b>   Ngày giờ đến:</b> ").append(f.getLandingDate()).append(" ").append(f.getLandingTime()).append("<br/>")
                            .append("<b>   Điểm đến:</b> ").append(f.getDestinationProvince().getProvinceId()).append("<br/>")
                            .append("<b>   Giá vé:</b> ").append(f.getTotalPrice()).append(" VND<br/><br/>");
                    count++;
                }
                return sb.toString();
            }
        } catch (Exception e) {
            return "❌ Sai format rồi đó, nhập lại đi!.";
        }

        return "Không hiểu yêu cầu. ➤ Vui lòng nhập: \"Có chuyến bay từ HCM đến HN vào ngày 1/4/2021 lúc 5:45 không?\"";
    }

    private String handleAirlineQuery(String message) {
        try {
            String airline = "Bamboo Airways";
            if (message.contains("vietjet")) airline = "Vietjet";
            if (message.contains("vietnam airline")) airline = "Vietnam Airlines";
            if (message.contains("pacific")) airline = "Pacific Airlines";

            String dateStr = null;
            String timeStr = null;

            String[] parts = message.split("vào ngày|lúc|vào lúc");
            if (parts.length >= 2) {
                String part = parts[1].trim();
                if (part.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*")) {
                    dateStr = part.split(" ")[0];
                } else if (part.matches(".*\\d{1,2}:\\d{2}.*")) {
                    timeStr = part.split(" ")[0];
                }
            }

            List<Flights> flights;

            if (dateStr != null && timeStr != null) {
                flights = flightsRepository.findByAirlineAndDateAndTime(airline, dateStr, timeStr);
            } else if (dateStr != null) {
                flights = flightsRepository.findByAirlineAndDate(airline, dateStr);
            } else if (timeStr != null) {
                flights = flightsRepository.findByAirlineAndTime(airline, timeStr);
            } else {
                flights = flightsRepository.findByAirline(airline);
            }

            if (flights.isEmpty()) {
                return "Không có chuyến bay nào thuộc hãng '" + airline + "'" +
                        (dateStr != null ? " vào ngày " + dateStr : "") +
                        (timeStr != null ? " lúc " + timeStr : "") + ".";
            }

            StringBuilder sb = new StringBuilder("➤ Danh sách chuyến bay của " + airline +
                    (dateStr != null ? " ngày " + dateStr : "") +
                    (timeStr != null ? " lúc " + timeStr : "") +
                    " (tối đa 10 chuyến):<br/><br/>");
            int count = 0;
            for (Flights f : flights) {
                if (count >= 10) break;

                sb.append("<b>➤ Mã chuyến bay:</b> ").append(f.getFlightId()).append("<br/>")
                        .append("<b>   Ngày giờ bay:</b> ").append(f.getTakeoffDate()).append(" ").append(f.getTakeoffTime()).append("<br/>")
                        .append("<b>   Điểm đi:</b> ").append(f.getDepartureProvince().getProvinceId()).append("<br/>")
                        .append("<b>   Ngày giờ đến:</b> ").append(f.getLandingDate()).append(" ").append(f.getLandingTime()).append("<br/>")
                        .append("<b>   Điểm đến:</b> ").append(f.getDestinationProvince().getProvinceId()).append("<br/>")
                        .append("<b>   Giá vé:</b> ").append(f.getTotalPrice()).append(" VND<br/><br/>");
                count++;
            }
            return sb.toString();
        } catch (Exception e) {
            return "❌ Không thể xử lý yêu cầu về hãng bay.";
        }
    }

    private String normalizeProvince(String input) {
        return input.toUpperCase(Locale.ROOT).replaceAll("[^A-Z]", "");
    }

    private String getPolicyResponse(String message) {
        message = message.toLowerCase(Locale.ROOT);

        if (message.contains("hoàn vé") || message.contains("trả vé") || message.contains("hủy vé")) {
            return """
            <b>➤ CHÍNH SÁCH HOÀN VÉ/TRẢ VÉ</b><br/><br/>
            <b>1. Điều kiện hoàn vé:</b><br/>
            - Hoàn 100% phí vé nếu hủy trước 7 ngày so với ngày khởi hành.<br/>
            - Hoàn 70% phí vé nếu hủy từ 3-7 ngày trước ngày khởi hành.<br/>
            - Hoàn 50% phí vé nếu hủy từ 24h-72h trước giờ khởi hành.<br/>
            - Không hoàn vé nếu hủy trong vòng 24h trước giờ khởi hành.<br/><br/>
            
            <b>2. Thủ tục hoàn vé:</b><br/>
            - Vé mua tại website/ứng dụng: Hoàn tiền tự động trong 7-10 ngày làm việc.<br/>
            - Vé mua tại đại lý: Liên hệ trực tiếp nơi mua vé để làm thủ tục.<br/><br/>
            
            <b>3. Lưu ý:</b><br/>
            - Vé khuyến mãi không được hoàn trừ trường hợp chuyến bay bị hủy.<br/>
            - Phí dịch vụ không được hoàn lại.<br/>
            """;
        }

        if (message.contains("giá vé") || message.contains("giá cả") || message.contains("phí vé")) {
            return """
            <b>➤ CHÍNH SÁCH GIÁ VÉ</b><br/><br/>
            <b>1. Giá vé bao gồm:</b><br/>
            - Giá vé cơ bản<br/>
            - Thuế, phí sân bay<br/>
            - Phí nhiên liệu (nếu có)<br/><br/>
            
            <b>2. Yếu tố ảnh hưởng giá:</b><br/>
            - Thời điểm đặt vé (càng sớm càng rẻ)<br/>
            - Mùa cao điểm/thấp điểm<br/>
            - Hạng ghế (Phổ thông/Thương gia)<br/><br/>
            
            <b>3. Chính sách vé trẻ em:</b><br/>
            - Trẻ dưới 2 tuổi: 10% giá vé người lớn<br/>
            - Trẻ 2-12 tuổi: 75% giá vé người lớn<br/>
            """;
        }

        if (message.contains("hành lý") || message.contains("ký gửi") || message.contains("xách tay")) {
            return """
            <b>➤ CHÍNH SÁCH HÀNH LÝ</b><br/><br/>
            <b>1. Hành lý xách tay:</b><br/>
            - Tối đa 7kg/chỗ ngồi<br/>
            - Kích thước không quá 56x36x23cm<br/><br/>
            
            <b>2. Hành lý ký gửi:</b><br/>
            - Phổ thông: 20kg miễn phí<br/>
            - Thương gia: 30kg miễn phí<br/>
            - Phụ thu 5% giá vé/kg vượt quá<br/><br/>
            
            <b>3. Hàng cấm:</b><br/>
            - Chất lỏng quá 100ml<br/>
            - Vũ khí, chất dễ cháy<br/>
            - Đồ ăn có mùi đặc biệt<br/>
            """;
        }

        if (message.contains("đổi vé") || message.contains("thay đổi")) {
            return """
            <b>➤ CHÍNH SÁCH ĐỔI VÉ</b><br/><br/>
            <b>1. Điều kiện đổi vé:</b><br/>
            - Đổi trước 24h so với giờ khởi hành<br/>
            - Chỉ áp dụng cho cùng hành trình<br/>
            - Vé còn hạn sử dụng<br/><br/>
            
            <b>2. Phí đổi vé:</b><br/>
            - Đổi trước 7 ngày: Miễn phí<br/>
            - Đổi trước 3 ngày: 10% giá vé<br/>
            - Đổi trong vòng 24h: 20% giá vé<br/><br/>
            
            <b>3. Thủ tục:</b><br/>
            - Vé mua online: Đổi trực tiếp trên website<br/>
            - Vé mua tại đại lý: Liên hệ nơi mua vé<br/>
            """;
        }

        if (message.contains("trẻ em") || message.contains("em bé") || message.contains("trẻ sơ sinh")) {
            return """
            <b>➤ CHÍNH SÁCH TRẺ EM/EM BÉ</b><br/><br/>
            <b>1. Quy định độ tuổi:</b><br/>
            - Em bé: Dưới 2 tuổi (ngồi cùng người lớn)<br/>
            - Trẻ em: Từ 2-12 tuổi (chỗ ngồi riêng)<br/><br/>
            
            <b>2. Giấy tờ cần thiết:</b><br/>
            - Giấy khai sinh bản gốc hoặc bản sao công chứng<br/>
            - Hộ chiếu (đối với chuyến bay quốc tế)<br/><br/>
            
            <b>3. Tiện ích:</b><br/>
            - Ưu tiên lên máy bay trước<br/>
            - Suất ăn riêng cho trẻ em<br/>
            - Ghế ngồi an toàn theo yêu cầu<br/>
            """;
        }

        return """
        <b>➤ CÁC CHÍNH SÁCH HỖ TRỢ</b><br/><br/>
        <b>1. Thay đổi thông tin:</b><br/>
        - Sửa tên: Phí 200.000 VND/lần (tối đa 3 ký tự)<br/>
        - Đổi ngày bay: Xem chính sách đổi vé<br/><br/>
        
        <b>2. Check-in:</b><br/>
        - Online: Mở trước 24h đến 1h trước giờ bay<br/>
        - Tại sân bay: Trước 2h với quốc tế, 1h với nội địa<br/><br/>
        
        <b>3. Hỗ trợ đặc biệt:</b><br/>
        - Hành khách khuyết tật: Liên hệ trước ít nhất 48h<br/>
        - Phụ nữ mang thai: Cần giấy khám sức khỏe sau tuần 28<br/>
        """;
    }

    private String callGeminiApi(String message) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String requestBody = """
            {
              "contents": [
                {
                  "role": "user",
                  "parts": [{"text": "%s"}]
                },
                {
                  "role": "model",
                  "parts": [{"text": "%s"}]
                }
              ]
            }
            """.formatted(message, systemPrompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            return json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } catch (Exception e) {
            return "Hết token rồi đó, nạp tiền đi!.";
        }
    }
}
