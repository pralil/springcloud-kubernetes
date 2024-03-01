package com.pablo.springcloud.msvc.usuarios.msvcusuarios.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pablo.springcloud.msvc.usuarios.msvcusuarios.models.entities.Usuario;
import com.pablo.springcloud.msvc.usuarios.msvcusuarios.services.UsuarioService;

import jakarta.validation.Valid;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    
    @GetMapping
    public Map<String, List<Usuario>> listar(){
        return Collections.singletonMap("usuarios", service.findAll());
    } 

    @GetMapping("/{id}")
    public ResponseEntity<?> porId(@PathVariable Long id ){
        Optional<Usuario> optUsuario = service.findById(id);
        if(optUsuario.isPresent()){
            return ResponseEntity.ok().body(optUsuario.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result){
        if(result.hasErrors()){
            return getErrors(result);
        }
        if(service.existsByEmail(usuario.getEmail())){
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "El email ya se encuentra registrado!"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(usuario));
        

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id ){
        if(result.hasErrors()){
            return getErrors(result);
        }
        Optional<Usuario> optUsuario = service.findById(id);
        if(optUsuario.isPresent()){
            Usuario usuarioDb = optUsuario.orElseThrow();
            if(!usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail()) && service.existsByEmail(usuario.getEmail())){
                return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "El email ya se encuentra registrado!"));
            }   
            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setApellido(usuario.getApellido());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setPassword(usuario.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(usuarioDb));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Usuario> optionalUsuario = service.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuarioDb = optionalUsuario.orElseThrow();
            Optional<Usuario> optionalUsuarioDb = service.delete(usuarioDb.getId());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(optionalUsuarioDb);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }
    
    @GetMapping("/usuarios-por-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids){

        return ResponseEntity.ok(service.listarPorIds(ids));

    }
  
    private ResponseEntity<?> getErrors(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());        
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
