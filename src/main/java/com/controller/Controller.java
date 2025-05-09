package com.controller;

import java.io.IOException;

import org.json.JSONObject;

import com.example.StockFetcher;
import com.example.Transicion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private TextField valorInicial;

    @FXML
    private TextField inicialPorcentage;

    @FXML
    private TextField inversionInicial;

    @FXML
    private TextField resultadoInicial;

    @FXML
    private TextField visiblePorcentage;

    @FXML
    private TextField resultadoVisible;

    @FXML
    private Button btnInicial;

    @FXML
    private Button btnBuscar;

    @FXML
    private TextField valorSymbol;

    @FXML
    private TextField symbol;

    @FXML
    private TextField cambioSymbol;

    @FXML
    private Button btndca;

    @FXML
    private Spinner<Double> balanceInicial;

    @FXML
    private Spinner<Double> deposito;

    @FXML
    private Spinner<Double> interesAnual;
    
    @FXML
    private Spinner<Integer> duracion;

    @FXML
    private VBox vboxDCA;

    @FXML
    private Label resultadoDCA;

    @FXML
    public void initialize() {
        // Configurar el botón "Calcular" para "Valor Inicial"
        btnInicial.setOnAction(event -> calcularValorInicial());

        btndca.setOnAction(event -> {
            try {
                calcularDCA();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnBuscar.setOnAction(event -> buscarStock());

        // Aplicar animación hover a los botones
        Transicion.aplicarTransicionHover(btnInicial);
        Transicion.aplicarTransicionHover(btnBuscar);
        Transicion.aplicarTransicionHover(btndca);

        //Inicializar los valores de los spinners
        balanceInicial.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000000.0, 100.0, 1.0));
        deposito.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000000.0, 50.0, 1.0));
        interesAnual.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100.0, 5.0, 0.1));
        duracion.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10, 1));
    }

    private void calcularValorInicial() {
        try {
            // Obtener valores de los campos de texto
            double valor = Double.parseDouble(valorInicial.getText());
            double porcentaje = Double.parseDouble(inicialPorcentage.getText());
            double inversion = Double.parseDouble(inversionInicial.getText());

            // Realizar el cálculo
            double cambioAbsoluto = valor * (porcentaje / 100);
            double nuevoCapital = valor + inversion;
            double nuevoPorcentaje = (cambioAbsoluto / nuevoCapital) * 100;

            // Mostrar el resultado
            resultadoInicial.setText(String.format("%.2f%%", nuevoPorcentaje));
        } catch (NumberFormatException e) {
            resultadoInicial.setText("Error");
        }
    }

    private void calcularDCA() throws IOException {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dca.fxml"));
        Parent dca = loader.load();

        controllerDCA dcaController = loader.getController();

        // Obtener los valores de los spinners
        dcaController.setBalanceInicial((double) balanceInicial.getValue());
        dcaController.setDeposito((double) deposito.getValue());
        dcaController.setInteresAnual((double) interesAnual.getValue());
        dcaController.setDuracion((int) duracion.getValue());

        dcaController.resultadoDCA(); // Llamar al método para calcular el resultado
        

        Stage nuevaVentana = new Stage();
        nuevaVentana.setTitle("Resultado DCA");
        nuevaVentana.setScene(new Scene(dca));
        nuevaVentana.show();

        } catch (NumberFormatException e) {
            resultadoVisible.setText("Error");
        }
    }

    private void buscarStock() {
        try {

        // Limpiar los campos de texto antes de buscar
        valorSymbol.clear();
        cambioSymbol.clear();

        // Obtener el símbolo del stock desde el campo de texto
        String symbol = this.symbol.getText().toUpperCase();

        // Llamar al método StockFetcher para obtener los datos del stock
        String resultado = StockFetcher.stock(symbol);

        // Procesar el JSON
        JSONObject json = new JSONObject(resultado);

        // Extraer los valores relevantes como String
        String precioActual = String.valueOf(json.getDouble("c"));
        String cambio = String.valueOf(json.getDouble("d"));
        String porcentajeCambio = String.valueOf(json.getDouble("dp"));
        // String maximo = String.valueOf(json.getDouble("h"));
        // String minimo = String.valueOf(json.getDouble("l"));
        // String apertura = String.valueOf(json.getDouble("o"));
        // String cierreAnterior = String.valueOf(json.getDouble("pc"));

        valorSymbol.setText(precioActual);
        cambioSymbol.setText(cambio + " | " + porcentajeCambio + "%");

        // Aplicar estilos dinámicos según el valor de "cambio"
        cambioSymbol.getStyleClass().removeAll("text-positive", "text-negative"); // Eliminar clases previas
        if (Double.parseDouble(cambio) >= 0) {
            cambioSymbol.getStyleClass().add("text-positive"); // Agregar clase positiva
            valorSymbol.getStyleClass().add("text-positive"); // Agregar clase positiva
        } else {
            cambioSymbol.getStyleClass().add("text-negative"); // Agregar clase negativa
            valorSymbol.getStyleClass().add("text-negative"); // Agregar clase negativa
        }
        } catch (Exception e) {
            e.printStackTrace();
            valorSymbol.setText("Error al buscar el stock.");
        }
    }
}
