package it.Controllers.Utente;


import it.Entities.Utente.DTOs.User_LoginDTO;
import it.Entities.Utente.DTOs.User_PasswordReco;
import it.Entities.Utente.DTOs.User_RegisterDTO;
import it.Entities.Utente.DTOs.User_passwordRecovery;
import it.Entities.Utente.UserEntity;
import it.Services.Utente.User_Service_Impl;
import jakarta.annotation.security.RolesAllowed;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("api/v1/")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
public class User_Controller {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final User_Service_Impl service;
    private final PasswordEncoder encoder;

    @Autowired
    public User_Controller(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, User_Service_Impl service, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.service = service;
        this.encoder = encoder;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
//Tested
    @PostMapping("utente")
    public ResponseEntity<?> register(@RequestBody User_RegisterDTO userRegisterDTO)
    {
        if(service.usernameTaken(userRegisterDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This username is already taken");
        }
        if(service.emailAlreadyUsed(userRegisterDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email is already taken");
        }
        if(userRegisterDTO.checkUnderage()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The user is under the requested age");
        }

        userRegisterDTO.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));

        return service.registerUser(userRegisterDTO);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping("utente/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id){
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        UserEntity user = service.getById(id);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found, try a different ID");

        //Version 1
//        if(service.getAuthority(user.getEmail())){
//            return ResponseEntity.status(HttpStatus.OK).body(user);
//        }
//        else{
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user not authorized");
//        }

        // Version 2
        // If an unauthorized user is trying to get the user page, remove the password for security reasons
        if(!User_Service_Impl.getAuthority2(user.getEmail()))
            user.setPassword(null);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    @DeleteMapping("utente/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id){
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        UserEntity user = service.getById(id);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found, try a different username");

        //Version 1
//        if(service.getAuthority(user.getEmail())){
//            return service.deleteUserByEmail(user.getEmail());
//        }
//        else{
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user not authorized");
//        }

        //Version 2
        if(!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        return service.deleteUserByEmail(user.getEmail());
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    //Tested
    @PutMapping("utente/verifica/{id}")
    public ResponseEntity<HttpStatus> verificato (@PathVariable String id){
        if (!ObjectId.isValid(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (service.getById(id) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        service.stato_verificato(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    //Non sono sicuro che sia davvero un metodo get
    @GetMapping("/login/recovery")
    public ResponseEntity<HttpStatus> email_passwordRecovery(@RequestBody User_PasswordReco user){
        if (service.userByEmail(user.getEmail()) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return service.email_senderPassword(user.getEmail());
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
//Tested
    @PostMapping(value = "login",produces = "application/json")
    public ResponseEntity<?> login(@RequestBody User_LoginDTO loginDto){
        UserEntity user = service.userByEmail(loginDto.getEmail());
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(user.getStatus()) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
        }
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
//Tested
    @PutMapping("/login/recovery/{id}")
    public ResponseEntity<HttpStatus> recoverPassword(@RequestBody User_passwordRecovery recovery, @PathVariable String id){
        if (!ObjectId.isValid(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (service.getById(id) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        recovery.setNuovaPassword(encoder.encode(recovery.getNuovaPassword()));
        return service.updatePassword(id,recovery.getNuovaPassword());
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
//Tested
//    //Forse deve essere rimosso
//    @GetMapping("/{username}")
//    public ResponseEntity<UserEntity> get(@PathVariable String username){
//        UserEntity user = service.getSingleUser(username);
//
//        if (user == null)
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }
//-------------------------------------------------------------------------------------------------------------------------------------------
//Tested
    @GetMapping("admin/utente")
    public ResponseEntity<?> getAll() {
        List<UserEntity> users = service.getAll();

        if (users == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    @PostMapping("/admin/utente")
    public ResponseEntity<?> registerAdmin(@RequestBody User_RegisterDTO userRegisterDTO){
        return service.registerUserAdmin(userRegisterDTO);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    @DeleteMapping("/admin/utente/")
    public ResponseEntity<?> deleteAdmin(@RequestBody String email){
        return service.deleteAdmin(email);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    //Non dovrebbe essere un PUT?
    @PostMapping("/admin/utente/ruolo/{username}")
    public ResponseEntity<?> setRole (@PathVariable String username, @RequestBody List<String> role){
        UserEntity user = service.findUser(username);
        user.setUserRoles(role);
        return service.save(user);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/admin/utente/{id}")
    @RolesAllowed({"ADMIN"})
    public UserEntity getSingleUser(@PathVariable String id){
        return service.getById(id);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/admin/utente/delete/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<HttpStatus> deleteUserByAdmin(@PathVariable String id) {
        return service.deleteUser(id);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/admin/utente/{id}/ruolo")
    public ResponseEntity<?> getRolesAdmin(@PathVariable String id){
        return service.getRoles(id);
    }
}

//-------------------------------------------------------------------------------------------------------------------------------------------

