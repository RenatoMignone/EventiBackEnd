package it.Services.Utente;

import it.BasicServices.Pasquale.Mail.GmailEmailSender;
import it.BasicServices.Pasquale.Mail.String_Templates.Email_templates;
import it.Entities.Utente.DTOs.User_RegisterDTO;
import it.Entities.Utente.UserEntity;
import it.Repositories.db.Utente.User_Repository;
import it.Services.Utente.Exception.UserNotFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Log
@Service
public class User_Service_Impl implements User_Service {
    private final Email_templates templates;
    private final User_Repository userRepo;
    private final GmailEmailSender email;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public User_Service_Impl(Email_templates templates, User_Repository userRepo, GmailEmailSender email) {
        this.templates = templates;
        this.userRepo = userRepo;
        this.email = email;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Maps UserEntity to User_RegisterDTO
     * @param userEntity: UserEntity to map
     * @return User_RegisterDTO mapped from UserEntity
     */

    @Override
    public User_RegisterDTO mapToDto(UserEntity userEntity){
        User_RegisterDTO userdto = new User_RegisterDTO();
        userdto.setAll(userEntity);
        return userdto;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Maps User_RegisterDTO to UserEntity
     * @param userdto: User_RegisterDTO to map
     * @return UserEntity mapped from User_RegisterDTO
     */

    @Override
    public UserEntity mapToEntity(User_RegisterDTO userdto){
        UserEntity userEntity = new UserEntity();
        userEntity.setAll(userdto);
        return userEntity;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Registers a new user with the provided User_RegisterDTO
     * Sends a verification email to the user's email address
     *
     * @param userDTO: User_RegisterDTO containing the user's data
     * @return ResponseEntity with HTTP status code 201 (Created)
     */

    @Override
    public ResponseEntity<?> registerUser(User_RegisterDTO userDTO) {
        //Validation
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        v.validate(userDTO, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo validità email
        if (!isEmail(userDTO.getEmail()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please insert a real email");

        UserEntity userEntity = new UserEntity();
        userEntity.setAll(userDTO);

        userEntity.setStatus(false);

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_CUSTOMER");
        userEntity.setUserRoles(roles);

        userRepo.save(userEntity);
        email.sendEmail(userEntity.getEmail(), templates.Object_Registration_verification(),
                templates.Body_Registration_verification() + userEntity.getId());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<?> registerUserAdmin(User_RegisterDTO userDTO) {
        //Validation
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        v.validate(userDTO, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo età
        if (userDTO.checkUnderage())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is underage");

        //Controllo esistenza utente
        if(usernameTaken(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This username is already taken");
        }
        if(emailAlreadyUsed(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email is already taken");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setAll(userDTO);

        userEntity.setStatus(true);

        userEntity.setUserRoles(new ArrayList<>());
        userEntity.getUserRoles().add("ROLE_ADMIN");

        userRepo.save(userEntity);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public ResponseEntity<HttpStatus> deleteAdmin(@RequestBody String email){
        userRepo.deleteUserEntityByEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public ResponseEntity<HttpStatus> save(UserEntity user) {
        userRepo.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<HttpStatus> email_senderPassword(String username) {
        UserEntity user = userRepo.findUserEntityByEmail(username);
        email.sendEmail(user.getEmail(),templates.Object_password_recovery(),templates.Body_password_recovery()+ user.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Checks if a username is already taken by another user
     * @param username: Username to check
     * @return Boolean indicating if the username is already taken
     */

    public Boolean usernameTaken(String username){
        return userRepo.existsUserEntitiesByUsername(username);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Gets a single UserEntity by its username
     * @param username: Username of the user to get
     * @return UserEntity with the specified username
     * @throws UserNotFoundException if no user with the specified username is found
     */

    public UserEntity getSingleUser(String username) throws UserNotFoundException {
        return userRepo.findUserByUsername(username);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Gets a list of all UserEntity objects
     * @return List of all UserEntity objects
     */

    public List<UserEntity> getAll(){
        return userRepo.findAll();
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Sets the verification status of a user to true
     * @param id: Username of the user to verify
     * @return ResponseEntity with HTTP status code 200 (OK)
     */

    @Override
    public ResponseEntity<HttpStatus> stato_verificato(String id) {
        UserEntity user = userRepo.findUserEntityById(id);
        user.setStatus(true);
        userRepo.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Checks if an email address is already used by another user
     * @param email: Email address to check
     * @return Boolean indicating if the email address is already used
     */

    @Override
    public Boolean emailAlreadyUsed(String email) {
        return userRepo.existsByEmail(email);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Finds a UserEntity by its username
     * @param username: Username of the user to find
     * @return UserEntity with the specified username
     * @throws UserNotFoundException if no user with the specified username is found
     */

    @Override
    public UserEntity findUser(String username) {
        return userRepo.findUserByUsername(username);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public UserEntity getById(String id){
        return userRepo.findUserEntityById(id);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public ResponseEntity<HttpStatus>updatePassword(String id, String newPassword){
        UserEntity user = userRepo.findUserEntityById(id);
        user.setPassword(newPassword);
        userRepo.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public UserEntity userByEmail(String email) {
        return userRepo.findUserEntityByEmail(email);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<HttpStatus> deleteUser(String id) {
        if (!userRepo.existsById(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        userRepo.deleteUserEntityById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<?> getRoles(String id) {
        UserEntity user = userRepo.findUserEntityById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user.getUserRoles());
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public Boolean getAuthority(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String emailAuth = userDetails.getUsername();
            return email.equals(emailAuth);
        }
        return false;
    }

    //Static version for better implementation
    public static Boolean getAuthority2(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            //Access granted if admin
            if (userDetails.getAuthorities().toString().contains("ROLE_ADMIN"))
                return true;

            //Otherwise, check if it's the right user
            return email.equals(userDetails.getUsername());
        }

        return false;
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<HttpStatus> deleteUserByEmail(String email) {
        userRepo.deleteUserEntityByEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    private static boolean isEmail(String input) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return input.matches(emailRegex);
    }
}
