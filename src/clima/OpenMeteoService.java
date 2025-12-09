package clima;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenMeteoService {

    // Classe interna para retornar os dados do clima de forma organizada
    public static class DadosClima {
        public String tempo;
        public double temperatura;
        public double velocidadeVento;
        public double direcaoVento;
        public String diaOuNoite;
        public String codigoClima;

        public DadosClima(String tempo, double temperatura, double velocidadeVento,
                          double direcaoVento, String diaOuNoite, String codigoClima) {
            this.tempo = tempo;
            this.temperatura = temperatura;
            this.velocidadeVento = velocidadeVento;
            this.direcaoVento = direcaoVento;
            this.diaOuNoite = diaOuNoite;
            this.codigoClima = codigoClima;
        }
    }

    /**
     * Busca as coordenadas geográficas de uma cidade e, em seguida,
     * busca os dados do clima para essas coordenadas.
     */
    public DadosClima buscarClimaPorCidade(String cidade) throws Exception {
        double[] coordenadas = buscarCoordenadas(cidade);

        if (coordenadas == null) {
            throw new Exception("Cidade não encontrada!");
        }

        return buscarClima(coordenadas[0], coordenadas[1]);
    }

    private double[] buscarCoordenadas(String cidade) throws Exception {
        String cidadeEncode = URLEncoder.encode(cidade, StandardCharsets.UTF_8.toString());

        URL url = new URL(
            "https://geocoding-api.open-meteo.com/v1/search?name=" + cidadeEncode
        );

        JsonObject json = JsonParser.parseReader(new InputStreamReader(url.openStream())).getAsJsonObject();
        JsonArray results = json.getAsJsonArray("results");

        if (results == null || results.size() == 0){
            return null; // Cidade não encontrada
        }

        JsonObject loc = results.get(0).getAsJsonObject();

        double lat = loc.get("latitude").getAsDouble();
        double lon = loc.get("longitude").getAsDouble();

        return new double[]{lat, lon};
    }

    private DadosClima buscarClima(double lat, double lon) throws Exception {
        URL url = new URL(
            "https://api.open-meteo.com/v1/forecast?latitude=" + lat +
            "&longitude=" + lon + "&current_weather=true"
        );

        JsonObject json = JsonParser.parseReader(new InputStreamReader(url.openStream())).getAsJsonObject();
        JsonObject current = json.getAsJsonObject("current_weather");

        String tempo = current.get("time").getAsString();
        double temperatura = current.get("temperature").getAsDouble();
        double velocidadeVento = current.get("windspeed").getAsDouble();
        double direcaoVento = current.get("winddirection").getAsDouble();
        String diaOuNoite = current.get("is_day").getAsInt() == 1 ? "Dia" : "Noite";
        String codigoClima = converterWeatherCode(current.get("weathercode").getAsInt());

        return new DadosClima(tempo, temperatura, velocidadeVento, direcaoVento, diaOuNoite, codigoClima);
    }

    private String converterWeatherCode(int code){
        switch(code){
            case 0: return "Céu limpo";
            case 1:
            case 2:
            case 3: return "Parcialmente nublado";
            case 45:
            case 48: return "Neblina";
            case 51:
            case 53:
            case 55: return "Garoa";
            case 61:
            case 63:
            case 65: return "Chuva";
            case 80:
            case 81:
            case 82: return "Pancadas de chuva";
            case 95: return "Tempestade";
            default: return "Desconhecido";
        }
    }
}