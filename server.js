
import express from "express";
import cors from "cors";
import pg from "pg";

await client.connect();


const app = express();
app.use(cors({
  origin: [
    "http://localhost:4200",                 // dev locale Angular
    "https://critiverse-3c820.web.app",        // Firebase hosting (se già pubblicato)
  ],
  methods: ["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"],
  credentials: false // true solo se usi cookie/sessioni
}));

app.use(express.json());

const client = new pg.Client({
  connectionString: process.env.DATABASE_URL,
  // Abilita SSL solo se stai usando la URL pubblica (proxy)
  ssl: process.env.DATABASE_URL?.includes("proxy.rlwy.net")
        ? { rejectUnauthorized: false }
        : false
});
await client.connect();

app.get("/api/test", async (req, res) => {
  const result = await client.query("SELECT NOW()");
  res.json({ serverTime: result.rows[0] });
});


const port = process.env.PORT || 3000;
app.listen(port, "0.0.0.0", () => {
  console.log(`Server in ascolto su 0.0.0.0:${port}`);
});


// Recupera il primo utente in base all'ID
app.get("/api/utente/first", async (_req, res) => {
  try {
    const result = await client.query(`
      SELECT *
      FROM utente
      ORDER BY id ASC
      LIMIT 1
    `);

    if (result.rows.length === 0) {
      return res.status(404).json({ message: "Nessun utente trovato" });
    }

    // Restituiamo il record come JSON
    return res.json(result.rows[0]);
  } catch (err) {
    console.error("Errore query utente:", err);
    return res.status(500).json({ message: "Errore server" });
  }
});

