package ma.fstt.microserviceexaminateur.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@TypeAlias("EXAMINATER")
@Getter
@Setter
public class Examinater extends User {
    @NotBlank
    @Size(max = 12)
    private String cin;
    @NotBlank
    private Date date_of_birth;

    public Examinater(String l_name, String f_name, String username, String password, String email,
                      String phone, String city, String address, String cin, Date date_of_birth) {
        super(null, l_name, f_name, username, password, email, phone, city, address, "EXAMINATER");
        this.cin = cin;
        this.date_of_birth = date_of_birth;
    }
}
