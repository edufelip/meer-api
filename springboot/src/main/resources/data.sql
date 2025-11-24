INSERT INTO category (id, name_string_id, image_res_id) VALUES
 ('casa', 'brecho_de_casa', 'brecho-categories-house'),
 ('masculino', 'brecho_masculino', 'categories-masculino'),
 ('feminino', 'brecho_feminino', 'categories-feminino'),
 ('infantil', 'brecho_infantil', 'categories-infantil'),
 ('luxo', 'brecho_de_luxo', 'categories-luxo'),
 ('designer', 'brecho_de_designer', 'categories-designer'),
 ('desapego', 'brecho_de_desapego', 'categories-desapego'),
 ('geral', 'brechos_gerais', 'categories-geral')
ON CONFLICT (id) DO NOTHING;

-- Seed thrift stores (dev preview data)
INSERT INTO thrift_store (id, name, tagline, cover_image_url, address_line, latitude, longitude, map_image_url,
                          opening_hours, opening_hours_notes, facebook, instagram, website, whatsapp,
                          distance_km, walk_time_minutes, neighborhood, badge_label, is_favorite, description)
VALUES
 (1, 'Vintage Vibes', 'Garimpos curados com pegada retrô',
  'https://images.unsplash.com/photo-1441123694162-e54a981ceba3?auto=format&fit=crop&w=1600&q=80',
  'Rua Augusta, 123 - Pinheiros', -23.5617, -46.6589,
  'https://lh3.googleusercontent.com/aida-public/AB6AXuATD7G9oKF2W1aQjxAqHpYvVPamBIvCIZ6Q7I74RHrH7zwrJHn7iFRGMdMEHWLTMlP9DQ7oquk7Frb_j9QaIiT7ZSYMjZJhvTjFAJU7U-X73PmboiSxOHwS4QZ9mIBO-fJWAbwbdWu5yfwTrXn0c6HHGRpI5fDlZ_HckG3G5-IAsF_Vsh98T6DdyXbPl0bdG-iC9J2bjl6tqGgQIoeItBfJUqcnWgrKl9Y05nEY0VjB15UkZf5t6v0xiO0VVOuXFpoAn1Z7WNfG-dc',
  'Seg a Sáb: 10:00 - 19:00', 'Fecha aos domingos', NULL, '@vintagevibes', NULL, NULL,
  0.5, 5, 'Pinheiros', 'Mais amado', true, 'Peças icônicas dos anos 70 e 80, curadoria semanal.'),

 (2, 'Secondhand Chic', 'Peças de grife em segunda mão',
  'https://images.unsplash.com/photo-1542293787938-4d273c37c1b8?auto=format&fit=crop&w=1600&q=80',
  'Av. Paulista, 456 - Bela Vista', -23.5614, -46.6558,
  NULL,
  'Seg a Sex: 11:00 - 18:00', NULL, NULL, '@secondhandchic', NULL, NULL,
  1.2, 15, 'Vila Madalena', NULL, false, 'Grifes seminovas autenticadas e em ótimo estado.'),

 (3, 'Thrift Haven', 'Achadinhos baratos e estilosos',
  'https://images.unsplash.com/photo-1469334031218-e382a71b716b?auto=format&fit=crop&w=1600&q=80',
  'Rua 7 de Abril, 90 - Centro', -23.546, -46.638,
  NULL,
  'Ter a Dom: 10:00 - 20:00', NULL, NULL, '@thrifthaven', NULL, NULL,
  2.4, 28, 'Centro', NULL, false, 'Racks sempre renovados com preços acessíveis.'),

 (4, 'Eco Trends', 'Slow fashion e peças eco-friendly',
  'https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb?auto=format&fit=crop&w=1600&q=80',
  'Alameda dos Maracatins, 300 - Moema', -23.6101, -46.6675,
  NULL,
  'Seg a Sáb: 11:00 - 19:00', NULL, NULL, '@ecotrends', NULL, NULL,
  3.1, 35, 'Augusta', NULL, false, 'Curadoria eco-friendly com marcas locais.'),

 (5, 'Garimpo Urbano', 'Street + vintage na Consolação',
  'https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb?auto=format&fit=crop&w=1600&q=80',
  'Rua da Consolação, 210', NULL, NULL,
  NULL,
  'Seg a Sáb: 10:00 - 19:30', NULL, NULL, '@garimpourbano', NULL, NULL,
  0.6, 8, 'Augusta', NULL, false, NULL),

 (6, 'Querido Brechó', 'Curadoria feminina e genderless',
  'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=1600&q=80',
  'Rua Sabará, 41', NULL, NULL,
  NULL,
  'Seg a Sex: 11:00 - 18:30', NULL, NULL, '@queridobrecho', NULL, NULL,
  1.2, 15, 'Pinheiros', NULL, false, NULL),

 (7, 'Revive Vintage', 'Peças clássicas restauradas',
  'https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=1600&q=80',
  'Rua São Carlos do Pinhal, 15', NULL, NULL,
  NULL,
  'Qua a Dom: 12:00 - 20:00', NULL, NULL, '@revivevintage', NULL, NULL,
  2.1, 25, 'Centro', NULL, false, NULL),

 (8, 'Vintage Chic', 'Clássicos curados',
  'https://images.unsplash.com/photo-1496747611180-206a5c8c1a09?auto=format&fit=crop&w=1600&q=80',
  'Rua Exemplo, 99', NULL, NULL,
  NULL,
  'Seg a Sex: 10:00 - 18:00', NULL, NULL, '@vintagechic', NULL, NULL,
  NULL, NULL, 'Centro', NULL, false, NULL)
ON CONFLICT (id) DO NOTHING;

-- Gallery URLs
INSERT INTO thrift_store_gallery_urls (thrift_store_id, gallery_urls) VALUES
 (1, 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=800&q=80'),
 (1, 'https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=800&q=80'),
 (1, 'https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80'),
 (2, 'https://images.unsplash.com/photo-1496747611180-206a5c8c1a09?auto=format&fit=crop&w=800&q=80'),
 (2, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80'),
 (2, 'https://images.unsplash.com/photo-1496747611180-206a5c8c1a09?auto=format&fit=crop&w=800&q=80'),
 (3, 'https://images.unsplash.com/photo-1524606894343-1e1a4c71d692?auto=format&fit=crop&w=800&q=80'),
 (3, 'https://images.unsplash.com/photo-1496747611180-206a5c8c1a09?auto=format&fit=crop&w=800&q=80'),
 (3, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80'),
 (4, 'https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80'),
 (4, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80'),
 (4, 'https://images.unsplash.com/photo-1496747611180-206a5c8c1a09?auto=format&fit=crop&w=800&q=80'),
 (5, 'https://images.unsplash.com/photo-1469334031218-e382a71b716b?auto=format&fit=crop&w=800&q=80'),
 (5, 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=800&q=80'),
 (5, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80'),
 (6, 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=800&q=80'),
 (6, 'https://images.unsplash.com/photo-1496747611180-206a5c8c1a09?auto=format&fit=crop&w=800&q=80'),
 (6, 'https://images.unsplash.com/photo-1524606894343-1e1a4c71d692?auto=format&fit=crop&w=800&q=80'),
 (7, 'https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb?auto=format&fit=crop&w=800&q=80'),
 (7, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80'),
 (7, 'https://images.unsplash.com/photo-1496747611180-206a5c8c1a09?auto=format&fit=crop&w=800&q=80')
ON CONFLICT DO NOTHING;

-- Categories per store
INSERT INTO thrift_store_categories (thrift_store_id, categories) VALUES
 (1, 'Feminino'), (1, 'Vintage'), (1, 'Acessórios'),
 (2, 'Feminino'), (2, 'Luxo'),
 (3, 'Vintage'), (3, 'Casual'),
 (4, 'Sustentável'), (4, 'Unissex'),
 (5, 'Street'), (5, 'Vintage'),
 (6, 'Feminino'), (6, 'Genderless'),
 (7, 'Vintage'), (7, 'Restauração')
ON CONFLICT DO NOTHING;

-- Guide content/articles (mapped to store 8: Vintage Chic)
INSERT INTO guide_content (id, title, description, category_label, type, image_url, thrift_store_id) VALUES
 (1, 'Dicas para um garimpo de sucesso', 'Aprenda a encontrar as melhores peças em brechós.', 'Guia de estilo', 'article',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuASh-dosAr4TVNex49PUkBKFWcLJ5g7HOQJC7p6SaRZyNznaks3TiQuWOksGvrnYi6IeO5sMPBjaerUTI7HzO4xaF5jyAX9NkZS80VOX-lpdnJIHTDaM3nL8ANPzyy3T2OEPfbqu2cuQlC-_PdB_tFutmEey75ynvkAcO3CQis8asojk9mkENmn1Hg88uqHJEOxr2z8LyIELsQfsWo_vVdfdLbws8VFobNPLNE5cMP-Snp3CsMplvntxVg4BQTHBAk7pgXTv1Px3Ls',
  8),
 (2, 'Tour pelo brechó: novidades da semana', 'Veja os achados que chegaram nesta semana no nosso espaço.', 'Vídeo', 'article',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuBmfZnwoz2FyAt09OQcXxSOWFu-nzy3A0N7Y8dSUZmyVxMgOCUCY3KYMJkp8VWQmVt_eEPzL69-jCYmwaqq52hdiViVNBeuOIy35QjmobKVUKmv0XujKAj04kCQYbEijnAQ84NSgvQ2618bew_ido3S_RsVHz9SiI3adBYqbWWILbs5CkJk5nZnxcQK4mTayuijLFbB_TIx3KsrW2ONKeAeFmq2bGqv-C1QFs-D3V9stPlgaG84Q31ythQGOQjNwpsW6FmOwIdo2us',
  8),
 (3, 'Como cuidar das suas peças vintage', 'Guia rápido para conservar tecidos delicados e couro antigo.', 'Dica Rápida', 'article',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuBmfZnwoz2FyAt09OQcXxSOWFu-nzy3A0N7Y8dSUZmyVxMgOCUCY3KYMJkp8VWQmVt_eEPzL69-jCYmwaqq52hdiViVNBeuOIy35QjmobKVUKmv0XujKAj04kCQYbEijnAQ84NSgvQ2618bew_ido3S_RsVHz9SiI3adBYqbWWILbs5CkJk5nZnxcQK4mTayuijLFbB_TIx3KsrW2ONKeAeFmq2bGqv-C1QFs-D3V9stPlgaG84Q31ythQGOQjNwpsW6FmOwIdo2us',
  8),
 (4, 'Look do dia com achados do brechó', 'Inspire-se com combinações práticas para o cotidiano.', 'Post', 'article',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuBmfZnwoz2FyAt09OQcXxSOWFu-nzy3A0N7Y8dSUZmyVxMgOCUCY3KYMJkp8VWQmVt_eEPzL69-jCYmwaqq52hdiViVNBeuOIy35QjmobKVUKmv0XujKAj04kCQYbEijnAQ84NSgvQ2618bew_ido3S_RsVHz9SiI3adBYqbWWILbs5CkJk5nZnxcQK4mTayuijLFbB_TIx3KsrW2ONKeAeFmq2bGqv-C1QFs-D3V9stPlgaG84Q31ythQGOQjNwpsW6FmOwIdo2us',
  8)
ON CONFLICT (id) DO NOTHING;
