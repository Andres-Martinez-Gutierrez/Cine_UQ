package uniquindio.edu.co.cineuq.controller;

import uniquindio.edu.co.cineuq.exception.PeliculaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaDuplicadaException;
import uniquindio.edu.co.cineuq.exception.SalaNoDisponibleException;
import uniquindio.edu.co.cineuq.model.*;
import uniquindio.edu.co.cineuq.model.enumeraciones.CategoriaProducto;
import uniquindio.edu.co.cineuq.model.enumeraciones.Clasificacion;
import uniquindio.edu.co.cineuq.model.enumeraciones.Genero;
import uniquindio.edu.co.cineuq.model.enumeraciones.TipoSala;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos en memoria para el modo demostración de CineUQ.
 */
final class DatosDemo {

    private DatosDemo() {
    }

    static CinemaUQ crearCine() {
        CinemaUQ cinema = new CinemaUQ("CineUQ", "Armenia", "900000000");
        cinema.getListCinemaTarifas().forEach(tarifa -> tarifa.actualizarValor(switch (tarifa.getTipoCliente()) {
            case GENERAL -> 15000;
            case ESTUDIANTE -> 12000;
            case NIÑO, ADULTO_MAYOR -> 10000;
        }));

        List<Pelicula> peliculas = List.of(
                new Pelicula("P001", "Viaje Estelar", Genero.ACCION, Clasificacion.MAYORES_12, 126),
                new Pelicula("P002", "Fuga de Medianoche", Genero.ACCION, Clasificacion.MAYORES_15, 120),
                new Pelicula("P003", "Mi Villano Favorito", Genero.COMEDIA, Clasificacion.TODOS, 95),
                new Pelicula("P004", "La Casa del Silencio", Genero.TERROR, Clasificacion.MAYORES_18, 104),
                new Pelicula("P005", "Pequeño Explorador", Genero.ANIMACION, Clasificacion.TODOS, 105),
                new Pelicula("P006", "Reino de Cristal", Genero.DRAMA, Clasificacion.MAYORES_7, 110));
        List<Sala> salas = List.of(
                new Sala("S1", "Tradicional 1", 60, TipoSala.TRADICIONAL),
                new Sala("S2", "Tradicional 2", 60, TipoSala.TRADICIONAL),
                new Sala("VIP", "Sala VIP 2", 60, TipoSala.VIP),
                new Sala("IMAX", "IMAX 1", 60, TipoSala.IMAX),
                new Sala("3D", "3D 1", 60, TipoSala.SALA_3D));
        try {
            for (Pelicula pelicula : peliculas) cinema.registrarPelicula(pelicula);
            for (Sala sala : salas) cinema.registrarSala(sala);

            LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);
            for (int i = 0; i < peliculas.size(); i++) {
                LocalDateTime inicio = ahora.plusHours(i + 1L);
                cinema.programarFuncion(new Funcion("F" + (i + 1), peliculas.get(i), salas.get(i % salas.size()),
                        inicio.toLocalDate(), inicio.toLocalTime(), 15000));
            }
        } catch (PeliculaDuplicadaException | SalaDuplicadaException | SalaNoDisponibleException exception) {
            throw new IllegalStateException("No se pudieron cargar los datos de demostración", exception);
        }
        return cinema;
    }

    static List<Producto> crearProductos() {
        Producto crispetas = new Producto("PR1", "Crispetas grandes", 8000, CategoriaProducto.COMIDA, null);
        Producto gaseosa = new Producto("PR2", "Gaseosa mediana", 5000, CategoriaProducto.BEBIDA, null);
        return List.of(
                crispetas,
                gaseosa,
                new Combo("CB1", "Combo pareja", 0.1, List.of(crispetas, gaseosa)),
                new Producto("PR3", "Brownie", 6000, CategoriaProducto.POSTRE, null));
    }
}
