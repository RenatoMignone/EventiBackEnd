package it.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.Entities.Evento.EventoEntity;
import it.MainApplication;
import it.Repositories.db.Eventi.Evento_Repository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.IOException;
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
@DisplayName("REST API Event tests")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class EventoTests {
    @Autowired
    Evento_Repository evento_repository;

    private final String baseUrl = "http://localhost:8080/api/v1/evento";
    private final String userId = "647378d77d34866a873ff0fd";       //kihivo1035@farebus.com : password2;

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
    @DisplayName("Event: tests on POST methods")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class POSTMethodsTests {
        @Test
        @Order(1)
        @DisplayName("Correctly create new event")
        void whenCorrectlyCreateNewEvent_thenCreated() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\n" +
                                    "    \"idCreatore\":           \"" + userId + "\",\n" +
                                    "    \"nome\":                 \"Test Evento 2\",\n" +
                                    "    \"descrizione\":          \"Descrizione 2\",\n" +
                                    "    \"dataInizio\":           \"Thu, 06 Jul 2023 12:00:00 CEST\",\n" +
                                    "    \"dataFine\":             \"Thu, 27 Jul 2023 18:15:00 CEST\",\n" +
                                    "    \"location\":             {\n" +
                                    "                                \"lat\":  41.063131891793695,\n" +
                                    "                                \"lng\":  14.756420644767658\n" +
                                    "                            },\n" +
                                    "    \"nomeCategoria\":        \"Categoria\"\n" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.CREATED.value(), response.statusCode());
        }

        @Test
        @Order(1)
        @DisplayName("Missing mandatory fields")
        void givenMissingMandatoryFields_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\n" +
//                                    "    \"idCreatore\":           \"" + userId + "\",\n" +
//                                    "    \"nome\":                 \"Test Evento 2\",\n" +
                                    "    \"descrizione\":          \"Descrizione 2\",\n" +
                                    "    \"dataInizio\":           \"Thu, 06 Jul 2023 12:00:00 CEST\",\n" +
                                    "    \"dataFine\":             \"Thu, 27 Jul 2023 18:15:00 CEST\",\n" +
                                    "    \"location\":             {\n" +
                                    "                                \"lat\":  41.063131891793695,\n" +
                                    "                                \"lng\":  14.756420644767658\n" +
                                    "                            },\n" +
                                    "    \"nomeCategoria\":        \"Categoria\"\n" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @Order(1)
        @DisplayName("Starting date after ending date")
        void givenStartingDateIsAfterEndingDate_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\n" +
                                    "    \"idCreatore\":           \"" + userId + "\",\n" +
                                    "    \"nome\":                 \"Test Evento 2\",\n" +
                                    "    \"descrizione\":          \"Descrizione 2\",\n" +
                                    "    \"dataInizio\":           \"Thu, 06 Aug 2023 12:00:00 CEST\",\n" +
                                    "    \"dataFine\":             \"Thu, 27 Jul 2023 18:15:00 CEST\",\n" +
                                    "    \"location\":             {\n" +
                                    "                                \"lat\":  41.063131891793695,\n" +
                                    "                                \"lng\":  14.756420644767658\n" +
                                    "                            },\n" +
                                    "    \"nomeCategoria\":        \"Categoria\"\n" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @Order(1)
        @DisplayName("Ending in the past")
        void givenEndingDateIsInThePast_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\n" +
                                    "    \"idCreatore\":           \"" + userId + "\",\n" +
                                    "    \"nome\":                 \"Test Evento 2\",\n" +
                                    "    \"descrizione\":          \"Descrizione 2\",\n" +
                                    "    \"dataInizio\":           \"Thu, 06 Jul 2002 12:00:00 CEST\",\n" +
                                    "    \"dataFine\":             \"Thu, 27 Jul 2003 18:15:00 CEST\",\n" +
                                    "    \"location\":             {\n" +
                                    "                                \"lat\":  41.063131891793695,\n" +
                                    "                                \"lng\":  14.756420644767658\n" +
                                    "                            },\n" +
                                    "    \"nomeCategoria\":        \"Categoria\"\n" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @Order(1)
        @DisplayName("Creator doesn't exist")
        void givenCreatorDoesNotExist_thenNotFound() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\n" +
                                    "    \"idCreatore\":           \"" + new ObjectId() + "\",\n" +
                                    "    \"nome\":                 \"Test Evento 2\",\n" +
                                    "    \"descrizione\":          \"Descrizione 2\",\n" +
                                    "    \"dataInizio\":           \"Thu, 06 Jul 2023 12:00:00 CEST\",\n" +
                                    "    \"dataFine\":             \"Thu, 27 Jul 2023 18:15:00 CEST\",\n" +
                                    "    \"location\":             {\n" +
                                    "                                \"lat\":  41.063131891793695,\n" +
                                    "                                \"lng\":  14.756420644767658\n" +
                                    "                            },\n" +
                                    "    \"nomeCategoria\":        \"Categoria\"\n" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
        }

        @Test
        @Order(2)
        @DisplayName("Exists already with name, dates and location")
        void givenExistsAlreadyAnEventWithSameNameDatesLocation_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\n" +
                                    "    \"idCreatore\":           \"" + userId + "\",\n" +
                                    "    \"nome\":                 \"Test Evento 2\",\n" +
                                    "    \"descrizione\":          \"Descrizione 2\",\n" +
                                    "    \"dataInizio\":           \"Thu, 06 Jul 2023 12:00:00 CEST\",\n" +
                                    "    \"dataFine\":             \"Thu, 27 Jul 2023 18:15:00 CEST\",\n" +
                                    "    \"location\":             {\n" +
                                    "                                \"lat\":  41.063131891793695,\n" +
                                    "                                \"lng\":  14.756420644767658\n" +
                                    "                            },\n" +
                                    "    \"nomeCategoria\":        \"Categoria\"\n" +
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
    @DisplayName("Event: tests on GET methods")
    class GETMethodsTests {
        @Nested
        @DisplayName("Search events by name")
        class SearchEventsByName {
            private final String urlSearch = baseUrl + "/search?query=";

            @Test
            @DisplayName("Correct query")
            void whenSearchWithCorrectQuery_thenCorrectInformationIsRetrieved() throws ExecutionException, InterruptedException, JsonProcessingException {
                String correctQuery = "test";

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlSearch + correctQuery))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                List<EventoEntity> eventi = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(evento_repository.findEventoEntitiesByNomeContainingIgnoreCase(correctQuery), eventi);
            }

            @Test
            @DisplayName("No results: not found")
            void whenNoElementsFound_thenNotFound() throws ExecutionException, InterruptedException {
                String incorrectQuery = "123abc";

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlSearch + incorrectQuery))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }
        }

        @Nested
        @DisplayName("Get event by ID")
        class GetEventByID {
            EventoEntity evento = evento_repository.findEventoEntitiesByIdCreatore(userId).get(0);

            @Test
            @DisplayName("Bad path: not an ObjectId")
            void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/something"))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("Event doesn't exist: 404")
            void givenEventDoesNotExist_whenInfoIsRetrieved_then404IsReceived() throws InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + new ObjectId()))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @DisplayName("MIME type: json")
            void givenEventExists_whenRequestIsExecuted_thenJsonIsReceived() throws InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + evento.getId()))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertTrue(response.headers().toString().contains("application/json"));
            }

            @Test
            @DisplayName("Correct information is retrieved")
            void givenEventExists_whenInfoIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException, InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + evento.getId()))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                EventoEntity resource = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(evento_repository.findEventoEntityById(evento.getId()), resource);
            }
        }

        @Nested
        @DisplayName("Get events by creator ID")
        class GetEventsByCreatorID {
            private final String urlWithUser = baseUrl + "/utente/";

            @Test
            @DisplayName("Bad path: not an ObjectId")
            void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + "something"))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("User doesn't exist: 404")
            void givenUserDoesNotExist_whenInfoIsRetrieved_then404IsReceived() throws InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + new ObjectId()))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @DisplayName("MIME type: json")
            void givenUserExists_whenRequestIsExecuted_thenJsonIsReceived() throws InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + userId))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertTrue(response.headers().toString().contains("application/json"));
            }

            @Test
            @DisplayName("List of user's event is retrieved")
            void givenEventExists_whenInfoIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException, InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + userId))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                List<EventoEntity> resource = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(evento_repository.findEventoEntitiesByIdCreatore(userId), resource);
            }
        }

        @Nested
        @DisplayName("Get all events")
        class GetAllEvents {
            private final String urlGetAll = baseUrl + "/getAll";

            @Test
            @DisplayName("MIME type: json")
            void givenUserExists_whenRequestIsExecuted_thenJsonIsReceived() throws InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlGetAll))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertTrue(response.headers().toString().contains("application/json"));
            }

            @Test
            @DisplayName("List of user's event is retrieved")
            void givenEventExists_whenInfoIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException, InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlGetAll))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                List<EventoEntity> resource = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(evento_repository.findAll(), resource);
            }
        }
    }

    @Nested
    @Order(3)
    @DisplayName("Event: tests on PUT methods")
    class PUTMethodsTests {
        EventoEntity evento = evento_repository.findEventoEntitiesByIdCreatore(userId).get(0);

        @Test
        @DisplayName("Bad path: not an ObjectId")
        void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/something"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            """
                                    {
                                        "nome":                 "Test Evento 2 (mod)",
                                        "descrizione":          "Descrizione 2 (mod)",
                                        "dataInizio":           "Thu, 06 Jul 2023 12:00:00 CEST",
                                        "dataFine":             "Thu, 27 Jul 2023 18:15:00 CEST",
                                        "location":             {
                                                                    "lat":  41.063131891793695,
                                                                    "lng":  14.756420644767658
                                                                },
                                        "nomeCategoria":        "Categoria"
                                    }"""
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("Event doesn't exist")
        void givenEventDoesNotExist_thenNotFound() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + new ObjectId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            """
                                    {
                                        "nome":                 "Test Evento 2 (mod)",
                                        "descrizione":          "Descrizione 2 (mod)",
                                        "dataInizio":           "Thu, 06 Jul 2023 12:00:00 CEST",
                                        "dataFine":             "Thu, 27 Jul 2023 18:15:00 CEST",
                                        "location":             {
                                                                    "lat":  41.063131891793695,
                                                                    "lng":  14.756420644767658
                                                                },
                                        "nomeCategoria":        "Categoria"
                                    }"""
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
                    .uri(URI.create(baseUrl + "/" + evento.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            """
                                    {
                                        "dataInizio":           "Thu, 06 Jul 2023 12:00:00 CEST",
                                        "dataFine":             "Thu, 27 Jul 2023 18:15:00 CEST",
                                        "location":             {
                                                                    "lat":  41.063131891793695,
                                                                    "lng":  14.756420644767658
                                                                },
                                        "nomeCategoria":        "Categoria"
                                    }"""
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("User not authorized")
        void givenUserIsNotAuthorized_thenUnauthorized() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + evento.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1036@farebus.com", "password3"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            """
                                    {
                                        "nome":                 "Test Evento 2 (mod)",
                                        "descrizione":          "Descrizione 2 (mod)",
                                        "dataInizio":           "Thu, 06 Jul 2023 12:00:00 CEST",
                                        "dataFine":             "Thu, 27 Jul 2023 18:15:00 CEST",
                                        "location":             {
                                                                    "lat":  41.063131891793695,
                                                                    "lng":  14.756420644767658
                                                                },
                                        "nomeCategoria":        "Categoria"
                                    }"""
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
        }

        @Test
        @DisplayName("Starting date after ending date")
        void givenStartingDateAfterEndingDate_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + evento.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            """
                                    {
                                        "nome":                 "Test Evento 2 (mod)",
                                        "descrizione":          "Descrizione 2 (mod)",
                                        "dataInizio":           "Thu, 06 Jul 2023 12:00:00 CEST",
                                        "dataFine":             "Thu, 27 Jul 2000 18:15:00 CEST",
                                        "location":             {
                                                                    "lat":  41.063131891793695,
                                                                    "lng":  14.756420644767658
                                                                },
                                        "nomeCategoria":        "Categoria"
                                    }"""
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("Ending date before current date")
        void givenEndingDateBeforeCurrentDate_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + evento.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            """
                                    {
                                        "nome":                 "Test Evento 2 (mod)",
                                        "descrizione":          "Descrizione 2 (mod)",
                                        "dataInizio":           "Thu, 06 Jul 2000 12:00:00 CEST",
                                        "dataFine":             "Thu, 27 Jul 2000 18:15:00 CEST",
                                        "location":             {
                                                                    "lat":  41.063131891793695,
                                                                    "lng":  14.756420644767658
                                                                },
                                        "nomeCategoria":        "Categoria"
                                    }"""
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("Attempt to overlap events")
        void givenSameEventAlreadyExists_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + evento.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            """
                                    {
                                        "nome":                 "Test Evento",
                                        "descrizione":          "Descrizione",
                                        "dataInizio":           "2023-07-06T12:00:00.000Z",
                                        "dataFine":             "2023-07-27T18:15:00.000Z",
                                        "location":             {
                                                                    "lat":  41.063131891793695,
                                                                    "lng":  14.756420644767658
                                                                },
                                        "nomeCategoria":        "Categoria"
                                    }"""
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("Correctly modify event")
        void whenCorrectlyModifyEvent_thenOk() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + evento.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            """
                                    {
                                        "nome":                 "Test Evento 2 (mod)",
                                        "descrizione":          "Descrizione 2 (mod)",
                                        "dataInizio":           "Thu, 06 Jul 2023 12:00:00 CEST",
                                        "dataFine":             "Thu, 27 Jul 2023 18:15:00 CEST",
                                        "location":             {
                                                                    "lat":  41.063131891793695,
                                                                    "lng":  14.756420644767658
                                                                },
                                        "nomeCategoria":        "Categoria"
                                    }"""
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
    @DisplayName("Event: tests on DELETE methods")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DELETEMethodsTests {
        EventoEntity evento = evento_repository.findEventoEntitiesByIdCreatore(userId).get(0);

        @Test
        @Order(1)
        @DisplayName("Bat path: not an ObjectId")
        void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/something"))
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @Order(1)
        @DisplayName("Nonexistent Event")
        void whenEventDoesNotExist_thenNotFound() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + new ObjectId()))
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
        @DisplayName("User not authorized")
        void givenUserIsNotAuthorized_thenUnauthorized() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + evento.getId()))
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
        @DisplayName("Correctly delete event by owner")
        void whenOwnerCorrectlyDeletesEvent_thenOk() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + evento.getId()))
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.OK.value(), response.statusCode());
        }
    }


    //Authentication
    private static String getBasicAuthenticationHeader(String email, String password) {
        String valueToEncode = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
