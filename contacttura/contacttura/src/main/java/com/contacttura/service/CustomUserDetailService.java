package com.contacttura.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.contacttura.contacttura.model.ContactturaUser;
import com.contacttura.contacttura.repository.ContactturaUserRepository;

@Component
public class CustomUserDetailService implements UserDetailsService{

	private final ContactturaUserRepository contactturaUserRepository;
	
	@Autowired
	public CustomUserDetailService(ContactturaUserRepository contactturaUserRepository) {
	//Método do spring que retorna um userDetails, buscando o user atravez do repositório, recebendo a intanância do repositorio do user local
		this.contactturaUserRepository = contactturaUserRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ContactturaUser user = Optional.ofNullable(contactturaUserRepository.findByUsername(username))
				.orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado"));
		
		//Lista que retornar as autorizações e permissões para cada tipo de usuario
		List<GrantedAuthority> autorityListAdmin = AuthorityUtils.createAuthorityList("ROLE_USER","ROLE_ADMIN");
		List<GrantedAuthority> autorityListUser = AuthorityUtils.createAuthorityList("ROLE_USER");
		
		//Inserindo os dados do meu model de usurario diretamente dentro do model de usuario springSecurity, validando as permissões de administrador ou user
		return new org.springframework.security.core.userdetails.User
				(user.getUsername(),user.getPassword(), user.isAdmin()? autorityListAdmin : autorityListUser);
				
		
		
	}

}
