import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestUSDA {
    public static void main(String[] args) throws Exception {
        String encodedQuery = URLEncoder.encode("apple", StandardCharsets.UTF_8.toString());
        String url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + encodedQuery + "&pageSize=5&api_key=cVuSa2ltJoN9hrks4cbdNDBrbe3IAztHtVCJFR4h";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body().substring(0, Math.min(600, response.body().length())));
    }
}
