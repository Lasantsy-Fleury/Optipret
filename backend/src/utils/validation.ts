import { z } from "zod";

export const idParamSchema = z.object({
  id: z.string().min(1)
});

export const createLoanSchema = z.object({
  num_compte: z.string().min(1),
  nom_client: z.string().min(1),
  nom_banque: z.string().min(1),
  montant: z.number().positive(),
  date_pret: z.coerce.date(),
  taux_pret: z.number().nonnegative()
});

export const updateLoanSchema = z
  .object({
    num_compte: z.string().min(1).optional(),
    nom_client: z.string().min(1).optional(),
    nom_banque: z.string().min(1).optional(),
    montant: z.number().positive().optional(),
    date_pret: z.coerce.date().optional(),
    taux_pret: z.number().nonnegative().optional()
  })
  .refine((data) => Object.keys(data).length > 0, {
    message: "At least one field is required"
  });
