import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

async function main() {
  await prisma.pret_bancaire.createMany({
    skipDuplicates: true,
    data: [
      {
        num_compte: "ACC-001",
        nom_client: "Alice Martin",
        nom_banque: "Banque Centrale",
        montant: 12000,
        date_pret: new Date("2024-01-15T00:00:00.000Z"),
        taux_pret: 4.5
      },
      {
        num_compte: "ACC-002",
        nom_client: "Benoit Dupont",
        nom_banque: "Credit Urbain",
        montant: 8500,
        date_pret: new Date("2024-03-05T00:00:00.000Z"),
        taux_pret: 5.2
      },
      {
        num_compte: "ACC-003",
        nom_client: "Carla Moreau",
        nom_banque: "Banque Atlantique",
        montant: 20000,
        date_pret: new Date("2023-11-20T00:00:00.000Z"),
        taux_pret: 3.9
      }
    ]
  });
}

main()
  .catch((error) => {
    console.error("Seed error:", error);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
