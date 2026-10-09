# TOGOFOOD — Product Requirements Document (PRD)

**Version :** 1.0  |  **Statut :** Product Master Specification  |  **Date :** 4 septembre 2026

---

## 🎯 Product North Star

> **TogoFood aide une personne à passer de "Je ne sais pas quoi manger / où trouver ça" à "Je sais exactement quoi trouver, chez qui, où et maintenant".**

---

## 📐 Vision

> Permettre à chacun de découvrir facilement la nourriture disponible autour de lui, au bon endroit, au bon moment et avec suffisamment de confiance pour passer à l'action.

**TogoFood N'EST PAS :** application de livraison / marketplace avec panier / annuaire statique / Google Maps alimentaire.

**TogoFood EST :** une plateforme intelligente de **découverte alimentaire locale**.

---

## 🏛️ Les 6 piliers fondamentaux

Toute fonctionnalité doit servir ces 6 piliers :

| # | Pilier | Question |
|---|---|---|
| 1 | **Food Discovery** | QUOI ? |
| 2 | **Localisation** | OÙ ? |
| 3 | **Disponibilité** | QUAND ? |
| 4 | **Prix** | COMBIEN ? |
| 5 | **Confiance** | POURQUOI LUI ? |
| 6 | **Action** | 📞 Appeler / 📍 Itinéraire / ❤️ Favori / ↗ Partager |

> **Règle d'or :** *Est-ce que cela aide réellement l'utilisateur à découvrir, comprendre, évaluer ou atteindre une nourriture locale ? Si non → non prioritaire.*

---

## 🔟 Principes produit (P1–P10)

| Code | Principe | À retenir |
|---|---|---|
| **P1** | Discovery First | La découverte est le cœur |
| **P2** | Local First | Proximité et contexte local essentiels |
| **P3** | Reality First | Ne jamais afficher une disponibilité certaine si l'info est ancienne |
| **P4** | Food First | La nourriture > les mécanismes commerciaux |
| **P5** | Trust by Design | La confiance doit être visible dans l'UX |
| **P6** | Simplicity | Compréhensible par des utilisateurs peu technophiles |
| **P7** | Mobile First | Android en priorité |
| **P8** | Low Connectivity | Utile même avec une connexion faible |
| **P9** | Local Business Respect | Respecter les modèles économiques variés des vendeurs |
| **P10** | No Fake E-commerce | **Ne pas imposer panier, stock, commande ou livraison si non nécessaires** |

---

## 📊 Priorisation des fonctionnalités

| Priorité | Fonctionnalités |
|---|---|
| **P0** ✅ | Recherche, Préparation, Vendeur, Localisation, Disponibilité, Horaires, Prix, Catégories, Favoris, Avis, Appels, Itinéraires, Offline basique, Modération, Analytics |
| **P1** 🔜 | Recommandations, Notifications, Historique avancé, Recherche intelligente, Carte améliorée |
| **P2** 🔭 | Personnalisation avancée, Stats vendeur, Fonctionnalités pro |
| **P3** 🚀 | Marketplace, Paiement, Livraison, IA avancée |

---

## ❌ Hors MVP — Ne pas construire maintenant

- Livraison complète
- Portefeuille électronique / Paiement intégré
- **Panier obligatoire** ← (viole P10)
- Gestion de stock avancée / ERP vendeur / Comptabilité
- Programme de fidélité complexe
- Publicité avancée
- IA conversationnelle complète
- Système logistique

---

## 📱 Navigation principale

```
🏠 Accueil  |  🔎 Recherche  |  ❤️ Favoris  |  🔔 Alertes  |  👤 Profil
```

---

## 📋 Page Préparation — Contenu obligatoire

Doit répondre à : **QUOI ? COMBIEN ? DISPONIBLE ? CHEZ QUI ? OÙ ? CONFIANCE ?**

Contenu : photo, nom, description, prix, variantes, disponibilité, vendeur, distance, localisation, horaires, note, avis, actions.

**Actions :**
```
❤️ Favori  |  📞 Appeler  |  📍 Voir l'emplacement  |  ↗ Partager
```

---

## 📋 Page Vendeur — Contenu obligatoire

Contenu : nom, photo/logo, description, téléphone, statut, horaires, localisation, quartier, point de repère, préparations, avis, note, réputation.

**Actions :**
```
📞 Appeler  |  📍 Itinéraire  |  ❤️ Favori  |  ↗ Partager
```

---

## 💰 Variantes de prix

Une préparation peut avoir **plusieurs formats/prix** :

```
Jus de bissap
Sachet    200 FCFA
50 cl     500 FCFA
1 L     1 000 FCFA
5 L     4 000 FCFA
```

Ne **jamais** supposer un seul prix par préparation.

---

## 🟢 États de disponibilité

```
🟢 Disponible        🟢 Ouvert maintenant
🟡 À vérifier        🟠 Ferme bientôt
🔴 Indisponible      ⚪ Inconnu
```

Accompagner d'une **fraîcheur** : *"Disponible — vérifié récemment"* / *"Disponibilité à vérifier"*

**3 dimensions distinctes :**
- Statut vendeur : ACTIF | FERMÉ | SUSPENDU
- Horaires : Ouvert selon planning
- Disponibilité réelle : Disponible | Indisponible | Inconnue

---

## 🔐 Authentification

La découverte **doit** pouvoir commencer **sans inscription**.

**Sans compte :** consulter, rechercher, voir vendeurs, appeler, obtenir itinéraire.

**Avec compte :** favoris synchronisés, historique, avis, notifications, personnalisation.

> Ne jamais demander un compte avant que cela soit réellement nécessaire.

---

## ⚡ Performance attendue

| Expérience | Objectif |
|---|---:|
| Ouverture écran cache | < 300 ms |
| API simple | < 300 ms |
| Recherche | < 500 ms |
| Détail | < 400 ms |
| Action utilisateur | < 500 ms |

---

## 📐 Décision produit finale

```
Découverte        > Commerce
Pertinence        > Quantité
Réalité           > Apparence
Confiance         > Volume
Simplicité        > Complexité
Local             > Générique
Action réelle     > Engagement artificiel
Utilité           > Notifications
Expérience        > Fonctionnalités
```
