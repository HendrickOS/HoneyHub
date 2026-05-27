package fr.honeygroup.repository;

import fr.honeygroup.bo.*;
import fr.honeygroup.enumeration.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;

public class RepositoryTestHelper {

    public static User buildValidUser(String email) {
        return User.builder()
                .email(email)
                .password("password123")
                .nom("Dupont")
                .prenom("Jean")
                .role(Role.CLIENT)
                .build();
    }

    public static User persistValidUser(TestEntityManager em, String email) {
        User u = buildValidUser(email);
        return em.persist(u);
    }

    public static Pole buildValidPole(String nom) {
        return Pole.builder()
                .nom(nom)
                .description("Description du pole")
                .build();
    }

    public static Pole persistValidPole(TestEntityManager em, String nom) {
        Pole p = buildValidPole(nom);
        return em.persist(p);
    }

    public static Prestation buildValidPrestation(Pole pole, String titre) {
        return Prestation.builder()
                .pole(pole)
                .titreService(titre)
                .description("Description valide de plus de 10 caractères")
                .prixBase(100.0)
                .statut(StatutPrestation.ACTIF)
                .build();
    }

    public static Prestation persistValidPrestation(TestEntityManager em, Pole pole, String titre) {
        Prestation p = buildValidPrestation(pole, titre);
        return em.persist(p);
    }

    public static Session buildValidSession(Prestation prestation) {
        return Session.builder()
                .prestation(prestation)
                .dateDebut(LocalDateTime.now().plusDays(1))
                .dateFin(LocalDateTime.now().plusDays(5))
                .capaciteMax(10)
                .statutSession(StatutSession.OUVERT)
                .nbInscrits(0)
                .build();
    }

    public static Session persistValidSession(TestEntityManager em, Prestation prestation) {
        Session s = buildValidSession(prestation);
        return em.persist(s);
    }

    public static Booking buildValidBooking(User user, Session session) {
        return Booking.builder()
                .user(user)
                .session(session)
                .nbPlaces(2)
                .typeReservation(TypeReservation.SESSION)
                .montantTotal(BigDecimal.valueOf(200.0))
                .statut(StatutBooking.EN_ATTENTE_PAIEMENT)
                .build();
    }

    public static Booking persistValidBooking(TestEntityManager em, User user, Session session) {
        Booking b = buildValidBooking(user, session);
        return em.persist(b);
    }

    public static DemandeLead buildValidDemandeLead(Pole pole, User user) {
        return DemandeLead.builder()
                .pole(pole)
                .user(user)
                .source("WEB")
                .statut(StatutLead.NOUVEAU)
                .nomContact("Contact")
                .emailContact("contact@domain.com")
                .build();
    }

    public static DemandeLead persistValidDemandeLead(TestEntityManager em, Pole pole, User user) {
        DemandeLead dl = buildValidDemandeLead(pole, user);
        return em.persist(dl);
    }

    public static DetailsSpecifiques buildValidDetailsSpecifiques(DemandeLead lead, String key, String val) {
        return DetailsSpecifiques.builder()
                .demandeLead(lead)
                .champCle(key)
                .valeur(val)
                .build();
    }

    public static DetailsSpecifiques persistValidDetailsSpecifiques(TestEntityManager em, DemandeLead lead, String key, String val) {
        DetailsSpecifiques ds = buildValidDetailsSpecifiques(lead, key, val);
        return em.persist(ds);
    }

    public static PasswordResetToken buildValidPasswordResetToken(User user, String token) {
        return PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(2))
                .build();
    }

    public static PasswordResetToken persistValidPasswordResetToken(TestEntityManager em, User user, String token) {
        PasswordResetToken prt = buildValidPasswordResetToken(user, token);
        return em.persist(prt);
    }

    public static RefreshToken buildValidRefreshToken(User user, String token) {
        return RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();
    }

    public static RefreshToken persistValidRefreshToken(TestEntityManager em, User user, String token) {
        RefreshToken rt = buildValidRefreshToken(user, token);
        return em.persist(rt);
    }

    public static Profile buildValidProfile(User user, String telephone) {
        return Profile.builder()
                .user(user)
                .adresse("123 Rue de Test")
                .telephone(telephone)
                .pays("France")
                .build();
    }

    public static Profile persistValidProfile(TestEntityManager em, User user, String telephone) {
        Profile p = buildValidProfile(user, telephone);
        return em.persist(p);
    }

    public static Photo buildValidPhoto(String url) {
        return Photo.builder()
                .urlFichier(url)
                .legende("Image Legende")
                .build();
    }

    public static Photo persistValidPhoto(TestEntityManager em, String url) {
        Photo p = buildValidPhoto(url);
        return em.persist(p);
    }

    public static Payment buildValidPayment(Booking booking, String transactionId) {
        return Payment.builder()
                .booking(booking)
                .transactionId(transactionId)
                .montantPaye(BigDecimal.valueOf(100.0))
                .methode(TypePayment.VIREMENT_BANCAIRE)
                .statutPaiement(StatutPayment.EN_VERIFICATION)
                .build();
    }

    public static Payment persistValidPayment(TestEntityManager em, Booking booking, String transactionId) {
        Payment p = buildValidPayment(booking, transactionId);
        return em.persist(p);
    }
}
