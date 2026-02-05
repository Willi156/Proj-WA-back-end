--
-- PostgreSQL database dump
--

\restrict KDFTIKqbwoz6MTGNQV9ZjZLP6n0O9nk3qH0qaXtxN723j6HKinKRsdU8sAh9jCn

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

-- Started on 2026-01-12 18:26:38

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 222 (class 1259 OID 16409)
-- Name: contenuto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contenuto (
    id integer NOT NULL,
    titolo character varying(200) NOT NULL,
    anno_pubblicazione integer,
    descrizione text,
    genere character varying(50),
    link text,
    tipo character varying(20) NOT NULL,
    CONSTRAINT contenuto_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['GIOCO'::character varying, 'FILM'::character varying, 'SERIE_TV'::character varying])::text[])))
);


ALTER TABLE public.contenuto OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16408)
-- Name: contenuto_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.contenuto_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.contenuto_id_seq OWNER TO postgres;

--
-- TOC entry 5047 (class 0 OID 0)
-- Dependencies: 221
-- Name: contenuto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.contenuto_id_seq OWNED BY public.contenuto.id;


--
-- TOC entry 223 (class 1259 OID 16421)
-- Name: film; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.film (
    id_contenuto integer NOT NULL,
    casa_produzione character varying(100)
);


ALTER TABLE public.film OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16443)
-- Name: gioco; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gioco (
    id_contenuto integer NOT NULL,
    casa_editrice character varying(100)
);


ALTER TABLE public.gioco OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16500)
-- Name: gioco_piattaforma; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gioco_piattaforma (
    id_gioco integer NOT NULL,
    id_piattaforma integer NOT NULL
);


ALTER TABLE public.gioco_piattaforma OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16493)
-- Name: piattaforma; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.piattaforma (
    id integer NOT NULL,
    nome character varying(50),
    versione character varying(20)
);


ALTER TABLE public.piattaforma OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16492)
-- Name: piattaforma_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.piattaforma_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.piattaforma_id_seq OWNER TO postgres;

--
-- TOC entry 5048 (class 0 OID 0)
-- Dependencies: 229
-- Name: piattaforma_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.piattaforma_id_seq OWNED BY public.piattaforma.id;


--
-- TOC entry 228 (class 1259 OID 16475)
-- Name: preferiti; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.preferiti (
    id integer NOT NULL,
    id_utente integer NOT NULL,
    id_contenuto integer NOT NULL
);


ALTER TABLE public.preferiti OWNER TO postgres;

-- Sequence for preferiti.id
CREATE SEQUENCE public.preferiti_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.preferiti_id_seq OWNER TO postgres;

-- Make sequence owned by preferiti.id and set default
ALTER SEQUENCE public.preferiti_id_seq OWNED BY public.preferiti.id;

ALTER TABLE ONLY public.preferiti ALTER COLUMN id SET DEFAULT nextval('public.preferiti_id_seq'::regclass);

-- Add primary key on id
ALTER TABLE public.preferiti ADD CONSTRAINT preferiti_pkey PRIMARY KEY (id);

--
-- TOC entry 227 (class 1259 OID 16455)
-- Name: recensione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.recensione (
    id integer NOT NULL,
    titolo character varying(100),
    testo text,
    voto integer,
    data date,
    id_utente integer,
    id_contenuto integer,
    CONSTRAINT recensione_voto_check CHECK (((voto >= 1) AND (voto <= 10)))
);


ALTER TABLE public.recensione OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16454)
-- Name: recensione_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.recensione_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.recensione_id_seq OWNER TO postgres;

--
-- TOC entry 5049 (class 0 OID 0)
-- Dependencies: 226
-- Name: recensione_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.recensione_id_seq OWNED BY public.recensione.id;


--
-- TOC entry 224 (class 1259 OID 16432)
-- Name: serie_tv; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.serie_tv (
    id_contenuto integer NOT NULL,
    in_corso boolean,
    stagioni integer
);


ALTER TABLE public.serie_tv OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16390)
-- Name: utente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utente (
    id integer NOT NULL,
    nome character varying(50),
    cognome character varying(50),
    username character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    immagine_profilo character varying(255),
    ruolo character varying(20) NOT NULL,
    CONSTRAINT utente_ruolo_check CHECK (((ruolo)::text = ANY ((ARRAY['USER'::character varying, 'ADMIN'::character varying])::text[])))
);


ALTER TABLE public.utente OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16389)
-- Name: utente_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.utente_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.utente_id_seq OWNER TO postgres;

--
-- TOC entry 5050 (class 0 OID 0)
-- Dependencies: 219
-- Name: utente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.utente_id_seq OWNED BY public.utente.id;


--
-- TOC entry 4845 (class 2604 OID 16412)
-- Name: contenuto id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contenuto ALTER COLUMN id SET DEFAULT nextval('public.contenuto_id_seq'::regclass);


--
-- TOC entry 4847 (class 2604 OID 16496)
-- Name: piattaforma id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.piattaforma ALTER COLUMN id SET DEFAULT nextval('public.piattaforma_id_seq'::regclass);


--
-- TOC entry 4846 (class 2604 OID 16458)
-- Name: recensione id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recensione ALTER COLUMN id SET DEFAULT nextval('public.recensione_id_seq'::regclass);


--
-- TOC entry 4844 (class 2604 OID 16393)
-- Name: utente id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utente ALTER COLUMN id SET DEFAULT nextval('public.utente_id_seq'::regclass);


-- Completed on 2026-01-12 18:26:38

--
-- PostgreSQL database dump complete
--

\unrestrict KDFTIKqbwoz6MTGNQV9ZjZLP6n0O9nk3qH0qaXtxN723j6HKinKRsdU8sAh9jCn

-- DROP TABLE IF EXISTS preferiti;

-- DELETE FROM recensione;