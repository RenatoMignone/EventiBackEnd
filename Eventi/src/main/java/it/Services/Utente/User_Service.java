package it.Services.Utente;


import it.Entities.Utente.DTOs.User_RegisterDTO;
import it.Entities.Utente.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface User_Service {

    User_RegisterDTO mapToDto(UserEntity userEntity);
    UserEntity mapToEntity(User_RegisterDTO userdto);
    UserEntity findUser(String username);
    Boolean usernameTaken(String username);
    UserEntity getSingleUser(String username);
    List<UserEntity> getAll();
    Boolean emailAlreadyUsed(String email);
    ResponseEntity<?> registerUser(User_RegisterDTO userDTO);
    ResponseEntity<HttpStatus> stato_verificato(String id);
    ResponseEntity<HttpStatus> email_senderPassword(String username);
    UserEntity getById(String id);
    ResponseEntity<HttpStatus> updatePassword(String id,String password);
    UserEntity userByEmail(String email);
    ResponseEntity<?> registerUserAdmin(User_RegisterDTO user);
    ResponseEntity<HttpStatus> deleteAdmin(String email);
    ResponseEntity<HttpStatus> save (UserEntity user);
    //DELETE
    ResponseEntity<HttpStatus> deleteUser(String id);
    ResponseEntity<?> getRoles(String id);
    Boolean getAuthority(String email);
    ResponseEntity<HttpStatus> deleteUserByEmail(String email);
}
