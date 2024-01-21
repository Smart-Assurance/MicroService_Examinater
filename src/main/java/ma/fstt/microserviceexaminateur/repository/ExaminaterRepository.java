package ma.fstt.microserviceexaminateur.repository;

import ma.fstt.microserviceexaminateur.entities.Examinater;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ExaminaterRepository extends MongoRepository<Examinater, String> {

}