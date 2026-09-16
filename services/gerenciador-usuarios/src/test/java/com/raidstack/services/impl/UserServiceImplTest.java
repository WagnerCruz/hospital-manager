package com.raidstack.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DisplayName("UserServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    private static final String USERNAME_TEST = "testuser";
    private static final String PASSWORD_TEST = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(passwordEncoder);
        setupUsers();
    }

    /**
     * Configura usuários iniciais no mapa interno de usuários.
     */
    private void setupUsers() {
        Map<String, String> users = new HashMap<>();
        users.put(USERNAME_TEST, ENCODED_PASSWORD);
        users.put("admin", "encodedAdminPassword");
        users.put("user2", "encodedPassword456");
        ReflectionTestUtils.setField(userService, "users", users);
    }

    @Test
    @DisplayName("Deve carregar usuário por username com sucesso")
    void testLoadUserByUsernameSucesso() {
        UserDetails resultado = userService.loadUserByUsername(USERNAME_TEST);

        assertNotNull(resultado);
        assertThat(resultado.getUsername()).isEqualTo(USERNAME_TEST);
        assertThat(resultado.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(resultado.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não existe")
    void testLoadUserByUsernameNaoEncontrado() {
        String usernameInexistente = "usuarioInexistente";

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername(usernameInexistente))
                .withMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção quando username é nulo")
    void testLoadUserByUsernameNulo() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername(null))
                .withMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção quando username é string vazia")
    void testLoadUserByUsernameVazio() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername(""))
                .withMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve carregar usuário 'admin' com sucesso")
    void testLoadUserByUsernameAdmin() {
        UserDetails resultado = userService.loadUserByUsername("admin");

        assertNotNull(resultado);
        assertThat(resultado.getUsername()).isEqualTo("admin");
        assertThat(resultado.getPassword()).isEqualTo("encodedAdminPassword");
    }

    @Test
    @DisplayName("Deve carregar múltiplos usuários diferentes")
    void testLoadMultiplosUsuarios() {
        UserDetails usuario1 = userService.loadUserByUsername(USERNAME_TEST);
        UserDetails usuario2 = userService.loadUserByUsername("admin");
        UserDetails usuario3 = userService.loadUserByUsername("user2");

        assertThat(usuario1.getUsername()).isNotEqualTo(usuario2.getUsername());
        assertThat(usuario2.getUsername()).isNotEqualTo(usuario3.getUsername());
    }


    @Test
    @DisplayName("Deve validar credenciais corretas com sucesso")
    void testValidateUserCredentialsSucesso() {
        when(passwordEncoder.matches(PASSWORD_TEST, ENCODED_PASSWORD))
                .thenReturn(true);

        boolean resultado = userService.validateUserCredentials(USERNAME_TEST, PASSWORD_TEST);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando credenciais são inválidas")
    void testValidateUserCredentialsInvalidas() {
        when(passwordEncoder.matches("senhaErrada", ENCODED_PASSWORD))
                .thenReturn(false);

        boolean resultado = userService.validateUserCredentials(USERNAME_TEST, "senhaErrada");

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não existe")
    void testValidateUserCredentialsUsuarioNaoExiste() {
        String usuarioInexistente = "usuarioInexistente";

        boolean resultado = userService.validateUserCredentials(usuarioInexistente, PASSWORD_TEST);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando username é nulo")
    void testValidateUserCredentialsUsernameNulo() {
        boolean resultado = userService.validateUserCredentials(null, PASSWORD_TEST);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando password é nulo")
    void testValidateUserCredentialsPasswordNulo() {
        when(passwordEncoder.matches(null, ENCODED_PASSWORD))
                .thenReturn(false);

        boolean resultado = userService.validateUserCredentials(USERNAME_TEST, null);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve validar credenciais do usuário 'admin'")
    void testValidateUserCredentialsAdmin() {
        when(passwordEncoder.matches("adminPassword", "encodedAdminPassword"))
                .thenReturn(true);

        boolean resultado = userService.validateUserCredentials("admin", "adminPassword");

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar credenciais para múltiplos usuários")
    void testValidateMultiplosUsuarios() {
        when(passwordEncoder.matches(PASSWORD_TEST, ENCODED_PASSWORD))
                .thenReturn(true);
        when(passwordEncoder.matches("password456", "encodedPassword456"))
                .thenReturn(true);
        when(passwordEncoder.matches("wrongPassword", ENCODED_PASSWORD))
                .thenReturn(false);

        assertTrue(userService.validateUserCredentials(USERNAME_TEST, PASSWORD_TEST));
        assertTrue(userService.validateUserCredentials("user2", "password456"));
        assertFalse(userService.validateUserCredentials(USERNAME_TEST, "wrongPassword"));
    }


    @Test
    @DisplayName("Deve processar username com caracteres especiais")
    void testLoadUserByUsernameComCaracteresEspeciais() {
        Map<String, String> users = new HashMap<>();
        users.put("user-name_123", "encodedPassword");
        ReflectionTestUtils.setField(userService, "users", users);

        UserDetails resultado = userService.loadUserByUsername("user-name_123");

        assertNotNull(resultado);
        assertThat(resultado.getUsername()).isEqualTo("user-name_123");
    }

    @Test
    @DisplayName("Deve processar username com números")
    void testLoadUserByUsernameComNumeros() {
        Map<String, String> users = new HashMap<>();
        users.put("user123", "encodedPassword");
        ReflectionTestUtils.setField(userService, "users", users);

        UserDetails resultado = userService.loadUserByUsername("user123");

        assertNotNull(resultado);
        assertThat(resultado.getUsername()).isEqualTo("user123");
    }

    @Test
    @DisplayName("Deve verificar que lista de autoridades é vazia")
    void testLoadUserByUsernameAuthoritiesVazias() {
        UserDetails resultado = userService.loadUserByUsername(USERNAME_TEST);

        assertThat(resultado.getAuthorities()).isEmpty();
        assertThat(resultado.getAuthorities().size()).isZero();
    }

    @Test
    @DisplayName("Deve validar password com espaços em branco")
    void testValidateUserCredentialsPasswordComEspacos() {
        when(passwordEncoder.matches(" password ", ENCODED_PASSWORD))
                .thenReturn(false);

        boolean resultado = userService.validateUserCredentials(USERNAME_TEST, " password ");

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve validar username com espaços em branco")
    void testValidateUserCredentialsUsernameComEspacos() {
        String usernameComEspacos = " testuser ";

        boolean resultado = userService.validateUserCredentials(usernameComEspacos, PASSWORD_TEST);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao carregar usuário com username em branco")
    void testLoadUserByUsernameBlank() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername("   "));
    }

    @Test
    @DisplayName("Deve validar credenciais com password muito longo")
    void testValidateUserCredentialsPasswordMuitoLongo() {
        String passwordLongo = "a".repeat(1000);
        when(passwordEncoder.matches(passwordLongo, ENCODED_PASSWORD))
                .thenReturn(false);

        boolean resultado = userService.validateUserCredentials(USERNAME_TEST, passwordLongo);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve carregar usuário múltiplas vezes com sucesso")
    void testLoadUserByUsernameMultiplasChamadas() {
        UserDetails resultado1 = userService.loadUserByUsername(USERNAME_TEST);
        UserDetails resultado2 = userService.loadUserByUsername(USERNAME_TEST);
        UserDetails resultado3 = userService.loadUserByUsername(USERNAME_TEST);

        assertThat(resultado1.getUsername()).isEqualTo(resultado2.getUsername());
        assertThat(resultado2.getUsername()).isEqualTo(resultado3.getUsername());
    }

    @Test
    @DisplayName("Deve verificar que objetos UserDetails retornam dados consistentes")
    void testConsistenciaUserDetails() {
        UserDetails resultado1 = userService.loadUserByUsername(USERNAME_TEST);
        UserDetails resultado2 = userService.loadUserByUsername(USERNAME_TEST);

        assertThat(resultado1.getUsername()).isEqualTo(resultado2.getUsername());
        assertThat(resultado1.getPassword()).isEqualTo(resultado2.getPassword());
    }
}
