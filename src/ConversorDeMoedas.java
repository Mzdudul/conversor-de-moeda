import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversorDeMoedas {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/cd67f52a20eca367596224bd/latest/USD";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Conversor de Moedas ===");
        System.out.print("Digite o valor em USD: ");
        double valorUSD = scanner.nextDouble();
        scanner.nextLine(); // limpa buffer

        System.out.print("Digite a moeda de destino (ex: BRL, EUR, ARS): ");
        String moedaDestino = scanner.nextLine().toUpperCase();

        try {
            double taxa = obterTaxaCambio(moedaDestino);
            double convertido = valorUSD * taxa;
            System.out.printf("→ %.2f USD = %.2f %s%n", valorUSD, convertido, moedaDestino);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        scanner.close();
    }

    private static double obterTaxaCambio(String moeda) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro na API: " + response.statusCode());
        }

        String body = response.body();

        // Expressão regular para extrair a taxa de câmbio
        Pattern pattern = Pattern.compile("\"" + moeda + "\":\\s*(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        } else {
            throw new RuntimeException("Moeda não encontrada: " + moeda);
        }
    }
}