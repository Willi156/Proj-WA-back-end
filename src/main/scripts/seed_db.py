import json
import psycopg2

# === CONFIG DB RAILWAY ===
DB_URL = "postgresql://postgres:cvyTtsvHxrfONVSjNDnxEScycSGICSZK@trolley.proxy.rlwy.net:11510/railway"

conn = psycopg2.connect(DB_URL)
cur = conn.cursor()

# === CARICAMENTO DATI DA FILE SEPARATI ===
def load_collection(path, key=None):
    with open(path, "r", encoding="utf-8") as f:
        data = json.load(f)

    # If the JSON root is a dict and contains the expected key, return it
    if isinstance(data, dict) and key is not None and key in data:
        return data[key]

    # If the JSON root is already a list, assume it's the collection
    if isinstance(data, list):
        return data

    # Otherwise raise a helpful error
    raise TypeError(f"Unexpected JSON format in {path}: expected list or dict with key '{key}'")


giochi = load_collection("giochi_real.json", key="giochi")
    
film = load_collection("film_real.json", key="film")
    
serie_tv = load_collection("serie_tv_real.json", key="serie_tv")

# === PIATTAFORME ===
piattaforme_map = {}

cur.execute("DELETE FROM gioco_piattaforma")
cur.execute("DELETE FROM piattaforma")

# Inserisci piattaforme una sola volta
for nome in ["PC", "PlayStation", "Xbox", "Nintendo"]:
    cur.execute(
        "INSERT INTO piattaforma (nome, versione) VALUES (%s, %s) RETURNING id",
        (nome, "default")
    )
    piattaforme_map[nome] = cur.fetchone()[0]

# === INSERIMENTO GIOCHI ===
for gioco in giochi:
    cur.execute(""" 
        INSERT INTO contenuto (titolo, anno_pubblicazione, descrizione, genere, link, tipo)
        VALUES (%s, %s, %s, %s, %s, 'GIOCO')
        RETURNING id
    """, (
        gioco["titolo"],
        gioco["anno"],
        gioco["descrizione"],
        gioco["genere"],
        gioco["link"]
    ))
    contenuto_id = cur.fetchone()[0]

    cur.execute(""" 
        INSERT INTO gioco (id_contenuto, casa_editrice)
        VALUES (%s, %s)
    """, (contenuto_id, gioco["casa_editrice"]))

    for piattaforma in gioco["piattaforme"]:
        # ensure piattaforma exists in the map / database; create if missing
        if piattaforma not in piattaforme_map:
            cur.execute(
                "INSERT INTO piattaforma (nome, versione) VALUES (%s, %s) RETURNING id",
                (piattaforma, "default")
            )
            piattaforme_map[piattaforma] = cur.fetchone()[0]

        cur.execute(""" 
            INSERT INTO gioco_piattaforma (id_gioco, id_piattaforma)
            VALUES (%s, %s)
        """, (contenuto_id, piattaforme_map[piattaforma]))

# === INSERIMENTO FILM ===
for film_item in film:
    cur.execute(""" 
        INSERT INTO contenuto (titolo, anno_pubblicazione, descrizione, genere, link, tipo)
        VALUES (%s, %s, %s, %s, %s, 'FILM')
        RETURNING id
    """, (
        film_item["titolo"],
        film_item["anno"],
        film_item["descrizione"],
        film_item["genere"],
        film_item["link"]
    ))
    contenuto_id = cur.fetchone()[0]

    cur.execute(""" 
        INSERT INTO film (id_contenuto, casa_produzione)
        VALUES (%s, %s)
    """, (contenuto_id, film_item["casa_produzione"]))

# === INSERIMENTO SERIE TV ===
for serie in serie_tv:
    cur.execute(""" 
        INSERT INTO contenuto (titolo, anno_pubblicazione, descrizione, genere, link, tipo)
        VALUES (%s, %s, %s, %s, %s, 'SERIE_TV')
        RETURNING id
    """, (
        serie["titolo"],
        serie["anno"],
        serie["descrizione"],
        serie["genere"],
        serie["link"]
    ))
    contenuto_id = cur.fetchone()[0]

    cur.execute(""" 
        INSERT INTO serie_tv (id_contenuto, stagioni, in_corso)
        VALUES (%s, %s, %s)
    """, (contenuto_id, serie["stagioni"], serie["in_corso"]))

# Completamento e chiusura connessione
conn.commit()
cur.close()
conn.close()

print("Data import completato con successo")
