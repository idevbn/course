package com.ead.course.repositories;

import com.ead.course.models.ModuleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ModuleModel, UUID>,
        JpaSpecificationExecutor<ModuleModel> {

    /**
     * O @Query é utilizado apenas para CONSULTAS.
     *
     * Para queries de atualização, remoção e criação
     * é preciso utilizar além do @Query, o @Modifying.
     *
     * Consulta que busca todos os elementos da tabela
     * tb_modules para o caso no qual a chave estrangeira
     * course_id é igual ao
     * @param id
     * @return
     */
    @Query(value = "SELECT * FROM tb_modules WHERE course_course_id = :id", nativeQuery = true)
    List<ModuleModel> findAllModulesIntoCourse(@Param("id") final UUID id);

    @Query(value = "SELECT * FROM tb_modules WHERE course_course_id = :courseId AND module_id = :moduleId", nativeQuery = true)
    Optional<ModuleModel> findModuleIntoCourse(
            @Param("courseId") final UUID courseId,
            @Param("moduleId") final UUID moduleId
    );

}
