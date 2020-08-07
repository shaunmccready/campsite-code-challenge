CREATE TABLE public.camper
(
    id text NOT NULL,
    email text NOT NULL,
    name text NOT NULL,
    created timestamp without time zone DEFAULT now(),
    CONSTRAINT camper_id_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

GRANT ALL ON TABLE public.camper TO campsite_rw;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.camper TO campsite_rw;

CREATE UNIQUE INDEX camper_email_idx
    ON public.camper
        USING btree (email);


CREATE TABLE public.registration
(
    id serial NOT NULL,
    booking_id text NOT NULL,
    camper_id text NOT NULL,
    reservation_date  date NOT NULL,
    created timestamp without time zone DEFAULT now(),
    modified timestamp without time zone DEFAULT now(),
    CONSTRAINT registration_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_registration_camper_id FOREIGN KEY (camper_id)
        REFERENCES public.camper (id) MATCH SIMPLE
        ON UPDATE CASCADE ON DELETE CASCADE
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

GRANT ALL ON TABLE public.registration TO campsite_rw;
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.registration TO campsite_rw;

CREATE UNIQUE INDEX registration_reservation_date_idx
    ON public.registration
        USING btree (reservation_date);