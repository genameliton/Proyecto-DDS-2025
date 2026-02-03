function togglePass(btn) {
  const passInput = document.getElementById('password');
  const icon = btn.querySelector('i');

  if (passInput.type === 'password') {
    passInput.type = 'text';
    icon.classList.replace('uil-eye', 'uil-eye-slash');
  } else {
    passInput.type = 'password';
    icon.classList.replace('uil-eye-slash', 'uil-eye');
  }
}