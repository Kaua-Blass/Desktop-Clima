package clima;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform; // Import necessário para JavaFX threads

// Importa a nova classe de serviço e a classe interna de dados
import clima.OpenMeteoService.DadosClima; 

public class FXMLDocumentController implements Initializable {

    // Instância do serviço de backend
    private final OpenMeteoService meteoService = new OpenMeteoService();

    @FXML
    private TextField txtCidade;

    @FXML
    private Label lblTime, lblTemperature, lblWindSpeed,
                  lblWindDirection, lblIsDay, lblWeatherCode;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        String cidade = txtCidade.getText();
        
        // Limpa e informa o status de busca (Thread do JavaFX)
        Platform.runLater(() -> {
            lblWeatherCode.setText("Buscando dados...");
            lblTime.setText("");
            lblTemperature.setText("");
            lblWindSpeed.setText("");
            lblWindDirection.setText("");
            lblIsDay.setText("");
        });

        // Executa a chamada de API (demorada) em uma nova Thread
        new Thread(() -> {
            try {
                // 1. Chama o serviço de backend
                DadosClima dados = meteoService.buscarClimaPorCidade(cidade);
                
                // 2. Atualiza a interface (de volta na Thread do JavaFX)
                Platform.runLater(() -> {
                    // Adicionamos um espaço para o valor não colar no rótulo estático do FXML
                    lblTime.setText(" " + dados.tempo); 
                    lblTemperature.setText(" " + dados.temperatura + " °C");
                    lblWindSpeed.setText(" " + dados.velocidadeVento + " km/h");
                    lblWindDirection.setText(" " + dados.direcaoVento + "°");
                    
                    // Este label (lblIsDay) não tem rótulo no seu FXML, mas é mantido no código
                    lblIsDay.setText(" " + dados.diaOuNoite); 
                    
                    // Condição (lblWeatherCode) - Recebe o nome do clima
                    lblWeatherCode.setText(" " + dados.codigoClima);
                });

            } catch (Exception e) {
                // 3. Em caso de erro, atualiza a interface com a mensagem
                Platform.runLater(() -> {
                    // Exibe a mensagem de erro no campo Condição (lblWeatherCode)
                    lblWeatherCode.setText(" Erro: " + e.getMessage()); 
                    
                    // Limpar outros labels
                    lblTime.setText("");
                    lblTemperature.setText("");
                    lblWindSpeed.setText("");
                    lblWindDirection.setText("");
                    lblIsDay.setText("");
                });
                System.err.println("Erro na busca: " + e.getMessage());
            }
        }).start(); // Inicia a nova thread
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}