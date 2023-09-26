package it.Repositories.db.Utente;


import it.Entities.Utente.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface User_Repository extends MongoRepository<UserEntity,String> {
    Boolean existsUserEntitiesByUsername(String username);
    @Query("{username:'?0'}")
    UserEntity findUserByUsername(String username);
    UserEntity findUserEntityByUsername(String username);
    Boolean existsByEmail(String email);
    UserEntity findUserEntityById(String id);
    UserEntity findUserEntityByEmail(String email);
    void deleteUserEntityById(String id);
    void deleteUserEntityByEmail(String email);
}
