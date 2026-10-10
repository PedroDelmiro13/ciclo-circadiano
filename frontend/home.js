const token = localStorage.getItem('token');

if (!token) {
  window.location.href = 'index.html';
}

const btnAvaliacao = document.getElementById('btn-avaliacao');
const btnSair = document.getElementById('btn-sair');

btnAvaliacao.addEventListener('click', function () {
  window.location.href = 'questionario.html';
});

btnSair.addEventListener('click', function () {
  localStorage.removeItem('token');
  localStorage.removeItem('usuario');

  window.location.href = 'index.html';
});