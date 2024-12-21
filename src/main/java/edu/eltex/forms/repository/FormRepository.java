package edu.eltex.forms.repository;

import edu.eltex.forms.entities.Form;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FormRepository extends CrudRepository<Form, Integer> {

    /**
     * Найти опрос по названию
     * @param title название опроса
     * @return форма опроса
     */
    Optional<Form> findByTitle(String title);

    /**
     * Найти опрос по создателю
     * @param username имя пользователя-создателя
     * @return форма опроса
     */
    List<Form> findAllByCreator_Username(String username);
}
