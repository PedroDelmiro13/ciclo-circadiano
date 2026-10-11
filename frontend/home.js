const token = localStorage.getItem('token');
const API = 'http://localhost:8080/api';

if (!token) {
  window.location.href = 'index.html';
}

const btnAvaliacao = document.getElementById('btn-avaliacao');
const btnSair = document.getElementById('btn-sair');
const profileName = document.getElementById('profile-name');
const profileEmail = document.getElementById('profile-email');
const latestCard = document.getElementById('latest-card');
const latestStatus = document.getElementById('latest-status');
const latestContent = document.getElementById('latest-content');
let latestLoaded = false;
let latestLoading = false;

function dadosDoPerfil() {
  try {
    const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
    const payload = token.split('.')[1];
    const claims = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    const email = usuario.email || claims.sub || '';
    const nome = usuario.nome || usuario.name || usuario.nomeCompleto
      || (email.includes('@') ? email.split('@')[0].replace(/[._-]+/g, ' ') : email);
    return { nome: nome || 'Meu Perfil', email };
  } catch {
    return { nome: 'Meu Perfil', email: '' };
  }
}

const perfil = dadosDoPerfil();
profileName.textContent = perfil.nome.toLocaleUpperCase('pt-BR');
profileEmail.textContent = perfil.email;

function renderizarDominios(dominios) {
  const container = document.getElementById('latest-domains');
  container.replaceChildren();

  dominios.forEach(function (dominio) {
    const item = document.createElement('div');
    item.className = 'latest-domain';

    const labels = document.createElement('div');
    labels.className = 'latest-domain-labels';
    const nome = document.createElement('span');
    nome.textContent = dominio.nome;
    const pontuacao = document.createElement('span');
    pontuacao.textContent = `${dominio.nota} / ${dominio.maximo}`;
    labels.append(nome, pontuacao);

    const trilha = document.createElement('div');
    trilha.className = 'latest-domain-track';
    trilha.setAttribute('role', 'progressbar');
    trilha.setAttribute('aria-label', `Pontuação em ${dominio.nome}`);
    trilha.setAttribute('aria-valuemin', '0');
    trilha.setAttribute('aria-valuemax', String(dominio.maximo));
    trilha.setAttribute('aria-valuenow', String(dominio.nota));
    const progresso = document.createElement('span');
    progresso.className = 'latest-domain-fill';
    progresso.style.width = `${dominio.maximo ? (dominio.nota / dominio.maximo) * 100 : 0}%`;
    trilha.append(progresso);

    item.append(labels, trilha);
    container.append(item);
  });
}

async function carregarUltimaAvaliacao() {
  if (latestLoaded || latestLoading) return;
  latestLoading = true;
  latestStatus.textContent = 'Carregando avaliação...';

  try {
    const response = await fetch(`${API}/avaliacoes/ultima`, {
      headers: { Authorization: `Bearer ${token}` }
    });

    if (response.status === 204) {
      latestStatus.textContent = 'Você ainda não realizou uma avaliação.';
      latestLoaded = true;
      return;
    }

    if (!response.ok) {
      throw new Error('Não foi possível carregar sua última avaliação.');
    }

    const avaliacao = await response.json();
    document.getElementById('latest-total').textContent = avaliacao.pontuacaoTotal;
    document.getElementById('latest-maximum').textContent = ` / ${avaliacao.pontuacaoMaxima}`;

    const classificacao = document.getElementById('latest-classification');
    classificacao.textContent = avaliacao.classificacao;
    classificacao.className = `latest-classification ${avaliacao.classificacao === 'Ruim' ? 'is-poor' : avaliacao.classificacao === 'Médio' ? 'is-medium' : ''}`.trim();

    const data = new Date(avaliacao.dataAvaliacao);
    const dataElemento = document.getElementById('latest-date');
    dataElemento.dateTime = avaliacao.dataAvaliacao;
    dataElemento.textContent = `Realizada em ${new Intl.DateTimeFormat('pt-BR', { dateStyle: 'medium', timeStyle: 'short' }).format(data)}`;

    renderizarDominios(avaliacao.dominios || []);
    latestStatus.hidden = true;
    latestContent.hidden = false;
    latestLoaded = true;
  } catch (error) {
    latestStatus.textContent = error.message || 'Não foi possível carregar sua última avaliação.';
  } finally {
    latestLoading = false;
  }
}

btnAvaliacao.addEventListener('click', function () {
  window.location.href = 'survey.html';
});

btnSair.addEventListener('click', function () {
  localStorage.removeItem('token');
  localStorage.removeItem('usuario');

  window.location.href = 'index.html';
});

latestCard.addEventListener('toggle', function () {
  if (latestCard.open) carregarUltimaAvaliacao();
});