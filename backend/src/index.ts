import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import swaggerJsdoc from "swagger-jsdoc";
import swaggerUi from "swagger-ui-express";
import loanRoutes from "./routes/loanRoutes";

dotenv.config();

const app = express();
const port = Number(process.env.PORT) || 3000;
const host = process.env.HOST || "0.0.0.0";
const publicHost = process.env.PUBLIC_HOST || "localhost";

app.use(cors({ origin: "*" }));
app.use(express.json());

app.use((req, res, next) => {
  const start = Date.now();
  res.on("finish", () => {
    const durationMs = Date.now() - start;
    console.log(`${req.method} ${req.originalUrl} -> ${res.statusCode} (${durationMs}ms)`);
  });
  next();
});

const swaggerSpec = swaggerJsdoc({
  definition: {
    openapi: "3.0.0",
    info: {
      title: "Optipret API",
      version: "1.0.0",
      description: "Documentation des endpoints de gestion des prets bancaires"
    },
    servers: [
      {
        url: `http://${publicHost}:${port}`
      }
    ],
    components: {
      schemas: {
        Loan: {
          type: "object",
          properties: {
            num_compte: { type: "string", المثال: "ACC-001" },
            nom_client: { type: "string" },
            nom_banque: { type: "string" },
            montant: { type: "number" },
            date_pret: { type: "string", format: "date-time" },
            taux_pret: { type: "number" }
          },
          required: ["num_compte", "nom_client", "nom_banque", "montant", "date_pret", "taux_pret"]
        },
        LoanInput: {
          type: "object",
          properties: {
            num_compte: { type: "string" },
            nom_client: { type: "string" },
            nom_banque: { type: "string" },
            montant: { type: "number" },
            date_pret: { type: "string", format: "date-time" },
            taux_pret: { type: "number" }
          },
          required: ["num_compte", "nom_client", "nom_banque", "montant", "date_pret", "taux_pret"]
        },
        LoanStats: {
          type: "object",
          properties: {
            total: { type: "number" },
            min: { type: "number" },
            max: { type: "number" }
          }
        }
      }
    },
    paths: {
      "/api/loans": {
        get: {
          summary: "Lister tous les prets",
          responses: {
            "200": {
              description: "Liste des prets",
              content: {
                "application/json": {
                  schema: { type: "array", items: { $ref: "#/components/schemas/Loan" } }
                }
              }
            }
          }
        },
        post: {
          summary: "Creer un pret",
          requestBody: {
            required: true,
            content: {
              "application/json": {
                schema: { $ref: "#/components/schemas/LoanInput" }
              }
            }
          },
          responses: {
            "201": {
              description: "Pret cree",
              content: { "application/json": { schema: { $ref: "#/components/schemas/Loan" } } }
            }
          }
        }
      },
      "/api/loans/{id}": {
        get: {
          summary: "Detail d'un pret",
          parameters: [
            { name: "id", in: "path", required: true, schema: { type: "string" } }
          ],
          responses: {
            "200": {
              description: "Pret",
              content: { "application/json": { schema: { $ref: "#/components/schemas/Loan" } } }
            },
            "404": { description: "Pret introuvable" }
          }
        },
        put: {
          summary: "Modifier un pret",
          parameters: [
            { name: "id", in: "path", required: true, schema: { type: "string" } }
          ],
          requestBody: {
            required: true,
            content: {
              "application/json": {
                schema: { $ref: "#/components/schemas/LoanInput" }
              }
            }
          },
          responses: {
            "200": {
              description: "Pret modifie",
              content: { "application/json": { schema: { $ref: "#/components/schemas/Loan" } } }
            },
            "404": { description: "Pret introuvable" }
          }
        },
        delete: {
          summary: "Supprimer un pret",
          parameters: [
            { name: "id", in: "path", required: true, schema: { type: "string" } }
          ],
          responses: {
            "200": { description: "Pret supprime" },
            "404": { description: "Pret introuvable" }
          }
        }
      },
      "/api/loans/stats": {
        get: {
          summary: "Statistiques des montants a payer",
          responses: {
            "200": {
              description: "Statistiques",
              content: { "application/json": { schema: { $ref: "#/components/schemas/LoanStats" } } }
            }
          }
        }
      },
      "/health": {
        get: {
          summary: "Health check",
          responses: {
            "200": { description: "OK" }
          }
        }
      }
    }
  },
  apis: []
});

app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));

app.use("/api/loans", loanRoutes);

app.get("/health", (_req, res) => {
  res.json({ status: "ok" });
});

app.listen(port, host, () => {
  console.log(`Server running on http://${publicHost}:${port}`);
});
