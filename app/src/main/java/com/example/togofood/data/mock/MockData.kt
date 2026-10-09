package com.example.togofood.data.mock

import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.domain.model.Variant
import com.example.togofood.domain.model.VendorStatus
import com.example.togofood.ui.components.OptionSelection
import com.example.togofood.ui.components.RequestOption
import com.example.togofood.ui.components.RequestOptionGroup

/**
 * Données de démonstration : catégories, vendeurs et préparations.
 *
 * Les options de « Personnaliser ma demande » ne sont PAS ici : elles sont fournies
 * par `RequestOptions.groupsFor(preparation)` (ui/components/CustomRequest.kt).
 */
object MockData {

    // ============================================================
    // 100 CATÉGORIES ALIMENTAIRES
    // Identifiants synchronisés avec FoodIcons et RequestOptions
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
    // VENDEURS DE DÉMONSTRATION (15)
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
        ),
        Seller(
            uuid = "s6",
            name = "Le Coin du Poulet",
            description = "Poulet braisé, rôti et frit, servi avec attiéké, alloco ou frites.",
            phone = "+22895667788",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Hédzranawoé",
            landmark = "Face à la station-service",
            rating = 4.7f,
            reviewCount = 176,
            isVerified = true,
            distanceMeters = 520,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
        Seller(
            uuid = "s7",
            name = "Poissons Grillés d'Ama",
            description = "Poisson et tilapia braisés, frits ou en sauce, avec leurs accompagnements.",
            phone = "+22896778899",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Kodjoviakopé",
            landmark = "Près de la plage",
            rating = 4.8f,
            reviewCount = 198,
            isVerified = true,
            distanceMeters = 760,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
        Seller(
            uuid = "s8",
            name = "Chez Tantie Esi",
            description = "Sauces traditionnelles, pâtes, foufou et ablo préparés à la maison.",
            phone = "+22897889900",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Amoutivé",
            landmark = "Derrière l'église catholique",
            rating = 4.6f,
            reviewCount = 131,
            isVerified = true,
            distanceMeters = 680,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
        Seller(
            uuid = "s9",
            name = "Snack Étoile",
            description = "Sandwichs, burgers, pains garnis, gâteaux et petites pâtisseries.",
            phone = "+22898990011",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Djidjolé",
            landmark = "À côté de la pharmacie",
            rating = 4.4f,
            reviewCount = 92,
            isVerified = false,
            distanceMeters = 840,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
        Seller(
            uuid = "s10",
            name = "Pizza & Co Lomé",
            description = "Pizzas, tacos, pâtes, nouilles et plats rapides à l'européenne.",
            phone = "+22899001122",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Lomé II",
            landmark = "Près de l'avenue de la Libération",
            rating = 4.5f,
            reviewCount = 118,
            isVerified = true,
            distanceMeters = 1400,
            SellerOpenStatus = SellerOpenStatus.OPENING_SOON
        ),
        Seller(
            uuid = "s11",
            name = "Fruits Frais de Séna",
            description = "Fruits de saison, salades de fruits, jus et yaourts frais.",
            phone = "+22890223344",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Bè Klikamé",
            landmark = "Devant le marché de Bè",
            rating = 4.7f,
            reviewCount = 104,
            isVerified = true,
            distanceMeters = 590,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
        Seller(
            uuid = "s12",
            name = "Maquis Le Palmier",
            description = "Grillades, viandes en sauce, poissons braisés et escargots.",
            phone = "+22891334455",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Ablogamé",
            landmark = "Sous le grand palmier",
            rating = 4.5f,
            reviewCount = 85,
            isVerified = false,
            distanceMeters = 980,
            SellerOpenStatus = SellerOpenStatus.OPENING_SOON
        ),
        Seller(
            uuid = "s13",
            name = "Délices de Dédé",
            description = "Gâteaux, crêpes, beignets, dégué et douceurs maison.",
            phone = "+22892445566",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Kégué",
            landmark = "Près du stade de Kégué",
            rating = 4.6f,
            reviewCount = 72,
            isVerified = true,
            distanceMeters = 1250,
            SellerOpenStatus = SellerOpenStatus.CLOSED
        ),
        Seller(
            uuid = "s14",
            name = "Chez Ablavi",
            description = "Ayimolou, haricots, gari, akoumé, akplè et plats du terroir.",
            phone = "+22893556677",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Totsi",
            landmark = "Près du carrefour de Totsi",
            rating = 4.5f,
            reviewCount = 99,
            isVerified = true,
            distanceMeters = 1050,
            SellerOpenStatus = SellerOpenStatus.OPEN
        ),
        Seller(
            uuid = "s15",
            name = "Fruits de Mer Baguida",
            description = "Crevettes, crabes, escargots et poissons de mer préparés minute.",
            phone = "+22894667788",
            photoUrl = "",
            vendorStatus = VendorStatus.ACTIVE,
            zone = "Baguida",
            landmark = "Au bord de la route de la côte",
            rating = 4.6f,
            reviewCount = 67,
            isVerified = false,
            distanceMeters = 2300,
            SellerOpenStatus = SellerOpenStatus.OPEN
        )
    )

    // ============================================================
    // OUTILS DE CONSTRUCTION
    // Le nom, la zone et la distance sont repris du vendeur.
    // ============================================================

    private fun v(vararg options: Pair<String, Int>): List<Variant> =
        options.map { Variant(it.first, it.second) }

    private fun prep(
        n: Int,
        name: String,
        description: String,
        categoryId: String,
        sellerId: String,
        variants: List<Variant>,
        rating: Float,
        reviewCount: Int,
        popular: Boolean = false,
        availability: AvailabilityStatus = AvailabilityStatus.AVAILABLE
    ): Preparation {
        val seller = sellers.first { it.uuid == sellerId }
        return Preparation(
            uuid = "p$n",
            name = name,
            description = description,
            photoUrl = "",
            categoryId = categoryId,
            sellerId = seller.uuid,
            sellerName = seller.name,
            sellerZone = seller.zone,
            availabilityStatus = availability,
            variants = variants,
            rating = rating,
            reviewCount = reviewCount,
            distanceMeters = seller.distanceMeters,
            isPopular = popular
        )
    }

    // ============================================================
    // 200 PRÉPARATIONS DE DÉMONSTRATION
    // ============================================================

    val preparations: List<Preparation> = listOf(

        // ---------- s1 : Chez Mama Adjoa (p1–p2, p9, p11–p20) ----------
        prep(1, "Ayimolou complet", "Riz et haricots accompagnés de piment, d'oignons et des garnitures disponibles chez le vendeur.", "ayimolou", "s1",
            v("Petite portion" to 500, "Portion moyenne" to 700, "Grande portion" to 1000), 4.9f, 89, popular = true),
        prep(2, "Riz sauce tomate", "Riz accompagné d'une sauce tomate maison, avec poisson ou poulet selon les disponibilités.", "riz_sauce", "s1",
            v("Petite portion" to 1000, "Grande portion" to 2000), 4.7f, 54, popular = true),
        prep(9, "Alloco", "Bananes plantain mûres frites, à déguster seules ou avec un accompagnement.", "alloco", "s1",
            v("Petite portion" to 300, "Grande portion" to 500), 4.7f, 38, popular = true),
        prep(11, "Riz gras", "Riz cuit avec tomate, oignons et épices, servi avec viande ou poisson.", "riz_gras", "s1",
            v("Petite portion" to 700, "Portion moyenne" to 1000, "Grande portion" to 1500), 4.6f, 47),
        prep(12, "Haricots au gari", "Haricots cuits à l'huile rouge, servis avec du gari croustillant.", "haricots", "s1",
            v("Petite portion" to 300, "Grande portion" to 600), 4.5f, 33),
        prep(13, "Gari foto", "Gari sauté aux œufs, oignons, tomates et piment.", "gari_foto", "s1",
            v("Petite portion" to 400, "Grande portion" to 800), 4.4f, 26),
        prep(14, "Spaghetti sauce tomate", "Spaghetti sautés dans une sauce tomate maison légèrement pimentée.", "spaghetti", "s1",
            v("Petite portion" to 500, "Grande portion" to 1000), 4.3f, 41),
        prep(15, "Frites d'igname", "Bâtonnets d'igname dorés, croustillants dehors et moelleux dedans.", "frites_igname", "s1",
            v("Petite portion" to 300, "Portion moyenne" to 500, "Grande portion" to 800), 4.7f, 58, popular = true),
        prep(16, "Riz sauce arachide", "Riz blanc nappé d'une sauce arachide onctueuse avec morceaux de viande.", "sauce_arachide", "s1",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.6f, 35),
        prep(17, "Œufs bouillis", "Œufs durs servis avec sel, piment et oignons.", "oeufs_bouillis", "s1",
            v("1 œuf" to 100, "3 œufs" to 250), 4.2f, 18),
        prep(18, "Banane plantain grillée", "Plantain grillé au feu de bois, accompagné de piment.", "banane_grillee", "s1",
            v("Petite portion" to 200, "Grande portion" to 400), 4.5f, 29),
        prep(19, "Poulet frit et riz", "Cuisse de poulet marinée et frite, servie avec du riz.", "poulet_frit", "s1",
            v("1 cuisse" to 1000, "2 cuisses" to 1800), 4.8f, 64, popular = true),
        prep(20, "Macaroni sauce viande", "Macaroni mijotés dans une sauce à la viande hachée.", "macaroni", "s1",
            v("Petite portion" to 500, "Grande portion" to 900), 4.2f, 20),

        // ---------- s2 : Kossivi Jus Frais (p3–p4, p10, p21–p28) ----------
        prep(3, "Jus de bissap", "Boisson à base de fleurs d'hibiscus, fraîche et légèrement acidulée.", "jus_bissap", "s2",
            v("Sachet" to 200, "50 cl" to 500, "1 litre" to 1000, "5 litres" to 4000), 4.6f, 73, popular = true),
        prep(4, "Jus de gingembre", "Boisson artisanale au gingembre frais et au citron.", "jus_gingembre", "s2",
            v("Sachet" to 250, "50 cl" to 600), 4.5f, 31, popular = true, availability = AvailabilityStatus.TO_CHECK),
        prep(10, "Jus de tamarin", "Boisson artisanale au tamarin, au goût fruité et acidulé.", "jus_tamarin", "s2",
            v("Sachet" to 200, "50 cl" to 500), 4.4f, 22),
        prep(21, "Jus de citron", "Citronnade maison pressée à la minute, peu sucrée.", "jus_citron", "s2",
            v("Sachet" to 200, "50 cl" to 500, "1 litre" to 900), 4.4f, 28),
        prep(22, "Jus de mangue", "Jus épais de mangues mûres, sans conservateur.", "jus_mangue", "s2",
            v("Sachet" to 250, "50 cl" to 600, "1 litre" to 1100), 4.7f, 52, popular = true),
        prep(23, "Jus d'ananas", "Jus d'ananas frais, sucré et rafraîchissant.", "jus_ananas", "s2",
            v("Sachet" to 250, "50 cl" to 600, "1 litre" to 1100), 4.6f, 44, popular = true),
        prep(24, "Jus de papaye", "Jus onctueux de papaye mûre, légèrement citronné.", "jus_papaye", "s2",
            v("50 cl" to 600, "1 litre" to 1100), 4.3f, 17, availability = AvailabilityStatus.TO_CHECK),
        prep(25, "Jus d'orange", "Oranges pressées sur place, sans ajout de sucre.", "jus_orange", "s2",
            v("50 cl" to 600, "1 litre" to 1100), 4.5f, 36),
        prep(26, "Dégué", "Dessert à base de mil et de lait caillé, frais et crémeux.", "degue", "s2",
            v("Petit pot" to 300, "Grand pot" to 600), 4.6f, 40),
        prep(27, "Yaourt nature maison", "Yaourt onctueux préparé artisanalement, légèrement acidulé.", "yaourt", "s2",
            v("Pot" to 250, "50 cl" to 500), 4.4f, 25),
        prep(28, "Eau fraîche", "Eau potable bien fraîche, en sachet ou en bouteille.", "eau", "s2",
            v("Sachet" to 50, "Bouteille 1,5 l" to 300), 4.1f, 12),

        // ---------- s3 : Brochettes de Yaovi (p5, p29–p36) ----------
        prep(5, "Brochettes de bœuf", "Morceaux de bœuf marinés et grillés au charbon de bois.", "brochettes_boeuf", "s3",
            v("2 brochettes" to 500, "5 brochettes" to 1200, "10 brochettes" to 2000), 4.5f, 48, popular = true),
        prep(29, "Brochettes de poulet", "Poulet mariné aux épices, grillé au charbon.", "brochettes_poulet", "s3",
            v("2 brochettes" to 500, "5 brochettes" to 1200, "10 brochettes" to 2200), 4.6f, 51, popular = true),
        prep(30, "Soya de bœuf", "Fines tranches de bœuf grillées, relevées d'épices et d'oignons.", "soya", "s3",
            v("Petite portion" to 500, "Portion moyenne" to 1000, "Grande portion" to 2000), 4.7f, 66, popular = true),
        prep(31, "Mouton grillé", "Morceaux de mouton tendres, grillés au feu de bois.", "viande_mouton", "s3",
            v("250 g" to 1500, "500 g" to 2800), 4.5f, 33),
        prep(32, "Chèvre grillée", "Viande de chèvre marinée puis grillée lentement.", "viande_chevre", "s3",
            v("250 g" to 1500, "500 g" to 2800), 4.4f, 27),
        prep(33, "Porc braisé", "Porc braisé au charbon, servi avec oignons et piment.", "porc", "s3",
            v("250 g" to 1200, "500 g" to 2200), 4.3f, 24, availability = AvailabilityStatus.TO_CHECK),
        prep(34, "Poulet braisé", "Poulet braisé entier ou en demi, mariné aux épices locales.", "poulet_braise", "s3",
            v("Demi" to 2500, "Entier" to 4500), 4.6f, 39),
        prep(35, "Omelette garnie", "Omelette aux oignons, tomates et piment.", "omelette", "s3",
            v("Simple" to 300, "Garnie" to 500), 4.2f, 15),
        prep(36, "Sandwich aux brochettes", "Pain frais garni de brochettes effilochées, oignons et sauce piquante.", "sandwich", "s3",
            v("Petit" to 500, "Grand" to 800), 4.5f, 31),

        // ---------- s4 : Chez Akossiwa (p6–p7, p37–p46) ----------
        prep(6, "Foufou sauce gombo", "Foufou accompagné d'une sauce gombo et de poisson fumé.", "fufu", "s4",
            v("Portion" to 1500), 4.9f, 120, popular = true, availability = AvailabilityStatus.UNAVAILABLE),
        prep(7, "Pâte sauce adémè", "Pâte de maïs accompagnée d'une sauce adémè préparée à la manière locale.", "akoume_ademe", "s4",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.8f, 95, popular = true),
        prep(37, "Akoumé sauce gboma", "Akoumé moelleux avec une sauce gboma parfumée au poisson fumé.", "akoume_gboma", "s4",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.8f, 77, popular = true),
        prep(38, "Akplè sauce graine", "Akplè onctueux servi avec une sauce graine de palme riche et épicée.", "akple", "s4",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.7f, 52),
        prep(39, "Pâte de maïs sauce tomate", "Pâte de maïs blanche avec une sauce tomate pimentée et du poisson.", "pate", "s4",
            v("Petite portion" to 800, "Grande portion" to 1500), 4.6f, 44),
        prep(40, "Akassa", "Pâte de maïs fermentée, enveloppée en feuilles, servie avec une sauce.", "akassa", "s4",
            v("Portion" to 500, "Grande portion" to 900), 4.4f, 21),
        prep(41, "Agbélima", "Pâte de manioc fermentée, servie avec une sauce au choix.", "agbelima", "s4",
            v("Portion" to 600, "Grande portion" to 1000), 4.3f, 18, availability = AvailabilityStatus.TO_CHECK),
        prep(42, "Pinon", "Boules de pâte de manioc râpé, mijotées dans une sauce relevée.", "pinon", "s4",
            v("Petite portion" to 1200, "Grande portion" to 2000), 4.5f, 29),
        prep(43, "Koklo mém", "Poulet mijoté à l'étuvée avec oignons, tomates et épices.", "koklo_mem", "s4",
            v("Portion" to 1500, "Grande portion" to 2500), 4.7f, 38),
        prep(44, "Sauce feuilles de manioc", "Feuilles de manioc pilées cuites à l'huile de palme avec du poisson fumé.", "feuille_manioc", "s4",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.5f, 23),
        prep(45, "Sauce aux feuilles vertes", "Mélange de feuilles locales mijotées avec viande et poisson.", "sauce_epinards", "s4",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.4f, 19),
        prep(46, "Igname bouillie et sauce", "Igname bouillie, accompagnée d'une sauce tomate à l'huile de palme.", "igname", "s4",
            v("Petite portion" to 800, "Grande portion" to 1500), 4.6f, 35),

        // ---------- s5 : Snacks de Komi (p8, p47–p56) ----------
        prep(8, "Akara", "Beignets salés à base de haricots, croustillants à l'extérieur.", "akara", "s5",
            v("5 pièces" to 200, "10 pièces" to 400), 4.3f, 29),
        prep(47, "Beignets", "Beignets de farine dorés, légèrement sucrés.", "beignets", "s5",
            v("5 pièces" to 200, "10 pièces" to 400), 4.4f, 36),
        prep(48, "Beignets de banane", "Beignets moelleux à la banane mûre, parfaits pour le goûter.", "beignets_banane", "s5",
            v("5 pièces" to 200, "10 pièces" to 400), 4.3f, 24),
        prep(49, "Pain omelette", "Pain frais garni d'une omelette aux oignons et tomates.", "pain_omelette", "s5",
            v("Simple" to 300, "Garni" to 500), 4.5f, 47, popular = true),
        prep(50, "Pain haricots", "Pain garni de haricots cuits à l'huile et de piment.", "pain_haricots", "s5",
            v("Petit" to 200, "Grand" to 400), 4.3f, 22),
        prep(51, "Hot-dog", "Pain moelleux garni d'une saucisse, de sauce et d'oignons.", "hot_dog", "s5",
            v("Simple" to 500, "Double" to 800), 4.2f, 19),
        prep(52, "Hamburger", "Pain, steak haché, salade, tomate et sauce maison.", "hamburger", "s5",
            v("Simple" to 1000, "Double" to 1600), 4.1f, 14),
        prep(53, "Pop-corn", "Maïs soufflé salé ou sucré, en sachet.", "popcorn", "s5",
            v("Sachet" to 100, "Grand sachet" to 300), 4.2f, 13),
        prep(54, "Arachides grillées", "Arachides grillées et salées, croquantes.", "arachides", "s5",
            v("Sachet" to 100, "Tasse" to 300), 4.5f, 27),
        prep(55, "Crêpes", "Crêpes fines servies nature ou sucrées.", "crepe", "s5",
            v("2 crêpes" to 300, "4 crêpes" to 550), 4.4f, 21),
        prep(56, "Frites de pommes de terre", "Frites dorées servies avec sel et piment.", "frites_pomme", "s5",
            v("Petite portion" to 400, "Grande portion" to 800), 4.3f, 30),

        // ---------- s6 : Le Coin du Poulet (p57–p70) ----------
        prep(57, "Poulet rôti", "Poulet rôti à la broche, doré et juteux.", "poulet", "s6",
            v("Quart" to 1200, "Demi" to 2300, "Entier" to 4400), 4.7f, 80, popular = true),
        prep(58, "Poulet braisé aux oignons", "Poulet braisé au charbon, servi avec oignons et sauce piquante.", "poulet_braise", "s6",
            v("Quart" to 1300, "Demi" to 2500, "Entier" to 4700), 4.8f, 95, popular = true),
        prep(59, "Poulet frit croustillant", "Morceaux de poulet panés et frits, très croustillants.", "poulet_frit", "s6",
            v("2 morceaux" to 1200, "4 morceaux" to 2200), 4.6f, 62),
        prep(60, "Brochettes de poulet épicées", "Brochettes de poulet marinées au piment, grillées au feu.", "brochettes_poulet", "s6",
            v("3 brochettes" to 800, "6 brochettes" to 1500), 4.5f, 41),
        prep(61, "Attiéké poulet", "Attiéké accompagné de poulet braisé, tomates et oignons.", "attieke", "s6",
            v("Petite portion" to 1200, "Grande portion" to 2200), 4.7f, 58, popular = true),
        prep(62, "Alloco poulet", "Alloco doré servi avec un morceau de poulet braisé.", "alloco", "s6",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.6f, 39),
        prep(63, "Riz à la tomate et poulet", "Riz parfumé à la tomate, servi avec du poulet.", "riz_tomate", "s6",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.5f, 44),
        prep(64, "Spaghetti au poulet", "Spaghetti sautés avec des morceaux de poulet et des légumes.", "spaghetti", "s6",
            v("Petite portion" to 900, "Grande portion" to 1600), 4.3f, 27),
        prep(65, "Frites et poulet", "Frites croustillantes servies avec poulet frit ou braisé.", "frites_pomme", "s6",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.5f, 36),
        prep(66, "Piment pilé maison", "Piment broyé à l'huile, pour relever tous vos plats.", "piment", "s6",
            v("Petit pot" to 200, "Grand pot" to 500), 4.6f, 22),
        prep(67, "Salade composée", "Laitue, tomates, concombre, œuf et vinaigrette.", "salade", "s6",
            v("Petite" to 500, "Grande" to 900), 4.2f, 16),
        prep(68, "Poulet sauce arachide", "Poulet mijoté dans une sauce arachide épaisse et parfumée.", "sauce_arachide", "s6",
            v("Petite portion" to 1200, "Grande portion" to 2200), 4.4f, 20),
        prep(69, "Riz cantonais au poulet", "Riz sauté aux légumes, œufs et morceaux de poulet.", "riz_cantonais", "s6",
            v("Petite portion" to 1200, "Grande portion" to 2200), 4.5f, 33),
        prep(70, "Boissons gazeuses", "Sodas bien frais, en canette ou en bouteille.", "soda", "s6",
            v("Canette" to 400, "Bouteille 1 l" to 700), 4.0f, 9),

        // ---------- s7 : Poissons Grillés d'Ama (p71–p84) ----------
        prep(71, "Poisson braisé", "Poisson entier braisé au charbon, servi avec oignons et piment.", "poisson_braise", "s7",
            v("Moyen" to 2000, "Grand" to 3500), 4.8f, 102, popular = true),
        prep(72, "Tilapia braisé", "Tilapia mariné et braisé, tendre et parfumé.", "tilapia", "s7",
            v("Moyen" to 2500, "Grand" to 4000), 4.8f, 91, popular = true),
        prep(73, "Poisson frit", "Poisson mariné aux épices puis frit à l'huile.", "poisson_frit", "s7",
            v("Petit" to 1000, "Moyen" to 1800), 4.5f, 47),
        prep(74, "Poisson en sauce", "Poisson mijoté dans une sauce tomate relevée.", "poisson", "s7",
            v("Petite portion" to 1200, "Grande portion" to 2200), 4.4f, 31),
        prep(75, "Attiéké poisson", "Attiéké accompagné de poisson braisé, tomates et oignons.", "attieke", "s7",
            v("Petite portion" to 1500, "Grande portion" to 2500), 4.7f, 66, popular = true),
        prep(76, "Alloco poisson", "Alloco doré servi avec du poisson braisé et du piment.", "alloco", "s7",
            v("Petite portion" to 1300, "Grande portion" to 2300), 4.6f, 48),
        prep(77, "Plantain bouilli et poisson", "Banane plantain bouillie accompagnée d'un poisson en sauce.", "banane_bouillie", "s7",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.3f, 21),
        prep(78, "Igname et poisson", "Igname bouillie servie avec un poisson braisé.", "igname", "s7",
            v("Petite portion" to 1200, "Grande portion" to 2000), 4.4f, 26),
        prep(79, "Riz sauce poisson", "Riz blanc nappé d'une sauce poisson tomatée.", "riz_sauce", "s7",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.5f, 38),
        prep(80, "Akoumé poisson fumé", "Akoumé avec une sauce au poisson fumé bien parfumée.", "akoume", "s7",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.6f, 34),
        prep(81, "Piment frais pilé", "Piment frais et oignons pilés à la minute.", "piment", "s7",
            v("Petit pot" to 200, "Grand pot" to 500), 4.5f, 19),
        prep(82, "Soupe de poisson", "Soupe épicée de poisson frais, aux légumes et au gingembre.", "soupe", "s7",
            v("Bol" to 1000, "Grand bol" to 1800), 4.6f, 41),
        prep(83, "Salade fraîche", "Salade de crudités servie avec le poisson.", "salade", "s7",
            v("Petite" to 400, "Grande" to 800), 4.2f, 14),
        prep(84, "Frites d'igname et poisson", "Frites d'igname servies avec du poisson frit.", "frites_igname", "s7",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.5f, 29),

        // ---------- s8 : Chez Tantie Esi (p85–p100) ----------
        prep(85, "Sauce arachide au poulet", "Sauce arachide épaisse avec du poulet, pour accompagner pâte ou riz.", "sauce_arachide", "s8",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.6f, 49),
        prep(86, "Sauce tomate au poisson", "Sauce tomate mijotée avec du poisson fumé et des épices.", "sauce_tomate", "s8",
            v("Petite portion" to 900, "Grande portion" to 1600), 4.5f, 37),
        prep(87, "Sauce graine au poulet", "Sauce de graine de palme riche, avec poulet et poisson fumé.", "sauce_graine", "s8",
            v("Petite portion" to 1200, "Grande portion" to 2200), 4.7f, 53, popular = true),
        prep(88, "Sauce gombo", "Sauce gombo gluante et parfumée, avec poisson fumé.", "sauce_gombo", "s8",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.6f, 45),
        prep(89, "Sauce aux feuilles", "Feuilles vertes locales mijotées à l'huile de palme.", "sauce_epinards", "s8",
            v("Petite portion" to 900, "Grande portion" to 1600), 4.4f, 24),
        prep(90, "Adémè", "Feuilles de corète cuites en sauce glissante, avec poisson fumé.", "ademe", "s8",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.6f, 40),
        prep(91, "Gboma", "Feuilles de gboma mijotées avec viande et poisson fumé.", "gboma", "s8",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.5f, 35),
        prep(92, "Feuilles de manioc au poisson fumé", "Feuilles de manioc finement pilées et cuites longuement.", "feuille_manioc", "s8",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.4f, 22),
        prep(93, "Pâte blanche", "Pâte de maïs blanche lisse, servie avec la sauce de votre choix.", "pate", "s8",
            v("Petite portion" to 800, "Grande portion" to 1500), 4.5f, 42),
        prep(94, "Foufou d'igname", "Foufou d'igname pilé, élastique et moelleux.", "fufu", "s8",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.6f, 38),
        prep(95, "Akpan", "Boisson-dessert fermentée de maïs, onctueuse et fraîche.", "akpan", "s8",
            v("Sachet" to 200, "Pot" to 400), 4.3f, 18),
        prep(96, "Kom", "Boules de pâte de maïs fermentée enveloppées en feuilles.", "kom", "s8",
            v("2 boules" to 200, "5 boules" to 450), 4.4f, 20),
        prep(97, "Ablo", "Petits gâteaux de riz cuits à la vapeur, doux et moelleux.", "abolo", "s8",
            v("5 pièces" to 250, "10 pièces" to 450), 4.5f, 33),
        prep(98, "Ablo de maïs", "Gâteaux de maïs cuits à la vapeur, légèrement sucrés.", "ablo_mais", "s8",
            v("5 pièces" to 250, "10 pièces" to 450), 4.4f, 25),
        prep(99, "Viande en sauce", "Morceaux de bœuf mijotés en sauce tomate épicée.", "akpan_viande", "s8",
            v("Petite portion" to 1200, "Grande portion" to 2200), 4.5f, 31),
        prep(100, "Haricots rouges en sauce", "Haricots rouges mijotés avec oignons, tomates et épices.", "haricots_rouges", "s8",
            v("Petite portion" to 500, "Grande portion" to 900), 4.3f, 16),

        // ---------- s9 : Snack Étoile (p101–p116) ----------
        prep(101, "Sandwich au poulet", "Pain croustillant garni de poulet, crudités et mayonnaise.", "sandwich", "s9",
            v("Petit" to 700, "Grand" to 1200), 4.5f, 44, popular = true),
        prep(102, "Sandwich au thon", "Pain garni de thon, tomates, oignons et sauce.", "sandwich", "s9",
            v("Petit" to 600, "Grand" to 1000), 4.3f, 28),
        prep(103, "Hamburger maison", "Steak haché, fromage, salade et sauce, dans un pain moelleux.", "hamburger", "s9",
            v("Simple" to 1200, "Double" to 1900), 4.4f, 36),
        prep(104, "Burger poulet", "Filet de poulet pané, salade et sauce dans un pain brioché.", "hamburger", "s9",
            v("Simple" to 1300, "Double" to 2000), 4.5f, 30),
        prep(105, "Hot-dog saucisse", "Saucisse grillée, oignons caramélisés et moutarde.", "hot_dog", "s9",
            v("Simple" to 600, "Double" to 1000), 4.3f, 26),
        prep(106, "Pain omelette fromage", "Omelette au fromage fondu dans un pain frais.", "pain_omelette", "s9",
            v("Simple" to 400, "Garni" to 700), 4.6f, 51),
        prep(107, "Pain haricots pimenté", "Pain garni de haricots, d'œuf et de piment.", "pain_haricots", "s9",
            v("Petit" to 250, "Grand" to 450), 4.4f, 23),
        prep(108, "Beignets sucrés", "Beignets dorés saupoudrés de sucre.", "beignets", "s9",
            v("5 pièces" to 250, "10 pièces" to 450), 4.5f, 39),
        prep(109, "Gâteau nature", "Gâteau moelleux à la vanille, vendu en part ou entier.", "gateau", "s9",
            v("Part" to 300, "Moule" to 3000), 4.4f, 21),
        prep(110, "Gâteau au chocolat", "Gâteau fondant au chocolat, très gourmand.", "gateau", "s9",
            v("Part" to 400, "Moule" to 4000), 4.6f, 34),
        prep(111, "Crêpes au sucre", "Crêpes chaudes saupoudrées de sucre.", "crepe", "s9",
            v("2 crêpes" to 300, "4 crêpes" to 550), 4.5f, 29),
        prep(112, "Omelette nature", "Omelette légère aux oignons, servie avec du pain.", "omelette", "s9",
            v("Simple" to 300, "Double" to 500), 4.3f, 24),
        prep(113, "Pop-corn caramel", "Pop-corn enrobé de caramel croustillant.", "popcorn", "s9",
            v("Sachet" to 200, "Grand sachet" to 400), 4.2f, 11, availability = AvailabilityStatus.TO_CHECK),
        prep(114, "Frites croustillantes", "Frites maison, bien dorées, servies avec ketchup.", "frites_pomme", "s9",
            v("Petite portion" to 500, "Grande portion" to 900), 4.4f, 32),
        prep(115, "Yaourt aux fruits", "Yaourt crémeux parfumé aux fruits de saison.", "yaourt", "s9",
            v("Pot" to 300, "Grand pot" to 600), 4.3f, 15),
        prep(116, "Jus d'orange pressé", "Jus d'orange frais pressé à la commande.", "jus_orange", "s9",
            v("33 cl" to 400, "50 cl" to 600), 4.5f, 27),

        // ---------- s10 : Pizza & Co Lomé (p117–p128) ----------
        prep(117, "Pizza margherita", "Tomate, mozzarella et basilic sur une pâte fine.", "pizza", "s10",
            v("Moyenne" to 3000, "Grande" to 4500), 4.6f, 55, popular = true),
        prep(118, "Pizza au poulet", "Poulet, oignons, poivrons et fromage fondant.", "pizza", "s10",
            v("Moyenne" to 3500, "Grande" to 5000), 4.7f, 61, popular = true),
        prep(119, "Pizza viande hachée", "Viande hachée épicée, oignons, olives et fromage.", "pizza", "s10",
            v("Moyenne" to 3500, "Grande" to 5000), 4.5f, 38),
        prep(120, "Tacos poulet", "Galette garnie de poulet, frites, fromage et sauce.", "tacos", "s10",
            v("Simple" to 1500, "Maxi" to 2500), 4.5f, 33),
        prep(121, "Tacos viande hachée", "Galette garnie de viande hachée, frites et sauce fromagère.", "tacos", "s10",
            v("Simple" to 1500, "Maxi" to 2500), 4.4f, 26),
        prep(122, "Nouilles sautées au poulet", "Nouilles sautées aux légumes et morceaux de poulet.", "nouilles", "s10",
            v("Petite portion" to 1200, "Grande portion" to 2000), 4.4f, 29),
        prep(123, "Nouilles aux légumes", "Nouilles sautées avec carottes, choux et poivrons.", "nouilles", "s10",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.2f, 17),
        prep(124, "Spaghetti bolognaise", "Spaghetti avec une sauce à la viande hachée mijotée.", "spaghetti", "s10",
            v("Petite portion" to 1500, "Grande portion" to 2500), 4.5f, 35),
        prep(125, "Macaroni au fromage", "Macaroni gratinés dans une sauce crémeuse au fromage.", "macaroni", "s10",
            v("Petite portion" to 1500, "Grande portion" to 2500), 4.3f, 21),
        prep(126, "Riz cantonais", "Riz sauté aux petits pois, carottes, œufs et jambon.", "riz_cantonais", "s10",
            v("Petite portion" to 1200, "Grande portion" to 2200), 4.5f, 40),
        prep(127, "Salade au poulet", "Salade verte, poulet grillé, tomates, maïs et vinaigrette.", "salade", "s10",
            v("Petite" to 1200, "Grande" to 2000), 4.3f, 18),
        prep(128, "Soupe de légumes", "Soupe chaude de légumes frais, légère et réconfortante.", "soupe", "s10",
            v("Bol" to 800, "Grand bol" to 1400), 4.2f, 14),

        // ---------- s11 : Fruits Frais de Séna (p129–p146) ----------
        prep(129, "Mangue fraîche", "Mangues mûres et juteuses, sélectionnées chaque matin.", "mangue", "s11",
            v("1 fruit" to 200, "3 fruits" to 500, "1 kg" to 800), 4.7f, 56, popular = true),
        prep(130, "Ananas frais", "Ananas sucré et parfumé, vendu en tranche, demi ou entier.", "ananas", "s11",
            v("Tranche" to 150, "Demi" to 500, "Entier" to 1000), 4.8f, 63, popular = true),
        prep(131, "Papaye mûre", "Papaye à chair fondante, prête à déguster.", "papaye", "s11",
            v("Demi" to 300, "Entière" to 600), 4.4f, 22),
        prep(132, "Pastèque en tranches", "Pastèque rouge très rafraîchissante, vendue en tranches.", "pastèque", "s11",
            v("Tranche" to 200, "Entière" to 2500), 4.6f, 41),
        prep(133, "Oranges douces", "Oranges juteuses et sucrées, épluchées sur demande.", "orange", "s11",
            v("3 oranges" to 200, "10 oranges" to 600), 4.5f, 37),
        prep(134, "Bananes douces", "Petites bananes sucrées et bien mûres.", "banane", "s11",
            v("Main" to 300, "Grande main" to 600), 4.4f, 25),
        prep(135, "Avocat", "Avocats crémeux, à maturité.", "avocat", "s11",
            v("1 avocat" to 300, "3 avocats" to 800), 4.5f, 28),
        prep(136, "Salade de fruits maison", "Mangue, ananas, papaye, banane et orange coupés frais.", "salade_fruits", "s11",
            v("Petit bol" to 500, "Grand bol" to 900), 4.7f, 52, popular = true),
        prep(137, "Jus d'ananas frais", "Jus pressé de l'ananas du jour, sans conservateur.", "jus_ananas", "s11",
            v("33 cl" to 400, "50 cl" to 600, "1 litre" to 1100), 4.6f, 40),
        prep(138, "Jus de mangue frais", "Jus épais de mangue, mixé minute.", "jus_mangue", "s11",
            v("33 cl" to 400, "50 cl" to 600, "1 litre" to 1100), 4.6f, 38),
        prep(139, "Jus d'orange frais", "Oranges pressées à la commande.", "jus_orange", "s11",
            v("33 cl" to 400, "50 cl" to 600, "1 litre" to 1100), 4.5f, 33),
        prep(140, "Jus de citron vert", "Citron vert pressé, légèrement sucré et très frais.", "jus_citron", "s11",
            v("33 cl" to 300, "50 cl" to 500), 4.3f, 19),
        prep(141, "Jus de papaye frais", "Jus doux et onctueux de papaye mûre.", "jus_papaye", "s11",
            v("33 cl" to 400, "50 cl" to 600), 4.3f, 16),
        prep(142, "Jus de tamarin frais", "Tamarin macéré et filtré, au goût acidulé.", "jus_tamarin", "s11",
            v("33 cl" to 300, "50 cl" to 500), 4.4f, 20),
        prep(143, "Jus de gingembre piquant", "Gingembre frais pilé avec citron, à la force réglable.", "jus_gingembre", "s11",
            v("33 cl" to 300, "50 cl" to 600), 4.5f, 29),
        prep(144, "Jus de bissap glacé", "Hibiscus infusé et servi bien frais.", "jus_bissap", "s11",
            v("33 cl" to 300, "50 cl" to 500, "1 litre" to 900), 4.6f, 44),
        prep(145, "Yaourt à la mangue", "Yaourt frais parfumé à la mangue.", "yaourt", "s11",
            v("Pot" to 300, "Grand pot" to 600), 4.4f, 18),
        prep(146, "Eau minérale", "Eau minérale fraîche en bouteille.", "eau", "s11",
            v("50 cl" to 200, "1,5 l" to 400), 4.0f, 10),

        // ---------- s12 : Maquis Le Palmier (p147–p162) ----------
        prep(147, "Bœuf grillé", "Morceaux de bœuf grillés au feu de bois, avec oignons et piment.", "viande_boeuf", "s12",
            v("250 g" to 1500, "500 g" to 2800), 4.6f, 49),
        prep(148, "Brochettes de bœuf du maquis", "Brochettes de bœuf marinées, grillées à point.", "brochettes_boeuf", "s12",
            v("3 brochettes" to 800, "6 brochettes" to 1500), 4.6f, 43),
        prep(149, "Poisson braisé du maquis", "Poisson braisé servi avec oignons et piment frais.", "poisson_braise", "s12",
            v("Moyen" to 2200, "Grand" to 3800), 4.7f, 58, popular = true),
        prep(150, "Poulet braisé du maquis", "Poulet braisé au charbon, mariné aux épices maison.", "poulet_braise", "s12",
            v("Demi" to 2500, "Entier" to 4500), 4.6f, 52),
        prep(151, "Porc au four", "Porc cuit lentement au four, tendre et épicé.", "porc", "s12",
            v("250 g" to 1300, "500 g" to 2400), 4.4f, 27),
        prep(152, "Mouton en sauce", "Mouton mijoté dans une sauce tomate épicée.", "viande_mouton", "s12",
            v("Petite portion" to 1500, "Grande portion" to 2700), 4.5f, 31, availability = AvailabilityStatus.UNAVAILABLE),
        prep(153, "Chèvre en sauce", "Chèvre mijotée avec oignons et épices.", "viande_chevre", "s12",
            v("Petite portion" to 1500, "Grande portion" to 2700), 4.5f, 28),
        prep(154, "Escargots grillés", "Escargots épicés et grillés, servis avec piment.", "escargots", "s12",
            v("6 pièces" to 1500, "12 pièces" to 2800), 4.4f, 16),
        prep(155, "Igname braisée", "Tranches d'igname braisées au charbon.", "igname", "s12",
            v("Petite portion" to 600, "Grande portion" to 1100), 4.5f, 34),
        prep(156, "Alloco et viande", "Alloco doré accompagné de viande grillée.", "alloco", "s12",
            v("Petite portion" to 1200, "Grande portion" to 2200), 4.5f, 36),
        prep(157, "Attiéké poisson braisé", "Attiéké avec poisson braisé, tomates et oignons.", "attieke", "s12",
            v("Petite portion" to 1500, "Grande portion" to 2500), 4.6f, 40),
        prep(158, "Riz sauce viande", "Riz blanc avec une sauce à la viande de bœuf.", "riz_sauce", "s12",
            v("Petite portion" to 1000, "Grande portion" to 1900), 4.4f, 30),
        prep(159, "Soupe de chèvre", "Soupe épicée de chèvre, bien relevée et réconfortante.", "soupe", "s12",
            v("Bol" to 1500, "Grand bol" to 2500), 4.6f, 37, popular = true),
        prep(160, "Œufs bouillis pimentés", "Œufs durs servis avec piment et oignons.", "oeufs_bouillis", "s12",
            v("1 œuf" to 100, "3 œufs" to 250), 4.2f, 12),
        prep(161, "Piment vert broyé", "Piment vert frais broyé à la main.", "piment", "s12",
            v("Petit pot" to 200, "Grand pot" to 500), 4.4f, 15),
        prep(162, "Boissons fraîches", "Sodas bien frais, en canette ou en bouteille.", "soda", "s12",
            v("Canette" to 400, "Bouteille 1 l" to 700), 4.0f, 8),

        // ---------- s13 : Délices de Dédé (p163–p178) ----------
        prep(163, "Gâteau à la vanille", "Gâteau moelleux parfumé à la vanille.", "gateau", "s13",
            v("Part" to 300, "Moule" to 3000), 4.6f, 33),
        prep(164, "Gâteau d'anniversaire", "Gâteau décoré sur commande, pour toutes les occasions.", "gateau", "s13",
            v("Petit" to 8000, "Moyen" to 15000, "Grand" to 25000), 4.8f, 29),
        prep(165, "Gâteau à la banane", "Gâteau moelleux aux bananes mûres.", "gateau", "s13",
            v("Part" to 300, "Moule" to 3000), 4.4f, 17),
        prep(166, "Crêpes au chocolat", "Crêpes garnies de chocolat fondu.", "crepe", "s13",
            v("2 crêpes" to 500, "4 crêpes" to 900), 4.7f, 46, popular = true),
        prep(167, "Crêpes à la banane", "Crêpes garnies de banane et de miel.", "crepe", "s13",
            v("2 crêpes" to 450, "4 crêpes" to 800), 4.5f, 28),
        prep(168, "Beignets au sucre", "Beignets légers passés dans le sucre.", "beignets", "s13",
            v("5 pièces" to 250, "10 pièces" to 450), 4.4f, 31),
        prep(169, "Beignets de banane mûre", "Beignets moelleux à base de banane écrasée.", "beignets_banane", "s13",
            v("5 pièces" to 250, "10 pièces" to 450), 4.5f, 24),
        prep(170, "Akara croustillants", "Beignets de haricots frits, croustillants et salés.", "akara", "s13",
            v("5 pièces" to 200, "10 pièces" to 400), 4.4f, 22),
        prep(171, "Dégué crémeux", "Dégué onctueux au lait caillé et au mil.", "degue", "s13",
            v("Petit pot" to 300, "Grand pot" to 600), 4.6f, 35),
        prep(172, "Yaourt vanille", "Yaourt maison parfumé à la vanille.", "yaourt", "s13",
            v("Pot" to 300, "Grand pot" to 600), 4.4f, 20),
        prep(173, "Salade de fruits fraîche", "Fruits de saison coupés avec un filet de citron.", "salade_fruits", "s13",
            v("Petit bol" to 500, "Grand bol" to 900), 4.5f, 26),
        prep(174, "Pop-corn salé", "Pop-corn léger et salé.", "popcorn", "s13",
            v("Sachet" to 100, "Grand sachet" to 300), 4.1f, 10),
        prep(175, "Arachides salées", "Arachides grillées et salées.", "arachides", "s13",
            v("Sachet" to 100, "Tasse" to 300), 4.4f, 19),
        prep(176, "Plantain grillé sucré", "Plantain mûr grillé, servi avec des arachides.", "banane_grillee", "s13",
            v("Petite portion" to 250, "Grande portion" to 450), 4.5f, 25),
        prep(177, "Jus de bissap maison", "Bissap parfumé à la menthe et à la vanille.", "jus_bissap", "s13",
            v("Sachet" to 200, "50 cl" to 500, "1 litre" to 900), 4.5f, 30),
        prep(178, "Jus de gingembre citron", "Gingembre frais, citron et une pointe de miel.", "jus_gingembre", "s13",
            v("Sachet" to 250, "50 cl" to 600), 4.4f, 21),

        // ---------- s14 : Chez Ablavi (p179–p192) ----------
        prep(179, "Ayimolou garni", "Riz et haricots avec œuf, poisson frit, piment et gari.", "ayimolou", "s14",
            v("Petite portion" to 600, "Portion moyenne" to 900, "Grande portion" to 1300), 4.7f, 61, popular = true),
        prep(180, "Riz blanc sauce", "Riz blanc nature avec la sauce du jour.", "riz", "s14",
            v("Petite portion" to 500, "Grande portion" to 1000), 4.4f, 28),
        prep(181, "Riz gras traditionnel", "Riz cuit dans un bouillon de tomate et d'épices.", "riz_gras", "s14",
            v("Petite portion" to 700, "Grande portion" to 1300), 4.6f, 40),
        prep(182, "Haricots à l'huile", "Haricots cuits à l'huile rouge et aux oignons.", "haricots", "s14",
            v("Petite portion" to 300, "Grande portion" to 600), 4.5f, 37),
        prep(183, "Haricots rouges sauce", "Haricots rouges mijotés avec épices et poisson fumé.", "haricots_rouges", "s14",
            v("Petite portion" to 400, "Grande portion" to 800), 4.4f, 22),
        prep(184, "Gari trempé", "Gari trempé dans l'eau froide, sucré ou salé.", "gari", "s14",
            v("Petit bol" to 200, "Grand bol" to 400), 4.3f, 17),
        prep(185, "Gari foto garni", "Gari sauté avec œufs, poisson fumé et piment.", "gari_foto", "s14",
            v("Petite portion" to 500, "Grande portion" to 900), 4.5f, 32),
        prep(186, "Akassa et sauce", "Pâte de maïs fermentée accompagnée d'une sauce au choix.", "akassa", "s14",
            v("Portion" to 500, "Grande portion" to 900), 4.3f, 18),
        prep(187, "Agbélima et sauce", "Pâte de manioc fermentée servie avec sauce.", "agbelima", "s14",
            v("Portion" to 600, "Grande portion" to 1000), 4.3f, 15),
        prep(188, "Akoumé et sauce", "Akoumé moelleux avec sauce tomate et poisson fumé.", "akoume", "s14",
            v("Petite portion" to 800, "Grande portion" to 1500), 4.6f, 36),
        prep(189, "Akplè", "Pâte de maïs fermentée, servie avec une sauce graine.", "akple", "s14",
            v("Petite portion" to 900, "Grande portion" to 1600), 4.6f, 33),
        prep(190, "Pâte de maïs sauce piment", "Pâte de maïs avec sauce pimentée au poisson.", "pate", "s14",
            v("Petite portion" to 800, "Grande portion" to 1500), 4.5f, 29),
        prep(191, "Foufou sauce graine", "Foufou lisse avec une sauce graine riche.", "fufu", "s14",
            v("Petite portion" to 1200, "Grande portion" to 2000), 4.6f, 31),
        prep(192, "Igname pilée", "Igname pilée servie avec sauce tomate et viande.", "igname", "s14",
            v("Petite portion" to 1000, "Grande portion" to 1800), 4.5f, 27),

        // ---------- s15 : Fruits de Mer Baguida (p193–p200) ----------
        prep(193, "Crevettes grillées", "Crevettes fraîches marinées et grillées.", "crevettes", "s15",
            v("Petite portion" to 1500, "Grande portion" to 3000), 4.6f, 38),
        prep(194, "Crevettes sautées", "Crevettes sautées à l'ail, au piment et aux oignons.", "crevettes", "s15",
            v("Petite portion" to 1500, "Grande portion" to 3000), 4.5f, 27),
        prep(195, "Crabe en sauce", "Crabe mijoté dans une sauce tomate relevée.", "crabe", "s15",
            v("1 crabe" to 1500, "3 crabes" to 4000), 4.5f, 24),
        prep(196, "Crabe grillé", "Crabe grillé au charbon, servi avec piment.", "crabe", "s15",
            v("1 crabe" to 1500, "3 crabes" to 4000), 4.4f, 19, availability = AvailabilityStatus.UNAVAILABLE),
        prep(197, "Escargots en sauce", "Escargots mijotés avec tomates, oignons et épices.", "escargots", "s15",
            v("6 pièces" to 1500, "12 pièces" to 2800), 4.4f, 17),
        prep(198, "Soupe de fruits de mer", "Soupe épicée aux crevettes, poisson et crabe.", "soupe", "s15",
            v("Bol" to 1500, "Grand bol" to 2500), 4.6f, 30),
        prep(199, "Tilapia frit", "Tilapia entier mariné et frit, servi avec attiéké.", "tilapia", "s15",
            v("Moyen" to 2500, "Grand" to 4000), 4.6f, 35),
        prep(200, "Attiéké aux crevettes", "Attiéké accompagné de crevettes sautées et de crudités.", "attieke", "s15",
            v("Petite portion" to 1800, "Grande portion" to 3200), 4.7f, 34, popular = true)
    )

    // ============================================================
    // OPTIONS DE PERSONNALISATION ("Personnaliser ma demande")
    // Clé de la map = uuid de la préparation.
    // ============================================================

    private fun opt(id: String, label: String, phrase: String? = null, isWithout: Boolean = false) = RequestOption(
        id = id,
        label = label,
        phrase = phrase,
        isWithout = isWithout
    )

    private fun group(
        id: String,
        title: String,
        options: List<RequestOption>,
        selection: OptionSelection = OptionSelection.SINGLE,
        required: Boolean = false
    ) = RequestOptionGroup(
        id = id,
        title = title,
        selection = selection,
        options = options
    )

    // ----- Groupes réutilisables -----

    private val spiceGroup = group(
        "spice", "Piment",
        listOf(
            opt("spice_none", "Sans piment", phrase = "sans piment", isWithout = true),
            opt("spice_mild", "Doux", phrase = "un peu de piment"),
            opt("spice_medium", "Moyen", phrase = "du piment"),
            opt("spice_hot", "Fort", phrase = "beaucoup de piment")
        ),
        required = true
    )

    private val sugarGroup = group(
        "sugar", "Sucre",
        listOf(
            opt("sugar_none", "Sans sucre", phrase = "sans sucre", isWithout = true),
            opt("sugar_low", "Peu sucré", phrase = "peu sucré"),
            opt("sugar_normal", "Normal", phrase = null),
            opt("sugar_high", "Très sucré", phrase = "bien sucré")
        ),
        required = true
    )

    private val temperatureGroup = group(
        "temperature", "Température",
        listOf(
            opt("temp_cold", "Bien frais", phrase = "bien frais"),
            opt("temp_ice", "Avec glaçons", phrase = "des glaçons"),
            opt("temp_room", "Température ambiante", phrase = null)
        ),
        required = true
    )

    private val removeGroup = group(
        "remove", "Retirer",
        listOf(
            opt("remove_onion", "Sans oignons", phrase = "sans oignons", isWithout = true),
            opt("remove_tomato", "Sans tomate", phrase = "sans tomate", isWithout = true)
        ),
        selection = OptionSelection.MULTIPLE
    )

    private val dishExtrasGroup = group(
        "extras", "Suppléments",
        listOf(
            opt("extra_egg", "Œuf supplémentaire", phrase = "un œuf"),
            opt("extra_fish", "Poisson frit", phrase = "du poisson frit"),
            opt("extra_chicken", "Morceau de poulet", phrase = "un morceau de poulet"),
            opt("extra_alloco", "Alloco", phrase = "de l'alloco"),
            opt("extra_gari", "Gari", phrase = "du gari")
        ),
        selection = OptionSelection.MULTIPLE
    )

    private val cookingGroup = group(
        "cooking", "Cuisson",
        listOf(
            opt("cook_medium", "À point", phrase = "à point"),
            opt("cook_well", "Bien cuit", phrase = "bien cuit"),
            opt("cook_crispy", "Très grillé", phrase = "très grillé")
        ),
        required = true
    )

    private val grillSidesGroup = group(
        "sides", "Accompagnements",
        listOf(
            opt("side_onion", "Oignons", phrase = "des oignons"),
            opt("side_tomato", "Tomates", phrase = "des tomates"),
            opt("side_pepper", "Piment à part", phrase = "du piment à part"),
            opt("side_alloco", "Alloco", phrase = "de l'alloco"),
            opt("side_bread", "Pain", phrase = "du pain"),
            opt("side_attieke", "Attiéké", phrase = "de l'attiéké")
        ),
        selection = OptionSelection.MULTIPLE
    )

    private val snackSaucesGroup = group(
        "sauces", "Sauces",
        listOf(
            opt("sauce_ketchup", "Ketchup", phrase = "du ketchup"),
            opt("sauce_mayo", "Mayonnaise", phrase = "de la mayonnaise"),
            opt("sauce_mustard", "Moutarde", phrase = "de la moutarde"),
            opt("sauce_pepper", "Sauce piquante", phrase = "de la sauce piquante")
        ),
        selection = OptionSelection.MULTIPLE
    )

    private val snackExtrasGroup = group(
        "snack_extras", "Suppléments",
        listOf(
            opt("extra_cheese", "Fromage", phrase = "du fromage"),
            opt("extra_egg", "Œuf", phrase = "un œuf"),
            opt("extra_sausage", "Saucisse", phrase = "une saucisse")
        ),
        selection = OptionSelection.MULTIPLE
    )

    private val pizzaExtrasGroup = group(
        "pizza_extras", "Suppléments",
        listOf(
            opt("pizza_cheese", "Fromage supplémentaire", phrase = "du fromage supplémentaire"),
            opt("pizza_olives", "Olives", phrase = "des olives"),
            opt("pizza_egg", "Œuf", phrase = "un œuf"),
            opt("pizza_meat", "Viande hachée", phrase = "de la viande hachée")
        ),
        selection = OptionSelection.MULTIPLE
    )

    private val sweetToppingsGroup = group(
        "toppings", "Garnitures",
        listOf(
            opt("top_sugar", "Sucre", phrase = "du sucre"),
            opt("top_chocolate", "Chocolat", phrase = "du chocolat"),
            opt("top_honey", "Miel", phrase = "du miel"),
            opt("top_banana", "Banane", phrase = "de la banane")
        ),
        selection = OptionSelection.MULTIPLE
    )

    private val saltGroup = group(
        "salt", "Assaisonnement",
        listOf(
            opt("salt_none", "Nature", phrase = "nature"),
            opt("salt_light", "Peu salé", phrase = "peu salé"),
            opt("salt_normal", "Salé", phrase = "salé")
        ),
        required = true
    )

    private val fruitCutGroup = group(
        "cut", "Préparation",
        listOf(
            opt("cut_whole", "Entier", phrase = "entier"),
            opt("cut_peeled", "Épluché", phrase = "épluché"),
            opt("cut_pieces", "Coupé en morceaux", phrase = "coupé en morceaux")
        ),
        required = true
    )

    private val fruitSaladExtrasGroup = group(
        "fruit_extras", "Ajouts",
        listOf(
            opt("add_honey", "Miel", phrase = "du miel"),
            opt("add_lemon", "Citron", phrase = "du citron"),
            opt("add_yogurt", "Yaourt", phrase = "du yaourt")
        ),
        selection = OptionSelection.MULTIPLE
    )

    // ----- Familles de catégories -----

    private val grillCategories = setOf(
        "poulet", "poulet_braise", "poulet_frit", "poisson", "poisson_braise", "poisson_frit", "tilapia",
        "viande_boeuf", "viande_mouton", "viande_chevre", "porc", "brochettes", "brochettes_boeuf",
        "brochettes_poulet", "soya", "akpan_viande", "escargots", "crevettes", "crabe"
    )

    private val fruitCategories = setOf("mangue", "ananas", "papaye", "pastèque", "orange", "banane", "avocat")

    private val snackSauceCategories = setOf(
        "sandwich", "hamburger", "hot_dog", "pain_omelette", "pain_haricots", "tacos",
        "frites_pomme", "frites_igname", "akara", "omelette", "alloco", "banane_grillee"
    )

    private val sweetCategories = setOf("crepe", "beignets", "beignets_banane", "gateau")

    private fun optionsFor(p: Preparation): List<RequestOptionGroup> {
        val c = p.categoryId
        return when {
            c.startsWith("jus_") || c == "degue" -> listOf(sugarGroup, temperatureGroup)
            c == "yaourt" -> listOf(sugarGroup)
            c == "eau" || c == "soda" -> listOf(temperatureGroup)
            c in fruitCategories -> listOf(fruitCutGroup)
            c == "salade_fruits" -> listOf(fruitSaladExtrasGroup)
            c in grillCategories -> listOf(cookingGroup, spiceGroup, grillSidesGroup)
            c == "pizza" -> listOf(spiceGroup, pizzaExtrasGroup)
            c in snackSauceCategories -> listOf(spiceGroup, snackSaucesGroup, snackExtrasGroup)
            c in sweetCategories -> listOf(sweetToppingsGroup)
            c == "popcorn" || c == "arachides" -> listOf(saltGroup)
            c == "piment" || c == "oeufs_bouillis" -> listOf(spiceGroup)
            else -> listOf(spiceGroup, dishExtrasGroup, removeGroup)
        }
    }

    /** Options de personnalisation de chaque préparation, indexées par uuid. */
    val customOptions: Map<String, List<RequestOptionGroup>> =
        preparations.associate { it.uuid to optionsFor(it) }

    fun customOptionsFor(preparationId: String): List<RequestOptionGroup> =
        customOptions[preparationId].orEmpty()
}