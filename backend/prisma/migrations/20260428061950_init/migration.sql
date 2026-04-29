-- CreateTable
CREATE TABLE "Pret_bancaire" (
    "num_compte" TEXT NOT NULL,
    "nom_client" TEXT NOT NULL,
    "nom_banque" TEXT NOT NULL,
    "montant" DOUBLE PRECISION NOT NULL,
    "date_pret" TIMESTAMP(3) NOT NULL,
    "taux_pret" DOUBLE PRECISION NOT NULL,

    CONSTRAINT "Pret_bancaire_pkey" PRIMARY KEY ("num_compte")
);
