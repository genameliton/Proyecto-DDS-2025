function validarInputs(event) {
  event.preventDefault(); // prevenimos envío inicial

  // referencias
  let fechaInicioInput = document.querySelector("#fecha_inicio");
  let fechaFinInput = document.querySelector("#fecha_fin");
  let errorDiv = document.querySelector(".errorMsg");

  // limpiar estado previo
  fechaInicioInput.classList.remove("is-invalid");
  fechaFinInput.classList.remove("is-invalid");
  errorDiv.hidden = true;
  errorDiv.textContent = "";

  const fechaInicio = fechaInicioInput.value;
  const fechaFin = fechaFinInput.value;

  if (fechaInicio && fechaFin) {
    const inicio = new Date(fechaInicio);
    const fin = new Date(fechaFin);

    // validar rango
    if (inicio >= fin) {
      // mostrar error visual y mensaje
      fechaInicioInput.classList.add("is-invalid");
      fechaFinInput.classList.add("is-invalid");
      errorDiv.hidden = false;
      errorDiv.textContent = "⚠️ La fecha de inicio debe ser anterior a la fecha de fin.";
      return; // no enviar
    }
  }

  // si todo es válido → enviar el formulario
  event.target.submit();
}