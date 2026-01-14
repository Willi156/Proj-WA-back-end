Critiverse Java backend (Spring Boot)

Quick start

- Build:

```bash
mvn package
```

- Run (uses `DATABASE_URL` env var or default to local Postgres):

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/critiverse mvn spring-boot:run
```

Endpoints

- `GET /api/test` : returns server time
- `GET /api/utente/first` : returns first row from `utente` table or 404

Notes

- This project uses `JdbcTemplate` and a simple DAO pattern (`UtenteDao`).
- Configure DB credentials via `DB_USER`, `DB_PASS`, or include them in `DATABASE_URL`.
# Proj-WA-back-end
Back End del progetto Critiverse
