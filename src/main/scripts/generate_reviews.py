import json
import random
from datetime import datetime, timedelta

NUM_CONTENT = 304
REVIEWS_PER_CONTENT = 10

adjectives = [
    "Eccellente", "Buono", "Discreto", "Scarso", "Terribile",
    "Coinvolgente", "Noioso", "Divertente", "Inefficace", "Stupendo"
]

nouns = [
    "esperienza", "gioco", "titolo", "contenuto", "storia",
    "grafica", "sonoro", "livello", "personaggio", "missione"
]

sentences = [
    "Mi è piaciuto molto, lo consiglio.",
    "Non ha soddisfatto le aspettative.",
    "La grafica è sorprendente ma il gameplay è ripetitivo.",
    "Esperienza solida con alcune pecche.",
    "Ottimo equilibrio tra difficoltà e ricompense.",
    "La storia è coinvolgente e ben scritta.",
    "Poche innovazioni rispetto ai precedenti capitoli.",
    "Modalità multiplayer divertente e stabile.",
    "Bug minori ma non rovinano il gioco.",
    "Non riuscivo a smettere di giocare fino a notte fonda."
]

reviews = []
current_id = 1
start_date = datetime.now() - timedelta(days=365*2)  # negli ultimi 2 anni

for id_contenuto in range(1, NUM_CONTENT + 1):
    for i in range(REVIEWS_PER_CONTENT):
        titolo = f"{random.choice(adjectives)} {random.choice(nouns)}"
        testo = " ".join(random.sample(sentences, k=2))
        voto = random.randint(1, 5)
        date = start_date + timedelta(days=random.randint(0, 365*2), hours=random.randint(0,23), minutes=random.randint(0,59))
        recensione = {
            "id": current_id,
            "titolo": titolo,
            "testo": testo,
            "voto": voto,
            "data": date.strftime("%Y-%m-%d %H:%M:%S"),
            "id_utente": 1,
            "id_contenuto": id_contenuto
        }
        reviews.append(recensione)
        current_id += 1

output_path = "C:/Users/User/Desktop/CritiverseProject/recensioni_3040.json"
with open(output_path, "w", encoding="utf-8") as f:
    json.dump(reviews, f, ensure_ascii=False, indent=2)

print(f"Generati {len(reviews)} recensioni in {output_path}")
