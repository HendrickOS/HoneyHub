package fr.honeygroup.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe de configuration du framework Spring MVC pour la gestion des ressources statiques.
 * <p>
 * Ce composant permet d'exposer des fichiers stockes localement sur le serveur de fichiers 
 * via des requetes HTTP. Il est essentiel pour permettre au client de recuperer des contenus 
 * media (images, documents) associes aux prestations sans passer par le cycle complet de 
 * traitement REST.
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure le mappage entre les URLs de type "/media/**" et le repertoire physique "media/"
     * situe a la racine de l'application ou dans le systeme de fichiers externe.
     * * @param registry Le registre des gestionnaires de ressources a configurer.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/media/**")
            .addResourceLocations("file:media/");
    }
}