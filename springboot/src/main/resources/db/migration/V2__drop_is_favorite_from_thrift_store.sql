-- Remove persisted favorite flag; favorite status is derived per-user via auth_user_favorites.
ALTER TABLE public.thrift_store
    DROP COLUMN IF EXISTS is_favorite;
