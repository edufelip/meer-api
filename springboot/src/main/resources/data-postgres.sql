-- Ensure PostGIS is available (safe to run repeatedly; requires superuser in some setups).
CREATE EXTENSION IF NOT EXISTS postgis;

-- KNN-friendly index on geography derived from lon/lat.
CREATE INDEX IF NOT EXISTS idx_thrift_store_geog_gist
  ON thrift_store
  USING GIST (geography(ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)));

-- Legacy point-based KNN index (kept for fallback/non-PostGIS use).
CREATE INDEX IF NOT EXISTS idx_thrift_store_point_gist
  ON thrift_store
  USING GIST (point(longitude, latitude));
