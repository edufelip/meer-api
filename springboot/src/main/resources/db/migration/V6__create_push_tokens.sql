-- Store FCM push tokens per user/device/environment.
CREATE TABLE public.push_token (
    id uuid NOT NULL,
    auth_user_id uuid NOT NULL,
    device_id character varying(255) NOT NULL,
    fcm_token character varying(4096) NOT NULL,
    platform character varying(16) NOT NULL,
    environment character varying(16) NOT NULL,
    app_version character varying(64),
    last_seen_at timestamp(6) with time zone NOT NULL,
    last_token_refresh_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    CONSTRAINT push_token_pkey PRIMARY KEY (id),
    CONSTRAINT push_token_platform_check CHECK ((platform)::text = ANY ((ARRAY['ANDROID'::character varying, 'IOS'::character varying])::text[])),
    CONSTRAINT push_token_environment_check CHECK ((environment)::text = ANY ((ARRAY['DEV'::character varying, 'STAGING'::character varying, 'PROD'::character varying])::text[]))
);

CREATE UNIQUE INDEX push_token_user_device_env_idx
    ON public.push_token (auth_user_id, device_id, environment);

CREATE INDEX push_token_user_env_idx
    ON public.push_token (auth_user_id, environment);

CREATE INDEX push_token_device_idx
    ON public.push_token (device_id);

ALTER TABLE ONLY public.push_token
    ADD CONSTRAINT push_token_auth_user_id_fkey
    FOREIGN KEY (auth_user_id) REFERENCES public.auth_user(id);
