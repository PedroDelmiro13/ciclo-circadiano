const API = 'http://localhost:8080';
const formRegister = document.getElementById('form-register');
const msg = document.getElementById('mensagem');

function mostrarMensagem(texto, tipo) {
  if (!msg) {
    return;
  }

  msg.textContent = texto;

  if (tipo === 'sucesso') {
    msg.style.color = 'green';
  } else {
    msg.style.color = 'red';
  }
}

async function lerJson(resp) {
  try {
    const retorno = await resp.json();
    return retorno;
  } catch {
    return null;
  }
}

async function enviarRegistro(nome, email, senha) {
  const resposta = await fetch(`${API}/api/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nome, email, senha })
  });

  return resposta;
}

function mensagemDeErro(resp, retorno) {
  const status = resp.status;

  if (status === 409) {
    return 'Este e-mail já está cadastrado.';
  }

  if (retorno) {
    if (retorno.message) {
      return retorno.message;
    }

    if (retorno.erro) {
      return retorno.erro;
    }
  }

  return 'Não foi possível realizar o cadastro.';
}

async function Register(e) {
  e.preventDefault();
  mostrarMensagem('', 'erro');

  const nome = document.getElementById('nomeForm').value.trim();
  const email = document.getElementById('emailForm').value.trim();
  const senha = document.getElementById('senhaForm').value;

  try {
    const resp = await enviarRegistro(nome, email, senha);
    const retorno = await lerJson(resp);

    if (!resp.ok) {
      const textoErro = mensagemDeErro(resp, retorno);
      mostrarMensagem(textoErro, 'erro');
      return;
    }

    mostrarMensagem('Cadastro realizado com sucesso! Redirecionando...', 'sucesso');

    setTimeout(function () {
      window.location.href = 'index.html';
    }, 1500);

  } catch {
    mostrarMensagem('Erro de conexão com o servidor.', 'erro');
  }
}

if (formRegister) {
  formRegister.addEventListener('submit', Register);
}