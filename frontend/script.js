const API_URL = "http://localhost:8080/api";
let questions = [];
let answers = [];
let currentQuestion = 0;

const questionnaire = document.querySelector("#questionnaire");
const questionForm = document.querySelector("#question-form");
const questionContent = document.querySelector("#question-content");
const questionCount = document.querySelector("#question-count");
const progressPercent = document.querySelector("#progress-percent");
const progressTrack = document.querySelector("#progress-track");
const progressFill = document.querySelector("#progress-fill");
const validationMessage = document.querySelector("#validation-message");
const previousButton = document.querySelector("#previous-button");
const nextButton = document.querySelector("#next-button");
const finishButton = document.querySelector("#finish-button");
const results = document.querySelector("#results");
const restartButton = document.querySelector("#restart-button");

function getAuthHeaders() {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Sua sessão não foi encontrada. Entre novamente para iniciar a avaliação.");
    return { Authorization: `Bearer ${token}` };
}

async function parseApiResponse(response) {
    const body = await response.json().catch(() => null);
    if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("token");
            localStorage.removeItem("usuario");
            throw new Error("Sua sessão expirou ou não é válida. Entre novamente para continuar.");
        }
        throw new Error(body?.detail || body?.message || "Não foi possível concluir a solicitação.");
    }
    return body;
}

async function loadQuestions() {
    questionContent.textContent = "Carregando perguntas...";
    nextButton.disabled = true;
    finishButton.disabled = true;
    try {
        const response = await fetch(`${API_URL}/perguntas`, { headers: getAuthHeaders() });
        const data = await parseApiResponse(response);
        if (!Array.isArray(data) || data.length === 0) {
            throw new Error("A API não retornou perguntas para esta avaliação.");
        }
        questions = data;
        answers = Array(questions.length).fill(null);
        renderQuestion();
    } catch (error) {
        questionContent.replaceChildren();
        validationMessage.textContent = error.message || "Não foi possível carregar as perguntas. Verifique sua conexão com o servidor.";
        previousButton.disabled = true;
        nextButton.disabled = true;
        finishButton.disabled = true;
    }
}

function renderQuestion() {
    const question = questions[currentQuestion];
    const selectedOptionId = answers[currentQuestion];
    const progress = ((currentQuestion + 1) / questions.length) * 100;

    questionCount.textContent = `Pergunta ${currentQuestion + 1} de ${questions.length}`;
    progressPercent.textContent = `${Math.round(progress)}%`;
    progressTrack.setAttribute("aria-valuemax", String(questions.length));
    progressTrack.setAttribute("aria-valuenow", String(currentQuestion + 1));
    progressFill.style.width = `${progress}%`;
    previousButton.disabled = currentQuestion === 0;
    nextButton.disabled = false;
    finishButton.disabled = false;
    nextButton.hidden = currentQuestion === questions.length - 1;
    finishButton.hidden = currentQuestion !== questions.length - 1;
    validationMessage.textContent = "";

    const domainLabel = document.createElement("p");
    domainLabel.className = "question-domain";
    domainLabel.textContent = "QUESTIONÁRIO DE SONO";

    const title = document.createElement("h2");
    title.className = "question-title";
    title.id = "current-question-title";
    title.tabIndex = -1;
    title.textContent = question.texto;

    const helper = document.createElement("p");
    helper.className = "question-helper";
    helper.id = "question-helper";
    helper.textContent = question.ajuda || "";
    helper.hidden = !question.ajuda;

    const fieldset = document.createElement("fieldset");
    fieldset.className = "choices";
    fieldset.setAttribute("aria-describedby", "question-helper");
    fieldset.setAttribute("aria-labelledby", title.id);

    question.opcoes.forEach((option) => {
        const label = document.createElement("label");
        label.className = "choice-option";

        const input = document.createElement("input");
        input.type = "radio";
        input.name = `answer-${question.id}`;
        input.value = String(option.id);
        input.checked = selectedOptionId === option.id;
        input.addEventListener("change", () => {
            answers[currentQuestion] = option.id;
            validationMessage.textContent = "";
        });

        const choiceText = document.createElement("span");
        choiceText.className = "choice-card";
        choiceText.textContent = option.texto;
        label.append(input, choiceText);
        fieldset.append(label);
    });

    questionContent.replaceChildren(domainLabel, title, helper, fieldset);
    questionForm.setAttribute("aria-labelledby", title.id);
}

function requireCurrentAnswer() {
    if (questions.length > 0 && answers[currentQuestion] != null) return true;

    validationMessage.textContent = "Selecione uma alternativa para continuar.";
    questionContent.querySelector('input[type="radio"]')?.focus();
    return false;
}

function renderDomainResults(domains) {
    const domainResults = document.querySelector("#domain-results");
    domainResults.replaceChildren();

    domains.forEach((domain) => {
        const row = document.createElement("div");
        row.className = "domain-row";
        const rowHeader = document.createElement("div");
        rowHeader.className = "domain-row-header";
        const name = document.createElement("span");
        name.className = "domain-name";
        name.textContent = domain.nome;
        const score = document.createElement("span");
        score.className = "domain-score";
        score.textContent = `${domain.nota} / ${domain.maximo} pontos`;
        rowHeader.append(name, score);

        const track = document.createElement("div");
        track.className = "domain-track";
        track.setAttribute("role", "progressbar");
        track.setAttribute("aria-label", `Pontuação em ${domain.nome}`);
        track.setAttribute("aria-valuemin", "0");
        track.setAttribute("aria-valuemax", String(domain.maximo));
        track.setAttribute("aria-valuenow", String(domain.nota));
        const fill = document.createElement("span");
        fill.className = "domain-fill";
        fill.style.width = `${domain.maximo ? (domain.nota / domain.maximo) * 100 : 0}%`;
        track.append(fill);
        row.append(rowHeader, track);
        domainResults.append(row);
    });

    const lowestRatio = Math.min(...domains.map((domain) => domain.nota / domain.maximo));
    const lowestNames = domains
        .filter((domain) => domain.nota / domain.maximo === lowestRatio)
        .map((domain) => domain.nome);
    document.querySelector("#lowest-domains").textContent = lowestNames.length === domains.length
        ? "As pontuações proporcionais estão equilibradas entre os três domínios."
        : `Os domínios com menor pontuação proporcional são: ${lowestNames.join(" e ")}. Eles podem ser um bom ponto de partida para observar seus hábitos.`;
}

function renderAnswerFeedback(items) {
    const feedbackList = document.querySelector("#answer-feedback");
    feedbackList.replaceChildren();
    const itemsToReview = items.filter((item) => item.pontos < 2);

    if (itemsToReview.length === 0) {
        const message = document.createElement("p");
        message.className = "all-maximal";
        message.textContent = "Todas as respostas receberam a pontuação máxima. Continue percebendo quais hábitos ajudam sua rotina a funcionar bem.";
        feedbackList.append(message);
        return;
    }

    itemsToReview.forEach((item) => {
        const article = document.createElement("article");
        article.className = "feedback-item";
        const title = document.createElement("h3");
        const questionIndex = questions.findIndex((question) => question.id === item.perguntaId);
        title.textContent = `Pergunta ${questionIndex + 1} · ${item.pergunta}`;
        const selected = document.createElement("p");
        selected.className = "feedback-answer";
        selected.textContent = `Sua resposta: ${item.suaResposta}`;
        const explanation = document.createElement("p");
        explanation.className = "feedback-explanation";
        explanation.textContent = item.feedback;
        article.append(title, selected, explanation);
        feedbackList.append(article);
    });
}

function renderResults(result) {
    const feedback = {
        Ruim: "O resultado aponta hábitos que podem merecer atenção. Observe os temas abaixo sem transformar esta avaliação em um diagnóstico.",
        Médio: "O resultado reúne hábitos consistentes e outros que podem ser observados. Considere mudanças realistas, uma de cada vez.",
        Ótimo: "O resultado indica hábitos favoráveis em várias áreas. Continue observando sua rotina e preserve o que funciona para você.",
    };

    document.querySelector("#total-score").textContent = String(result.pontuacaoTotal);
    document.querySelector(".score-max").textContent = ` / ${result.pontuacaoMaxima}`;
    const badge = document.querySelector("#classification");
    badge.textContent = result.classificacao;
    badge.className = `classification ${result.classificacao === "Ruim" ? "is-poor" : result.classificacao === "Médio" ? "is-medium" : ""}`.trim();
    document.querySelector("#general-feedback").textContent = feedback[result.classificacao]
        || "Veja abaixo os resultados por domínio e os comentários da avaliação.";
    renderDomainResults(result.dominios || []);
    renderAnswerFeedback(result.feedback || []);

    questionnaire.hidden = true;
    results.hidden = false;
    document.querySelector("#result-title").focus();
    window.scrollTo({ top: 0, behavior: "smooth" });
}

async function submitEvaluation() {
    if (!answers.every((answer) => answer !== null)) {
        validationMessage.textContent = "Selecione uma alternativa para continuar.";
        questionContent.querySelector('input[type="radio"]')?.focus();
        return;
    }

    finishButton.disabled = true;
    validationMessage.textContent = "Enviando avaliação...";
    try {
        const response = await fetch(`${API_URL}/avaliacoes`, {
            method: "POST",
            headers: {
                ...getAuthHeaders(),
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                respostas: questions.map((question, index) => ({
                    perguntaId: question.id,
                    opcaoId: answers[index],
                })),
            }),
        });
        const result = await parseApiResponse(response);
        validationMessage.textContent = "";
        renderResults(result);
    } catch (error) {
        validationMessage.textContent = error.message || "Não foi possível enviar a avaliação. Tente novamente.";
        finishButton.disabled = false;
    }
}

previousButton.addEventListener("click", () => {
    if (currentQuestion === 0) return;
    currentQuestion -= 1;
    renderQuestion();
    document.querySelector("#current-question-title").focus();
});

nextButton.addEventListener("click", () => {
    if (!requireCurrentAnswer()) return;
    currentQuestion += 1;
    renderQuestion();
    document.querySelector("#current-question-title").focus();
});

finishButton.addEventListener("click", submitEvaluation);

questionForm.addEventListener("submit", (event) => event.preventDefault());

restartButton.addEventListener("click", () => {
    answers = Array(questions.length).fill(null);
    currentQuestion = 0;
    results.hidden = true;
    questionnaire.hidden = false;
    renderQuestion();
    window.scrollTo({ top: 0, behavior: "smooth" });
    document.querySelector("#current-question-title").focus();
});

loadQuestions();