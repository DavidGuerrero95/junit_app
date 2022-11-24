package org.sguerrero.junit5app.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sguerrero.junit5app.exception.DineroInsuficienteException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;


//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter){
        this.cuenta = new Cuenta("Santiago", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Inciando el metodo.");
        testReporter.publishEntry(" Ejecutando: "+ testInfo.getDisplayName()+ " "+testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas: "+testInfo.getTags());
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

    @Tag("cuenta")
    @Nested
    @DisplayName("Probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("el nombre!")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if(testInfo.getTags().contains("cuenta")) testReporter.publishEntry("Hacer algo con la etiqueta cuenta");
            //cuenta.setPersona("Santiago");
            String esperado = "Santiago";
            String real = cuenta.getPersona();
            assertNotNull(real, () -> "La cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba");
            assertTrue(real.equals("Santiago"), () -> "Nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        @DisplayName("el saldo que no sea null, mayor que cero")
        void testSaldoCuenta() {
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
    }

    @Nested
    class CuentaOperacionesTest{
        @Tag("cuenta")
        @Test
        @DisplayName("Probando el metodo que debita valor de la cuenta en entero y string")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }
        @Tag("cuenta")
        @Test
        @DisplayName("Probando el metodo que aumenta valor de la cuenta en entero y string")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }
        @Tag("banco")
        @Tag("cuenta")
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
    }

    @Tag("cuenta")
    @Tag("error")
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

    @Tag("banco")
    @Tag("cuenta")
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

    @Nested
    class SistemaOperativoTest {
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
    }

    @Nested
    class JavaVersionTest{
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
    }

    @Nested
    class SistemPropertiesTest{
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

    @Nested
    class VariablesAmbienteTest {
        @Test
        void imprimirVariablesAmbiente(){
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k,v) -> System.out.println(k+" = "+v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11-0-1-*")
        void testJavaHome(){
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testProcesadores(){
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv(){

        }
        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled(){
        }
    }


    @Test
    @DisplayName("testSaldoCuentaDev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertNotNull(cuenta.getSaldo());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test Saldo Cuenta Dev2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, ()-> {
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertNotNull(cuenta.getSaldo());

        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Probando Debito Cuenta Repetir!")
    @RepeatedTest(value=5, name="{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) System.out.println("Estamos en la repeticion "+info.getCurrentRepetition());
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadasTest{
        @ParameterizedTest(name = "numero {index} ejecuntando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100","200","300","500","700","1000.12345"})
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecuntando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100","2,200","3,300","4,500","5,700","6,1000.12345"})
        void testDebitoCuentaCsvSource(String index,String monto) {
            System.out.println(index + " -> "+monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecuntando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres","250,200,Pepe,Pepe","298,300,Maria,Maria","400,500,Pepa,Pepa","750,700,Lucas,Lucas","1000.12345,1000.12345,Cata,Cata"})
        void testDebitoCuentaCsvSource2(String saldo,String monto, String esperado, String actual) {
            System.out.println(saldo + " -> "+monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecuntando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecuntando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data1.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }


    }
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecuntando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
    static private List<String> montoList(){
        return Arrays.asList("100","200","300","500","700","1000.12345");
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest{
        @Test
        @Timeout(5)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(4);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeoutAssertions(){
            assertTimeout(Duration.ofSeconds(5),()->{
                TimeUnit.MILLISECONDS.sleep(4500);
            });
        }
    }


}