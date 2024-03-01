package com.pablo.springcloud.msvc.cursos.msvccursos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pablo.springcloud.msvc.cursos.msvccursos.clients.UsuarioClientRest;
import com.pablo.springcloud.msvc.cursos.msvccursos.models.Usuario;
import com.pablo.springcloud.msvc.cursos.msvccursos.models.entities.Curso;
import com.pablo.springcloud.msvc.cursos.msvccursos.models.entities.CursoUsuario;
import com.pablo.springcloud.msvc.cursos.msvccursos.repositories.CursoRepository;

@Service
public class CursoServiceImpl implements CursoService{

    @Autowired
    private CursoRepository repository;

    @Autowired
    private UsuarioClientRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> findAll() {
        return (List<Curso>) repository.findAll();
    }

    @SuppressWarnings("null")
    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> findById(Long id) {
        return repository.findById(id);
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Curso save(Curso curso) {
        return repository.save(curso);
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Optional<Curso> delete(Long id) {
        Optional<Curso> optCurso = repository.findById(id);
        if(optCurso.isPresent()){
            repository.delete(optCurso.orElseThrow()); 
        }
        return Optional.empty();
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> optCurso = repository.findById(cursoId);
        if(optCurso.isPresent()){
            Usuario usuarioMsvc = client.porId(usuario.getId());
            Curso cursoDb = optCurso.orElseThrow();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            cursoDb.addCursoUsuarios(cursoUsuario);
            repository.save(cursoDb);
            return Optional.of(usuarioMsvc);            
        }
        return Optional.empty();

    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> optCurso = repository.findById(cursoId);
        if(optCurso.isPresent()){
            Usuario usuarioNuevoMsvc = client.crear(usuario);
            Curso cursoDb = optCurso.orElseThrow();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioNuevoMsvc.getId());
            cursoDb.addCursoUsuarios(cursoUsuario);
            repository.save(cursoDb);
            return Optional.of(usuarioNuevoMsvc);            
        }
        return Optional.empty();
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> optCurso = repository.findById(cursoId);
        if(optCurso.isPresent()){
            Usuario usuarioMsvc = client.porId(usuario.getId());
            Curso cursoDb = optCurso.orElseThrow();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            cursoDb.removeCursoUsuarios(cursoUsuario);
            repository.save(cursoDb);
            return Optional.of(usuarioMsvc);            
        }
        return Optional.empty();

    }

    @SuppressWarnings("null")
    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> findByIdWithUsuarios(Long id) {
        Optional<Curso> o = repository.findById(id);
        if(o.isPresent()){
            Curso curso = o.orElseThrow();
            if(!curso.getCursoUsuarios().isEmpty()){
                List<Long> ids = curso.getCursoUsuarios().stream().map(CursoUsuario::getUsuarioId).toList();
                curso.setUsuarios(client.obtenerAlumnosPorCurso(ids));
            }
            return Optional.of(curso);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        repository.eliminarCursoUsuarioPorId(id);
    }

}
