# Optipret Backend

Backend Express + TypeScript + Prisma + PostgreSQL.

## Installation

```bash
npm install
```

## Configuration

Copiez le fichier `.env.example` en `.env` et renseignez la variable `DATABASE_URL`.

## Migrations et seed

```bash
npx prisma generate
npx prisma migrate dev --name init
npx prisma db seed
```

## Demarrage local

```bash
npm run dev
```

Serveur accessible sur `http://localhost:3000`.

## Endpoints

- GET /api/loans
- GET /api/loans/:id
- POST /api/loans
- PUT /api/loans/:id
- DELETE /api/loans/:id
- GET /api/loans/stats

## Deploiement Render.com

### Build command

```bash
npm install && npx prisma generate && npx prisma migrate deploy && npm run build
```

### Start command

```bash
npm start
```

### Variables d'environnement

- DATABASE_URL
- PORT (optionnel, par defaut 3000)
- HOST (optionnel, par defaut 0.0.0.0)
- PUBLIC_HOST (optionnel, par defaut localhost)

## Notes

- CORS est actif pour toutes les origines (dev local).
- Validation des donnees via Zod.
