package it.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.Entities.Biglietto.BigliettoEntity;
import it.MainApplication;
import it.Repositories.db.Eventi.Biglietto_Repository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("REST API Biglietto tests")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class BigliettoTests {
    @Autowired
    Biglietto_Repository biglietto_repository;

    private final String baseUrl = "http://localhost:8080/api/v1/biglietto";
    private final String idEventoTest = "648b260e088c6635f6ffc0e2";
    private final String idCreatoreEvento = "647378457d34866a873ff0fc";     //kihivo1034@farebus.com : password1
    private final String idCliente = "647378d77d34866a873ff0fd";        //kihivo1035@farebus.com : password2

    @BeforeAll
    public static void setUp() {
        MainApplication.start();
        System.out.println("\n\n\nServer is up and running! Time to unleash the awesomeness!\n\n\n");
    }

    @AfterAll
    public static void tearDown() {
        MainApplication.stop();
        System.out.println("\n\n\nServer bidding farewell. It's been a wild ride! See you next time!\n\n\n");
    }

    @Nested
    @Order(1)
    @DisplayName("Tests on POST methods")
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class POSTMethodsTests {

        @Nested
        @Order(1)
        @DisplayName("Tickets creation")
        class TicketCreationTests {
            private final String urlCreate = baseUrl + "/crea";

            @Test
            @DisplayName("Tickets correctly created")
            void whenTicketCreatedCorrectly_thenCreated() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlCreate))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "   \"idEvento\":           \"" + idEventoTest + "\"," +
                                        "   \"numeroBiglietti\":    3," +
                                        "   \"data\":               \"2023-07-15T13:00:00.000Z\"," +
                                        "   \"prezzo\":             12.90" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.CREATED.value(), response.statusCode());
            }

            @Test
            @DisplayName("Missing mandatory fields")
            void whenMissingMandatoryFields_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlCreate))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"idEvento\":          \"" + idEventoTest + "\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }
        }

        @Nested
        @Order(2)
        @DisplayName("Single ticket creation")
        class SingleTicketCreationTests {
            @Test
            @DisplayName("Missing mandatory fields")
            void whenMissingMandatoryFields_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                  "{" +
                                        "   \"idEvento\":           \"" + idEventoTest + "\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("Ticket number already taken")
            void whenTicketNumberIsAlreadyTaken_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "   \"idEvento\":           \"" + idEventoTest + "\"," +
                                        "   \"numeroBiglietti\":    3," +
                                        "   \"data\":               \"2023-07-15T13:00:00.000Z\"," +
                                        "   \"prezzo\":             12.90" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("Correct creation")
            void whenNewTicketIsCreatedCorrectly_thenCreated() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "   \"idEvento\":           \"" + idEventoTest + "\"," +
                                        "   \"numeroBiglietti\":    4," +
                                        "   \"data\":               \"2023-07-15T13:00:00.000Z\"," +
                                        "   \"prezzo\":             12.90" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.CREATED.value(), response.statusCode());
            }
        }

        @Nested
        @Order(3)
        @DisplayName("Ticket booking")
        class TicketBookingTests {
            private final String urlBooking = baseUrl + "/prenota";

            private final List<BigliettoEntity> biglietti = biglietto_repository.findBigliettoEntitiesByIdEvent(idEventoTest);
            private final BigliettoEntity biglietto = biglietti.get(0);

            @Test
            @DisplayName("Correct ticket booking")
            void whenCorrectTicketBooking_thenOk() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlBooking))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"idUtente\":          \"" + idCliente + "\"," +
                                        "    \"idBiglietto\":       \"" + biglietto.getId() + "\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.OK.value(), response.statusCode());
            }

            @Test
            @DisplayName("Missing mandatory fields")
            void whenMissingMandatoryFields_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlBooking))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"idUtente\":          \"" + idCliente + "\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("Incorrect type in json - not an ObjectId")
            void whenIncorrectTypeInJson_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlBooking))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"idUtente\":          \"" + idCreatoreEvento + "\"," +
                                        "    \"idBiglietto\":       \"something\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("Nonexistent user")
            void whenNonexistentUser_thenNotFound() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlBooking))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"idUtente\":          \"" + new ObjectId() + "\"," +
                                        "    \"idBiglietto\":       \"" + biglietto.getId() + "\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @DisplayName("Nonexistent ticket")
            void whenNonexistentTicket_thenNotFound() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlBooking))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"idUtente\":          \"" + idCreatoreEvento + "\"," +
                                        "    \"idBiglietto\":       \"" + new ObjectId() + "\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @DisplayName("Mismatch between logged user and userID in DTO")
            void whenMismatchUserID_thenUnauthorized() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlBooking))
                        .header("Content-Type", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"idUtente\":          \"" + idCreatoreEvento + "\"," +
                                        "    \"idBiglietto\":       \"" + biglietto.getId() + "\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
            }
        }
    }


    @Nested
    @Order(2)
    @DisplayName("Tests on GET methods")
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class GETMethodsTests {
        private final List<BigliettoEntity> biglietti = biglietto_repository.findBigliettoEntitiesByIdEvent(idEventoTest);
        private final BigliettoEntity biglietto = biglietti.get(0);

        @Nested
        @Order(1)
        @DisplayName("Get ticket by ID")
        class GetBigliettoByID {
            @Test
            @DisplayName("Bad path: not an ObjectId")
            void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + "something"))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("Correct MIME type")
            void givenReviewExists_JsonIsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + biglietto.getId()))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertTrue(response.headers().toString().contains("application/json"));
            }

            @Test
            @DisplayName("Ticket not found")
            void whenTicketDoesNotExist_thanNotFound() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + idEventoTest))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @DisplayName("Unauthorized request")
            void whenUnauthorizedRequest_thanUnauthorized() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + biglietto.getId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1036@farebus.com", "password3"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
            }

            @Test
            @DisplayName("Correct request")
            void whenGetExistingTicket_thenCorrectResourceIsRetrieved() throws ExecutionException, InterruptedException, JsonProcessingException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + biglietto.getId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                BigliettoEntity resource = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(biglietto_repository.findBigliettoEntityById(biglietto.getId()), resource);
            }
        }

        @Nested
        @Order(2)
        @DisplayName("Get number of tickets available")
        class GetNumberTicketsAvailable {
            @Test
            @DisplayName("Bad path: not an ObjectId")
            void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/disponibili/something"))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("Correct MIME type")
            void givenReviewExists_JsonIsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/disponibili/" + idEventoTest))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertTrue(response.headers().toString().contains("application/json"));
            }

            @Test
            @DisplayName("Resource acquired correctly")
            void whenCorrectRequest_thenCorrectResourceIsRetrieved() throws ExecutionException, InterruptedException, JsonProcessingException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/disponibili/" + idEventoTest))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                int nBigliettiDisponibili = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(biglietto_repository.findBigliettoEntitiesByIdEventAndIdUtenteIsNull(idEventoTest).size(), nBigliettiDisponibili);
            }
        }
    }


    @Nested
    @Order(3)
    @DisplayName("Tests on PUT methods")
    class PUTMethodsTests {
        private final List<BigliettoEntity> biglietti = biglietto_repository.findBigliettoEntitiesByIdEvent(idEventoTest);
        private final BigliettoEntity biglietto = biglietti.get(0);

        @Test
        @DisplayName("Bad path: not an ObjectId")
        void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/something"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"data\":               \"2023-07-15T13:00:00.000Z\"," +
                                    "   \"prezzo\":             4.90" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("Deleted or nonexistent ticket")
        void whenNonexistentTicket_thenNotFound() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + new ObjectId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"data\":               \"2023-07-15T13:00:00.000Z\"," +
                                    "   \"prezzo\":             4.90" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
        }

        @Test
        @DisplayName("Missing mandatory fields")
        void whenMissingMandatoryFields_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + biglietto.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"prezzo\":             4.90" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("Correct modification")
        void whenCorrectModification_thenOk() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + biglietto.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"data\":               \"2023-07-15T13:00:00.000Z\"," +
                                    "   \"prezzo\":             4.90" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.OK.value(), response.statusCode());
        }
    }


    @Nested
    @Order(4)
    @DisplayName("Tests on DELETE methods")
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class DELETEMethodsTests {
        private final List<BigliettoEntity> biglietti = biglietto_repository.findBigliettoEntitiesByIdEvent(idEventoTest);
        private final BigliettoEntity biglietto = biglietti.get(0);

        @Nested
        @Order(1)
        @DisplayName("Cancel booking")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class DeleteBookingTests {
            @Test
            @Order(1)
            @DisplayName("Bad path: not an ObjectId")
            void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/prenota/something"))
                        .header("accept", "application/json")
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @Order(1)
            @DisplayName("Ticket not found")
            void whenTicketDoesNotExist_thanNotFound() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/prenota/" + new ObjectId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @Order(1)
            @DisplayName("Unauthorized cancellation")
            void whenUnauthorizedCancellation_thenUnauthorized() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/prenota/" + biglietto.getId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1036@farebus.com", "password3"))
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
            }

            @Test
            @Order(2)
            @DisplayName("Cancel booking correctly")
            void whenBookingCorrectlyCancelled_thenOk() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/prenota/" + biglietto.getId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.OK.value(), response.statusCode());
            }
        }

        @Nested
        @Order(2)
        @DisplayName("Delete ticket by ID")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class DeleteTicketByIdTests {
            @Test
            @Order(1)
            @DisplayName("Bad path: not an ObjectId")
            void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/something"))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @Order(1)
            @DisplayName("Ticket not found")
            void whenTicketDoesNotExist_thanNotFound() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + new ObjectId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @Order(2)
            @DisplayName("Ticket deleted correctly")
            void whenBookingCorrectlyCancelled_thenOk() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + biglietto.getId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.OK.value(), response.statusCode());
            }
        }

        @Nested
        @Order(3)
        @DisplayName("Delete all tickets of an event")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class DeleteAll {
            @Test
            @Order(1)
            @DisplayName("Bad path: not an ObjectId")
            void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/evento/something"))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @Order(2)
            @DisplayName("Delete all correctly")
            void whenCorrectDeleteAllIsExecuted_thenOk() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/evento/" + idEventoTest))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .DELETE()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.OK.value(), response.statusCode());
            }
        }
    }


    //Authentication
    private static String getBasicAuthenticationHeader(String email, String password) {
        String valueToEncode = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
