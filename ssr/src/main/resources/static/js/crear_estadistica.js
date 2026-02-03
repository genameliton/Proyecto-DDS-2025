function procesarSubmit(event) {
  event.preventDefault()
  event.target.submit()
  //replace container content with spinner
  const mainContainer = document.querySelector(".container")
  mainContainer.innerHTML = "<div class='d-flex flex-column align-items-center justify-content-center'> <div class='spinner-border' role='status'><span class='visually-hidden'>Loading...</span></div><h4>Creando estadistica, aguarde unos instantes</h4></div>"
}

