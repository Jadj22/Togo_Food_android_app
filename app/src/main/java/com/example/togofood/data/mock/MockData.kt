package com.example.togofood.data.mock

import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.domain.model.Variant
import com.example.togofood.domain.model.VendorStatus

object MockData {

    // ============================================================
    // 100 CATÉGORIES ALIMENTAIRES
    // Identifiants synchronisés avec FoodIcons
    // ============================================================

    val categories: List<Category> = listOf(
        // 1–20 : Plats et préparations populaires
        Category("ayimolou", "Ayimolou", "🍛"),
        Category("riz", "Riz", "🍚"),
        Category("riz_sauce", "Riz sauce", "🍛"),
        Category("riz_gras", "Riz gras", "🍚"),
        Category("riz_tomate", "Riz à la tomate", "🍅"),
        Category("riz_cantonais", "Riz cantonais", "🍳"),
        Category("poulet", "Poulet", "🍗"),
        Category("poulet_braise", "Poulet braisé", "🍗"),
        Category("poulet_frit", "Poulet frit", "🍗"),
        Category("pate", "Pâte de maïs", "🥣"),
        Category("fufu", "Foufou", "🥣"),
        Category("akoume", "Akoumé", "🥣"),
        Category("akple", "Akplè", "🥣"),
        Category("abolo", "Ablo", "🫓"),
        Category("kom", "Kom", "🫔"),
        Category("akpan", "Akpan", "🥛"),
        Category("attieke", "Attiéké", "🍚"),
        Category("spaghetti", "Spaghetti", "🍝"),
        Category("macaroni", "Macaroni", "🍝"),
        Category("igname", "Igname", "🍠"),

        // 21–40 : Plats traditionnels et accompagnements
        Category("haricots", "Haricots", "🫘"),
        Category("haricots_rouges", "Haricots rouges", "🫘"),
        Category("gari", "Gari", "🥣"),
        Category("gari_foto", "Gari foto", "🍳"),
        Category("akassa", "Akassa", "🥣"),
        Category("ablo_mais", "Ablo de maïs", "🫓"),
        Category("agbelima", "Agbélima", "🥣"),
        Category("pinon", "Pinon", "🍲"),
        Category("koklo_mem", "Koklo mém", "🍗"),
        Category("gboma", "Gboma", "🥬"),
        Category("ademe", "Adémè", "🥬"),
        Category("feuille_manioc", "Feuilles de manioc", "🌿"),
        Category("sauce_arachide", "Sauce arachide", "🥜"),
        Category("sauce_tomate", "Sauce tomate", "🍅"),
        Category("sauce_graine", "Sauce graine", "🍲"),
        Category("sauce_gombo", "Sauce gombo", "🥣"),
        Category("sauce_epinards", "Sauce aux feuilles", "🥬"),
        Category("akoume_ademe", "Akoumé avec adémè", "🥣"),
        Category("akoume_gboma", "Akoumé avec gboma", "🥬"),
        Category("frites_igname", "Frites d'igname", "🍟"),

        // 41–55 : Viandes, poissons et grillades
        Category("poisson", "Poisson", "🐟"),
        Category("poisson_braise", "Poisson braisé", "🐟"),
        Category("poisson_frit", "Poisson frit", "🐟"),
        Category("tilapia", "Tilapia", "🐟"),
        Category("viande_boeuf", "Bœuf", "🥩"),
        Category("viande_mouton", "Mouton", "🍖"),
        Category("viande_chevre", "Chèvre", "🍖"),
        Category("porc", "Porc", "🥩"),
        Category("brochettes", "Brochettes", "🍢"),
        Category("brochettes_boeuf", "Brochettes de bœuf", "🍢"),
        Category("brochettes_poulet", "Brochettes de poulet", "🍢"),
        Category("soya", "Soya", "🥩"),
        Category("akpan_viande", "Viande en sauce", "🍲"),
        Category("omelette", "Omelette", "🍳"),
        Category("oeufs_bouillis", "Œufs bouillis", "🥚"),

        // 56–70 : Street food et snacks
        Category("piment", "Piment", "🌶️"),
        Category("alloco", "Alloco", "🍌"),
        Category("banane_bouillie", "Banane plantain bouillie", "🍌"),
        Category("banane_grillee", "Banane plantain grillée", "🍌"),
        Category("frites_pomme", "Frites de pommes de terre", "🍟"),
        Category("sandwich", "Sandwich", "🥪"),
        Category("hamburger", "Hamburger", "🍔"),
        Category("hot_dog", "Hot-dog", "🌭"),
        Category("pain_omelette", "Pain omelette", "🥪"),
        Category("pain_haricots", "Pain haricots", "🥖"),
        Category("beignets", "Beignets", "🍩"),
        Category("beignets_banane", "Beignets de banane", "🍌"),
        Category("akara", "Akara", "🧆"),
        Category("popcorn", "Pop-corn", "🍿"),
        Category("arachides", "Arachides grillées", "🥜"),

        // 71–82 : Boissons locales et jus
        Category("jus_bissap", "Jus de bissap", "🧃"),
        Category("jus_gingembre", "Jus de gingembre", "🥤"),
        Category("jus_ananas", "Jus d'ananas", "🍍"),
        Category("jus_tamarin", "Jus de tamarin", "🥤"),
        Category("jus_citron", "Jus de citron", "🍋"),
        Category("jus_mangue", "Jus de mangue", "🥭"),
        Category("jus_papaye", "Jus de papaye", "🥤"),
        Category("jus_orange", "Jus d'orange", "🍊"),
        Category("degue", "Dégué", "🥛"),
        Category("yaourt", "Yaourt", "🥛"),
        Category("eau", "Eau", "💧"),
        Category("soda", "Boissons gazeuses", "🥤"),

        // 83–92 : Fruits et desserts
        Category("mangue", "Mangue", "🥭"),
        Category("ananas", "Ananas", "🍍"),
        Category("papaye", "Papaye", "🍈"),
        Category("pastèque", "Pastèque", "🍉"),
        Category("orange", "Orange", "🍊"),
        Category("banane", "Banane", "🍌"),
        Category("avocat", "Avocat", "🥑"),
        Category("salade_fruits", "Salade de fruits", "🍇"),
        Category("crepe", "Crêpes", "🥞"),
        Category("gateau", "Gâteau", "🎂"),

        // 93–100 : Autres préparations
        Category("pizza", "Pizza", "🍕"),
        Category("tacos", "Tacos", "🌮"),
        Category("nouilles", "Nouilles", "🍜"),
        Category("salade", "Salade", "🥗"),
        Category("soupe", "Soupe", "🍲"),
        Category("crevettes", "Crevettes", "🍤"),
        Category("crabe", "Crabe", "🦀"),
        Category("escargots", "Escargots", "🐌")
    )

    // ============================================================
    // VENDEURS DE DÉMONSTRATION
    // ============================================================

    val sellers: List<Seller> = listOf(
        Seller(
            uuid = "s1",
            name = "Chez Mama Adjoa",
            description = "Spécialiste de l'Ayimolou, du riz sauce et des plats locaux.",
            phone = "+22890112233",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Tokoin",
            landmark = "Près du marché Assiyéyé",
            rating = 4.8f,
            reviewCount = 142,
            isVerified = true,
            distanceMeters = 380,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
        Seller(
            uuid = "s2",
            name = "Kossivi Jus Frais",
            description = "Jus naturels de bissap, gingembre et fruits de saison.",
            phone = "+22891223344",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Bè Kpota",
            landmark = "En face de l'école primaire",
            rating = 4.6f,
            reviewCount = 87,
            isVerified = true,
            distanceMeters = 620,
            SellerOpenStatus = SellerOpenStatus.CLOSED
        ),
        Seller(
            uuid = "s3",
            name = "Brochettes de Yaovi",
            description = "Soya et brochettes de bœuf et de poulet grillées au charbon.",
            phone = "+22892334455",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Agoè",
            landmark = "Près du rond-point d'Agoè",
            rating = 4.5f,
            reviewCount = 63,
            isVerified = false,
            distanceMeters = 1100,
            SellerOpenStatus = SellerOpenStatus.OPENING_SOON
        ),
        Seller(
            uuid = "s4",
            name = "Chez Akossiwa",
            description = "Foufou, pâte de maïs et sauces traditionnelles préparés sur place.",
            phone = "+22893445566",
            photoUrl = "",
            vendorStatus = VendorStatus.CLOSED,
            zone = "Adidogomé",
            landmark = "Près du marché d'Adidogomé",
            rating = 4.9f,
            reviewCount = 211,
            isVerified = true,
            distanceMeters = 900,
            SellerOpenStatus = SellerOpenStatus.CLOSED
        ),
        Seller(
            uuid = "s5",
            name = "Snacks de Komi",
            description = "Beignets, akara, pain omelette et petites préparations.",
            phone = "+22894556677",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Nyékonakpoè",
            landmark = "Derrière le CHU",
            rating = 4.3f,
            reviewCount = 44,
            isVerified = false,
            distanceMeters = 450,
            SellerOpenStatus = SellerOpenStatus.OPEN
        )
    )

    // ============================================================
    // PRÉPARATIONS DE DÉMONSTRATION
    // ============================================================

    val preparations: List<Preparation> = listOf(
        Preparation(
            uuid = "p1",
            name = "Ayimolou complet",
            description = "Riz et haricots accompagnés de piment, d'oignons et des garnitures disponibles chez le vendeur.",
            photoUrl = "",
            categoryId = "ayimolou",
            sellerId = "s1",
            sellerName = "Chez Mama Adjoa",
            sellerZone = "Tokoin",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(
                Variant("Petite portion", 500),
                Variant("Portion moyenne", 700),
                Variant("Grande portion", 1000)
            ),
            rating = 4.9f,
            reviewCount = 89,
            distanceMeters = 380,
            isPopular = true
        ),
        Preparation(
            uuid = "p2",
            name = "Riz sauce tomate",
            description = "Riz accompagné d'une sauce tomate maison, avec poisson ou poulet selon les disponibilités.",
            photoUrl = "",
            categoryId = "riz_sauce",
            sellerId = "s1",
            sellerName = "Chez Mama Adjoa",
            sellerZone = "Tokoin",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(
                Variant("Petite portion", 1000),
                Variant("Grande portion", 2000)
            ),
            rating = 4.7f,
            reviewCount = 54,
            distanceMeters = 380,
            isPopular = true
        ),
        Preparation(
            uuid = "p3",
            name = "Jus de bissap",
            description = "Boisson à base de fleurs d'hibiscus, fraîche et légèrement acidulée.",
            photoUrl = "",
            categoryId = "jus_bissap",
            sellerId = "s2",
            sellerName = "Kossivi Jus Frais",
            sellerZone = "Bè Kpota",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(
                Variant("Sachet", 200),
                Variant("50 cl", 500),
                Variant("1 litre", 1000),
                Variant("5 litres", 4000)
            ),
            rating = 4.6f,
            reviewCount = 73,
            distanceMeters = 620,
            isPopular = true
        ),
        Preparation(
            uuid = "p4",
            name = "Jus de gingembre",
            description = "Boisson artisanale au gingembre frais et au citron.",
            photoUrl = "",
            categoryId = "jus_gingembre",
            sellerId = "s2",
            sellerName = "Kossivi Jus Frais",
            sellerZone = "Bè Kpota",
            availabilityStatus = AvailabilityStatus.TO_CHECK,
            variants = listOf(
                Variant("Sachet", 250),
                Variant("50 cl", 600)
            ),
            rating = 4.5f,
            reviewCount = 31,
            distanceMeters = 620,
            isPopular = true
        ),
        Preparation(
            uuid = "p5",
            name = "Brochettes de bœuf",
            description = "Morceaux de bœuf marinés et grillés au charbon de bois.",
            photoUrl = "",
            categoryId = "brochettes_boeuf",
            sellerId = "s3",
            sellerName = "Brochettes de Yaovi",
            sellerZone = "Agoè",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(
                Variant("2 brochettes", 500),
                Variant("5 brochettes", 1200),
                Variant("10 brochettes", 2000)
            ),
            rating = 4.5f,
            reviewCount = 48,
            distanceMeters = 1100,
            isPopular = true
        ),
        Preparation(
            uuid = "p6",
            name = "Foufou sauce gombo",
            description = "Foufou accompagné d'une sauce gombo et de poisson fumé.",
            photoUrl = "",
            categoryId = "fufu",
            sellerId = "s4",
            sellerName = "Chez Akossiwa",
            sellerZone = "Adidogomé",
            availabilityStatus = AvailabilityStatus.UNAVAILABLE,
            variants = listOf(
                Variant("Portion", 1500)
            ),
            rating = 4.9f,
            reviewCount = 120,
            distanceMeters = 900,
            isPopular = true
        ),
        Preparation(
            uuid = "p7",
            name = "Pâte sauce adémè",
            description = "Pâte de maïs accompagnée d'une sauce adémè préparée à la manière locale.",
            photoUrl = "",
            categoryId = "akoume_ademe",
            sellerId = "s4",
            sellerName = "Chez Akossiwa",
            sellerZone = "Adidogomé",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(
                Variant("Petite portion", 1000),
                Variant("Grande portion", 1800)
            ),
            rating = 4.8f,
            reviewCount = 95,
            distanceMeters = 900,
            isPopular = true
        ),
        Preparation(
            uuid = "p8",
            name = "Akara",
            description = "Beignets salés à base de haricots, croustillants à l'extérieur.",
            photoUrl = "",
            categoryId = "akara",
            sellerId = "s5",
            sellerName = "Snacks de Komi",
            sellerZone = "Nyékonakpoè",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(
                Variant("5 pièces", 200),
                Variant("10 pièces", 400)
            ),
            rating = 4.3f,
            reviewCount = 29,
            distanceMeters = 450
        ),
        Preparation(
            uuid = "p9",
            name = "Alloco",
            description = "Bananes plantain mûres frites, à déguster seules ou avec un accompagnement.",
            photoUrl = "",
            categoryId = "alloco",
            sellerId = "s1",
            sellerName = "Chez Mama Adjoa",
            sellerZone = "Tokoin",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(
                Variant("Petite portion", 300),
                Variant("Grande portion", 500)
            ),
            rating = 4.7f,
            reviewCount = 38,
            distanceMeters = 380,
            isPopular = true
        ),
        Preparation(
            uuid = "p10",
            name = "Jus de tamarin",
            description = "Boisson artisanale au tamarin, au goût fruité et acidulé.",
            photoUrl = "",
            categoryId = "jus_tamarin",
            sellerId = "s2",
            sellerName = "Kossivi Jus Frais",
            sellerZone = "Bè Kpota",
            availabilityStatus = AvailabilityStatus.AVAILABLE,
            variants = listOf(
                Variant("Sachet", 200),
                Variant("50 cl", 500)
            ),
            rating = 4.4f,
            reviewCount = 22,
            distanceMeters = 620
        )
    )
}
