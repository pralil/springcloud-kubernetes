package com.pablo.springcloud.msvc.cursos.msvccursos.services;

import java.util.List;
import java.util.Optional;

import com.pablo.springcloud.msvc.cursos.msvccursos.models.Usuario;
import com.pablo.springcloud.msvc.cursos.msvccursos.models.entities.Curso;

public interface CursoService {

    List<Curso> findAll();
    Optional<Curso> findById(Long id);
    Optional<Curso> findByIdWithUsuarios(Long id);
    Curso save(Curso curso);
    Optional<Curso> delete(Long id);   
    
    Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId);

    void eliminarCursoUsuarioPorId(Long id);

}
