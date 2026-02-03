package ar.edu.utn.frba.dds.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseInitializerConfig {

  @Bean
  public ApplicationRunner createFullTextIndex(JdbcTemplate jdbcTemplate) {
    return args -> {
      try {
        // 1. Query para verificar si el índice ya existe
        // (Esta query es específica de MySQL/MariaDB)
        String checkIndexSql =
            "SELECT COUNT(1) " +
                "FROM INFORMATION_SCHEMA.STATISTICS " +
                "WHERE table_schema = DATABASE() " +
                "AND table_name = 'hecho' " +
                "AND index_name = 'idx_hecho_titulo_categoria' " +
                "OR  index_name =  'idx_hecho_categoria_titulo'";

        Integer indexExists = jdbcTemplate.queryForObject(checkIndexSql, Integer.class);

        if (indexExists != null && indexExists == 0) {
          String createIndexSql = "CREATE FULLTEXT INDEX idx_hecho_categoria_titulo ON hecho(titulo, categoria)";
          jdbcTemplate.execute(createIndexSql);
        }

      } catch (Exception e) {
        System.err.println("Error al verificar/crear el índice FULLTEXT: " + e.getMessage());
      }
    };
  }
}