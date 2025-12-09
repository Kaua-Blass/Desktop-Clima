package clima;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField txtCidade;

    @FXML
    private Label lblTime, lblTemperature, lblWindSpeed,
                  lblWindDirection, lblIsDay, lblWeatherCode;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        String cidade = txtCidade.getText();
        buscarLocalizacao(cidade);
    }

    private void buscarLocalizacao(String cidade) {
        try {
            String cidadeEncode = URLEncoder.encode(cidade, StandardCharsets.UTF_8.toString());

            URL url = new URL(
                "https://geocoding-api.open-meteo.com/v1/search?name=" + cidadeEncode
            );

            JsonObject json = JsonParser.parseReader(new InputStreamReader(url.openStream())).getAsJsonObject();
            JsonArray results = json.getAsJsonArray("results");

            if (results == null || results.size() == 0){
                lblWeatherCode.setText("Cidade não encontrada!");
                return;
            }

            JsonObject loc = results.get(0).getAsJsonObject();

            double lat = loc.get("latitude").getAsDouble();
            double lon = loc.get("longitude").getAsDouble();

            buscarClima(lat, lon);

        } catch (Exception e) {
            lblWeatherCode.setText("Erro ao buscar cidade");
        }
    }

    private void buscarClima(double lat, double lon){
        try {
            URL url = new URL(
                "https://api.open-meteo.com/v1/forecast?latitude=" + lat +
                        "&longitude=" + lon + "&current_weather=true"
            );

            JsonObject json = JsonParser.parseReader(new InputStreamReader(url.openStream())).getAsJsonObject();
            JsonObject current = json.getAsJsonObject("current_weather");

            lblTime.setText(current.get("time").getAsString());
            lblTemperature.setText(current.get("temperature").getAsDouble() + " °C");
            lblWindSpeed.setText(current.get("windspeed").getAsDouble() + " km/h");
            lblWindDirection.setText(current.get("winddirection").getAsDouble() + "°");

            lblIsDay.setText(current.get("is_day").getAsInt() == 1 ? "Dia" : "Noite");
            lblWeatherCode.setText(converterWeatherCode(current.get("weathercode").getAsInt()));

        } catch (Exception e) {
            lblWeatherCode.setText("Erro obtendo clima");
        }
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}
