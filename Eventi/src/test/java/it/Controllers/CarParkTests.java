package it.Controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.Entities.CarPark.CarPark;
import it.MainApplication;
import it.Repositories.db.Eventi.CarPark_Repository;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("REST API CarPark tests")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class CarParkTests {
    @Autowired
    CarPark_Repository carPark_repository;

    private final String baseUrl = "http://localhost:8080/api/v1/parcheggio";
    private final String userId = "647378457d34866a873ff0fc";

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
    @DisplayName("Tests on POST method")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class POSTMethodTests {
        @Test
        @Order(1)
        @DisplayName("Create new CarPark")
        void whenPostNewCarPark_thenCreated() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano\"," +
                                    "   \"indirizzo\":                  \"Via vittime di Nassiriya, 5\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"idUtente\":                   \"" + userId + "\"," +
                                    "   \"location\":                   {" +
                                    "                                       \"lat\":    41.134164893406805," +
                                    "                                       \"lng\":    14.776150487836201" +
                                    "                                   }," +
                                    "   \"maxPostiDisponibili\":        \"100+\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.CREATED.value(), response.statusCode());
        }

        @Test
        @Order(2)
        @DisplayName("Location already taken")
        void whenPostExistingCarParkLocation_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano\"," +
                                    "   \"indirizzo\":                  \"Via vittime di Nassiriya, 3\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"idUtente\":                   \"" + userId + "\"," +
                                    "   \"location\":                   {" +
                                    "                                       \"lat\":    41.134164893406805," +
                                    "                                       \"lng\":    14.776150487836201" +
                                    "                                   }," +
                                    "   \"maxPostiDisponibili\":        \"100+\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @Order(3)
        @DisplayName("Address already taken")
        void whenPostExistingCarParkAddress_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano\"," +
                                    "   \"indirizzo\":                  \"Via vittime di Nassiriya, 5\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"idUtente\":                   \"" + userId + "\"," +
                                    "   \"location\":                   {" +
                                    "                                       \"lat\":    100," +
                                    "                                       \"lng\":    100" +
                                    "                                   }," +
                                    "   \"maxPostiDisponibili\":        \"100+\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @Order(4)
        @DisplayName("Missing mandatory fields")
        void whenMissingMandatoryFields_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano\"," +
//                                    "   \"indirizzo\":                  \"Via vittime di Nassiriya, 5\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
//                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"idUtente\":                   \"" + userId + "\"," +
                                    "   \"location\":                   {" +
                                    "                                       \"lat\":    41.134164893406805," +
                                    "                                       \"lng\":    4.776150487836201" +
                                    "                                   }," +
                                    "   \"maxPostiDisponibili\":        \"100+\"" +
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
    @DisplayName("Tests on GET method")
    class GETMethodTests {
        @Nested
        @DisplayName("GET CarPark by ID")
        class GETCarParkByID {
            private final String indirizzo = "Via vittime di Nassiriya, 5";
            private final CarPark carPark = carPark_repository.findCarParkByIndirizzo(indirizzo);

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
            @DisplayName("CarPark doesn't exist: 404")
            void givenCarParkDoesNotExist_whenInfoIsRetrieved_then404IsReceived() throws InterruptedException, ExecutionException {
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
            void givenCarParkExists_whenRequestIsExecuted_thenJsonIsReceived() throws InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + carPark.getId()))
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
            void givenCarParkExists_whenInfoIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException, InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + carPark.getId()))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                CarPark resource = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(carPark_repository.findCarParkById(carPark.getId()), resource);
            }
        }

        @Nested
        @DisplayName("GET CarPark by address")
        class GETCarParkByAddress {
            private final String urlWithAddress = baseUrl + "/indirizzo/";
            private final String address = "Via vittime di Nassiriya, 5";
            private final String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8).replace("+", "%20");

            @Test
            @DisplayName("CarPark doesn't exist: 404")
            void givenCarParkDoesNotExist_whenInfoIsRetrieved_then404IsReceived() throws InterruptedException, ExecutionException {
                String fakeAddress = RandomStringUtils.randomAlphabetic( 10 );

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithAddress + fakeAddress))
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
            void givenCarParkExists_whenRequestIsExecuted_thenJsonIsReceived() throws InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithAddress + encodedAddress))
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
            void givenCarParkExists_whenInfoIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException, InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithAddress + encodedAddress))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                CarPark resource = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(carPark_repository.findCarParkByIndirizzo(address), resource);
            }
        }

        @Nested
        @DisplayName("GET list of CarParks by user ID")
        class GETCarParksByUserId {
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
                String fakeId = (new ObjectId()).toString();

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + fakeId))
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
            @DisplayName("Correct information is retrieved by admin")
            void givenUserExists_whenInfoIsRetrievedByAdmin_thenRetrievedResourceIsCorrect() throws IOException, InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + userId))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                List<CarPark> carParks = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                List<CarPark> carParksExpected = carPark_repository.findCarParksByIdUtente(userId);

                assertEquals(carParksExpected, carParks);
            }

            @Test
            @DisplayName("Correct information is retrieved by creator")
            void givenUserExists_whenInfoIsRetrievedByCreator_thenRetrievedResourceIsCorrect() throws IOException, InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + userId))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                List<CarPark> carParks = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                List<CarPark> carParksExpected = carPark_repository.findCarParksByIdUtente(userId);

                assertEquals(carParksExpected, carParks);
            }

            @Test
            @DisplayName("Unauthorized if not creator or admin")
            void givenUserExists_whenInfoIsRetrievedByNotCreator_thenUnauthorized() throws InterruptedException, ExecutionException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + userId))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
            }
        }
    }

    @Nested
    @Order(3)
    @DisplayName("Tests on PUT method")
    class PUTMethodTests {
        private final String address = "Via vittime di Nassiriya, 5";
        private final CarPark carPark = carPark_repository.findCarParkByIndirizzo(address);


        @Test
        @DisplayName("Modify existing CarPark")
        void whenModifyExistingCarPark_thenOk() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + carPark.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano (modificato)\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"maxParkingSpaces\":           \"150+\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.OK.value(), response.statusCode());
        }

        @Test
        @DisplayName("Modify deleted or nonexistent CarPark")
        void whenModifyingDeletedCarPark_thenNotFound() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + (new ObjectId())))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano (modificato)\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"maxParkingSpaces\":           \"150+\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
        }

        @Test
        @DisplayName("Bad path: not an ObjectId")
        void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/something"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano (modificato)\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"maxParkingSpaces\":           \"150+\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("Missing arguments")
        void whenMissingArguments_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + carPark.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
//                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano (modificato)\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"maxParkingSpaces\":           \"150+\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("User unauthorized")
        void whenUserNotAuthorized_thenUnauthorized() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + carPark.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"nomeParcheggio\":             \"Parcheggio multi-piano (modificato)\"," +
                                    "   \"descrizione\":                \"Comodo e ampio parcheggio vicino al centro città\"," +
                                    "   \"costoOrario\":                \"€1 la prima ora, €0.50 le rimanenti\"," +
                                    "   \"orarioAperturaEChiusura\":    \"24/7\"," +
                                    "   \"apertoAlPubblico\":           true," +

                                    "   \"maxParkingSpaces\":           \"150+\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
        }
    }

    @Nested
    @Order(4)
    @DisplayName("Tests on DELETE method")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DELETEMethodTests {
        String address = "Via vittime di Nassiriya, 5";
        CarPark carPark = carPark_repository.findCarParkByIndirizzo(address);

        @Test
        @Order(1)
        @DisplayName("Unauthorized delete CarPark")
        void whenUnauthorizedDeleteExistingCarPark_thenUnauthorized() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + carPark.getId()))
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
        }

        @Test
        @Order(2)
        @DisplayName("Admin delete existing CarPark")
        void whenAdminDeleteExistingCarPark_thenOk() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + carPark.getId()))
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.OK.value(), response.statusCode());
        }

        @Test
        @Order(3)
        @DisplayName("Delete deleted or nonexistent CarPark")
        void whenDeletingDeletedCarPark_thenNotFound() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + (new ObjectId())))
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
        }

        @Test
        @Order(4)
        @DisplayName("Bad path: not an ObjectId")
        void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/123abc"))
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }
    }

    //Authentication
    private static String getBasicAuthenticationHeader(String email, String password) {
        String valueToEncode = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
