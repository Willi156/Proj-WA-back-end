
import express from "express";
import cors from "cors";
import pg from "pg";

const app = express();
pp.use(cors({
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
  ssl: { rejectUnauthorized: false }
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
