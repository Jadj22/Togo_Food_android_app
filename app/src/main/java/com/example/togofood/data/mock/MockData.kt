package com.example.togofood.data.mock

import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.domain.model.Variant
import com.example.togofood.domain.model.VendorStatus

object MockData {

    val categories: List<Category> = listOf(
        Category("riz", "Riz", "🍚"),
        Category("jus", "Jus", "🥤"),
        Category("poulet", "Poulet", "🍗"),
        Category("pate", "Pâte", "🍲"),
        Category("fufu", "Fufu", "🥘"),
        Category("snacks", "Snacks", "🥟"),
        Category("brochettes", "Brochettes", "🍢"),
        Category("boissons", "Boissons", "🧃"),
    )

    val sellers: List<Seller> = listOf(
        Seller(
            uuid = "s1", name = "Chez Mama Adjoa", description = "Spécialiste du riz sauce et plats locaux depuis 10 ans",
            phone = "+22890112233", photoUrl = "", vendorStatus = VendorStatus.ACTIVE,
            zone = "Tokoin", landmark = "Près du marché Assiyéyé", rating = 4.8f, reviewCount = 142,
            isVerified = true, distanceMeters = 380,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
        Seller(
            uuid = "s2", name = "Kossivi Jus Frais", description = "Jus naturels pressés à la commande, sans colorants",
            phone = "+22891223344", photoUrl = "", vendorStatus = VendorStatus.ACTIVE,
            zone = "Bè Kpota", landmark = "En face de l'école primaire", rating = 4.6f, reviewCount = 87,
            isVerified = true, distanceMeters = 620,
            SellerOpenStatus = SellerOpenStatus.CLOSED,
        ),
        Seller(
            uuid = "s3", name = "Brochettes de Yaovi", description = "Brochettes de bœuf et poulet grillées au charbon",
            phone = "+22892334455", photoUrl = "", vendorStatus = VendorStatus.ACTIVE,
            zone = "Agoè Fidjrossè", landmark = "Rond-point Agoè", rating = 4.5f, reviewCount = 63,
            isVerified = false, distanceMeters = 1100,
            SellerOpenStatus = SellerOpenStatus.OPENING_SOON
        ),
        Seller(
            uuid = "s4", name = "Chez Akossiwa", description = "Fufu et pâte sauce fraîchement préparés chaque matin",
            phone = "+22893445566", photoUrl = "", vendorStatus = VendorStatus.CLOSED,
            zone = "Adidogomé", landmark = "Marché Adidogomé", rating = 4.9f, reviewCount = 211,
            isVerified = true, distanceMeters = 900,
            SellerOpenStatus = SellerOpenStatus.CLOSED
        ),
        Seller(
            uuid = "s5", name = "Snacks de Komi", description = "Beignets, accra et gâteaux locaux",
            phone = "+22894556677", photoUrl = "", vendorStatus = VendorStatus.ACTIVE,
            zone = "Nyékonakpoè", landmark = "Derrière le CHU", rating = 4.3f, reviewCount = 44,
            isVerified = false, distanceMeters = 450,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
    )

    val preparations: List<Preparation> = listOf(
        Preparation(
            uuid = "p1", name = "Riz sauce arachide", description = "Riz blanc avec sauce d'arachide maison, accompagné de viande",
            photoUrl = "", categoryId = "riz", sellerId = "s1", sellerName = "Chez Mama Adjoa", sellerZone = "Tokoin",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(Variant("Petite portion", 1000), Variant("Grande portion", 2000)),
            rating = 4.9f, reviewCount = 89, distanceMeters = 380, isPopular = true
        ),
        Preparation(
            uuid = "p2", name = "Riz sauce tomate", description = "Riz sauce tomate avec poisson frit ou poulet",
            photoUrl = "", categoryId = "riz", sellerId = "s1", sellerName = "Chez Mama Adjoa", sellerZone = "Tokoin",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(Variant("Portion", 1500), Variant("Double", 2500)),
            rating = 4.7f, reviewCount = 54, distanceMeters = 380
        ),
        Preparation(
            uuid = "p3", name = "Jus de bissap", description = "Jus d'hibiscus naturel, légèrement sucré, très rafraîchissant",
            photoUrl = "", categoryId = "jus", sellerId = "s2", sellerName = "Kossivi Jus Frais", sellerZone = "Bè Kpota",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(Variant("Sachet", 200), Variant("50 cl", 500), Variant("1 L", 1000), Variant("5 L", 4000)),
            rating = 4.6f, reviewCount = 73, distanceMeters = 620, isPopular = true
        ),
        Preparation(
            uuid = "p4", name = "Jus de gingembre", description = "Jus de gingembre frais avec citron, tonifiant",
            photoUrl = "", categoryId = "jus", sellerId = "s2", sellerName = "Kossivi Jus Frais", sellerZone = "Bè Kpota",
            availabilityStatus = AvailabilityStatus.TO_CHECK,
            variants = listOf(Variant("Sachet", 250), Variant("50 cl", 600)),
            rating = 4.5f, reviewCount = 31, distanceMeters = 620
        ),
        Preparation(
            uuid = "p5", name = "Brochettes bœuf", description = "Brochettes de bœuf marinées et grillées au charbon de bois",
            photoUrl = "", categoryId = "brochettes", sellerId = "s3", sellerName = "Brochettes de Yaovi", sellerZone = "Agoè",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(Variant("2 brochettes", 500), Variant("5 brochettes", 1200), Variant("10 brochettes", 2000)),
            rating = 4.5f, reviewCount = 48, distanceMeters = 1100, isPopular = true
        ),
        Preparation(
            uuid = "p6", name = "Fufu sauce gombo", description = "Fufu de manioc avec sauce gombo et poisson fumé",
            photoUrl = "", categoryId = "fufu", sellerId = "s4", sellerName = "Chez Akossiwa", sellerZone = "Adidogomé",
            availabilityStatus = AvailabilityStatus.UNAVAILABLE,
            variants = listOf(Variant("Portion", 1500)),
            rating = 4.9f, reviewCount = 120, distanceMeters = 900
        ),
        Preparation(
            uuid = "p7", name = "Pâte sauce palme", description = "Pâte de maïs avec sauce palme et viande de mouton",
            photoUrl = "", categoryId = "pate", sellerId = "s4", sellerName = "Chez Akossiwa", sellerZone = "Adidogomé",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(Variant("Petite", 1000), Variant("Grande", 1800)),
            rating = 4.8f, reviewCount = 95, distanceMeters = 900, isPopular = true
        ),
        Preparation(
            uuid = "p8", name = "Beignets haricots", description = "Beignets de haricots croustillants, idéaux pour le petit déjeuner",
            photoUrl = "", categoryId = "snacks", sellerId = "s5", sellerName = "Snacks de Komi", sellerZone = "Nyékonakpoè",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(Variant("5 pièces", 200), Variant("10 pièces", 400)),
            rating = 4.3f, reviewCount = 29, distanceMeters = 450
        ),
        Preparation(
            uuid = "p9", name = "Ailimolu", description = "Spécialité locale mijotée avec igname et épices locales",
            photoUrl = "", categoryId = "pate", sellerId = "s1", sellerName = "Chez Mama Adjoa", sellerZone = "Tokoin",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(Variant("Portion", 1000)),
            rating = 4.7f, reviewCount = 38, distanceMeters = 380, isPopular = true
        ),
        Preparation(
            uuid = "p10", name = "Jus de tamarin", description = "Jus de tamarin acidulé et sucré, très apprécié",
            photoUrl = "", categoryId = "jus", sellerId = "s2", sellerName = "Kossivi Jus Frais", sellerZone = "Bè Kpota",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(Variant("Sachet", 200), Variant("50 cl", 500)),
            rating = 4.4f, reviewCount = 22, distanceMeters = 620
        ),
    )
}
