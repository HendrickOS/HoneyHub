package fr.honeygroup.security;

import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service de chargement des informations utilisateurs pour le framework Spring Security.
 * <p>
 * Cette classe implemente {@link UserDetailsService} pour permettre l'authentification 
 * personnalisee. Elle interroge le referentiel {@link UserRepository} afin de recuperer 
 * l'entite utilisateur a partir de son adresse email, utilisee comme identifiant unique 
 * dans le systeme Honey Group.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Depot de donnees pour l'acces aux entites User.
     */
    private final UserRepository userRepository;

    /**
     * Charge l'utilisateur par son email lors du processus d'authentification.
     * <p>
     * Cette methode est invoquee par le gestionnaire d'authentification de Spring Security.
     * En cas d'absence de l'utilisateur en base de donnees, une exception {@link UsernameNotFoundException} 
     * est levee pour stopper la chaine d'authentification.
     * </p>
     * @param username L'adresse email de l'utilisateur.
     * @return Une instance de {@link UserDetails} correspondant a l'utilisateur.
     * @throws UsernameNotFoundException Si aucun utilisateur n'est associe a cet email.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouve avec l'email : " + username));
    }
}