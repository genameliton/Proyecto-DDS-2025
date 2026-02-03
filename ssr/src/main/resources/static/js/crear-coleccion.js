

async function procesarSubmit(event) {
  event.preventDefault()
  //agregar animaciones de espera
  let submitButton = document.querySelector("#submit-button")
  submitButton.disabled = true
  const fuentes = document.querySelectorAll('.fuente-item');
  let contenedorFuentes
  for (const fuente of fuentes) {
    const tipo = fuente.querySelector('select').value;
    const input = fuente.querySelector('.fuente-input');
    contenedorFuentes = input.parentElement

    if (tipo === "ESTATICA" && input.files !== null && input.files.length > 0) {
      const formData = new FormData();
      formData.append("file", input.files[0]);

      try {
        const resp = await fetch(FUENTE_ESTATICA_URL + "/api/fuentes", {
          method: "POST",
          body: formData
        });

        const data = await resp.json();
        let newInput = document.createElement('input')
        newInput.type = "text"
        newInput.value = FUENTE_ESTATICA_URL + "/api/fuentes/" + data.id;
        newInput.name = input.name; // mantiene el binding
        contenedorFuentes.replaceChild(newInput, input)
      } catch (err) {
        alert("Error guardando archivo csv: " + err.message);
        submitButton.disabled = false
        return;
      }
    }
  }

  //replace container content with spinner
  event.target.submit();
  const mainContainer = document.querySelector(".container")
  mainContainer.innerHTML = "<div class='d-flex flex-column align-items-center justify-content-center'> <div class='spinner-border' role='status'><span class='visually-hidden'>Loading...</span></div><h4>Subiendo coleccion, aguarde unos instantes</h4></div>"

}



function cambiarInput(select) {
  const contenedor = select.parentElement;
  const oldInput = contenedor.querySelector('.fuente-input');
  let newInput = oldInput;
  if (select.value === "DINAMICA") {
    newInput.hidden = true
    input.required = false;
  }
  else if (select.value === "ESTATICA") {
    newInput.type = "file"
    newInput.accept = ".csv";
    newInput.onchange = () => verificarArchivoCsv(newInput);
    newInput.required = true
  } else {
    newInput.type = "text";
    newInput.placeholder = "URL";
  }

  contenedor.replaceChild(newInput, oldInput);
}


function agregarFuente() {
  const container = document.getElementById('fuentes-container');
  const nuevaFuente = document.createElement('div');
  const id = container.querySelectorAll('.fuente-item').length;
  nuevaFuente.innerHTML = `
<div class="fuente-item d-flex flex-row mb-4 p-2 align-items-center justify-content-start gap-3">
                        <select class="form-select" aria-label="seleccionar-fuente"
                        name="fuentes[${id}].tipoFuente" onchange="cambiarInput(this)"
                        >
                            <option selected>Tipo de fuente</option>
                            <option value="ESTATICA">Estatica</option>
                            <option value="DINAMICA">Dinamica</option>
                            <option value="PROXY_API">Proxy api</option>
                        </select>
                        <input class="form-control fuente-input" type="text" placeholder="URL"
                        name="fuentes[${id}].url">
                        <button type="button" class="btn btn-danger" onclick="eliminarFuente(this)">Eliminar</button>
                    </div>
    `;

  container.appendChild(nuevaFuente);

  // Animación de entrada
  setTimeout(() => {
    nuevaFuente.style.opacity = '1';
    nuevaFuente.style.transform = 'translateY(0)';
  }, 10);
}


function eliminarFuente(boton) {
  const fuente = boton.closest('.fuente-item');
  // Animación de salida
  fuente.style.transition = 'all 0.3s ease';
  fuente.style.opacity = '0';
  fuente.style.transform = 'translateX(-100%)';

  setTimeout(() => {
    fuente.remove();
    reindexarFuentes()
  }, 200);
}

function reindexarFuentes() {
  const items = document.querySelectorAll('#fuentes-container .fuente-item');
  items.forEach((item, index) => {
    const select = item.querySelector("select");
    const input = item.querySelector(".fuente-input");

    if (select) select.name = `fuentes[${index}].tipoFuente`;
    if (input) input.name = `fuentes[${index}].url`;
  });
}

async function verificarArchivoCsv(inputFile) {

  const errorMsg = document.getElementById("errorMsg");

  errorMsg.style.display = "none";

  if (inputFile.files.length === 0) return;

  const formData = new FormData();
  formData.append("file", inputFile.files[0]);

  try {
    // Consulta API externa para validar si es CSV
    const resp = await fetch(FUENTE_ESTATICA_URL + "/api/fuentes/validar-csv", {
      method: "POST",
      body: formData
    });
    const result = await resp.json();

    if (!result.esCsv) {
      errorMsg.innerText = "El archivo no es un CSV válido.";
      errorMsg.style.display = "inline";
      inputFile.classList.add("is-invalid");
    } else {
      // if (result.registros >= 10000) {
      //   inputFile.classList.add("is-valid");
      // } else {
      //   errorMsg.innerText = "Tiene menos de 10000";
      //   errorMsg.style.display = "inline";
      //   inputFile.classList.add("is-invalid");
      // }s
      inputFile.classList.add("is-valid");
    }
  } catch (e) {
    errorMsg.innerText = "Error consultando API externa.";
    errorMsg.style.display = "inline";
  }
}

const filtrosUsados = new Set();
const listaFiltros = document.getElementById("lista-filtros");
const selectTipoFiltro = document.getElementById("tipoFiltro");

function agregarFiltro() {
  const tipoFiltro = selectTipoFiltro.value;
  if (!tipoFiltro) return;

  if (filtrosUsados.has(tipoFiltro)) {
    alert("Ya se agregó un filtro de este tipo.");
    return;
  }

  filtrosUsados.add(tipoFiltro);
  const index = listaFiltros.children.length;

  const div = document.createElement("div");
  div.classList.add("filtro-item", "border", "p-3", "mb-3", "rounded");
  div.dataset.tipo = tipoFiltro;

  let contenido = `
    <input type="hidden" name="filtros[${index}].tipoFiltro" id="filtros${index}.tipoFiltro" value="${tipoFiltro}">
  `;

  switch (tipoFiltro) {
    case "FILTRO_CATEGORIA":
    case "FILTRO_PROVINCIA":
    case "FILTRO_DEPARTAMENTO":
    case "FILTRO_MUNICIPIO":
      contenido += `
        <label class="form-label">${tipoFiltro.replace("FILTRO_", "").toLowerCase()}:</label>
        <input type="text" class="form-control"
               name="filtros[${index}].valor" id="filtros${index}.valor"
               placeholder="Ingrese valor">
      `;
      break;

    case "FILTRO_FUENTE":
      contenido += `
        <label class="form-label">Tipo de fuente:</label>
        <select class="form-select"
                name="filtros[${index}].tipoFuente" id="filtros${index}.tipoFuente">
          <option value="">Seleccione tipo de fuente</option>
          <option value="ESTATICA">Estática</option>
          <option value="DINAMICA">Dinámica</option>
          <option value="PROXY_API">Proxy API</option>
        </select>
      `;
      break;

    case "FILTRO_FECHA_ACONTECIMIENTO":
    case "FILTRO_FECHA_REPORTE":
      contenido += `
        <label class="form-label">
          ${tipoFiltro === "FILTRO_FECHA_ACONTECIMIENTO" ? "Fecha de acontecimiento" : "Fecha de reporte"}:
        </label>
        <div class="d-flex gap-2 align-items-center">
          <input type="date" class="form-control"
                 name="filtros[${index}].fechaInicio" id="filtros${index}.fechaInicio">
          <span>a</span>
          <input type="date" class="form-control"
                 name="filtros[${index}].fechaFin" id="filtros${index}.fechaFin">
        </div>
      `;
      break;
  }

  contenido += `
    <button type="button" class="btn btn-danger mt-2" onclick="eliminarFiltro(this, '${tipoFiltro}')">Eliminar</button>
  `;

  div.innerHTML = contenido;
  listaFiltros.appendChild(div);
  selectTipoFiltro.value = "";
}

function eliminarFiltro(btn, tipoFiltro) {
  filtrosUsados.delete(tipoFiltro);
  btn.closest(".filtro-item").remove();

  // Reindexar campos para mantener compatibilidad con el mapeo de Spring
  Array.from(listaFiltros.children).forEach((div, i) => {
    div.querySelectorAll("input, select").forEach(el => {
      if (el.name) {
        el.name = el.name.replace(/filtros\[\d+\]/, `filtros[${i}]`);
      }
      if (el.id) {
        el.id = el.id.replace(/filtros\d+/, `filtros${i}`);
      }
    });
  });
}