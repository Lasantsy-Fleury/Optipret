export type LoanInput = {
  num_compte: string;
  nom_client: string;
  nom_banque: string;
  montant: number;
  date_pret: Date;
  taux_pret: number;
};

export type LoanUpdateInput = Partial<LoanInput>;

export type LoanStats = {
  total: number;
  min: number;
  max: number;
};
