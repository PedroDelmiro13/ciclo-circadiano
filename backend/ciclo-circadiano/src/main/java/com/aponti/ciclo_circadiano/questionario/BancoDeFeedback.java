package com.aponti.ciclo_circadiano.questionario;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BancoDeFeedback {

    public record Feedback(String favoravel, String melhorar) {}

    private final Map<Integer, Feedback> feedbacks = Map.of(
            1, new Feedback(
                    "Seu horário de começar a dormir varia um pouco. Essa regularidade ajuda a manter o ciclo sono-vigília previsível.",
                    "Seu horário de dormir varia bastante de um dia para outro. Manter uma variação de até 1 hora ajuda o corpo a prever o momento de dormir."),
            2, new Feedback(
                    "Seu horário de acordar é estável, o que funciona como um bom marcador da rotina diária e da exposição à luz.",
                    "Seu horário de acordar muda bastante. Acordar em horários mais parecidos ajuda a organizar a rotina e a exposição à luz."),
            3, new Feedback(
                    "Sua duração de sono está dentro da faixa proposta para a maioria dos adultos (7 a 9 horas). O foco passa a ser manter a regularidade e a qualidade.",
                    "Sua duração de sono está fora da faixa proposta para a maioria dos adultos (7 a 9 horas). Vale observar se há espaço na rotina para ajustar o tempo dedicado ao sono."),
            4, new Feedback(
                    "Você costuma adormecer e permanecer dormindo com facilidade, o que indica um sono relativamente contínuo.",
                    "Em parte do dia você tem dificuldade para adormecer ou permanecer dormindo. Observar o que antecede esses momentos (horário, luz, cafeína) pode ajudar a identificar o que atrapalha."),
            5, new Feedback(
                    "Você raramente sente sonolência que atrapalha suas atividades, o que é um bom sinal de alerta durante o dia.",
                    "A sonolência durante o dia aparece com alguma frequência. Se for intensa ou persistente, considere procurar orientação profissional."),
            6, new Feedback(
                    "Você costuma acordar satisfeito com a qualidade do seu sono, o que é um ponto positivo.",
                    "Sua satisfação com o sono varia ou é baixa. Este ponto merece atenção, pois a percepção de qualidade mostra algo que o número de horas não revela."),
            7, new Feedback(
                    "Você tem contato frequente com luz natural depois de acordar. A luz do dia é um dos principais sinais para sincronizar o relógio do corpo.",
                    "A luz natural forte depois de acordar é um dos principais sinais que ajudam a sincronizar o ciclo sono-vigília. Passar alguns minutos ao ar livre pela manhã pode ser um bom começo."),
            8, new Feedback(
                    "Você evita luz intensa e telas muito brilhantes na última hora antes de dormir, o que favorece o início do sono.",
                    "Luz intensa e telas brilhantes perto da hora de dormir podem atrasar o relógio do corpo e dificultar o início do sono. Reduzir esse tempo na última hora pode ajudar."),
            9, new Feedback(
                    "Você evita cafeína nas 6 horas anteriores ao sono, o que favorece a qualidade do descanso.",
                    "Cafeína nas horas que antecedem o sono pode prejudicá-lo. Pode ser interessante aumentar o intervalo entre a última cafeína e o horário de dormir e observar se sua facilidade para dormir melhora."),
            10, new Feedback(
                    "Seus horários nos dias livres são parecidos com os dos dias de compromisso, o que indica uma rotina regular.",
                    "Seus horários mudam bastante entre dias livres e dias de compromisso, o que pode indicar irregularidade (o chamado \"jet lag social\"). Aproximar esses horários ajuda a manter o ritmo.")
    );

    public String textoPara(int perguntaId, int pontos) {
        Feedback feedback = feedbacks.get(perguntaId);
        return pontos == 2 ? feedback.favoravel() : feedback.melhorar();
    }
}