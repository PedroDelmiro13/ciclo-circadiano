package com.aponti.ciclo_circadiano.questionario;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BancoDePerguntas {

    private static final List<Opcao> VARIACAO_HORARIO = List.of(
            new Opcao(1, "Até 1 hora", 2),
            new Opcao(2, "Entre 1 e 2 horas", 1),
            new Opcao(3, "Mais de 2 horas", 0)
    );

    private static final List<Opcao> FREQUENCIA_FAVORAVEL_ALTA = List.of(
            new Opcao(1, "Até 1 hora", 2),
            new Opcao(2, "Às vezes", 1),
            new Opcao(3, "Raramente", 0)
    );

    private static final List<Opcao> FREQUENCIA_FAVORAVEL_BAIXA = List.of(
            new Opcao(1, "Raramente", 2),
            new Opcao(2, "Às vezes", 1),
            new Opcao(3, "Frequentemente", 0)
    );

    private final List<Pergunta> perguntas = List.of(
            new Pergunta(1, "Quanto varia o seu horário de iniciar o sono de um dia para outro?",
                    "Considere os últimos 14 dias. Regularidade ajuda a manter um padrão sono-vigília previsível.",
                    Dominio.REGULARIDADE_TIMING, VARIACAO_HORARIO),
            new Pergunta(2, "Quanto varia o seu horário de acordar de um dia para outro?",
                    "O horário de despertar é um marcador importante da rotina e da exposição à luz do sol.",
                    Dominio.REGULARIDADE_TIMING, VARIACAO_HORARIO),
            new Pergunta(3, "Em média, quantas horas você dorme no período de 24 horas?",
                    "A duração é uma das dimensões centrais da saúde do sono.",
                    Dominio.SAUDE_DO_SONO, List.of(
                    new Opcao(1, "7 a 9 horas", 2),
                    new Opcao(2, "6 a menos de 7 horas, ou mais de 9 a 10 horas", 1),
                    new Opcao(3, "Menos de 6 h ou mais de 10 horas", 0))),
            new Pergunta(4, "Quando tenta dormir, com que frequência consegue adormecer e permanecer dormindo sem ficar longos períodos acordado?",
                    "Representa a continuidade do sono.",
                    Dominio.SAUDE_DO_SONO, List.of(
                    new Opcao(1, "Na maioria dos dias", 2),
                    new Opcao(2, "Em parte dos dias", 1),
                    new Opcao(3, "Raramente", 0))),
            new Pergunta(5, "Durante o período em que precisa estar acordado, com que frequência sente sonolência que atrapalha sua atenção ou atividade?",
                    "Estar alerta durante a vigília é uma das dimensões da saúde do sono.",
                    Dominio.SAUDE_DO_SONO, FREQUENCIA_FAVORAVEL_BAIXA),
            new Pergunta(6, "Ao acordar, quão satisfeito você costuma ficar com a qualidade do seu sono?",
                    "A satisfação capta algo que o número de horas não mostra.",
                    Dominio.SAUDE_DO_SONO, List.of(
                    new Opcao(1, "Satisfeito na maioria dos dias", 2),
                    new Opcao(2, "Varia bastante", 1),
                    new Opcao(3, "Geralmente insatisfeito", 0))),
            new Pergunta(7, "Depois de acordar, com que frequência você tem contato com luz natural forte, preferencialmente em ambiente externo?",
                    "A luz é um dos principais sincronizadores do sistema circadiano.",
                    Dominio.LUZ_E_ESTIMULANTES, FREQUENCIA_FAVORAVEL_ALTA),
            new Pergunta(8, "Na última hora antes do seu período principal de sono, com que frequência você fica por muito tempo sob luz intensa ou telas muito brilhantes?",
                    "Luz no fim do período de vigília pode atrasar o relógio circadiano e interferir no início do sono.",
                    Dominio.LUZ_E_ESTIMULANTES, FREQUENCIA_FAVORAVEL_BAIXA),
            new Pergunta(9, "Com que frequência você consome cafeína nas 6 horas anteriores ao horário em que pretende dormir?",
                    "Cafeína, mesmo algumas horas antes de dormir, pode prejudicar o sono.",
                    Dominio.LUZ_E_ESTIMULANTES, List.of(
                    new Opcao(1, "Nunca, raramente ou não consumo", 2),
                    new Opcao(2, "Às vezes", 1),
                    new Opcao(3, "Frequentemente", 0))),
            new Pergunta(10, "Nos dias livres, seus horários de dormir e acordar mudam quanto em relação aos dias de trabalho/estudo?",
                    "Grandes diferenças entre dias de compromisso e dias livres indicam irregularidade (\"jet lag social\").",
                    Dominio.REGULARIDADE_TIMING, VARIACAO_HORARIO)
    );

    public List<Pergunta> listarPerguntas() {
        return perguntas;
    }
}
