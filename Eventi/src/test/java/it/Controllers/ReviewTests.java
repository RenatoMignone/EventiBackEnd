package it.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.Entities.Review.ReviewEntity;
import it.MainApplication;
import it.Repositories.db.Eventi.Review_Repository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("REST API Review tests")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ReviewTests {
    @Autowired
    Review_Repository review_repository;

    private final String baseUrl = "http://localhost:8080/api/v1/recensione";
    private final String idEventoTest = "648b3d7607fb775a4f701eee";
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
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class POSTMethodTests {
        @Test
        @Order(1)
        @DisplayName("Exceeding stars")
        void whenPostNewReviewWithExceedingStars_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "    \"idEvento\":          \"" + idEventoTest + "\"," +
                                    "    \"idUtente\":          \"" + idCliente + "\"," +
                                    "    \"numeroStelle\":      42," +
                                    "    \"testo\":             \"Too good\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @Order(2)
        @DisplayName("Missing mandatory fields")
        void whenPostNewReviewWithMissingFields_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "    \"idUtente\":          \"" + idCliente + "\"," +
                                    "    \"numeroStelle\":      6," +
                                    "    \"testo\":             \"Good\"" +
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
        @DisplayName("Nonexistent user id")
        void whenNonexistentUserId_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "    \"idEvento\":          \"" + idEventoTest + "\"," +
                                    "    \"idUtente\":          \"" + new ObjectId() + "\"," +
                                    "    \"numeroStelle\":      42," +
                                    "    \"testo\":             \"Good\"" +
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
        @DisplayName("Create review correctly")
        void whenPostNewReview_ThenCreated() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "    \"idEvento\":          \"" + idEventoTest + "\"," +
                                    "    \"idUtente\":          \"" + idCliente + "\"," +
                                    "    \"numeroStelle\":      6," +
                                    "    \"testo\":             \"Quite nice\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.CREATED.value(), response.statusCode());
        }

        @Test
        @Order(5)
        @DisplayName("Max 1 review per user on same event")
        void givenReviewAlreadyCreatedForThatEventForThatUser_whenPostNewReview_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "    \"idEvento\":          \"" + idEventoTest + "\"," +
                                    "    \"idUtente\":          \"" + idCliente + "\"," +
                                    "    \"numeroStelle\":      2," +
                                    "    \"testo\":             \"Not good\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @Order(6)
        @DisplayName("User didn't buy ticket")
        void givenUserDidNotBuyATicket_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "    \"idEvento\":          \"" + idEventoTest + "\"," +
                                    "    \"idUtente\":          \"647378457d34866a873ff0fc\"," +
                                    "    \"numeroStelle\":      2," +
                                    "    \"testo\":             \"Not good\"" +
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
    @DisplayName("Tests on GET methods")
    class GETMethodsTests {
        @Nested
        @DisplayName("GET review by ID")
        class GETReviewById {
            //Just for testing
            List<ReviewEntity> reviewsList = review_repository.findReviewEntitiesByIdEvento(idEventoTest);
            ReviewEntity review = reviewsList.get(0);

            @Test
            @DisplayName("Get correct review by id")
            void whenGetExistingReview_thenCorrectReviewIsRetrieved() throws ExecutionException, InterruptedException, JsonProcessingException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + review.getId()))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                ReviewEntity resource = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(resource, review_repository.findReviewEntityById(review.getId()));
            }

            @Test
            @DisplayName("Review doesn't exist")
            void givenReviewDoesNotExist_NotFound() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + idEventoTest))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @DisplayName("Correct MIME type")
            void givenReviewExists_JsonIsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + review.getId()))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertTrue(response.headers().toString().contains("application/json"));
            }

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
        }

        @Nested
        @DisplayName("GET review IDs by user ID")
        class GETReviewIdsByUserId {
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
                        .uri(URI.create(urlWithUser + (new ObjectId())))
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
                        .uri(URI.create(urlWithUser + idCliente))
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
            void givenUserExists_whenInfoIsRetrievedByAdmin_thenRetrievedResourceIsCorrect() throws ExecutionException, InterruptedException, JsonProcessingException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + idCliente))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                List<String> reviewIDs = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                List<ReviewEntity> reviews = review_repository.findReviewEntitiesByIdUtente(idCliente);
                List<String> ids = new ArrayList<>();
                for (ReviewEntity x : reviews)
                    ids.add(x.getId());

                assertEquals(ids, reviewIDs);
            }

            @Test
            @DisplayName("Correct information is retrieved by owner")
            void givenUserExists_whenInfoIsRetrievedByOwner_thenRetrievedResourceIsCorrect() throws ExecutionException, InterruptedException, JsonProcessingException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + idCliente))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                List<String> reviewIDs = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                List<ReviewEntity> reviews = review_repository.findReviewEntitiesByIdUtente(idCliente);
                List<String> ids = new ArrayList<>();
                for (ReviewEntity x : reviews)
                    ids.add(x.getId());

                assertEquals(ids, reviewIDs);
            }

            @Test
            @DisplayName("Unauthorized if not owner or admin")
            void givenUserExists_whenInfoIsRetrievedByNotCreator_thenUnauthorized() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlWithUser + idCliente))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1036@farebus.com", "password3"))
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
    @DisplayName("Tests on PUT methods")
    class PUTMethodsTests {
        //Just for testing
        List<ReviewEntity> reviewsList = review_repository.findReviewEntitiesByIdEvento(idEventoTest);
        ReviewEntity review = reviewsList.get(0);

        @Test
        @DisplayName("Modify review correctly by admin")
        void whenModifyExistingReviewCorrectlyByAdmin_thenOk() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + review.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"numeroStelle\":       7," +
                                    "   \"testo\":              \"Nice\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.OK.value(), response.statusCode());
        }

        @Test
        @DisplayName("Modify review correctly by owner")
        void whenModifyExistingReviewCorrectlyByCreator_thenOk() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + review.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"numeroStelle\":       5," +
                                    "   \"testo\":              \"Not so nice\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.OK.value(), response.statusCode());
        }

        @Test
        @DisplayName("Unauthorized if not creator or admin")
        void whenUnauthorizedModifyAttempt_thenUnauthorized() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + review.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1036@farebus.com", "password3"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "   \"numeroStelle\":       7," +
                                    "   \"testo\":              \"Nice\"" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
        }

        @Test
        @DisplayName("Modifying deleted/nonexistent review")
        void whenModifyingNonexistentReview_thenNotFound() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + (new ObjectId())))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "    \"numeroStelle\":      8" +
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
                                    "    \"numeroStelle\":      8" +
                                    "}"
                    ))
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        }

        @Test
        @DisplayName("Exceeding stars")
        void whenModifyingWithExceedingStars_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + review.getId()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "{" +
                                    "    \"numeroStelle\":     -3," +
                                    "    \"testo\":            \"Too bad\"" +
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
    @Order(4)
    @DisplayName("Tests on DELETE methods")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DELETEMethodsTests {
        @Test
        @Order(1)
        @DisplayName("Delete deleted/nonexistent review")
        void whenDeletingNonexistentReview_thenNotFound() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + idEventoTest))
                    .header("accept", "application/json")
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
        }

        @Test
        @Order(1)
        @DisplayName("Bad path: not an ObjectId")
        void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + "something"))
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
        @DisplayName("Unauthorized delete")
        void whenUnauthorizedDelete_thenUnauthorized() throws ExecutionException, InterruptedException {
            //Just for testing
            List<ReviewEntity> reviewsList = review_repository.findReviewEntitiesByIdUtente(idCliente);
            ReviewEntity review = reviewsList.get(0);

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + review.getId()))
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
        @DisplayName("Delete review correctly by admin")
        void whenDeletingExistingReviewByAdmin_thenOk() throws ExecutionException, InterruptedException {
            //Just for testing
            List<ReviewEntity> reviewsList = review_repository.findReviewEntitiesByIdEvento(idEventoTest);
            ReviewEntity review = reviewsList.get(0);

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + review.getId()))
                    .header("accept", "application/json")
                    .header("Authorization", getBasicAuthenticationHeader("email", "password"))
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
