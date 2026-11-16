package uniquindio.edu.co.cineuq.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Administrador("admin", "clave123", "Administrador");
    }

    @Test
    void autenticarConCredencialesCorrectasDevuelveTrue() {
        assertTrue(usuario.autenticar("admin", "clave123"));
    }

    @Test
    void autenticarConUsuarioIncorrectoDevuelveFalse() {
        assertFalse(usuario.autenticar("otro", "clave123"));
    }

    @Test
    void autenticarConContrasenaIncorrectaDevuelveFalse() {
        assertFalse(usuario.autenticar("admin", "mala"));
    }
}
