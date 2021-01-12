package garg.digaant.Asset_Management.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BasicEntity implements Serializable {

    //Generates BeanId for all the models(Extended by all models)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
