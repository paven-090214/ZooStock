ALTER TABLE public.member
    ADD COLUMN role VARCHAR(10) NOT NULL
    DEFAULT 'USER'
    CONSTRAINT check_role CHECK (role IN ('ADMIN', 'USER'));