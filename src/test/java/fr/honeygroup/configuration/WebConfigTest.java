package fr.honeygroup.configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@DisplayName("Tests de configuration Web (Gestion des ressources)")
class WebConfigTest {

    @Test
    @DisplayName("Configuration : Vérification du mapping du gestionnaire de ressources")
    void webConfig_ResourceHandler_ConfigurationValide() {
        // Mock du registre
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);

        // Configuration du comportement du mock
        when(registry.addResourceHandler("/media/**")).thenReturn(registration);
        when(registration.addResourceLocations("file:media/")).thenReturn(registration);

        // Exécution de la méthode de config
        WebConfig webConfig = new WebConfig();
        webConfig.addResourceHandlers(registry);

        // Vérifications
        verify(registry, times(1)).addResourceHandler("/media/**");
        verify(registration, times(1)).addResourceLocations("file:media/");
    }
}