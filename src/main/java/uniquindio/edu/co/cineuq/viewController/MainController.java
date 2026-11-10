package uniquindio.edu.co.cineuq.viewController;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import uniquindio.edu.co.cineuq.exception.PeliculaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaNoDisponibleException;
import uniquindio.edu.co.cineuq.exception.CinemaUQException;
import uniquindio.edu.co.cineuq.model.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pantallas principales de cartelera, selección de asientos y venta de CineUQ.
 */
public class MainController {
    private static final NumberFormat COP = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private static final DateTimeFormatter HOUR = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] POSTER_COLORS = {"#d9cdbd", "#c6d3db", "#ead5c3", "#c9c8d0", "#c9d9c8", "#d2d7e8"};

    @FXML
    private VBox pageContent;
    @FXML
    private Label pageTitle;
    @FXML
    private Label pageDescription;
    @FXML
    private Button carteleraButton;
    @FXML
    private Button funcionesButton;
    @FXML
    private Button ventasButton;
    @FXML
    private Button administracionButton;

    private CinemaUQ cinema;
    private Operador operador;
    private Administrador administrador;
    private List<Producto> products;
    private Funcion selectedFunction;
    private final List<AsientoFuncion> selectedSeats = new ArrayList<>();
    private final Map<Producto, Integer> selectedProducts = new LinkedHashMap<>();
    private Venta lastSale;
    private TipoCliente customerType = TipoCliente.GENERAL;
    private Label seatSelectionSummary;
    private Label seatTotal;
    private Label saleTotal;
    private Label productSummary;
    private Label productListSummary;
    private TextField customerName;
    private TextField customerId;
    private ComboBox<Pelicula> functionMovieSelector;
    private ComboBox<Sala> functionRoomSelector;

    @FXML
    private void initialize() {
        cinema = createSampleCinema();
        operador = new Operador("operador", "", "Operador CineUQ", cinema);
        administrador = new Administrador("admin", "", "Administrador CineUQ", cinema);
        products = sampleProducts();
        showCartelera();
    }

    @FXML
    private void showCartelera() {
        setPage("Cartelera", "Películas disponibles y próximas funciones", carteleraButton);
        pageContent.getChildren().setAll(createMovieCatalog());
    }

    @FXML
    private void showFunciones() {
        setPage("Funciones", "Horarios programados para hoy", funcionesButton);
        pageContent.getChildren().setAll(createSchedule());
    }

    @FXML
    private void showVentas() {
        setPage("Venta y cobro", "Selecciona una función para iniciar una compra", ventasButton);
        pageContent.getChildren().setAll(createSchedule());
    }

    @FXML
    private void showAdministracion() {
        setPage("Administración", "Resumen del cine · " + LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("es-CO"))), administracionButton);
        pageContent.getChildren().setAll(createAdministration());
    }

    private void setPage(String title, String description, Button selectedButton) {
        pageTitle.setText(title);
        pageDescription.setText(description);
        for (Button button : List.of(carteleraButton, funcionesButton, ventasButton, administracionButton)) {
            button.getStyleClass().remove("nav-button-active");
        }
        if (!selectedButton.getStyleClass().contains("nav-button-active")) {
            selectedButton.getStyleClass().add("nav-button-active");
        }
    }

    private Node createMovieCatalog() {
        VBox content = new VBox(16);
        long availableMovies = cinema.getPeliculas().stream()
                .filter(movie -> movie.getEstado() == EstadoPelicula.DISPONIBLE).count();
        Label count = label(availableMovies + " películas en exhibición", "muted-text");
        HBox filters = new HBox(7);
        for (String category : List.of("Todas", "Acción", "Comedia", "Drama", "Terror", "Animación")) {
            Button filter = new Button(category);
            filter.getStyleClass().add(category.equals("Todas") ? "filter-chip-active" : "filter-chip");
            filter.setOnAction(event -> {
                filters.getChildren().forEach(node -> {
                    node.getStyleClass().remove("filter-chip-active");
                    if (!node.getStyleClass().contains("filter-chip")) node.getStyleClass().add("filter-chip");
                });
                filter.getStyleClass().remove("filter-chip");
                filter.getStyleClass().add("filter-chip-active");
                refreshMovieGrid(content, category);
            });
            filters.getChildren().add(filter);
        }
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        content.getChildren().addAll(count, filters, grid);
        refreshMovieGrid(content, "Todas");
        return scroll(content);
    }

    private void refreshMovieGrid(VBox content, String category) {
        GridPane grid = (GridPane) content.getChildren().get(2);
        grid.getChildren().clear();
        List<Pelicula> movies = cinema.getPeliculas().stream()
                .filter(movie -> movie.getEstado() == EstadoPelicula.DISPONIBLE)
                .filter(movie -> category.equals("Todas") || genreLabel(movie.getGenero()).equalsIgnoreCase(category))
                .toList();
        if (movies.isEmpty()) {
            grid.add(label("No hay películas en esta categoría.", "empty-state"), 0, 0);
            return;
        }
        for (int i = 0; i < movies.size(); i++) {
            int column = i % 3;
            int row = i / 3;
            grid.add(createMovieCard(movies.get(i), i), column, row);
        }
    }

    private VBox createMovieCard(Pelicula movie, int index) {
        VBox card = new VBox(0);
        card.getStyleClass().add("movie-card");
        VBox poster = new VBox();
        poster.setMinHeight(130);
        poster.setStyle("-fx-background-color: " + POSTER_COLORS[index % POSTER_COLORS.length] + ";");
        Label kicker = label("CINEUQ  ·  EN CARTELERA", "poster-kicker");
        Label genre = label(genreLabel(movie.getGenero()).toUpperCase(Locale.ROOT), "poster-genre");
        Region posterSpacer = new Region();
        VBox.setVgrow(posterSpacer, Priority.ALWAYS);
        Label rating = label(classificationLabel(movie.getClasificacion()).toUpperCase(Locale.ROOT), "rating-pill");
        poster.getChildren().addAll(kicker, posterSpacer, genre, rating);
        VBox details = new VBox(6);
        details.getStyleClass().add("movie-details");
        Label title = label(movie.getNombre(), "movie-title");
        Label subtitle = label(genreLabel(movie.getGenero()) + " · " + movie.getDuracionMinutos() + " min · Disponible", "movie-meta");
        Button sessions = new Button("Ver funciones");
        sessions.getStyleClass().add("primary-button");
        sessions.setOnAction(event -> showFunctionsFor(movie));
        details.getChildren().addAll(title, subtitle, sessions);
        card.getChildren().addAll(poster, details);
        card.setPrefWidth(270);
        return card;
    }

    private Node createSchedule() {
        VBox panel = panel();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        GridPane table = new GridPane();
        Label empty = label("No hay funciones para esta fecha.", "empty-state");
        Runnable refresh = () -> {
            if (datePicker.getValue() == null) return;
            List<Funcion> dayFunctions = cinema.funcionesDia(datePicker.getValue()).stream()
                    .sorted((first, second) -> first.getHoraInicio().compareTo(second.getHoraInicio())).toList();
            table.getChildren().clear();
            if (dayFunctions.isEmpty()) {
                panel.getChildren().remove(table);
                if (!panel.getChildren().contains(empty)) panel.getChildren().add(empty);
            } else {
                panel.getChildren().remove(empty);
                if (!panel.getChildren().contains(table)) panel.getChildren().add(table);
                GridPane refreshed = scheduleTable(dayFunctions);
                table.getChildren().setAll(refreshed.getChildren());
                table.getColumnConstraints().setAll(refreshed.getColumnConstraints());
            }
        };
        datePicker.setOnAction(event -> refresh.run());
        HBox heading = new HBox(10, label("Funciones", "section-title"), spacer(), label("Fecha", "muted-text"), datePicker);
        panel.getChildren().addAll(heading, table);
        refresh.run();
        return scroll(panel);
    }

    private GridPane scheduleTable(List<Funcion> functions) {
        GridPane table = new GridPane();
        table.getStyleClass().add("data-table");
        table.setHgap(12);
        table.setVgap(0);
        table.setMaxWidth(Double.MAX_VALUE);
        double[] columnWidths = {8, 21, 16, 14, 11, 13, 17};
        for (double width : columnWidths) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(width);
            column.setHgrow(Priority.ALWAYS);
            table.getColumnConstraints().add(column);
        }
        String[] headers = {"Hora", "Película", "Sala", "Disponibles", "Ocupación", "Estado", ""};
        for (int column = 0; column < headers.length; column++)
            table.add(label(headers[column], "table-header"), column, 0);
        for (int row = 0; row < functions.size(); row++) {
            Funcion function = functions.get(row);
            int r = row + 1;
            table.add(label(function.getHoraInicio().format(HOUR), "table-cell"), 0, r);
            table.add(label(function.getPelicula().getNombre(), "table-cell"), 1, r);
            table.add(label(function.getSala().getNombre(), "table-cell"), 2, r);
            table.add(label(function.asientosDisponibles() + " de " + function.getSala().getCapacidad(), "table-cell"), 3, r);
            table.add(label(Math.round(function.porcentajeOcupacion()) + "%", "table-cell"), 4, r);
            table.add(label(functionStateLabel(function.getEstado()), function.getEstado() == EstadoFuncion.PROGRAMADA
                    ? "status-scheduled" : "status-inactive"), 5, r);
            if (isFunctionBookable(function)) {
                Button select = new Button("Elegir asientos");
                select.getStyleClass().add("text-button");
                select.setOnAction(event -> openSeatMap(function));
                table.add(select, 6, r);
            } else {
                table.add(label("No disponible", "muted-text"), 6, r);
            }
        }
        return table;
    }

    private void showFunctionsFor(Pelicula movie) {
        List<Funcion> functions = cinema.getFunciones().stream()
                .filter(function -> function.getPelicula().equals(movie) && isFunctionBookable(function))
                .sorted((first, second) -> {
                    int dateComparison = first.getFecha().compareTo(second.getFecha());
                    return dateComparison != 0 ? dateComparison : first.getHoraInicio().compareTo(second.getHoraInicio());
                }).toList();
        setPage("Funciones · " + movie.getNombre(), "Elige el horario y los asientos", carteleraButton);
        VBox panel = panel();
        panel.getChildren().addAll(label(movie.getNombre(), "section-title"), scheduleTable(functions), backButton("Volver a cartelera", this::showCartelera));
        pageContent.getChildren().setAll(scroll(panel));
    }

    private void openSeatMap(Funcion function) {
        if (!isFunctionBookable(function)) {
            showMessage(Alert.AlertType.WARNING, "Función no disponible", "Esta función ya inició o no está programada para la venta.");
            return;
        }
        selectedFunction = function;
        selectedSeats.clear();
        selectedProducts.clear();
        setPage("Mapa de asientos", function.getPelicula().getNombre() + " · " + function.getSala().getNombre() + " · " + function.getHoraInicio().format(HOUR), funcionesButton);
        VBox seatsPanel = panel();
        seatsPanel.getChildren().addAll(label("PANTALLA", "screen-label"), createSeatGrid(function), seatLegend());
        VBox summary = panel();
        summary.getStyleClass().add("purchase-summary");
        seatSelectionSummary = label("Ningún asiento seleccionado", "movie-meta");
        seatTotal = label(money(0), "total-value");
        Button next = new Button("Continuar al cobro");
        next.getStyleClass().add("primary-button");
        next.setMaxWidth(Double.MAX_VALUE);
        next.setOnAction(event -> {
            if (selectedSeats.isEmpty()) {
                seatSelectionSummary.setText("Selecciona al menos un asiento para continuar.");
                return;
            }
            showCheckout();
        });
        summary.getChildren().addAll(label("Tu selección", "section-title"), seatSelectionSummary,
                label("Entrada por asiento", "muted-text"), label(money(seatPrice()), "table-cell"),
                label("Total", "section-title"), seatTotal, next);
        HBox layout = new HBox(20, seatsPanel, summary);
        HBox.setHgrow(seatsPanel, Priority.ALWAYS);
        summary.setPrefWidth(260);
        pageContent.getChildren().setAll(scroll(layout));
    }

    private Node createSeatGrid(Funcion function) {
        GridPane grid = new GridPane();
        grid.setHgap(7);
        grid.setVgap(7);
        int capacity = function.getSala().getCapacidad();
        int columns = 10;
        for (int i = 0; i < capacity; i++) {
            AsientoFuncion seat = function.getAsientos().get(i);
            int row = i / columns;
            int column = i % columns;
            Button button = new Button(seat.getAsiento().getCodigo());
            button.getStyleClass().add(seat.estaDisponible() ? "seat-available" : "seat-occupied");
            button.setDisable(!seat.estaDisponible());
            button.setOnAction(event -> {
                if (selectedSeats.remove(seat)) {
                    button.getStyleClass().remove("seat-selected");
                    button.getStyleClass().add("seat-available");
                } else {
                    selectedSeats.add(seat);
                    button.getStyleClass().remove("seat-available");
                    button.getStyleClass().add("seat-selected");
                }
                updateSeatSummary();
            });
            if (column == 0) grid.add(label(String.valueOf((char) ('A' + row)), "seat-row-label"), 0, row);
            grid.add(button, column + 1, row);
        }
        return grid;
    }

    private Node seatLegend() {
        HBox legend = new HBox(14, legendItem("Disponible", "seat-available"), legendItem("Seleccionado", "seat-selected"), legendItem("Ocupado", "seat-occupied"));
        legend.getStyleClass().add("legend-row");
        return legend;
    }

    private HBox legendItem(String text, String style) {
        Label swatch = label(" ", style);
        return new HBox(6, swatch, label(text, "muted-text"));
    }

    private void updateSeatSummary() {
        seatSelectionSummary.setText(selectedSeats.isEmpty() ? "Ningún asiento seleccionado" : selectedSeats.stream()
                .map(seat -> seat.getAsiento().getCodigo()).reduce((a, b) -> a + ", " + b).orElse(""));
        seatTotal.setText(money(selectedSeats.size() * seatPrice()));
    }

    private void showCheckout() {
        setPage("Venta y cobro", selectedFunction.getPelicula().getNombre() + " · " + selectedFunction.getSala().getNombre() + " · " + selectedSeatCodes(), ventasButton);
        VBox form = new VBox(16);
        VBox customerPanel = panel();
        customerPanel.getChildren().add(label("Tipo de cliente", "section-title"));
        ComboBox<TipoCliente> type = new ComboBox<>();
        type.getItems().setAll(TipoCliente.values());
        type.setValue(customerType);
        type.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoCliente value) {
                if (value == null) return "";
                return switch (value) {
                    case GENERAL -> "General";
                    case ESTUDIANTE -> "Estudiante";
                    case NIÑO -> "Niño";
                    case ADULTO_MAYOR -> "Adulto mayor";
                };
            }

            @Override
            public TipoCliente fromString(String value) {
                return type.getItems().stream()
                        .filter(item -> toString(item).equalsIgnoreCase(value))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Tipo de cliente desconocido: " + value));
            }
        });
        type.getStyleClass().add("customer-type-select");
        type.setOnAction(event -> {
            customerType = type.getValue();
            updateCheckoutTotal();
        });
        customerName = new TextField();
        customerName.setPromptText("Nombre del cliente");
        customerId = new TextField();
        customerId.setPromptText("Identificación (opcional)");
        customerPanel.getChildren().addAll(type, customerName, customerId);

        VBox productsPanel = panel();
        productsPanel.getChildren().add(label("Productos", "section-title"));
        for (Producto product : products) productsPanel.getChildren().add(createProductRow(product));
        productListSummary = label("Sin productos", "muted-text");
        productsPanel.getChildren().add(productListSummary);

        VBox summary = panel();
        summary.getStyleClass().add("purchase-summary");
        saleTotal = label("", "total-value");
        Button confirm = new Button("Confirmar venta");
        confirm.getStyleClass().add("primary-button");
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setOnAction(event -> confirmSale());
        summary.getChildren().addAll(label("Cobro", "section-title"),
                label(selectedSeats.size() + " entrada(s) · " + selectedSeatCodes(), "table-cell"),
                label("Sala " + selectedFunction.getSala().getTipo(), "muted-text"),
                label("Productos", "muted-text"), productSummary = label("Sin productos", "muted-text"), label("Total", "section-title"), saleTotal,
                confirm, backButton("Cambiar asientos", () -> openSeatMap(selectedFunction)));
        HBox layout = new HBox(18, form, summary);
        form.getChildren().addAll(customerPanel, productsPanel);
        HBox.setHgrow(form, Priority.ALWAYS);
        summary.setPrefWidth(270);
        pageContent.getChildren().setAll(scroll(layout));
        updateCheckoutTotal();
    }

    private void updateCheckoutTotal() {
        if (saleTotal == null || selectedFunction == null) return;
        double productTotal = selectedProducts.entrySet().stream().mapToDouble(entry -> entry.getKey().getPrecio() * entry.getValue()).sum();
        saleTotal.setText(money(selectedSeats.size() * seatPrice() + productTotal));
        String products = selectedProducts.isEmpty() ? "Sin productos" : selectedProducts.entrySet().stream()
                .map(entry -> entry.getValue() + " × " + entry.getKey().getNombre()).reduce((a, b) -> a + ", " + b).orElse("");
        productSummary.setText(products);
        productListSummary.setText(products);
    }

    private HBox createProductRow(Producto product) {
        Label name = label(product.getNombre() + "\n" + money(product.getPrecio()), "product-name");
        Label quantity = label("0", "product-quantity");
        Button decrease = new Button("−");
        decrease.getStyleClass().add("quantity-button");
        Button increase = new Button("+");
        increase.getStyleClass().add("quantity-button");
        decrease.setOnAction(event -> {
            selectedProducts.computeIfPresent(product, (item, count) -> count <= 1 ? null : count - 1);
            quantity.setText(String.valueOf(selectedProducts.getOrDefault(product, 0)));
            updateCheckoutTotal();
        });
        increase.setOnAction(event -> {
            selectedProducts.merge(product, 1, Integer::sum);
            quantity.setText(String.valueOf(selectedProducts.get(product)));
            updateCheckoutTotal();
        });
        HBox row = new HBox(8, name, spacer(), decrease, quantity, increase);
        row.getStyleClass().add("product-row");
        return row;
    }

    private void confirmSale() {
        if (customerName.getText().isBlank()) {
            showMessage(Alert.AlertType.WARNING, "Falta el nombre", "Ingresa el nombre del cliente para continuar.");
            return;
        }
        try {
            Cliente customer = new Cliente(customerId.getText().isBlank() ? "SIN-ID" : customerId.getText().trim(), customerName.getText().trim(), customerType);
            lastSale = operador.vender(selectedFunction, customer, selectedSeatCodesList());
            for (Map.Entry<Producto, Integer> entry : selectedProducts.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) lastSale.agregarProducto(entry.getKey());
            }
            showReceipt();
        } catch (CinemaUQException | IllegalArgumentException | IllegalStateException exception) {
            showMessage(Alert.AlertType.ERROR, "No se pudo completar la venta", exception.getMessage());
        }
    }

    private void showReceipt() {
        setPage("Boleta", "Venta registrada correctamente", ventasButton);
        VBox receipt = panel();
        receipt.getStyleClass().add("receipt-card");
        receipt.getChildren().addAll(label("CineUQ", "receipt-brand"), label("Boleta de venta", "muted-text"),
                receiptLine("Boleta", lastSale.getCodigo()), receiptLine("Fecha", lastSale.getFechaHora().format(DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale.forLanguageTag("es-CO")))),
                receiptLine("Cliente", lastSale.getCliente().getNombre()), receiptLine("Función", lastSale.getFuncion().getPelicula().getNombre()),
                receiptLine("Sala / hora", lastSale.getFuncion().getSala().getNombre() + " · " + lastSale.getFuncion().getHoraInicio().format(HOUR)),
                receiptLine("Asientos", lastSale.getAsientos().stream().map(seat -> seat.getAsiento().getCodigo()).reduce((a, b) -> a + ", " + b).orElse("")),
                receiptLine("Entradas", money(lastSale.getCobro().getValorEntrada())),
                receiptLine("Recargo de sala", money(lastSale.getCobro().getRecargoSala())));
        for (Producto product : lastSale.getProductos())
            receipt.getChildren().add(receiptLine("Producto", product.getNombre() + " · " + money(product.getPrecio())));
        if (lastSale.getCobro().getDescuentoCliente() > 0) {
            receipt.getChildren().add(receiptLine("Descuento", "− " + money(lastSale.getCobro().getDescuentoCliente())));
        }
        receipt.getChildren().addAll(receiptLine("Total pagado", money(lastSale.getTotal())), backButton("Volver a cartelera", this::showCartelera));
        pageContent.getChildren().setAll(scroll(receipt));
    }

    private HBox receiptLine(String label, String value) {
        HBox row = new HBox(10, this.label(label, "muted-text"), spacer(), this.label(value, "table-cell"));
        row.getStyleClass().add("receipt-line");
        return row;
    }

    private Node createAdministration() {
        TabPane sections = new TabPane();
        sections.getStyleClass().add("admin-tabs");
        sections.getTabs().addAll(
                new Tab("Resumen", createAdminSummary()),
                new Tab("Películas", createMovieManagement()),
                new Tab("Salas", createRoomManagement()),
                new Tab("Funciones", createFunctionManagement()),
                new Tab("Tarifas", createTariffManagement()));
        sections.getTabs().forEach(tab -> tab.setClosable(false));
        return sections;
    }

    private Node createAdminSummary() {
        List<Venta> sales = cinema.getVentas().stream().filter(v -> v.getEstado() == EstadoVenta.ACTIVA).toList();
        double income = sales.stream().mapToDouble(Venta::getTotal).sum();
        List<Funcion> functionsToday = cinema.funcionesDia(LocalDate.now());
        double averageOccupancy = functionsToday.stream().mapToDouble(Funcion::porcentajeOcupacion).average().orElse(0);
        Pelicula bestSellingMovie = cinema.peliculaMasVendida();
        HBox metrics = new HBox(12,
                metric("Entradas vendidas", String.valueOf(cinema.totalEntradasVendidas()), "Solo ventas activas"),
                metric("Ingresos", money(income), "Total de ventas activas"),
                metric("Más vendida", bestSellingMovie == null ? "Sin ventas" : bestSellingMovie.getNombre(),
                        bestSellingMovie == null ? "Se actualiza con las ventas" : "Por entradas vendidas"),
                metric("Ocupación promedio", Math.round(averageOccupancy) + "%", "Funciones del día"));
        VBox table = panel();
        table.getChildren().addAll(label("Funciones del día", "section-title"), scheduleTable(functionsToday));
        VBox content = new VBox(16, metrics, table);
        return scroll(content);
    }

    private Node createMovieManagement() {
        VBox content = new VBox(14);
        ListView<Pelicula> movieList = new ListView<>();
        movieList.getItems().setAll(cinema.getPeliculas());
        movieList.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Pelicula movie, boolean empty) {
                super.updateItem(movie, empty);
                setText(empty || movie == null ? null : movie.getNombre() + " · " + genreLabel(movie.getGenero())
                        + " · " + classificationLabel(movie.getClasificacion())
                        + (movie.getEstado() == EstadoPelicula.DISPONIBLE ? "" : " · No disponible"));
            }
        });
        TextField code = new TextField();
        code.setPromptText("Código");
        TextField name = new TextField();
        name.setPromptText("Nombre de la película");
        TextField duration = new TextField();
        duration.setPromptText("Duración en minutos");
        ComboBox<Genero> genre = new ComboBox<>();
        genre.getItems().setAll(Genero.values());
        genre.setValue(Genero.ACCION);
        genre.setConverter(enumConverter(this::genreLabel));
        ComboBox<Clasificacion> classification = new ComboBox<>();
        classification.getItems().setAll(Clasificacion.values());
        classification.setValue(Clasificacion.TODOS);
        classification.setConverter(enumConverter(this::classificationLabel));
        Label feedback = label("Registra películas o selecciona una para editarla.", "muted-text");

        Button create = new Button("Nueva película");
        create.getStyleClass().add("secondary-button");
        create.setOnAction(event -> {
            movieList.getSelectionModel().clearSelection();
            code.clear();
            code.setDisable(false);
            name.clear();
            duration.clear();
            genre.setValue(Genero.ACCION);
            classification.setValue(Clasificacion.TODOS);
            feedback.setText("Completa los datos de la película.");
        });
        Button save = new Button("Guardar película");
        save.getStyleClass().add("primary-button");
        save.setOnAction(event -> {
            try {
                if (code.getText().isBlank() || name.getText().isBlank() || genre.getValue() == null
                        || classification.getValue() == null) {
                    throw new IllegalArgumentException("Completa todos los campos de la película.");
                }
                int minutes = Integer.parseInt(duration.getText().trim());
                if (minutes <= 0) throw new IllegalArgumentException("La duración debe ser mayor que cero.");
                Pelicula selected = movieList.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    selected = new Pelicula(code.getText().trim(), name.getText().trim(), genre.getValue(),
                            classification.getValue(), minutes);
                    administrador.gestionarPeliculas(selected);
                } else {
                    selected.setNombre(name.getText().trim());
                    selected.setGenero(genre.getValue());
                    selected.setClasificacion(classification.getValue());
                    selected.setDuracionMinutos(minutes);
                }
                movieList.getItems().setAll(cinema.getPeliculas());
                movieList.getSelectionModel().select(selected);
                refreshFunctionOptions();
                code.setText(selected.getCodigo());
                code.setDisable(true);
                feedback.setText("Película guardada correctamente.");
            } catch (NumberFormatException exception) {
                feedback.setText("La duración debe ser un número entero de minutos.");
            } catch (CinemaUQException | IllegalArgumentException exception) {
                feedback.setText(exception.getMessage());
            }
        });
        Button toggleState = new Button("Habilitar / deshabilitar");
        toggleState.getStyleClass().add("secondary-button");
        toggleState.setOnAction(event -> {
            Pelicula selected = movieList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                feedback.setText("Selecciona una película primero.");
                return;
            }
            selected.setEstado(selected.getEstado() == EstadoPelicula.DISPONIBLE
                    ? EstadoPelicula.NO_DISPONIBLE : EstadoPelicula.DISPONIBLE);
            movieList.refresh();
            refreshFunctionOptions();
            feedback.setText("Estado actualizado: " + (selected.getEstado() == EstadoPelicula.DISPONIBLE ? "disponible" : "no disponible") + ".");
        });
        movieList.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> {
            if (selected == null) return;
            code.setText(selected.getCodigo());
            code.setDisable(true);
            name.setText(selected.getNombre());
            duration.setText(String.valueOf(selected.getDuracionMinutos()));
            genre.setValue(selected.getGenero());
            classification.setValue(selected.getClasificacion() == null ? Clasificacion.TODOS : selected.getClasificacion());
        });

        VBox form = panel();
        form.getChildren().addAll(label("Datos de la película", "section-title"), code, name, duration, genre,
                classification, new HBox(8, create, save), toggleState, feedback);
        HBox layout = new HBox(14, movieList, form);
        HBox.setHgrow(movieList, Priority.ALWAYS);
        movieList.setPrefWidth(390);
        form.setPrefWidth(390);
        content.getChildren().addAll(label("Películas", "section-title"), layout);
        VBox.setVgrow(layout, Priority.ALWAYS);
        return content;
    }

    private Node createRoomManagement() {
        VBox content = new VBox(14);
        ListView<Sala> roomList = new ListView<>();
        roomList.getItems().setAll(cinema.getSalas());
        roomList.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Sala room, boolean empty) {
                super.updateItem(room, empty);
                setText(empty || room == null ? null : room.getNombre() + " · " + room.getTipo() + " · "
                        + room.getCapacidad() + " puestos"
                        + (room.getEstado() == EstadoSala.DISPONIBLE ? "" : " · Fuera de servicio"));
            }
        });
        TextField code = new TextField();
        code.setPromptText("Código");
        TextField name = new TextField();
        name.setPromptText("Nombre de la sala");
        TextField capacity = new TextField();
        capacity.setPromptText("Capacidad");
        ComboBox<TipoSala> type = new ComboBox<>();
        type.getItems().setAll(TipoSala.values());
        type.setValue(TipoSala.TRADICIONAL);
        type.setConverter(enumConverter(value -> switch (value) {
            case TRADICIONAL -> "Tradicional";
            case VIP -> "VIP";
            case SALA_3D -> "3D";
            case IMAX -> "IMAX";
        }));
        Label feedback = label("Registra salas o selecciona una para editarla.", "muted-text");
        Button create = new Button("Nueva sala");
        create.getStyleClass().add("secondary-button");
        create.setOnAction(event -> {
            roomList.getSelectionModel().clearSelection();
            code.clear();
            code.setDisable(false);
            name.clear();
            capacity.clear();
            type.setValue(TipoSala.TRADICIONAL);
        });
        Button save = new Button("Guardar sala");
        save.getStyleClass().add("primary-button");
        save.setOnAction(event -> {
            try {
                if (code.getText().isBlank() || name.getText().isBlank() || type.getValue() == null) {
                    throw new IllegalArgumentException("Completa todos los campos de la sala.");
                }
                int seats = Integer.parseInt(capacity.getText().trim());
                if (seats <= 0) throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
                Sala selected = roomList.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    selected = new Sala(code.getText().trim(), name.getText().trim(), seats, type.getValue());
                    administrador.gestionarSalas(selected);
                } else {
                    Sala selectedRoom = selected;
                    if (seats != selected.getCapacidad() && cinema.getFunciones().stream()
                            .anyMatch(function -> function.getSala() == selectedRoom)) {
                        throw new IllegalArgumentException("No se puede cambiar la capacidad de una sala que ya tiene funciones programadas.");
                    }
                    selected.setNombre(name.getText().trim());
                    selected.setCapacidad(seats);
                    selected.setTipo(type.getValue());
                }
                roomList.getItems().setAll(cinema.getSalas());
                roomList.getSelectionModel().select(selected);
                refreshFunctionOptions();
                code.setText(selected.getCodigo());
                code.setDisable(true);
                feedback.setText("Sala guardada correctamente.");
            } catch (NumberFormatException exception) {
                feedback.setText("La capacidad debe ser un número entero.");
            } catch (CinemaUQException | IllegalArgumentException exception) {
                feedback.setText(exception.getMessage());
            }
        });
        Button toggleState = new Button("Habilitar / deshabilitar");
        toggleState.getStyleClass().add("secondary-button");
        toggleState.setOnAction(event -> {
            Sala selected = roomList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                feedback.setText("Selecciona una sala primero.");
                return;
            }
            selected.setEstado(selected.getEstado() == EstadoSala.DISPONIBLE
                    ? EstadoSala.FUERA_DE_SERVICIO : EstadoSala.DISPONIBLE);
            roomList.refresh();
            refreshFunctionOptions();
            feedback.setText("Estado de sala actualizado.");
        });
        roomList.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> {
            if (selected == null) return;
            code.setText(selected.getCodigo());
            code.setDisable(true);
            name.setText(selected.getNombre());
            capacity.setText(String.valueOf(selected.getCapacidad()));
            type.setValue(selected.getTipo());
        });
        VBox form = panel();
        form.getChildren().addAll(label("Datos de la sala", "section-title"), code, name, capacity, type,
                new HBox(8, create, save), toggleState, feedback);
        HBox layout = new HBox(14, roomList, form);
        HBox.setHgrow(roomList, Priority.ALWAYS);
        roomList.setPrefWidth(390);
        form.setPrefWidth(390);
        content.getChildren().addAll(label("Salas", "section-title"), layout);
        VBox.setVgrow(layout, Priority.ALWAYS);
        return content;
    }

    private Node createFunctionManagement() {
        VBox content = new VBox(14);
        functionMovieSelector = new ComboBox<>();
        functionMovieSelector.setConverter(enumConverter(Pelicula::getNombre));
        functionRoomSelector = new ComboBox<>();
        functionRoomSelector.setConverter(enumConverter(Sala::getNombre));
        refreshFunctionOptions();
        LocalDateTime suggestedStart = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0);
        DatePicker date = new DatePicker(suggestedStart.toLocalDate());
        TextField time = new TextField(suggestedStart.toLocalTime().format(HOUR));
        time.setPromptText("HH:mm");
        Label feedback = label("Selecciona película, sala, fecha y hora para programar una función.", "muted-text");
        ListView<Funcion> functionList = new ListView<>();
        refreshFunctionList(functionList);
        functionList.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Funcion function, boolean empty) {
                super.updateItem(function, empty);
                setText(empty || function == null ? null : function.getFecha() + " · "
                        + function.getHoraInicio().format(HOUR) + " · " + function.getPelicula().getNombre()
                        + " · " + function.getSala().getNombre() + " · " + functionStateLabel(function.getEstado()));
            }
        });
        Button schedule = new Button("Programar función");
        schedule.getStyleClass().add("primary-button");
        schedule.setOnAction(event -> {
            try {
                if (functionMovieSelector.getValue() == null || functionRoomSelector.getValue() == null || date.getValue() == null || time.getText().isBlank()) {
                    throw new IllegalArgumentException("Completa todos los datos de la función.");
                }
                LocalTime start = LocalTime.parse(time.getText().trim(), HOUR);
                if (!LocalDateTime.of(date.getValue(), start).isAfter(LocalDateTime.now())) {
                    throw new IllegalArgumentException("La función debe programarse en una fecha y hora futuras.");
                }
                Funcion function = new Funcion("F" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT),
                        functionMovieSelector.getValue(), functionRoomSelector.getValue(), date.getValue(), start, 15000);
                administrador.programarFuncion(function);
                refreshFunctionList(functionList);
                feedback.setText("Función programada correctamente.");
            } catch (java.time.format.DateTimeParseException exception) {
                feedback.setText("La hora debe tener formato HH:mm, por ejemplo 19:30.");
            } catch (CinemaUQException | IllegalArgumentException exception) {
                feedback.setText(exception.getMessage());
            }
        });
        Button cancel = new Button("Cancelar función seleccionada");
        cancel.getStyleClass().add("secondary-button");
        cancel.setOnAction(event -> {
            Funcion selected = functionList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                feedback.setText("Selecciona una función primero.");
            } else if (selected.getEstado() != EstadoFuncion.PROGRAMADA) {
                feedback.setText("Solo se pueden cancelar funciones programadas.");
            } else {
                selected.setEstado(EstadoFuncion.CANCELADA);
                functionList.refresh();
                feedback.setText("Función cancelada.");
            }
        });
        VBox form = panel();
        form.getChildren().addAll(label("Nueva función", "section-title"), functionMovieSelector, functionRoomSelector, date, time, schedule,
                cancel, feedback);
        VBox listing = panel();
        listing.getChildren().addAll(label("Funciones programadas", "section-title"), functionList);
        VBox.setVgrow(functionList, Priority.ALWAYS);
        HBox layout = new HBox(14, form, listing);
        HBox.setHgrow(listing, Priority.ALWAYS);
        form.setPrefWidth(350);
        content.getChildren().addAll(label("Programación de funciones", "section-title"), layout);
        VBox.setVgrow(layout, Priority.ALWAYS);
        return content;
    }

    private Node createTariffManagement() {
        VBox content = new VBox(14);
        ComboBox<TipoCliente> type = new ComboBox<>();
        type.getItems().setAll(TipoCliente.values());
        type.setValue(TipoCliente.GENERAL);
        type.setConverter(enumConverter(value -> switch (value) {
            case GENERAL -> "General";
            case ESTUDIANTE -> "Estudiante";
            case NIÑO -> "Niño";
            case ADULTO_MAYOR -> "Adulto mayor";
        }));
        Label currentValue = label("", "muted-text");
        TextField newValue = new TextField();
        newValue.setPromptText("Nuevo valor en pesos");
        Label feedback = label("Ajusta el valor de entrada para cada tipo de cliente.", "muted-text");
        Runnable refresh = () -> {
            Tarifa tariff = cinema.buscarTarifa(type.getValue());
            currentValue.setText("Tarifa actual: " + money(tariff.getValor()));
            newValue.setText(String.valueOf(tariff.getValor()));
        };
        type.setOnAction(event -> refresh.run());
        Button save = new Button("Actualizar tarifa");
        save.getStyleClass().add("primary-button");
        save.setOnAction(event -> {
            try {
                double value = Double.parseDouble(newValue.getText().trim());
                if (!Double.isFinite(value) || value < 0) {
                    throw new IllegalArgumentException("La tarifa debe ser un valor válido mayor o igual que cero.");
                }
                administrador.cambiarTarifa(type.getValue(), value);
                refresh.run();
                feedback.setText("Tarifa actualizada correctamente.");
            } catch (NumberFormatException exception) {
                feedback.setText("Ingresa un valor numérico válido.");
            } catch (IllegalArgumentException exception) {
                feedback.setText(exception.getMessage());
            }
        });
        VBox form = panel();
        form.getChildren().addAll(label("Tarifas de entrada", "section-title"), type, currentValue, newValue, save, feedback);
        content.getChildren().add(form);
        refresh.run();
        return content;
    }

    private void refreshFunctionList(ListView<Funcion> functionList) {
        functionList.getItems().setAll(cinema.getFunciones().stream()
                .sorted((first, second) -> {
                    int dateComparison = first.getFecha().compareTo(second.getFecha());
                    return dateComparison != 0 ? dateComparison : first.getHoraInicio().compareTo(second.getHoraInicio());
                }).toList());
    }

    private void refreshFunctionOptions() {
        if (functionMovieSelector != null) {
            Pelicula selectedMovie = functionMovieSelector.getValue();
            functionMovieSelector.getItems().setAll(cinema.getPeliculas().stream()
                    .filter(item -> item.getEstado() == EstadoPelicula.DISPONIBLE).toList());
            if (selectedMovie != null && functionMovieSelector.getItems().contains(selectedMovie)) {
                functionMovieSelector.setValue(selectedMovie);
            } else if (!functionMovieSelector.getItems().isEmpty()) {
                functionMovieSelector.setValue(functionMovieSelector.getItems().get(0));
            }
        }
        if (functionRoomSelector != null) {
            Sala selectedRoom = functionRoomSelector.getValue();
            functionRoomSelector.getItems().setAll(cinema.getSalas().stream()
                    .filter(item -> item.getEstado() == EstadoSala.DISPONIBLE).toList());
            if (selectedRoom != null && functionRoomSelector.getItems().contains(selectedRoom)) {
                functionRoomSelector.setValue(selectedRoom);
            } else if (!functionRoomSelector.getItems().isEmpty()) {
                functionRoomSelector.setValue(functionRoomSelector.getItems().get(0));
            }
        }
    }

    private <T> StringConverter<T> enumConverter(java.util.function.Function<T, String> formatter) {
        return new StringConverter<>() {
            @Override
            public String toString(T value) {
                return value == null ? "" : formatter.apply(value);
            }

            @Override
            public T fromString(String value) {
                throw new UnsupportedOperationException("La selección se realiza desde la lista desplegable.");
            }
        };
    }

    private void showMessage(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private VBox metric(String title, String value, String footer) {
        VBox card = panel();
        card.getStyleClass().add("metric-card");
        card.getChildren().addAll(label(title, "muted-text"), label(value, "metric-value"), label(footer, "movie-meta"));
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPrefWidth(180);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private HBox backButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("text-button");
        button.setOnAction(event -> action.run());
        return new HBox(button);
    }

    private VBox panel() {
        VBox box = new VBox(12);
        box.getStyleClass().add("module-card");
        return box;
    }

    private ScrollPane scroll(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("content-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.getStyleClass().add(style);
        label.setWrapText(true);
        return label;
    }

    private Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private double seatPrice() {
        return cinema.buscarTarifa(customerType).getValor() + selectedFunction.getSala().recargo();
    }

    private List<String> selectedSeatCodesList() {
        return selectedSeats.stream().map(seat -> seat.getAsiento().getCodigo()).toList();
    }

    private String selectedSeatCodes() {
        return String.join(", ", selectedSeatCodesList());
    }

    private String money(double value) {
        return COP.format(value);
    }

    private String genreLabel(Genero genre) {
        return switch (genre) {
            case ACCION -> "Acción";
            case COMEDIA -> "Comedia";
            case DRAMA -> "Drama";
            case TERROR -> "Terror";
            case ANIMACION -> "Animación";
        };
    }

    private String functionStateLabel(EstadoFuncion state) {
        return switch (state) {
            case PROGRAMADA -> "Programada";
            case FINALIZADA -> "Finalizada";
            case CANCELADA -> "Cancelada";
        };
    }

    private String classificationLabel(Clasificacion classification) {
        if (classification == null) return "Sin clasificación";
        return switch (classification) {
            case TODOS -> "Todos";
            case MAYORES_7 -> "7+";
            case MAYORES_12 -> "12+";
            case MAYORES_15 -> "15+";
            case MAYORES_18 -> "18+";
        };
    }

    private boolean isFunctionBookable(Funcion function) {
        return function != null
                && function.getEstado() == EstadoFuncion.PROGRAMADA
                && function.getFecha() != null
                && function.getHoraInicio() != null
                && LocalDateTime.of(function.getFecha(), function.getHoraInicio()).isAfter(LocalDateTime.now())
                && function.getSala() != null
                && function.getSala().getEstado() == EstadoSala.DISPONIBLE
                && function.getPelicula() != null
                && function.getPelicula().getEstado() == EstadoPelicula.DISPONIBLE
                && function.asientosDisponibles() > 0;
    }

    private CinemaUQ createSampleCinema() {
        CinemaUQ sample = new CinemaUQ("CineUQ", "Armenia", "900000000");
        sample.getTarifas().forEach(tariff -> tariff.actualizarValor(switch (tariff.getTipoCliente()) {
            case GENERAL -> 15000;
            case ESTUDIANTE -> 12000;
            case NIÑO, ADULTO_MAYOR -> 10000;
        }));
        List<Pelicula> movies = List.of(
                new Pelicula("P001", "Viaje Estelar", Genero.ACCION, Clasificacion.MAYORES_12, 126),
                new Pelicula("P002", "Fuga de Medianoche", Genero.ACCION, Clasificacion.MAYORES_15, 120),
                new Pelicula("P003", "Mi Villano Favorito", Genero.COMEDIA, Clasificacion.TODOS, 95),
                new Pelicula("P004", "La Casa del Silencio", Genero.TERROR, Clasificacion.MAYORES_18, 104),
                new Pelicula("P005", "Pequeño Explorador", Genero.ANIMACION, Clasificacion.TODOS, 105),
                new Pelicula("P006", "Reino de Cristal", Genero.DRAMA, Clasificacion.MAYORES_7, 110));
        movies.forEach(movie -> {
            try {
                sample.registrarPelicula(movie);
            } catch (PeliculaDuplicadaException exception) {
                throw new IllegalStateException("No se pudo cargar el catálogo de muestra", exception);
            }
        });
        List<Sala> rooms = List.of(
                new Sala("S1", "Tradicional 1", 60, TipoSala.TRADICIONAL),
                new Sala("S2", "Tradicional 2", 60, TipoSala.TRADICIONAL),
                new Sala("VIP", "Sala VIP 2", 60, TipoSala.VIP),
                new Sala("IMAX", "IMAX 1", 60, TipoSala.IMAX),
                new Sala("3D", "3D 1", 60, TipoSala.SALA_3D));
        rooms.forEach(room -> {
            try {
                sample.registrarSala(room);
            } catch (SalaDuplicadaException exception) {
                throw new IllegalStateException("No se pudo cargar las salas de muestra", exception);
            }
        });
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        for (int i = 0; i < movies.size(); i++) {
            Sala room = rooms.get(i % rooms.size());
            LocalDateTime start = now.plusHours(i + 1L);
            Funcion function = new Funcion("F" + (i + 1), movies.get(i), room, start.toLocalDate(), start.toLocalTime(), 15000);
            try {
                sample.programarFuncion(function);
            } catch (SalaNoDisponibleException exception) {
                throw new IllegalStateException("No se pudo cargar las funciones de muestra", exception);
            }
        }
        return sample;
    }

    private List<Producto> sampleProducts() {
        Producto popcorn = new Producto("PR1", "Crispetas grandes", 8000, CategoriaProducto.COMIDA);
        Producto soda = new Producto("PR2", "Gaseosa mediana", 5000, CategoriaProducto.BEBIDA);
        return List.of(
                popcorn,
                soda,
                new Combo("CB1", "Combo pareja", 0.1, List.of(popcorn, soda)),
                new Producto("PR3", "Brownie", 6000, CategoriaProducto.POSTRE));
    }
}
