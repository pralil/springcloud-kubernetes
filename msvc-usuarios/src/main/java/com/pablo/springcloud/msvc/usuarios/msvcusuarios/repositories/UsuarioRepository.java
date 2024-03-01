package com.pablo.springcloud.msvc.usuarios.msvcusuarios.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.pablo.springcloud.msvc.usuarios.msvcusuarios.models.entities.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

    Optional<Usuario> findByEmail(String email);

    @Query("select u from Usuario u where u.email=?1")
    Optional<Usuario> porEmail(String email);

    boolean existsByEmail(String email);
}
