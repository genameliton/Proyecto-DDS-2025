--Requerido para soportar full text search
ALTER TABLE hecho
ADD FULLTEXT idx_hecho_categoria_titulo (categoria, titulo)
