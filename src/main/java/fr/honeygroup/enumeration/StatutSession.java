package fr.honeygroup.enumeration;

/**
 * Cycle de vie opérationnel d'une session fixe du pôle Écotourisme.
 * <p>
 * Cette énumération orchestre les transitions d'états d'une session. Elle garantit 
 * l'intégrité du flux logistique, en empêchant toute mutation incohérente 
 * (ex: clôturer une session qui n'a pas débuté).
 * </p>
 */
public enum StatutSession {

    /** * La session accepte activement les réservations clients. 
     */
    OUVERT {
        @Override
        public boolean peutBasculerVers(StatutSession nouveauStatut) {
            return nouveauStatut == COMPLET || nouveauStatut == ANNULE || nouveauStatut == EN_COURS;
        }
    },

    /** * Le nombre maximal de participants est atteint, les inscriptions sont bloquées.
     * Possibilité de repasser en OUVERT si une place se libère suite à une annulation.
     */
    COMPLET {
        @Override
        public boolean peutBasculerVers(StatutSession nouveauStatut) {
            return nouveauStatut == OUVERT || nouveauStatut == EN_COURS || nouveauStatut == ANNULE;
        }
    },

    /** * Le circuit ou voyage est actuellement en cours de réalisation sur le terrain. 
     */
    EN_COURS {
        @Override
        public boolean peutBasculerVers(StatutSession nouveauStatut) {
            return nouveauStatut == CLOTURE;
        }
    },

    /** * Le voyage est terminé, les clients sont rentrés (clôture du dossier). 
     */
    CLOTURE {
        @Override
        public boolean peutBasculerVers(StatutSession nouveauStatut) {
            return false;
        }
    },

    /** * Le départ a été annulé (raisons climatiques, logistiques, etc.). 
     */
    ANNULE {
        @Override
        public boolean peutBasculerVers(StatutSession nouveauStatut) {
            return false;
        }
    };

    /**
     * Valide si la transition est conforme au workflow opérationnel Honey Group.
     * * @param nouveauStatut Le statut cible vers lequel la session tente de migrer.
     * @return true si le changement d'état respecte le workflow, sinon false.
     */
    public abstract boolean peutBasculerVers(StatutSession nouveauStatut);
}