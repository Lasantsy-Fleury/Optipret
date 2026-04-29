import { Router } from "express";
import {
  createLoan,
  deleteLoan,
  getLoanById,
  getLoanStats,
  listLoans,
  updateLoan
} from "../controllers/loanController";

const router = Router();

router.get("/stats", getLoanStats);
router.get("/", listLoans);
router.get("/:id", getLoanById);
router.post("/", createLoan);
router.put("/:id", updateLoan);
router.delete("/:id", deleteLoan);

export default router;
