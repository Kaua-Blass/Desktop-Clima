package clima;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import clima.OpenMeteoService.DadosClima;

import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.collections.ObservableList;

public class FXMLDocumentController implements Initializable {

    private final OpenMeteoService meteoService = new OpenMeteoService();

    @FXML private TextField txtCidade;
    @FXML private Button btnBuscar;

    @FXML private Label lblTime;
    @FXML private Label lblTemperature;
    @FXML private Label lblWindSpeed;
    @FXML private Label lblWindDirection;
    @FXML private Label lblIsDay;
    @FXML private Label lblWeatherCode;

    @FXML private Label lblStatus;

    @FXML private VBox rootVBox;
    @FXML private ImageView logoImageView;
    @FXML private VBox dataPanelVBox;

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
    public void initialize(URL url, ResourceBundle rb) {
        final double BASE_WIDTH = 600.0;
        final double BASE_HEIGHT = 400.0;
        final double BASE_FONT = 12.0;
        final double BASE_SPACING = 20.0;
        final double BASE_PADDING = 30.0;
        final double BASE_INPUT_HEIGHT = 25.0;
        final double BASE_BUTTON_HEIGHT = 29.0;
        final double BASE_BUTTON_WIDTH = 56.0;
        final double BASE_IMAGE_HEIGHT = 82.0;
        final double BASE_IMAGE_WIDTH = 79.0;
        final double BASE_KEY_WIDTH = 130.0;
        final double BASE_VALUE_WIDTH = 200.0;
        final double MIN_SCALE = 0.7;
        final double MAX_SCALE = 2.0;

        if (rootVBox == null) return;

        rootVBox.sceneProperty().addListener((obsScene, oldScene, scene) -> {
            if (scene == null) return;

            DoubleBinding scale = Bindings.createDoubleBinding(() -> {
                double w = scene.getWidth();
                double h = scene.getHeight();
                double s = Math.min(w / BASE_WIDTH, h / BASE_HEIGHT);
                return Math.max(MIN_SCALE, Math.min(MAX_SCALE, s));
            }, scene.widthProperty(), scene.heightProperty());

            rootVBox.styleProperty().bind(Bindings.createStringBinding(() ->
                    String.format("-fx-font-size: %.2fpx;", BASE_FONT * scale.get()), scale));

            rootVBox.spacingProperty().bind(scale.multiply(BASE_SPACING));
            rootVBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                    new Insets(BASE_PADDING * scale.get(), BASE_PADDING * scale.get(),
                               BASE_PADDING * scale.get(), BASE_PADDING * scale.get()), scale));

            if (dataPanelVBox != null) {
                dataPanelVBox.prefWidthProperty().bind(scene.widthProperty().multiply(0.6));
                dataPanelVBox.maxWidthProperty().bind(scene.widthProperty().multiply(0.95));
            }

            if (txtCidade != null) {
                txtCidade.prefHeightProperty().bind(scale.multiply(BASE_INPUT_HEIGHT));
                txtCidade.prefWidthProperty().bind(Bindings.createDoubleBinding(() ->
                        Math.max(120.0, scene.getWidth() * 0.22 * scale.get()), scale));
            }

            if (btnBuscar != null) {
                btnBuscar.prefHeightProperty().bind(scale.multiply(BASE_BUTTON_HEIGHT));
                btnBuscar.prefWidthProperty().bind(scale.multiply(BASE_BUTTON_WIDTH));
            }

            if (logoImageView != null) {
                logoImageView.fitHeightProperty().bind(scale.multiply(BASE_IMAGE_HEIGHT));
                logoImageView.fitWidthProperty().bind(scale.multiply(BASE_IMAGE_WIDTH));
            }

            if (dataPanelVBox != null) {
                ObservableList<Node> rows = dataPanelVBox.getChildren();
                for (Node row : rows) {
                    if (row instanceof HBox) {
                        HBox h = (HBox) row;
                        ObservableList<Node> ch = h.getChildren();
                        if (ch.size() >= 2 && ch.get(0) instanceof Label && ch.get(1) instanceof Label) {
                            Label key = (Label) ch.get(0);
                            Label val = (Label) ch.get(1);
                            key.prefWidthProperty().bind(scale.multiply(BASE_KEY_WIDTH));
                            val.prefWidthProperty().bind(scale.multiply(BASE_VALUE_WIDTH));
                            val.setWrapText(true);
                        }
                    }
                }
            }
        });
    }
}