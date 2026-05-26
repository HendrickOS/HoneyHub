package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de conformité de l'énumération Role (RBAC)")
class RoleTest {

    @Test
    @DisplayName("Conformité : Vérification de l'intégrité des rôles définis")
    void role_EnumConstantes_DoiventEtrePresentes() {
        // Vérification de la présence des constantes attendues
        assertNotNull(Role.valueOf("ADMIN"));
        assertNotNull(Role.valueOf("CLIENT"));
        assertNotNull(Role.valueOf("MANAGER"));
        
        // Vérification du nombre de rôles (sécurité contre les ajouts non documentés)
        assertEquals(3, Role.values().length);
    }

    @Test
    @DisplayName("Identité : Vérification des noms des rôles pour la conformité Spring Security")
    void role_Nommage_DoitCorrespondreAuxAttendus() {
        assertEquals("ADMIN", Role.ADMIN.name());
        assertEquals("CLIENT", Role.CLIENT.name());
        assertEquals("MANAGER", Role.MANAGER.name());
    }
}