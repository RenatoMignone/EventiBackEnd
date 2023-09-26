package it.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.Entities.Utente.UserEntity;
import it.MainApplication;
import it.Repositories.db.Utente.User_Repository;
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
@DisplayName("REST API User tests")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class UserTests {
    @Autowired
    User_Repository user_repository;

    private final String baseUrl = "http://localhost:8080/api/v1/";
    private final String baseUrlUser = baseUrl + "utente";
    private final String baseUrlLogin = baseUrl + "login";
    private final String adminUrlUser = baseUrl + "admin/utente";

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
        @DisplayName("Registration")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class registrationTests {
            @Test
            @Order(1)
            @DisplayName("Create new user")
            void whenCreatingNewUser_then201IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"username\":      \"Mattia01\"," +
                                        "    \"password\":      \"Prova01\"," +
                                        "    \"nome\":          \"Mattia\"," +
                                        "    \"cognome\":       \"Marino\"," +
                                        "    \"email\":         \"cetag88109@farebus.com\"," +
                                        "    \"dataNascita\":   \"2001-11-21\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                System.out.println(response);

                assertEquals(HttpStatus.CREATED.value(), response.statusCode());
            }

            @Test
            @Order(2)
            @DisplayName("Underage user: 400")
            void whenUnderageUser_then400IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"username\":      \"Mattia01\"," +
                                        "    \"password\":      \"Prova01\"," +
                                        "    \"nome\":          \"Mattia\"," +
                                        "    \"cognome\":       \"Marino\"," +
                                        "    \"email\":         \"cetag88109@farebus.com\"," +
                                        "    \"dataNascita\":   \"2023-11-21\"" +
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
            @DisplayName("User with missing arguments: 400")
            void whenUserWithMissingArguments_then400IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"username\":      \"Mattia01\"," +
                                        "    \"password\":      \"Prova01\"," +
                                        "    \"email\":         \"cetag88109@farebus.com\"" +
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
            @DisplayName("Existing username: 400")
            void whenExistingUsername_then400IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"username\":      \"Mattia01\"," +
                                        "    \"password\":      \"Prova01\"," +
                                        "    \"nome\":          \"Mattia\"," +
                                        "    \"cognome\":       \"Marino\"," +
                                        "    \"email\":         \"xekehij849@andorem.com\"," +
                                        "    \"dataNascita\":   \"2001-11-21\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @Order(5)
            @DisplayName("Existing email: 400")
            void whenExistingEmail_then400IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"username\":      \"Marino01\"," +
                                        "    \"password\":      \"Prova01\"," +
                                        "    \"nome\":          \"Mattia\"," +
                                        "    \"cognome\":       \"Marino\"," +
                                        "    \"email\":         \"cetag88109@farebus.com\"," +
                                        "    \"dataNascita\":   \"2001-11-21\"" +
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
        @DisplayName("Login")
        class loginTests {
            @Test
            @DisplayName("Correct login")
            void whenLoginWithCorrectCredentials_thenAccepted() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlLogin))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"email\":         \"email\"," +
                                        "    \"password\":      \"password\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.ACCEPTED.value(), response.statusCode());
            }

            @Test
            @DisplayName("Wrong email")
            void whenLoginWithWrongEmail_thenNotFound() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlLogin))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"email\":         \"not_an_email\"," +
                                        "    \"password\":      \"password\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @DisplayName("Wrong password")
            void whenLoginWithWrongPassword_thenUnauthorized() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlLogin))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"email\":         \"email\"," +
                                        "    \"password\":      \"wrong_password\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
            }

            @Test
            @DisplayName("Unverified user")
            void whenLoginWithUnverifiedUser_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlLogin))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"email\":         \"cetag88109@farebus.com\"," +
                                        "    \"password\":      \"Prova01\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }
        }
    }


    @Nested
    @Order(2)
    @DisplayName("Tests on GET methods")
    class GETMethodsTests {
        @Nested
        @DisplayName("Get single user")
        class GetSingleUserTests {
            private final String username = "utente1";
            private final UserEntity user = user_repository.findUserByUsername(username);

            @Test
            @DisplayName("Bad path: not an ObjectId")
            void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser + "/something"))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }

            @Test
            @DisplayName("User doesn't exist: 404")
            void givenUserDoesNotExist_whenInfoIsRetrieved_then404IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser + "/" + new ObjectId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @DisplayName("MIME type: json")
            void getUserByUsername() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser + "/" + user.getId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                System.out.println(response.headers());

                assertTrue(response.headers().toString().contains("application/json"));
            }

            @Test
            @DisplayName("Correct information is retrieved by owner")
            void givenUserExists_whenInfoIsRetrieved_correctInfoIsRetrieved() throws ExecutionException, InterruptedException, JsonProcessingException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser + "/" + user.getId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1034@farebus.com", "password1"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                UserEntity user = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(user_repository.findUserByUsername(username), user);
            }

            @Test
            @DisplayName("Password absent when not owner")
            void whenUnauthorizedAccess_thenPasswordIsRemoved() throws ExecutionException, InterruptedException, JsonProcessingException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrlUser + "/" + user.getId()))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                UserEntity user = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertTrue(HttpStatus.OK.value() == response.statusCode()
                    && user.getPassword() == null);
            }
        }

        @Nested
        @DisplayName("Admin: get all")
        class adminGetAllTests {
            @Test
            @DisplayName("Unauthorized: 401")
            void whenUnauthorizedGetAll_then401IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(adminUrlUser))
                        .header("accept", "application/json")
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
            }

            @Test
            @DisplayName("Not an admin: 403 forbidden")
            void whenUnauthorizedGetAll_then403IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(adminUrlUser))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.FORBIDDEN.value(), response.statusCode());
            }

            @Test
            @DisplayName("Authorized: 200")
            void whenAuthorizedGetAll_then200IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(adminUrlUser))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.OK.value(), response.statusCode());
            }

            @Test
            @DisplayName("MIME type: json")
            void whenAuthorizedGetAll_thenJsonIsReceived() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(adminUrlUser))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertTrue(response.headers().toString().contains("application/json"));
            }

            @Test
            @DisplayName("Correct info is received")
            void whenAuthorizedGetAll_thenCorrectInfoIsReceived() throws ExecutionException, InterruptedException, JsonProcessingException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(adminUrlUser))
                        .header("accept", "application/json")
                        .header("Authorization", getBasicAuthenticationHeader("email", "password"))
                        .GET()
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                ObjectMapper objectMapper = new ObjectMapper();
                List<UserEntity> users = objectMapper.readValue(response.body(),
                        new TypeReference<>() {});

                assertEquals(user_repository.findAll(), users);
            }
        }
    }


    @Nested
    @Order(3)
    @DisplayName("Tests on PUT methods")
    class PUTMethodsTests {
        @Nested
        @DisplayName("Verify user")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class VerifyUserTests {
            private final String urlVerify = baseUrlUser + "/verifica/";

            @Test
            @Order(1)
            @DisplayName("Verify new user")
            void whenNewUserIsVerifying_then200IsReturned() throws ExecutionException, InterruptedException {
                UserEntity user = user_repository.findUserByUsername("Mattia01");

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlVerify + user.getId()))
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.OK.value(), response.statusCode());
            }

            @Test
            @Order(2)
            @DisplayName("User already verified")
            void whenUserIsAlreadyVerified_then200IsReturned() throws ExecutionException, InterruptedException {
                UserEntity user = user_repository.findUserByUsername("Mattia01");

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlVerify + user.getId()))
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.OK.value(), response.statusCode());
            }

            @Test
            @Order(3)
            @DisplayName("Verify deleted/nonexistent user")
            void whenVerifyNonexistentUser_then404IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlVerify + new ObjectId()))
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
            }

            @Test
            @Order(4)
            @DisplayName("Verify user with invalid ID")
            void whenVerifyWithInvalidID_then400IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlVerify + "something"))
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }
        }

        @Nested
        @DisplayName("Password recovery")
        class PasswordRecoveryTests {
            String urlRecoveryPassword = baseUrlLogin + "/recovery/";

            @Test
            @DisplayName("Correct password recovery")
            void whenCorrectPasswordRecoveryIsPerformed_then200IsReturned() throws ExecutionException, InterruptedException {
                UserEntity user = user_repository.findUserByUsername("Mattia01");

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlRecoveryPassword + user.getId()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"nuovaPassword\":             \"Prova01\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.OK.value(), response.statusCode());
            }

            @Test
            @DisplayName("User deleted/nonexistent")
            void whenChangePasswordNonexistentUser_then404IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlRecoveryPassword + new ObjectId()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"nuovaPassword\":             \"Prova01\"" +
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
            void whenChangePasswordIncorrectID_then400IsReturned() throws ExecutionException, InterruptedException {
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlRecoveryPassword + "not_an_id"))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(
                                "{" +
                                        "    \"nuovaPassword\":             \"Prova01\"" +
                                        "}"
                        ))
                        .build();

                CompletableFuture<HttpResponse<String>> futureResponse =
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> response = futureResponse.get();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
            }
        }
    }


    @Nested
    @Order(4)
    @DisplayName("Tests on DELETE methods")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DELETEMethodsTests {
        UserEntity user = user_repository.findUserByUsername("Mattia01");

        @Test
        @Order(1)
        @DisplayName("Bad path: not an ObjectId")
        void whenBadPath_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrlUser + "/something"))
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
        @DisplayName("Unauthorized delete")
        void whenUnauthorizedDelete_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrlUser + "/" + user.getId()))
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
        }

        @Test
        @Order(1)
        @DisplayName("Username not found")
        void whenUsernameNotFound_thenBadRequest() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrlUser + "/" + new ObjectId()))
                    .header("Authorization", getBasicAuthenticationHeader("kihivo1035@farebus.com", "password2"))
                    .DELETE()
                    .build();

            CompletableFuture<HttpResponse<String>> futureResponse =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = futureResponse.get();

            assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
        }

        @Test
        @Order(2)
        @DisplayName("Owner delete account")
        void whenOwnerDeletesAccount_thenOk() throws ExecutionException, InterruptedException {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrlUser + "/" + user.getId()))
                    .header("Authorization", getBasicAuthenticationHeader("cetag88109@farebus.com", "Prova01"))
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
