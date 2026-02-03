package ar.edu.utn.frba.dds.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiMvcConfig implements WebMvcConfigurer {
  // Asumamos que la carpeta raíz de tus uploads está en application.properties
  //@Value("${app.multimedia.upload-dir}")
  private String rutaUploads = "./uploads";

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    // 1. La URL pública que los clientes usarán
    String urlPublica = "/media/**";

    // 2. La carpeta local real donde están los archivos
    // "file:" es OBLIGATORIO.
    String rutaLocal = "file:" + rutaUploads;

    registry
        .addResourceHandler(urlPublica)
        .addResourceLocations(rutaLocal);
    System.out.println(registry);
  }
}
