package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.User;
import models.reclamation;
import services.reclamationC;
import java.time.LocalDate;

public class chatbot {
    private enum ChatState {
        INITIAL, AWAITING_CONFIRMATION, CLAIM_CREATION
    }
        @FXML
        private TextArea chatArea;
        @FXML
        private TextField inputField;

        private ChatState currentState = ChatState.INITIAL;
        private reclamation pendingClaim;
        private final reclamationC claimService = new reclamationC();

        @FXML
        public void initialize() {
            // Simuler un utilisateur par défaut s'il n'y a pas de session utilisateur active
            if (SessionManager.getCurrentUser() == null) {
                // Si aucun utilisateur connecté, on crée un utilisateur fictif
                User defaultUser = new User(1, "daryl.kuhn@ethereal.email");
                SessionManager.login(defaultUser);
            }
        }

        @FXML
        public void envoyerMessage() {
            String userMessage = inputField.getText().trim().toLowerCase();
            if (userMessage.isEmpty()) return;

            chatArea.appendText("👤 Vous : " + userMessage + "\n");
            inputField.clear();

            String botResponse = processMessage(userMessage);
            chatArea.appendText("🤖 Bot : " + botResponse + "\n");
        }

        private String processMessage(String message) {
            return switch (currentState) {
                case INITIAL -> handleInitialState(message);
                case AWAITING_CONFIRMATION -> handleConfirmation(message);
                case CLAIM_CREATION -> handleClaimCreation(message);
            };
        }

        private String handleInitialState(String message) {
            if (containsAny(message, "bonjour", "salut", "coucou")) {
                return "Bonjour 👋 ! Comment puis-je vous aider aujourd'hui ?";
            }

            if (containsAny(message, "livraison", "non reçu", "colis", "commande")) {
                currentState = ChatState.AWAITING_CONFIRMATION;
                return "Vous semblez avoir un problème de livraison. Souhaitez-vous créer une réclamation ? (Oui/Non)";
            }

            if (containsAny(message, "retour", "défectueux", "cassé", "remboursement")) {
                currentState = ChatState.AWAITING_CONFIRMATION;
                return "Souhaitez-vous initier une demande de retour ou de remboursement ? (Oui/Non)";
            }

            if (containsAny(message, "aide", "assistance", "support")) {
                return "Je peux vous aider avec :\n- Problèmes de livraison\n- Retours produits\n- Réclamations générales";
            }

            return "Je n'ai pas bien compris votre demande. Pouvez-vous reformuler ou choisir parmi ces options ?\n"
                    + "1. Problème de livraison\n2. Retour produit\n3. Autre réclamation";
        }

        private String handleConfirmation(String message) {
            if (containsAny(message, "oui", "yes", "ok", "d'accord")) {
                currentState = ChatState.CLAIM_CREATION;
                pendingClaim = new reclamation();
                return "Parfait ! Commençons la création de votre réclamation.\n"
                        + "Quel titre souhaitez-vous donner à votre réclamation ?\n"
                        + "(Exemple : 'Colis non reçu le 15/05')";
            }

            if (containsAny(message, "non", "non merci", "plus tard", "annuler")) {
                currentState = ChatState.INITIAL;
                return "D'accord. N'hésitez pas à revenir vers moi si vous changez d'avis !";
            }

            // Si réponse ambiguë
            return "Je n'ai pas saisi votre réponse. Veuillez répondre par :\n"
                    + "- 'Oui' pour continuer\n"
                    + "- 'Non' pour annuler";
        }

        private String handleClaimCreation(String message) {
            if (pendingClaim == null) {
                User currentUser = SessionManager.getCurrentUser();
                if (currentUser == null) {
                    return "⚠️ Aucun utilisateur connecté. Veuillez vous connecter pour créer une réclamation.";
                }

                pendingClaim = new reclamation(currentUser.getId());
                pendingClaim.setStatus("en attente");
                pendingClaim.setDate(LocalDate.now());
            }

            if (pendingClaim.getTitre() == null) {
                pendingClaim.setTitre(message);
                return "Décrivez-nous le problème en détail :";
            }

            if (pendingClaim.getDescription() == null) {
                pendingClaim.setDescription(message);
                return "Merci. Quel est votre email pour le suivi ?";
            }

            if (pendingClaim.getEmailUtilisateur() == null) {
                if (!message.contains("@")) {
                    return "Email invalide. Veuillez ressaisir :";
                }
                pendingClaim.setEmailUtilisateur(message);

                try {
                    pendingClaim.setDate(LocalDate.now());
                    pendingClaim.setStatus("en attente");

                    claimService.create(pendingClaim);
                    currentState = ChatState.INITIAL;
                    return "Réclamation créée avec succès ! Numéro : "
                            + pendingClaim.getId();
                } catch (Exception e) {
                    return "Erreur lors de la création : " + e.getMessage();
                }
            }

            return "";
        }

        private boolean containsAny(String input, String... keywords) {
            for (String kw : keywords) {
                if (input.contains(kw)) return true;
            }
            return false;
        }
    }
