package clima;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import clima.OpenMeteoService.DadosClima;

public class FXMLDocumentController implements Initializable {

    private final OpenMeteoService meteoService = new OpenMeteoService();

    @FXML
    private TextField txtCidade;

    @FXML
    private Label lblTime, lblTemperature, lblWindSpeed,
                  lblWindDirection, lblIsDay, lblWeatherCode;
    
    @FXML
    private Label lblStatus;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        String cidade = txtCidade.getText();

        Platform.runLater(() -> {
            lblStatus.setText("Buscando dados...");
            lblWeatherCode.setText("");
            lblTime.setText("");
            lblTemperature.setText("");
            lblWindSpeed.setText("");
            lblWindDirection.setText("");
            lblIsDay.setText("");
        });

        new Thread(() -> {
            try {
                DadosClima dados = meteoService.buscarClimaPorCidade(cidade);

                Platform.runLater(() -> {
                    lblTime.setText(" " + dados.tempo); 
                    lblTemperature.setText(" " + dados.temperatura + " °C");
                    lblWindSpeed.setText(" " + dados.velocidadeVento + " km/h");
                    lblWindDirection.setText(" " + dados.direcaoVento + "°");
                    lblIsDay.setText(" " + dados.diaOuNoite); 
                    lblWeatherCode.setText(" " + dados.codigoClima);
                    lblStatus.setText("");
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblStatus.setText("Erro: " + e.getMessage()); 
                    lblWeatherCode.setText("");
                    lblTime.setText("");
                    lblTemperature.setText("");
                    lblWindSpeed.setText("");
                    lblWindDirection.setText("");
                    lblIsDay.setText("");
                });
                System.err.println("Erro na busca: " + e.getMessage());
            }
        }).start();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}