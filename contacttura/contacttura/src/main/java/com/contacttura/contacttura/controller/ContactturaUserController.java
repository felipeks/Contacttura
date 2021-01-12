package com.contacttura.contacttura.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contacttura.contacttura.model.ContactturaUser;
import com.contacttura.contacttura.repository.ContactturaUserRepository;

@RestController
@RequestMapping({ "/contactturauser" })
public class ContactturaUserController {

	@Autowired
	private ContactturaUserRepository repository;

	//Listar usuarios
	@GetMapping
	//http://localhost:8090/user
	public List findAll() {
		return repository.findAll();
	}

	@GetMapping(value = "{id}")
	//http://localhost:8090/user/id do usuario
	public ResponseEntity findById(@PathVariable long id) {
		return repository.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	//http://localhost:8090/user
	public ContactturaUser create (@RequestBody ContactturaUser user) {
		user.setPassword(criptografarPassword(user.getPassword()));
		return repository.save(user);
	}
	
	@PutMapping
	//http://localhost:8090/user
	public ResponseEntity update(@PathVariable long id, @RequestBody ContactturaUser user) {
		return repository.findById(id).map(record -> {
			record.setName(user.getName());
			record.setUsername(user.getUsername());
			record.setPassword(criptografarPassword(user.getPassword()));
			record.setAdmin(false);
			ContactturaUser update = repository.save(record);
			
			return ResponseEntity.ok().body("Cliente: \n"
					+ "Nome: "+ update.getName() + "\n"
					+ "Username: "+ update.getUsername() + "\n"
					+ "Password: Atualizado! \n"
					+ "Atualizado com sucesso");
		}).orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping(path = {"/{id}"})
	//http://localhost:8090/user/id do usuario
	@PreAuthorize("hasRole ('ADMIN')")
	public ResponseEntity<?> delete (@PathVariable long id){
		return repository.findById(id).map(objeto -> {
			repository.deleteById(id);
			return ResponseEntity.ok().body("Cliente "+ objeto.getUsername() + " foid deletado com sucesso!");
		}).orElse(ResponseEntity.notFound().build());
	}

	public String criptografarPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String passwordCriptografado = passwordEncoder.encode(password);
		return passwordCriptografado;
	}
}
