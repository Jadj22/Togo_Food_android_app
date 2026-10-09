---
name: togofood-evolution
description: >-
  Use this skill to read the project evolution and architecture history of TogoFood before proposing architectural changes, making big features, or when you need to understand the current state of the app without reading all files.
---

# TogoFood Evolution & Architecture Guide

Ce projet suit des règles strictes de développement (Single Responsibility, SOLID, architecture MVVM, petits composants réutilisables).

## Instructions obligatoires

1. **Lire l'historique** : Avant de commencer une nouvelle implémentation majeure, tu DOIS lire le fichier [project_evolution.md](../../../project_evolution.md) à la racine du projet pour comprendre l'architecture actuelle, ce qui a déjà été implémenté et comment.
2. **Respecter l'architecture** : Base tes décisions sur l'architecture décrite dans l'historique :
   - Garder les vues "stupides" (dumb components) : L'état doit être géré par des `ViewModel`.
   - Utiliser le **Design System** : Utilise les composants déjà existants dans `ui/components/` (`PrimaryButton`, `PhoneTextField`, etc.) au lieu d'en recréer de nouveaux.
   - Pas de textes en dur : Tous les nouveaux textes doivent aller dans `res/values/strings.xml`.
3. **Mettre à jour le journal** : Lorsque tu implémentes une nouvelle fonctionnalité importante ou que tu modifies l'architecture, tu DOIS mettre à jour le fichier `project_evolution.md` (section Historique ou Nouvelles fonctionnalités) pour refléter ton travail afin que les prochaines sessions aient le contexte à jour.
