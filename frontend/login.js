const API = 'http://localhost:8080';
const formLogin = document.getElementById('form-login');
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

function salvarSessao(retorno) {
  if (!retorno) {
    return;
  }

  if (retorno.token) {
    localStorage.setItem('token', retorno.token);
  }

  localStorage.setItem('usuario', JSON.stringify(retorno));
}

async function enviarLogin(email, senha) {
  const resposta = await fetch(`${API}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, senha })
  });

  return resposta;
}

function mensagemDeErro(resp, retorno) {
  const status = resp.status;

  if (status === 400 || status === 401 || status === 403) {
    return 'E-mail ou senha inválidos.';
  }

  if (retorno) {
    if (retorno.message) {
      return retorno.message;
    }

    if (retorno.erro) {
      return retorno.erro;
    }
  }

  return 'Não foi possível realizar o login.';
}

async function Login(e) {
  e.preventDefault();
  mostrarMensagem('', 'erro');

  const email = document.getElementById('emailForm').value.trim();
  const senha = document.getElementById('senhaForm').value;

  try {
    const resp = await enviarLogin(email, senha);
    const retorno = await lerJson(resp);

    if (!resp.ok) {
      const textoErro = mensagemDeErro(resp, retorno);
      mostrarMensagem(textoErro, 'erro');
      return;
    }

    salvarSessao(retorno);
    mostrarMensagem('Login realizado com sucesso! Redirecionando...', 'sucesso');

    setTimeout(function () {
      window.location.href = 'home.html';
    }, 1500);

  } catch {
    mostrarMensagem('Erro de conexão com o servidor.', 'erro');
  }
}

if (formLogin) {
  formLogin.addEventListener('submit', Login);
}