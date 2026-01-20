import json
import os
import psycopg2

# Connection parameters read from environment variables (Railway compatible)
DB_HOST = os.getenv("postgres.railway.internal")
DB_NAME = os.getenv("railway")
DB_USER = os.getenv("postgres")
DB_PASSWORD = os.getenv("cvyTtsvHxrfONVSjNDnxEScycSGICSZK")
DB_PORT = os.getenv("PGPORT", "5432")
DB_URL = "postgresql://postgres:cvyTtsvHxrfONVSjNDnxEScycSGICSZK@trolley.proxy.rlwy.net:11510/railway"

JSON_FILE = "recensioni.json"

def main():
    conn = psycopg2.connect(DB_URL)
    cur = conn.cursor()

    with open(JSON_FILE, "r", encoding="utf-8") as f:
        recensioni = json.load(f)

    insert_query = """
        INSERT INTO recensione (titolo, testo, voto, data, id_utente, id_contenuto)
        VALUES (%s, %s, %s, %s, %s, %s)
    """

    for r in recensioni:
        cur.execute(
            insert_query,
            (
                r["titolo"],
                r["testo"],
                r["voto"],
                r["data"],
                r["id_utente"],
                r["id_contenuto"]
            )
        )

    conn.commit()
    cur.close()
    conn.close()
    print("Inserimento completato con successo.")

if __name__ == "__main__":
    main()