package uniquindio.edu.co.cineuq.viewController.componentes;

import javafx.util.StringConverter;
import uniquindio.edu.co.cineuq.model.Combo;
import uniquindio.edu.co.cineuq.model.Producto;
import uniquindio.edu.co.cineuq.model.enumeraciones.*;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;

/**
 * Textos y formatos que la interfaz muestra al usuario.
 */
public final class Formatos {
    public static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d MMM yyyy", COLOMBIA);
    public static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", COLOMBIA);
    private static final NumberFormat PESOS = NumberFormat.getCurrencyInstance(COLOMBIA);

    private Formatos() {
    }

    public static String dinero(double valor) {
        return PESOS.format(valor);
    }

    public static String genero(Genero genero) {
        return switch (genero) {
            case ACCION -> "Acción";
            case COMEDIA -> "Comedia";
            case DRAMA -> "Drama";
            case TERROR -> "Terror";
            case ANIMACION -> "Animación";
        };
    }

    public static String clasificacion(Clasificacion clasificacion) {
        if (clasificacion == null) return "Sin clasificación";
        return switch (clasificacion) {
            case TODOS -> "Todos";
            case MAYORES_7 -> "7+";
            case MAYORES_12 -> "12+";
            case MAYORES_15 -> "15+";
            case MAYORES_18 -> "18+";
        };
    }

    public static String estadoFuncion(EstadoFuncion estado) {
        return switch (estado) {
            case PROGRAMADA -> "Programada";
            case FINALIZADA -> "Finalizada";
            case CANCELADA -> "Cancelada";
        };
    }

    public static String tipoCliente(TipoCliente tipo) {
        return switch (tipo) {
            case GENERAL -> "General";
            case ESTUDIANTE -> "Estudiante";
            case NIÑO -> "Niño";
            case ADULTO_MAYOR -> "Adulto mayor";
        };
    }

    public static String tipoSala(TipoSala tipo) {
        return switch (tipo) {
            case TRADICIONAL -> "Tradicional";
            case VIP -> "VIP";
            case SALA_3D -> "3D";
            case IMAX -> "IMAX";
        };
    }

    public static String categoria(Producto producto) {
        if (producto instanceof Combo) return "Combo";
        return switch (producto.getCategoria()) {
            case COMIDA -> "Comida";
            case BEBIDA -> "Bebida";
            case POSTRE -> "Postre";
        };
    }

    /** Convertidor de solo lectura para listas desplegables no editables. */
    public static <T> StringConverter<T> convertidor(Function<T, String> formato) {
        return new StringConverter<>() {
            @Override
            public String toString(T valor) {
                return valor == null ? "" : formato.apply(valor);
            }

            @Override
            public T fromString(String texto) {
                throw new UnsupportedOperationException("La selección se realiza desde la lista desplegable.");
            }
        };
    }
}
