package com.pablo.springcloud.msvc.cursos.msvccursos.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pablo.springcloud.msvc.cursos.msvccursos.models.Usuario;
import com.pablo.springcloud.msvc.cursos.msvccursos.models.entities.Curso;
import com.pablo.springcloud.msvc.cursos.msvccursos.services.CursoService;

import feign.FeignException;
import jakarta.validation.Valid;


@RestController
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public List<Curso> listar() {
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Optional<Curso> optCurso = service.findById(id);
        if(optCurso.isPresent()){
            return ResponseEntity.ok().body(optCurso.orElseThrow());
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/detalle-con-alumnos/{id}")
    public ResponseEntity<?> detalleConAlumnos(@PathVariable Long id){
        Optional<Curso> optCurso = service.findByIdWithUsuarios(id);
        if(optCurso.isPresent()){
            return ResponseEntity.ok().body(optCurso.orElseThrow());
        }
        return ResponseEntity.notFound().build();

    }


    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result){
        if(result.hasErrors()){
            return getErrors(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(curso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            return getErrors(result);
        }
        Optional<Curso> optCurso = service.findById(id);
        if(optCurso.isPresent()) {
            Curso cursoDb = optCurso.orElseThrow();
            cursoDb.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(cursoDb));      
        }
        return ResponseEntity.notFound().build(); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Curso> optCurso = service.delete(id);
        if(optCurso.isPresent()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o; 
        try {
            o = service.asignarUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections
                        .singletonMap("mensaje", "No existe el usuario " 
                        + "o error de comunicacion" + e.getMessage()));
        }
        if(o.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.orElseThrow());
        }    
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o; 
        try {
            o = service.crearUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections
                        .singletonMap("mensaje", "No se pudo crear el usuario " 
                        + "o error de comunicacion" + e.getMessage()));
        }
        if(o.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.orElseThrow());
        }    
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o; 
        try {
            o = service.eliminarUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections
                        .singletonMap("mensaje", "No se pudo eliminar el usuario " 
                        + "o error de comunicacion" + e.getMessage()));
        }
        if(o.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(o.orElseThrow());
        }    
        return ResponseEntity.notFound().build();
    }    
    
    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id){
        service.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> getErrors(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() +" "+ err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
    


}
 