import { PrismaClient } from "@prisma/client";
import { LoanInput, LoanUpdateInput, LoanStats } from "../types";

const prisma = new PrismaClient();

export async function listLoans() {
  return prisma.pret_bancaire.findMany({ orderBy: { date_pret: "desc" } });
}

export async function getLoanById(id: string) {
  return prisma.pret_bancaire.findUnique({ where: { num_compte: id } });
}

export async function createLoan(data: LoanInput) {
  return prisma.pret_bancaire.create({ data });
}

export async function updateLoan(id: string, data: LoanUpdateInput) {
  const existing = await prisma.pret_bancaire.findUnique({ where: { num_compte: id } });
  if (!existing) {
    return null;
  }

  return prisma.pret_bancaire.update({
    where: { num_compte: id },
    data
  });
}

export async function deleteLoan(id: string) {
  const existing = await prisma.pret_bancaire.findUnique({ where: { num_compte: id } });
  if (!existing) {
    return null;
  }

  await prisma.pret_bancaire.delete({ where: { num_compte: id } });
  return true;
}

export async function getLoanStats(): Promise<LoanStats> {
  const loans = await prisma.pret_bancaire.findMany({
    select: { montant: true, taux_pret: true }
  });

  if (loans.length === 0) {
    return { total: 0, min: 0, max: 0 };
  }

  const values = loans.map((loan) => loan.montant * (1 + loan.taux_pret / 100));
  const total = values.reduce((sum, value) => sum + value, 0);
  const min = Math.min(...values);
  const max = Math.max(...values);

  return { total, min, max };
}
