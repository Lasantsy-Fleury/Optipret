import { Request, Response } from "express";
import {
  createLoanSchema,
  updateLoanSchema,
  idParamSchema
} from "../utils/validation";
import * as loanService from "../services/loanService";

export async function listLoans(_req: Request, res: Response) {
  try {
    const loans = await loanService.listLoans();
    res.json(loans);
  } catch (error) {
    res.status(500).json({ message: "Failed to list loans" });
  }
}

export async function getLoanById(req: Request, res: Response) {
  try {
    const { id } = idParamSchema.parse(req.params);
    const loan = await loanService.getLoanById(id);

    if (!loan) {
      return res.status(404).json({ message: "Loan not found" });
    }

    res.json(loan);
  } catch (error) {
    res.status(400).json({ message: "Invalid request" });
  }
}

export async function createLoan(req: Request, res: Response) {
  try {
    const payload = createLoanSchema.parse(req.body);
    const loan = await loanService.createLoan(payload);
    res.status(201).json(loan);
  } catch (error) {
    res.status(400).json({ message: "Invalid request" });
  }
}

export async function updateLoan(req: Request, res: Response) {
  try {
    const { id } = idParamSchema.parse(req.params);
    const payload = updateLoanSchema.parse(req.body);
    const loan = await loanService.updateLoan(id, payload);

    if (!loan) {
      return res.status(404).json({ message: "Loan not found" });
    }

    res.json(loan);
  } catch (error) {
    res.status(400).json({ message: "Invalid request" });
  }
}

export async function deleteLoan(req: Request, res: Response) {
  try {
    const { id } = idParamSchema.parse(req.params);
    const deleted = await loanService.deleteLoan(id);

    if (!deleted) {
      return res.status(404).json({ message: "Loan not found" });
    }

    res.json({ message: "Loan deleted" });
  } catch (error) {
    res.status(400).json({ message: "Invalid request" });
  }
}

export async function getLoanStats(_req: Request, res: Response) {
  try {
    const stats = await loanService.getLoanStats();
    res.json(stats);
  } catch (error) {
    res.status(500).json({ message: "Failed to compute stats" });
  }
}
