package org.sguerrero.junit5app.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.sguerrero.junit5app.exception.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest(){
        this.cuenta = new Cuenta("Santiago", new BigDecimal("1000.12345"));
        System.out.println("Inciando el metodo.");
    }

    @AfterEach
    void tearDown(){
        System.out.println("Finalizando el metodo prueba.");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Test
    @DisplayName("Probando el nombre de la cuenta corriente")
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Santiago", new BigDecimal("1000.12345"));
        //cuenta.setPersona("Santiago");
        String esperado = "Santiago";
        String real = cuenta.getPersona();
        assertNotNull(real, () -> "La cuenta no puede ser nula");
        assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba");
        assertTrue(real.equals("Santiago"), () -> "Nombre cuenta esperada debe ser igual a la real");
    }

    @Test
    @DisplayName("Probando el saldo de la cuenta corriente que no sea null, mayor que cero")
    void testSaldoCuenta() {
        cuenta = new Cuenta("Santigo", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertNotNull(cuenta.getSaldo());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Testeando referencias que sean iguales con el método equals.")
    void testReferenciaCuenta() {
        cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        //assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    @DisplayName("Probando el metodo que debita valor de la cuenta en entero y string")
    void testDebitoCuenta() {
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando el metodo que aumenta valor de la cuenta en entero y string")
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando el metodo que debita valor y corrobora que el valor es insuficiente")
    void testDineroInsuficienteExceptionCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    @DisplayName("Probando el metodo que envia valor entre cuentas")
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Santiago", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.setNombre("Bancolombia");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    //@Disabled
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll.")
    void testRelacionBancoCuentas() {
        //fail();
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Santiago", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Bancolombia");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertAll(() -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(),
                        () -> "El banco no tiene las cuentas esperadas"),
                () -> assertEquals("Bancolombia", cuenta1.getBanco().getNombre()),
                () -> {
                    assertEquals("Santiago", banco.getCuentas().stream()
                            .filter(cuenta -> cuenta.getPersona().equals("Santiago"))
                            .findFirst()
                            .get().getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(cuenta -> cuenta.getPersona().equals("Santiago")));
                });
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testSoloWindows(){
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testSoloLinuxMac(){
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows(){
    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void soloJdk8(){
    }

    @Test
    @EnabledOnJre(JRE.JAVA_15)
    void soloJdk15(){
    }

    @Test
    @DisabledOnJre(JRE.JAVA_15)
    void testNoJdk15(){
    }

    @Test
    void imprimirSystemProperties(){
        Properties properties = System.getProperties();
        properties.forEach((k,v) -> System.out.println(k+":"+v));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = ".*11.*")
    void testJavaVersion(){
    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testSolo64(){}

    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testNo64(){}

    @Test
    @EnabledIfSystemProperty(named = "user.name", matches = "santiago")
    void testUsername(){}

    @Test
    @EnabledIfSystemProperty(named = "ENV", matches = "dev")
    void testDev(){}

}